package ua.pp.hak.setting;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

import ua.pp.hak.ui.Notepad;

public class SettingsOperation {

	Notepad npd;
	RSyntaxTextArea taExpr;
	SyntaxScheme scheme;
	JCheckBoxMenuItem wordWrapItem;
	JCheckBoxMenuItem statusBarItem;
	JCheckBoxMenuItem parserPanelItem;
	Font defaultFont;

	public SettingsOperation(Notepad npd) {
		this.npd = npd;
		taExpr = npd.getExprTextArea();
		scheme = taExpr.getSyntaxScheme();
		wordWrapItem = npd.getWordWrapItem();
		statusBarItem = npd.getStatusBarItem();
		parserPanelItem = npd.getParserPanelItem();
		defaultFont = npd.getDefaultFont();

	}

	final static Logger logger = LogManager.getLogger(SettingsOperation.class);

	public void saveSettings() {

		logger.info("Try to save settings...");
		Settings settings = new Settings(taExpr.getFont(), taExpr.getBackground(), taExpr.getForeground(),
				scheme.getStyle(Token.RESERVED_WORD).foreground, scheme.getStyle(Token.COMMENT_EOL).foreground,
				scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground, wordWrapItem.isSelected(),
				statusBarItem.isSelected(), parserPanelItem.isSelected());

		SettingStAXWriter.saveSettings(settings);
		logger.info("Settings were saved!");
	}

	public void resetSettings() {

		int answer = JOptionPane.showConfirmDialog(npd.getFrame(),
				"Following settings will be reset to default:\n\n" + "    Font: familly, size, style\n"
						+ "    Color: text, pad, keywords, comments, literal strings\n"
						+ "    View: status bar, word wrap, parser panel\n\n" + "Continue?",
				"Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.YES_OPTION) {

			taExpr.setFont(defaultFont);
			taExpr.setBackground(new Color(-1)); // white
			taExpr.setForeground(new Color(-16777216)); // black
			scheme.getStyle(Token.RESERVED_WORD).foreground = Color.blue;
			scheme.getStyle(Token.DATA_TYPE).foreground = Color.blue;
			scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(68, 102, 170);// lightBlue
			scheme.getStyle(Token.COMMENT_EOL).foreground = new Color(51, 136, 85); // darkGreen
			taExpr.revalidate();
			taExpr.repaint();

			if (wordWrapItem.isSelected() != true) {
				wordWrapItem.doClick();
			}
			if (statusBarItem.isSelected() != true) {
				statusBarItem.doClick();
			}
			if (parserPanelItem.isSelected() != true) {
				parserPanelItem.doClick();
			}
			logger.info("Settings were reset!");
		}
	}

	public void readSettings() {

		logger.info("Try to read settings...");
		Settings settings = SettingStAXReader.parseSettings();
		if (settings == null) {
			// file setting is not valid against XSD schema or doesn't exist
			return;
		}
		Font font = settings.getFont();
		taExpr.setFont(font);
		taExpr.setBackground(settings.getBackgroundColor());
		taExpr.setForeground(settings.getForegroundColor());
		scheme.getStyle(Token.RESERVED_WORD).foreground = settings.getKeywordColor();
		scheme.getStyle(Token.DATA_TYPE).foreground = settings.getKeywordColor();
		scheme.getStyle(Token.COMMENT_EOL).foreground = settings.getCommentColor();
		scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = settings.getStringColor();
		taExpr.revalidate();
		taExpr.repaint();

		if (wordWrapItem.isSelected() != settings.isWordWrapEnabled()) {
			wordWrapItem.doClick();
		}
		if (statusBarItem.isSelected() != settings.isStatusBarEnabled()) {
			statusBarItem.doClick();
		}
		if (parserPanelItem.isSelected() != settings.isParserPanelEnabled()) {
			parserPanelItem.doClick();
		}
		logger.info("Settings were read!");
	}

}
