package ua.pp.hak.ui;

//package p1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.GroupLayout.Alignment;

public class FindDialog extends JPanel implements ActionListener {
	private JTextArea ta;
	private int lastIndex;
	private JLabel findLabel;
	private JLabel replaceLabel;

	private JTextField findWhat;
	private JTextField replaceWith;

	private JCheckBox matchCase;

	private JRadioButton up, down;

	private JButton findNextButton, replaceButton, replaceAllButton, cancelButton;

	private JPanel direction;

	private boolean ok;
	private JDialog dialog;
	private TitledBorder titledBorder1;

	///////////////////////
	public FindDialog(JTextArea ta) {

		this.ta = ta;
		findWhat = new JTextField(20);
		replaceWith = new JTextField(20);
		findLabel = new JLabel("Find what");
		replaceLabel = new JLabel("Replace with");

		matchCase = new JCheckBox("Match case");

		// Border etched=BorderFactory.createEtchedBorder();
		// Border titled=BorderFactory.createTitledBorder(etched,"Direction");
		titledBorder1 = new TitledBorder("Direction");
		up = new JRadioButton("Up");
		down = new JRadioButton("Down");

		down.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(up);
		bg.add(down);

		direction = new JPanel();
		direction.setBorder(titledBorder1);
		direction.setLayout(new BorderLayout());
		direction.add(up, BorderLayout.LINE_START);
		direction.add(down, BorderLayout.CENTER);

		findNextButton = new JButton("Find Next");
		replaceButton = new JButton("Replace");
		replaceAllButton = new JButton("Replace All");
		cancelButton = new JButton("Cancel");

		// ---------------------------
		GroupLayout layout = new GroupLayout(this);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(findLabel)
						.addComponent(replaceLabel))
				.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(findWhat).addComponent(replaceWith)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(matchCase)
										.addComponent(direction))))
				.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(findNextButton)
						.addComponent(replaceButton).addComponent(replaceAllButton).addComponent(cancelButton))));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(findLabel).addComponent(findWhat)
						.addComponent(findNextButton))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(replaceLabel)
						.addComponent(replaceWith).addComponent(replaceButton))
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addComponent(matchCase).addComponent(direction,
								GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup().addComponent(replaceAllButton)
								.addComponent(cancelButton)))));

		layout.linkSize(SwingConstants.HORIZONTAL,
				new Component[] { findNextButton, replaceButton, replaceAllButton, cancelButton });
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		// ----------------------------------
		setSize(340, 200);

		findNextButton.addActionListener(this);
		replaceButton.addActionListener(this);
		replaceAllButton.addActionListener(this);

		// cancelButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent ev) {
		// dialog.setVisible(false);
		// }
		// });

		// close dialog window if pressed ESC button -----------
		AbstractAction buttonListener = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};
		cancelButton.getInputMap(JTextArea.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"press_close_dialog_window");
		cancelButton.getActionMap().put("press_close_dialog_window", buttonListener);
		cancelButton.addActionListener(buttonListener);
		// ------------

		findWhat.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent te) {
				enableDisableButtons();
			}
		});

		// findWhat.addTextListener(new TextListener() {
		// public void textValueChanged(TextEvent te) {
		// enableDisableButtons();
		// }
		// });
		findWhat.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				enableDisableButtons();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				enableDisableButtons();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				enableDisableButtons();
			}
		});

	}

	//////////////////////////
	void enableDisableButtons() {
		if (findWhat.getText().length() == 0) {
			findNextButton.setEnabled(false);
			replaceButton.setEnabled(false);
			replaceAllButton.setEnabled(false);
		} else {
			findNextButton.setEnabled(true);
			replaceButton.setEnabled(true);
			replaceAllButton.setEnabled(true);
		}
	}

	///////////////////////////////////
	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == findNextButton)
			findNextWithSelection();
		else if (ev.getSource() == replaceButton)
			replaceNext();
		else if (ev.getSource() == replaceAllButton)
			JOptionPane.showMessageDialog(null, "Total replacements made = " + replaceAllNext());

	}

	/////////////////////////
	int findNext() {

		String s1 = ta.getText();
		String s2 = findWhat.getText();

		lastIndex = ta.getCaretPosition();

		int selStart = ta.getSelectionStart();
		int selEnd = ta.getSelectionEnd();

		if (up.isSelected()) {
			if (selStart != selEnd)
				lastIndex = selEnd - s2.length() - 1;
			/*****
			 * Notepad doesnt use the else part, but it should be, instead of
			 * using caretPosition.*** else lastIndex=lastIndex-s2.length();
			 ******/

			if (!matchCase.isSelected())
				lastIndex = s1.toUpperCase().lastIndexOf(s2.toUpperCase(), lastIndex);
			else
				lastIndex = s1.lastIndexOf(s2, lastIndex);
		} else {
			if (selStart != selEnd)
				lastIndex = selStart + 1;
			if (!matchCase.isSelected())
				lastIndex = s1.toUpperCase().indexOf(s2.toUpperCase(), lastIndex);
			else
				lastIndex = s1.indexOf(s2, lastIndex);
		}

		return lastIndex;
	}

	///////////////////////////////////////////////
	public void findNextWithSelection() {
		int idx = findNext();
		if (idx != -1) {
			ta.setSelectionStart(idx);
			ta.setSelectionEnd(idx + findWhat.getText().length());
		} else
			JOptionPane.showMessageDialog(this, "Cannot find" + " \"" + findWhat.getText() + "\"", "Find",
					JOptionPane.INFORMATION_MESSAGE);
	}

	void replaceNext() {
		// if nothing is selectd
		if (ta.getSelectionStart() == ta.getSelectionEnd()) {
			findNextWithSelection();
			return;
		}

		String searchText = findWhat.getText();
		String temp = ta.getSelectedText(); // get selected text

		// check if the selected text matches the search text then do
		// replacement

		if ((matchCase.isSelected() && temp.equals(searchText))
				|| (!matchCase.isSelected() && temp.equalsIgnoreCase(searchText)))
			ta.replaceSelection(replaceWith.getText());

		findNextWithSelection();
	}

	int replaceAllNext() {
		if (up.isSelected())
			ta.setCaretPosition(ta.getText().length() - 1);
		else
			ta.setCaretPosition(0);

		int idx = 0;
		int counter = 0;
		do {
			idx = findNext();
			if (idx == -1)
				break;
			counter++;
			ta.replaceRange(replaceWith.getText(), idx, idx + findWhat.getText().length());
		} while (idx != -1);

		return counter;
	}

	public boolean showDialog(Component parent, boolean isFind) {

		Frame owner = null;
		if (parent instanceof Frame)
			owner = (Frame) parent;
		else
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
		if (dialog == null || dialog.getOwner() != owner) {
			dialog = new JDialog(owner, false);
			dialog.getContentPane().add(this);
			dialog.getRootPane().setDefaultButton(findNextButton);
		}

		if (findWhat.getText().length() == 0)
			findNextButton.setEnabled(false);
		else
			findNextButton.setEnabled(true);

		replaceButton.setVisible(false);
		replaceAllButton.setVisible(false);
		replaceWith.setVisible(false);
		replaceLabel.setVisible(false);

		if (isFind) {
			// card.show(buttonPanel,"find");
			dialog.setSize(460, 180);
			dialog.setTitle("Find");
		} else {
			replaceButton.setVisible(true);
			replaceAllButton.setVisible(true);
			replaceWith.setVisible(true);
			replaceLabel.setVisible(true);

			// card.show(buttonPanel,"replace");
			dialog.setSize(450, 200);
			dialog.setTitle("Replace");
		}
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);

		// System.out.println(dialog.getWidth()+" "+dialog.getHeight());
		return ok;
	}
}