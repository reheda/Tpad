
//package p1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

//import p1.FontChooser;
//import p1.FontDialog;
//import p1.FindDialog;
//import p1.LookAndFeelMenu;
//import p1.MyFileFilter;
/************************************/
class FileOperation {
	Notepad npd;

	final String encoding = "UTF-8";
	boolean saved;
	boolean newFileFlag;
	String fileName;
	String applicationTitle = "Tpad";

	File fileRef;
	JFileChooser chooser;

	/////////////////////////////
	boolean isSave() {
		return saved;
	}

	void setSave(boolean saved) {
		this.saved = saved;
	}

	String getFileName() {
		return new String(fileName);
	}

	void setFileName(String fileName) {
		this.fileName = new String(fileName);
	}

	/////////////////////////
	FileOperation(Notepad npd) {
		this.npd = npd;

		saved = true;
		newFileFlag = true;
		fileName = new String("Untitled");
		fileRef = new File(fileName);
		this.npd.f.setTitle(fileName + " - " + applicationTitle);

		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new MyFileFilter(".java", "Java Source Files(*.java)"));
		chooser.addChoosableFileFilter(new MyFileFilter(".txt", "Text Files(*.txt)"));
		chooser.setCurrentDirectory(new File("."));

	}
	//////////////////////////////////////

	boolean saveFile(File temp) {
		// FileWriter fout = null;
		Writer fout = null;
		try {
			// fout = new FileWriter(temp);
			fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), encoding));
			npd.ta.write(fout); // fout.write(npd.ta.getText()); was changed due
								// to incorrect saving of new line
		} catch (IOException ioe) {
			ioe.printStackTrace();
			updateStatus(temp, false);
			return false;
		} finally {
			try {
				fout.close();
			} catch (IOException excp) {
				excp.printStackTrace();
			}
		}
		updateStatus(temp, true);
		return true;
	}

	////////////////////////
	boolean saveThisFile() {

		if (!newFileFlag) {
			return saveFile(fileRef);
		}

		return saveAsFile();
	}

	////////////////////////////////////
	boolean saveAsFile() {
		File temp = null;
		chooser.setDialogTitle("Save As...");
		chooser.setApproveButtonText("Save Now");
		chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
		chooser.setApproveButtonToolTipText("Click me to save!");
		do {
			if (chooser.showSaveDialog(this.npd.f) != JFileChooser.APPROVE_OPTION)
				return false;
			temp = chooser.getSelectedFile();
			if (!temp.exists())
				break;
			if (JOptionPane.showConfirmDialog(this.npd.f,
					"<html>" + temp.getPath() + " already exists.<br>Do you want to replace it?<html>", "Save As",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				break;
		} while (true);

		return saveFile(temp);
	}

	////////////////////////
	boolean openFile(File temp) {
		FileInputStream fin = null;
		BufferedReader din = null;

		try {
			fin = new FileInputStream(temp);
			din = new BufferedReader(new InputStreamReader(fin, encoding));
			String str = " ";
			while (str != null) {
				str = din.readLine();
				if (str == null)
					break;
				this.npd.ta.append(str + System.getProperty("line.separator"));
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			updateStatus(temp, false);
			return false;
		} finally {
			try {
				din.close();
				fin.close();
			} catch (IOException excp) {
				excp.printStackTrace();
			}
		}
		updateStatus(temp, true);
		// this.npd.ta.setCaretPosition(0);
		return true;
	}

	///////////////////////
	void openFile() {
		if (!confirmSave())
			return;

		chooser.setDialogTitle("Open File...");
		chooser.setApproveButtonText("Open this");
		chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
		chooser.setApproveButtonToolTipText("Click me to open the selected file.!");

		File temp = null;
		do {
			if (chooser.showOpenDialog(this.npd.f) != JFileChooser.APPROVE_OPTION)
				return;
			temp = chooser.getSelectedFile();

			if (temp.exists())
				break;
			else {
				JOptionPane.showMessageDialog(this.npd.f,
						"<html><b>" + temp.getName() + "</b> file not found.<br>"
								+ "Please verify the correct file name was given.<html>",
						"Open", JOptionPane.INFORMATION_MESSAGE);
				continue;
			}
		} while (true);

		this.npd.ta.setText("");

		if (!openFile(temp)) {
			fileName = "Untitled";
			saved = true;
			this.npd.f.setTitle(fileName + " - " + applicationTitle);
		} else {
			this.npd.manager.discardAllEdits();
			this.npd.undoButton.setEnabled(false);
			this.npd.redoButton.setEnabled(false);
		}
		if (!temp.canWrite())
			newFileFlag = true;

	}

	////////////////////////
	void updateStatus(File temp, boolean saved) {
		if (saved) {
			this.saved = true;
			fileName = new String(temp.getName());
			if (!temp.canWrite()) {
				fileName += "(Read only)";
				newFileFlag = true;
			}
			fileRef = temp;
			npd.f.setTitle(fileName + " - " + applicationTitle);
			npd.statusBar.setText("File: " + temp.getPath() + " saved/opened successfully.");
			newFileFlag = false;
		} else {
			npd.statusBar.setText("Failed to save/open: " + temp.getPath());
		}
	}

	///////////////////////
	boolean confirmSave() {
		String strMsg = "<html>The text in the <b>" + fileName + "</b> file has been changed.<br>"
				+ "Do you want to save the changes?<html>";
		if (!saved) {
			int x = JOptionPane.showConfirmDialog(this.npd.f, strMsg, applicationTitle,
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (x == JOptionPane.CANCEL_OPTION)
				return false;
			if (x == JOptionPane.YES_OPTION && !saveAsFile())
				return false;
		}
		return true;
	}

	///////////////////////////////////////
	void newFile() {
		if (!confirmSave())
			return;

		this.npd.ta.setText("");
		fileName = new String("Untitled");
		fileRef = new File(fileName);
		saved = true;
		newFileFlag = true;
		this.npd.f.setTitle(fileName + " - " + applicationTitle);
		this.npd.manager.discardAllEdits();
		this.npd.undoButton.setEnabled(false);
		this.npd.redoButton.setEnabled(false);
	}
	//////////////////////////////////////
}// end defination of class FileOperation

/************************************/
public class Notepad implements ActionListener, MenuConstants {

	JFrame f;
	RSyntaxTextArea ta;
	JTextArea tp;
	JLabel statusBar;
	JScrollPane taScrollPane;
	JScrollPane tpScrollPane;
	JToolBar toolBar;
	static String build = "x";

	private String fileName = "Untitled";
	private boolean saved = true;
	String applicationName = "Notepad VR";

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
	UndoManager manager;
	RedoAction redoAction;
	UndoAction undoAction;
	Highlighter highlighter;
	LinePainter painter;
	JSplitPane splitPane;
	JPanel panel;

	JButton newButton, openButton, saveButton, runButton, lexButton, parseButton, undoButton, redoButton, copyButton,
			cutButton, pasteButton, findButton, replaceButton, zoomInButton, zoomOutButton, zoomDefaultButton,
			fontButton, helpButton, aboutButton, wrapButton, shortcutsButton;

	/****************************/
	Notepad() {
		f = new JFrame(fileName + " - " + applicationName);
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
		txtAreaParametrs.setText("AlternativeCategoryVersion=16, Evaluate=false, Locale=en-US, ResultSeparator=<>");
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
		//////////////////
		/*
		 * ta.append("Hello dear hello hi"); ta.append(
		 * "\nwho are u dear mister hello"); ta.append("\nhello bye hel");
		 * ta.append("\nHello"); ta.append("\nMiss u mister hello hell");
		 * fileHandler.saved=true;
		 */
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

		// toolbar
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

/**************************************/
// public
interface MenuConstants {
	final String fileText = "File";
	final String editText = "Edit";
	final String formatText = "Format";
	final String viewText = "View";
	final String helpText = "Help";

	final String fileNew = "New";
	final String fileOpen = "Open...";
	final String fileSave = "Save";
	final String fileSaveAs = "Save As...";
	final String filePageSetup = "Page Setup...";
	final String filePrint = "Print";
	final String fileExit = "Exit";

	final String editUndo = "Undo";
	final String editRedo = "Redo";
	final String editCut = "Cut";
	final String editCopy = "Copy";
	final String editPaste = "Paste";
	final String editDelete = "Delete";
	final String editFind = "Find...";
	final String editFindNext = "Find Next";
	final String editReplace = "Replace...";
	final String editGoTo = "Go To...";
	final String editSelectAll = "Select All";
	final String editTimeDate = "Time/Date";

	final String formatWordWrap = "Word Wrap";
	final String formatFont = "Font...";
	final String formatForeground = "Set Text color...";
	final String formatBackground = "Set Pad color...";

	final String viewStatusBar = "Status Bar";
	final String viewZoomIn = "Zoom In";
	final String viewZoomOut = "Zoom Out";
	final String viewZoomDefault = "Zoom Default";

	final String helpKeyboardShortcuts = "Keyboard Shortcuts";
	final String helpHelpTopic = "Help Topic";
	final String helpAboutNotepad = "About Notepad";

	final String quickReferenceText = "<html><body style=\"font-family:Segoe UI; font-size:9px\">" + "<div>"
			+ "<big><b>Quick reference</b></big> [<a href=\"http://templex.cnetcontent.com/Reference\">click to see the full reference</a>]"
			+ "<hr />" + "<div style=\"zoom: 83%;border-left: 6px solid red;background-color: white;\">"
			+ "<pre style=\"margin: 5; line-height: 125%\">SKU.ProductType_<span style=\"color: #aa5500\">&quot; is &quot;</span>_SKU.Colors.FlattenWithAnd()<br>"
			+ "                                 .ToLower();<br>" + "                                 <br>"
			+ "IF A[<span style=\"color: #009999\">374</span>].Values.Where(<span style=\"color: #aa5500\">&quot;%sec%lock%&quot;</span>) IS NOT NULL<br>"
			+ "THEN A[<span style=\"color: #009999\">374</span>].Values.Where(<span style=\"color: #aa5500\">&quot;%sec%lock%&quot;</span>).First().Prefix(<span style=\"color: #aa5500\">&quot;Yes - &quot;</span>)<br>"
			+ "ELSE <span style=\"color: #aa5500\">&quot;No security lock slot&quot;</span>;<br>" + "<br>"
			+ "A[<span style=\"color: #009999\">5791</span>].Where(<span style=\"color: #aa5500\">&quot;%USB%&quot;</span>).Match(<span style=\"color: #009999\">5792</span>, <span style=\"color: #009999\">5791</span>).Values<br>"
			+ "                                        .UseSeparators(<span style=\"color: #aa5500\">&quot; x &quot;</span>)<br>"
			+ "                                        .Flatten(<span style=\"color: #aa5500\">&quot; | &quot;</span>);<br>"
			+ "                                               <br>"
			+ "A[<span style=\"color: #009999\">5791</span>].Where(<span style=\"color: #aa5500\">&quot;%USB%&quot;</span>).Match(<span style=\"color: #009999\">5792</span>).Total;<br>"
			+ "Coalesce(A[<span style=\"color: #009999\">5796</span>].Value, A[<span style=\"color: #009999\">109</span>].Value, <span style=\"color: #aa5500\">&quot;No&quot;</span>);<br>"
			+ "<br>" + "CASE A[<span style=\"color: #009999\">5784</span>].Value<br>"
			+ "   WHEN <span style=\"color: #aa5500\">&quot;LED&quot;</span> THEN <span style=\"color: #aa5500\">&quot;LED display&quot;</span><br>"
			+ "   WHEN <span style=\"color: #aa5500\">&quot;LCD&quot;</span> THEN <span style=\"color: #aa5500\">&quot;LCD display&quot;</span><br>"
			+ "   ELSE <span style=\"color: #aa5500\">&quot;Other&quot;</span><br>" + "END;</pre>" + "</div>"
			+ "<footer>Quick reference provided by&nbsp;<cite title=\"Asya Kasidova\">Asya</cite></footer><br>"
			+ "</div>" + "</body></html>";

	final String aboutText = "<html><body style=\"font-family:Segoe UI; font-size:9px\"><big><b>Tpad</b><i> beta</i></big><br>"
			+ "v 1.0.0." + Notepad.build + " <hr>"
			+ "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS<br>"
			+ "\"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT<br>"
			+ "LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR<br>"
			+ "A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT<br>"
			+ "OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,<br>"
			+ "SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT<br>"
			+ "LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,<br>"
			+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY<br>"
			+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT<br>"
			+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE<br>"
			+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.<br><br>"
			+ "Your comments as well as bug reports are very welcome at:<br><span style=\"background-color:#ffffcc;font-size:11px;\"><b><a href='mailto:valerii.reheda@gmail.com' style='color: black; text-decoration: none;'>valerii.reheda@gmail.com</a></b></span>"
			+ "</body></html>";
	final String shortcutsText = "<html><head><style>" + "span.keyboard {" + "    background: #3d3c40;"
			+ "    font-weight: 700;" + "    padding: 2px .35rem;" + "    font-size: .8rem;" + "    margin: 0 2px;"
			+ "    border-radius: .25rem;" + "    color: #ffffff;" + "    border-bottom: 2px solid #9e9ea6;"
			+ "    box-shadow: 0 1px 2px rgba(0,0,0,.5);" + "    text-shadow: none;" + "}" + "table {"
			+ "    border-collapse: collapse;" + "}" + "th, td {" + "    text-align: left;"
			+ "    border-bottom: 1px solid #dddddd;" + "}" + "td.r {" + "    text-align: left;"
			+ "    border-bottom: 1px solid #dddddd;" + "    border-right: 1px solid #E03134;" + "}"
			+ "tr:hover{background-color:#f5f5f5}" + "</style></head>"
			+ "<body style=\"font-family:Segoe UI; font-size:9px\">"
			+ "<div style=\"background-color: white; padding:5px;\">" + "<table width='682'>" + "<tbody>"
			+ "<tr style='background-color: #E03134; color: white; font-weight: bold;'>" + "<td width='203'>Action</td>"
			+ "<td width='131'>Shortcuts</td>" + "<td width='203'>Action</td>" + "<td width='131'>Shortcuts</td>"
			+ "</tr>" + "<tr>" + "<td>Copy Lines</td>" + "<td class='r'>Ctrl+Alt+Down</td>" + "<td>Next Word</td>"
			+ "<td>Ctrl+Right</td>" + "</tr>" + "<tr>" + "<td>Delete Line</td>" + "<td class='r'>Ctrl+D</td>"
			+ "<td>Previous Word</td>" + "<td>Ctrl+Left</td>" + "</tr>" + "<tr>" + "<td>Delete Next Word</td>"
			+ "<td class='r'>Ctrl+Delete</td>" + "<td>Reset Command</td>" + "<td>Ctrl+0</td>" + "</tr>" + "<tr>"
			+ "<td>Delete Previous Word</td>" + "<td class='r'>Ctrl+Backspace</td>" + "<td>Scroll Line Down</td>"
			+ "<td>Ctrl+Down</td>" + "</tr>" + "<tr>" + "<td>Delete to End of Line</td>"
			+ "<td class='r'>Ctrl+Shift+Delete</td>" + "<td>Scroll Line Up</td>" + "<td>Ctrl+Up</td>" + "</tr>" + "<tr>"
			+ "<td>Duplicate Lines</td>" + "<td class='r'>Ctrl+Alt+Up</td>" + "<td>Select Line End</td>"
			+ "<td>Shift+End</td>" + "</tr>" + "<tr>" + "<td>Expand</td>" + "<td class='r'>Ctrl+Numpad_Add</td>"
			+ "<td>Select Line Start</td>" + "<td>Shift+Home</td>" + "</tr>" + "<tr>" + "<td>Find Next</td>"
			+ "<td class='r'>Ctrl+K</td>" + "<td>Select Next Word</td>" + "<td>Ctrl+Shift+Right</td>" + "</tr>" + "<tr>"
			+ "<td>Find Previous</td>" + "<td class='r'>Ctrl+Shift+K</td>" + "<td>Select Previous Word</td>"
			+ "<td>Ctrl+Shift+Left</td>" + "</tr>" + "<tr>" + "<td>Go to Line</td>" + "<td class='r'>Ctrl+G</td>"
			+ "<td>Text End</td>" + "<td>Ctrl+End</td>" + "</tr>" + "<tr>" + "<td>Incremental Find Reverse</td>"
			+ "<td class='r'>Ctrl+Shift+J</td>" + "<td>Text Start</td>" + "<td>Ctrl+Home</td>" + "</tr>" + "<tr>"
			+ "<td>Insert Line Above Current Line</td>" + "<td class='r'>Ctrl+Shift+Enter</td>"
			+ "<td>To Lower Case</td>" + "<td>Ctrl+Shift+Y</td>" + "</tr>" + "<tr>"
			+ "<td>Insert Line Below Current Line</td>" + "<td class='r'>Shift+Enter</td>" + "<td>To Upper Case</td>"
			+ "<td>Ctrl+Shift+X</td>" + "</tr>" + "<tr>" + "<td>Join Lines</td>" + "<td class='r'>Ctrl+J</td>"
			+ "<td>Toggle Overwrite</td>" + "<td>Insert</td>" + "</tr>" + "<tr>" + "<td>Line End</td>"
			+ "<td class='r'>End</td>" + "<td>Word Completion</td>" + "<td>Alt+/</td>" + "</tr>" + "<tr>"
			+ "<td>Line Start</td>" + "<td class='r'>Home</td>" + "<td>zoomIn</td>" + "<td>Ctrl+=</td>" + "</tr>"
			+ "<tr>" + "<td>Move Lines Down</td>" + "<td class='r'>Alt+Down</td>" + "<td>zoomOut</td>"
			+ "<td>Ctrl+-</td>" + "</tr>" + "<tr>" + "<td>Move Lines Up</td>" + "<td class='r'>Alt+Up</td>"
			+ "<td>&nbsp;</td>" + "<td>&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>" + "</div>" + "</body></html>";

}