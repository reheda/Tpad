package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import ua.pp.hak.autocomplete.AutoCompleter;
import ua.pp.hak.autocomplete.LanguageSupportFactory;
import ua.pp.hak.setting.SettingsOperation;
import ua.pp.hak.update.Updater;
import ua.pp.hak.update.Version;
import ua.pp.hak.util.Actions;
import ua.pp.hak.util.FileOperation;
import ua.pp.hak.util.Listeners;

public class Notepad implements ActionListener, MenuConstants, Constants {
	final static Logger logger = LogManager.getLogger(Notepad.class);

	private JFrame frame;
	private RSyntaxTextArea taExpr;
	private JTextField tfSKU;
	private JTextArea taExprRes;
	private JTextArea taParameters;
	private Notepad npd;
	private SyntaxScheme scheme;

	private JLabel statusBar;
	private JScrollPane spExpr;
	private JScrollPane spExprRes;
	private JScrollPane spParameters;
	private JToolBar toolBar;
	static String build = "x";

	private Font defaultFont = new Font("Consolas", Font.PLAIN, 14);
	private Font font = new Font("Consolas", Font.PLAIN, 14);
	private FileOperation fileHandler;
	private SettingsOperation settingsHandler;
	private FindDialog findReplaceDialog = null;
	private JColorChooser bcolorChooser, fcolorChooser, keywordColorChooser, commentColorChooser, stringColorChooser;
	private JDialog backgroundDialog, foregroundDialog, keywordDialog, commentDialog, stringDialog;
	private JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem,
			undoItem, redoItem;
	private JCheckBoxMenuItem wordWrapItem;
	private JCheckBoxMenuItem parserPanelItem;
	private JCheckBoxMenuItem statusBarItem;
	private Action redoAction;
	private Action undoAction;
	private JSplitPane splitPane;
	private JPanel rightPanel;
	private JPanel leftPanel;

	private JButton newButton, openButton, saveButton, parseButton, checkButton, undoButton, redoButton, copyButton,
			cutButton, pasteButton, findButton, replaceButton, zoomInButton, zoomOutButton, zoomDefaultButton,
			fontButton, helpButton, aboutButton, wrapButton, shortcutsButton;

	public JFrame getFrame() {
		return frame;
	}

	public JTextArea getExprResTextArea() {
		return taExprRes;
	}

	public RSyntaxTextArea getExprTextArea() {
		return taExpr;
	}

	public JLabel getStatusBar() {
		return statusBar;
	}

	public JButton getUndoButton() {
		return undoButton;
	}

	public JButton getRedoButton() {
		return redoButton;
	}

	public JTextField getSkuField() {
		return tfSKU;
	}

	public JTextArea getParametersTextArea() {
		return taParameters;
	}

	public Font getDefaultFont() {
		return defaultFont;
	}

	public JCheckBoxMenuItem getWordWrapItem() {
		return wordWrapItem;
	}

	public JCheckBoxMenuItem getStatusBarItem() {
		return statusBarItem;
	}

	public JCheckBoxMenuItem getParserPanelItem() {
		return parserPanelItem;
	}

	public JMenuItem getFindItem() {
		return findItem;
	}

	public JMenuItem getReplaceItem() {
		return replaceItem;
	}

	public JMenuItem getGotoItem() {
		return gotoItem;
	}

	public JMenuItem getUndoItem() {
		return undoItem;
	}

	public JMenuItem getRedoItem() {
		return redoItem;
	}

	public JButton getCopyButton() {
		return copyButton;
	}

	public JButton getCutButton() {
		return cutButton;
	}

	public JButton getFindButton() {
		return findButton;
	}

	public JButton getReplaceButton() {
		return replaceButton;
	}

	public FileOperation getFileHandler() {
		return fileHandler;
	}

	public SettingsOperation getSettingsHandler() {
		return settingsHandler;
	}

