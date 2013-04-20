package com.live.rrutt.newsgoggles;

import com.live.rrutt.newsgoggles.scala.RulesEngine;
import com.live.rrutt.newsgoggles.scala.test.RulesEngineTest;

public class NewsGoggles {

	private static Boolean testing = false;
	
	public NewsGoggles() {
		this(new String[] {});
	}

	public NewsGoggles(String[] args) {
		System.out
				.println("Rick Rutt's News Goggles - Using Scala "
//						+ scala.getVersion()
						);

		for (String arg : args) {
			if ((arg.length() > 1) && (arg.charAt(0) == '-')) {
				if (arg.equalsIgnoreCase("-test")) {
					testing = true;
					System.out.println("Test mode enabled.");
				} else {
					System.out.println("Unknown command argument ignored: "
							+ arg);
				}
			} else {
				System.out.println("Unknown command argument ignored: " + arg);
			}
		}
	}

	public void load() throws Exception {
		DataLoader loader = new DataLoader();
		String dataText = loader.load();
		
		boolean dataOk = RulesEngine.loadData(dataText);
		if (!dataOk) {
			throw new Exception("Invalid data text.");
		}
	}

	private void run() {
		try {
			load();
			
			RulesEngine.show_all_news();

			if (testing) {
				boolean testsAllPass = RulesEngineTest.runAllTests();
				if (testsAllPass) {
					System.out.println("Test run succeeded.");
				} else {
					System.out.println("Test run did NOT succeed.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void write(String s) {
//		outputArea.append(s);
//		outputArea.setCaretPosition(outputArea.getText().length());
		System.out.print(s);
	}

	public static void main(String args[]) {
		new NewsGoggles(args).run();
	}
}
