package ua.pp.hak.compiler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import ua.pp.hak.ui.Notepad;
import ua.pp.hak.ui.SquigglePainter;

public class TChecker {
	static RSyntaxTextArea taExpr;
	static final String NEW_LINE = "\n";

	static List<Attribute> attibutes = StAXParser.parse();
	static ArrayList<Object> highlighterTags = new ArrayList<>();

	final String TYPE_SIMPLE = "Simple";
	final String TYPE_MULTI_VALUED = "Multi-valued";
	final String TYPE_SIMPLE_NUMERIC = "Simple numeric";
	final String TYPE_REPEATING = "Repeating";
	final String TYPE_REPEATING_NUMERIC = "Repeating numeric";
	final String TYPE_MULTI_VALUED_NUMERIC = "Multi-valued numeric";

	private static void eraseAllHighlighter(RSyntaxTextArea taExpr) {
		for (Object tag : highlighterTags) {
			taExpr.getHighlighter().removeHighlight(tag);
		}
		highlighterTags.clear();
		taExpr.repaint();
	}

	String getAttributeType(int attr) {
		for (Attribute attribute : attibutes) {
			if (attribute.getId() == attr) {
				return attribute.getType();
			}
		}
		return null;
	}

	public static void check(Notepad npd) {
		taExpr = npd.getExprTextArea();
		JTextArea taExprRes = npd.getExprResTextArea();
		String textExpr = taExpr.getText();
		StringBuilder sbErrors = new StringBuilder();

		Color green = new Color(0, 188, 57);
		Color red = new Color(189, 0, 0);

		boolean isWholeExpressionValid = true;

		eraseAllHighlighter(taExpr);
		taExprRes.setText("Processing...");

		if (!isCommentsValid(textExpr)) {
			sbErrors.append("Comment shouldn't contains quote!");
			sbErrors.append("\n");
			isWholeExpressionValid = false;
		} else if (!isQuotesValid(textExpr)) {
			sbErrors.append("Quote is not closed!");
			sbErrors.append("\n");
			isWholeExpressionValid = false;
		}

		Color color = null;
		if (isWholeExpressionValid) {
			color = green;
			taExprRes.setText("Expression is valid!");
		} else {
			color = red;
			taExprRes.setText("Errors: \n" + sbErrors.toString());
		}

		// set color of the expression result's text area
		taExprRes.setBorder(
				new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, color), new EmptyBorder(2, 5, 2, 0)));

	}

	static boolean isQuotesValid(String textExpr) {
		int charsBeforeTheCurrentLine = 0;
		if (!matchQuotes(textExpr)) {
			SquigglePainter red = new SquigglePainter(Color.RED);
			int p0 = 0, p1 = 0;
			String[] lines = textExpr.split("\\n");
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
						e.printStackTrace();
					}
				}
				charsBeforeTheCurrentLine += line.length() + 1;
			}

			return false;
		}

		return true;
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

	static boolean isCommentsValid(String textExpr) {
		int charsBeforeTheCurrentLine = 0;
		boolean isValid = true;
		SquigglePainter red = new SquigglePainter(Color.RED);
		int p0 = 0, p1 = 0;
		String[] lines = textExpr.split("\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.contains("--") && line.contains("\"") && line.indexOf("--") < line.lastIndexOf("\"")) {
				isValid = false;
				p0 = line.indexOf("\"");
				p1 = line.length();
				try {
					highlighterTags.add(taExpr.getHighlighter().addHighlight(charsBeforeTheCurrentLine + p0,
							charsBeforeTheCurrentLine + p1, red));
					taExpr.repaint();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			charsBeforeTheCurrentLine += line.length() + 1;
		}

		return isValid;
	}

	static String checkTemplateExpresion(String textExpr) {

		// split by semicolon
		String delimiter = ";";
		String[] expressions = textExpr.split(delimiter);
		int charsBefore = 0;
		int p0 = 0, p1 = 0;
		StringBuilder errors = new StringBuilder();
		for (int i = 0; i < expressions.length; i++) {
			int exprLength = expressions[i].length();

			String error = checkExpression(expressions[i], charsBefore + p0, charsBefore + p1);
			if (error != null) {
				errors.append(error).append(NEW_LINE);
			}

			charsBefore += exprLength + delimiter.length();
		}

		return errors.toString();
	}

	static String checkExpression(String expr, int p0, int p1) {

		// check comments
		// erase comments
		// check quotes
		// erase values surrounded by quotes
		String exprCleaned = expr.replaceAll("\\n+", "\\s").replaceAll("\\s+", "\\s").replaceAll(" \\. ", ".")
				.replaceAll(" ?\\[ ", "[").replaceAll(" ?\\] ", "]").replaceAll(" ?\\( ", "(").replaceAll(" ?\\) ", ")")
				.replaceAll("else if", "elseif");

		// count "else" words. qty should be 1 or 0
		// count "if" words. qty should be 1 or 0
		// check "then" words. should be same quantity as qty("elseif") -1

		exprCleaned.matches("^if .* then .*( elseif .* then .*)* (else .*)?");

		return null;
	}

	static String checkStatement(String expr, int p0, int p1) {
		return null;
	}

	static String checkReturnValue(String expr, int p0, int p1) {
		return null;
	}

	static String checkFunction(String expr, int p0, int p1) {
		return null;
	}

}
