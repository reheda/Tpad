package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.compiler.TParser;

public class LoadingPanel {
	private static JLabel label;

	public static JLabel getLabel() {
		return label;
	}

	public static void doProcess(String processToDo, Notepad npd) {

		JFrame frame = npd.getFrame();
		JTextArea taExprRes = npd.getExprResTextArea();

		final JPanel popup = new JPanel(new BorderLayout(5, 5));
		popup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		popup.setBackground(Color.white);
		frame.getLayeredPane().add(popup);

		String prorcessToDoCapitalized = processToDo.substring(0, 1).toUpperCase() + processToDo.substring(1);
		label = new JLabel("[" + prorcessToDoCapitalized + "] Processing...");
		popup.add(label, BorderLayout.NORTH);
		JProgressBar pb = new JProgressBar();
		popup.add(pb, BorderLayout.CENTER);
		pb.setBorderPainted(true);
		pb.setMinimum(0);
		pb.setMaximum(100);
		pb.setValue(0);
		// pb.setStringPainted(true);
		pb.setIndeterminate(true);
		JButton cancel = new JButton("Cancel");
		popup.add(cancel, BorderLayout.EAST);
		popup.doLayout();
		Dimension size = popup.getPreferredSize();
		Dimension windowSize = frame.getSize();
		int popupWidth = 300;
		popup.setBounds((windowSize.width - popupWidth) / 2, (windowSize.height - size.height) / 2, popupWidth,
				size.height);
		frame.getLayeredPane().setLayer(popup, JLayeredPane.POPUP_LAYER);
		popup.setVisible(false);

		final SwingWorker<?, ?> worker = new SwingWorker<Void, String>() {
			protected Void doInBackground() throws InterruptedException {
				publish(processToDo);
				if (processToDo.equals("parse")) {
					TParser.parse(npd);
				} else if (processToDo.equals("parse-sku-list")) {
					new ParseSkuListDialog().show(npd);
				} else if (processToDo.equals("check")) {
					TChecker.check(npd);
				} else if (processToDo.equals("check-expr-list")) {
					new CheckExprListDialog().show(npd);
				} else if (processToDo.equals("tps")) {
					TpsInfo.show(npd);
				} else if (processToDo.equals("rollback-changes")) {
					RollbackChangesDialog.show(npd);
				}
				return null;
			}

			protected void process(List<String> chunks) {
				// invoked by publish()

				String proc = chunks.get(chunks.size() - 1);
				if (proc.equals("parse") || proc.equals("check") || proc.equals("tps")) {

					// do something

				}
			}

			protected void done() {
				popup.setVisible(false);
				if (isCancelled()) {
					Color red = new Color(189, 0, 0);
					taExprRes.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, red),
							new EmptyBorder(2, 5, 2, 0)));

					taExprRes.setText("Canceled!");
				}
			}
		};
		worker.execute();
		popup.setVisible(true);

		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				worker.cancel(true);
			}
		});
	}
}
