package ua.pp.hak.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.Legacy;

public class LegacyInfoDialog implements MenuConstants {
	final static Logger logger = LogManager.getLogger(LegacyInfoDialog.class);

	public static void showLegacyInfo(JFrame frame) {
		try {
			String legacyCode = JOptionPane.showInputDialog(frame, "Enter Legacy Code:", helpLegacyInfo,
					JOptionPane.PLAIN_MESSAGE);
			if (legacyCode == null) {
				return;
			}

			legacyCode = legacyCode.toUpperCase();
			String legacyName = Legacy.getLecagyName(legacyCode);

			if (legacyName == null) {
				JOptionPane.showMessageDialog(frame, "Can't find legacy '" + legacyCode + "'", helpLegacyInfo,
						JOptionPane.INFORMATION_MESSAGE);
			} else {

				String text = "<html> <head> <style> table { border-collapse: collapse; } th, td { text-align: left; border-bottom: 1px solid #dddddd; } td.right { border-right: 1px solid #E03134; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; } body {font-family:Segoe UI; font-size:9px; } div {background-color: white; padding:5px; } </style> </head> <body> <div> <table width='500'> "
						+ "<tbody><tr class='header'><td width='90'>Parameter</td><td>Value</td></tr>"
						+ "<tr><td class='right'>Code</td><td>" + legacyCode + "</td></tr>"
						+ "<tr><td class='right'>Name</td><td>" + legacyName + "</td></tr>"
						+ "</tbody> </table> </div> </body></html>";
				JTextPane textPane = new JTextPane();
				textPane.setContentType("text/html");
				textPane.setBackground(null);
				textPane.setOpaque(false);
				textPane.setBorder(null);
				textPane.setText(text);
				textPane.setEditable(false);
				JOptionPane.showMessageDialog(frame, textPane, helpLegacyInfo, JOptionPane.PLAIN_MESSAGE);
				return;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
