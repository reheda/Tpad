package ua.pp.hak.ui;

import java.awt.Component;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.util.Attribute;

public class TpsInfo {
	final static Logger logger = LogManager.getLogger(TpsInfo.class);
	static JPanel parameters;

	public static void main(String[] args) {
		//16102293
		 show();
//		String avus = "{\"result\":0,\"time\":2,\"data\":{\"tx-avus\":{\"avus\":[{\"attr\":630,\"vals\":[{\"id\":1919,\"name\":\"Bushnell Outdoor Products\",\"extended\":{\"usm\":{\"name\":\"Bushnell Outdoor Products\"},\"inv\":{\"name\":\"Bushnell Outdoor Products\"}}}]},{\"attr\":600,\"vals\":[{\"id\":400392,\"name\":\"Bushnell Rubicon\",\"extended\":{\"usm\":{\"name\":\"Bushnell Rubicon\"},\"inv\":{\"name\":\"Bushnell Rubicon\"}}}]},{\"attr\":601,\"vals\":[{\"id\":2079834,\"name\":\"H125R\",\"extended\":{\"usm\":{\"name\":\"H125R\"},\"inv\":{\"name\":\"H125R\"}}}]},{\"attr\":380,\"vals\":[{\"id\":371085,\"name\":\"IPX4\",\"extended\":{\"usm\":{\"name\":\"IPX4\"},\"inv\":{\"name\":\"IPX4\"}}}]},{\"attr\":606,\"vals\":[{\"id\":354512,\"name\":\"Bushnell\",\"extended\":{\"usm\":{\"name\":\"Bushnell\"},\"inv\":{\"name\":\"Bushnell\"}}}]},{\"attr\":694,\"vals\":[{\"id\":2103,\"name\":\"1\",\"extended\":{\"usm\":{\"name\":\"1\"},\"inv\":{\"name\":\"1\"}}}]},{\"attr\":3235,\"vals\":[{\"id\":99797,\"name\":\"the displayed product image may be a different color\",\"extended\":{\"usm\":{\"name\":\"the displayed product image may be a different color\"},\"inv\":{\"name\":\"the displayed product image may be a different color\"}}}]},{\"attr\":5520,\"vals\":[{\"set\":1,\"id\":2097,\"name\":\"2\",\"unit\":{\"id\":80208,\"name\":\"hrs\"},\"extended\":{\"usm\":{\"name\":\"2\",\"unit\":\"hrs\"},\"inv\":{\"name\":\"2\",\"unit\":\"hrs\"}}},{\"set\":2,\"id\":2148,\"name\":\"12\",\"unit\":{\"id\":80208,\"name\":\"hrs\"},\"extended\":{\"usm\":{\"name\":\"12\",\"unit\":\"hrs\"},\"inv\":{\"name\":\"12\",\"unit\":\"hrs\"}}},{\"set\":3,\"id\":2880,\"name\":\"3.3\",\"unit\":{\"id\":80208,\"name\":\"hrs\"},\"extended\":{\"usm\":{\"name\":\"3.3\",\"unit\":\"hrs\"},\"inv\":{\"name\":\"3.3\",\"unit\":\"hrs\"}}},{\"set\":4,\"id\":2158,\"name\":\"18\",\"unit\":{\"id\":80208,\"name\":\"hrs\"},\"extended\":{\"usm\":{\"name\":\"18\",\"unit\":\"hrs\"},\"inv\":{\"name\":\"18\",\"unit\":\"hrs\"}}},{\"set\":5,\"id\":2097,\"name\":\"2\",\"unit\":{\"id\":80208,\"name\":\"hrs\"},\"extended\":{\"usm\":{\"name\":\"2\",\"unit\":\"hrs\"},\"inv\":{\"name\":\"2\",\"unit\":\"hrs\"}}}]},{\"attr\":5795,\"vals\":[{\"id\":352694,\"name\":\"black\",\"extended\":{\"usm\":{\"name\":\"black\"},\"inv\":{\"name\":\"black\"}}}]},{\"attr\":6908,\"vals\":[{\"id\":452069,\"name\":\"LED\",\"extended\":{\"usm\":{\"name\":\"LED\"},\"inv\":{\"name\":\"LED\"}}}]},{\"attr\":6925,\"vals\":[{\"id\":2914,\"name\":\"125\",\"unit\":{\"id\":80313,\"name\":\"lumens\"},\"extended\":{\"usm\":{\"name\":\"125\",\"unit\":\"lumens\"},\"inv\":{\"name\":\"125\",\"unit\":\"lumens\"}}}]},{\"attr\":6938,\"vals\":[{\"id\":466261,\"name\":\"Cree LED technology\",\"extended\":{\"usm\":{\"name\":\"Cree LED technology\"},\"inv\":{\"name\":\"Cree LED technology\"}}}]},{\"attr\":7450,\"vals\":[{\"id\":454541,\"name\":\"head flashlight\",\"extended\":{\"usm\":{\"name\":\"head flashlight\"},\"inv\":{\"name\":\"head flashlight\"}}}]},{\"attr\":7457,\"vals\":[{\"id\":450291,\"name\":\"tilting head\",\"extended\":{\"usm\":{\"name\":\"tilting head\"},\"inv\":{\"name\":\"tilting head\"}}}]},{\"attr\":7459,\"vals\":[{\"id\":455845,\"name\":\"head\",\"extended\":{\"usm\":{\"name\":\"head\"},\"inv\":{\"name\":\"head\"}}}]},{\"attr\":7462,\"vals\":[{\"id\":450303,\"name\":\"5\",\"extended\":{\"usm\":{\"name\":\"5\"},\"inv\":{\"name\":\"5\"}}}]},{\"attr\":7463,\"vals\":[{\"id\":645994,\"name\":\"spot high\",\"extended\":{\"usm\":{\"name\":\"spot high\"},\"inv\":{\"name\":\"spot high\"}}},{\"id\":645995,\"name\":\"spot low\",\"extended\":{\"usm\":{\"name\":\"spot low\"},\"inv\":{\"name\":\"spot low\"}}},{\"id\":753926,\"name\":\"flood high\",\"extended\":{\"usm\":{\"name\":\"flood high\"},\"inv\":{\"name\":\"flood high\"}}},{\"id\":753927,\"name\":\"flood low\",\"extended\":{\"usm\":{\"name\":\"flood low\"},\"inv\":{\"name\":\"flood low\"}}},{\"id\":753928,\"name\":\"red halo\",\"extended\":{\"usm\":{\"name\":\"red halo\"},\"inv\":{\"name\":\"red halo\"}}}]},{\"attr\":7465,\"vals\":[{\"id\":450305,\"name\":\"Yes\",\"extended\":{\"usm\":{\"name\":\"Yes\"},\"inv\":{\"name\":\"Yes\"}}}]},{\"attr\":7466,\"vals\":[{\"id\":481078,\"name\":\"USB powered\",\"extended\":{\"usm\":{\"name\":\"USB powered\"},\"inv\":{\"name\":\"USB powered\"}}},{\"id\":492760,\"name\":\"impact-resistant\",\"extended\":{\"usm\":{\"name\":\"impact-resistant\"},\"inv\":{\"name\":\"impact-resistant\"}}},{\"id\":509625,\"name\":\"collimator lens\",\"extended\":{\"usm\":{\"name\":\"collimator lens\"},\"inv\":{\"name\":\"collimator lens\"}}},{\"id\":753117,\"name\":\"T.I.R. Optic\",\"extended\":{\"usm\":{\"name\":\"T.I.R. Optic\"},\"inv\":{\"name\":\"T.I.R. Optic\"}}},{\"id\":782145,\"name\":\"safe drop height of 1 meter\",\"extended\":{\"usm\":{\"name\":\"safe drop height of 1 meter\"},\"inv\":{\"name\":\"safe drop height of 1 meter\"}}}]},{\"attr\":7468,\"vals\":[{\"set\":1,\"id\":645994,\"name\":\"spot high\",\"extended\":{\"usm\":{\"name\":\"spot high\"},\"inv\":{\"name\":\"spot high\"}}},{\"set\":2,\"id\":645995,\"name\":\"spot low\",\"extended\":{\"usm\":{\"name\":\"spot low\"},\"inv\":{\"name\":\"spot low\"}}},{\"set\":3,\"id\":753926,\"name\":\"flood high\",\"extended\":{\"usm\":{\"name\":\"flood high\"},\"inv\":{\"name\":\"flood high\"}}},{\"set\":4,\"id\":753927,\"name\":\"flood low\",\"extended\":{\"usm\":{\"name\":\"flood low\"},\"inv\":{\"name\":\"flood low\"}}},{\"set\":5,\"id\":753928,\"name\":\"red halo\",\"extended\":{\"usm\":{\"name\":\"red halo\"},\"inv\":{\"name\":\"red halo\"}}}]},{\"attr\":7470,\"vals\":[{\"set\":1,\"id\":2007,\"name\":\"57\",\"unit\":{\"id\":100,\"name\":\"m\"},\"extended\":{\"usm\":{\"name\":\"187\",\"unit\":\"ft\"},\"inv\":{\"name\":\"57\",\"unit\":\"m\"}}},{\"set\":2,\"id\":2124,\"name\":\"27\",\"unit\":{\"id\":100,\"name\":\"m\"},\"extended\":{\"usm\":{\"name\":\"89\",\"unit\":\"ft\"},\"inv\":{\"name\":\"27\",\"unit\":\"m\"}}},{\"set\":3,\"id\":2137,\"name\":\"11\",\"unit\":{\"id\":100,\"name\":\"m\"},\"extended\":{\"usm\":{\"name\":\"36\",\"unit\":\"ft\"},\"inv\":{\"name\":\"11\",\"unit\":\"m\"}}},{\"set\":4,\"id\":2146,\"name\":\"6\",\"unit\":{\"id\":100,\"name\":\"m\"},\"extended\":{\"usm\":{\"name\":\"19.7\",\"unit\":\"ft\"},\"inv\":{\"name\":\"6\",\"unit\":\"m\"}}},{\"set\":5,\"id\":2103,\"name\":\"1\",\"unit\":{\"id\":100,\"name\":\"m\"},\"extended\":{\"usm\":{\"name\":\"3.3\",\"unit\":\"ft\"},\"inv\":{\"name\":\"1\",\"unit\":\"m\"}}}]},{\"attr\":8091,\"vals\":[{\"id\":574158,\"name\":\"Yes\",\"extended\":{\"usm\":{\"name\":\"Yes\"},\"inv\":{\"name\":\"Yes\"}}}]}]}},\"debug\":[\"[2017-01-19 16:14:08 UTC] CC.PresentationEngine_IN_1, ver. 17.1.16.2 (+0.04, total 0.04)\",\"Call ID: eb055c (+0.03, total 0.07)\",\"Call type: LIVE (+0.02, total 0.09)\",\"Call version: PUBLISHED (+0.01, total 0.09)\",\"Publishing mode: $live (+0.01, total 0.10)\",\"Cache mode: normal (+0.01, total 0.11)\",\"Legacy localization: 'en-US' [1033] / 'US' [244] (+0.04, total 0.15)\",\"Request locale: 'EN-USA' (+0.02, total 0.17)\",\"TPD language: 'ez' (+0.01, total 0.18)\",\"Session #: 208 (new) (+0.64, total 0.82)\",\"Hook ID / version to display: '7f68abe3d9' [1] (+0.26, total 1.08)\",\"ProductInfoDataProvider: RESOLVE PRODUCT BY FCAT >> OK ... 0.45 ms (+0.53, total 1.61)\",\"RESOLVED PRODUCT:\",\"- manufacturer name / ID: 'Bushnell Outdoor Products' [1919]\",\"- product PN / master ID: '10R125' [1055ccda7f3a1512]\",\"- customer PN / catalog ID: none\",\" (+0.03, total 1.65)\",\"Total generation time: 2 ms (+0.90, total 2.54)\"]}";
//		System.out.println(parseJson(avus));
	}

