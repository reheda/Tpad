package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
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
import javax.swing.undo.UndoManager;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import ua.pp.hak.isnotused.LinePainter;
import ua.pp.hak.util.FileOperation;

public class Notepad implements ActionListener, MenuConstants, Constants {

	private JFrame f;
	private RSyntaxTextArea ta;

	JTextArea tp;
	private JLabel statusBar;
	JScrollPane taScrollPane;
	JScrollPane tpScrollPane;
	JToolBar toolBar;
	static String build = "x";

	String searchString, replaceString;
	int lastSearchIndex;
	private Font font = new Font("Consolas", Font.PLAIN, 14);
	FileOperation fileHandler;
	FontChooser fontDialog = null;
	FindDialog findReplaceDialog = null;
	JColorChooser bcolorChooser = null;
	JColorChooser fcolorChooser = null;
	JDialog backgroundDialog = null;
	JDialog foregroundDialog = null;
	JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem, undoItem,
			redoItem;
	JCheckBoxMenuItem wordWrapItem;
	private UndoManager manager;
	RedoAction redoAction;
	UndoAction undoAction;
	Highlighter highlighter;
	LinePainter painter;
	JSplitPane splitPane;
	JPanel panel;

	JButton newButton, openButton, saveButton, runButton, lexButton, parseButton;
	private JButton undoButton;
	private JButton redoButton;
	JButton copyButton;
	JButton cutButton;
	JButton pasteButton;
	JButton findButton;
	JButton replaceButton;
	JButton zoomInButton;
	JButton zoomOutButton;
	JButton zoomDefaultButton;
	JButton fontButton;
	JButton helpButton;
	JButton aboutButton;
	JButton wrapButton;
	JButton shortcutsButton;

	public JFrame getFrame() {
		return f;
	}

	public RSyntaxTextArea getTextArea() {
		return ta;
	}

	public JLabel getStatusBar() {
		return statusBar;
	}

	public UndoManager getManager() {
		return manager;
	}

	public JButton getUndoButton() {
		return undoButton;
	}

	public JButton getRedoButton() {
		return redoButton;
	}

