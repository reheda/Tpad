package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
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
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaEditorKit;
import org.fife.ui.rtextarea.RecordableTextAction;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import ua.pp.hak.compiler.Attribute;
import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.compiler.TParser;
import ua.pp.hak.setting.SettingStAXReader;
import ua.pp.hak.setting.SettingStAXWriter;
import ua.pp.hak.setting.Settings;
import ua.pp.hak.update.Updater;
import ua.pp.hak.util.AutoCompleter;
import ua.pp.hak.util.FileOperation;
import ua.pp.hak.util.Legacy;
import ua.pp.hak.util.Version;

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
	private JToolBar toolBar;
	static String build = "x";

	private Font defaultFont = new Font("Consolas", Font.PLAIN, 14);
	private Font font = new Font("Consolas", Font.PLAIN, 14);
	private FileOperation fileHandler;
	private FindDialog findReplaceDialog = null;
	private JColorChooser bcolorChooser = null;
	private JColorChooser fcolorChooser = null;
	private JColorChooser keywordColorChooser = null;
	private JColorChooser commentColorChooser = null;
	private JColorChooser stringColorChooser = null;
	private JDialog backgroundDialog = null;
	private JDialog foregroundDialog = null;
	private JDialog keywordDialog = null;
	private JDialog commentDialog = null;
	private JDialog stringDialog = null;
	private JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem,
			undoItem, redoItem, statusBarItem;
	private JCheckBoxMenuItem wordWrapItem;
	private JCheckBoxMenuItem parserPanelItem;
	private Action redoAction;
	private Action undoAction;
	private Highlighter highlighter;
	private JSplitPane splitPane;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private ArrayList<Object> highlighterTags = new ArrayList<>();

	JButton newButton, openButton, saveButton, parseButton, checkButton;
	private JButton undoButton, redoButton;
	JButton copyButton, cutButton, pasteButton, findButton, replaceButton, zoomInButton, zoomOutButton,
			zoomDefaultButton, fontButton, helpButton, aboutButton, wrapButton, shortcutsButton;

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

		// build
		try {
			Date tmpDate = new Date(Notepad.class.getResource("Notepad.class").openConnection().getLastModified());
			build = new SimpleDateFormat("yyMMdd").format(tmpDate);
		} catch (Exception e) {
			logger.error("can't find Notepad.class");
			logger.error(e.getMessage());
		}

		// ------- disable ctrl+H in textAreas and textFields
		String[] ctrlHmaps = new String[] { "TextArea.focusInputMap", "TextField.focusInputMap" };
		for (int i = 0; i < ctrlHmaps.length; i++) {
			InputMap im = (InputMap) UIManager.get(ctrlHmaps[i]);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK), "none");
		}

		// -------
		taExpr = new RSyntaxTextArea(30, 60);
		taExpr.setFont(font);
		taExpr.setTabSize(4);
		taExpr.setMargin(new Insets(0, 5, 0, 5));

		// ----- change color of Syntax --------------------------
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
		// ----- set syntax lexer --------------------------------
		// taExpr.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("templexLanguage", "ua.pp.hak.ui.TemplexTokenMaker");
		taExpr.setSyntaxEditingStyle("templexLanguage");
		// -------------------------------------------------------
		taExpr.setCodeFoldingEnabled(true);
		// A CompletionProvider is what knows of all possible completions, and
		// analyzes the contents of the text area at the caret position to
		// determine what completion choices should be presented. Most instances
		// of CompletionProvider (such as DefaultCompletionProvider) are
		// designed
		// so that they can be shared among multiple text components.
		CompletionProvider provider = AutoCompleter.createCompletionProvider();

		// An AutoCompletion acts as a "middle-man" between a text component
		// and a CompletionProvider. It manages any options associated with
		// the auto-completion (the popup trigger key, whether to display a
		// documentation window along with completion choices, etc.). Unlike
		// CompletionProviders, instances of AutoCompletion cannot be shared
		// among multiple text components.
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(taExpr);

		// -----
		spExpr = new JScrollPane(taExpr);
		TextLineNumber tln = new TextLineNumber(taExpr);
		// enables the automatic updating of the Font when the Font of the
		// related text component changes.
		tln.setUpdateFont(true);
		spExpr.setRowHeaderView(tln);
		spExpr.setPreferredSize(new Dimension(600, 450));
		// -----
		taExpr.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				if (e.isControlDown()) {
					if (notches < 0) {
						taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() + 1));
					} else {
						taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() - 1));
					}
				} else {
					// int unitsToScroll = e.getUnitsToScroll() * 16;
					// final JScrollBar bar = spExpr.getVerticalScrollBar();
					// int currentValue = bar.getValue();
					// bar.setValue(currentValue + unitsToScroll);
					int delta = 3;
					if (notches < 0) {
						delta = -delta;
					}
					Container parent = taExpr.getParent();
					if (parent instanceof JViewport) {
						JViewport viewport = (JViewport) parent;
						Point p = viewport.getViewPosition();
						p.y += delta * taExpr.getLineHeight();
						if (p.y < 0) {
							p.y = 0;
						} else {
							Rectangle viewRect = viewport.getViewRect();
							int visibleEnd = p.y + viewRect.height;
							if (visibleEnd >= taExpr.getHeight()) {
								p.y = taExpr.getHeight() - viewRect.height;
							}
						}
						viewport.setViewPosition(p);
					}
				}
			}
		});

		Action ctrlPlus = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() + 1));
			}
		};
		Action ctrlMinus = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() - 1));
			}
		};
		Action ctrlZero = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				taExpr.setFont(font);
			}
		};
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK), "ctrlPlus");
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "ctrlPlus");
		taExpr.getActionMap().put("ctrlPlus", ctrlPlus);

		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK), "ctrlMinus");
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "ctrlMinus");
		taExpr.getActionMap().put("ctrlMinus", ctrlMinus);

		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.CTRL_DOWN_MASK), "ctrlZero");
		taExpr.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), "ctrlZero");
		taExpr.getActionMap().put("ctrlZero", ctrlZero);

		// create Expression result view
		taExprRes = new JTextArea();
		spExprRes = new JScrollPane(taExprRes);
		spExprRes.setPreferredSize(new Dimension(400, 450));
		spExprRes.setBorder(new EmptyBorder(0, 0, 0, 0));
		taExprRes.setFont(font.deriveFont(12f));
		taExprRes.setEditable(false);
		// tp.setBackground(new Color(240,240,240)); //light grey
		taExprRes.setBackground(null);
		taExprRes.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, new Color(0, 188, 57)),
				new EmptyBorder(2, 5, 2, 0))); // green
		taExprRes.setText(defaultExpressionResult);
		taExprRes.setText(taExprRes.getText().replaceAll("\n", "//\n"));
		taExprRes.setLineWrap(true);

		statusBar = new JLabel("Line 1, Column 1  ", JLabel.LEFT);
		statusBar.setBorder(new CompoundBorder(statusBar.getBorder(), new EmptyBorder(2, 6, 2, 5)));
		// painter = new LinePainter(ta, new Color(255,255,210));

		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
		tfSKU = new JTextField(20);
		tfSKU.setMaximumSize(new Dimension(4000, 20));
		tfSKU.setFont(font.deriveFont(12f));
		tfSKU.setText(defaultSKU);

		tfSKU.setBorder(new CompoundBorder(tfSKU.getBorder(), new EmptyBorder(2, 2, 2, 2)));
		JLabel lblExprRes = new JLabel(txtExprRes);
		JLabel lblExpr = new JLabel(txtExpr);
		JLabel lblParameters = new JLabel(txtParameters);
		JLabel lblSKU = new JLabel(txtSKU);
		taParameters = new JTextArea();
		taParameters.setLineWrap(true);
		taParameters.setFont(font.deriveFont(12f));
		taParameters.setBorder(new EmptyBorder(3, 3, 3, 3));
		taParameters.setText(defaultParameters);
		JScrollPane spParameters = new JScrollPane(taParameters);
		spParameters.setBorder(BorderFactory.createLineBorder(new Color(171, 173, 179))); // grey
		spParameters.setPreferredSize(new Dimension(400, 50));
		spParameters.setMinimumSize(new Dimension(300, 50));

		spExprRes.setAlignmentX(Component.LEFT_ALIGNMENT);
		tfSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblExprRes.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblExpr.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblParameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
		spParameters.setAlignmentX(Component.LEFT_ALIGNMENT);

		rightPanel.add(lblExprRes);
		rightPanel.add(spExprRes);
		rightPanel.add(new JLabel(" "));

		rightPanel.add(lblParameters);
		rightPanel.add(spParameters);
		rightPanel.add(new JLabel(" "));

		rightPanel.add(lblSKU);
		rightPanel.add(tfSKU);

		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		lblExpr.setAlignmentX(Component.LEFT_ALIGNMENT);
		spExpr.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(lblExpr);
		leftPanel.add(spExpr);
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, rightPanel);
		splitPane.setResizeWeight(1.0);
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
		divider.setBackground(new Color(171, 173, 179));
		divider.setBorder(null);
		// splitPane.setDividerSize(1);
		// Provide minimum sizes for the two components in the split pane.
		Dimension minimumSize = new Dimension(300, 200);
		spExpr.setMinimumSize(minimumSize);
		spExprRes.setMinimumSize(minimumSize);

		// Provide a preferred size for the split pane.
		// splitPane.setPreferredSize(new Dimension(1000, 450));

		frame.getContentPane().add(splitPane, BorderLayout.CENTER); ///
		// f.getContentPane().add(taScrollPane, BorderLayout.CENTER);
		// f.getContentPane().add(tpScrollPane, BorderLayout.EAST);
		frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		frame.getContentPane().add(new JLabel("  "), BorderLayout.EAST);
		frame.getContentPane().add(new JLabel("  "), BorderLayout.WEST);
		createMenuBar();
		createToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		frame.setSize(1000, 450);
		frame.pack();
		frame.setLocation(100, 50);
		frame.setMinimumSize(new Dimension(600, 450));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		fileHandler = new FileOperation(this);

		/////////////////////

		taExpr.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {

				buttonStatusChange(); // button enable
				int lineNumber = 0, column = 0, pos = 0, selectedCharsNumber = 0, selectedLinesNumber = 0,
						selectionStartPos = 0, selectionEndPos = 0;

				String text = null, selectedText = null;

				try {
					text = new String(taExpr.getText());
					selectionStartPos = taExpr.getSelectionStart();
					selectionEndPos = taExpr.getSelectionEnd();
					selectedCharsNumber = selectionEndPos - selectionStartPos;
					selectedLinesNumber = taExpr.getLineOfOffset(selectionEndPos)
							- taExpr.getLineOfOffset(selectionStartPos);
					pos = taExpr.getCaretPosition();
					lineNumber = taExpr.getLineOfOffset(pos);
					column = pos - taExpr.getLineStartOffset(lineNumber);
					selectedText = new String(text.substring(selectionStartPos, selectionEndPos));

				} catch (Exception excp) {
					excp.printStackTrace();
				}
				if (text.length() == 0) {
					lineNumber = 0;
					column = 0;
					selectedCharsNumber = 0;
				}
				if (selectedCharsNumber == 0) {
					statusBar.setText("Line " + (lineNumber + 1) + ", Column " + (column + 1));
					if (highlighter != null) {
						// taExpr.getHighlighter().removeAllHighlights();
						// highlighter.removeAllHighlights();
						for (Object tag : highlighterTags) {
							highlighter.removeHighlight(tag);
						}
						highlighterTags.clear();
						highlighter = null;
						// painter = new LinePainter(ta, new
						// Color(255,255,210)); // restore line painter
					} else {
					}
				} else {
					// set highlighter
					if (!isLetterOrDigit(text, selectionStartPos - 1) && !isLetterOrDigit(text, selectionEndPos)) {
						try {
							highlighter = taExpr.getHighlighter();
							HighlightPainter wordpainter = new DefaultHighlighter.DefaultHighlightPainter(
									new Color(191, 255, 178)); // light green
							int p0 = 0, p1 = 0;
							do {
								p0 = text.toLowerCase().indexOf(selectedText.toLowerCase(), p1);
								p1 = p0 + selectedText.length();

								if (p0 > -1 && !isLetterOrDigit(text, p0 - 1) && !isLetterOrDigit(text, p1)
										&& p0 != selectionStartPos) {
									highlighterTags.add(highlighter.addHighlight(p0, p1, wordpainter));
								}
							} while (p0 > -1);
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}

					// set status
					if (selectedLinesNumber == 0)
						statusBar.setText(selectedCharsNumber + " characters selected");
					else
						statusBar.setText(
								(selectedLinesNumber + 1) + " lines, " + selectedCharsNumber + " characters selected");
				}
			}

			private boolean isLetterOrDigit(String str, int index) {
				if (index < 0 || index >= str.length())
					return false;

				return Character.isLetter(str.charAt(index)) || Character.isDigit(str.charAt(index));
			}

			private void buttonStatusChange() {

				if (taExpr.getText().length() == 0) {
					findButton.setEnabled(false);
					replaceButton.setEnabled(false);
				} else {
					findButton.setEnabled(true);
					findItem.setEnabled(true);
					gotoItem.setEnabled(true);
					replaceButton.setEnabled(true);
					replaceItem.setEnabled(true);
				}
				if (taExpr.getSelectionStart() == taExpr.getSelectionEnd()) {
					cutButton.setEnabled(false);
					copyButton.setEnabled(false);
				} else {
					cutButton.setEnabled(true);
					copyButton.setEnabled(true);
				}
			}
		});
		//////////////////
		DocumentListener myListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fileHandler.saved = false;
			}

			public void removeUpdate(DocumentEvent e) {
				fileHandler.saved = false;
			}

			public void insertUpdate(DocumentEvent e) {
				fileHandler.saved = false;
			}

		};
		taExpr.getDocument().addDocumentListener(myListener);
		/////////
		WindowListener frameClose = new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (fileHandler.confirmSave()) {
					logger.info("Stop working...");

					// save settings to settings.xml
					saveSettings();

					// just for getting backup
					saveTempPadText();

					System.exit(0);
				}
			}
		};
		frame.addWindowListener(frameClose);

		logger.info("Finish creating GUI");

		// read settings.xml file and apply
		readSettings();
	}

	////////////////////////////////////
	private void goTo() {
		int lineNumber = 0;
		try {
			lineNumber = taExpr.getLineOfOffset(taExpr.getCaretPosition()) + 1;
			String tempStr = JOptionPane.showInputDialog(frame, "Enter Line Number:", "" + lineNumber);
			if (tempStr == null) {
				return;
			}
			lineNumber = Integer.parseInt(tempStr);
			if (lineNumber > taExpr.getLineCount()) {
				taExpr.setCaretPosition(taExpr.getLineStartOffset(taExpr.getLineCount() - 1));
				return;
			} else if (lineNumber < 1) {
				taExpr.setCaretPosition(taExpr.getLineStartOffset(0));
				return;
			}
			taExpr.setCaretPosition(taExpr.getLineStartOffset(lineNumber - 1));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void showAttributeInfo() {
		boolean inputAccepted = false;
		while (!inputAccepted) {
			try {
				String tempStr = JOptionPane.showInputDialog(frame, "Enter Attribute ID:", helpAttributeInfo,
						JOptionPane.PLAIN_MESSAGE);
				if (tempStr == null) {
					return;
				}
				int attrId = Integer.parseInt(tempStr);
				List<Attribute> attibutes = TChecker.getAttributes();
				for (Attribute attr : attibutes) {
					if (attr.getId() == attrId) {
						String name = attr.getName();
						String type = attr.getType();
						String comment = "-- " + attrId + " // " + name.replace("\"", "''");

						String text = "<html> <head> <style> table { border-collapse: collapse; } th, td { text-align: left; border-bottom: 1px solid #dddddd; } td.right { border-right: 1px solid #E03134; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; } body {font-family:Segoe UI; font-size:9px; } div {background-color: white; padding:5px; } </style> </head> <body> <div> <table width='500'> "
								+ "<tbody><tr class='header'><td width='90'>Parameter</td><td>Value</td></tr>"
								+ "<tr><td class='right'>ID</td><td>" + attrId + "</td></tr>"
								+ "<tr><td class='right'>Name</td><td>" + name + "</td></tr>"
								+ "<tr><td class='right'>Type</td><td>" + type + "</td></tr> "
								+ "</tbody> </table> </div><br />  "
								+ "<div> <table width='500'> <tbody><tr class='header'><td width='90'>For Comments</td></tr>"
								+ "<tr><td>" + comment + "</td></tr>" + "</tbody> </table> </div> </body></html>";
						JTextPane textPane = new JTextPane();
						textPane.setContentType("text/html");
						textPane.setBackground(null);
						textPane.setOpaque(false);
						textPane.setBorder(null);
						textPane.setText(text);
						textPane.setEditable(false);
						JOptionPane.showMessageDialog(frame, textPane, helpAttributeInfo, JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}

				JOptionPane.showMessageDialog(frame, "Can't find attribute '" + attrId + "'", helpAttributeInfo,
						JOptionPane.INFORMATION_MESSAGE);

			} catch (NumberFormatException e) {

				JOptionPane.showMessageDialog(frame, "Only integer is accepted!", helpAttributeInfo,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	void showLegacyInfo() {
		try {
			String legacyCode = JOptionPane.showInputDialog(frame, "Enter Legacy Code:", helpLegacyInfo,
					JOptionPane.PLAIN_MESSAGE);
			if (legacyCode == null) {
				return;
			}

			legacyCode = legacyCode.toUpperCase();
			String legacyName = Legacy.getLecagyName(legacyCode);

			if (legacyName == null) {
				JOptionPane.showMessageDialog(frame, "Can't find legacy '" + legacyCode + "'", helpLegacyInfo,
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
				saveSettings();
				// just for getting backup
				saveTempPadText();
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
			goTo();
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
			logger.info("Change WrodWrap");
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
			taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() - 1));
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
			showKeyboardShortcuts();
		}
		////////////////////////////////////
		else if (cmdText.equals(helpLegacyInfo)) {
			logger.info("Open LegacyInfo window");
			showLegacyInfo();
		}
		////////////////////////////////////
		else if (cmdText.equals(helpAttributeInfo)) {
			logger.info("Open AttributeInfo window");
			showAttributeInfo();
		}
		////////////////////////////////////
		else if (cmdText.equals(helpAbout) || evObj == aboutButton) {
			logger.info("Open About window");
			showAbout();
		}
		////////////////////////////////////
		else if (cmdText.equals(helpCheckUpdates)) {
			saveSettings();
			// just for getting backup
			saveTempPadText();
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
			showHelpTopic();
		}
		////////////////////////////////////
		else if (cmdText.equals(helpResetSettings)) {
			logger.info("Try reset settings");
			resetSettings();
		}
		////////////////////////////////////
		else if (evObj == checkButton) {
			// taExprRes.setText("Checking...");
			doProcess("check");
			// TChecker.check(this);
			if (!parserPanelItem.isSelected()) {
				parserPanelItem.doClick();
			}
		}
		////////////////////////////////////
		else if (evObj == parseButton) {
			// try {
			// Desktop.getDesktop().browse(new
			// URI("http://templex.cnetcontent.com/Home/Parser"));
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// } catch (URISyntaxException e1) {
			// e1.printStackTrace();
			// }

			// TParser.parse(this);
			// taExprRes.setText("Parsing...");
			doProcess("parse");
			if (!parserPanelItem.isSelected()) {
				parserPanelItem.doClick();
			}
		}
		////////////////////////////////////
		else {
			logger.info("This " + cmdText + " command is yet to be implemented");
			statusBar.setText("This " + cmdText + " command is yet to be implemented");
		}
	}
	// action Performed
	////////////////////////////////////

	private void showKeyboardShortcuts() {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(null);
		textPane.setOpaque(false);
		textPane.setBorder(null);
		textPane.setText(shortcutsText);
		textPane.setEditable(false);
		JOptionPane.showMessageDialog(frame, textPane, helpKeyboardShortcuts, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)));
	}

	private void showAbout() {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(null);
		textPane.setOpaque(false);
		textPane.setBorder(null);
		textPane.setText(aboutText);
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					logger.info(e.getURL());
					try {
						Desktop.getDesktop().mail(new URI(e.getURL() + ""));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JOptionPane.showMessageDialog(frame, textPane, helpAbout, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)));
	}

	private void showHelpTopic() {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(null);
		textPane.setOpaque(false);
		textPane.setBorder(null);
		textPane.setText(quickReferenceText);
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					logger.info(e.getURL());
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JOptionPane.showMessageDialog(frame, textPane, helpHelpTopic, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)));
	}

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
	void saveSettings() {
		logger.info("Try to save settings...");
		Settings settings = new Settings(taExpr.getFont(), taExpr.getBackground(), taExpr.getForeground(),
				scheme.getStyle(Token.RESERVED_WORD).foreground, scheme.getStyle(Token.COMMENT_EOL).foreground,
				scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground, wordWrapItem.isSelected(),
				statusBarItem.isSelected(), parserPanelItem.isSelected());

		SettingStAXWriter.saveSettings(settings);
		logger.info("Settings were saved!");
	}

	private void saveTempPadText() {
		logger.info("Try to save temp file...");
		File temp = new File("temp/temp.txt");
		File parent = temp.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		// FileWriter fout = null;
		Writer fout = null;
		try {
			// fout = new FileWriter(temp);
			fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), encoding));
			taExpr.write(fout); // fout.write(npd.ta.getText()); was
								// changed due
			// to incorrect saving of new line
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		} finally {
			try {
				fout.close();
			} catch (IOException excp) {
				logger.error(excp.getMessage());
			}
		}
		logger.info("Temp file was saved!");
	}

	void readSettings() {
		logger.info("Try to read settings...");
		Settings settings = SettingStAXReader.parseSettings();
		if (settings == null) {
			// file setting is not valid against XSD schema or doesn't exist
			return;
		}
		font = settings.getFont();
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

	void resetSettings() {

		int answer = JOptionPane.showConfirmDialog(frame,
				"Following settings will be reset to default:\n\n" + "    Font: familly, size, style\n"
						+ "    Color: text, pad, keywords, comments, literal strings\n\n" + "Continue?",
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
							new NextOccurrenceAction(RTextAreaEditorKit.rtaNextOccurrenceAction));
				} else if (actionName.equals(RTextAreaEditorKit.rtaPrevOccurrenceAction)) {
					// change action of prev occurrence shortcut (Ctrl+Shift+K)
					am.put(RTextAreaEditorKit.rtaPrevOccurrenceAction,
							new PreviousOccurrenceAction(RTextAreaEditorKit.rtaPrevOccurrenceAction));
				}
			}

			final String rtaCopyLineDownAction = "RTA.CopyLineDownAction";
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, defaultModifier | alt), rtaCopyLineDownAction);
			am.put(rtaCopyLineDownAction, new LineCopyAction(rtaCopyLineDownAction, 1));

			final String rtaCopyLineUpAction = "RTA.CopyLineUpAction";
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, defaultModifier | alt), rtaCopyLineUpAction);
			am.put(rtaCopyLineUpAction, new LineCopyAction(rtaCopyLineUpAction, -1));
		}

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
		taExpr.setLineWrap(true);
		wordWrapItem.setSelected(true);

		createMenuItem(formatFont, KeyEvent.VK_F, formatMenu, KeyEvent.VK_T, this);
		formatMenu.addSeparator();
		createMenuItem(formatForeground, KeyEvent.VK_T, formatMenu, this);
		createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);
		createMenuItem(formatKeyword, KeyEvent.VK_K, formatMenu, this);
		createMenuItem(formatComment, KeyEvent.VK_C, formatMenu, this);
		createMenuItem(formatString, KeyEvent.VK_S, formatMenu, this);

		parserPanelItem = createCheckBoxMenuItem(viewParserPanel, KeyEvent.VK_P, viewMenu, this);
		parserPanelItem.setSelected(true);
		parserPanelItem.setAccelerator(
				(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)));
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

		createMenuItem(helpHome, KeyEvent.VK_T, helpMenu, this);
		helpMenu.addSeparator();
		createMenuItem(helpKeyboardShortcuts, KeyEvent.VK_K, helpMenu, this);
		createMenuItem(helpLegacyInfo, KeyEvent.VK_L, helpMenu, KeyEvent.VK_L, this);
		createMenuItem(helpAttributeInfo, KeyEvent.VK_I, helpMenu, KeyEvent.VK_I, this);
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

	private void doProcess(String processToDo) {
		final JPanel popup = new JPanel(new BorderLayout(5, 5));
		popup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		popup.setBackground(Color.white);
		frame.getLayeredPane().add(popup);

		JLabel label = new JLabel("Processing...");
		popup.add(label, BorderLayout.NORTH);
		JProgressBar pb = new JProgressBar();
		popup.add(pb, BorderLayout.CENTER);
		pb.setBorderPainted(true);
		pb.setMinimum(0);
		pb.setMaximum(100);
		pb.setValue(0);
		// pb.setStringPainted(true);
		pb.setIndeterminate(true);
		JButton cancel = new JButton("Cancel");
		popup.add(cancel, BorderLayout.EAST);
		popup.doLayout();
		Dimension size = popup.getPreferredSize();
		Dimension windowSize = frame.getSize();
		int popupWidth = 300;
		popup.setBounds((windowSize.width - popupWidth) / 2, (windowSize.height - size.height) / 2, popupWidth,
				size.height);
		frame.getLayeredPane().setLayer(popup, JLayeredPane.POPUP_LAYER);
		popup.setVisible(false);

		final SwingWorker<?, ?> worker = new SwingWorker<Void, String>() {
			protected Void doInBackground() throws InterruptedException {
				publish(processToDo);
				if (processToDo.equals("parse")) {
					TParser.parse(npd);
				} else if (processToDo.equals("check")) {
					TChecker.check(npd);
				}
				return null;
			}

			protected void process(List<String> chunks) {
				// invoked by publish()

				String proc = chunks.get(chunks.size() - 1);
				if (proc.equals("parse") || proc.equals("check")) {

					// do something

				}
			}

			protected void done() {
				popup.setVisible(false);
				if (isCancelled()) {
					Color red = new Color(189, 0, 0);
					taExprRes.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, red),
							new EmptyBorder(2, 5, 2, 0)));

					taExprRes.setText("Canceled!");
				}
			}
		};
		worker.execute();
		popup.setVisible(true);

		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worker.cancel(true);
			}
		});
	}

	/**
	 * Action that copies a line up or down. VR edition
	 */
	public static class LineCopyAction extends RecordableTextAction {

		private int copyAmt;

		public LineCopyAction(String name, int copyAmt) {
			super(name);
			this.copyAmt = copyAmt;
		}

		@Override
		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			if (!textArea.isEditable() || !textArea.isEnabled()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				return;
			}
			try {
				int caret = textArea.getCaretPosition();
				Document doc = textArea.getDocument();
				Element root = doc.getDefaultRootElement();
				int line = root.getElementIndex(caret);
				if (copyAmt == -1) {
					copyLineUp(textArea, line);
				} else if (copyAmt == 1) {
					copyLineDown(textArea, line);
				} else {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					return;
				}
			} catch (BadLocationException ble) {
				// Never happens.
				ble.printStackTrace();
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				return;
			}
		}

		@Override
		public final String getMacroID() {
			return getName();
		}

		private void copyLineDown(RTextArea textArea, int line) throws BadLocationException {
			Document doc = textArea.getDocument();
			Element root = doc.getDefaultRootElement();
			Element elem = root.getElement(line);
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();
			int caret = textArea.getCaretPosition();
			int caretOffset = caret - start;
			String text = doc.getText(start, end - start);
			// doc.remove(start, end-start);
			Element elem2 = root.getElement(line); // not "line+1" - removed.
			// int start2 = elem2.getStartOffset();
			int end2 = elem2.getEndOffset();
			doc.insertString(end2, text, null);
			elem = root.getElement(line + 1);
			textArea.setCaretPosition(elem.getStartOffset() + caretOffset);
		}

		private void copyLineUp(RTextArea textArea, int line) throws BadLocationException {
			Document doc = textArea.getDocument();
			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementCount();
			Element elem = root.getElement(line);
			int start = elem.getStartOffset();
			int end = line == lineCount - 1 ? elem.getEndOffset() - 1 : elem.getEndOffset();
			int caret = textArea.getCaretPosition();
			int caretOffset = caret - start;
			String text = doc.getText(start, end - start);
			if (line == lineCount - 1) {
				start--; // Remove previous line's ending \n
			}
			// doc.remove(start, end-start);
			Element elem2 = root.getElement(line);
			int start2 = elem2.getStartOffset();
			// int end2 = elem2.getEndOffset();
			if (line == lineCount - 1) {
				text += '\n';
			}
			doc.insertString(start2, text, null);
			// caretOffset = Math.min(start2+caretOffset, end2-1);
			textArea.setCaretPosition(start2 + caretOffset);
		}

	}

	/**
	 * Selects the next occurrence of the text last selected.
	 */
	public static class NextOccurrenceAction extends RecordableTextAction {

		public NextOccurrenceAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			String selectedText = textArea.getSelectedText();
			if (selectedText == null || selectedText.length() == 0) {
				selectedText = RTextArea.getSelectedOccurrenceText();
				if (selectedText == null || selectedText.length() == 0) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					return;
				}
			}
			SearchContext context = new SearchContext(selectedText);
			if (textArea.getMarkAllOnOccurrenceSearches()) { // VR edition: was
																// if
																// (!textArea.getMarkAllOnOccurrenceSearches())
				context.setMarkAll(false);
			}
			if (!SearchEngine.find(textArea, context).wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			RTextArea.setSelectedOccurrenceText(selectedText);
		}

		@Override
		public final String getMacroID() {
			return getName();
		}

	}

	/**
	 * Select the previous occurrence of the text last selected.
	 */
	public static class PreviousOccurrenceAction extends RecordableTextAction {

		public PreviousOccurrenceAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			String selectedText = textArea.getSelectedText();
			if (selectedText == null || selectedText.length() == 0) {
				selectedText = RTextArea.getSelectedOccurrenceText();
				if (selectedText == null || selectedText.length() == 0) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					return;
				}
			}
			SearchContext context = new SearchContext(selectedText);
			if (textArea.getMarkAllOnOccurrenceSearches()) { // VR edition: was
																// if
																// (!textArea.getMarkAllOnOccurrenceSearches())
																// {
				context.setMarkAll(false);
			}
			context.setSearchForward(false);
			if (!SearchEngine.find(textArea, context).wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			RTextArea.setSelectedOccurrenceText(selectedText);
		}

		@Override
		public final String getMacroID() {
			return getName();
		}

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
