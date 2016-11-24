package ua.pp.hak.compiler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import ua.pp.hak.ui.Notepad;
import ua.pp.hak.ui.SquigglePainter;

public class TChecker {

	static HashMap<Integer, String> hm;
	static ArrayList<Object> highlighterTags = new ArrayList<>();

	final String TYPE_SIMPLE = "Simple";
	final String TYPE_MULTI_VALUED = "Multi-valued";
	final String TYPE_SIMPLE_NUMERIC = "Simple numeric";
	final String TYPE_REPEATING = "Repeating";
	final String TYPE_REPEATING_NUMERIC = "Repeating numeric";
	final String TYPE_MULTI_VALUED_NUMERIC = "Multi-valued numeric";

	public TChecker(Notepad npd) {
		hm = StAXParser.parse();
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

	String getAttributeType(int attr) {
		return hm.get(attr);
	}

	public static void check(Notepad npd) {
		RSyntaxTextArea taExpr = npd.getExprTextArea();
		JTextArea taExprRes = npd.getExprResTextArea();
		String textExpr = taExpr.getText();
		String textExprRes = taExprRes.getText();

		Color green = new Color(0, 188, 57);
		Color red = new Color(189, 0, 0);

		boolean isWholeExpressionValid = true;

		if (!isValidQuotes(taExpr, textExpr)) {
			taExprRes.setText("Quotes error found!");
			isWholeExpressionValid = false;
		}

		Color color = null;
		if (isWholeExpressionValid) {
			color = green;
			taExprRes.setText("Expression is valid!");
		} else {
			color = red;
		}

		// set color of the expression result text area
		taExprRes.setBorder(
				new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, color), new EmptyBorder(2, 5, 2, 0)));

	}

	static boolean isValidQuotes(RSyntaxTextArea taExpr, String textExpr) {
		int charsBeforeTheCurrentLine = 0;
		if (!matchQuotes(textExpr)) {
			eraseAllHighlighter(taExpr);
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
		} else {

			eraseAllHighlighter(taExpr);
			return true;
		}
	}

	private static void eraseAllHighlighter(RSyntaxTextArea taExpr) {
		for (Object tag : highlighterTags) {
			taExpr.getHighlighter().removeHighlight(tag);
		}
		highlighterTags.clear();
		taExpr.repaint();
	}

}
