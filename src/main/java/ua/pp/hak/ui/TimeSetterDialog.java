package ua.pp.hak.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class TimeSetterDialog {

	private JTextField tfLoadTime;
	private JTextField tfParseTime;
	private JComponent parent;
	private int loadTime = 3;
	private int parseTime = 15;

	public int[] setTime(JComponent parent) {
		this.parent = parent;
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		main.add(createTimeToWaitPanel());

		boolean inputAccepted = false;
		while (!inputAccepted) {

			int result = JOptionPane.showConfirmDialog(parent, main, "Set time to wait", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				if (attemptToGetTime()) {
					return new int[] { loadTime, parseTime };
				}
			} else {
				inputAccepted = true;
			}
		}

		return null;
	}

	private boolean attemptToGetTime() {

		try {

			loadTime = Integer.parseInt(tfLoadTime.getText());
			parseTime = Integer.parseInt(tfParseTime.getText());

			if (loadTime < 1 || loadTime > 99) {
				loadTime = -1;
				throw new NumberFormatException();
			}
			if (parseTime < 1 || parseTime > 99) {
				parseTime = -1;
				throw new NumberFormatException();
			}

		} catch (NumberFormatException nfe) {
			displayInvalidLineNumberMessage();

			return false;
		}

		return true;

	}

	private void displayInvalidLineNumberMessage() {
		JOptionPane.showMessageDialog(parent, "Please enter an integer between 1 and 99.", "Error",
				JOptionPane.ERROR_MESSAGE);

	}

	private JPanel createTimeToWaitPanel() {
		tfLoadTime = new JTextField("3");
		tfParseTime = new JTextField("15");
		((PlainDocument) tfLoadTime.getDocument()).setDocumentFilter(new MyIntFilter(2));
		((PlainDocument) tfParseTime.getDocument()).setDocumentFilter(new MyIntFilter(2));

		JPanel timeToWaitPanel = new JPanel();
		timeToWaitPanel.setLayout(new SpringLayout());
		timeToWaitPanel.setBorder(BorderFactory.createTitledBorder("Time to wait (seconds): "));

		JLabel loadingLabel = new JLabel("Load time: ");
		JLabel prasingLabel = new JLabel("Parse time: ");

		timeToWaitPanel.add(loadingLabel);
		timeToWaitPanel.add(tfLoadTime);
		// timeToWaitPanel.add(new JLabel(" "));
		timeToWaitPanel.add(prasingLabel);
		timeToWaitPanel.add(tfParseTime);

		SpringUtilities.makeCompactGrid(timeToWaitPanel, 2, 2, 5, 5, 3, 3);
		timeToWaitPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		return timeToWaitPanel;
	}

	/**
	 * A document filter that only lets the user enter digits with limit.
	 */
	class MyIntFilter extends DocumentFilter {
		private int limit;

		MyIntFilter(int limit) {
			super();
			this.limit = limit;
		}

		MyIntFilter() {
			super();
			this.limit = 100;
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString()) && (sb.toString().length()) <= limit) {
				super.insertString(fb, offset, string, attr);
			} else {
				// warn the user and don't allow the insert
			}
		}

		private boolean test(String text) {
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString()) && (sb.toString().length()) <= limit) {
				super.replace(fb, offset, length, text, attrs);
			} else {
				// warn the user and don't allow the insert
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (sb.toString().isEmpty() || test(sb.toString())) {
				super.remove(fb, offset, length);
			} else {
				// warn the user and don't allow the insert
			}

		}
	}

}
