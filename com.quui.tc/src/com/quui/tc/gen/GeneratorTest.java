/*******************************************************************************
 * Copyright (c) 2004 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg
 *******************************************************************************/

package com.quui.tc.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quui.tc.CodeGen;

import junit.framework.Assert;
import junit.framework.TestCase;

public class GeneratorTest extends TestCase {
  public void testGen() {
    String problem = getText(new File("testdata/problem1.txt"));
    CodeGen gen = new CodeGen(problem);
    System.out.println(gen.createSolutionText());
    System.out.println(gen.createTestText());
  }

  public void testRegex() {
    String orig = "jgl;ks \r\n dfjgl;k\r\nClass:\r\nColor\r\n fsafdsadf \r\n afasdf";
    System.out.println("Orig: \n" + orig);
    String regex = ".*Class:\\s*\r\n(\\w+)\\s*\r\n.*";
    Matcher m = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL)
        .matcher(orig);
    if (m.matches()) {
      String clazz = m.group(1);
      System.out.println("Class: " + clazz);
      Assert.assertEquals("Color", clazz);
    }
  }
  
  private String getText(File file) {
    StringBuilder text = new StringBuilder();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = "";
      while ((line = reader.readLine()) != null) {
        text.append(line).append("\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return text.toString();
  }
}
