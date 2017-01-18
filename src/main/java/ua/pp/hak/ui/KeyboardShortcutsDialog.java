package ua.pp.hak.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

public class KeyboardShortcutsDialog implements MenuConstants {
	public static void showKeyboardShortcuts(JFrame frame) {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(null);
		textPane.setOpaque(false);
		textPane.setBorder(null);
		textPane.setText(shortcutsText);
		textPane.setEditable(false);
		JOptionPane.showMessageDialog(frame, textPane, helpKeyboardShortcuts, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)));
	}
}
