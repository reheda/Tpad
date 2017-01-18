package ua.pp.hak.util;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RecordableTextAction;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

public class Actions {
	/**
	 * Action that copies a line up or down. VR edition
	 */
	@SuppressWarnings("serial")
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
	@SuppressWarnings("serial")
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

			// VR edition. Was: if (!textArea.getMarkAllOnOccurrenceSearches())
			if (textArea.getMarkAllOnOccurrenceSearches()) {
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
	@SuppressWarnings("serial")
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

			// VR edition. Was: if (!textArea.getMarkAllOnOccurrenceSearches())
			// {
			if (textArea.getMarkAllOnOccurrenceSearches()) {
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

	/**
	 * Action that change a size of the font.
	 */
	@SuppressWarnings("serial")
	public static class ChangeFontSizeAction extends AbstractAction {

		private RSyntaxTextArea taExpr;
		private int delta;

		public ChangeFontSizeAction(RSyntaxTextArea taExpr, int delta) {
			this.taExpr = taExpr;
			this.delta = delta;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Font tempFont = taExpr.getFont();
			float newFontSize = tempFont.getSize2D() + delta; 
			
			if (newFontSize > 0){
				taExpr.setFont(tempFont.deriveFont(newFontSize));				
			}
		}

	}

	@SuppressWarnings("serial")
	public static class SetFontAction extends AbstractAction {

		private RSyntaxTextArea taExpr;
		private Font font;

		public SetFontAction(RSyntaxTextArea taExpr, Font font) {
			this.taExpr = taExpr;
			this.font = font;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			taExpr.setFont(font);
		}

	}

}
