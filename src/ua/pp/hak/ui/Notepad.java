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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
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
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.update.Updater;
import ua.pp.hak.util.AutoCompleter;
import ua.pp.hak.util.FileOperation;

public class Notepad implements ActionListener, MenuConstants, Constants {

	private JFrame frame;
	private RSyntaxTextArea taExpr;

	private JTextArea taExprRes;
	private JLabel statusBar;
	private JScrollPane spExpr;
	private JScrollPane spExprRes;
	private JToolBar toolBar;
	static String build = "x";

	int lastSearchIndex;
	private Font font = new Font("Consolas", Font.PLAIN, 14);
	private FileOperation fileHandler;
	private FindDialog findReplaceDialog = null;
	private JColorChooser bcolorChooser = null;
	private JColorChooser fcolorChooser = null;
	private JDialog backgroundDialog = null;
	private JDialog foregroundDialog = null;
	private JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem,
			undoItem, redoItem;
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

	/****************************/
	Notepad() {
		frame = new JFrame(defaultFileName + " - " + applicationName);
		// change icon of the app
		frame.setIconImage(new ImageIcon(frame.getClass().getResource("/images/templex-big.png")).getImage());
		// change look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// build
		try {
			Date tmpDate = new Date(Notepad.class.getResource("Notepad.class").openConnection().getLastModified());
			build = new SimpleDateFormat("yyMMdd").format(tmpDate);
		} catch (Exception e) {
			System.out.println("can't find Notepad.class");
			e.printStackTrace();
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
		taExpr.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
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
		tln.setUpdateFont(true); // enables the automatic updating of the Font
									// when the Font of the related text
									// component changes.
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

		// -----
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
		taExprRes.setText(
				"Microsoft Bluetooth Mobile Mouse 3600 - Mouse - Bluetooth 4.0 - Dark red\nOutputProcessors=RemarkProcessor+LegacyProductsToTableProcessor+L");
		taExprRes.setText(taExprRes.getText().replaceAll("\n", "//\n"));
		taExprRes.setLineWrap(true);

		// -----
		statusBar = new JLabel("Line 1, Column 1  ", JLabel.LEFT);
		statusBar.setBorder(new CompoundBorder(statusBar.getBorder(), new EmptyBorder(2, 6, 2, 5)));
		// painter = new LinePainter(ta, new Color(255,255,210));

		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
		JTextField tfSKU = new JTextField(20);
		tfSKU.setMaximumSize(new Dimension(4000, 20));
		tfSKU.setFont(font.deriveFont(12f));
		tfSKU.setText("12345679");

		tfSKU.setBorder(new CompoundBorder(tfSKU.getBorder(), new EmptyBorder(2, 2, 2, 2)));
		JLabel lblExprRes = new JLabel(txtExprRes);
		JLabel lblExpr = new JLabel(txtExpr);
		JLabel lblParameters = new JLabel(txtParameters);
		JLabel lblSKU = new JLabel(txtSKU);
		JTextArea taParameters = new JTextArea();
		taParameters.setLineWrap(true);
		taParameters.setFont(font.deriveFont(12f));
		taParameters.setBorder(new EmptyBorder(3, 3, 3, 3));
		taParameters.setText(
				"AlternativeCategoryVersion=16, Evaluate=false, Locale=en-US, ResultSeparator=<>, Verbatim=false, LegacyValues=false");
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
		// f.setLocation(150, 50);
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
				if (fileHandler.confirmSave())
					System.exit(0);
			}
		};
		frame.addWindowListener(frameClose);
	}

	public String getBuild() {
		return build;
	}

	////////////////////////////////////
	void goTo() {
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
			e.printStackTrace();
		}
	}

	///////////////////////////////////
	public void actionPerformed(ActionEvent ev) {
		String cmdText = ev.getActionCommand();
		Object evObj = ev.getSource();
		////////////////////////////////////
		if (cmdText.equals(fileNew) || evObj == newButton)
			fileHandler.newFile();
		else if (cmdText.equals(fileOpen) || evObj == openButton)
			fileHandler.openFile();
		////////////////////////////////////
		else if (cmdText.equals(fileSave) || evObj == saveButton)
			fileHandler.saveThisFile();
		////////////////////////////////////
		else if (cmdText.equals(fileSaveAs))
			fileHandler.saveAsFile();
		////////////////////////////////////
		else if (cmdText.equals(fileExit)) {
			if (fileHandler.confirmSave())
				System.exit(0);
		}
		////////////////////////////////////
		else if (cmdText.equals(filePrint))
			JOptionPane.showMessageDialog(frame, "Get ur printer repaired first! It seems u dont have one!",
					"Bad Printer", JOptionPane.INFORMATION_MESSAGE);

		////////////////////////////////////
		else if (cmdText.equals(editCut) || evObj == cutButton)
			taExpr.cut();
		////////////////////////////////////
		else if (cmdText.equals(editCopy) || evObj == copyButton)
			taExpr.copy();
		////////////////////////////////////
		else if (cmdText.equals(editPaste) || evObj == pasteButton)
			taExpr.paste();
		////////////////////////////////////
		else if (cmdText.equals(editDelete))
			taExpr.replaceSelection("");
		////////////////////////////////////
		else if (cmdText.equals(editFind) || evObj == findButton) {
			if (taExpr.getText().length() == 0)
				return; // text box have no text
			if (findReplaceDialog == null)
				findReplaceDialog = new FindDialog(taExpr);
			findReplaceDialog.showDialog(frame, true);// find
		}
		////////////////////////////////////
		else if (cmdText.equals(editFindNext)) {
			if (taExpr.getText().length() == 0)
				return; // text box have no text

			if (findReplaceDialog == null)
				statusBar.setText("Nothing to search for, use Find option of Edit Menu first !!!!");
			else
				findReplaceDialog.findNextWithSelection();
		}
		////////////////////////////////////
		else if (cmdText.equals(editReplace) || evObj == replaceButton) {
			if (taExpr.getText().length() == 0)
				return; // text box have no text

			if (findReplaceDialog == null)
				findReplaceDialog = new FindDialog(taExpr);
			findReplaceDialog.showDialog(frame, false);// replace
		}
		////////////////////////////////////
		else if (cmdText.equals(editGoTo)) {
			if (taExpr.getText().length() == 0)
				return; // text box have no text
			goTo();
		}
		////////////////////////////////////
		else if (cmdText.equals(editSelectAll))
			taExpr.selectAll();
		////////////////////////////////////
		else if (cmdText.equals(editTimeDate))
			taExpr.insert(new Date().toString(), taExpr.getSelectionStart());
		////////////////////////////////////
		else if (cmdText.equals(formatWordWrap) || evObj == wrapButton) {
			// JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			// ta.setLineWrap(temp.isSelected());
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
			font = FontChooser.showDialog(frame, "Font settings", true, taExpr.getFont());
			taExpr.setFont(font);
		}
		////////////////////////////////////
		else if (cmdText.equals(formatForeground))
			showForegroundColorDialog();
		////////////////////////////////////
		else if (cmdText.equals(formatBackground))
			showBackgroundColorDialog();

		////////////////////////////////////
		else if (cmdText.equals(viewParserPanel)) {
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			if (temp.isSelected()) {
				splitPane.setRightComponent(rightPanel);
			} else {
				splitPane.remove(rightPanel);
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(viewStatusBar)) {
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			statusBar.setVisible(temp.isSelected());
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomIn) || evObj == zoomInButton) {
			taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() + 1));
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomOut) || evObj == zoomOutButton) {
			taExpr.setFont(taExpr.getFont().deriveFont(taExpr.getFont().getSize2D() - 1));
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomDefault) || evObj == zoomDefaultButton) {
			taExpr.setFont(font);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpKeyboardShortcuts) || evObj == shortcutsButton) {
			JTextPane textPane = new JTextPane();
			textPane.setContentType("text/html");
			textPane.setBackground(null);
			textPane.setOpaque(false);
			textPane.setBorder(null);
			textPane.setText(shortcutsText);
			textPane.setEditable(false);
			JOptionPane.showMessageDialog(frame, textPane, "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(frame.getClass().getResource("/images/templex-big.png")));
		}
		////////////////////////////////////
		else if (cmdText.equals(helpAboutNotepad) || evObj == aboutButton) {
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
						System.out.println(e.getURL());
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
			JOptionPane.showMessageDialog(frame, textPane, "About", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(frame.getClass().getResource("/images/templex-big.png")));
		}
		////////////////////////////////////
		else if (cmdText.equals(helpCheckUpdates)){
			Updater.start(0);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpHelpTopic) || evObj == helpButton) {
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
						System.out.println(e.getURL());
						try {
							// Desktop.getDesktop().mail(new URI(e.getURL() +
							// ""));
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			JOptionPane.showMessageDialog(frame, textPane, "Help topic", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(frame.getClass().getResource("/images/templex-big.png")));
		}
		////////////////////////////////////
		else if (evObj == checkButton) {
			TChecker.check(this);
			if (!parserPanelItem.isSelected()){
				parserPanelItem.doClick();
			}
		}
		////////////////////////////////////
		else
			statusBar.setText("This " + cmdText + " command is yet to be implemented");
	}
	// action Performed
	////////////////////////////////////

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

		Action[] actions = taExpr.getActions();
		int n = actions.length;
		for (int i = 0; i < n; i++) {
			Action a = actions[i];
			if (a.getValue(Action.NAME).equals(RTextAreaEditorKit.rtaUndoAction)) {
				undoAction = a;
			} else if (a.getValue(Action.NAME).equals(RTextAreaEditorKit.rtaRedoAction)) {
				redoAction = a;
			}
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

		parserPanelItem = createCheckBoxMenuItem(viewParserPanel, KeyEvent.VK_P, viewMenu, this);
		parserPanelItem.setSelected(true);
		parserPanelItem.setAccelerator(
				(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)));
		createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);
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

		createMenuItem(helpKeyboardShortcuts, KeyEvent.VK_K, helpMenu, KeyEvent.VK_L, KeyEvent.SHIFT_MASK, this);
		createMenuItem(helpHelpTopic, KeyEvent.VK_H, helpMenu, this);

		helpMenu.addSeparator();
		createMenuItem(helpCheckUpdates, KeyEvent.VK_U, helpMenu, this);
		createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this)
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
		newButton = createButton("/images/new.png", fileNew, false, this);
		openButton = createButton("/images/open.png", fileOpen, false, this);
		saveButton = createButton("/images/save.png", fileSave, false, this);
		
		checkButton = createButton("/images/check.png", "Check", false, this);
		parseButton = createButton("/images/parse.png", "Parse", false, this);
		undoButton = createButton("/images/undo.png", editUndo, false, undoAction);
		undoButton.setEnabled(true);
		redoButton = createButton("/images/redo.png", editRedo, false, redoAction);
		redoButton.setEnabled(true);
		copyButton = createButton("/images/copy.png", editCopy, false, this);
		copyButton.setEnabled(false);
		cutButton = createButton("/images/cut.png", editCut, false, this);
		cutButton.setEnabled(false);
		pasteButton = createButton("/images/paste.png", editPaste, false, this);
		findButton = createButton("/images/find.png", editFind, false, this);
		findButton.setEnabled(false);
		replaceButton = createButton("/images/replace.png", editReplace, false, this);
		replaceButton.setEnabled(false);
		zoomInButton = createButton("/images/zoom-in.png", "Zoom In (Ctrl + Mouse Wheel Up)", false, this);
		zoomOutButton = createButton("/images/zoom-out.png", "Zoom Out (Ctrl + Mouse Wheel Down)", false, this);
		zoomDefaultButton = createButton("/images/zoom-default.png", "Zoom Default (Ctrl + 0)", false, this);
		fontButton = createButton("/images/font.png", formatFont, false, this);
		wrapButton = createButton("/images/wrap.png", formatWordWrap, false, this);
		wrapButton.setSelected(true);
		helpButton = createButton("/images/help.png", helpHelpTopic, false, this);
		aboutButton = createButton("/images/info.png", helpAboutNotepad, false, this);
		shortcutsButton = createButton("/images/keyboard.png", helpKeyboardShortcuts, false, this);
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
