package ua.pp.hak.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.util.Attribute;

public class AttributeNameIntoCommentDialog implements MenuConstants {
	final static Logger logger = LogManager.getLogger(AttributeNameIntoCommentDialog.class);

	public static void show(Notepad npd) {
		try {
			int result = JOptionPane.showConfirmDialog(npd.getFrame(),
					"This function will insert attribute\n" + "names into comments before first use.\n" + "Continue?",
					expressionAttrNameIntoComment, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {

				JTextArea ta = npd.getExprTextArea();

				Document doc = ta.getDocument();

				// create map(attribute,line)
				LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>();
				String regex = "A\\s*?\\[\\s*?(\\d+?)\\s*?\\]";
				String expr = ta.getText();
				String[] lines = expr.split("\n");
				for (int i = 0; i < lines.length; i++) {
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(lines[i].replaceAll("--.*", ""));

					while (m.find()) {
						if (!lhm.containsKey(m.group(1))) {
							lhm.put(m.group(1), i);
						}
					}
				}

				// search for attributes in the Match function
				{
					regex = "Match\\s*?\\((?:\\s*?(\\d+)\\s*?,?\\s*?)*?\\)";
					for (int i = 0; i < lines.length; i++) {
						Pattern p = Pattern.compile(regex);
						Matcher m = p.matcher(lines[i].replaceAll("--.*", ""));
						while (m.find()) {

							String regex2 = "(\\d+)";
							Pattern p2 = Pattern.compile(regex2);
							Matcher m2 = p2.matcher(m.group());

							while (m2.find()) {
								String parsedValue = m2.group(1);
								if (!lhm.containsKey(parsedValue)) {
									lhm.put(m2.group(1), i);
								} else if (lhm.containsKey(parsedValue) && i < lhm.get(parsedValue)) {
									lhm.put(m2.group(1), i);
								}
							}
						}
					}

					// sort by line for insertion before first use
					lhm = (LinkedHashMap<String, Integer>) MapUtil.sortByValue(lhm);
				}

				logger.info("Start comments insertion...");
				int delta = 0;
				for (Map.Entry<String, Integer> entry : lhm.entrySet()) {
					boolean isCommentPresented = false;
					String attrCode = entry.getKey();
					int attrLine = entry.getValue();

					int indexOfLineStart = 0;
					String[] updatedLines = ta.getText().split("\n");
					for (int i = 0; i < attrLine + delta; i++) {

						String rgxForLine = ".*?--.*?\\b" + attrCode + "\\b.*";
						if (updatedLines[i].matches(rgxForLine)) {
							isCommentPresented = true;
							break;
						}
						indexOfLineStart += updatedLines[i].length() + 1;
					}

					if (!isCommentPresented) {
						List<Attribute> attibutes = TChecker.getAttributes();
						String comment = null;
						for (Attribute attr : attibutes) {
							int attrId = Integer.parseInt(attrCode);
							if (attr.getId() == attrId) {
								String name = attr.getName();
								comment = "-- " + attrId + " // " + name.replace("\"", "''");
							}
						}
						if (comment != null) {
							logger.info("Insert comment (line " + (attrLine + delta + 1) + "): " + comment);
							doc.insertString(indexOfLineStart, comment + '\n', null);
							delta++;
						}
					}
				}
				logger.info("Finish comments insertion");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}

class MapUtil {
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
