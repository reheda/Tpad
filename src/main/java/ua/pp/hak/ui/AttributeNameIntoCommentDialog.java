package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.util.Attribute;

public class AttributeNameIntoCommentDialog {

	public static void main(String[] args) {
		init();
	}

	public static void insert(Notepad npd) {

	}

	static void init() {
		JTextArea ta = new JTextArea();
		ta.setText("first line with A[890] parameter\n second file with A  [  891     ] param");
		JScrollPane jsp = new JScrollPane(ta);
		JButton btn = new JButton("insert");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					doSmth(ta);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		JFrame frame = new JFrame();
		frame.add(jsp, BorderLayout.CENTER);
		frame.add(btn, BorderLayout.SOUTH);

		frame.setSize(new Dimension(800, 600));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static void doSmth(JTextArea ta) throws BadLocationException {

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
				if (comment!=null){
					doc.insertString(indexOfLineStart, comment + '\n', null);
					delta++;
				}
			}
		}

	}

	static String getDigits(String attr) {
		return attr.replaceAll("[\\D]", "");
	}
}
