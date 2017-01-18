package ua.pp.hak.ui;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class GoToDialog {
	
	final static Logger logger = LogManager.getLogger(GoToDialog.class);
	
	public static void goTo(Notepad npd) {
		
		RSyntaxTextArea taExpr = npd.getExprTextArea();
		
		int lineNumber = 0;
		try {
			lineNumber = taExpr.getLineOfOffset(taExpr.getCaretPosition()) + 1;
			String tempStr = JOptionPane.showInputDialog(npd.getFrame(), "Enter Line Number:", "" + lineNumber);
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
}
