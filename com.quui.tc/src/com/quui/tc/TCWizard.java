/*******************************************************************************
 * Copyright (c) 2004 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg
 *******************************************************************************/

/**
 * @author Fabian Steeg
 * Created on 07.11.2004
 * 
 */

package com.quui.tc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.ide.IDE;


/**
 * the wizard to build an tc solution.
 * 
 * @author fsteeg
 */
public class TCWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;

    protected static final ImageDescriptor IMAGE = TCPlugin
            .imageDescriptorFromPlugin("com.quui.tc", "icons/tc-banner.png");

    private CodeGen gen;

    protected TCPage tcPage = null;

    public boolean hasProblemDescription = false;

    public Logger logger = Logger.getAnonymousLogger();

    protected String containerName;

    private IWorkbenchWindow window;

    private IFile solution;

    private IFile test;

    public TCWizard() {
        super();
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.window = workbench.getActiveWorkbenchWindow();
        setWindowTitle("New TopCoder Algorithm Competition Solution");
        if (((IStructuredSelection) selection).getFirstElement() != null
                && ((IStructuredSelection) selection).getFirstElement() instanceof IFile) {
            containerName = ((IFile) ((IStructuredSelection) selection)
                    .getFirstElement()).getParent().getName();
        }

        ConsoleHandler finer;
        finer = new ConsoleHandler();
        finer.setLevel(Level.FINER);
        this.logger.addHandler(finer);
        this.logger.setLevel(Level.ALL);
    }

    public void createPageControls(Composite pageContainer) {
    }

    public void addPages() {
        tcPage = new TCPage(selection);
        addPage(tcPage);
    }

    public boolean performFinish() {
        Action a = new OpenCheatSheetAction(
                "com.quui.tc.algorithm.plugin.cheatsheet");
        a.run();
        if (tcPage != null) {
            gen = new CodeGen(tcPage.getProblemText());
            containerName = tcPage.getContainerName();
        }
        saveFiles();
        return true;
    }

    public boolean canFinish() {
        return hasProblemDescription;
    }

    private InputStream openContentStream(String toWrite) {
        StringWriter out = new StringWriter();
        out.write(toWrite);
        String contents = out.toString();
        return new ByteArrayInputStream(contents.getBytes());
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean saveFiles() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException {
                try {
                    String template = gen.createSolutionText();
                    doFinish(containerName, gen.getClassName() + ".java",
                            monitor, template);
                    doFinish(containerName, gen.getClassName() + "Test"
                            + ".java", monitor, gen.createTestText());
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException
                    .getMessage());
            return false;
        }
        try {
            IWorkbenchPage page = window.getActivePage();
            IDE.openEditor(page, test);
            this.logger.finer("opened a " + solution.getClass().toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            JUnitLaunchShortcut f = new JUnitLaunchShortcut();
            f
                    .launch(PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage()
                            .getActiveEditor(), ILaunchManager.RUN_MODE);
            IWorkbenchPage page = window.getActivePage();
            IMarker marker = null;
            marker = solution.createMarker(IMarker.TASK);
            marker.setAttribute(IMarker.CHAR_START, gen.getStartingPoint());
            marker.setAttribute(IMarker.CHAR_END, gen.getStartingPoint());
            IDE.openEditor(page, marker);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return true;
    }

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     */

    private void doFinish(String containerName, String fileName,
            IProgressMonitor monitor, String toWrite) throws CoreException {
        monitor.beginTask("Creating " + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            String message = "Container \"" + containerName
                    + "\" does not exist.";
            IStatus status = new Status(IStatus.ERROR, "com.quui.tc.TCWizard",
                    IStatus.OK, message, null);
            throw new CoreException(status);
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        try {
            InputStream stream = openContentStream(toWrite);
            if (file.exists()) {
                file.setContents(stream, true, true, monitor);
            } else {
                file.create(stream, true, monitor);
                this.logger.finer("created a " + file.getClass().toString());
            }
            stream.close();
        } catch (IOException e) {
        }
        monitor.worked(1);
        if (fileName.indexOf("Test") != -1)
            test = file;
        else
            solution = file;
    }

    /**
     * @param hasProblemDescription
     *            The hasProblemDescription to set.
     */
    public void setHasProblemDescription(boolean hasProblemDescription) {
        this.hasProblemDescription = hasProblemDescription;
    }
}