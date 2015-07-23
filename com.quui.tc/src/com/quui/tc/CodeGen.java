/*******************************************************************************
 * Copyright (c) 2004-2015 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg
 *******************************************************************************/
/**
 * @author Fabian Steeg
 * Created on 07.11.2004
 */
package com.quui.tc;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fsteeg
 */
public class CodeGen {
	private String problemStatement;

	private String className;

	private String sig;

	private String method;

	private String[] params;

	private String returnType;

	private int startingPoint;

	public int getStartingPoint() {
		return startingPoint;
	}

	/**
	 * 
	 */
	public CodeGen(String problemStatement) {
		super();
		this.problemStatement = problemStatement;
		getValues();

	}

	/**
	 * Parses the values from the problem statement
	 * 
	 * @param problemStatement
	 */
	private void getValues() {
		/*
		 * problem statements start with a line break, so use that as the sep
		 * for lines. we can't use the system property cause it's not really
		 * what the arena applet uses.
		 */
		String sep = problemStatement.startsWith("\r\n") ? "\r\n"
				: problemStatement.charAt(0) + "";
		/* but let's not depend entirely on that break first in the text */
		if (!(sep.equals("\r") || sep.equals("\r\n") || sep.equals("\n")))
			sep = System.getProperty("line.separator");
		// Captures the content of the next line:
		String captureNextLine = "\\s*" + sep + "(.+?)\\s*" + sep + "";
		// the values of the problem statement:
		String regex = ".*Class:" + captureNextLine + "Method:"
				+ captureNextLine + "Parameters:" + captureNextLine
				+ "Returns:" + captureNextLine + "Method signature:"
				+ captureNextLine + ".*";
		Matcher m = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(problemStatement);
		String allParams = "";
		// retrieve the captured values:
		if (m.matches()) {
			className = m.group(1);
			method = m.group(2);
			allParams = m.group(3);
			returnType = m.group(4);
			sig = m.group(5);
		}
		// parse the parameters into an array:
		if (allParams.indexOf(",") == -1) {
			params = new String[] { allParams };
		} else {
			params = allParams.split(",");
		}
		for (int i = 0; i < params.length; i++) {
			params[i] = params[i].trim();
			System.out.println("param " + i + " ist " + params[i]);
		}

	}

	public String createSolutionText() {
		StringBuffer buf = new StringBuffer();
		buf.append("public class ").append(className).append(" {\r").append(
				"\t public ").append(sig).append(" {\r\t\t ");
		startingPoint = buf.length();
		String nullThing = "null";
		if (returnType.equals("boolean"))
			nullThing = "true";
		else if (returnType.equals("char"))
			nullThing = "'0'";
		else if (returnType.equals("long"))
			nullThing = "-1L";
		else if (returnType.equals("int") || returnType.equals("double")
				|| returnType.equals("float"))
			nullThing = "-1";
		buf.append("\r" + "\t\t return " + nullThing + ";\r\t }\r}");
		return buf.toString();
	}

	public String createTestText() {
		StringBuffer buf = new StringBuffer();
		buf.append("import junit.framework.TestCase;\r");
		if (returnType.indexOf("[]") != -1) {
			buf.append("import java.util.Arrays;\r");
		}
		buf
				.append("/*")
				.append(problemStatement + "\n")
				.append("*/\r\r")
				.append("public class ")
				.append(className + "Test ")
				.append("extends TestCase { \r \t private ")
				.append(className)
				.append(" toTest;\r")
				.append(
						"\t protected void setUp() throws Exception { \r\t\t toTest = new ")
				.append(className + "();")
				.append("\r\t }\r")
				.append(
						"\t protected void tearDown() throws Exception { \r\t\t super.tearDown(); \r\t }\r");

		buf.append(createTests());

		if (returnType.indexOf("[]") != -1)
			addArrayEquals(buf);

		buf.append("}");

		return buf.toString();
	}

	/**
	 * @param buf
	 */
	private void addArrayEquals(StringBuffer buf) {

		buf.append("\t void assertArrayEquals(" + returnType + " correct,"
				+ returnType + " result){\n");
		buf
				.append("\t\t StringBuffer correctBuf;\n\t\t StringBuffer resultBuf;\n");
		buf.append("\t\t int c;\r");
		String re = "result";
		String co = "correct";
		buf.append("\t\t c = result.length;\r\r");

		buf.append("\n\t\t correctBuf = new StringBuffer(\"{\");\n");
		buf.append("\t\t resultBuf = new StringBuffer(\"{\");\n");
		buf.append("\t\t for (int i = 0; i < correct.length; i++) {\n");
		buf.append("\t\t\t correctBuf.append(\"\\\"\"+correct[i]+\"\\\"\");\n");
		buf.append("\t\t\t if(i!=correct.length-1){\n");
		buf.append("\t\t\t\t correctBuf.append(\", \");\n");
		buf.append("\t\t\t }\n");
		buf.append("\t\t }\n");
		buf.append("\t\t correctBuf.append(\"}\");\n");
		buf.append("\t\t for (int i = 0; i < result.length; i++) {\n");

		buf.append("\t\t\t resultBuf.append(\"\\\"\"+result[i]+\"\\\"\");\n");
		buf.append("\t\t\t if(i!=result.length-1){\n");

		buf.append("\t\t\t\t resultBuf.append(\", \");\n");
		buf.append("\t\t\t }\n");
		buf.append("\t\t }\n");
		buf.append("\t\t resultBuf.append(\"}\");\n");
		buf
				.append("\t\t assertTrue(\"Arrays are different. "
						+ "\\nexpected \" + correctBuf.toString() + \" but was \" + resultBuf.toString() + "
						+ "\"\\nexpected.length (\"+correct.length+\") does not match result.length (\"+result.length+\") \",result.length==correct.length);\r\r");

		buf.append("\t\t for(int i = 0; i < c; i++){\r");
		re += "[i]";
		co += "[i]";
		buf
				.append("\t\t assertEquals(")
				.append(
						"\"\\nexpected \" + correctBuf.toString() + \" but was \" + resultBuf.toString() + "
								+ "\"\\nat position \" + i + \" \",").append(
						co + ",").append(re + ");\r\t\t ");
		buf.append("\r\t\t }\r}\t ");
	}

