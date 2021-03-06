package ua.pp.hak.util;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import ua.pp.hak.ui.Constants;
import ua.pp.hak.ui.MyFileFilter;
import ua.pp.hak.ui.Notepad;

public class FileOperation implements Constants {
	final static Logger logger = LogManager.getLogger(FileOperation.class);
	
	Notepad npd;

	public boolean saved;
	boolean newFileFlag;
	String fileName;

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
	public FileOperation(Notepad npd) {
		this.npd = npd;

		saved = true;
		newFileFlag = true;
		fileName = defaultFileName;
		fileRef = new File(fileName);
		this.npd.getFrame().setTitle(fileName + " - " + applicationName);

		chooser = new JFileChooser();
//		chooser.addChoosableFileFilter(new MyFileFilter(".java", "Java Source Files(*.java)"));
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
			npd.getExprTextArea().write(fout); // fout.write(npd.ta.getText()); was
											// changed due
			// to incorrect saving of new line
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			updateStatus(temp, false);
			return false;
		} finally {
			try {
				fout.close();
			} catch (IOException excp) {
				logger.error(excp.getMessage());
			}
		}
		updateStatus(temp, true);
		return true;
	}

	////////////////////////
	public boolean saveThisFile() {

		if (!newFileFlag) {
			return saveFile(fileRef);
		}

		return saveAsFile();
	}

	////////////////////////////////////
	public boolean saveAsFile() {
		File temp = null;
		chooser.setDialogTitle("Save As...");
		chooser.setApproveButtonText("Save Now");
		chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
		chooser.setApproveButtonToolTipText("Click me to save!");
		do {
			if (chooser.showSaveDialog(this.npd.getFrame()) != JFileChooser.APPROVE_OPTION)
				return false;
			temp = chooser.getSelectedFile();
			if (!temp.exists())
				break;
			if (JOptionPane.showConfirmDialog(this.npd.getFrame(),
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
				this.npd.getExprTextArea().append(str + System.getProperty("line.separator"));
			}

		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			updateStatus(temp, false);
			return false;
		} finally {
			try {
				din.close();
				fin.close();
			} catch (IOException excp) {
				logger.error(excp.getMessage());
			}
		}
		updateStatus(temp, true);
//		npd.getTextArea().setCaretPosition(0);
		return true;
	}

	///////////////////////
	public void openFile() {
		if (!confirmSave())
			return;

		chooser.setDialogTitle("Open File...");
		chooser.setApproveButtonText("Open this");
		chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
		chooser.setApproveButtonToolTipText("Click me to open the selected file!");

		File temp = null;
		do {
			if (chooser.showOpenDialog(this.npd.getFrame()) != JFileChooser.APPROVE_OPTION)
				return;
			temp = chooser.getSelectedFile();

			if (temp.exists())
				break;
			else {
				JOptionPane.showMessageDialog(this.npd.getFrame(),
						"<html><b>" + temp.getName() + "</b> file not found.<br>"
								+ "Please verify the correct file name was given.<html>",
						"Open", JOptionPane.INFORMATION_MESSAGE);
				continue;
			}
		} while (true);

		this.npd.getExprTextArea().setText("");

		if (!openFile(temp)) {
			fileName = defaultFileName;
			saved = true;
			this.npd.getFrame().setTitle(fileName + " - " + applicationName);
		} else {
//			this.npd.getUndoButton().setEnabled(false);
//			this.npd.getRedoButton().setEnabled(false);
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
			npd.getFrame().setTitle(fileName + " - " + applicationName);
			npd.getStatusBar().setText("File: " + temp.getPath() + " saved/opened successfully.");
			newFileFlag = false;
		} else {
			npd.getStatusBar().setText("Failed to save/open: " + temp.getPath());
		}
	}

	///////////////////////
	public boolean confirmSave() {
		String strMsg = "<html>The text in the <b>" + fileName + "</b> file has been changed.<br>"
				+ "Do you want to save the changes?<html>";
		if (!saved) {
			int x = JOptionPane.showConfirmDialog(this.npd.getFrame(), strMsg, applicationName,
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (x == JOptionPane.NO_OPTION)
				return true;
			if (x == JOptionPane.YES_OPTION && saveAsFile())
				return true;

		} else if (saved) {
			return true;
		}
		return false;
	}

	///////////////////////////////////////
	public void newFile() {
		if (!confirmSave())
			return;

		this.npd.getExprTextArea().setText("");
		fileName = defaultFileName;
		fileRef = new File(fileName);
		saved = true;
		newFileFlag = true;
		this.npd.getFrame().setTitle(fileName + " - " + applicationName);
//		this.npd.getManager().discardAllEdits();
//		this.npd.getUndoButton().setEnabled(false);
//		this.npd.getRedoButton().setEnabled(false);
	}
	//////////////////////////////////////
	
	
	
	public void saveTempPadText() {
		RSyntaxTextArea taExpr = npd.getExprTextArea();
		
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
}