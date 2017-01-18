package ua.pp.hak.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelpTopicDialog implements MenuConstants {
	final static Logger logger = LogManager.getLogger(HelpTopicDialog.class);
	
	public static void showHelpTopic(JFrame frame) {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(null);
		textPane.setOpaque(false);
		textPane.setBorder(null);
		textPane.setText(quickReferenceText);
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					logger.info(e.getURL());
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JOptionPane.showMessageDialog(frame, textPane, helpHelpTopic, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)));
	}
}
