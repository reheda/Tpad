package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.PlanioParser;
import ua.pp.hak.util.RequestFocusListener;

public class CheckExprListDialog implements Constants, MenuConstants {
	final static Logger logger = LogManager.getLogger(CheckExprListDialog.class);

	private JTextArea taLinksList;
	private Notepad npd;

	public void show(Notepad npd) {
		this.npd = npd;
		try {
			JPanel main = new JPanel();
			main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

			main.add(createExpressionListPanel());

			main.setPreferredSize(new Dimension(400, 500));

			boolean isAccepted = false;
			int result = 0;
			while (!isAccepted) {

				// replace "null" to "npd.getFrame()"
				result = JOptionPane.showConfirmDialog(npd.getFrame(), main, "Check Expression List",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (result == JOptionPane.OK_OPTION) {
					taLinksList.getHighlighter().removeAllHighlights();

					String links = taLinksList.getText();
					String[] errors = checkLinks(links);
					if (errors != null) {
						// replace "null" to "npd.getFrame()"
						JOptionPane.showMessageDialog(npd.getFrame(), errors, "Wrong links", JOptionPane.ERROR_MESSAGE);
					} else {
						isAccepted = true;
					}
				} else {
					break;
				}

			}
			if (result == JOptionPane.OK_OPTION) {
				// "(?m)^[ \t]*\r?\n" - regex to remove empty lines
				String links = taLinksList.getText();
				String text = new PlanioParser().getResultPage(links.replaceAll("(?m)^[ \t]*\r?\n", "").split("\\n"));

				JTextPane textPane = new JTextPane();
				textPane.setContentType("text/html");
				textPane.setBackground(null);
				textPane.setOpaque(false);
				textPane.setBorder(null);
				textPane.setText(text);
				textPane.setEditable(false);

				JScrollPane jsp = new JScrollPane(textPane);

				JFrame frame = new JFrame("Error results for Expression list");
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

	private String[] checkLinks(String input) throws BadLocationException {
		List<String> errorLinks = new ArrayList<>();

		String[] links = input.split("\\n");
		for (int i = 0; i < links.length; i++) {

			String link = links[i];
			if (!link.trim().isEmpty()) {

				boolean isCnetContentUrl = link.matches("https?://claims\\.cnetcontent\\.com/issues/\\d+/?");
				boolean isCnetPlanioUrl = link.matches("https?://cnet\\.plan\\.io/issues/\\d+/?");

				if (!isCnetContentUrl && !isCnetPlanioUrl) {
					errorLinks.add(" - Line " + (i + 1) + ":   " + link);

					SquigglePainter red = new SquigglePainter(Color.RED);
					int startIndex = taLinksList.getLineStartOffset(i);
					int endIndex = taLinksList.getLineEndOffset(i);
					taLinksList.getHighlighter().addHighlight(startIndex, endIndex, red);
				}
			}
		}

		if (errorLinks.size() > 0) {
			// errorLinks.add(0," ");
			errorLinks.add(0, "Please fix wrong links:");
			errorLinks.add(" ");
			errorLinks.add("Correct link should looks like:");
			errorLinks.add("    e.g. https://claims.cnetcontent.com/issues/123456");
			errorLinks.add("    e.g. https://cnet.plan.io/issues/123456");
			return errorLinks.toArray(new String[0]);
		}

		return null;

	}

	private JPanel createExpressionListPanel() {

		taLinksList = new JTextArea();
		taLinksList.setLineWrap(true);
		TextLineNumber tln = new TextLineNumber(taLinksList, 2);
		tln.setUpdateFont(true);
		taLinksList.setFont(npd.getDefaultFont().deriveFont(12f));
		taLinksList.addAncestorListener(new RequestFocusListener());
		JScrollPane spSkuList = new JScrollPane(taLinksList);
		spSkuList.setRowHeaderView(tln);
		spSkuList.setPreferredSize(new Dimension(400, 350));
		spSkuList.setMinimumSize(new Dimension(400, 50));
		spSkuList.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel skuListPanel = new JPanel();
		skuListPanel.setLayout(new BoxLayout(skuListPanel, BoxLayout.Y_AXIS));
		skuListPanel.setBorder(BorderFactory.createTitledBorder("List of links to expression / item / section: "));
		skuListPanel.add(spSkuList);

		return skuListPanel;
	}
}
