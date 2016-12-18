package ua.pp.hak.update;

import java.io.InputStream;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.Version;

public class Updater {
	final static Logger logger = LogManager.getLogger(Updater.class);

	private final static String versionURL = "http://tpad.hak.pp.ua/version.html";

	private final static String historyURL = "http://tpad.hak.pp.ua/history.html";

	public static String getLatestVersion() throws Exception {
		String data = getData(versionURL);
		return data.substring(data.indexOf("[version]") + 9, data.indexOf("[/version]"));
	}

	public static String getWhatsNew() throws Exception {
		String data = getData(historyURL);
		return data.substring(data.indexOf("[history]") + 9, data.indexOf("[/history]"));
	}

	private static String getData(String address) throws Exception {
		URL url = new URL(address);

		InputStream html = null;

		html = url.openStream();

		int c = 0;
		StringBuffer buffer = new StringBuffer("");

		while (c != -1) {
			c = html.read();

			buffer.append((char) c);
		}
		return buffer.toString();
	}

	public static void start(Version currentVersion) {
		// change look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		try {
			Version latestVerion = new Version(Updater.getLatestVersion());
			if (latestVerion.compareTo(currentVersion) > 0) {
				logger.info("Start updating...");
				new UpdateInfo(Updater.getWhatsNew());
				logger.info("Finish updating...");
			} else {
				logger.info("No update is available");
				JOptionPane.showMessageDialog(null, "No update is available", "Update",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

}
