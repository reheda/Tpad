package ua.pp.hak.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.db.DatabaseStAXParser;
import ua.pp.hak.db.DatabaseStAXWriter;
import ua.pp.hak.db.DatabaseUtils;
import ua.pp.hak.util.Attribute;

public class DatabaseUpdateDialog {
	final static Logger logger = LogManager.getLogger(DatabaseUpdateDialog.class);


	public static boolean show(Notepad npd) {

		Timestamp localLastUpdate = new DatabaseUpdateDialog().getLocalLastUpdate();

		Timestamp webLastUpdate = new DatabaseUtils().downloadLastUpdate();

		if (webLastUpdate != null) {
			if (localLastUpdate == null || localLastUpdate.before(webLastUpdate)) {

				Object[] options1 = { "Update", "Cancel" };
				JEditorPane infoPane = new JEditorPane();
				infoPane.setEditable(false);
				infoPane.setPreferredSize(new Dimension(300, 100));
				Font font = new Font("Consolas", Font.PLAIN, 14);
				infoPane.setFont(font);
//				infoPane.setContentType("text/html");
				String formattedDate = DatabaseUtils.dateFormat.format(webLastUpdate);
				String text = new StringBuilder().append("Web DB version: \n").append(formattedDate).toString();
				infoPane.setText(text);
				int answer = JOptionPane.showOptionDialog(npd.getFrame(), infoPane, "New Database Update Found",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);

				if (answer == JOptionPane.YES_OPTION) {
					updateDatabase(formattedDate);
					logger.info("Update is finished for DB");
					return true;
//					JOptionPane.showMessageDialog(npd.getFrame(), "Update finished!", "Database Update",
//							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				logger.info("No update is available for DB");
//				JOptionPane.showMessageDialog(npd.getFrame(), "No update is available", "Database Update",
//						JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			logger.error("webLastUpdate is NULL!");
		}
		
		return false;
	}

	public static void updateDatabase(String lastUpdate) {
		List<Attribute> attributes = new DatabaseUtils().downloadAttributes();
		logger.info("Saving web db...");
		DatabaseStAXWriter.save(attributes, "db/db-web.xml", lastUpdate);
		logger.info("Web db was saved successfully...");
		try {
			TChecker.setAttributes(DatabaseStAXParser.parse());
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private Timestamp getLocalLastUpdate() {
		String lastUpdateStr = DatabaseStAXParser.readLastUpdate(new File("db/last-update.txt"));
		Timestamp localLastUpdate = null;
		if (lastUpdateStr != null) {
			try {
				Date parsedDate = DatabaseUtils.dateFormat.parse(lastUpdateStr);
				localLastUpdate = new Timestamp(parsedDate.getTime());
			} catch (Exception e) {
				// do nothing
			}
		}

		return localLastUpdate;
	}

}
