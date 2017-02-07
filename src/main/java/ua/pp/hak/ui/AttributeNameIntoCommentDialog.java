package ua.pp.hak.ui;

import java.util.LinkedHashMap;
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
					"This function will insert attribute\n" + "names into comments before first use.\n"
							+ "Continue?",
					expressionAttrNameIntoComment, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				JTextArea ta = npd.getExprTextArea();

				Document doc = ta.getDocument();
				System.out.println("----------");
				System.out.println("Lines = " + ta.getLineCount());

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
							System.out.println(m.group(1) + ", " + i);
							lhm.put(m.group(1), i);
						}
					}
				}

				int delta = 0;

				for (Map.Entry<String, Integer> entry : lhm.entrySet()) {
					boolean isCommentPresented = false;
					String attrCode = entry.getKey();
					int attrLine = entry.getValue();

					int indexOfLineStart = 0;
					String[] blaLines = ta.getText().split("\n");
					for (int i = 0; i < attrLine + delta; i++) {

						String rgxForLine = ".*?--.*?\\b" + attrCode + "\\b.*";
						if (blaLines[i].matches(rgxForLine)) {
							isCommentPresented = true;
							break;
						}
						indexOfLineStart += blaLines[i].length() + 1;
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
							doc.insertString(indexOfLineStart, comment + '\n', null);
							delta++;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
