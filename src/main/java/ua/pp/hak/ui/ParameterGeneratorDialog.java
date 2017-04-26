package ua.pp.hak.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.RequestFocusListener;

public class ParameterGeneratorDialog implements ActionListener, KeyListener {

	final static Logger logger = LogManager.getLogger(ParameterGeneratorDialog.class);

	private JTextArea taParameters;
	private JLabel accTree;
	private JCheckBox evaluate, locale, verbatim, legacyValues, resultSeparator, substitution;
	private JComboBox<String> cbEvaluate, cbVerbatim, cbLegacyValues;
	private JTextField tfLocale, tfResultSeparator, tfAccTree, tfSubstitution;
	private String defaultText = "AccTree=-1";

	public String generateParameters(JComponent parent) {
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		main.add(createParameterPanel());

		// main.setPreferredSize(new Dimension(400, 100));

		int result = JOptionPane.showConfirmDialog(parent, main, "Generate Parameters", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			return taParameters.getText();
		}

		return null;
	}

	private JPanel createParameterPanel() {
		taParameters = new JTextArea();
		taParameters.setLineWrap(true);
		taParameters.setFont(new Font("Consolas", Font.PLAIN, 12));
		taParameters.addKeyListener(this);
		taParameters.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				checkParameters();
			}
		});
		taParameters.addAncestorListener(new RequestFocusListener());
		taParameters.setText(defaultText);

		JScrollPane spParameters = new JScrollPane(taParameters);
		spParameters.setPreferredSize(new Dimension(400, 50));
		spParameters.setMinimumSize(new Dimension(400, 50));
		spParameters.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		paramPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
		paramPanel.add(spParameters);
		paramPanel.add(createParametersToSelect());

		return paramPanel;
	}

	private JPanel createParametersToSelect() {
		String[] valueStrings = { "true", "false" };

		evaluate = createCheckBox("Evaluate");
		locale = createCheckBox("Locale");
		verbatim = createCheckBox("Verbatim");
		legacyValues = createCheckBox("LegacyValues");
		resultSeparator = createCheckBox("ResultSeparator");
		substitution = createCheckBox("Substitution");
		accTree = new JLabel("AccTree");

		cbEvaluate = createComboBox(valueStrings);
		cbVerbatim = createComboBox(valueStrings);
		cbLegacyValues = createComboBox(valueStrings);
		tfLocale = new JTextField("en-US");
		tfLocale.setEnabled(false);
		tfLocale.addKeyListener(this);
		tfResultSeparator = new JTextField(";");
		tfResultSeparator.setEnabled(false);
		tfResultSeparator.addKeyListener(this);
		tfSubstitution = new JTextField("Old/g~New");
		tfSubstitution.setEnabled(false);
		tfSubstitution.addKeyListener(this);
		tfAccTree = new JTextField("-1");
		tfAccTree.addKeyListener(this);

		JPanel parametersToSelect = new JPanel(new SpringLayout());

		parametersToSelect.add(accTree);
		parametersToSelect.add(tfAccTree);
		parametersToSelect.add(new JLabel("   "));
		parametersToSelect.add(evaluate);
		parametersToSelect.add(cbEvaluate);

		parametersToSelect.add(locale);
		parametersToSelect.add(tfLocale);
		parametersToSelect.add(new JLabel("   "));
		parametersToSelect.add(verbatim);
		parametersToSelect.add(cbVerbatim);

		parametersToSelect.add(resultSeparator);
		parametersToSelect.add(tfResultSeparator);
		parametersToSelect.add(new JLabel("   "));
		parametersToSelect.add(legacyValues);
		parametersToSelect.add(cbLegacyValues);
		
		parametersToSelect.add(substitution);
		parametersToSelect.add(tfSubstitution);
		parametersToSelect.add(new JLabel("   "));
		parametersToSelect.add(new JLabel("   "));
		parametersToSelect.add(new JLabel("   "));

		parametersToSelect.setAlignmentX(Component.LEFT_ALIGNMENT);
		SpringUtilities.makeCompactGrid(parametersToSelect, 4, 5, 5, 5, 3, 3);
		return parametersToSelect;
	}

	private JComboBox<String> createComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setEnabled(false);
		comboBox.addActionListener(this);
		return comboBox;
	}

	private JCheckBox createCheckBox(String text) {
		JCheckBox cb = new JCheckBox(text);
		cb.addActionListener(this);
		return cb;
	}

	private String removeParameters(String parameterName) {
		String oldParamText = taParameters.getText();
		String newParamText = oldParamText;

		// check with comma
		if (oldParamText.matches(".*,.*?" + parameterName + ".*=.*")) {
			newParamText = newParamText.replaceAll(",\\s*?\\b" + parameterName + "\\b.*?=.*?(,\\s(?=[A-Z])|$)", ", ");

			// check without comma
		} else if (oldParamText.matches(".*?" + parameterName + ".*=.*")) {
			newParamText = newParamText.replaceAll("\\b" + parameterName + "\\b.*?=.*?(,\\s(?=[A-Z])|$)", " ");
		}

		if (!newParamText.trim().isEmpty() && newParamText.trim().charAt(newParamText.trim().length() - 1) == ',') {
			newParamText = newParamText.substring(0, newParamText.lastIndexOf(','));
		}

		if (newParamText.startsWith(" ")) {
			newParamText = newParamText.replaceAll("^\\s*", "");
		}
		return newParamText;
	}

	private String addParameters(String parameterName, String parameterValue) {
		String oldParamText = taParameters.getText();
		String newParamText = oldParamText;
		String txtToAdd = parameterName.concat("=").concat(parameterValue).concat(", ");

		if (oldParamText.matches(".*" + parameterName + ".*=.*")) {
			newParamText = newParamText.replaceAll("\\b" + parameterName + "\\b.*?=.*?(,\\s(?=[A-Z])|$)", txtToAdd);
		} else {
			String trimmerdOldParamText = oldParamText.trim();
			if (!trimmerdOldParamText.isEmpty()
					&& trimmerdOldParamText.charAt(trimmerdOldParamText.length() - 1) != ',') {
				txtToAdd = ", ".concat(txtToAdd);
			} else if (!trimmerdOldParamText.isEmpty()) {
				txtToAdd = " ".concat(txtToAdd);
			}
			newParamText = newParamText.concat(txtToAdd);
		}

		if (!newParamText.trim().isEmpty() && newParamText.trim().charAt(newParamText.trim().length() - 1) == ',') {
			newParamText = newParamText.substring(0, newParamText.lastIndexOf(','));
		}
		return newParamText;
	}

	private void checkParameters() {

		String paramText = taParameters.getText();

		if (paramText.matches(".*Evaluate.*=.*")) {
			cbEvaluate.setEnabled(true);
			evaluate.setSelected(true);
		} else {
			cbEvaluate.setEnabled(false);
			evaluate.setSelected(false);
		}
		if (paramText.matches(".*Locale.*=.*")) {
			tfLocale.setEnabled(true);
			locale.setSelected(true);
		} else {
			tfLocale.setEnabled(false);
			locale.setSelected(false);
		}
		if (paramText.matches(".*ResultSeparator.*=.*")) {
			tfResultSeparator.setEnabled(true);
			resultSeparator.setSelected(true);
		} else {
			tfResultSeparator.setEnabled(false);
			resultSeparator.setSelected(false);
		}
		if (paramText.matches(".*Substitution.*=.*")) {
			tfSubstitution.setEnabled(true);
			substitution.setSelected(true);
		} else {
			tfSubstitution.setEnabled(false);
			substitution.setSelected(false);
		}
		if (paramText.matches(".*Verbatim.*=.*")) {
			cbVerbatim.setEnabled(true);
			verbatim.setSelected(true);
		} else {
			cbVerbatim.setEnabled(false);
			verbatim.setSelected(false);
		}
		if (paramText.matches(".*LegacyValues.*=.*")) {
			cbLegacyValues.setEnabled(true);
			legacyValues.setSelected(true);
		} else {
			cbLegacyValues.setEnabled(false);
			legacyValues.setSelected(false);
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent ev) {
		Object evObj = ev.getSource();
		if (evObj == taParameters) {
			checkParameters();
		} else if (evObj == tfAccTree) {
			String newParamText = addParameters(accTree.getText(), tfAccTree.getText());
			taParameters.setText(newParamText);
		} else if (evObj == tfLocale) {
			String newParamText = addParameters(locale.getText(), tfLocale.getText());
			taParameters.setText(newParamText);
		} else if (evObj == tfResultSeparator) {
			String newParamText = addParameters(resultSeparator.getText(), "\"" + tfResultSeparator.getText() + "\"");
			taParameters.setText(newParamText);
		} else if (evObj == tfSubstitution) {
			String newParamText = addParameters(substitution.getText(), "\"" + tfSubstitution.getText() + "\"");
			taParameters.setText(newParamText);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		// String cmdText = ev.getActionCommand();
		Object evObj = ev.getSource();

		if (evObj == locale) {
			String newParamText = null;
			if (locale.isSelected()) {
				tfLocale.setEnabled(true);
				newParamText = addParameters(locale.getText(), tfLocale.getText());
			} else {
				tfLocale.setEnabled(false);
				newParamText = removeParameters(locale.getText());
			}
			taParameters.setText(newParamText);
		} else if (evObj == resultSeparator) {
			String newParamText = null;
			if (resultSeparator.isSelected()) {
				tfResultSeparator.setEnabled(true);
				newParamText = addParameters(resultSeparator.getText(), "\"" + tfResultSeparator.getText() + "\"");
			} else {
				tfResultSeparator.setEnabled(false);
				newParamText = removeParameters(resultSeparator.getText());
			}
			taParameters.setText(newParamText);
		} else if (evObj == substitution) {
			String newParamText = null;
			if (substitution.isSelected()) {
				tfSubstitution.setEnabled(true);
				newParamText = addParameters(substitution.getText(), "\"" + tfSubstitution.getText() + "\"");
			} else {
				tfSubstitution.setEnabled(false);
				newParamText = removeParameters(substitution.getText());
			}
			taParameters.setText(newParamText);
		} else if (evObj == evaluate) {
			String newParamText = null;
			if (evaluate.isSelected()) {
				cbEvaluate.setEnabled(true);
				newParamText = addParameters(evaluate.getText(), (String) cbEvaluate.getSelectedItem());
			} else {
				cbEvaluate.setEnabled(false);
				newParamText = removeParameters(evaluate.getText());
			}
			taParameters.setText(newParamText);
		} else if (evObj == verbatim) {
			String newParamText = null;
			if (verbatim.isSelected()) {
				cbVerbatim.setEnabled(true);
				newParamText = addParameters(verbatim.getText(), (String) cbVerbatim.getSelectedItem());
			} else {
				cbVerbatim.setEnabled(false);
				newParamText = removeParameters(verbatim.getText());
			}
			taParameters.setText(newParamText);
		} else if (evObj == legacyValues) {
			String newParamText = null;
			if (legacyValues.isSelected()) {
				cbLegacyValues.setEnabled(true);
				newParamText = addParameters(legacyValues.getText(), (String) cbLegacyValues.getSelectedItem());
			} else {
				cbLegacyValues.setEnabled(false);
				newParamText = removeParameters(legacyValues.getText());
			}
			taParameters.setText(newParamText);
		}

		else if (evObj == cbEvaluate) {
			String newParamText = addParameters(evaluate.getText(), (String) cbEvaluate.getSelectedItem());
			taParameters.setText(newParamText);
		} else if (evObj == cbLegacyValues) {
			String newParamText = addParameters(legacyValues.getText(), (String) cbLegacyValues.getSelectedItem());
			taParameters.setText(newParamText);
		} else if (evObj == cbVerbatim) {
			String newParamText = addParameters(verbatim.getText(), (String) cbVerbatim.getSelectedItem());
			taParameters.setText(newParamText);
		}

	}

}
