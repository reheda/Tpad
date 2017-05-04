package ua.pp.hak.autocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;

import ua.pp.hak.compiler.TChecker;

/**
 * The completion provider used for Templex source code.
 *
 * @author VR
 * @version 1.0
 */
public class TemplexSourceCompletionProvider extends DefaultCompletionProvider {

	final static Logger logger = LogManager.getLogger(TemplexSourceCompletionProvider.class);

	public TemplexSourceCompletionProvider() {
		setAutoActivationRules(false, "."); // Default - only activate after '.'
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		List<Completion> retVal = new ArrayList<Completion>();
		String text = getAlreadyEnteredText(comp);

		// get return type of the expression and fill in possibleCompleteion
		// list
		List<Completion> possibleCompletions = new ArrayList<>();
		int indexBeforeEnteredText = comp.getCaretPosition() - text.length();
		if (indexBeforeEnteredText > -1) {
			String allText = comp.getText().substring(0, indexBeforeEnteredText);

			try {
				String returnType = getReturnType(allText);

				for (Completion completion : completions) {
					String summary = completion.getSummary();
					int ind = summary.indexOf("Defined in: <em>") + 16;
					if (ind > -1) {
						String definedIf = summary.substring(ind, summary.length() - 5);
						if (definedIf.equals(returnType)) {
							possibleCompletions.add(completion);
						}
					}
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}

		// use default list if no suitable completions
		if (possibleCompletions.isEmpty()) {
			possibleCompletions = completions;
		}

		// filter completions by already entered text
		if (text != null) {

			int index = Collections.binarySearch(possibleCompletions, text, comparator);
			if (index < 0) { // No exact match
				index = -index - 1;
			} else {
				// If there are several overloads for the function being
				// completed, Collections.binarySearch() will return the index
				// of one of those overloads, but we must return all of them,
				// so search backward until we find the first one.
				int pos = index - 1;
				while (pos > 0 && comparator.compare(possibleCompletions.get(pos), text) == 0) {
					retVal.add(possibleCompletions.get(pos));
					pos--;
				}
			}

			while (index < possibleCompletions.size()) {
				Completion c = possibleCompletions.get(index);
				if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
					retVal.add(c);
					index++;
				} else {
					break;
				}
			}

		}

		return retVal;

	}

	private String getReturnType(String allText) {
		String[] values = allText
				//erase comments
				.replaceAll("--.*", "")
				//erase multi spaces
				.replaceAll("\\s+", " ")
				// erase text surrounded by quotes
				.replaceAll("(?s)\".*?\"", "\"\"").replaceAll("(\"\")+", "\"\"")
				.split("(?i)_|;|THEN |ELSE |IF |WHEN |CASE |AND |OR ");

		if (values.length == 0) {
			return null;
		}

		String exprCleaned = values[values.length - 1].trim();

		// erase text surrounded by quotes
		exprCleaned = exprCleaned.replaceAll("(?s)\".*?\"", "\"\"").replaceAll("(\"\")+", "\"\"");

		// clean expression to make it parsable
		exprCleaned = exprCleaned.replaceAll("\\n+", " ").replaceAll("\\s+", " ").replaceAll(" ?\\. ?", ".")
				.replaceAll(" ?\\[ ?", "[").replaceAll(" ?\\] ?\\.", "].").replaceAll(" ?\\( ?", "(")
				.replaceAll(" ?\\) ?\\.", ").");

		int exprCleanedLen = exprCleaned.length();
		if (exprCleanedLen > 1 && exprCleaned.charAt(exprCleanedLen - 1) == '.'
				&& exprCleaned.charAt(exprCleanedLen - 2) != '.') {
			// remove dot in the end
			exprCleaned = exprCleaned.substring(0, exprCleanedLen - 1);
		} else {
			exprCleaned = "";
		}

		exprCleaned = exprCleaned.replaceAll("(?i)coalesce", "COALESCE");
		exprCleaned = exprCleaned.replaceAll("(?i)in\\(", "IN(");

		final String COALESCE_TEXT = "COALESCE(";
		final String IN_TEXT = "IN(";
		final String DECODE_TEXT = "DECODE(";

		if (exprCleaned.contains(IN_TEXT)) {

			exprCleaned = exprCleaned.substring(exprCleaned.indexOf(IN_TEXT) + IN_TEXT.length());
			if (!exprCleaned.isEmpty() && exprCleaned.charAt(exprCleaned.length() - 1) == ',') {
				exprCleaned = "";
			}
			if (!exprCleaned.isEmpty()) {
				String[] valuesSplitWithComma = TChecker.correctSplitByComma(exprCleaned);
				if (valuesSplitWithComma.length > 0) {
					exprCleaned = valuesSplitWithComma[valuesSplitWithComma.length - 1];
				}
			}
		} else if (exprCleaned.contains(COALESCE_TEXT)) {
			exprCleaned = exprCleaned.substring(exprCleaned.lastIndexOf(COALESCE_TEXT) + COALESCE_TEXT.length());
			if (!exprCleaned.isEmpty() && exprCleaned.charAt(exprCleaned.length() - 1) == ',') {
				exprCleaned = "";
			}
			if (!exprCleaned.isEmpty()) {
				String[] valuesSplitWithComma = TChecker.correctSplitByComma(exprCleaned);
				if (valuesSplitWithComma.length > 0) {
					exprCleaned = valuesSplitWithComma[valuesSplitWithComma.length - 1];
				}
			}
		} else if (exprCleaned.contains(DECODE_TEXT)) {

			exprCleaned = exprCleaned.substring(exprCleaned.indexOf(DECODE_TEXT) + DECODE_TEXT.length());
		}

		// erase brackets, but left Match(number) cause it should return
		// PdmMultivalueAttribute instead of PdmAttributeSet
		exprCleaned = TChecker.eraseBrackets(exprCleaned);

		// ither cases
		exprCleaned = exprCleaned.replaceAll("BulletFeatures\\[\\d+\\]", "BulletFeatures[]").replaceAll("Ksp\\[\\d+\\]",
				"Ksp[]");

		String[] functns = exprCleaned.split("\\.");

		String previousType = null;

		for (int i = 0; i < functns.length; i++) {
			// check if pdm attribute or not
			if (functns[i].matches("A\\[\\d+\\]")) {
				int attr = Integer.parseInt(functns[i].replaceAll("[^0-9]", ""));
				String attrType = TChecker.getAttributeType(attr);
				if (attrType != null) {
					previousType = TChecker.getPdmReturnType(attrType);
				}
			} else {

				previousType = TChecker.getFunctionReturnType(functns[i], previousType);

			}

		}

		return previousType;
	}

}