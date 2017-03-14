package ua.pp.hak.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiVortexParser {
	private static final int TIMEOUT_MILLIS = 10 * 1000;

	public static void main(String[] args) throws MalformedURLException, IOException {
		long start = System.nanoTime();
		System.out.println("Start parsing attributes...");

		List<Attribute> allAttributes = new ArrayList<>();
		List<String> categories = new ArrayList<>();

		// parse Attributes
		parseAttributesInfo(allAttributes, categories);
		System.out.println("Data were parsed.");

		// generate page
		String page = generatePage(allAttributes, categories);

		// save to file
		saveToFile(page);

		// output elapsed time
		long elapsedTime = System.nanoTime() - start;
		System.out.println();
		System.out.println("Elapsed time: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");
	}

	/**
	 * @param allAttributes
	 * @param categories
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void parseAttributesInfo(List<Attribute> allAttributes, List<String> categories)
			throws MalformedURLException, IOException {
		float total = Legacy.getLegacy().keySet().size();
		int counter = 0;
		for (String category : Legacy.getLegacy().keySet()) {
			System.out.print("Legacy: " + (++counter) + "/" + (int) total + " ("
					+ new DecimalFormat("#.#").format((counter / total) * 100) + "%)");
			if (category.length() == 2) {
				Map<String, String> groupMap = parseGroups(category);

				for (Map.Entry<String, String> entry : groupMap.entrySet()) {
					List<Attribute> attrs = parseAttributes(category, entry.getKey(), entry.getValue());
					allAttributes.addAll(attrs);

					// add categories to output
					for (int i = 0; i < attrs.size(); i++) {
						categories.add(category);
					}
				}
			}
			System.out.println();
		}
	}

	/**
	 * @param page
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void saveToFile(String page)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		// get current date
		String date = getFormattedDate();
		
		String fileName = "attributes_" + date + ".html";
		Writer fout = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		fout.write(page);
		fout.close();
		System.out.println("Data were saved to '"+fileName+"'.");
	}

	private static String getFormattedDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		String date = dateFormat.format(currentDate);
		return date;
	}

	private static Map<String, String> parseGroups(String category) throws MalformedURLException, IOException {
		Map<String, String> groupMap = new LinkedHashMap<>();
		Document doc = parseDoc(category, null);
		if (doc != null) {
			Elements el = doc.select("#article table:nth-of-type(2) td:nth-of-type(2) td");

			// Elements groupIds = el.select("span");
			Elements attrHrefs = el.select("a");

			for (Element element : attrHrefs) {
				String groupName = element.text();
				String href = element.attr("abs:href");
				String groupId = href.replaceAll(".*group_id=(G\\d+).*", "$1");
				groupMap.put(groupId, groupName);
			}

			// System.out.println(groupMap);

		}
		return groupMap;
	}

	private static List<Attribute> parseAttributes(String category, String groupId, String groupName)
			throws MalformedURLException, IOException {
		List<Attribute> attributes = new LinkedList<>();
		Document doc = parseDoc(category, groupId);
		if (doc != null) {
			Elements el = doc.select("#article table:nth-of-type(2) td:nth-of-type(2) td");

			for (Element td : el) {
				// set isDeactivated
				boolean isDeactivated = false;
				Elements imgs = td.select("img");
				for (Element img : imgs) {
					String imgSrc = img.attr("src");
					if (imgSrc.contains("gray")) {
						isDeactivated = true;
					}
				}

				// Elements groupIds = el.select("span");
				Elements attrHrefs = td.select("a");

				for (Element element : attrHrefs) {

					String href = element.attr("abs:href");
					if (href.contains("&attrib_id=")) {
						String attrName = element.text();
						String attrId = href.replaceAll(".*attrib_id=A(\\d+).*", "$1");
						int attrIdInt = Integer.parseInt(attrId);
						int groupIdInt = Integer.parseInt(groupId.substring(1));
						Attribute attr = new Attribute(attrIdInt, "", attrName, isDeactivated, groupIdInt, groupName, null);
						attributes.add(attr);
					}
				}
			}
			// for visibility of process
			System.out.print(".");
			// System.out.println(attributes);
		}
		return attributes;
	}

	private static Document parseDoc(String category, String groupId) throws MalformedURLException, IOException {

		String catId = category.trim().toUpperCase();
		if (catId.length() != 2) {
			System.err.println("Wrong format of Category Id '" + category + "'.");
			return null;
		}

		String link = null;
		if (groupId == null) {
			link = generateLink(catId);
		} else if (groupId.matches("G\\d+")) {
			link = generateLink(catId, groupId);
		} else {
			System.err.println("Wrong format of Group Id '" + groupId + "'.");
			return null;
		}

		Map<String, String> loginCookies = generateCookies();

		Document doc = Jsoup.connect(link).cookies(loginCookies).timeout(TIMEOUT_MILLIS).get();
		return doc;

	}

	private static Map<String, String> generateCookies() {
		Map<String, String> loginCookies = new HashMap<>();
		String key = "tpad-wiki-login";
		String login = "FxkKCFgaTF4qWx4KDwwKFQ==";
		String hashPass = "QhFQUB5AXVhRHA0NUVlXRUlWUkgRUF5RHAlcAVkLQEU=";
		loginCookies.put("udbPasswordHash", EncryptUtils.xorMessage(EncryptUtils.base64decode(hashPass), key));
		loginCookies.put("udbUserName", EncryptUtils.xorMessage(EncryptUtils.base64decode(login), key));
		return loginCookies;
	}

	private static String generateLink(String catId) {
		String classId = catId.substring(0, 1);
		StringBuilder sb = new StringBuilder();
		sb.append("http://vxwiki.cnetcontentsolutions.com/index.php?title=PDM_Help_Tree&cat_group=all");
		sb.append("&class_id=");
		sb.append(classId);
		sb.append("&cat_id=");
		sb.append(catId);
		sb.append("&deactivated=1");
		return sb.toString();
	}

	private static String generateLink(String catId, String groupId) {
		String classId = catId.substring(0, 1);
		StringBuilder sb = new StringBuilder();
		sb.append("http://vxwiki.cnetcontentsolutions.com/index.php?title=PDM_Help_Tree&cat_group=all");
		sb.append("&class_id=");
		sb.append(classId);
		sb.append("&cat_id=");
		sb.append(catId);
		sb.append("&group_id=");
		sb.append(groupId);
		sb.append("&deactivated=1");
		return sb.toString();
	}

	private static String generatePage(List<Attribute> allAttributes, List<String> categories) {

		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(
				"<head><style> div.centered {text-align: center;} div.centered table { border-collapse: collapse; margin: 0 auto;  background-color: white; padding:5px;} tr {border-bottom: 1px solid #dddddd; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; border-bottom: none; } td { text-align: left; border-right: 1px solid #dddddd;} td.red { border-right: 1px solid #E03134; } td.cntr {text-align: center;} body {font-family:Segoe UI; font-size:9px; } </style></head>");
		sb.append("<body>");
		sb.append("<div class='centered'>");
		sb.append("<table>");
		sb.append("<tbody>");
		sb.append("<tr class='header'>");
		sb.append("<td>Legacy</td>");
		sb.append("<td class='red'>Group Id</td>");
		sb.append("<td>Group name</td>");
		sb.append("<td class='red'>Attr Id</td>");
		sb.append("<td>Attr name</td>");
		sb.append("<td>Is deactivated?</td>");
		sb.append("</tr>");

		//////////////////////////////////////
		int counter = 0;
		for (Attribute attribute : allAttributes) {
			sb.append("<tr>");
			sb.append("<td class='red'>");
			sb.append(categories.get(counter));
			sb.append("</td>");
			sb.append("<td>");
			sb.append(attribute.getGroupId());
			sb.append("</td>");
			sb.append("<td class='red'>");
			sb.append(attribute.getGroupName());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(attribute.getId());
			sb.append("</td>");
			sb.append("<td class='red'>");
			sb.append(attribute.getName());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(attribute.isDeactivated());
			sb.append("</td>");
			sb.append("</tr>");

			counter++;
		}

		//////////////////////////////////////
		sb.append(getEndRow());
		//////////////////////////////////////
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private static String getEndRow() {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr class='header'>");
		int columns = 6;
		for (int i = 0; i < columns; i++) {
			sb.append("<td class='red'>&nbsp;</td>");
		}
		sb.append("</tr>");

		return sb.toString();
	}
}