	public static void show() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
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
		JTextField skuIdField = new JTextField(5);

		parameters = new JPanel(new SpringLayout());
		parameters.setBorder(BorderFactory.createTitledBorder("Parameters: "));

		setField(langLabel, langField);
		setField(marketLabel, marketField);
		setField(skuIdLabel, skuIdField);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(parameters, 3, 2, 6, 6, 6, 6);

		main.add(buttons);
		main.add(parameters);

		int result = JOptionPane.showConfirmDialog(null, main, "Enter your test parameters",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			String server = liveButton.isSelected() ? liveButton.getText() : txdev1Button.getText();
			
			String Json = parse(server, langField.getText(), marketField.getText(), getCleanedSku(skuIdField.getText()));
			String text = parseJson(Json);
			
			JTextPane textPane = new JTextPane();
			textPane.setContentType("text/html");
			textPane.setBackground(null);
			textPane.setOpaque(false);
			textPane.setBorder(null);
			textPane.setText(text);
			textPane.setEditable(false);
			JOptionPane.showMessageDialog(null, textPane, "some title", JOptionPane.PLAIN_MESSAGE);
			return;
		}

	}

	public static void setField(JLabel l, JTextField textField) {
		parameters.add(l);
		l.setLabelFor(textField);
		parameters.add(textField);
	}

	private static String parse(String server, String lang, String market, String skuId) {

		final String ENCODING = "UTF-8";
		StringBuilder sb = new StringBuilder();
		sb.append("http%3A%2F%2F");
		sb.append(server);
		sb.append(".cnetcontent.com%2FTps%3FParameters%3Dlang%3D");
		sb.append(lang);
		sb.append("%26market%3D");
		sb.append(market);
		sb.append("%26skuId%3D");
		sb.append(skuId);

		Document doc;
		try {
			String link = "http://txdev1.cnetcontent.com/Tps?Parameters=lang%3Den%26market%3DUS%26skuId%3D16102293";
			// doc = Jsoup.connect(link).timeout(10*1000).get();
			doc = Jsoup.parse(new URL(link).openStream(), ENCODING, link);
			Elements content = doc.select("#Avus_Result");

			for (Element element : content) {
				String avus = element.text();
				return avus;
			}

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
			if (!Character.isLetter(skuChars[i])) {
				sb.append(skuChars[i]);
			}
		}

		return sb.toString();
	}

	private static String parseJson(String input) {
		String indent = "\t";

		JSONObject obj = new JSONObject(input);
		JSONObject data = obj.getJSONObject("data");
		JSONObject txAvus = data.getJSONObject("tx-avus");
		JSONArray avus = txAvus.getJSONArray("avus");
		
		
		//////////////////////////////////////////////
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td colspan='2'>DEFAULT</td>");
		sb.append("<td colspan='2'>USM</td>");
		sb.append("<td colspan='2'>INV</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td class='header'>id</td>");
		sb.append("<td class='header'>name</td>");
		sb.append("<td class='header'>set</td>");
		sb.append("<td class='header'>value</td>");
		sb.append("<td class='header'>unit</td>");
		sb.append("<td class='header'>value</td>");
		sb.append("<td class='header'>unit</td>");
		sb.append("<td class='header'>value</td>");
		sb.append("<td class='header'>unit</td>");
		sb.append("</tr>");
		//////////////////////////////////////
		
		for (int i = 0; i < avus.length(); i++) {

			JSONObject attr = avus.getJSONObject(i);

			int attrId = attr.getInt("attr");
			System.out.println("attr id: " + attrId);

			JSONArray vals = attr.getJSONArray("vals");
			


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

				if (set==0 || set==1){
					sb.append("<td>");
					sb.append(attrId);
					sb.append("</td>");
					
					String name=null;
					List<Attribute> attibutes = TChecker.getAttributes();
					for (Attribute attribute : attibutes) {
						if (attribute.getId() == attrId) {
							name = attribute.getName();
						}
					}
					sb.append("<td>");
					sb.append(name);
					sb.append("</td>");	
					
					sb.append("<td>");
					if (set==0){
						sb.append("&nbsp;");
					} else if (set==1){						
						sb.append(set);
					}
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");
					sb.append("<td>&nbsp;</td>");
					sb.append("<td>");
					sb.append(set);
					sb.append("</td>");	
				}

				if (valueDefault!=null){
					sb.append("<td>");
					sb.append(valueDefault);
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");	
				}
				if (unitDefault!=null){
					sb.append("<td>");
					sb.append(unitDefault);
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");
				}

				if (valueUsm!=null){
					sb.append("<td>");
					sb.append(valueUsm);
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");	
				}
				if (unitUsm!=null){
					sb.append("<td>");
					sb.append(unitUsm);
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");
				}

				if (valueInv!=null){
					sb.append("<td>");
					sb.append(valueInv);
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");	
				}
				if (unitInv!=null){
					sb.append("<td>");
					sb.append(unitInv);
					sb.append("</td>");	
				} else {
					sb.append("<td>&nbsp;</td>");
				}

				sb.append("</tr>");
				
				////////////////////////////////////////
				
				StringBuilder sbNEW = new StringBuilder();
							
				
				sbNEW.append(indent);
				sbNEW.append("set: ");
				sbNEW.append(set);
				sbNEW.append("\n");
				
				// default
				sbNEW.append(indent);
				sbNEW.append("default: ");
				sbNEW.append("\n");
				
				sbNEW.append(indent);
				sbNEW.append(indent);
				sbNEW.append("value: ");
				sbNEW.append(valueDefault);
				sbNEW.append("\n");
				sbNEW.append(indent);
				sbNEW.append(indent);
				sbNEW.append("unit: ");
				sbNEW.append(unitDefault);
				sbNEW.append("\n");
				
				
				
				// usm
				sbNEW.append(indent);
				sbNEW.append("usm: ");
				sbNEW.append("\n");
				
				sbNEW.append(indent);
				sbNEW.append(indent);
				sbNEW.append("value: ");
				sbNEW.append(valueUsm);
				sbNEW.append("\n");
				sbNEW.append(indent);
				sbNEW.append(indent);
				sbNEW.append("unit: ");
				sbNEW.append(unitUsm);
				sbNEW.append("\n");
				
				
				
				// inv
				sbNEW.append(indent);
				sbNEW.append("inv: ");
				sbNEW.append("\n");
				
				sbNEW.append(indent);
				sbNEW.append(indent);
				sbNEW.append("value: ");
				sbNEW.append(valueInv);
				sbNEW.append("\n");
				sbNEW.append(indent);
				sbNEW.append(indent);
				sbNEW.append("unit: ");
				sbNEW.append(unitInv);
				sbNEW.append("\n");
				
				
				System.out.println(sbNEW.toString());
			}
		}
		
		sb.append("</tbody>");
		sb.append("</table>");
		
		return sb.toString();
	}
}
