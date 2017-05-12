package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.PlanioParser;
import ua.pp.hak.util.RequestFocusListener;

public class RollbackChangesDialog implements MenuConstants {
	final static Logger logger = LogManager.getLogger(RollbackChangesDialog.class);

	private static JTextField exprLinkField;
	private static JTextField diffLinkField;

	public static void show(Notepad npd) {
		try {
			JPanel main = new JPanel();
			main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

			main.add(createFieldsPanel());
			// main.setPreferredSize(new Dimension(400, 500));

			boolean isAccepted = false;
			int result = 0;
			while (!isAccepted) {

				result = JOptionPane.showConfirmDialog(npd.getFrame(), main, "Rollback changes of the expression",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (result == JOptionPane.OK_OPTION) {
					String[] errors = checkLinks(exprLinkField.getText(), diffLinkField.getText());
					if (errors != null) {
						JOptionPane.showMessageDialog(npd.getFrame(), errors, "Wrong links", JOptionPane.ERROR_MESSAGE);
					} else {
						isAccepted = true;
					}
				} else {
					break;
				}

			}
			if (result == JOptionPane.OK_OPTION) {
				String text = new PlanioParser().getJournalExpression(exprLinkField.getText(), diffLinkField.getText());

				JTextPane textPane = new JTextPane();
				// textPane.setContentType("text/html");
				textPane.setBackground(null);
				textPane.setOpaque(false);
				textPane.setBorder(null);
				textPane.setText(text);
				textPane.setFont(npd.getDefaultFont().deriveFont(13f));
				textPane.setEditable(false);

				JScrollPane jsp = new JScrollPane(textPane);
				jsp.getViewport().setBackground(Color.WHITE);  // color

				JFrame frameResult = new JFrame("Results for Rollback changes of the expression");
				frameResult.setIconImage(
						new ImageIcon(frameResult.getClass().getResource(imgTemplexBigLocation)).getImage());
				frameResult.add(jsp, BorderLayout.CENTER);
				frameResult.setSize(new Dimension(800, 600));
				frameResult.setLocationRelativeTo(null);
				textPane.setCaretPosition(0);
				frameResult.setVisible(true);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private static String[] checkLinks(String exprLink, String diffLink) throws BadLocationException {
		List<String> errorLinks = new ArrayList<>();

		boolean isCnetContentUrlExpr = exprLink.matches("https?://claims\\.cnetcontent\\.com/issues/\\d+/?");
		boolean isCnetPlanioUrlExpr = exprLink.matches("https?://cnet\\.plan\\.io/issues/\\d+/?");
		boolean isCnetContentUrlDiff = diffLink.matches("https?://claims\\.cnetcontent\\.com/journals/\\d+/diff.*");
		boolean isCnetPlanioUrlDiff = diffLink.matches("https?://cnet\\.plan\\.io/journals/\\d+/diff.*");

		if (!isCnetContentUrlExpr && !isCnetPlanioUrlExpr) {
			// errorLinks.add(0," ");
			errorLinks.add(0, "Please fix wrong links:");
			errorLinks.add(" ");
			errorLinks.add("Correct EXPR link should looks like:");
			errorLinks.add("    e.g. https://claims.cnetcontent.com/issues/123456");
			errorLinks.add("    e.g. https://cnet.plan.io/issues/123456");
			return errorLinks.toArray(new String[0]);
		} else if (!isCnetContentUrlDiff && !isCnetPlanioUrlDiff) {
			errorLinks.add(0, "Please fix wrong links:");
			errorLinks.add(" ");
			errorLinks.add("Correct DIFF link should looks like:");
			errorLinks.add("    e.g. https://claims.cnetcontent.com/journals/567890/diff?detail_id=876543");
			errorLinks.add("    e.g. https://cnet.plan.io/journals/567890/diff?detail_id=876543");
			return errorLinks.toArray(new String[0]);
		}

		return null;

	}

	private static JPanel createFieldsPanel() {
		JLabel exprLinkLabel = new JLabel("Expr link: ", JLabel.TRAILING);
		exprLinkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel diffLinkLabel = new JLabel("Diff link: ", JLabel.TRAILING);
		exprLinkField = new JTextField(25);
		exprLinkField.addAncestorListener(new RequestFocusListener());
		diffLinkField = new JTextField(25);
		JPanel parameters = new JPanel(new SpringLayout());
		// Lay out the panel.
		setField(parameters, exprLinkLabel, exprLinkField);
		// parameters.add(new JLabel(" "));
		setField(parameters, diffLinkLabel, diffLinkField);
		SpringUtilities.makeCompactGrid(parameters, 2, 2, 6, 6, 6, 6);
		return parameters;
	}

	private static void setField(JPanel panel, JLabel l, JTextField textField) {
		panel.add(l);
		l.setLabelFor(textField);
		panel.add(textField);
	}
}
