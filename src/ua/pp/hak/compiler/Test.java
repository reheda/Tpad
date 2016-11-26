package ua.pp.hak.compiler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	private static String NEW_LINE = "\n";

	public static void main(String[] args) {
		String expr = "  IF A[816].Values ( what) --asdasd\n IS NOT NULL AND a>=1 --asdasd\n THEN \"asda else sd\" --- sdfsdf\n"
				+ "ELSE IF A[1].Value(123) > 3 THEN \"blasd\" " + " ELSE IF b>3 THEN " + "ELSE \"ooooo\"  ";

		System.out.println(checkExpression(expr, 0, 0));
	}

	private static String checkExpression(String expr, int p0, int p1) {
		String exprCleaned = null;
		StringBuilder errors = new StringBuilder();
		String error = null;
		// check comments
		if (!isCommentsValid(expr)) {
			error = "Comment can't contains quote";
			errors.append(error);

			return errors.toString();
		}

		// erase comments. will erase until line feed
		exprCleaned = expr.replaceAll("--.*", "");

		// check quotes
		if (!matchQuotes(expr)) {
			error = "Quote is not closed";
			errors.append(error);

			return errors.toString();
		}

		// erase text surrounded by quotes
		exprCleaned = exprCleaned.replaceAll("\".*?\"", "\"\"");

		// check brackets
		if (!matchBrackets(exprCleaned)) {
			error = "Bracket is not closed/opened";
			errors.append(error);

			return errors.toString();
		}

		// erase text surrounded by brackets
		exprCleaned = exprCleaned.replaceAll("\\(.*?\\)", "()");

		// clean expression to make it parsable
		exprCleaned = exprCleaned.replaceAll("\\n+", " ").replaceAll("\\s+", " ").replaceAll(" \\. ", ".")
				.replaceAll(" ?\\[ ?", "[").replaceAll(" ?\\] ?", "]").replaceAll(" ?\\( ?", "(")
				.replaceAll(" ?\\) ?\\.", ").").replaceAll("(?i)ELSE IF", "ELSEIF");

		System.out.println(exprCleaned);

		// check if expression is returnValue or ifThenElseStatement
		if (exprCleaned.toUpperCase().contains(" IF ") || exprCleaned.contains(" THEN ")
				|| exprCleaned.contains(" ELSEIF ") || exprCleaned.contains(" ELSE ")) {

			// if its ifThenElseStatement
			error = checkIfThenElseStatement(exprCleaned);
			errors.append(error);

			return errors.toString();

		} else {
			System.out.println("not implemented");
		}

		return null;
	}

	private static String checkElseStatementQuantity(String str) {
		String temp = str.replaceAll("(?i) ELSE ", " ELS ");
		int elseCounter = str.length() - temp.length();
		if (elseCounter > 1) {
			return "You are using ELSE statement " + elseCounter + " times, but have to use only 1 time";

		}

		return null;
	}

	private static String checkIfStatementQuantity(String str) {
		String temp = str.replaceAll("(?i)^IF ", "I ").replaceAll("(?i) IF ", " I ");
		int ifCounter = str.length() - temp.length();
		if (ifCounter > 1) {
			return "You are using IF statement " + ifCounter + " times, but have to use only 1 time";

		}

		return null;
	}

	private static String checkThenStatementQuantity(String str) {
		int ifCounter = str.length() - str.replaceAll("(?i)^IF ", "I ").replaceAll("(?i) IF ", " I ").length();
		int elseIfCounter = str.length() - str.replaceAll("(?i) ELSEIF ", " ELSEI ").length();
		int thenCounter = str.length() - str.replaceAll("(?i) THEN ", " THE ").length();

		if (thenCounter != elseIfCounter + ifCounter) {
			return "You are using THEN statement " + thenCounter + " times, but have to use "
					+ (elseIfCounter + ifCounter) + " time(s)";
		}

		return null;
	}

	private static boolean isCommentsValid(String textExpr) {
		boolean isValid = true;
		String[] lines = textExpr.split("\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.contains("--") && line.contains("\"") && line.indexOf("--") < line.lastIndexOf("\"")) {
				isValid = false;
			}
		}

		return isValid;
	}

	private static boolean matchQuotes(String str) {
		int count = str.length() - str.replace("\"", "").length();

		if (count % 2 == 0) {
			return true;
		}

		return false;
	}

	private static boolean matchBrackets(String str) {
		int leftBracketCount = str.length() - str.replace("(", "").length();
		int rightBracketCount = str.length() - str.replace(")", "").length();

		if (leftBracketCount == rightBracketCount) {
			return true;
		}

		return false;
	}

	private static String checkIfThenElseStatement(String exprCleaned) {

		StringBuilder errors = new StringBuilder();
		String error = null;
		String structure = NEW_LINE + NEW_LINE + "Valid structure:" + NEW_LINE + "IF statement THEN returnValue"
				+ NEW_LINE + "[ ELSE IF statement THEN returnValue ]" + NEW_LINE + "[ ELSE returnValue ]";

		// count "if" words. qty should be 1 or 0
		error = checkIfStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);

			return errors.toString();

		}

		// count "else" words. qty should be 1 or 0
		error = checkElseStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);

			return errors.toString();
		}

		// check "then" words. should be same quantity as qty("elseif") +1
		error = checkThenStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);

			return errors.toString();
		}

		boolean isIfThenElseStatementValid = exprCleaned
				.matches("(?i)^ ?IF .*? THEN .*?( ELSEIF .*? THEN .*?)*?( ELSE .*?)?");

		if (!isIfThenElseStatementValid) {
			errors.append(structure);

			return errors.toString();
		}

		String regex = "IF (.*?) THEN ";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(exprCleaned);
		while (m.find()) {
			String condition = m.group(1);
			error = checkCondition(condition);
			if (error != null) {
				errors.append(error);

				return errors.toString();
			}
		}

		return null;
	}

	private static String checkCondition(String condition) {
		// -if without "AND" and "OR":
		// -should have 1 and only 1 occurence one of the following ("<", ">",
		// "=", "LIKE", "IS")
		// -should NOT have "_"
		// -should have correct comment (without quotes)
		// -should have pair quotes
		//
		// -if with "AND" and "OR":
		// -split by "AND" and "OR" then do the same as without "AND" and "OR"
		StringBuilder errors = new StringBuilder();
		String error = null;

		String structure = NEW_LINE + NEW_LINE + "Valid condition:" + NEW_LINE + "value1 operator value2";

		String[] temp = condition.split(" AND ");
		ArrayList<String> conditions = new ArrayList<String>();
		for (int i = 0; i < temp.length; i++) {
			for (String str : temp[i].split(" OR ")) {
				conditions.add(str);
			}
		}

		for (String con : conditions) {
			int operatorsQty = con.length()
					- con.replaceAll(">=", "1").replaceAll("<=", "1").replaceAll("<", "").replaceAll(">", "")
							.replaceAll("=", "").replaceAll(" LIKE ", " LIK ").replaceAll(" IS ", " I ").length();
			if (operatorsQty > 1) {
				error = "Condition have to contains only 1 operator";
				errors.append(error);
				errors.append(structure);

				return errors.toString();
			} else if (operatorsQty == 0) {
				error = "Condition have to contains operator";
				errors.append(error);
				errors.append(structure);

				return errors.toString();
			}

			int underScroreCount = con.length() - con.replaceAll("_", "").length();
			if (underScroreCount > 0) {
				error = "Condition DON'T have to contains underscore";
				errors.append(error);

				return errors.toString();
			}
		}

		return null;
	}

	private static String checkValue(String value) {

		return null;
	}
}