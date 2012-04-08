package com.quui.tc.gen;

import java.util.*;

public class SolutionGenerator
{
  protected static String nl;
  public static synchronized SolutionGenerator create(String lineSeparator)
  {
    nl = lineSeparator;
    SolutionGenerator result = new SolutionGenerator();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/**" + NL + " * A TopCoder Algorithm Competition Solution " + NL + " * (http://www.topcoder.com/tc)" + NL + " */" + NL + "public class ";
  protected final String TEXT_2 = " {" + NL + "    public ";
  protected final String TEXT_3 = " {" + NL + "    \t\t" + NL + "        return ";
  protected final String TEXT_4 = ";" + NL + "    }" + NL + "}";

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
    Map map = (Map) argument;
    stringBuffer.append(TEXT_1);
    stringBuffer.append(map.get(Constants.CLASS_NAME));
    stringBuffer.append(TEXT_2);
    stringBuffer.append(map.get(Constants.SIGNATURE));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(map.get(Constants.RETURN_VALUE));
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
