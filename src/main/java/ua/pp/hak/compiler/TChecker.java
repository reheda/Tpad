package ua.pp.hak.compiler;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import ua.pp.hak.ui.Notepad;
import ua.pp.hak.ui.SquigglePainter;
import ua.pp.hak.util.Attribute;

public class TChecker {
	final static Logger logger = LogManager.getLogger(TChecker.class);

	static {
		initFunctions();
		initFunctionsWithParams();
	}

	static RSyntaxTextArea taExpr;
	static final String NEW_LINE = "\n";
	static final String KEYWORD_NOTE = NEW_LINE + NEW_LINE + ">>> PAY ATTENTION <<<" + NEW_LINE
			+ "KEYWORDS should be surrounded by space or line feed" + NEW_LINE
			+ "- Hint: search for \"(?i)(?<!\\s)KEYWORD|KEYWORD(?!\\s)\" with turned on Regex";

	static List<Attribute> attibutes = DatabaseStAXParser.parse();
	static ArrayList<Object> highlighterTags = new ArrayList<>();

	private static ArrayList<Function> functions;

	public static ArrayList<Function> getFunctions() {
		return functions;
	}

	private static ArrayList<FunctionWithParameters> functionsWithParams;

	private static void eraseAllHighlighter(RSyntaxTextArea taExpr) {
		for (Object tag : highlighterTags) {
			taExpr.getHighlighter().removeHighlight(tag);
		}
		highlighterTags.clear();
		taExpr.repaint();
	}

	public static List<Attribute> getAttributes() {
		return attibutes;
	}

	public static String getAttributeType(int attr) {
		for (Attribute attribute : attibutes) {
			if (attribute.getId() == attr) {
				return attribute.getType();
			}
		}
		return null;
	}

