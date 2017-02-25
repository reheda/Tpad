package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.Legacy;
import ua.pp.hak.util.RequestFocusListener;

public class LegacyInfoDialog implements MenuConstants {
	final static Logger logger = LogManager.getLogger(LegacyInfoDialog.class);

	public static void showLegacyInfo(JFrame frame) {
		try {
			// String legacyCode = JOptionPane.showInputDialog(frame, "Enter
			// Legacy Code:", helpLegacyInfo, JOptionPane.PLAIN_MESSAGE);
			AutoCompletePanel p = new AutoCompletePanel();
			int returnVal = JOptionPane.showOptionDialog(frame, p, helpLegacyInfo, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null, null);

			if (returnVal != JOptionPane.OK_OPTION) {
				return;
			}
			String legacyCode = null;
			String legacyName = null;
			if (p.getBtnByCode().isSelected()) {
				legacyCode = p.getFieldCode().getText();
				legacyCode = legacyCode.toUpperCase();
				legacyName = Legacy.getLecagyName(legacyCode);
			} else {
				legacyName = p.getFieldName().getText();
				legacyCode = Legacy.getLecagyCode(legacyName);
			}

			if (legacyName == null || legacyCode == null) {
				JOptionPane.showMessageDialog(frame,
						"Can't find legacy '" + (legacyName != null ? legacyName : legacyCode) + "'", helpLegacyInfo,
						JOptionPane.INFORMATION_MESSAGE);
			} else {

				String text = "<html> <head> <style> table { border-collapse: collapse; } th, td { text-align: left; border-bottom: 1px solid #dddddd; } td.right { border-right: 1px solid #E03134; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; } body {font-family:Segoe UI; font-size:9px; } div {background-color: white; padding:5px; } </style> </head> <body> <div> <table width='500'> "
						+ "<tbody><tr class='header'><td width='90'>Parameter</td><td>Value</td></tr>"
						+ "<tr><td class='right'>Code</td><td>" + legacyCode + "</td></tr>"
						+ "<tr><td class='right'>Name</td><td>" + legacyName + "</td></tr>"
						+ "</tbody> </table> </div> </body></html>";
				JTextPane textPane = new JTextPane();
				textPane.setContentType("text/html");
				textPane.setBackground(null);
				textPane.setOpaque(false);
				textPane.setBorder(null);
				textPane.setText(text);
				textPane.setEditable(false);
				JOptionPane.showMessageDialog(frame, textPane, helpLegacyInfo, JOptionPane.PLAIN_MESSAGE);
				return;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}

class ComboKeyHandler extends KeyAdapter {
	private final JComboBox<String> comboBox;
	private final List<String> list = new ArrayList<>();
	private boolean shouldHide;

	protected ComboKeyHandler(JComboBox<String> combo) {
		super();
		this.comboBox = combo;
		for (int i = 0; i < comboBox.getModel().getSize(); i++) {
			list.add((String) comboBox.getItemAt(i));
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				String text = ((JTextField) e.getComponent()).getText();
				ComboBoxModel<String> m;
				if (text.isEmpty()) {
					String[] array = list.toArray(new String[list.size()]);
					m = new DefaultComboBoxModel<String>(array);
					setSuggestionModel(comboBox, m, "");
					comboBox.hidePopup();
				} else {
					m = getSuggestedModel(list, text);
					if (m.getSize() == 0 || shouldHide) {
						comboBox.hidePopup();
					} else {
						setSuggestionModel(comboBox, m, text);
						comboBox.showPopup();
					}
				}
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// JTextField textField = (JTextField) e.getComponent();
		// String text = textField.getText();
		shouldHide = false;
		switch (e.getKeyCode()) {
		// case KeyEvent.VK_TAB:
		// for (String s : list) {
		// if (s.startsWith(text)) {
		// textField.setText(s);
		// return;
		// }
		// }
		// break;
		case KeyEvent.VK_ENTER:
			// if (!list.contains(text)) {
			// list.add(text);
			// Collections.sort(list);
			// //setSuggestionModel(comboBox, new DefaultComboBoxModel(list),
			// text);
			// setSuggestionModel(comboBox, getSuggestedModel(list, text),
			// text);
			// }
			shouldHide = true;
			break;
		case KeyEvent.VK_ESCAPE:
			shouldHide = true;
			break;
		default:
			break;
		}
	}

	private static void setSuggestionModel(JComboBox<String> comboBox, ComboBoxModel<String> mdl, String str) {
		JTextField tf = ((JTextField) comboBox.getEditor().getEditorComponent());
		int caretPos = tf.getCaretPosition();
		comboBox.setModel(mdl);
		comboBox.setSelectedIndex(-1);
		tf.setText(str);
		tf.setCaretPosition(caretPos);
	}

	private static ComboBoxModel<String> getSuggestedModel(List<String> list, String text) {
		DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
		for (String s : list) {
			// if (s.toUpperCase().contains(text.toUpperCase())){
			String regex = "(?i).*" + escapeSpecialRegexChars(text).replace("%", ".*") + ".*";
			if (isRegexValid(regex) && s.matches(regex)) {
				m.addElement(s);
			}
		}
		return m;
	}

	private static boolean isRegexValid(String regex) {
		boolean isRegexValid;
		try {
			Pattern.compile(regex);
			isRegexValid = true;
		} catch (PatternSyntaxException e) {
			isRegexValid = false;
		}
		return isRegexValid;
	}

	private static String escapeSpecialRegexChars(String regexString) {
		// from here http://stackoverflow.com/a/27454382

		// all possible values <([{\^-=$!|]})?*+.>
		// http://docs.oracle.com/javase/tutorial/essential/regex/literals.html
		String escaped = regexString.replaceAll("[\\<\\(\\[\\{\\\\\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]",
				"\\\\$0");
		return escaped;
	}
}

final class AutoCompletePanel extends JPanel {
	private JTextField fieldName;
	private JTextField fieldCode;
	private JRadioButton btnByCode;
	private JRadioButton btnByName;

	public JRadioButton getBtnByCode() {
		return btnByCode;
	}

	public JRadioButton getBtnByName() {
		return btnByName;
	}

	public JTextField getFieldCode() {
		return fieldCode;
	}

	public JTextField getFieldName() {
		return fieldName;
	}

	AutoCompletePanel() {
		super(new BorderLayout());

		String[] arrayCode = Legacy.getLegacy().keySet().toArray(new String[0]);
		JComboBox<String> comboCode = makeComboBox(arrayCode);
		comboCode.setEditable(true);
		comboCode.setSelectedIndex(-1);
		fieldCode = (JTextField) comboCode.getEditor().getEditorComponent();
		fieldCode.setText("");
		// fieldCode.setFocusTraversalKeysEnabled(false); // enable TAB
		fieldCode.addAncestorListener(new RequestFocusListener());
		fieldCode.addKeyListener(new ComboKeyHandler(comboCode));

		String[] arrayName = Legacy.getLegacy().values().toArray(new String[0]);
		JComboBox<String> comboName = makeComboBox(arrayName);
		comboName.setEditable(true);
		comboName.setSelectedIndex(-1);
		fieldName = (JTextField) comboName.getEditor().getEditorComponent();
		fieldName.setText("");
		// fieldName.setFocusTraversalKeysEnabled(false); // enable TAB
		fieldName.addKeyListener(new ComboKeyHandler(comboName));

		btnByCode = new JRadioButton("Search by code");
		btnByCode.setFocusable(false);
		btnByCode.setSelected(true);
		btnByCode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fieldCode.requestFocus();
			}
		});

		btnByName = new JRadioButton("Search by name");
		btnByName.setFocusable(false);
		btnByName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fieldName.requestFocus();
			}
		});
		ButtonGroup bg = new ButtonGroup();
		bg.add(btnByCode);
		bg.add(btnByName);

		// add listeners
		addListenersToTextFields(comboCode, comboName, btnByCode, btnByName);

		JPanel pTop = new JPanel(new BorderLayout());
		pTop.setBorder(BorderFactory.createTitledBorder(""));
		pTop.add(btnByCode, BorderLayout.NORTH);
		pTop.add(comboCode, BorderLayout.SOUTH);

		JPanel pBottom = new JPanel(new BorderLayout());
		pBottom.setBorder(BorderFactory.createTitledBorder(""));
		pBottom.add(btnByName, BorderLayout.NORTH);
		pBottom.add(comboName, BorderLayout.SOUTH);

		Box box = Box.createVerticalBox();
		box.add(pTop);
		box.add(Box.createVerticalStrut(20));
		box.add(pBottom);
		add(box, BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	private void addListenersToTextFields(JComboBox<String> comboCode, JComboBox<String> comboName,
			JRadioButton btnByCode, JRadioButton btnByName) {
		fieldCode.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnByCode.setSelected(true);
				comboName.getEditor().getEditorComponent().setForeground(Color.LIGHT_GRAY);
				comboCode.getEditor().getEditorComponent().setForeground(Color.BLACK);
			}
		});
		fieldName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnByName.setSelected(true);
				comboName.getEditor().getEditorComponent().setForeground(Color.BLACK);
				comboCode.getEditor().getEditorComponent().setForeground(Color.LIGHT_GRAY);
			}
		});
	}

	private static JComboBox<String> makeComboBox(String... model) {
		return new JComboBox<String>(model);
	}
}
