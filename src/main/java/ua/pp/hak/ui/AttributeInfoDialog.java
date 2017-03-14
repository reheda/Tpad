package ua.pp.hak.ui;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.util.Attribute;

public class AttributeInfoDialog implements MenuConstants {

	final static Logger logger = LogManager.getLogger(AttributeInfoDialog.class);

	public static void showAttributeInfo(JFrame frame) {
		boolean inputAccepted = false;
		while (!inputAccepted) {
			try {
				String tempStr = JOptionPane.showInputDialog(frame, "Enter Attribute ID:", helpAttributeInfo,
						JOptionPane.PLAIN_MESSAGE);
				if (tempStr == null) {
					return;
				}
				int attrId = Integer.parseInt(tempStr);
				List<Attribute> attibutes = TChecker.getAttributes();
				for (Attribute attr : attibutes) {
					if (attr.getId() == attrId) {
						String name = attr.getGroupName() + " - " + attr.getName();
						String type = attr.getType();
						String status = attr.isDeactivated() ? "<span class='gray'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> Deactivated" : "<span class='green'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> Active";
						String lastUpdate = attr.getLastUpdate();
						String comment = "-- " + attrId + " // " + name.replace("\"", "''");

						String text = "<html> <head> <style> table { border-collapse: collapse; } th, td { text-align: left; border-bottom: 1px solid #dddddd; } td.right { border-right: 1px solid #E03134; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; } body {font-family:Segoe UI; font-size:9px; } div {background-color: white; padding:5px; } span {border: 1px solid black; } .green{background-color:#00C152} .gray{background-color:silver}</style> </head> <body> <div> <table width='500'> "
								+ "<tbody><tr class='header'><td width='90'>Parameter</td><td>Value</td></tr>"
								+ "<tr><td class='right'>ID</td><td>" + attrId + "</td></tr>"
								+ "<tr><td class='right'>Name</td><td>" + name + "</td></tr>"
								+ "<tr><td class='right'>Type</td><td>" + type + "</td></tr> "
								+ "<tr><td class='right'>Status</td><td>" + status + "</td></tr> "
								+ "<tr><td class='right'>Last update (dd/mm/yyyy)</td><td>" + lastUpdate + "</td></tr> "
								+ "</tbody> </table> </div><br />  "
								+ "<div> <table width='500'> <tbody><tr class='header'><td width='90'>For Comments</td></tr>"
								+ "<tr><td>" + comment + "</td></tr>" + "</tbody> </table> </div> </body></html>";
						JTextPane textPane = new JTextPane();
						textPane.setContentType("text/html");
						textPane.setBackground(null);
						textPane.setOpaque(false);
						textPane.setBorder(null);
						textPane.setText(text);
						textPane.setEditable(false);
						JOptionPane.showMessageDialog(frame, textPane, helpAttributeInfo, JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}

				JOptionPane.showMessageDialog(frame, "Can't find attribute '" + attrId + "'", helpAttributeInfo,
						JOptionPane.INFORMATION_MESSAGE);

			} catch (NumberFormatException e) {

				JOptionPane.showMessageDialog(frame, "Only integer is accepted!", helpAttributeInfo,
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}