	public static void check(Notepad npd) {
		long start = System.nanoTime();

		try {
			taExpr = npd.getExprTextArea();
			JTextArea taExprRes = npd.getExprResTextArea();
			String textExpr = taExpr.getText();
			StringBuilder sbErrors = new StringBuilder();

			Color green = new Color(0, 188, 57);
			Color red = new Color(189, 0, 0);
			Color orange = new Color(233, 144, 2);

			boolean isWholeExpressionValid = true;

			eraseAllHighlighter(taExpr);

			String error = null;
			logger.info("Checking expression...");
			error = checkExpression(textExpr);
			if (error != null) {
				logger.info("Expression check result: " + error);
				sbErrors.append(error);
				isWholeExpressionValid = false;
			} else {
				logger.info("Expression check result: expression is OK");
			}

			Color color = null;
			if (isWholeExpressionValid) {
				color = green;
				String textToOutput = "Expression is valid!";

				// check deactivated attributes
				String deactivatedAttrNote = checkDeactivatedAttributes(textExpr);
				if (deactivatedAttrNote != null) {
					textToOutput += NEW_LINE;
					textToOutput += NEW_LINE;
					textToOutput += deactivatedAttrNote;
					color = orange;
				}

				taExprRes.setText(textToOutput);
			} else {
				color = red;
				// taExprRes.setText("Error! \n" + sbErrors.toString());
				taExprRes.setText(sbErrors.toString());
				taExprRes.setCaretPosition(0);
			}

			// set color of the expression result's text area
			taExprRes.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, color),
					new EmptyBorder(2, 5, 2, 0)));

			logger.info("Finish checking expression");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		long elapsedTime = System.nanoTime() - start;
		logger.info("Elapsed time to check: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");
	}

	public static String checkExpression(String exprOriginal) {

		StringBuilder errors = new StringBuilder();
		String error = null;
		String exprCleaned = exprOriginal.trim();

		if (exprCleaned.isEmpty()) {
			error = "Expression is empty";
			errors.append(error);

			return errors.toString();
		}

		// check comments
		error = checkComments(exprCleaned);
		if (error != null) {
			error = "Comment can't contains quote";
			errors.append(error);
			return errors.toString();
		}

		// erase comments. will erase until line feed
		exprCleaned = exprCleaned.replaceAll("--.*", "");

		// check quotes
		error = checkQuotes(exprCleaned);
		if (error != null) {
			errors.append(error);
			return errors.toString();
		}

		// check references
		error = checkReferences(exprOriginal);
		if (error != null) {
			errors.append(error);
			return errors.toString();
		}

		// erase text surrounded by quotes
		exprCleaned = exprCleaned.replaceAll("(?s)\".*?\"", "\"\"").replaceAll("(\"\")+", "\"\"");

		// replace Reference with text(REFERENCE) within text surrounded by
		// dollar ($)
		exprCleaned = exprCleaned.replaceAll("(?s)\\$.*?\\$", "\\$REFERENCE\\$");

		// clean expression to make it parsable
		exprCleaned = exprCleaned.replaceAll("\\n+", " ").replaceAll("\\s+", " ").replaceAll("(?i)ELSE IF", "ELSEIF")
				.replaceAll(" ?\\. ?", ".").replaceAll(" ?\\[ ?", "[").replaceAll(" ?\\] ?\\.", "].")
				.replaceAll(" ?\\( ?", "(").replaceAll(" ?\\) ?\\.", ").");

		exprCleaned = exprCleaned.replaceAll("(?i) is ", " IS ");
		exprCleaned = exprCleaned.replaceAll("(?i) not", " NOT");
		exprCleaned = exprCleaned.replaceAll("(?i)(?<!\\w)null", "NULL");
		exprCleaned = exprCleaned.replaceAll("(?i) then", " THEN");
		exprCleaned = exprCleaned.replaceAll("(?i) else", " ELSE");
		exprCleaned = exprCleaned.replaceAll("(?i) when", " WHEN");
		exprCleaned = exprCleaned.replaceAll("(?i) end", " END");
		exprCleaned = exprCleaned.replaceAll("(?i)case ", "CASE ");
		exprCleaned = exprCleaned.replaceAll("(?i)if ", "IF ");
		exprCleaned = exprCleaned.replaceAll("(?i)coalesce", "COALESCE");
		exprCleaned = exprCleaned.replaceAll("(?i) in", " IN");

		// System.out.println(exprCleaned);

		// split by semicolon
		String[] statements = exprCleaned.split(" ?; ?");

		for (int i = 0; i < statements.length; i++) {
			if (statements[i].trim().isEmpty()) {
				errors.append("There is empty statement");
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				// if (statements[i].isEmpty()) {
				// errors.append("Search for \";;\"");
				// } else {
				errors.append("Look at empty values between two semicolons.");
				errors.append(NEW_LINE);
				errors.append("- Hint: search for \";\\s+;\" with turned on Regex");
				// }
				return errors.toString();

			}

			// check if expression is returnValue or ifThenElseStatement
			if (statements[i].toUpperCase().contains("CASE ") || statements[i].toUpperCase().contains("WHEN")
					|| statements[i].toUpperCase().contains("END")) {

				error = checkCaseStatement(statements[i]);
				if (error != null) {
					errors.append(error);
					errors.append(NEW_LINE);
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(statements[i]);
					return errors.toString();
				}

			} else if (statements[i].toUpperCase().contains("IF ") || statements[i].contains(" ELSEIF ")
					|| statements[i].contains(" THEN ") || statements[i].contains(">") || statements[i].contains("<")
					|| statements[i].contains("=") || statements[i].contains(" IS ") || statements[i].contains(" AND ")
					|| statements[i].contains(" OR ") || statements[i].contains(" LIKE ")
					|| statements[i].contains(" IN(")) {

				// if its ifThenElseStatement
				error = checkIfThenElseStatement(statements[i]);
				if (error != null) {
					errors.append(error);
					errors.append(NEW_LINE);
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(statements[i]);
					return errors.toString();
				}
			} else {
				error = checkReturnValue(statements[i]);
				if (error != null) {
					errors.append(error);
					errors.append(NEW_LINE);
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(statements[i]);
					return errors.toString();
				}
			}
		}

		return null;
	}

	private static String checkQuotes(String expr) {

		StringBuilder errors = null;
		int charsBeforeTheCurrentLine = 0;
		if (!matchQuotes(expr)) {
			SquigglePainter red = new SquigglePainter(Color.RED);
			int p0 = 0, p1 = 0;
			String[] lines = expr.split("\\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (!matchQuotes(line)) {
					p0 = line.lastIndexOf("\"");
					p1 = line.length();
					try {
						highlighterTags.add(taExpr.getHighlighter().addHighlight(charsBeforeTheCurrentLine + p0,
								charsBeforeTheCurrentLine + p1, red));
						taExpr.repaint();
					} catch (BadLocationException e) {
						logger.error(e.getMessage());
					}
					if (errors == null) {
						errors = new StringBuilder("Lines: ");
						errors.append(i + 1);
					} else {
						errors.append(",").append(i + 1);
					}
				}
				charsBeforeTheCurrentLine += line.length() + 1;
			}

			if (errors != null) {
				String error = "Quote is not closed";
				errors.append(". ").append(error);
				return errors.toString();
			}
		}

		return null;
	}

	/**
	 * Check if string contains even quantity of quotes
	 * 
	 * @param str
	 * @return boolean
	 */
	static boolean matchQuotes(String str) {
		int count = str.length() - str.replace("\"", "").length();

		if (count % 2 == 0) {
			return true;
		}

		return false;
	}

	private static String errorOfReferences(String input) {
		String str = input.replaceAll("\\s+", " ");
		int count = str.length() - str.replace("$", "").length();

		if (count % 2 != 0) {
			String error = "Reference is not closed";
			return error;
		}

		int endIndex = 0;
		for (int i = 0; i < count / 2; i++) {

			int startIndex = str.indexOf("$", endIndex);
			endIndex = str.indexOf("$", startIndex + 1) + 1;
			String reference = str.substring(startIndex, endIndex);
			if (reference.replace("$", "").trim().contains(" ")) {
				String error = "You shouldn't use space or line feed in Reference";
				return error;
			}
		}

		return null;
	}

	private static String checkReferences(String expr) {
		String exprCleaned = expr;
		// erase text surrounded by quotes
		exprCleaned = exprCleaned.replaceAll("(?s)\".*?\"", "\"\"").replaceAll("(\"\")+", "\"\"");

		StringBuilder errors = null;
		int charsBeforeTheCurrentLine = 0;
		if (errorOfReferences(exprCleaned) != null) {
			SquigglePainter red = new SquigglePainter(Color.RED);
			int p0 = 0, p1 = 0;
			String[] lines = expr.split("\\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				String error = errorOfReferences(line.replaceAll("(?s)\".*?\"", "\"\"").replaceAll("(\"\")+", "\"\""));
				if (error != null) {
					// p0 = line.lastIndexOf("$");
					p0 = 0;
					p1 = line.length();
					try {
						highlighterTags.add(taExpr.getHighlighter().addHighlight(charsBeforeTheCurrentLine + p0,
								charsBeforeTheCurrentLine + p1, red));
						taExpr.repaint();
					} catch (BadLocationException e) {
						logger.error(e.getMessage());
					}
					if (errors == null) {
						errors = new StringBuilder("Lines: ");
						errors.append(i + 1);
					} else {
						errors.append(",").append(i + 1);
					}
				}
				charsBeforeTheCurrentLine += line.length() + 1;
			}

			if (errors != null) {
				errors.append(". ").append(errorOfReferences(exprCleaned));
				return errors.toString();
			}
		}

		return null;
	}

	static String checkComments(String textExpr) {
		StringBuilder errors = null;
		boolean isValid = true;
		int charsBeforeTheCurrentLine = 0;
		SquigglePainter redPainter = new SquigglePainter(Color.RED);
		int p0 = 0, p1 = 0;
		String[] lines = textExpr.split("\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.contains("--") && line.contains("\"") && line.indexOf("--") < line.lastIndexOf("\"")) {
				isValid = false;
				p0 = line.lastIndexOf("\"");
				p1 = line.length();
				try {
					highlighterTags.add(taExpr.getHighlighter().addHighlight(charsBeforeTheCurrentLine + p0,
							charsBeforeTheCurrentLine + p1, redPainter));
					taExpr.repaint();
				} catch (BadLocationException e) {
					logger.error(e.getMessage());
				}

				if (errors == null) {
					errors = new StringBuilder("Lines: ");
					errors.append(i + 1);
				} else {
					errors.append(",").append(i + 1);
				}
			}
			charsBeforeTheCurrentLine += line.length() + 1;
		}

		if (!isValid) {
			String error = "Comment can't contains quote";
			errors.append(". ").append(error);
			return errors.toString();
		}
		return null;
	}

	private static void initFunctionsWithParams() {
		final int MAX_LIMIT = 1000;
		functionsWithParams = new ArrayList<>();
		functionsWithParams.add(new FunctionWithParameters("GetAncestry()", 0, 1, "Boolean"));
		functionsWithParams.add(new FunctionWithParameters("GetDescendants()", 0, 1, "Boolean"));
		functionsWithParams.add(new FunctionWithParameters("ToLower()", 0, 1, "Boolean"));
		functionsWithParams.add(new FunctionWithParameters("ExtractDecimals()", 0, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("GetDateTime()", 0, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("AtLeast()", 1, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("AtMost()", 1, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("MultiplyBy()", 1, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("ToTitleCase()", 0, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("GetLine()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("GetLineBody()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("Skip()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("Take()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("Flatten()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("GetFullColorDescription()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("ListPaths()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("GetPath()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("EraseTextSurroundedBy()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Format()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("IsDescendantOf()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Postfix()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Prefix()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("ToString()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("ToText()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Match()", 0, MAX_LIMIT, "Int32[]"));
		functionsWithParams.add(new FunctionWithParameters("Split()", 0, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("UseSeparators()", 0, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("Where()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereNot()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereUnit()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereUnitOrValue()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereCategory()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereManufacturer()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereModelName()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereProductLine()", 1, MAX_LIMIT, "String[]"));

		functionsWithParams.add(new FunctionWithParameters("IfLike()", 2, 2, "String", "String"));
		functionsWithParams.add(new FunctionWithParameters("IfLongerThan()", 2, 2, "Int32", "String"));
		functionsWithParams.add(new FunctionWithParameters("FlattenWithAnd()", 0, 2, "Int32", "String"));
		functionsWithParams.add(new FunctionWithParameters("RegexReplace()", 2, 2, "String", "String"));
		functionsWithParams.add(new FunctionWithParameters("ListUSM()", 0, 2, "String", "String"));
		functionsWithParams.add(new FunctionWithParameters("Erase()", 1, 3, "String", "Boolean", "String"));
		functionsWithParams.add(new FunctionWithParameters("Shorten()", 1, 3, "Int32", "String", "String"));
		functionsWithParams
				.add(new FunctionWithParameters("Replace()", 2, 4, "String", "String", "String", "StringComparison"));
		functionsWithParams
				.add(new FunctionWithParameters("IsBiggerThan()", 4, 4, "Double", "Double", "Double", "Double"));

	}

	private static void initFunctions() {
		functions = new ArrayList<>();
		functions.add(new Function("Main", "AlternativeCategory", "ProductCategories"));
		functions.add(new Function("HasText", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("IsEmpty", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("IsDescendantOf()", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("LaunchDate", "DateTime", "Sku"));
		functions.add(new Function("GetDateTime()", "DateTimeOffset", "SystemObject"));
		functions.add(new Function("Count", "ExpressionResultNumeric", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute", "Specs"));
		functions.add(
				new Function("Total", "ExpressionResultNumeric", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("BestImage", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("BoxContents", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("KeySellingPoints", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Ksp", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("MarketingText", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Msds", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("ProductFeatures", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("ProductSheet", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("QuickStartGuide", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Thumbnail", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("UserManual", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Distinct", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("Min()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("MinMax()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("Max()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("Last()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("FlattenWithAnd()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("HtmlEncode()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("First()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("DiscardNulls()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("ExtractDecimals()", "ExpressionResultList", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToLower()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("ToLowerFirstChar()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToTitleCase()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToUpper()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("ToUpperFirstChar()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Erase()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("EraseTextSurroundedBy()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("GetLine()", "ExpressionResultLiteral", "Specs"));
		functions.add(new Function("GetLineBody()", "ExpressionResultLiteral", "Specs"));
		functions.add(new Function("Flatten()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("IfLike()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("IfLongerThan()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Pluralize()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("Postfix()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric", "String"));
		functions.add(new Function("Prefix()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric", "String"));
		functions.add(new Function("RegexReplace()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Replace()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("Shorten()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric", "String"));
		functions.add(new Function("Split()", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("UseSeparators()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("InvariantValues", "ExpressionResultList", "PdmAttributeSet",
				"PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("Values", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute", "DigitalContentItem"));
		functions.add(new Function("ValuesAndUnits", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("ValuesAndUnitsUSM", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("ValuesUSM", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("Format()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Skip()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Take()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Where()", "ExpressionResult", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("WhereNot()", "ExpressionResult", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("BulletFeatures", "ExpressionResultList", "TemplexGenerator"));
		functions.add(new Function("CategoryCodes", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("CategoryKeys", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("Colors", "ExpressionResultList", "Sku"));
		functions.add(new Function("CustomerPns", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("FancyColors", "ExpressionResultList", "Sku"));
		functions.add(new Function("InvariantColors", "ExpressionResultList", "Sku"));
		functions.add(new Function("Keywords", "ExpressionResultList", "Sku"));
		functions.add(new Function("ManufacturerNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ModelNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ProductIds", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ProductLineNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ProductNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("GetLines()", "ExpressionResultList", "Specs"));
		functions.add(new Function("GetAncestry()", "IEnumerable`1", "AlternativeCategory"));
		functions.add(new Function("GetDescendants()", "IEnumerable`1", "AlternativeCategory"));
		functions.add(new Function("CategoryKey", "ExpressionResultNumeric", "RelatedProduct"));
		functions.add(new Function("Length", "ExpressionResultNumeric", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "ProductPackage"));
		functions.add(new Function("LineCount", "ExpressionResultNumeric", "Specs"));
		functions.add(new Function("NonOemAccessories", "ExpressionResultNumeric", "Sku"));
		functions.add(new Function("Order", "ExpressionResultNumeric", "SpecLine"));
		functions.add(new Function("PackQuantity", "ExpressionResultNumeric", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductId", "ExpressionResultNumeric", "Sku", "RelatedProduct"));
		functions.add(new Function("AltCats", "List`1", "ProductCategories"));
		functions.add(new Function("BestImages", "List`1", "DigitalContent"));
		functions.add(new Function("Round()", "ExpressionResultNumeric", "ExpressionResultNumeric",
				"ExpressionResultLiteral"));
		functions.add(new Function("AtLeast()", "ExpressionResultNumeric", "ExpressionResultNumeric",
				"ExpressionResultLiteral"));
		functions.add(new Function("AtMost()", "ExpressionResultNumeric", "ExpressionResultNumeric",
				"ExpressionResultLiteral"));
		functions.add(new Function("MultiplyBy()", "ExpressionResult", "ExpressionResultNumeric",
				"ExpressionResultList", "ExpressionResultLiteral"));
		functions.add(new Function("Match()", "PdmAttributeSet", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereUnit()", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereUnitOrValue()", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereCategory()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("WhereManufacturer()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("WhereModelName()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("WhereProductLine()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("Body", "ExpressionResultLiteral", "SpecLine"));
		functions.add(new Function("Brand", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("CategoryCode", "ExpressionResultLiteral", "ProductCategories", "RelatedProduct",
				"RelatedProductList"));
		functions.add(new Function("CustomerPn", "ExpressionResultLiteral", "RelatedProduct", "RelatedProductList"));
		functions.add(new Function("Description", "ExpressionResultLiteral", "Sku", "DigitalContentItem"));
		functions.add(new Function("GroupName", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("Header", "ExpressionResultLiteral", "SpecLine"));
		functions.add(new Function("Invariant", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("InvariantUnit", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("ItemName", "ExpressionResultLiteral", "Sku"));
		functions.add(new Function("Key", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("Manufacturer", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("MimeType", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ModelName", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("Name", "ExpressionResultLiteral", "Sku", "PdmAttribute", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute", "SpecSection", "AlternativeCategory"));
		functions.add(new Function("PartNumber", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductLine", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductType", "ExpressionResultLiteral", "Sku"));
		functions.add(new Function("Unit", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("UnitUSM", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("Value", "ExpressionResultLiteral", "PdmAttribute", "ExpressionResultNumeric",
				"NullableValueUnitPair", "Gtin"));
		functions.add(new Function("ValueUSM", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("XmlContent", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ListPaths()", "ExpressionResultLiteral", "ProductCategories"));
		functions.add(new Function("ListUSM()", "ExpressionResultLiteral", "PdmAttributeSet"));
		functions.add(new Function("GetFullColorDescription()", "ExpressionResultLiteral", "TemplexGenerator"));
		functions.add(new Function("ToHtml()", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ToPlainText()", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ToText()", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "DateTimeOffset"));
		functions.add(new Function("Url", "Uri", "DigitalContentItem"));
		functions.add(new Function("CompatibleProducts", "ExpressionResultList", "Specs"));
		functions.add(new Function("CAT", "ProductCategories"));
		functions.add(new Function("SKU", "Sku"));
		functions.add(new Function("Request", "RelatedProduct"));
		functions.add(new Function("ParentProducts", "RelatedProductList"));
		functions.add(new Function("MS", "Specs"));
		functions.add(new Function("Sys", "SystemObject"));
		functions.add(new Function("DC", "DigitalContent"));
		functions.add(new Function("Generator", "TemplexGenerator"));
		functions.add(new Function("ES", "Specs"));
		functions.add(new Function("COALESCE()", "You can invoke nothing on it"));
		functions.add(new Function("NOT NULL", "You can invoke nothing on it"));
		functions.add(new Function("NULL", "You can invoke nothing on it"));
		functions.add(new Function("Data[\"\"]", "ExpressionResultLiteral", "RelatedProduct"));
		functions.add(new Function("Gtin", "Gtin", "RelatedProduct"));
		functions.add(new Function("Format", "ExpressionResultLiteral", "Gtin"));
		functions.add(new Function("HasValue", "ExpressionResultLiteral", "ExpressionResultNumeric",
				"NullableValueUnitPair"));
		functions.add(new Function("Height", "NullableValueUnitPair", "ProductPackage"));
		functions.add(new Function("Width", "NullableValueUnitPair", "ProductPackage"));
		functions.add(new Function("Weight", "NullableValueUnitPair", "ProductPackage"));
		functions.add(new Function("Package", "ProductPackage", "RelatedProduct"));
		functions.add(new Function("IsBiggerThan()", "ExpressionResultLiteral", "ProductPackage"));
		functions.add(new Function("BulletFeatures[]", "ExpressionResultLiteral", "TemplexGenerator"));
		functions.add(new Function("Ksp[]", "ExpressionResultLiteral", "DigitalContent"));
		functions.add(new Function("\"\"", "You can invoke nothing on it"));
		functions.add(new Function("Year", "ExpressionResultNumeric", "DateTime"));
		functions.add(new Function("Month", "ExpressionResultNumeric", "DateTime"));
		functions.add(new Function("Day", "ExpressionResultNumeric", "DateTime"));
		functions.add(new Function("ToString()", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "DateTimeOffset"));
		functions.add(new Function("DECODE()", "You can invoke nothing on it"));
		functions.add(new Function("GetPath()", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("CategoryCode", "ExpressionResultLiteral", "Sku"));

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
		int elseIfCounter = str.length() - str.replaceAll("(?i)ELSEIF ", "ELSEI ").length();
		int thenCounter = str.length() - str.replaceAll("(?i)THEN ", "THE ").length();

		if (thenCounter != elseIfCounter + ifCounter) {
			String lines = "";
			if (taExpr != null) {
				if (thenCounter > (elseIfCounter + ifCounter)) {
					lines = getLines(taExpr.getText(), "(?i)(?<!\\s|ELSE|^)IF|IF(?!\\s)");
				} else {
					lines = getLines(taExpr.getText(), "(?i)(?<!\\s)THEN|THEN(?!\\s)");

				}
			}
			return lines + "You are using THEN statement " + thenCounter + " times, but have to use "
					+ (elseIfCounter + ifCounter) + " time(s)";
		}

		return null;
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
		String structure = NEW_LINE + NEW_LINE + "Valid structure:" + NEW_LINE + "IF condition THEN returnValue"
				+ NEW_LINE + "[ ELSE IF condition THEN returnValue ]" + NEW_LINE + "[ ELSE returnValue ]";

		// count "if" words. qty should be 1 or 0
		error = checkIfStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);
			errors.append(KEYWORD_NOTE);
			return errors.toString();

		}

		// count "else" words. qty should be 1 or 0
		error = checkElseStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);
			errors.append(KEYWORD_NOTE);
			return errors.toString();
		}

		// check "then" words. should be same quantity as qty("elseif") +1
		error = checkThenStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);
			errors.append(KEYWORD_NOTE);
			return errors.toString();
		}

		boolean isIfThenElseStatementValid = exprCleaned
				.matches("(?i)^ ?IF .*? THEN .*?( ELSEIF .*? THEN .*?)*?( ELSE .*?)?");

		if (!isIfThenElseStatementValid) {
			errors.append("Invalid IF THEN ELSE statements.");
			errors.append(structure);
			errors.append(KEYWORD_NOTE);
			return errors.toString();
		}

		// check conditions
		int conditionCounter = 0;
		{
			String regex = "(?i)IF (.*?) THEN ";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(exprCleaned);
			while (m.find()) {
				String condition = m.group(1);
				error = checkCondition(condition);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
				conditionCounter++;
			}
		}

		// check return values
		int returnValueCounter = 0;
		String regex = "(?i)( THEN | ELSE )(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(exprCleaned.replaceAll(" ELSEIF ", "\n ELSEIF ").replaceAll(" ELSE ", "\n ELSE "));
		while (m.find()) {
			String thenOrElse = m.group(1);
			String returnValue = m.group(2);
			error = checkReturnValue(returnValue);
			if (error != null) {
				errors.append(error);
				return errors.toString();
			}

			if (thenOrElse.toUpperCase().contains("THEN")) {
				returnValueCounter++;
			}
		}

		// check if quantity of conditions is the same with returnValues after
		// Then keyword
		if (returnValueCounter != conditionCounter) {
			errors.append("Invalid IF THEN ELSE statements.");
			errors.append(NEW_LINE);
			errors.append("- Hint: Check for empty return value");
			errors.append(structure);
			errors.append(KEYWORD_NOTE);
			return errors.toString();
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

		// check brackets
		if (!matchBrackets(condition)) {
			errors.append("Bracket is not closed/opened");
			errors.append(NEW_LINE);
			errors.append(NEW_LINE);
			errors.append("-----");
			errors.append(NEW_LINE);
			errors.append(condition);
			return errors.toString();
		}
		// check brackets matching
		if (!isParenthesisMatch(condition)) {
			errors.append("Bracket is not using prorely");
			errors.append(NEW_LINE);
			errors.append(NEW_LINE);
			errors.append("-----");
			errors.append(NEW_LINE);
			errors.append(condition);
			return errors.toString();
		}

		String contitonCleaned = condition;

		String error = null;
		String structure = NEW_LINE + NEW_LINE + "Valid condition:" + NEW_LINE + "value1 operator value2";

		List<String> conditions = Arrays.asList(contitonCleaned.split("(?i) AND | OR "));

		for (String con : conditions) {

			String conCleaned = con;

			final String COALESCE_TEXT = "COALESCE(";
			final String IN_TEXT = " IN(";
			final String DECODE_TEXT = "DECODE(";

			// erase content surrounded by brackets
			if (conCleaned.toUpperCase().contains(IN_TEXT)) {
				int point = getLastBracketIndex(conCleaned, conCleaned.indexOf(IN_TEXT));
				conCleaned = conCleaned.substring(0, conCleaned.indexOf(IN_TEXT) + IN_TEXT.length())
						+ conCleaned.substring(point);
			}

			if (conCleaned.toUpperCase().contains(COALESCE_TEXT)) {
				int point = getLastBracketIndex(conCleaned, conCleaned.indexOf(COALESCE_TEXT));
				conCleaned = conCleaned.substring(0, conCleaned.indexOf(COALESCE_TEXT) + COALESCE_TEXT.length())
						+ conCleaned.substring(point);
			}

			if (conCleaned.toUpperCase().contains(DECODE_TEXT)) {
				int point = getLastBracketIndex(conCleaned, conCleaned.indexOf(DECODE_TEXT));
				conCleaned = conCleaned.substring(0, conCleaned.indexOf(DECODE_TEXT) + DECODE_TEXT.length())
						+ conCleaned.substring(point);
			}

			// erase brackets, but left Match(number) cause it should return
			// PdmMultivalueAttribute instead of PdmAttributeSet
			conCleaned = eraseBrackets(conCleaned);

			
			int operatorsQty = conCleaned.length() - conCleaned.replaceAll("<>", "1").replaceAll(">=", "1")
					.replaceAll("<=", "1").replaceAll("<", "").replaceAll(">", "").replaceAll("=", "")
					.replaceAll("(?i) LIKE ", " LIK ").replaceAll("(?i) IS ", " I ").replaceAll("(?i) IN\\(", " IN")
					.replaceAll("HasText(?!\\.)", "HasTex").replaceAll("IsEmpty(?!\\.)", "IsEmpt")
					.replaceAll("IsDescendantOf\\(\\)(?!\\.)", "IsDescendantOf(")
					.replaceAll("HasValue(?!\\.)", "HasValu").replaceAll("IsBiggerThan\\(\\)(?!\\.)", "IsBiggerThan(")
					.length();

			if (operatorsQty > 1) {
				error = "Condition '" + conCleaned + "' have to contains only 1 operator";
				errors.append(error);
				errors.append(structure);
				errors.append(KEYWORD_NOTE);
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				errors.append(con);
				if (!con.equals(condition)) {
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(condition);
				}
				return errors.toString();
			} else if (operatorsQty == 0) {
				error = "Condition '" + conCleaned + "' have to contains operator";
				errors.append(error);
				errors.append(structure);
				errors.append(KEYWORD_NOTE);
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				errors.append(con);
				if (!con.equals(condition)) {
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(condition);
				}
				return errors.toString();
			}

			// check values
			if (conCleaned.toUpperCase().contains(" IS ")) {
				String[] values = conCleaned.split("(?i) IS ");
				if (values.length == 2) {

					// check right part
					String rightPart = values[1].trim();
					if (!rightPart.matches("(?i)NOT NULL") && !rightPart.matches("(?i)NULL")) {

						errors.append("You shouldn't check for '");
						errors.append(rightPart);
						errors.append("'. Expected check for NULL");
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(con);
						if (!con.equals(condition)) {
							errors.append(NEW_LINE);
							errors.append("-----");
							errors.append(NEW_LINE);
							errors.append(condition);
						}
						return errors.toString();
					}

					// check left part
					int underScroreCount = values[0].length() - values[0].replaceAll("_", "").length();
					if (underScroreCount > 0) {
						error = "Left part of the condition DON'T have to contains underscore";
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(con);
						if (!con.equals(condition)) {
							errors.append(NEW_LINE);
							errors.append("-----");
							errors.append(NEW_LINE);
							errors.append(condition);
						}
						return errors.toString();
					}

					error = checkValue(values[0]);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(con);
						if (!con.equals(condition)) {
							errors.append(NEW_LINE);
							errors.append("-----");
							errors.append(NEW_LINE);
							errors.append(condition);
						}
						return errors.toString();
					}

					// check parameters
					String tempValue = con.split("(?i) IS ")[0];
					error = checkParametersInCondition(tempValue);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(tempValue);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(con);
						if (!con.equals(condition)) {
							errors.append(NEW_LINE);
							errors.append("-----");
							errors.append(NEW_LINE);
							errors.append(condition);
						}
						return errors.toString();
					}

				} else {
					logger.error("Please report this expression!");
				}

			} else {

				// check values
				{
					String[] values = conCleaned.split("(?i)<>|>=|<=|>|<|=| NOT LIKE | LIKE | NOT IN\\(\\)| IN\\(\\)");
					for (int i = 0; i < values.length; i++) {

						// check left part for underscore
						if (values.length == 2 && i == 0) {
							int underScroreCount = values[i].length() - values[i].replaceAll("_", "").length();
							if (underScroreCount > 0) {
								error = "Left part of the condition DON'T have to contains underscore";
								errors.append(error);
								errors.append(NEW_LINE);
								errors.append(NEW_LINE);
								errors.append("-----");
								errors.append(NEW_LINE);
								errors.append(con);
								if (!con.equals(condition)) {
									errors.append(NEW_LINE);
									errors.append("-----");
									errors.append(NEW_LINE);
									errors.append(condition);
								}
								return errors.toString();
							}
						}

						String[] valuesSplitByUnderscore = values[i].split(" ?_ ?");
						for (int j = 0; j < valuesSplitByUnderscore.length; j++) {

							error = checkValue(valuesSplitByUnderscore[j]);
							if (error != null) {
								errors.append(error);
								errors.append(NEW_LINE);
								errors.append(NEW_LINE);
								errors.append("-----");
								errors.append(NEW_LINE);
								errors.append(con);
								if (!con.equals(condition)) {
									errors.append(NEW_LINE);
									errors.append("-----");
									errors.append(NEW_LINE);
									errors.append(condition);
								}
								return errors.toString();
							}
						}

					}
				}

				// check parameters
				String[] values = con.split("(?i)<>|>=|<=|>|<|=| NOT LIKE | LIKE ");
				for (int i = 0; i < values.length; i++) {
					error = checkParametersInCondition(values[i]);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(values[i]);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(con);
						if (!con.equals(condition)) {
							errors.append(NEW_LINE);
							errors.append("-----");
							errors.append(NEW_LINE);
							errors.append(condition);
						}
						return errors.toString();
					}

				}

			}

		}

		return null;
	}

	private static String checkValue(String value) {
		String valueCleaned = value.trim();
		// erase brackets, but left Match(number) cause it should return
		// PdmMultivalueAttribute instead of PdmAttributeSet
		valueCleaned = eraseBrackets(valueCleaned);

		// ither cases
		valueCleaned = valueCleaned.replaceAll("BulletFeatures\\[\\d+\\]", "BulletFeatures[]")
				.replaceAll("Ksp\\[\\d+\\]", "Ksp[]");

		// value cant finished with "." or ". "

		StringBuilder errors = new StringBuilder();
		if (valueCleaned.matches(".*\\. ?$")) {
			errors.append("Value shouldn't be finished with DOT");
			errors.append(NEW_LINE);
			errors.append(NEW_LINE);
			errors.append("-----");
			errors.append(NEW_LINE);
			errors.append(valueCleaned);
			return errors.toString();
		}

		String[] functns = valueCleaned.split("\\.");

		String previousType = null;

		for (int i = 0; i < functns.length; i++) {
			// check if function exists
			if (!isFunction(functns[i]) && !isPdm(functns[i]) && !isNumber(functns[i]) && !isReference(functns[i])) {
				errors.append("Function '");
				errors.append(functns[i]);
				errors.append("' doesn't exist");
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				errors.append(valueCleaned);
				return errors.toString();
			}

			// if decimal or reference do nothing
			if (isNumber(functns[i]) || isReference(functns[i])) {
				if (previousType != null) {
					errors.append("Function '");
					errors.append(functns[i]);
					errors.append("' shouldn't be invoked on '");
					errors.append(previousType);
					errors.append("'");
					errors.append(NEW_LINE);
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(valueCleaned);
					return errors.toString();
				}

			} else {
				// check if pdm attribute or not
				if (isPdm(functns[i])) {
					int attr = Integer.parseInt(functns[i].replaceAll("[^0-9]", ""));
					String attrType = getAttributeType(attr);
					if (attrType != null) {
						previousType = getPdmReturnType(attrType);

					} else {
						errors.append("Attribute '");
						errors.append(functns[i]);
						errors.append("' doesn't exist");
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(valueCleaned);
						return errors.toString();
					}

					if (!valueCleaned.contains(".")) {
						errors.append("DOT expected");
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(valueCleaned);
						return errors.toString();
					}

				} else {
					if (!isString(functns[i]) && !"COALESCE()".equals(functns[i].toUpperCase())
							&& !"DECODE()".equals(functns[i].toUpperCase()) && !"NULL".equals(functns[i].toUpperCase())
							&& !valueCleaned.contains(".")) {
						errors.append("DOT expected");
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(valueCleaned);
						return errors.toString();
					}

					if (!isFunctionMemberOfValid(functns[i], previousType)) {
						errors.append("Function '");
						errors.append(functns[i]);
						errors.append("' shouldn't be invoked on '");
						errors.append(previousType);
						errors.append("'");
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(valueCleaned);
						return errors.toString();
					}

					previousType = getFunctionReturnType(functns[i], previousType);
				}

			}

		}

		return null;
	}

	public static String getPdmReturnType(String attrType) {
		String pdmReturnType = null;

		final String TYPE_SIMPLE = "Simple";
		final String TYPE_MULTI_VALUED = "Multi-valued";
		final String TYPE_SIMPLE_NUMERIC = "Simple numeric";
		final String TYPE_REPEATING = "Repeating";
		final String TYPE_REPEATING_NUMERIC = "Repeating numeric";
		final String TYPE_MULTI_VALUED_NUMERIC = "Multi-valued numeric";

		switch (attrType) {
		case TYPE_SIMPLE:
			pdmReturnType = "PdmAttribute";
			break;
		case TYPE_SIMPLE_NUMERIC:
			pdmReturnType = "PdmAttribute";
			break;
		case TYPE_MULTI_VALUED:
			pdmReturnType = "PdmMultivalueAttribute";
			break;
		case TYPE_MULTI_VALUED_NUMERIC:
			pdmReturnType = "PdmMultivalueAttribute";
			break;
		case TYPE_REPEATING:
			pdmReturnType = "PdmRepeatingAttribute";
			break;
		case TYPE_REPEATING_NUMERIC:
			pdmReturnType = "PdmRepeatingAttribute";
			break;
		default:
			break;
		}

		return pdmReturnType;
	}

	private static boolean isString(String str) {
		return str.matches("\".*\"");
	}

	private static boolean isNumber(String str) {
		return str.matches("\\d+");
	}

	private static boolean isReference(String functionName) {
		return functionName.matches("\\$.+\\$");
	}

	private static boolean isPdm(String functionName) {
		return functionName.matches("A\\[\\d+\\]");
	}

	private static boolean isFunction(String functionName) {
		if (functionName.matches("Match\\(\\d+\\)")) {
			return true;
		}

		if (functionName.equals("Coalesce()")) {
			functionName = functionName.toUpperCase();
		}

		for (Function function : functions) {
			if (function.getName().equals(functionName)) {
				return true;
			}
		}

		return false;
	}

	public static String getFunctionReturnType(String functionName, String previousType) {
		if (functionName.matches("Match\\(\\d+\\)")) {
			return "PdmRepeatingAttribute";
		}
		for (Function function : functions) {
			if (function.getName().equals(functionName)) {
				if (previousType != null && "ExpressionResult".equals(function.getReturnType())) {
					return previousType;
				} else {
					return function.getReturnType();
				}
			}
		}

		return null;
	}

	private static boolean isFunctionMemberOfValid(String functionName, String previousType) {

		if (functionName.matches("Match\\(\\d+\\)") && previousType.equals("PdmRepeatingAttribute")) {
			return true;
		}

		if (functionName.equals("Coalesce()")) {
			functionName = functionName.toUpperCase();
		}

		for (Function function : functions) {
			if (function.getName().equals(functionName)) {
				String[] membersOf = function.getMembersOf();
				if (membersOf.length == 0 && previousType == null) {
					return true;
				} else if (previousType != null) {

					for (int i = 0; i < membersOf.length; i++) {
						if (membersOf[i].equals(previousType)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private static String checkParametersInCondition(String condition) {
		StringBuilder errors = new StringBuilder();
		String error = null;

		String conCleaned = condition;

		String regex = "\\.(\\w+) ?\\((.*?)\\)";
		Pattern p = Pattern.compile(regex);

		final String COALESCE_TEXT = "COALESCE(";
		String IN_TEXT = "IN(";
		final String DECODE_TEXT = "DECODE(";

		if (condition.contains(COALESCE_TEXT) && condition.contains(IN_TEXT)) {
			int pointCoalesce = getLastBracketIndex(condition, condition.indexOf(COALESCE_TEXT));
			int pointIn = getLastBracketIndex(condition, condition.indexOf(IN_TEXT));

			{
				if (condition.matches(" ?COALESCE\\(.*\\) NOT IN\\(.*")) {
					IN_TEXT = "NOT IN(";
				}
				String allExceptFunc = condition.substring(0, condition.indexOf(COALESCE_TEXT))
						+ condition.substring(pointCoalesce + 1, condition.indexOf(IN_TEXT))
						+ condition.substring(pointIn + 1);
				if (!allExceptFunc.trim().isEmpty()) {
					String[] values = allExceptFunc.split(" ?_ ?");
					for (int i = 0; i < values.length; i++) {
						error = checkValue(values[i]);
						if (error != null) {
							errors.append(error);
							errors.append(NEW_LINE);
							errors.append(NEW_LINE);
							errors.append("-----");
							errors.append(NEW_LINE);
							errors.append(values[i]);
							return errors.toString();
						}

					}
				}
			}

			{
				// check coalesce
				conCleaned = condition.substring(condition.indexOf(COALESCE_TEXT), pointCoalesce + 1);
				error = checkMultiCoalesce(conCleaned, p, COALESCE_TEXT);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
			}

			{
				// check in
				conCleaned = condition.substring(condition.indexOf(IN_TEXT) + IN_TEXT.length(), pointIn);

				error = checkCoalesceOrInFunctionParameters(conCleaned, p, "IN");
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
			}

		} else if (condition.contains(COALESCE_TEXT)) {

			error = checkMultiCoalesce(condition, p, COALESCE_TEXT);
			if (error != null) {
				errors.append(error);
				return errors.toString();
			}

		} else if (condition.contains(IN_TEXT)) {
			int point = getLastBracketIndex(condition, condition.indexOf(IN_TEXT));

			{
				if (condition.matches(".* NOT IN\\(.*")) {
					IN_TEXT = "NOT IN(";
				}
				String allExceptFunc = condition.substring(0, condition.indexOf(IN_TEXT))
						+ condition.substring(point + 1);
				if (!allExceptFunc.trim().isEmpty()) {
					String[] values = allExceptFunc.split(" ?_ ?");
					for (int i = 0; i < values.length; i++) {
						error = checkValue(values[i]);
						if (error != null) {
							errors.append(error);
							return errors.toString();
						}

					}
				}

			}

			conCleaned = condition.substring(condition.indexOf(IN_TEXT) + IN_TEXT.length(), point);
			error = checkCoalesceOrInFunctionParameters(conCleaned, p, "IN");
			if (error != null) {
				errors.append(error);
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				errors.append(conCleaned);
				return errors.toString();
			}
		}

		if (!condition.contains(COALESCE_TEXT) && !condition.contains(IN_TEXT)) {

			Matcher m = p.matcher(condition);
			while (m.find()) {
				String functionName = m.group(1) + "()";
				String parameters = m.group(2);
				error = checkParameters(functionName, parameters);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
			}

		}

		if (condition.contains(DECODE_TEXT)) {
			String temp = condition.replaceAll("DECODE\\(", "123456");
			int decodeCounter = condition.length() - temp.length();
			int startIndex = 0;
			for (int j = 0; j < decodeCounter; j++) {
				int point = getLastBracketIndex(condition, condition.indexOf(DECODE_TEXT, startIndex));
				conCleaned = condition.substring(condition.indexOf(DECODE_TEXT, startIndex) + DECODE_TEXT.length(),
						point);
				error = checkDecodeFunctionParameters(conCleaned, p);
				if (error != null) {
					errors.append(error);
					errors.append(NEW_LINE);
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(conCleaned);
					return errors.toString();
				}

				startIndex = point;
			}
		}

		return null;
	}

	private static String checkMultiCoalesce(String condition, Pattern p, final String COALESCE_TEXT) {
		StringBuilder errors = new StringBuilder();
		String error;
		String conCleaned;
		// count external COALESCE (for COALESCE in COALESCE check)
		int coalesceCounter = 0;
		{
			int tempIndex = 0;
			int tempLen = condition.length();
			while (tempIndex != -1 && tempIndex < tempLen) {
				tempIndex = getLastBracketIndex(condition, condition.indexOf(COALESCE_TEXT, tempIndex));
				if (tempIndex != -1) {
					coalesceCounter++;
				}
			}
		}

		int startIndex = 0;
		StringBuilder sbAllExceptFunc = new StringBuilder();
		// all before coalesce
		sbAllExceptFunc.append(condition.substring(0, condition.indexOf(COALESCE_TEXT)));
		for (int j = 0; j < coalesceCounter; j++) {
			int point = getLastBracketIndex(condition, condition.indexOf(COALESCE_TEXT, startIndex));

			conCleaned = condition.substring(condition.indexOf(COALESCE_TEXT, startIndex) + COALESCE_TEXT.length(),
					point);
			error = checkCoalesceOrInFunctionParameters(conCleaned, p, "COALESCE");
			if (error != null) {
				errors.append(error);
				return errors.toString();
			}

			startIndex = point;

			// insert COALESCE("") without parameters
			sbAllExceptFunc.append("COALESCE(\"\")");

			// all after coalesce till the next coalesce or end of the
			// string
			if (j + 1 < coalesceCounter) {
				sbAllExceptFunc.append(condition.substring(point + 1, condition.indexOf(COALESCE_TEXT, startIndex)));
			} else {
				if (point + 1 < condition.length()) {
					sbAllExceptFunc.append(condition.substring(point + 1));
				}
			}
		}

		// check all expect coalesce
		String allExceptFunc = sbAllExceptFunc.toString();
		if (!allExceptFunc.trim().isEmpty()) {
			String[] values = allExceptFunc.split(" ?_ ?");
			for (int i = 0; i < values.length; i++) {
				error = checkValue(values[i]);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}

			}
		}

		return null;
	}

	private static String checkCoalesceOrInFunctionParameters(String conCleaned, Pattern p, String functionText) {
		StringBuilder errors = new StringBuilder();
		String error = null;
		if (conCleaned.trim().isEmpty()) {
			errors.append("Value in the " + functionText + " shouldn't be empty");
			return errors.toString();
		}
		if (conCleaned.matches(".*\\, ?$")) {
			errors.append("Value in the " + functionText + " shouldn't be finished with COMMA");
			return errors.toString();
		}
		// check values and parameters
		String[] values = correctSplitByComma(conCleaned);
		for (int i = 0; i < values.length; i++) {
			if (values[i].matches(".*\\_ ?$")) {
				errors.append("Value in the " + functionText + " shouldn't be finished with UNDERSCORE");
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				errors.append(values[i]);
				return errors.toString();
			}
			String[] valuesSplitedByUnderscore = correctSplitByUnderscore(values[i]);

			for (int j = 0; j < valuesSplitedByUnderscore.length; j++) {
				String value = valuesSplitedByUnderscore[j];
				if (value.trim().isEmpty()) {
					errors.append("Value in the " + functionText + " shouldn't be empty");
					return errors.toString();
				}

				// add COALESCE in COALESCE check
				if (value.contains(functionText)) {
					error = checkParametersInCondition(value);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(value);
						return errors.toString();
					}

				} else {

					error = checkValue(value);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(value);
						return errors.toString();
					}

					Matcher m = p.matcher(value);
					while (m.find()) {
						String functionName = m.group(1) + "()";
						String parameters = m.group(2);
						error = checkParameters(functionName, parameters);
						if (error != null) {
							errors.append(error);
							return errors.toString();
						}
					}

				}
			}

		}
		return null;
	}

	private static String checkDecodeFunctionParameters(String conCleaned, Pattern p) {
		// !!!!!! its not the same as checkCoalesceAndInFunctionParameters
		String functionText = "DECODE";

		StringBuilder errors = new StringBuilder();
		String error = null;
		if (conCleaned.trim().isEmpty()) {
			errors.append("Value in the " + functionText + " shouldn't be empty");
			return errors.toString();
		}
		if (conCleaned.contains(",")) {
			errors.append(functionText + " should have only 1 parameter");

			return errors.toString();
		}
		// check values and parameters
		if (conCleaned.matches(".*\\_ ?$")) {
			errors.append("Value in the " + functionText + " shouldn't be finished with UNDERSCORE");
			errors.append(NEW_LINE);
			errors.append(NEW_LINE);
			errors.append("-----");
			errors.append(NEW_LINE);
			errors.append(conCleaned);
			return errors.toString();
		}
		String[] valuesSplitedByUnderscore = conCleaned.split(" ?_ ?");
		for (int j = 0; j < valuesSplitedByUnderscore.length; j++) {
			if (valuesSplitedByUnderscore[j].trim().isEmpty()) {
				errors.append("Value in the " + functionText + " shouldn't be empty");
				return errors.toString();
			}
			error = checkValue(valuesSplitedByUnderscore[j]);
			if (error != null) {
				errors.append(error);
				return errors.toString();
			}

			Matcher m = p.matcher(valuesSplitedByUnderscore[j]);
			while (m.find()) {
				String functionName = m.group(1) + "()";
				String parameters = m.group(2);
				error = checkParameters(functionName, parameters);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
			}
		}

		return null;
	}

	private static String checkParameters(String functionName, String parameters) {
		// Replace("before", "after")
		// functionName = Replace
		// parameters = "\"before\", \"after\""
		
		StringBuilder errors = new StringBuilder();
		
		if (parameters.matches(".*\\, ?$")) {
			errors.append("Parameters in the '" + functionName + "' function shouldn't be finished with COMMA");
			return errors.toString();
		}
		
		if (parameters.matches(".*\\, ?\\,.*")) {
			errors.append("Parameters in the '" + functionName + "' function shouldn't be empty");
			return errors.toString();
		}
		
		String[] params = parameters.split(" ?, ?");

		int paramsQty = 0;
		if (!parameters.trim().isEmpty()) {
			paramsQty = params.length;
		}

		for (FunctionWithParameters func : functionsWithParams) {

			if (func.getName().equals(functionName)) {

				if (func.isParamsQtyValid(paramsQty)) {

					for (int i = 0; i < params.length; i++) {

						if (!params[i].isEmpty() && !func.isTypeValid(params[i].trim(), i)) {
							errors.append("Function '");
							errors.append(func.getName());
							errors.append("' has incorrect parameters. '");
							errors.append(params[i].trim());
							errors.append("' shouldn't be on position #");
							errors.append((i + 1));
							return errors.toString();
						}
					}

					// if all parameters are valid
					return null;

				} else {
					errors.append("'");
					errors.append(functionName);
					errors.append("' shouldn't have '");
					errors.append(paramsQty);
					errors.append("' parameters");
					return errors.toString();
				}
			}
		}

		if (!parameters.isEmpty()) {
			errors.append("'");
			errors.append(functionName);
			errors.append("' shouldn't have parameters");
			return errors.toString();
		}

		return null;
	}

	private static boolean isParenthesisMatch(String str) {

		Stack<Character> stack = new Stack<Character>();

		char c;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			if (c == '(')
				stack.push(c);
			else if (c == '[')
				stack.push(c);
			else if (c == ')')
				if (stack.empty())
					return false;
				else if (stack.peek() == '(')
					stack.pop();
				else
					return false;
			else if (c == ']')
				if (stack.empty())
					return false;
				else if (stack.peek() == '[')
					stack.pop();
				else
					return false;
		}
		return stack.empty();
	}

	private static int getLastBracketIndex(String str, int startIndex) {

		if (startIndex < 0) {
			return -1;
		}

		Stack<Character> stack = new Stack<Character>();

		char c;
		for (int i = startIndex; i < str.length(); i++) {

			c = str.charAt(i);

			if (c == '(')
				stack.push(c);
			else if (c == ')')
				if (stack.empty())
					return -1;
				else if (stack.size() == 1 && stack.peek() == '(')
					return i;
				else if (stack.peek() == '(')
					stack.pop();
				else
					return -1;

		}

		return -1;
	}

	private static String checkReturnValue(String returnValue) {
		StringBuilder errors = new StringBuilder();
		String error = null;

		// check for THROW
		if (returnValue.toUpperCase().contains("THROW")) {
			String returnValueCleaned = returnValue;

			final String COALESCE_TEXT = "COALESCE(";
			if (returnValue.contains(COALESCE_TEXT)) {
				// erase content surrounded by brackets
				int point = getLastBracketIndex(returnValue, returnValue.indexOf(COALESCE_TEXT));
				returnValueCleaned = returnValue.substring(returnValue.indexOf(COALESCE_TEXT) + COALESCE_TEXT.length(),
						point);

				String regex = "\\.(\\w+) ?\\((.*?)\\)";
				Pattern p = Pattern.compile(regex);
				error = checkCoalesceOrInFunctionParameters(returnValueCleaned, p, "COALESCE");
				if (error != null) {
					if (!error.toUpperCase().contains("THROW")) {
						errors.append(error);
						return errors.toString();
					}
				}
				String[] values = correctSplitByComma(returnValueCleaned);
				if (values.length > 0) {

					if (!values[values.length - 1].toUpperCase().contains("THROW")) {
						errors.append("You should place Throw as the last element of Coalesce function");
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(values[values.length - 1]);
						return errors.toString();
					}
					error = checkThrowCorrectness(values[values.length - 1]);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(values[values.length - 1]);
						return errors.toString();
					}
				}

			} else {
				error = checkThrowCorrectness(returnValueCleaned);
				if (error != null) {
					errors.append(error);
					errors.append(NEW_LINE);
					errors.append(NEW_LINE);
					errors.append("-----");
					errors.append(NEW_LINE);
					errors.append(returnValueCleaned);
					return errors.toString();
				}
			}

		} else {

			final String COALESCE_TEXT = "COALESCE(";
			final String DECODE_TEXT = "DECODE(";

			if (!returnValue.contains(COALESCE_TEXT) && !returnValue.contains(DECODE_TEXT)) {
				// check values

				// erase brackets, but left Match(number) cause it should return
				// PdmMultivalueAttribute instead of PdmAttributeSet
				String returnValueCleaned = eraseBrackets(returnValue);

				String[] values = returnValueCleaned.split(" ?_ ?");
				for (int i = 0; i < values.length; i++) {
					error = checkValue(values[i]);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(values[i]);
						return errors.toString();
					}

				}
			}

			// check parameters
			error = checkParametersInCondition(returnValue);
			if (error != null) {
				errors.append(error);
				return errors.toString();
			}
		}
		return null;
	}

	private static String checkThrowCorrectness(String returnValue) {
		StringBuilder errors = new StringBuilder();
		String error = null;
		// erase brackets, but left Match(number) cause it should return
		// PdmMultivalueAttribute instead of PdmAttributeSet
		String returnValueCleaned = eraseBrackets(returnValue);

		
		// check throw structure
		{
			boolean isThrowError = false;
			switch (returnValueCleaned.trim()) {
			case "THROW":
			case "throw new NoSuitableValueException()":
			case "throw new MissingProductDataException()":
			case "throw new OutOfScopeException()":
				break;

			default:
				isThrowError = true;
				break;
			}

			if (isThrowError) {
				error = "You shoudn't throw like this: '" + returnValueCleaned.trim() + "'" + NEW_LINE + NEW_LINE
						+ "Valid stucture:" + NEW_LINE + "1) 'THROW'" + NEW_LINE + "2) 'throw new CorrectClassName()'";
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
			}

		}

		// check throw parameters
		{
			String regex = "\\((.*?)\\)";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(returnValue);
			while (m.find()) {
				String parameters = m.group(1);

				// check if text
				boolean isString = parameters.matches("\".*\"");
				// check if number
//				boolean isDouble = parameters.matches("(\\d+)?\\.\\d+");
				boolean isDouble = isDouble(parameters);
				boolean isInteger = parameters.matches("[+-]?\\d+");

				if (!parameters.isEmpty() && !isString && !isDouble && !isInteger) {

					error = "Incorrect parameter's type while throwing.\n"
							+ "You should use String, Integer or Empty parameter";
					if (error != null) {
						errors.append(error);
						return errors.toString();
					}
				}
			}
		}

		return null;
	}

	private static String checkWhenStatement(String value) {
		// this method special for IN function.
		// By default we are using "checkReturnValue" function, but we shouldn't
		// add there "IN" function

		StringBuilder errors = new StringBuilder();
		String error = null;

		// check brackets
		if (!matchBrackets(value)) {
			errors.append("Bracket is not closed/opened");
			return errors.toString();
		}
		// check brackets matching
		if (!isParenthesisMatch(value)) {
			errors.append("Bracket is not using prorely");
			return errors.toString();
		}

		// value cant finished with "_" or "_ "
		if (value.matches(".*_ ?$")) {
			errors.append("Value shouldn't be finished with UNDERSCORE");
			return errors.toString();
		}

		final String COALESCE_TEXT = "COALESCE(";
		final String IN_TEXT = "IN(";

		if (!value.contains(COALESCE_TEXT) && !value.contains(IN_TEXT)) {
			// check values

			// erase brackets, but left Match(number) cause it should return
			// PdmMultivalueAttribute instead of PdmAttributeSet
			String returnValueCleaned = eraseBrackets(value);

			String[] values = returnValueCleaned.split(" ?_ ?");
			for (int i = 0; i < values.length; i++) {
				if (!values[i].trim().matches("IS (NOT )?NULL")) {
					error = checkValue(values[i]);
					if (error != null) {
						errors.append(error);
						errors.append(NEW_LINE);
						errors.append(NEW_LINE);
						errors.append("-----");
						errors.append(NEW_LINE);
						errors.append(values[i]);
						return errors.toString();
					}
				}

			}
		}

		// check parameters
		error = checkParametersInCondition(value);
		if (error != null) {
			errors.append(error);
			return errors.toString();
		}

		return null;
	}

	private static String checkCaseStatement(String caseStatement) {
		StringBuilder errors = new StringBuilder();
		String error = null;
		String structure = NEW_LINE + NEW_LINE + "Valid structure:" + NEW_LINE + "CASE value" + NEW_LINE
				+ "[ WHEN value THEN returnValue ]" + NEW_LINE + "[ ELSE returnValue ]" + NEW_LINE + "END";

		boolean isCaseStatementValid = caseStatement
				.matches("(?i)^ ?CASE .*?( WHEN .*? THEN .*?)+?( ELSE .*?)?( END)?");

		if (!isCaseStatementValid) {
			errors.append("Invalid CASE statements. ");
			errors.append(structure);
			errors.append(KEYWORD_NOTE);
			return errors.toString();
		}

		// check values
		String regex = "(?i)(CASE | WHEN | THEN | ELSE )(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(caseStatement.replaceAll(" WHEN ", "\n WHEN ").replaceAll(" THEN ", "\n THEN ")
				.replaceAll(" ELSE ", "\n ELSE ").replaceAll(" END", "\nEND"));
		while (m.find()) {

			String returnValue = m.group(2);
			String stmnt = m.group(1);

			// CASE can't contains UNDERSCORE
			if (stmnt.trim().toUpperCase().contains("CASE") && returnValue.contains("_")
					&& !returnValue.contains("COALESCE(")) {
				errors.append("CASE condition shouldn't contains UNDERSCORE");
				errors.append(NEW_LINE);
				errors.append(NEW_LINE);
				errors.append("-----");
				errors.append(NEW_LINE);
				errors.append(returnValue);
				return errors.toString();
			}

			// check if WHEN statement
			if (stmnt.trim().toUpperCase().contains("WHEN")) {
				error = checkWhenStatement(returnValue);
			} else {
				error = checkReturnValue(returnValue);
			}

			if (error != null) {
				errors.append(error);
				return errors.toString();
			}

		}

		return null;
	}

	public static String[] correctSplitByComma(String str) {
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

	private static String[] correctSplitByUnderscore(String str) {
		if (str.trim().isEmpty()) {
			return new String[] { "" };
		}

		str = str.replaceAll(" ?_ ?", "_");

		List<String> list = new ArrayList<>();
		String temp = null;
		int start = 0;
		int startWithOld = 0;
		int counter = 0;
		int point = 0;
		while (start < str.length() && point != -1) {
			point = str.indexOf("_", start);

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

	private static Integer[] getMatches(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		// Check all occurrences
		List<Integer> list = new ArrayList<>();
		while (matcher.find()) {
			int lineNumber = 0;
			try {
				int startIndex = matcher.start();
				int endIndex = matcher.end();
				lineNumber = taExpr.getLineOfOffset(startIndex);
				SquigglePainter red = new SquigglePainter(Color.RED);
				try {
					highlighterTags.add(taExpr.getHighlighter().addHighlight(startIndex, endIndex, red));
					taExpr.repaint();
				} catch (BadLocationException e) {
					logger.error(e.getMessage());
				}

			} catch (BadLocationException e) {
				logger.error(e.getMessage());
			}

			list.add(lineNumber + 1);
		}

		return list.toArray(new Integer[list.size()]);
	}

	private static String getLines(String text, String regex) {
		StringBuilder sb = new StringBuilder();
		Integer[] arr = getMatches(text, regex);

		for (int i = 0; i < arr.length; i++) {
			if (i == 0) {
				sb.append("Lines: ");
				sb.append(arr[i]);
			} else {
				sb.append(",");
				sb.append(arr[i]);
			}
			if (i + 1 == arr.length) {
				sb.append(NEW_LINE);
			}
		}

		return sb.toString();
	}

	public static String checkDeactivatedAttributes(String expr) {
		String exprCleaned = expr;

		// erase comments. will erase until line feed
		exprCleaned = exprCleaned.replaceAll("--.*", "");

		// erase text surrounded by quotes
		exprCleaned = exprCleaned.replaceAll("(?s)\".*?\"", "\"\"").replaceAll("(\"\")+", "\"\"");

		// create set with attributes to check
		Set<String> set = new LinkedHashSet<>();
		String regex = "A\\s*?\\[\\s*?(\\d+?)\\s*?\\]";
		{
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(exprCleaned);

			while (m.find()) {
				set.add(m.group(1));
			}
		}
		// search for attributes in the Match function
		{
			regex = "Match\\s*?\\((?:\\s*?(\\d+)\\s*?,?\\s*?)*?\\)";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(exprCleaned);
			while (m.find()) {

				String regex2 = "(\\d+)";
				Pattern p2 = Pattern.compile(regex2);
				Matcher m2 = p2.matcher(m.group());

				while (m2.find()) {
					set.add(m2.group(1));
				}
			}

		}

		// create list with deactivated attributes
		List<String> list = new ArrayList<>();
		for (String strAttribute : set) {
			int attrId = Integer.parseInt(strAttribute);
			List<Attribute> attibutes = TChecker.getAttributes();
			for (Attribute attr : attibutes) {
				if (attr.getId() == attrId && attr.isDeactivated()) {
					list.add(strAttribute);
				}
			}
		}

		if (list.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Note! You are using deactivated attribute(s): ");
			int len = list.size();
			for (int i = 0; i < len; i++) {
				sb.append(list.get(i));

				if ((i + 1) < len) {
					sb.append(", ");
				}
			}

			return sb.toString();
		}

		return null;

	}
	
	public static String eraseBrackets(String expr){
		// erase brackets, but left Match(number) cause it should return PdmMultivalueAttribute instead of PdmAttributeSet
		String exprCleaned = expr.replaceAll("(?<!Match)\\(.*?\\)", "()").replaceAll("Match\\(\\s*?\\d+\\s*?,.*?\\)", "Match()");
		return exprCleaned;
	}
	
	private static boolean isDouble(String text){
		final String Digits = "(\\p{Digit}+)";
		final String Exp = "[eE][+-]?" + Digits;
		final String fpRegex    =
			"[+-]?(" + // Optional sign character
			
			// Digits ._opt Digits_opt ExponentPart FloatTypeSuffix_opt
			"("+Digits+"(\\.)?("+Digits+"?)("+Exp+"))|"+
			
			// . Digits ExponentPart_opt FloatTypeSuffix_opt
			"(("+Digits+"?)\\.("+Digits+")("+Exp+")?)"+		
			
			")";
		
		return text.matches(fpRegex);
	}
}
