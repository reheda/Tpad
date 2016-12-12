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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class UpdateInfo extends JFrame {

	private static final long serialVersionUID = -9197099692400348955L;
	private JEditorPane infoPane;
	private JScrollPane scp;
	private JButton ok;
	private JButton cancel;
	private JPanel pan1;
	private JPanel pan2;

	public UpdateInfo(String info) {
		initComponents();
		infoPane.setText(info);
	}

	private void initComponents() {

		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("New Update Found");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/images/templex-big.png")).getImage());
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
				update();
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
		String[] cmdarray = { "updater/update.exe", "-update" };
		try {
			Runtime.getRuntime().exec(cmdarray);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);

	}

}