	public String createTests() {
		StringBuffer result = new StringBuffer();
		String[] examples = problemStatement.substring(
				problemStatement.indexOf("Examples")).split("\\s\\d\\)\\s\\s");
		for (int i = 1; i < examples.length; i++) {
			if (examples[i].indexOf("Returns:") == -1)
				continue;

			result.append("\t public void test").append(
					className + (i - 1) + "()").append(
					" throws Exception { \r\t\t ");

			// first element is "Examples:" therefore we start with 1
			result.append(returnType + " correct;\r");
			result.append("\t\t " + returnType + " result;\r");

			result.append("\t\t long start,end,diff;\r");
			result.append("\t\t int limit = 8000;\r\r");

			String allParams = examples[i].substring(0, examples[i]
					.indexOf("Returns:"));
			allParams = allParams.replace((char) 160, ' ');
			allParams = allParams.trim();
			// using stringtokenizer cause we need the delims...
			StringTokenizer t = new StringTokenizer(allParams, "\r\n", true);

			StringBuffer toTest = new StringBuffer();
			int k = 0;
			boolean inArray = false;
			boolean inString = false;
			int numTok = t.countTokens();
			for (int j = 0; j < numTok; j++) {
				String s = t.nextToken();
				s = s.trim();
				if ((inString || inArray) && s.equals(" "))
					toTest.append(s);
				if (!s.equals(" ") && !s.equals("")
						&& s.indexOf("" + (char) 160) == -1) {
					// recent param is a string
					if (!inString && params[k].equals("String")
							&& s.startsWith("\"")) {
						inString = true;
						if (s.length() > 1 && s.endsWith("\"")) {
							inString = false;
						}
						toTest.append(s);
					} else if (inString && params[k].equals("String")
							&& s.endsWith("\"")) {
						inString = false;
						toTest.append(s);
					}

					// recent param is an array
					else if (!inArray && params[k].indexOf("[]") != -1) {
						toTest.append("new " + params[k] + s);
						if (s.indexOf("}") == -1)
							inArray = true;
					} else {
						toTest.append(s);
						if ((params[k].equals("double") || params[k]
								.equals("float"))) {
							if (s.indexOf(".") == -1) {
								toTest.append(".0");
							}
						}
						if (params[k].equals("long")) {
								toTest.append("L");
						}
						if (params[k].indexOf("[]") == -1) {
							inArray = false;
						}
						if (s.indexOf("}") != -1)
							inArray = false;
					}
					if (!inArray && !inString && k < params.length - 1)
						k++;

					if (j < numTok - 1 && !inArray && !inString) {
						toTest.append(",");
					}
				}

			}
			// returns int or double, default
			String correct = examples[i].substring(
					examples[i].indexOf("Returns:")).split("\\s")[1];

			// returns is a string
			if (returnType.equals("String")) {
				correct = "\""
						+ examples[i]
								.substring(examples[i].indexOf("Returns:"))
								.split("\"")[1] + "\"";
			}
			// returns is an array
			else if (returnType.indexOf("[]") != -1) {
				String ret = examples[i].substring(examples[i]
						.indexOf("Returns:"));
				correct = "new " + returnType
						+ ret.substring(ret.indexOf("{"), ret.indexOf("}") + 1);
			}

			System.out.println("correct value is: " + correct);

			result.append("\t\t correct = " + correct);
			if (returnType.equals("long")) {
				result.append("L");
			}
			result.append(";\r");
			// time complexity benchmarking:
			result
					.append("\t\t start = System.currentTimeMillis(); result = toTest.");
			result.append(method + "(").append(toTest + ");\r");
			result
					.append("\t\t end = System.currentTimeMillis(); diff = end - start;\r");
			result
					.append("\t\t assertTrue(\"High time complexity. \\nExecution time exceeds \" + limit +\" ms, took \" + diff + \" ms.\",diff<limit);\r");
			if (returnType.indexOf("[]") != -1
					|| returnType.indexOf("String") != -1
					|| (returnType.indexOf("int") == -1
							&& returnType.indexOf("long") == -1
							&& returnType.indexOf("short") == -1
							&& returnType.indexOf("byte") == -1
							&& returnType.indexOf("double") == -1
							&& returnType.indexOf("float") == -1
							&& returnType.indexOf("char") == -1 && returnType
							.indexOf("boolean") == -1))
				result
						.append("\t\t assertTrue(\"The method returns null.\", result!=null);\n");

			// unittest for arrays: compare each element and the length
			if (returnType.indexOf("[]") != -1) {
				result.append("\t\t assertArrayEquals(correct,result);\n");

			}
			// unittest for floating point
			if (returnType.indexOf("double") != -1
					|| returnType.indexOf("float") != -1) {
				result.append("\t\t assertEquals(").append("correct" + ",")
						.append("result" + ",1e-9").append(");\r\t\t ");
			}
			// default: string, int
			else if (returnType.indexOf("[]") == -1) {
				result.append("\t\t assertEquals(").append("correct" + ",")
						.append("result" + ");\r\t\t ");
			}
			result.append("\r");
			result.append("\r\t } \r");
		}
		// replace bla,))); with bla))); this is because the tokenizer has to
		// return delims...
		return result.toString().replaceAll(",\\)", "\\)");

	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}
}