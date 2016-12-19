package ua.pp.hak.update;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.ui.Constants;

public class UpdateInfo extends JFrame implements Constants {
	final static Logger logger = LogManager.getLogger(UpdateInfo.class);

	private static final long serialVersionUID = -9197099692400348955L;
	private JEditorPane infoPane;
	private JScrollPane scp;
	private JButton ok;
	private JButton cancel;
	private JPanel pan1;
	private JPanel pan2;
	private JFrame frame = this;

	public UpdateInfo(String info) {
		initComponents();
		infoPane.setText(info);
	}

	private void initComponents() {

		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("New Update Found");
		this.setIconImage(new ImageIcon(this.getClass().getResource(imgTemplexBigLocation)).getImage());
		pan1 = new JPanel();
		pan1.setLayout(new BorderLayout());

		pan2 = new JPanel();
		pan2.setLayout(new FlowLayout());

		infoPane = new JEditorPane();
		infoPane.setEditable(false);
		Font font = new Font("Consolas", Font.PLAIN, 14);
		// remove this when you have html formatted text
		infoPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		infoPane.setFont(font);
		infoPane.setContentType("text/html");

		scp = new JScrollPane();
		scp.setViewportView(infoPane);

		ok = new JButton("Update");
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(frame,
						"Tpad is opened.\nUpdater will close it in order to process the update.\nContinue?", "Update",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if (answer == JOptionPane.YES_OPTION) {
					update();
				}
			}
		});

		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				UpdateInfo.this.dispose();
			}
		});
		pan2.add(ok);
		pan2.add(cancel);
		pan1.add(pan2, BorderLayout.SOUTH);
		pan1.add(scp, BorderLayout.CENTER);
		this.add(pan1);
		pack();
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void update() {
		// String[] cmdarray = { "java", "-jar", "updater/update.jar",
		// "-update"};
		String[] cmdarray = { "updater/update.exe", "-update", "Tpad.exe" };
		try {
			logger.info("Start updating...");
			Runtime.getRuntime().exec(cmdarray);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		logger.info("Stop working...");
		System.exit(0);

	}

}