	/****************************/
	Notepad() {
		f = new JFrame(defaultFileName + " - " + applicationName);
		// change icon of the app
		f.setIconImage(new ImageIcon(f.getClass().getResource("/images/templex-big.png")).getImage());
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
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK), "none"); // disable
																						// ctrl+h
		}
		// -------
		ta = new RSyntaxTextArea(30, 60);
		ta.setFont(font);
		ta.setTabSize(4);
		ta.setMargin(new Insets(0, 5, 0, 5));
		ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		ta.setCodeFoldingEnabled(true);
		// -----
		taScrollPane = new JScrollPane(ta);
		TextLineNumber tln = new TextLineNumber(ta);
		tln.setUpdateFont(true); // enables the automatic updating of the Font
									// when the Font of the related text
									// component changes.
		taScrollPane.setRowHeaderView(tln);
		taScrollPane.setPreferredSize(new Dimension(600, 450));
		// -----
		ta.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				int unitsToScroll = e.getUnitsToScroll() * 16;
				if (e.isControlDown()) {
					if (notches < 0) {
						ta.setFont(ta.getFont().deriveFont(ta.getFont().getSize2D() + 1));
					} else {
						ta.setFont(ta.getFont().deriveFont(ta.getFont().getSize2D() - 1));
					}
				} else {
					final JScrollBar bar = taScrollPane.getVerticalScrollBar();
					int currentValue = bar.getValue();
					bar.setValue(currentValue + unitsToScroll);
				}
			}
		});

		Action ctrlPlus = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ta.setFont(ta.getFont().deriveFont(ta.getFont().getSize2D() + 1));
			}
		};
		Action ctrlMinus = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ta.setFont(ta.getFont().deriveFont(ta.getFont().getSize2D() - 1));
			}
		};
		Action ctrlZero = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ta.setFont(font);
			}
		};
		ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK), "ctrlPlus");
		ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "ctrlPlus");
		ta.getActionMap().put("ctrlPlus", ctrlPlus);

		ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK), "ctrlMinus");
		ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "ctrlMinus");
		ta.getActionMap().put("ctrlMinus", ctrlMinus);

		ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.CTRL_DOWN_MASK), "ctrlZero");
		ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), "ctrlZero");
		ta.getActionMap().put("ctrlZero", ctrlZero);
		// -----
		// create console view
		tp = new JTextArea();
		tpScrollPane = new JScrollPane(tp);
		tpScrollPane.setPreferredSize(new Dimension(400, 450));
		tpScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		tp.setFont(font.deriveFont(12f));
		tp.setEditable(false);
		// tp.setBackground(new Color(240,240,240)); //light grey
		tp.setBackground(null);
		tp.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, new Color(0, 188, 57)),
				new EmptyBorder(2, 5, 2, 0))); // green
		tp.setText(
				"Microsoft Bluetooth Mobile Mouse 3600 - Mouse - Bluetooth 4.0 - Dark red\nOutputProcessors=RemarkProcessor+LegacyProductsToTableProcessor+L");
		tp.setText(tp.getText().replaceAll("\n", "//\n"));
		tp.setLineWrap(true);

		// Create the undo manager and actions
		manager = new UndoManager();
		redoAction = new RedoAction(manager);
		undoAction = new UndoAction(manager);
		ta.getDocument().addUndoableEditListener(manager);
		// -----
		statusBar = new JLabel("Line 1, Column 1  ", JLabel.LEFT);
		statusBar.setBorder(new CompoundBorder(statusBar.getBorder(), new EmptyBorder(2, 6, 2, 5)));
		// painter = new LinePainter(ta, new Color(255,255,210));

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.setBorder(new EmptyBorder(0, 10, 0, 0));
		JTextField txtFldSKU = new JTextField(20);
		txtFldSKU.setMaximumSize(new Dimension(4000, 20));
		txtFldSKU.setFont(font.deriveFont(12f));
		txtFldSKU.setText("12345679");

		txtFldSKU.setBorder(new CompoundBorder(txtFldSKU.getBorder(), new EmptyBorder(2, 2, 2, 2)));
		JLabel lblExprResult = new JLabel("Expression result: ");
		JLabel lblExpr = new JLabel("Expression: ");
		JLabel lblParameters = new JLabel("Parameters: ");
		JLabel lblSKU = new JLabel("SKU ID: ");
		JTextArea txtAreaParametrs = new JTextArea();
		txtAreaParametrs.setLineWrap(true);
		txtAreaParametrs.setFont(font.deriveFont(12f));
		txtAreaParametrs.setBorder(new EmptyBorder(3, 3, 3, 3));
		txtAreaParametrs.setText(
				"AlternativeCategoryVersion=16, Evaluate=false, Locale=en-US, ResultSeparator=<>, Verbatim=false, LegacyValues=false");
		JScrollPane scrlTxtAreaParametrs = new JScrollPane(txtAreaParametrs);
		scrlTxtAreaParametrs.setBorder(BorderFactory.createLineBorder(new Color(171, 173, 179))); // grey
		scrlTxtAreaParametrs.setPreferredSize(new Dimension(400, 50));
		scrlTxtAreaParametrs.setMinimumSize(new Dimension(300, 50));

		tpScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtFldSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblExprResult.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblExpr.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblParameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrlTxtAreaParametrs.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(lblExprResult);
		panel.add(tpScrollPane);
		panel.add(new JLabel(" "));

		panel.add(lblParameters);
		panel.add(scrlTxtAreaParametrs);
		panel.add(new JLabel(" "));

		panel.add(lblSKU);
		panel.add(txtFldSKU);

		JPanel pnlExpr = new JPanel();
		pnlExpr.setLayout(new BoxLayout(pnlExpr, BoxLayout.Y_AXIS));
		lblExpr.setAlignmentX(Component.LEFT_ALIGNMENT);
		taScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlExpr.add(lblExpr);
		pnlExpr.add(taScrollPane);
		pnlExpr.setBorder(new EmptyBorder(0, 0, 0, 10));

		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, pnlExpr, panel);
		splitPane.setResizeWeight(1.0);
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
		divider.setBackground(new Color(171, 173, 179));
		divider.setBorder(null);
		// splitPane.setDividerSize(1);
		// Provide minimum sizes for the two components in the split pane.
		Dimension minimumSize = new Dimension(300, 200);
		taScrollPane.setMinimumSize(minimumSize);
		tpScrollPane.setMinimumSize(minimumSize);

		// Provide a preferred size for the split pane.
		// splitPane.setPreferredSize(new Dimension(1000, 450));

		f.getContentPane().add(splitPane, BorderLayout.CENTER); /// asdasd
		// f.getContentPane().add(taScrollPane, BorderLayout.CENTER);
		// f.getContentPane().add(tpScrollPane, BorderLayout.EAST);
		f.getContentPane().add(statusBar, BorderLayout.SOUTH);
		f.getContentPane().add(new JLabel("  "), BorderLayout.EAST);
		f.getContentPane().add(new JLabel("  "), BorderLayout.WEST);
		createMenuBar(f);
		createToolBar();
		f.getContentPane().add(toolBar, BorderLayout.NORTH);
		f.setSize(1000, 450);
		f.pack();
		f.setLocation(100, 50);
		f.setMinimumSize(new Dimension(600, 450));
		f.setVisible(true);
		f.setLocation(150, 50);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		fileHandler = new FileOperation(this);

		/////////////////////

		ta.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {

				buttonStatusChange(); // button enable
				int lineNumber = 0, column = 0, pos = 0, selectedCharsNumber = 0, selectedLinesNumber = 0,
						selectionStartPos = 0, selectionEndPos = 0;

				String text = null, selectedText = null;

				try {
					text = new String(ta.getText());
					selectionStartPos = ta.getSelectionStart();
					selectionEndPos = ta.getSelectionEnd();
					selectedCharsNumber = selectionEndPos - selectionStartPos;
					selectedLinesNumber = ta.getLineOfOffset(selectionEndPos) - ta.getLineOfOffset(selectionStartPos);
					pos = ta.getCaretPosition();
					lineNumber = ta.getLineOfOffset(pos);
					column = pos - ta.getLineStartOffset(lineNumber);
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
						ta.getHighlighter().removeAllHighlights();
						highlighter = null;
						// painter = new LinePainter(ta, new
						// Color(255,255,210)); // restore line painter
					} else {
					}
				} else {
					// set highlighter
					if (!isLetterOrDigit(text, selectionStartPos - 1) && !isLetterOrDigit(text, selectionEndPos)) {
						try {
							highlighter = ta.getHighlighter();
							HighlightPainter wordpainter = new DefaultHighlighter.DefaultHighlightPainter(
									new Color(191, 255, 178)); // light green
							int p0 = 0, p1 = 0;
							do {
								p0 = text.toLowerCase().indexOf(selectedText.toLowerCase(), p1);
								p1 = p0 + selectedText.length();

								if (p0 > -1 && !isLetterOrDigit(text, p0 - 1) && !isLetterOrDigit(text, p1)
										&& p0 != selectionStartPos) {
									highlighter.addHighlight(p0, p1, wordpainter);
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

				if (Notepad.this.ta.getText().length() == 0) {
					findButton.setEnabled(false);
					replaceButton.setEnabled(false);
				} else {
					findButton.setEnabled(true);
					findItem.setEnabled(true);
					replaceButton.setEnabled(true);
					replaceItem.setEnabled(true);
				}
				if (Notepad.this.ta.getSelectionStart() == ta.getSelectionEnd()) {
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
				foo();
			}

			public void removeUpdate(DocumentEvent e) {
				foo();
			}

			public void insertUpdate(DocumentEvent e) {
				foo();
				undoButton.setEnabled(true);
				undoItem.setEnabled(true);
			}

			private void foo() {
				fileHandler.saved = false;
				undoItem.setEnabled(manager.canUndo());
				undoButton.setEnabled(manager.canUndo());
				redoItem.setEnabled(manager.canRedo());
				redoButton.setEnabled(manager.canRedo());
			}
		};
		ta.getDocument().addDocumentListener(myListener);
		/////////
		WindowListener frameClose = new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (fileHandler.confirmSave())
					System.exit(0);
			}
		};
		f.addWindowListener(frameClose);
	}

	public String getBuild() {
		return build;
	}

	////////////////////////////////////
	void goTo() {
		int lineNumber = 0;
		try {
			lineNumber = ta.getLineOfOffset(ta.getCaretPosition()) + 1;
			String tempStr = JOptionPane.showInputDialog(f, "Enter Line Number:", "" + lineNumber);
			if (tempStr == null) {
				return;
			}
			lineNumber = Integer.parseInt(tempStr);
			ta.setCaretPosition(ta.getLineStartOffset(lineNumber - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	///////////////////////////////////
	public void actionPerformed(ActionEvent ev) {
		String cmdText = ev.getActionCommand();
		Object evObj = ev.getSource();
		////////////////////////////////////
		if (cmdText.equals(fileNew) || evObj.equals(newButton))
			fileHandler.newFile();
		else if (cmdText.equals(fileOpen) || evObj.equals(openButton))
			fileHandler.openFile();
		////////////////////////////////////
		else if (cmdText.equals(fileSave) || evObj.equals(saveButton))
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
			JOptionPane.showMessageDialog(Notepad.this.f, "Get ur printer repaired first! It seems u dont have one!",
					"Bad Printer", JOptionPane.INFORMATION_MESSAGE);

		////////////////////////////////////
		else if (cmdText.equals(editCut) || evObj.equals(cutButton))
			ta.cut();
		////////////////////////////////////
		else if (cmdText.equals(editCopy) || evObj.equals(copyButton))
			ta.copy();
		////////////////////////////////////
		else if (cmdText.equals(editPaste) || evObj.equals(pasteButton))
			ta.paste();
		////////////////////////////////////
		else if (cmdText.equals(editDelete))
			ta.replaceSelection("");
		////////////////////////////////////
		else if (cmdText.equals(editFind) || evObj.equals(findButton)) {
			if (Notepad.this.ta.getText().length() == 0)
				return; // text box have no text
			if (findReplaceDialog == null)
				findReplaceDialog = new FindDialog(Notepad.this.ta);
			findReplaceDialog.showDialog(Notepad.this.f, true);// find
		}
		////////////////////////////////////
		else if (cmdText.equals(editFindNext)) {
			if (Notepad.this.ta.getText().length() == 0)
				return; // text box have no text

			if (findReplaceDialog == null)
				statusBar.setText("Nothing to search for, use Find option of Edit Menu first !!!!");
			else
				findReplaceDialog.findNextWithSelection();
		}
		////////////////////////////////////
		else if (cmdText.equals(editReplace) || evObj.equals(replaceButton)) {
			if (Notepad.this.ta.getText().length() == 0)
				return; // text box have no text

			if (findReplaceDialog == null)
				findReplaceDialog = new FindDialog(Notepad.this.ta);
			findReplaceDialog.showDialog(Notepad.this.f, false);// replace
		}
		////////////////////////////////////
		else if (cmdText.equals(editGoTo)) {
			if (Notepad.this.ta.getText().length() == 0)
				return; // text box have no text
			goTo();
		}
		////////////////////////////////////
		else if (cmdText.equals(editSelectAll))
			ta.selectAll();
		////////////////////////////////////
		else if (cmdText.equals(editTimeDate))
			ta.insert(new Date().toString(), ta.getSelectionStart());
		////////////////////////////////////
		else if (cmdText.equals(formatWordWrap) || evObj.equals(wrapButton)) {
			// JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			// ta.setLineWrap(temp.isSelected());
			JCheckBoxMenuItem temp;
			if (evObj instanceof JCheckBoxMenuItem) {
				temp = (JCheckBoxMenuItem) evObj;
				ta.setLineWrap(temp.isSelected());
				wrapButton.setSelected(temp.isSelected());
			} else {
				boolean isSelected = wrapButton.isSelected();
				wordWrapItem.setSelected(!isSelected);
				wrapButton.setSelected(!isSelected);
				ta.setLineWrap(!isSelected);
			}
		}
		////////////////////////////////////
		else if (cmdText.equals(formatFont) || evObj.equals(fontButton)) {
			font = FontChooser.showDialog(Notepad.this.f, "Font settings", true, Notepad.this.ta.getFont());
			Notepad.this.ta.setFont(font);
		}
		////////////////////////////////////
		else if (cmdText.equals(formatForeground))
			showForegroundColorDialog();
		////////////////////////////////////
		else if (cmdText.equals(formatBackground))
			showBackgroundColorDialog();
		////////////////////////////////////

		else if (cmdText.equals(viewStatusBar)) {
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) evObj;
			statusBar.setVisible(temp.isSelected());
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomIn) || evObj.equals(zoomInButton)) {
			ta.setFont(ta.getFont().deriveFont(ta.getFont().getSize2D() + 1));
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomOut) || evObj.equals(zoomOutButton)) {
			ta.setFont(ta.getFont().deriveFont(ta.getFont().getSize2D() - 1));
		}
		////////////////////////////////////

		else if (cmdText.equals(viewZoomDefault) || evObj.equals(zoomDefaultButton)) {
			ta.setFont(font);
		}
		////////////////////////////////////
		else if (cmdText.equals(helpKeyboardShortcuts) || evObj.equals(shortcutsButton)) {
			JTextPane textPane = new JTextPane();
			textPane.setContentType("text/html");
			textPane.setBackground(null);
			textPane.setBorder(null);
			textPane.setText(shortcutsText);
			textPane.setEditable(false);
			JOptionPane.showMessageDialog(Notepad.this.f, textPane, "Keyboard Shortcuts",
					JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(f.getClass().getResource("/images/templex-big.png")));
		}
		////////////////////////////////////
		else if (cmdText.equals(helpAboutNotepad) || evObj.equals(aboutButton)) {
			JTextPane textPane = new JTextPane();
			textPane.setContentType("text/html");
			textPane.setBackground(null);
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
			JOptionPane.showMessageDialog(Notepad.this.f, textPane, "About", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(f.getClass().getResource("/images/templex-big.png")));
		}
		////////////////////////////////////
		else if (cmdText.equals(helpHelpTopic) || evObj.equals(helpButton)) {
			JTextPane textPane = new JTextPane();
			textPane.setContentType("text/html");
			textPane.setBackground(null);
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
			JOptionPane.showMessageDialog(Notepad.this.f, textPane, "Help topic", JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(f.getClass().getResource("/images/templex-big.png")));
		} else
			statusBar.setText("This " + cmdText + " command is yet to be implemented");
	}// action Performed
		////////////////////////////////////

	void showBackgroundColorDialog() {
		if (bcolorChooser == null)
			bcolorChooser = new JColorChooser();
		if (backgroundDialog == null)
			backgroundDialog = JColorChooser.createDialog(Notepad.this.f, formatBackground, false, bcolorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							Notepad.this.ta.setBackground(bcolorChooser.getColor());
						}
					}, null);

		backgroundDialog.setVisible(true);
	}

	////////////////////////////////////
	void showForegroundColorDialog() {
		if (fcolorChooser == null)
			fcolorChooser = new JColorChooser();
		if (foregroundDialog == null)
			foregroundDialog = JColorChooser.createDialog(Notepad.this.f, formatForeground, false, fcolorChooser,
					new ActionListener() {
						public void actionPerformed(ActionEvent evvv) {
							Notepad.this.ta.setForeground(fcolorChooser.getColor());
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
		temp.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK + modifier));
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
		JButton temp = new JButton(new ImageIcon(f.getClass().getResource(imgResourse)));
		temp.setToolTipText(toolTipText);
		temp.addActionListener(al);
		temp.setFocusable(isFocusable);
		return temp;
	}

	/*********************************/
	void createMenuBar(JFrame f) {
		JMenuBar mb = new JMenuBar();
		JMenuItem temp;

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
		temp = createMenuItem(filePageSetup, KeyEvent.VK_U, fileMenu, this);
		temp.setEnabled(false);
		createMenuItem(filePrint, KeyEvent.VK_P, fileMenu, KeyEvent.VK_P, this);
		fileMenu.addSeparator();
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
		ta.setLineWrap(true);
		wordWrapItem.setSelected(true);

		createMenuItem(formatFont, KeyEvent.VK_F, formatMenu, KeyEvent.VK_T, this);
		formatMenu.addSeparator();
		createMenuItem(formatForeground, KeyEvent.VK_T, formatMenu, this);
		createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);

		createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);
		/************
		 * For Look and Feel, May not work properly on different operating
		 * environment
		 ***/
		// LookAndFeelMenu.createLookAndFeelMenuItem(viewMenu, this.f);
		{
			JMenu tmp = new JMenu("Zoom");
			tmp.setMnemonic('Z');
			createMenuItem(viewZoomIn, KeyEvent.VK_I, tmp, KeyEvent.VK_ADD, this);
			createMenuItem(viewZoomOut, KeyEvent.VK_O, tmp, KeyEvent.VK_SUBTRACT, this);
			createMenuItem(viewZoomDefault, KeyEvent.VK_O, tmp, KeyEvent.VK_0, this);
			viewMenu.add(tmp);
		}

		createMenuItem(helpKeyboardShortcuts, KeyEvent.VK_K, helpMenu, this).setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		createMenuItem(helpHelpTopic, KeyEvent.VK_H, helpMenu, this)
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		helpMenu.addSeparator();
		createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this)
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

		MenuListener editMenuListener = new MenuListener() {
			public void menuSelected(MenuEvent evvvv) {
				if (Notepad.this.ta.getText().length() == 0) {
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
				if (Notepad.this.ta.getSelectionStart() == ta.getSelectionEnd()) {
					cutItem.setEnabled(false);
					copyItem.setEnabled(false);
					deleteItem.setEnabled(false);
				} else {
					cutItem.setEnabled(true);
					copyItem.setEnabled(true);
					deleteItem.setEnabled(true);
				}

				undoItem.setEnabled(manager.canUndo());
				redoItem.setEnabled(manager.canRedo());
			}

			public void menuDeselected(MenuEvent evvvv) {
			}

			public void menuCanceled(MenuEvent evvvv) {
			}
		};
		editMenu.addMenuListener(editMenuListener);
		f.setJMenuBar(mb);
	}

	private void createToolBar() {
		newButton = createButton("/images/new.png", fileNew, false, this);
		openButton = createButton("/images/open.png", fileOpen, false, this);
		saveButton = createButton("/images/save.png", fileSave, false, this);
		runButton = createButton("/images/run.png", "Run", false, this);
		undoButton = createButton("/images/undo.png", editUndo, false, undoAction);
		undoButton.setEnabled(false);
		redoButton = createButton("/images/redo.png", editRedo, false, redoAction);
		redoButton.setEnabled(false);
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
		toolBar.add(runButton);
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

	/************* Constructor **************/
	////////////////////////////////////
	public static void main(String[] s) {
		new Notepad();
	}
}
