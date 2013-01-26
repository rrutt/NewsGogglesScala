package com.live.rrutt.newsgoggles;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TheoryLoader {

	private static final String[] TheoryResourceNames = {
		"com/live/rrutt/newsgoggles/Articles.pl",
		"com/live/rrutt/newsgoggles/Preferences.pl",
		"com/live/rrutt/newsgoggles/Rules.pl",
		"com/live/rrutt/newsgoggles/Test.pl",
	};
	
	public String load() {
		StringBuilder sb = new StringBuilder();

		for (String resourceName : TheoryResourceNames) {
			appendResource(resourceName, sb);
		}

		String theoryString = sb.toString();

		return theoryString;
	}

	private void appendResource(String resourceName, StringBuilder sb) {
		System.out.println("Loading " + resourceName);
		
		try {
			InputStream theoryInputStream = null;
			ClassLoader cl = getClass().getClassLoader();
			if (cl == null) {
				throw new Exception("Could not get ClassLoader");
			}

			theoryInputStream = cl.getResourceAsStream(resourceName);
			if (theoryInputStream == null) {
				throw new Exception("Could not load theory resource: "
						+ resourceName);
			}

			InputStreamReader isr = new InputStreamReader(theoryInputStream);
			BufferedReader br = new BufferedReader(isr);

			String s = br.readLine();
			while (s != null) {
				// System.out.println(s);
				sb.append(s);
				sb.append('\n');

				s = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
