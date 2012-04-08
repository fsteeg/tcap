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
 * Created on 23.09.2004
 */
package com.quui.tc;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.quui.tc.TCWizard;

/**
 * The "New" wizard page allows setting the container for the new files.
 */
public class TCPage extends WizardPage {
    private Text containerTextWidget;

    private Text problemTextWidget;

    private String containerText = "";

    private String fileText = "";

    private ISelection selection;

    /**
     * 
     * @param selection
     */
    public TCPage(ISelection selection) {
        super("TopCoder Algorithm Competition Solution");
        setTitle("TopCoder Algorithm Competition Solution");
        setDescription("TopCoder Algorithm Competition Solution");
        setImageDescriptor(TCWizard.IMAGE);
        this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Project:");

        containerTextWidget = new Text(container, SWT.BORDER | SWT.SINGLE);
        containerTextWidget.setText(containerText);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerTextWidget.setLayoutData(gd);
        containerTextWidget.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        Button button = new Button(container, SWT.PUSH);
        button.setText("Select...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        label = new Label(container, SWT.NULL);
        label.setText("");
        label.setText("Problem \rStatement:");

        problemTextWidget = new Text(container, SWT.BORDER | SWT.MULTI
                | SWT.V_SCROLL);
        problemTextWidget.setText(fileText);
        gd = new GridData(GridData.FILL_BOTH);
        problemTextWidget.setLayoutData(gd);

        label = new Label(container, SWT.NULL);
        problemTextWidget.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        initialize();
        dialogChanged();
        setControl(container);
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */

    private void initialize() {
        if (selection != null && selection.isEmpty() == false
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IJavaProject) {
                containerTextWidget.setText(((IJavaProject) obj).getPath()
                        .toPortableString());
            }
            problemTextWidget.setFocus();
        }
        problemTextWidget.setText("");
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */

    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
                getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                "Choose a Java-Project for the Solution");
        dialog.showClosedProjects(false);
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerTextWidget.setText(((Path) result[0]).toOSString());
            }
        }
    }

    /**
     * Ensures that both text fields are set.
     */
    private void dialogChanged() {
        String container = getContainerName();
        String fileName = getFileName();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(container));
        IResource r = ((IContainer) resource).findMember(fileName);
        if (problemTextWidget.getText().indexOf("Problem Statement") == -1) {
            updateStatus("Please paste a TopCoder Algorithm Competition Problem Statement.");
            ((TCWizard) this.getWizard()).setHasProblemDescription(false);
            return;
        }
        if (r != null) {
            updateStatus("Please choose a different Location, such a File is present here.");
            ((TCWizard) this.getWizard()).setHasProblemDescription(false);
            return;
        }
        if (container.length() == 0) {
            updateStatus("Please choose a project for your solution.");
            ((TCWizard) this.getWizard()).setHasProblemDescription(false);
            return;
        }
        // if this is reached all is fine
        ((TCWizard) this.getWizard()).setHasProblemDescription(true);
        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName() {
        return containerTextWidget.getText();
    }

    public String getFileName() {
        return problemTextWidget.getText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
     */
    public IWizardPage getNextPage() {
        ((TCWizard) getWizard()).containerName = getContainerName();
        return super.getNextPage();
    }

    /**
     * @return Returns the problemTextWidget.
     */
    public String getProblemText() {
        return problemTextWidget.getText();
    }
}