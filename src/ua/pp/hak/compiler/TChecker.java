package ua.pp.hak.compiler;

import java.util.ArrayList;

public class TChecker {

	ArrayList<Integer> simpleAttributes;
	ArrayList<Integer> simpleNumericAttributes;
	ArrayList<Integer> multiValuedAttributes;
	ArrayList<Integer> multiValuedNumericAttributes;
	ArrayList<Integer> repeatingAttributes;
	ArrayList<Integer> repeatingNumericAttributes;

	/**
	 * Check if string contains even quantity of quotes
	 * 
	 * @param str
	 * @return boolean
	 */
	boolean matchQuotes(String str) {
		int count = str.length() - str.replace("\"", "").length();

		if (count % 2 == 0) {
			return true;
		}

		return false;
	}

	boolean isSimpleAttr(int attr) {
		if (simpleAttributes.contains(attr)) {
			return true;
		}

		return false;
	}

	void initSimpleAttributes() {
		simpleAttributes = new ArrayList<>();
	}

	void initSimpleNumericAttributes() {
		simpleNumericAttributes = new ArrayList<>();
	}
}
