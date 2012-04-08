package com.quui.tc.gen;

import java.util.*;

public class TestsGenerator
{
  protected static String nl;
  public static synchronized TestsGenerator create(String lineSeparator)
  {
    nl = lineSeparator;
    TestsGenerator result = new TestsGenerator();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "import java.util.List;";
  protected final String TEXT_2 = NL + "/**" + NL + " * TopCoder Algorithm Competition Tests " + NL + " * (http://www.topcoder.com/tc)" + NL + " */" + NL + "public class ";
  protected final String TEXT_3 = "Test extends TestCase { " + NL + " \t private ";
  protected final String TEXT_4 = " toTest;" + NL + "\t protected void setUp() throws Exception { " + NL + "\t\t toTest = new ";
  protected final String TEXT_5 = "();" + NL + "\t }" + NL + "\t protected void tearDown() throws Exception { " + NL + "\t\t super.tearDown(); " + NL + "\t }" + NL + "\t" + NL + "\t " + NL + "\t";
  protected final String TEXT_6 = NL + "\t \t" + NL + "\tpublic void test";
  protected final String TEXT_7 = "() throws Exception { " + NL + "\t\t ";
  protected final String TEXT_8 = " correct;" + NL + "\t\t ";
  protected final String TEXT_9 = " result;" + NL + "\t\t long start,end,diff;" + NL + "\t\t int limit = 8000;" + NL + "\t\t correct = ";
  protected final String TEXT_10 = ";" + NL + "\t\t start = System.currentTimeMillis(); result = toTest.";
  protected final String TEXT_11 = "(test.params);" + NL + "\t\t end = System.currentTimeMillis(); diff = end - start;" + NL + "\t\t assertTrue(\"High time complexity. \\nExecution time exceeds \" + limit +\" ms, took \" + diff + \" ms.\",diff<limit);" + NL + "\t\t assertTrue(\"The method returns null.\", result!=null);" + NL + "\t\t ";
  protected final String TEXT_12 = NL + "\t}" + NL + "\t" + NL + "\t";
  protected final String TEXT_13 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    /*******************************************************************************
 * Copyright (c) 2004 Fabian Steeg. All rights reserved. This program and 
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg
 *******************************************************************************/
    stringBuffer.append(TEXT_1);
    Map map = (Map) argument;
    stringBuffer.append(TEXT_2);
    stringBuffer.append(map.get(Constants.CLASS_NAME));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(map.get(Constants.CLASS_NAME));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(map.get(Constants.CLASS_NAME));
    stringBuffer.append(TEXT_5);
     List<Test> tests = (List<Test>)map.get(Constants.TESTS);
	 	for(int i = 0; i<tests.size();i++){
	 	Test test = tests.get(i); 
	
    stringBuffer.append(TEXT_6);
    stringBuffer.append(map.get(Constants.CLASS_NAME));
    stringBuffer.append(i);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(map.get(Constants.RETURN_TYPE));
    stringBuffer.append(TEXT_8);
    stringBuffer.append(map.get(Constants.RETURN_TYPE));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(test.correctValue);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(map.get(Constants.METHOD_NAME));
    stringBuffer.append(TEXT_11);
    stringBuffer.append(map.get(test.assertLine));
    stringBuffer.append(TEXT_12);
     } 
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
