package ua.pp.hak.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.util.Attribute;
import ua.pp.hak.util.RequestFocusListener;

public class TpsInfo implements Constants, MenuConstants {
	final static Logger logger = LogManager.getLogger(TpsInfo.class);
	static JPanel parameters;

	// 16102293

	public static void show(Notepad npd) {
		try{
			
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		JPanel buttons = new JPanel();
		JRadioButton liveButton = new JRadioButton("live");
		JRadioButton txdev1Button = new JRadioButton("txdev1");
		txdev1Button.setSelected(true);
		ButtonGroup group = new ButtonGroup();
		group.add(liveButton);
		group.add(txdev1Button);
		buttons.add(liveButton);
		buttons.add(txdev1Button);
		buttons.setBorder(BorderFactory.createTitledBorder("Server: "));

		JLabel langLabel = new JLabel("lang: ", JLabel.TRAILING);
		langLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel marketLabel = new JLabel("market: ", JLabel.TRAILING);
		JLabel skuIdLabel = new JLabel("skuId: ", JLabel.TRAILING);
		JTextField langField = new JTextField("en", 5);
		JTextField marketField = new JTextField("US", 5);
		JTextField skuIdField = new JTextField(npd.getSkuField().getText(), 5);
		skuIdField.addAncestorListener(new RequestFocusListener());
		skuIdField.selectAll();

		parameters = new JPanel(new SpringLayout());
		parameters.setBorder(BorderFactory.createTitledBorder("Parameters: "));

		setField(langLabel, langField);
		setField(marketLabel, marketField);
		setField(skuIdLabel, skuIdField);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(parameters, 3, 2, 6, 6, 6, 6);

		main.add(buttons);
		main.add(parameters);

		int result = JOptionPane.showConfirmDialog(null, main, helpTpsInfo + ". Enter your test parameters",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String server = liveButton.isSelected() ? "templex" : txdev1Button.getText();

			Document doc = parseDoc(server, langField.getText(), marketField.getText(),
					getCleanedSku(skuIdField.getText()));
			String text = "";
			if (doc != null) {
				text = parseJson(doc);
			} else {
				logger.error("Document of TPS page is null!");
			}

			JTextPane textPane = new JTextPane();
			textPane.setContentType("text/html");
			textPane.setBackground(null);
			textPane.setOpaque(false);
			textPane.setBorder(null);
			textPane.setText(text);
			textPane.setEditable(false);

			JScrollPane jsp = new JScrollPane(textPane);

			// textPane.setSize(new Dimension(480, 10));
			// textPane.setPreferredSize(new Dimension(480, 400));
			// // TIP: Make the JOptionPane resizable using the
			// HierarchyListener
			// textPane.addHierarchyListener(new HierarchyListener() {
			// public void hierarchyChanged(HierarchyEvent e) {
			// Window window = SwingUtilities.getWindowAncestor(textPane);
			// if (window instanceof Dialog) {
			// Dialog dialog = (Dialog)window;
			// if (!dialog.isResizable()) {
			// dialog.setResizable(true);
			// }
			// }
			// }
			// });
			JFrame frame = new JFrame(helpTpsInfo);
			frame.setIconImage(new ImageIcon(frame.getClass().getResource(imgTemplexBigLocation)).getImage());
			frame.add(jsp, BorderLayout.CENTER);
			frame.setSize(new Dimension(800, 600));
			frame.setLocationRelativeTo(null);
			textPane.setCaretPosition(0);
			frame.setVisible(true);
			// JOptionPane.showMessageDialog(null, textPane, "some title",
			// JOptionPane.PLAIN_MESSAGE);
			return;
		}
		} catch (Exception e){
			logger.error(e.getMessage());
		}
	}

	public static void setField(JLabel l, JTextField textField) {
		parameters.add(l);
		l.setLabelFor(textField);
		parameters.add(textField);
	}

