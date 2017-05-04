package ua.pp.hak.ui;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShowLog {
	final static Logger logger = LogManager.getLogger(ShowLog.class);

	public static void show(JFrame frame) {
		File file = new File("logs\\app.log");

		if (file == null || !file.exists()) {
			logger.info("Log file was not found");
			JOptionPane.showMessageDialog(frame, "Log file was not found", "Log", JOptionPane.INFORMATION_MESSAGE);
		} else {
			// Desktop desktop = Desktop.getDesktop();
			// try {
			// desktop.open(file);
			// } catch (IOException e) {
			// logger.error(e.getMessage());
			// }

			String cmd = "explorer.exe /select," + file.getAbsolutePath();
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
	}
}
