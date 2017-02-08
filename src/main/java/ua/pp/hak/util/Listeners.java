package ua.pp.hak.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JViewport;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import ua.pp.hak.setting.SettingsOperation;
import ua.pp.hak.ui.Notepad;

public class Listeners {
	final static Logger logger = LogManager.getLogger(Listeners.class);

	public static class MouseWheel implements MouseWheelListener {
		private RSyntaxTextArea taExpr;

		public MouseWheel(Notepad npd) {
			taExpr = npd.getExprTextArea();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (e.isControlDown()) {
				Font tempFont = taExpr.getFont();
				float tempFontSize = tempFont.getSize2D();
				if (notches < 0) {
					taExpr.setFont(tempFont.deriveFont(tempFontSize + 1));
				} else {
					if (tempFontSize > 1) {
						taExpr.setFont(tempFont.deriveFont(tempFontSize - 1));
					}
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

	}

	public static class Caret implements CaretListener {
		private RSyntaxTextArea taExpr;
		private JLabel statusBar;
		private Highlighter highlighter;
		private ArrayList<Object> highlighterTags = new ArrayList<>();
		private JButton findButton, replaceButton, cutButton, copyButton;
		private JMenuItem findItem, gotoItem, replaceItem;

		public Caret(Notepad npd) {
			taExpr = npd.getExprTextArea();
			statusBar = npd.getStatusBar();
			findButton = npd.getFindButton();
			replaceButton = npd.getReplaceButton();
			cutButton = npd.getCutButton();
			copyButton = npd.getCopyButton();
			findItem = npd.getFindItem();
			gotoItem = npd.getGotoItem();
			replaceItem = npd.getReplaceItem();
		}

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
				SearchContext context = new SearchContext();
				context.setMarkAll(true);
				context.setMatchCase(false);
				context.setWholeWord(true);
				SearchEngine.markAll(taExpr, context);
//				if (highlighter != null) {
//					// taExpr.getHighlighter().removeAllHighlights();
//					// highlighter.removeAllHighlights();
//					for (Object tag : highlighterTags) {
//						highlighter.removeHighlight(tag);
//					}
//					highlighterTags.clear();
//					highlighter = null;
//					// painter = new LinePainter(ta, new
//					// Color(255,255,210)); // restore line painter
//				} else {
//				}
			} else {
				SearchContext context = new SearchContext(selectedText);
				context.setMarkAll(true);
				context.setMatchCase(false);
				context.setWholeWord(true);
				SearchEngine.markAll(taExpr, context);
				// set highlighter
//				if (!isLetterOrDigit(text, selectionStartPos - 1) && !isLetterOrDigit(text, selectionEndPos)) {
//					try {
//						highlighter = taExpr.getHighlighter();
//						HighlightPainter wordpainter = new DefaultHighlighter.DefaultHighlightPainter(
//								new Color(191, 255, 178)); // light green
//						int p0 = 0, p1 = 0;
//						do {
//							p0 = text.toLowerCase().indexOf(selectedText.toLowerCase(), p1);
//							p1 = p0 + selectedText.length();
//
//							if (p0 > -1 && !isLetterOrDigit(text, p0 - 1) && !isLetterOrDigit(text, p1)
//									&& p0 != selectionStartPos) {
//								highlighterTags.add(highlighter.addHighlight(p0, p1, wordpainter));
//							}
//						} while (p0 > -1);
//					} catch (Exception exc) {
//						exc.printStackTrace();
//					}
//				}

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
	}

	public static class Document implements DocumentListener {
		Notepad npd;
		JFrame frame;
		FileOperation fileHandler;

		public Document(Notepad npd) {
			this.npd = npd;
			frame = npd.getFrame();
			fileHandler = npd.getFileHandler();
		}

		public void changedUpdate(DocumentEvent e) {
			fileHandler.saved = false;
			// add asterisk at the beginning
			addSymbolAtTheTitleBeginning('*');
		}

		public void removeUpdate(DocumentEvent e) {
			fileHandler.saved = false;
			// add asterisk at the beginning
			addSymbolAtTheTitleBeginning('*');
		}

		public void insertUpdate(DocumentEvent e) {
			fileHandler.saved = false;
			// add asterisk at the beginning
			addSymbolAtTheTitleBeginning('*');
		}

		private void addSymbolAtTheTitleBeginning(char c) {
			String tempTitle = frame.getTitle();
			if (tempTitle.charAt(0) != c) {
				frame.setTitle(c + frame.getTitle());
			}
		}

	};
	
	public static class Window extends WindowAdapter {
		FileOperation fileHandler;
		SettingsOperation settingsHandler;
		
		public Window(Notepad npd) {
			fileHandler = npd.getFileHandler();
			settingsHandler = npd.getSettingsHandler();
		}

		@Override
		public void windowClosing(WindowEvent we) {

			if (fileHandler.confirmSave()) {
				logger.info("Stop working...");

				// save settings to settings.xml
				settingsHandler.saveSettings();

				// just for getting backup
				fileHandler.saveTempPadText();

				// kill process if needed
				String processName = "chromedriver.exe";
				try {
					if (ProcessKiller.isProcessRunning(processName)) {
						ProcessKiller.killProcess(processName);
					}
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}

				System.exit(0);
			}
		}
	};
}