	/****************************/
	Notepad() {
		
		logger.info("Creating GUI...");
		this.npd = this;
		frame = new JFrame(defaultFileName + " - " + applicationName);
		// change icon of the app
		frame.setIconImage(new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)).getImage());
		// change look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// // build
		// try {
		// Date tmpDate = new
		// Date(Notepad.class.getResource("Notepad.class").openConnection().getLastModified());
		// build = new SimpleDateFormat("yyMMdd").format(tmpDate);
		// } catch (Exception e) {
		// logger.error("can't find Notepad.class");
		// logger.error(e.getMessage());
		// }

		
		createSplitPaneView();
		createStatusBarView();
		createMenuBar();
		createToolBar();
		
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		frame.getContentPane().add(new JLabel("  "), BorderLayout.EAST);
		frame.getContentPane().add(new JLabel("  "), BorderLayout.WEST);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		frame.setSize(1000, 450);
		frame.pack();
		frame.setLocation(100, 50);
		frame.setMinimumSize(new Dimension(600, 450));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		fileHandler = new FileOperation(this);
		settingsHandler = new SettingsOperation(this);

		// add TextArea listeners. Should place at the end of the class due to using buttons and items.
		taExpr.addCaretListener(new Listeners.Caret(this));
		taExpr.addMouseWheelListener(new Listeners.MouseWheel(this));
		taExpr.getDocument().addDocumentListener(new Listeners.Document(this));
		
		// add Frame listener
		frame.addWindowListener(new Listeners.Window(this));

		logger.info("Finish creating GUI");

		// read settings.xml file and apply
		settingsHandler.readSettings();
	}

	private void createStatusBarView() {
		statusBar = new JLabel("Line 1, Column 1  ", JLabel.LEFT);
		statusBar.setBorder(new CompoundBorder(statusBar.getBorder(), new EmptyBorder(2, 6, 2, 5)));
	}

	private void createSplitPaneView() {
		createLeftPanelView();
		createRightPanelView();

		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, rightPanel);
		splitPane.setResizeWeight(1.0);
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
		divider.setBackground(new Color(171, 173, 179)); // gray
		divider.setBorder(null);
		// splitPane.setDividerSize(1);

		// Provide a preferred size for the split pane.
		// splitPane.setPreferredSize(new Dimension(1000, 450));
	}

	private void createLeftPanelView() {
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		JLabel lblExpr = new JLabel(txtExpr);
		createExpressionView();

		lblExpr.setAlignmentX(Component.LEFT_ALIGNMENT);
		spExpr.setAlignmentX(Component.LEFT_ALIGNMENT);

		leftPanel.add(lblExpr);
		leftPanel.add(spExpr);
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
	}

	private void createExpressionView() {

		taExpr = new RSyntaxTextArea(30, 60);
		taExpr.setFont(font);
		taExpr.setTabSize(4);
		taExpr.setCodeFoldingEnabled(true);
		taExpr.setMargin(new Insets(0, 5, 0, 5));

		// change color of Syntax
		scheme = taExpr.getSyntaxScheme();
		// new Color (153,51,153); //dark pink
		scheme.getStyle(Token.RESERVED_WORD).foreground = Color.blue;
		scheme.getStyle(Token.DATA_TYPE).foreground = Color.blue;
		// light blue
		scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(68, 102, 170);
		// dark green
		scheme.getStyle(Token.COMMENT_EOL).foreground = new Color(51, 136, 85);
		taExpr.revalidate();
		taExpr.repaint();

		// set lexer syntax
		// taExpr.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("templexLanguage", "ua.pp.hak.ui.TemplexTokenMaker");
		taExpr.setSyntaxEditingStyle("templexLanguage");

		// set auto complete
		 LanguageSupportFactory.get().register(taExpr);
//		CompletionProvider provider = AutoCompleter.createCompletionProvider();
//		AutoCompletion ac = new AutoCompletion(provider);
//		ac.install(taExpr);

		spExpr = new JScrollPane(taExpr);
		TextLineNumber tln = new TextLineNumber(taExpr);
		// enables the automatic updating of the Font when the Font of the
		// related text component changes.
		tln.setUpdateFont(true);
		spExpr.setRowHeaderView(tln);
		spExpr.setPreferredSize(new Dimension(600, 450));
		spExpr.setMinimumSize(new Dimension(300, 200));

		changeActionMap();
	}

	private void changeActionMap() {
		// ------- disable ctrl+H in textAreas and textFields
		String[] ctrlHmaps = new String[] { "TextArea.focusInputMap", "TextField.focusInputMap" };
		for (int i = 0; i < ctrlHmaps.length; i++) {
			InputMap im = (InputMap) UIManager.get(ctrlHmaps[i]);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK), "none");
		}

		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK), "ctrlPlus");
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "ctrlPlus");
		taExpr.getActionMap().put("ctrlPlus", new Actions.ChangeFontSizeAction(taExpr, 1));

		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK), "ctrlMinus");
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "ctrlMinus");
		taExpr.getActionMap().put("ctrlMinus", new Actions.ChangeFontSizeAction(taExpr, -1));

		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.CTRL_DOWN_MASK), "ctrlZero");
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), "ctrlZero");
		taExpr.getActionMap().put("ctrlZero", new Actions.SetFontAction(taExpr, font));

		{
			// add few actions to TextArea
			Action[] actions = taExpr.getActions();
			int n = actions.length;
			int defaultModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			int alt = InputEvent.ALT_MASK;
			int shift = InputEvent.SHIFT_MASK;
			InputMap im = taExpr.getInputMap();
			ActionMap am = taExpr.getActionMap();
			for (int i = 0; i < n; i++) {
				Action a = actions[i];
				String actionName = (String) a.getValue(Action.NAME);
				if (actionName.equals(RTextAreaEditorKit.rtaUndoAction)) {
					undoAction = a;
				} else if (actionName.equals(RTextAreaEditorKit.rtaRedoAction)) {
					redoAction = a;
				} else if (actionName.equals(RTextAreaEditorKit.rtaUpperSelectionCaseAction)) {
					// set shortcut Ctrl+Shift+X for ToUpperCase function
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, defaultModifier | shift),
							RTextAreaEditorKit.rtaUpperSelectionCaseAction);
				} else if (actionName.equals(RTextAreaEditorKit.rtaLowerSelectionCaseAction)) {
					// set shortcut Ctrl+Shift+Y for ToLowerCase function
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, defaultModifier | shift),
							RTextAreaEditorKit.rtaLowerSelectionCaseAction);
				} else if (actionName.equals(RTextAreaEditorKit.rtaNextOccurrenceAction)) {
					// change action of next occurrence shortcut (Ctrl+K)
					am.put(RTextAreaEditorKit.rtaNextOccurrenceAction,
							new Actions.NextOccurrenceAction(RTextAreaEditorKit.rtaNextOccurrenceAction));
				} else if (actionName.equals(RTextAreaEditorKit.rtaPrevOccurrenceAction)) {
					// change action of prev occurrence shortcut (Ctrl+Shift+K)
					am.put(RTextAreaEditorKit.rtaPrevOccurrenceAction,
							new Actions.PreviousOccurrenceAction(RTextAreaEditorKit.rtaPrevOccurrenceAction));
				}
			}

			final String rtaCopyLineDownAction = "RTA.CopyLineDownAction";
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, defaultModifier | alt), rtaCopyLineDownAction);
			am.put(rtaCopyLineDownAction, new Actions.LineCopyAction(rtaCopyLineDownAction, 1));

			final String rtaCopyLineUpAction = "RTA.CopyLineUpAction";
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, defaultModifier | alt), rtaCopyLineUpAction);
			am.put(rtaCopyLineUpAction, new Actions.LineCopyAction(rtaCopyLineUpAction, -1));
		}
	}

	private void createRightPanelView() {
		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

		JLabel lblExprRes = new JLabel(txtExprRes);
		createExpressionResultView();

		JLabel lblParameters = new JLabel(txtParameters);
		createParametersView();

		JLabel lblSKU = new JLabel(txtSKU);
		createSkuView();

		lblExprRes.setAlignmentX(Component.LEFT_ALIGNMENT);
		spExprRes.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblParameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		spParameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
		tfSKU.setAlignmentX(Component.LEFT_ALIGNMENT);

		rightPanel.add(lblExprRes);
		rightPanel.add(spExprRes);
		rightPanel.add(new JLabel(" "));

		rightPanel.add(lblParameters);
		rightPanel.add(spParameters);
		JButton generateParams = new JButton("Generate...");
		generateParams.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String newParams = new ParameterGeneratorDialog().generateParameters(rightPanel);
				if (newParams!=null){
					taParameters.setText(newParams);
				}
			}
		});
		rightPanel.add(generateParams);
		rightPanel.add(new JLabel(" "));

		rightPanel.add(lblSKU);
		rightPanel.add(tfSKU);
	}

	private void createSkuView() {
		tfSKU = new JTextField(20);
		tfSKU.setMaximumSize(new Dimension(4000, 20));
		tfSKU.setFont(font.deriveFont(12f));
		tfSKU.setText(defaultSKU);
		tfSKU.setBorder(new CompoundBorder(tfSKU.getBorder(), new EmptyBorder(2, 2, 2, 2)));
	}

	private void createParametersView() {
		taParameters = new JTextArea();
		taParameters.setLineWrap(true);
		taParameters.setFont(font.deriveFont(12f));
		taParameters.setBorder(new EmptyBorder(3, 3, 3, 3));
		taParameters.setText(defaultParameters);
		spParameters = new JScrollPane(taParameters);
		spParameters.setBorder(BorderFactory.createLineBorder(new Color(171, 173, 179))); // grey
		spParameters.setPreferredSize(new Dimension(400, 50));
		spParameters.setMinimumSize(new Dimension(300, 50));
	}

	private void createExpressionResultView() {
		// create Expression result view
		taExprRes = new JTextArea();
		taExprRes.setFont(font.deriveFont(12f));
		taExprRes.setEditable(false);
		// tp.setBackground(new Color(240,240,240)); //light grey
		taExprRes.setBackground(null);
		taExprRes.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, new Color(0, 188, 57)),
				new EmptyBorder(2, 5, 2, 0))); // green
		taExprRes.setText(defaultExpressionResult);
		taExprRes.setText(taExprRes.getText().replaceAll("\n", "//\n"));
		taExprRes.setLineWrap(true);
		
		spExprRes = new JScrollPane(taExprRes);
		spExprRes.setPreferredSize(new Dimension(400, 450));
		spExprRes.setBorder(new EmptyBorder(0, 0, 0, 0));
		spExprRes.setMinimumSize(new Dimension(300, 200));
	}

	///////////////////////////////////
	public void actionPerformed(ActionEvent ev) {
		String cmdText = ev.getActionCommand();
		Object evObj = ev.getSource();
		////////////////////////////////////
		if (cmdText.equals(fileNew) || evObj == newButton) {
			logger.info("Creating new file");
			fileHandler.newFile();
		} else if (cmdText.equals(fileOpen) || evObj == openButton) {
			logger.info("Opening file");
			fileHandler.openFile();
		}
		////////////////////////////////////
		else if (cmdText.equals(fileSave) || evObj == saveButton) {
			logger.info("Saving file");
			fileHandler.saveThisFile();
		}
		////////////////////////////////////
		else if (cmdText.equals(fileSaveAs)) {
			logger.info("SavingAs file");
			fileHandler.saveAsFile();
		}
		////////////////////////////////////
		else if (cmdText.equals(fileExit)) {
			if (fileHandler.confirmSave()) {
				logger.info("Stop working...");
				// save settings to settings.xml
				settingsHandler.saveSettings();
				// just for getting backup
				fileHandler.saveTempPadText();
				System.exit(0);
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(filePrint)) {
			logger.info("File print");
			JOptionPane.showMessageDialog(frame, "Get ur printer repaired first! It seems u dont have one!",
					"Bad Printer", JOptionPane.INFORMATION_MESSAGE);
		}

		////////////////////////////////////
		else if (cmdText.equals(editCut) || evObj == cutButton) {
			logger.info("Cut text");
			taExpr.cut();
		}
		////////////////////////////////////
		else if (cmdText.equals(editCopy) || evObj == copyButton) {

			logger.info("Copy text");
			taExpr.copy();
		}
		////////////////////////////////////
		else if (cmdText.equals(editPaste) || evObj == pasteButton) {
			logger.info("Paste text");
			taExpr.paste();
		}
		////////////////////////////////////
		else if (cmdText.equals(editDelete)) {
			logger.info("Delete text");
			taExpr.replaceSelection("");
		}
		////////////////////////////////////
		else if (cmdText.equals(editFind) || evObj == findButton) {
			logger.info("Open Find window");
			if (taExpr.getText().length() == 0)
				return; // text box have no text
			if (findReplaceDialog == null)
				findReplaceDialog = new FindDialog(taExpr);
			findReplaceDialog.showDialog(frame, true);// find
		}
		////////////////////////////////////
		else if (cmdText.equals(editFindNext)) {
			logger.info("Try Find next");
			if (taExpr.getText().length() == 0)
				return; // text box have no text

			if (findReplaceDialog == null)
				statusBar.setText("Nothing to search for, use Find option of Edit Menu first !!!!");
			else
				findReplaceDialog.findNextWithSelection();
		}
		////////////////////////////////////
		else if (cmdText.equals(editReplace) || evObj == replaceButton) {
			logger.info("Open Replace window");
			if (taExpr.getText().length() == 0)
				return; // text box have no text

			if (findReplaceDialog == null)
				findReplaceDialog = new FindDialog(taExpr);
			findReplaceDialog.showDialog(frame, false);// replace
		}
		////////////////////////////////////
		else if (cmdText.equals(editGoTo)) {
			logger.info("Open goTo window");
			if (taExpr.getText().length() == 0)
				return; // text box have no text
			GoToDialog.goTo(npd);
		}
		////////////////////////////////////
		else if (cmdText.equals(editSelectAll)) {
			logger.info("Select all");
			taExpr.selectAll();
		}
		////////////////////////////////////
		else if (cmdText.equals(editTimeDate)) {
			logger.info("Time date paste");
			taExpr.insert(new Date().toString(), taExpr.getSelectionStart());
		}
		////////////////////////////////////
		else if (cmdText.equals(formatWordWrap) || evObj == wrapButton) {
			// JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			// ta.setLineWrap(temp.isSelected());
			logger.info("Change WordWrap");
			JCheckBoxMenuItem temp;
			if (evObj instanceof JCheckBoxMenuItem) {
				temp = (JCheckBoxMenuItem) evObj;
				taExpr.setLineWrap(temp.isSelected());
				wrapButton.setSelected(temp.isSelected());
			} else {
				boolean isSelected = wrapButton.isSelected();
				wordWrapItem.setSelected(!isSelected);
				wrapButton.setSelected(!isSelected);
				taExpr.setLineWrap(!isSelected);
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(formatFont) || evObj == fontButton) {
			logger.info("Open Font window");
			font = FontChooser.showDialog(frame, "Font settings", true, taExpr.getFont());
			taExpr.setFont(font);
		}
		////////////////////////////////////
		else if (cmdText.equals(formatForeground)) {
			logger.info("Open TextColor window");
			showForegroundColorDialog();
		}
		////////////////////////////////////
		else if (cmdText.equals(formatBackground)) {
			logger.info("Open PadColor window");
			showBackgroundColorDialog();
		}
		////////////////////////////////////
		else if (cmdText.equals(formatKeyword)) {
			logger.info("Open KeywordColor window");
			showKeywordColorDialog();
		}
		////////////////////////////////////
		else if (cmdText.equals(formatComment)) {
			logger.info("Open CommentColor window");
			showCommentColorDialog();
		}
		////////////////////////////////////
		else if (cmdText.equals(formatString)) {
			logger.info("Open StringColor window");
			showStringColorDialog();
		}

		////////////////////////////////////
		else if (cmdText.equals(viewParserPanel)) {
			logger.info("Change ParserPanel view");
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			if (temp.isSelected()) {
				splitPane.setRightComponent(rightPanel);
			} else {
				splitPane.remove(rightPanel);
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(viewStatusBar)) {
			logger.info("Change StatusBar view");
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			statusBar.setVisible(temp.isSelected());
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomIn) || evObj == zoomInButton) {
			logger.info("Change zoom: zoom in");
			taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() + 1));
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomOut) || evObj == zoomOutButton) {
			logger.info("Change zoom: zoom out");
			if (taExpr.getFont().getSize2D() > 1) {
				taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() - 1));
			}
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomDefault) || evObj == zoomDefaultButton) {
			logger.info("Change zoom: zoom default");
			taExpr.setFont(font);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpHome)) {
			logger.info("Open Tpad Home");
			try {
				Desktop.getDesktop().browse(new URI("http://tpad.hak.pp.ua/"));
			} catch (IOException e1) {
				logger.info(e1.getMessage());
			} catch (URISyntaxException e1) {
				logger.info(e1.getMessage());
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(helpKeyboardShortcuts) || evObj == shortcutsButton) {
			logger.info("Open KeyboardShortcuts window");
			KeyboardShortcutsDialog.showKeyboardShortcuts(frame);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpLegacyInfo)) {
			logger.info("Open LegacyInfo window");
			LegacyInfoDialog.showLegacyInfo(frame);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpAttributeInfo)) {
			logger.info("Open AttributeInfo window");
			AttributeInfoDialog.showAttributeInfo(frame);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpTpsInfo)) {
			logger.info("Open TpsInfo window");
//			TpsInfo.show(this);
			LoadingPanel.doProcess("tps", npd);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpAbout) || evObj == aboutButton) {
			logger.info("Open About window");
			AboutDialog.showAbout(frame);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpCheckUpdates)) {
			settingsHandler.saveSettings();
			// just for getting backup
			fileHandler.saveTempPadText();

			if (fileHandler.confirmSave()) {
				logger.info("Checking updates...");
				try {
					Version currentVersion = new Version(applicationVersion);
					Updater.start(currentVersion);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(helpHelpTopic) || evObj == helpButton) {
			logger.info("Open HelpTopic window");
			HelpTopicDialog.showHelpTopic(frame);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpResetSettings)) {
			logger.info("Try reset settings");
			settingsHandler.resetSettings();
		}
		////////////////////////////////////
		else if (cmdText.equals(expressionCheck) || evObj == checkButton) {
			LoadingPanel.doProcess("check", npd);
			if (!parserPanelItem.isSelected()) {
				parserPanelItem.doClick();
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(expressionParse) || evObj == parseButton) {
			// try {
			// Desktop.getDesktop().browse(new
			// URI("http://templex.cnetcontent.com/Home/Parser"));
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// } catch (URISyntaxException e1) {
			// e1.printStackTrace();
			// }

			LoadingPanel.doProcess("parse", npd);
			if (!parserPanelItem.isSelected()) {
				parserPanelItem.doClick();
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(expressionParseSkuList)) {
			logger.info("Open ParseSkuList window");
			LoadingPanel.doProcess("parse-sku-list", npd);
		}
		////////////////////////////////////
		else {
			logger.info("This " + cmdText + " command is yet to be implemented");
			statusBar.setText("This " + cmdText + " command is yet to be implemented");
		}
	}
	// action Performed
	////////////////////////////////////

	void showKeywordColorDialog() {
		if (keywordColorChooser == null)
			keywordColorChooser = new JColorChooser();
		if (keywordDialog == null)
			keywordDialog = JColorChooser.createDialog(frame, formatKeyword, false, keywordColorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							Color col = keywordColorChooser.getColor();
							scheme.getStyle(Token.RESERVED_WORD).foreground = col;
							scheme.getStyle(Token.DATA_TYPE).foreground = col;
							taExpr.revalidate();
							taExpr.repaint();
						}
					}, null);

		keywordDialog.setVisible(true);
	}

	void showCommentColorDialog() {
		if (commentColorChooser == null)
			commentColorChooser = new JColorChooser();
		if (commentDialog == null)
			commentDialog = JColorChooser.createDialog(frame, formatComment, false, commentColorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							Color col = commentColorChooser.getColor();
							scheme.getStyle(Token.COMMENT_EOL).foreground = col;
							taExpr.revalidate();
							taExpr.repaint();
						}
					}, null);

		commentDialog.setVisible(true);
	}

	void showStringColorDialog() {
		if (stringColorChooser == null)
			stringColorChooser = new JColorChooser();
		if (stringDialog == null)
			stringDialog = JColorChooser.createDialog(frame, formatKeyword, false, stringColorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							Color col = stringColorChooser.getColor();
							scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = col;
							taExpr.revalidate();
							taExpr.repaint();
						}
					}, null);

		stringDialog.setVisible(true);
	}

	void showBackgroundColorDialog() {
		if (bcolorChooser == null)
			bcolorChooser = new JColorChooser();
		if (backgroundDialog == null)
			backgroundDialog = JColorChooser.createDialog(frame, formatBackground, false, bcolorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							taExpr.setBackground(bcolorChooser.getColor());
						}
					}, null);

		backgroundDialog.setVisible(true);
	}

	////////////////////////////////////
	void showForegroundColorDialog() {
		if (fcolorChooser == null)
			fcolorChooser = new JColorChooser();
		if (foregroundDialog == null)
			foregroundDialog = JColorChooser.createDialog(frame, formatForeground, false, fcolorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							taExpr.setForeground(fcolorChooser.getColor());
						}
					}, null);

		foregroundDialog.setVisible(true);
	}

	///////////////////////////////////
	JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
		JMenuItem temp = new JMenuItem(s, key);
		temp.addActionListener(al);
		toMenu.add(temp);

		return temp;
	}

	////////////////////////////////////
	JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener al) {
		JMenuItem temp = new JMenuItem(s, key);
		temp.addActionListener(al);
		temp.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
		toMenu.add(temp);

		return temp;
	}

	////////////////////////////////////
	JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, int modifier, ActionListener al) {
		JMenuItem temp = new JMenuItem(s, key);
		temp.addActionListener(al);
		temp.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK | modifier));
		toMenu.add(temp);

		return temp;
	}

	////////////////////////////////////
	JCheckBoxMenuItem createCheckBoxMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
		JCheckBoxMenuItem temp = new JCheckBoxMenuItem(s);
		temp.setMnemonic(key);
		temp.addActionListener(al);
		temp.setSelected(false);
		toMenu.add(temp);

		return temp;
	}

	////////////////////////////////////
	JMenu createMenu(String s, int key, JMenuBar toMenuBar) {
		JMenu temp = new JMenu(s);
		temp.setMnemonic(key);
		toMenuBar.add(temp);
		return temp;
	}

	////////////////////////////////////
	JButton createButton(String imgResourse, String toolTipText, boolean isFocusable, ActionListener al) {
		JButton temp = new JButton(new ImageIcon(frame.getClass().getResource(imgResourse)));
		temp.setToolTipText(toolTipText);
		temp.addActionListener(al);
		temp.setFocusable(isFocusable);
		return temp;
	}

	/*********************************/
	void createMenuBar() {
		JMenuBar mb = new JMenuBar();
		// JMenuItem temp;

		JMenu fileMenu = createMenu(fileText, KeyEvent.VK_F, mb);
		JMenu editMenu = createMenu(editText, KeyEvent.VK_E, mb);
		JMenu formatMenu = createMenu(formatText, KeyEvent.VK_O, mb);
		JMenu viewMenu = createMenu(viewText, KeyEvent.VK_V, mb);
		JMenu expressionMenu = createMenu(expressionText, KeyEvent.VK_X, mb);
		JMenu helpMenu = createMenu(helpText, KeyEvent.VK_H, mb);

		createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
		createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
		createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
		createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, KeyEvent.VK_S, KeyEvent.SHIFT_MASK, this);
		fileMenu.addSeparator();
		// temp = createMenuItem(filePageSetup, KeyEvent.VK_U, fileMenu, this);
		// temp.setEnabled(false);
		// temp = createMenuItem(filePrint, KeyEvent.VK_P, fileMenu,
		// KeyEvent.VK_P, this);
		// temp.setEnabled(false);
		// fileMenu.addSeparator();
		createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);

		undoItem = createMenuItem(editUndo, KeyEvent.VK_U, editMenu, KeyEvent.VK_Z, undoAction);
		redoItem = createMenuItem(editRedo, KeyEvent.VK_O, editMenu, KeyEvent.VK_Y, redoAction);
		editMenu.addSeparator();
		cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
		copyItem = createMenuItem(editCopy, KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
		createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
		deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, this);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenu.addSeparator();
		findItem = createMenuItem(editFind, KeyEvent.VK_F, editMenu, KeyEvent.VK_F, this);
		findNextItem = createMenuItem(editFindNext, KeyEvent.VK_N, editMenu, this);
		findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		replaceItem = createMenuItem(editReplace, KeyEvent.VK_R, editMenu, KeyEvent.VK_H, this);
		gotoItem = createMenuItem(editGoTo, KeyEvent.VK_G, editMenu, KeyEvent.VK_G, this);
		editMenu.addSeparator();
		selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
		createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this)
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		wordWrapItem = createCheckBoxMenuItem(formatWordWrap, KeyEvent.VK_W, formatMenu, this);
		wordWrapItem.setSelected(true);
		taExpr.setLineWrap(true);

		createMenuItem(formatFont, KeyEvent.VK_F, formatMenu, KeyEvent.VK_T, this);
		formatMenu.addSeparator();
		createMenuItem(formatForeground, KeyEvent.VK_T, formatMenu, this);
		createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);
		createMenuItem(formatKeyword, KeyEvent.VK_K, formatMenu, this);
		createMenuItem(formatComment, KeyEvent.VK_C, formatMenu, this);
		createMenuItem(formatString, KeyEvent.VK_S, formatMenu, this);

		parserPanelItem = createCheckBoxMenuItem(viewParserPanel, KeyEvent.VK_P, viewMenu, this);
		parserPanelItem.setSelected(true);
		// Ctrl + Shift + P shortcut
		// parserPanelItem.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_P,
		// ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)));
		statusBarItem = createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this);
		statusBarItem.setSelected(true);
		/************
		 * For Look and Feel, May not work properly on different operating
		 * environment
		 ***/
		// LookAndFeelMenu.createLookAndFeelMenuItem(viewMenu, f);
		{
			JMenu tmp = new JMenu("Zoom");
			tmp.setMnemonic('Z');
			createMenuItem(viewZoomIn, KeyEvent.VK_I, tmp, KeyEvent.VK_ADD, this);
			createMenuItem(viewZoomOut, KeyEvent.VK_O, tmp, KeyEvent.VK_SUBTRACT, this);
			createMenuItem(viewZoomDefault, KeyEvent.VK_O, tmp, KeyEvent.VK_0, this);
			viewMenu.add(tmp);
		}

		createMenuItem(expressionCheck, KeyEvent.VK_C, expressionMenu, KeyEvent.VK_C, KeyEvent.SHIFT_MASK, this);
		createMenuItem(expressionParse, KeyEvent.VK_P, expressionMenu, KeyEvent.VK_P, KeyEvent.SHIFT_MASK, this);
		createMenuItem(expressionParseSkuList, KeyEvent.VK_S, expressionMenu, KeyEvent.VK_P,
				KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK, this);

		createMenuItem(helpHome, KeyEvent.VK_T, helpMenu, this);
		helpMenu.addSeparator();
		createMenuItem(helpKeyboardShortcuts, KeyEvent.VK_K, helpMenu, this);
		createMenuItem(helpLegacyInfo, KeyEvent.VK_L, helpMenu, KeyEvent.VK_L, this);
		createMenuItem(helpAttributeInfo, KeyEvent.VK_I, helpMenu, KeyEvent.VK_I, this);
		createMenuItem(helpTpsInfo, KeyEvent.VK_P, helpMenu, KeyEvent.VK_P, this);
		createMenuItem(helpHelpTopic, KeyEvent.VK_H, helpMenu, this);
		helpMenu.addSeparator();
		createMenuItem(helpResetSettings, KeyEvent.VK_R, helpMenu, this);
		helpMenu.addSeparator();
		createMenuItem(helpCheckUpdates, KeyEvent.VK_U, helpMenu, this);
		createMenuItem(helpAbout, KeyEvent.VK_A, helpMenu, this)
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		MenuListener editMenuListener = new MenuListener() {
			public void menuSelected(MenuEvent evvvv) {
				if (taExpr.getText().length() == 0) {
					findItem.setEnabled(false);
					findNextItem.setEnabled(false);
					replaceItem.setEnabled(false);
					selectAllItem.setEnabled(false);
					gotoItem.setEnabled(false);
				} else {
					findItem.setEnabled(true);
					findNextItem.setEnabled(true);
					replaceItem.setEnabled(true);
					selectAllItem.setEnabled(true);
					gotoItem.setEnabled(true);
				}
				if (taExpr.getSelectionStart() == taExpr.getSelectionEnd()) {
					cutItem.setEnabled(false);
					copyItem.setEnabled(false);
					deleteItem.setEnabled(false);
				} else {
					cutItem.setEnabled(true);
					copyItem.setEnabled(true);
					deleteItem.setEnabled(true);
				}

				undoItem.setEnabled(taExpr.canUndo());
				redoItem.setEnabled(taExpr.canRedo());
			}

			public void menuDeselected(MenuEvent evvvv) {
			}

			public void menuCanceled(MenuEvent evvvv) {
			}
		};
		editMenu.addMenuListener(editMenuListener);
		frame.setJMenuBar(mb);
	}

	private void createToolBar() {
		newButton = createButton(imgNewLocation, fileNew, false, this);
		openButton = createButton(imgOpenLocation, fileOpen, false, this);
		saveButton = createButton(imgSaveLocation, fileSave, false, this);

		checkButton = createButton(imgCheckLocation, "Check", false, this);
		parseButton = createButton(imgParseLocation, "Parse", false, this);
		undoButton = createButton(imgUndoLocation, editUndo, false, undoAction);
		undoButton.setEnabled(true);
		redoButton = createButton(imgRedoLocation, editRedo, false, redoAction);
		redoButton.setEnabled(true);
		copyButton = createButton(imgCopyLocation, editCopy, false, this);
		copyButton.setEnabled(false);
		cutButton = createButton(imgCutLocation, editCut, false, this);
		cutButton.setEnabled(false);
		pasteButton = createButton(imgPasteLocation, editPaste, false, this);
		findButton = createButton(imgFindLocation, editFind, false, this);
		findButton.setEnabled(false);
		replaceButton = createButton(imgReplaceLocation, editReplace, false, this);
		replaceButton.setEnabled(false);
		zoomInButton = createButton(imgZoomInLocation, "Zoom In (Ctrl + Mouse Wheel Up)", false, this);
		zoomOutButton = createButton(imgZoomOutLocation, "Zoom Out (Ctrl + Mouse Wheel Down)", false, this);
		zoomDefaultButton = createButton(imgZoomDefaultLocation, "Zoom Default (Ctrl + 0)", false, this);
		fontButton = createButton(imgFontLocation, formatFont, false, this);
		wrapButton = createButton(imgWrapLocation, formatWordWrap, false, this);
		wrapButton.setSelected(true);
		helpButton = createButton(imgHelpLocation, helpHelpTopic, false, this);
		aboutButton = createButton(imgInfoLocation, helpAbout, false, this);
		shortcutsButton = createButton(imgKeyboardLocation, helpKeyboardShortcuts, false, this);

		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.addSeparator();
		toolBar.add(checkButton);
		toolBar.add(parseButton);
		toolBar.addSeparator();
		toolBar.add(undoButton);
		toolBar.add(redoButton);
		toolBar.addSeparator();
		toolBar.add(cutButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.addSeparator();
		toolBar.add(findButton);
		toolBar.add(replaceButton);
		toolBar.addSeparator();
		toolBar.add(zoomInButton);
		toolBar.add(zoomOutButton);
		toolBar.add(zoomDefaultButton);
		toolBar.addSeparator();
		toolBar.add(fontButton);
		toolBar.add(wrapButton);
		toolBar.addSeparator();
		toolBar.add(shortcutsButton);
		toolBar.add(helpButton);
		toolBar.add(aboutButton);
	}

	public static void main(String[] s) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Notepad();
			}
		});
	}
}
