package ua.pp.hak.compiler;

import java.util.ArrayList;
import java.util.List;

public class Test2 {
	public static void main(String[] args) {
		String ttt = "A[5498].Value, A[31].Values.Where(\"\", \"\", \"\"), A[4446].Values.Where(\"\")";
		String[] list = correctSplitByComma(ttt);
		for (int i = 0; i < list.length; i++) {
			System.out.println(list[i]);
		}
	}

	private static String[] correctSplitByComma(String str) {
		str = str.replaceAll(" ?, ?", ",");
		
		List<String> list = new ArrayList<>();
		String temp = null;
		int start = 0;
		int startWithOld = 0;
		int counter = 0;
		int point = 0;
		while (start < str.length() && point != -1) {
			point = str.indexOf(",", start);

			if (point != -1) {
				temp = str.substring(startWithOld, point);

			} else {
				temp = str.substring(startWithOld);
			}
			if (matchBrackets(temp)) {
				list.add(temp);
				counter = 0;
				start = point + 1;
				startWithOld = start;
			} else {
				if (counter == 0) {
					startWithOld = start;
				}
				start = point + 1;
				counter++;
			}

		}

		return list.toArray(new String[list.size()]);
	}

	private static boolean matchBrackets(String str) {
		int leftBracketCount = str.length() - str.replace("(", "").length();
		int rightBracketCount = str.length() - str.replace(")", "").length();

		if (leftBracketCount == rightBracketCount) {
			return true;
		}

		return false;
	}
}
