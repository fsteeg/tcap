package com.quui.tc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String orig = "jgl;ks \r\n dfjgl;k\r\nClass:\r\nColor\r\n fsafdsadf \r\n afasdf";
		System.out.println("Orig: \n" + orig);
		String regex = ".*Class:\\s*\r\n(\\w+)\\s*\r\n.*";
		Matcher m = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(orig);
		if (m.matches())
			System.out.println("Class heisst: " + m.group(1));

	}

}