	private static Document parseDoc(String server, String lang, String market, String skuId) {

		final String ENCODING = "UTF-8";
		StringBuilder sb = new StringBuilder();
		sb.append("http://");
		sb.append(server);
		sb.append(".cnetcontent.com/Tps?Parameters=lang%3D");
		sb.append(lang);
		sb.append("%26market%3D");
		sb.append(market);
		sb.append("%26skuId%3D");
		sb.append(skuId);

		Document doc;
		try {
			String link = sb.toString();
			// doc = Jsoup.connect(link).timeout(10*1000).get();
			doc = Jsoup.parse(new URL(link).openStream(), ENCODING, link);
			return doc;

		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	private static String getCleanedSku(String Sku) {
		StringBuilder sb = new StringBuilder();
		char[] skuChars = Sku.toCharArray();
		for (int i = 0; i < skuChars.length; i++) {
			if (Character.isDigit(skuChars[i])) {
				sb.append(skuChars[i]);
			}
		}

		return sb.toString();
	}

	private static String parseJson(Document doc) {
		Elements contentAvus = doc.select("#Avus_Result");
		Elements contentSku = doc.select("#Sku_Result");
		Elements contentTexts = doc.select("#Texts_Result");
		Elements contentSpecs = doc.select("#Specs_Result");

		String avusRes = contentAvus.get(0).text();
		String skuRes = contentSku.get(0).text();
		String textsRes = contentTexts.get(0).text();
		String specsRes = contentSpecs.get(0).text();

		StringBuilder sb = new StringBuilder();

		sb.append("<html>");
		sb.append(
				"<head><style> div.centered {text-align: center;} div.centered table { border-collapse: collapse; margin: 0 auto;  background-color: white; padding:5px;} tr {border-bottom: 1px solid #dddddd; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; border-bottom: none; } td { text-align: left; border-right: 1px solid #dddddd;} td.red { border-right: 1px solid #E03134; } td.cntr {text-align: center;} body {font-family:Segoe UI; font-size:9px; } </style></head>");
		sb.append("<body>");
		sb.append("<div class='centered'>");
		sb.append("<table>");
		sb.append("<tbody>");
		sb.append("<tr class='header'>");
		sb.append("<td colspan='3' class='cntr'>");
		//////////////////////////////////////
		sb.append(parseSkuInfo(skuRes));
		//////////////////////////////////////
		sb.append("</td>");
		sb.append("<td colspan='2' class='cntr'>DEFAULT</td>");
		sb.append("<td colspan='2' class='cntr'>USM</td>");
		sb.append("<td colspan='2' class='cntr'>INV</td>");
		sb.append("</tr>");
		sb.append("<tr class='header'>");
		sb.append("<td class='red'>id</td>");
		sb.append("<td class='red'>name</td>");
		sb.append("<td>set</td>");
		sb.append("<td class='red'>value</td>");
		sb.append("<td>unit</td>");
		sb.append("<td class='red'>value</td>");
		sb.append("<td>unit</td>");
		sb.append("<td class='red'>value</td>");
		sb.append("<td>unit</td>");
		sb.append("</tr>");
		//////////////////////////////////////
		sb.append(parseSpecsInfo(specsRes));		
		sb.append(parseFeatureInfo(textsRes));
		sb.append(getEndRow());
		
		sb.append(parseAttributeInfo(avusRes));
		sb.append(getEndRow());
		//////////////////////////////////////

		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private static String parseAttributeInfo(String json) {
		StringBuilder sb = new StringBuilder();

		JSONObject obj = new JSONObject(json);

		if (obj.has("data")) {
			JSONObject data = obj.getJSONObject("data");

			if (data.has("tx-avus")) {
				JSONObject txAvus = data.getJSONObject("tx-avus");

				if (txAvus.has("avus")) {
					JSONArray avus = txAvus.getJSONArray("avus");

					//////////////////////////////////////////////

					int prevAttrId = 0;
					for (int i = 0; i < avus.length(); i++) {

						JSONObject attr = avus.getJSONObject(i);
						
						int attrId = 0;
						if (attr.has("attr")){
							attrId = attr.getInt("attr");
						}
						
						JSONArray vals=null;
						if (attr.has("vals")){
							 vals = attr.getJSONArray("vals");
						}
						

						for (int j = 0; j < vals.length(); j++) {
							JSONObject val = vals.getJSONObject(j);

							// get Set
							int set = 0;
							if (val.has("set")) {
								set = val.getInt("set");
							}

							// get default value and unit
							String valueDefault = null;
							String unitDefault = null;
							if (val.has("name")) {
								valueDefault = val.getString("name");
							}
							if (val.has("unit")) {
								JSONObject unit = val.getJSONObject("unit");
								unitDefault = unit.getString("name");
							}

							// get usm and invariant values
							String valueUsm = null;
							String unitUsm = null;
							String valueInv = null;
							String unitInv = null;

							if (val.has("extended")) {

								JSONObject extended = val.getJSONObject("extended");

								// get usm value and unit
								if (extended.has("usm")) {
									JSONObject usm = extended.getJSONObject("usm");
									if (usm.has("name")) {
										valueUsm = usm.getString("name");
									}
									if (usm.has("unit")) {
										unitUsm = usm.getString("unit");
									}

								}

								// get invariant value and unit
								if (extended.has("inv")) {
									JSONObject inv = extended.getJSONObject("inv");
									if (inv.has("name")) {
										valueInv = inv.getString("name");
									}
									if (inv.has("unit")) {
										unitInv = inv.getString("unit");
									}

								}

							}

							////////////////////////////////////////

							sb.append("<tr>");

							if ((prevAttrId == attrId && set > 1) || j > 0) {
								sb.append("<td>&nbsp;</td>");
								sb.append("<td>&nbsp;</td>");
							} else {
								sb.append("<td>");
								sb.append(attrId);
								sb.append("</td>");

								String name = null;
								List<Attribute> attibutes = TChecker.getAttributes();
								for (Attribute attribute : attibutes) {
									if (attribute.getId() == attrId) {
										name = attribute.getName();
									}
								}
								sb.append("<td>");
								sb.append(name);
								sb.append("</td>");
							}

							sb.append("<td class='red'>");
							sb.append(set == 0 ? "&nbsp;" : set);
							sb.append("</td>");

							sb.append("<td>");
							sb.append(valueDefault != null ? valueDefault : "&nbsp;");
							sb.append("</td>");

							sb.append("<td class='red'>");
							sb.append(unitDefault != null ? unitDefault : "&nbsp;");
							sb.append("</td>");

							sb.append("<td>");
							sb.append(valueUsm != null ? valueUsm : "&nbsp;");
							sb.append("</td>");

							sb.append("<td class='red'>");
							sb.append(unitUsm != null ? unitUsm : "&nbsp;");
							sb.append("</td>");

							sb.append("<td>");
							sb.append(valueInv != null ? valueInv : "&nbsp;");
							sb.append("</td>");

							sb.append("<td class='red'>");
							sb.append(unitInv != null ? unitInv : "&nbsp;");
							sb.append("</td>");

							sb.append("</tr>");

							////////////////////////////////////////
						}

						prevAttrId = attrId;
					}
				}
			}
		}
		return sb.toString();
	}

	private static String parseSkuInfo(String json) {
		StringBuilder sb = new StringBuilder();

		JSONObject obj = new JSONObject(json);
		if (obj.has("data")) {
			JSONObject data = obj.getJSONObject("data");

			if (data.has("tx-sku")) {

				JSONObject txSku = data.getJSONObject("tx-sku");
				int sku = txSku.getInt("sku");
				int status = txSku.getInt("status");

				sb.append("SKU: ");
				sb.append(sku);
				sb.append(" (status: ");
				sb.append(status);
				sb.append(")");
			}
		}

		return sb.toString();
	}

	private static String parseFeatureInfo(String json) {
		StringBuilder sb = new StringBuilder();

		JSONObject obj = new JSONObject(json);
		if (obj.has("data")) {

			JSONObject data = obj.getJSONObject("data");
			
			if (data.has("tx-feat")) {
				JSONObject tx = data.getJSONObject("tx-feat");

				if (tx.has("items")) {
					JSONArray items = tx.getJSONArray("items");

					for (int i = 0; i < items.length(); i++) {
						JSONObject item = items.getJSONObject(i);

						String name = null;
						if (item.has("name")) {
							name = item.getString("name");
						}

						if (item.has("lines")) {
							JSONArray lines = item.getJSONArray("lines");

							for (int j = 0; j < lines.length(); j++) {
								sb.append("<tr>");
								if (i == 0) {
									sb.append("<td>");
									sb.append("PF");
									sb.append("<td>");
									sb.append("Product Features");
									sb.append("</td>");
									sb.append("</td>");
								} else {
									sb.append("<td>");
									sb.append("&nbsp;");
									sb.append("</td>");
									sb.append("<td>");
									sb.append("&nbsp;");
									sb.append("</td>");
								}
								sb.append("<td class='red'>");
								sb.append("&nbsp;");
								sb.append("</td>");
								sb.append("<td>");
								if (name != null) {
									sb.append(name);
									sb.append(": ");
								}
								sb.append(lines.getString(j));
								sb.append("</td>");
								sb.append("<td class='red'>");
								sb.append("&nbsp;");
								sb.append("</td>");
								for (int z = 0; z < 2; z++) {
									sb.append("<td>");
									sb.append("&nbsp;");
									sb.append("</td>");
									sb.append("<td class='red'>");
									sb.append("&nbsp;");
									sb.append("</td>");
								}
								sb.append("</tr>");
							}
						}
					}
				}
			}
			
			String[] arr = { "tx-mkt", "tx-ksp", "tx-wib" };
			for (int k = 0; k < arr.length; k++) {

				if (data.has(arr[k])) {
					JSONObject tx = data.getJSONObject(arr[k]);

					if (tx.has("lines")) {
						JSONArray lines = tx.getJSONArray("lines");

						for (int i = 0; i < lines.length(); i++) {
							sb.append("<tr>");
							if (i == 0) {
								sb.append("<td>");
								if (arr[k].equals("tx-mkt")) {
									sb.append("MD");
									sb.append("<td>");
									sb.append("Marketing Description");
									sb.append("</td>");
								} else if (arr[k].equals("tx-ksp")) {
									sb.append("KSP");
									sb.append("<td>");
									sb.append("Key Selling Points");
									sb.append("</td>");
								} else if (arr[k].equals("tx-wib")) {
									sb.append("WIB");
									sb.append("<td>");
									sb.append("What's in the Box");
									sb.append("</td>");
								} else {
									sb.append("&nbsp;");
									sb.append("<td>");
									sb.append("&nbsp;");
									sb.append("</td>");
								}
								sb.append("</td>");
							} else {
								sb.append("<td>");
								sb.append("&nbsp;");
								sb.append("</td>");
								sb.append("<td>");
								sb.append("&nbsp;");
								sb.append("</td>");
							}
							sb.append("<td class='red'>");
							sb.append("&nbsp;");
							sb.append("</td>");
							sb.append("<td>");
							sb.append(lines.getString(i));
							sb.append("</td>");
							sb.append("<td class='red'>");
							sb.append("&nbsp;");
							sb.append("</td>");
							for (int j = 0; j < 2; j++) {
								sb.append("<td>");
								sb.append("&nbsp;");
								sb.append("</td>");
								sb.append("<td class='red'>");
								sb.append("&nbsp;");
								sb.append("</td>");
							}
							sb.append("</tr>");
						}
					}
				}
			}

			
		}

		return sb.toString();
	}
	
	
	private static String parseSpecsInfo(String json){
		StringBuilder sb = new StringBuilder();

		JSONObject obj = new JSONObject(json);
		if (obj.has("data")) {
			JSONObject data = obj.getJSONObject("data");

			if (data.has("tx-std-desc")) {

				String descr = data.getString("tx-std-desc");
				
				sb.append("<tr>");
				sb.append("<td>");
				sb.append("SD");
				sb.append("</td>");
				sb.append("<td>");
				sb.append("Standard Description");
				sb.append("</td>");
				sb.append("<td class='red'>");
				sb.append("&nbsp;");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(descr);
				sb.append("</td>");
				sb.append("<td class='red'>");
				sb.append("&nbsp;");
				sb.append("</td>");
				for (int z = 0; z < 2; z++) {
					sb.append("<td>");
					sb.append("&nbsp;");
					sb.append("</td>");
					sb.append("<td class='red'>");
					sb.append("&nbsp;");
					sb.append("</td>");
				}
				sb.append("</tr>");
			}
		}

		return sb.toString();
	}
	
	private static String getEndRow (){
		StringBuilder sb = new StringBuilder();
		sb.append("<tr class='header'>");
		for (int i = 0; i < 9; i++) {
			sb.append("<td class='red'>&nbsp;</td>");
		}
		sb.append("</tr>");
		
		return sb.toString();
	}
}