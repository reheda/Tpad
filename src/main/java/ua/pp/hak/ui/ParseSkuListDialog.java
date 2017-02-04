package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.compiler.TParser;
import ua.pp.hak.util.RequestFocusListener;

public class ParseSkuListDialog implements Constants, MenuConstants {

	final static Logger logger = LogManager.getLogger(ParseSkuListDialog.class);

	private JTextArea taParameters, taSkuList;
	private Notepad npd;

	public void show(Notepad npd) {
		this.npd = npd;
		try {
			JPanel main = new JPanel();
			main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
			
			main.add(createParameterPanel());
			main.add(new JLabel("   "));
			main.add(createSkuListPanel());

			main.setPreferredSize(new Dimension(400, 500));

			int result = JOptionPane.showConfirmDialog(npd.getFrame(), main, "Parse Sku list", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				//"(?m)^[ \t]*\r?\n" - regex to remove empty lines
				String text = TParser.parseForSkuList(npd, taParameters.getText(),
						taSkuList.getText().replaceAll("(?m)^[ \t]*\r?\n", "").split("\\n"));

				JTextPane textPane = new JTextPane();
				textPane.setContentType("text/html");
				textPane.setBackground(null);
				textPane.setOpaque(false);
				textPane.setBorder(null);
				textPane.setText(text);
				textPane.setEditable(false);

				JScrollPane jsp = new JScrollPane(textPane);

				JFrame frame = new JFrame("Expression results for SKU list");
				frame.setIconImage(new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)).getImage());
				frame.add(jsp, BorderLayout.CENTER);
				frame.setSize(new Dimension(800, 600));
				frame.setLocationRelativeTo(null);
				textPane.setCaretPosition(0);
				frame.setVisible(true);
				return;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	

	private JPanel createParameterPanel() {
		taParameters = new JTextArea();
		taParameters.setLineWrap(true);
		taParameters.setFont(npd.getDefaultFont().deriveFont(12f));
		taParameters.setText(npd.getParametersTextArea().getText());

		JScrollPane spParameters = new JScrollPane(taParameters);
		spParameters.setPreferredSize(new Dimension(400, 50));
		spParameters.setMinimumSize(new Dimension(400, 50));
		spParameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		


		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		paramPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
		paramPanel.add(spParameters);
		
		JButton generateParams = new JButton("Generate...");
		generateParams.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String newParams = new ParameterGeneratorDialog().generateParameters(paramPanel);
				if (newParams!=null){
					taParameters.setText(newParams);
				}
			}
		});
		paramPanel.add(generateParams);

		return paramPanel;
	}

	private JPanel createSkuListPanel() {

		taSkuList = new JTextArea();
		taSkuList.setLineWrap(true);
		taSkuList.setFont(npd.getDefaultFont().deriveFont(12f));
		taSkuList.addAncestorListener(new RequestFocusListener());
		JScrollPane spSkuList = new JScrollPane(taSkuList);
		spSkuList.setPreferredSize(new Dimension(400, 350));
		spSkuList.setMinimumSize(new Dimension(400, 50));
		spSkuList.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel skuListPanel = new JPanel();
		skuListPanel.setLayout(new BoxLayout(skuListPanel, BoxLayout.Y_AXIS));
		skuListPanel.setBorder(BorderFactory.createTitledBorder("SKU list: "));
		skuListPanel.add(spSkuList);

		return skuListPanel;
	}
}
