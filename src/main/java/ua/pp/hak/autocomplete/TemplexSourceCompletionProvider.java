package ua.pp.hak.autocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;

import ua.pp.hak.compiler.Function;
import ua.pp.hak.compiler.TChecker;

/**
 * The completion provider used for Templex source code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TemplexSourceCompletionProvider extends DefaultCompletionProvider {

	/**
	 * Constructor.
	 *
	 * @param jarManager
	 *            The jar manager for this provider.
	 */
	public TemplexSourceCompletionProvider() {
		setAutoActivationRules(false, "."); // Default - only activate after '.'
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		List<Completion> tempCompletions = new ArrayList<>(completions);
		List<Completion> retVal = new ArrayList<Completion>();
		String text = getAlreadyEnteredText(comp);
		int lastDotIndex = comp.getCaretPosition() - text.length() - 1;
		if (lastDotIndex > -1) {

			String allText = comp.getText().substring(0, lastDotIndex);
			int previosDotIndex = allText.lastIndexOf(".") + 1;
			if (previosDotIndex < 0) {
				previosDotIndex = allText.lastIndexOf("_") + 1;
			}
			if (previosDotIndex < 0) {
				previosDotIndex = allText.lastIndexOf(";") + 1;
			}
			if (previosDotIndex < 0) {
				int lineStartIndex = 0;
				if (comp instanceof JTextArea) {
					try {
						JTextArea ta = (JTextArea) comp;
						int lineNumber = ta.getLineOfOffset(lastDotIndex);
						lineStartIndex = ta.getLineStartOffset(lineNumber);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				previosDotIndex = lineStartIndex;
			}
			System.out.println(previosDotIndex);
			System.out.println(text);

			ArrayList<Function> functions = TChecker.getFunctions();
			if (functions == null) {
				TChecker.initFunctions();
			}

			System.out.println("---");
			System.out.println(previosDotIndex);
			System.out.println(lastDotIndex);
			System.out.println(allText.substring(previosDotIndex, lastDotIndex).trim());
			String invokedOn = allText.substring(previosDotIndex, lastDotIndex).trim().replaceAll("\\(.*\\)", "()");
			String returnType = TChecker.getFunctionReturnType(invokedOn, "");
			if (returnType == null) {
				if (invokedOn.matches("A\\[\\d+\\]")) {
					final String TYPE_SIMPLE = "Simple";
					final String TYPE_MULTI_VALUED = "Multi-valued";
					final String TYPE_SIMPLE_NUMERIC = "Simple numeric";
					final String TYPE_REPEATING = "Repeating";
					final String TYPE_REPEATING_NUMERIC = "Repeating numeric";
					final String TYPE_MULTI_VALUED_NUMERIC = "Multi-valued numeric";

					int attr = Integer.parseInt(invokedOn.replaceAll("[^0-9]", ""));
					String attrType = TChecker.getAttributeType(attr);
					if (attrType != null) {
						switch (attrType) {
						case TYPE_SIMPLE:
							returnType = "PdmAttribute";
							break;
						case TYPE_SIMPLE_NUMERIC:
							returnType = "PdmAttribute";
							break;
						case TYPE_MULTI_VALUED:
							returnType = "PdmMultivalueAttribute";
							break;
						case TYPE_MULTI_VALUED_NUMERIC:
							returnType = "PdmMultivalueAttribute";
							break;
						case TYPE_REPEATING:
							returnType = "PdmRepeatingAttribute";
							break;
						case TYPE_REPEATING_NUMERIC:
							returnType = "PdmRepeatingAttribute";
							break;
						default:
							break;
						}
					}
				}
			}
			System.out.println(returnType);

			//
			// for (Function function : functions) {
			// TChecker.isFunctionMemberOfValid("","");
			// }

			List<Completion> toRemove = new ArrayList<>();
			System.out.println(tempCompletions.size());
			for (int i = 0; i < tempCompletions.size(); i++) {
				Completion completeion = tempCompletions.get(i);
				String summary = completeion.getSummary();
				int ind = summary.indexOf("Defined in: <em>") + 16;
				if (ind > -1) {
					String definedIf = summary.substring(ind, summary.length() - 5);
					if (!definedIf.equals(returnType)) {
						toRemove.add(completeion);
					}
				}

			}

			if (toRemove.size() != tempCompletions.size()) {

				for (Completion com : toRemove) {
					tempCompletions.remove(com);
				}
			}
		}
		if (text != null && tempCompletions != null) {

			int index = Collections.binarySearch(tempCompletions, text, comparator);
			if (index < 0) { // No exact match
				index = -index - 1;
			} else {
				// If there are several overloads for the function being
				// completed, Collections.binarySearch() will return the index
				// of one of those overloads, but we must return all of them,
				// so search backward until we find the first one.
				int pos = index - 1;
				while (pos > 0 && comparator.compare(tempCompletions.get(pos), text) == 0) {
					retVal.add(tempCompletions.get(pos));
					pos--;
				}
			}

			while (index < tempCompletions.size()) {
				Completion c = tempCompletions.get(index);
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

}