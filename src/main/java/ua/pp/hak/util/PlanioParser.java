package ua.pp.hak.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.pp.hak.compiler.TChecker;
import ua.pp.hak.compiler.TParser;
import ua.pp.hak.ui.LoadingPanel;

public class PlanioParser {
	final static Logger logger = LogManager.getLogger(PlanioParser.class);
	private final static String CNET_CONTENT_ISSUES_URL = "https://claims.cnetcontent.com/issues/";
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

	private List<ExpressionObject> exprListWithErrors;
	private boolean outputExpressionCode;
	private boolean isReadyOnly;

	public String getResultPage(String[] inputLinks, boolean isReadyOnly) throws IOException {
		this.isReadyOnly = isReadyOnly;
		long start = System.nanoTime();
		exprListWithErrors = new ArrayList<>();
		logger.info("Check expression list...");

		// get distinct values
		Set<String> tempLinksSet = new HashSet<>(Arrays.asList(inputLinks));
		String[] distinctLinks = tempLinksSet.toArray(new String[tempLinksSet.size()]);

		// fill set with expressions
		Set<ExpressionObject> exprSet = new HashSet<>();
		for (int i = 0; i < distinctLinks.length; i++) {
			if (Thread.currentThread().isInterrupted()) {
				logger.warn("Canceling process...");
				break;
			}
			String json = getJson(distinctLinks[i], Include.CHILDREN);
			if (json != null) {
				fillExpressionSet(json, exprSet);
			}
		}

		// parse expression code to set
		parseExpressionCodesToSet(exprSet);

		// fill expression list witj errors
		fillExpresionListWithErrors(exprSet);

		// get result page
		String page = null;
		if (exprListWithErrors.isEmpty()) {
			page = generatePageWithoutErrors();
		} else {
			page = generatePage(exprListWithErrors);
		}

		logger.info("Finish check expression list.");
		long elapsedTime = System.nanoTime() - start;
		logger.info("Elapsed time to check expression list: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");

		return page;

	}

	public String getJournalExpression(String issueLink, String differencesLink) throws IOException {
		long start = System.nanoTime();
		logger.info("Rollback changes...");
		
		String[] values = differencesLink.trim().split("/");
		if (values.length < 2) {
			logger.error("Link is wrong: " + issueLink);
			return null;
		}

		int idJournal = 0;
		try {
			idJournal = Integer.parseInt(values[values.length - 2]);
		} catch (NumberFormatException e) {
			logger.error("Id is wrong: " + idJournal + " (" + issueLink + ")");
			return null;
		}

		String result = parseRolledExpression(issueLink, idJournal);
		
		logger.info("Finish rollback changes.");
		long elapsedTime = System.nanoTime() - start;
		logger.info("Elapsed time to rollback changes: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");
		
		return result;
	}

	private String parseRolledExpression(String issueLink, int idJournal) throws IOException {
		boolean isIdFound = false;
		String json = getJson(issueLink, Include.JOURNALS);
		if (json != null) {

			JSONObject jsonObj = new JSONObject(json);
			if (jsonObj.has("issue")) {

				JSONObject obj = jsonObj.getJSONObject("issue");

				if (obj.has("journals")) {
					JSONArray objJournals = obj.getJSONArray("journals");

					for (int i = 0; i < objJournals.length(); i++) {

						JSONObject diff = objJournals.getJSONObject(i);

						if (diff.has("id") && diff.getInt("id") == idJournal && diff.has("details")) {
							
							isIdFound = true;

							JSONArray objDetails = diff.getJSONArray("details");

							if (objDetails.length() == 1) {

								for (int j = 0; j < objDetails.length(); j++) {
									JSONObject objOldValue = objDetails.getJSONObject(j);

									// get data
									if (objOldValue.has("old_value")) {
										String oldExpression = objOldValue.getString("old_value");
										logger.info("Expression is restored successfully");
										return oldExpression;
									}
								}
							} else {
								logger.error("Json object 'details' length more than 1");

							}
						}

					}

				} else {
					logger.error("Json doesn't contain 'journals'");
				}

			} else {
				logger.error("Json doesn't contain 'issue'");
			}
		} else {
			logger.error("Json didn't parsed correctly");			
		}
		
		
		if (!isIdFound) {
			String error = "Journal ID is not found at the expression differences list!";
			logger.info(error);
			
			return error + "\n\nPlease make sure links are linked";
		}
		
		return null;
	}

	private String generatePageWithoutErrors() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(
				"<head><style> div.centered {text-align: center;} div.centered table { border-collapse: collapse; margin: 0 auto;  background-color: white; padding:5px;} tr {border-bottom: 1px solid #dddddd; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; border-bottom: none; } td { text-align: left; border-right: 1px solid #dddddd;} td.red { border-right: 1px solid #E03134; } td.cntr {text-align: center;} body {font-family:Segoe UI; font-size:9px; } </style></head>");
		sb.append("<body>");
		sb.append("<div class='centered'>");
		sb.append("<table>");
		sb.append("<tbody>");
		sb.append("<tbody>");
		sb.append("<tr class='header'><td>");
		sb.append("Message");
		sb.append("</td></tr>");
		sb.append("<tr><td>");
		sb.append("All expressions are valid");
		sb.append("</td></tr>");
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	private String generatePage(List<ExpressionObject> exprListWithErrors) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(
				"<head><style> div.centered {text-align: center;} div.centered table { border-collapse: collapse; margin: 0 auto;  background-color: white; padding:5px;} tr {border-bottom: 1px solid #dddddd; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; border-bottom: none; } td { text-align: left; border-right: 1px solid #dddddd;} td.red { border-right: 1px solid #E03134; } td.cntr {text-align: center;} body {font-family:Segoe UI; font-size:9px; } </style></head>");
		sb.append("<body>");
		sb.append("<div class='centered'>");
		sb.append("<table>");
		sb.append("<tbody>");
		sb.append("<tr class='header'>");
		sb.append("<td>Expression link</td>");
		if (outputExpressionCode) {
			sb.append("<td>Expression code</td>");
		}
		sb.append("<td class='red'>Error message</td>");
		sb.append("</tr>");

		for (ExpressionObject expr : exprListWithErrors) {
			sb.append("<tr>");
			String link = CNET_CONTENT_ISSUES_URL + expr.getId();
			sb.append("<td class='red'>");
			sb.append(link);
			sb.append("</td>");
			if (outputExpressionCode) {
				sb.append("<td>");
				sb.append("<pre>" + TParser.escapeHtml(expr.getExpressionCode()) + "</pre>");
				sb.append("</td>");
			}
			sb.append("<td>");
			String exprResult = expr.getExpressionResult();
			// shorten error message
			int endIndex = 0;
			if ((endIndex = exprResult.indexOf("-----")) > 0) {
				exprResult = exprResult.substring(0, endIndex).trim();
			}
			// ---------------------
			sb.append(TParser.escapeHtml(exprResult).replaceAll("\\n", "<br />"));
			sb.append("</td>");
			sb.append("</tr>");
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

	private String getEndRow() {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr class='header'>");
		int columns = 2;
		if (outputExpressionCode) {
			columns++;
		}
		for (int i = 0; i < columns; i++) {
			sb.append("<td class='red'>&nbsp;</td>");
		}
		sb.append("</tr>");

		return sb.toString();
	}

	private void fillExpresionListWithErrors(Set<ExpressionObject> exprSet) throws IOException {

		for (ExpressionObject exprObj : exprSet) {
			String exprCode = exprObj.getExpressionCode();
			if (exprCode == null) {
				continue;
			}

			Long id = exprObj.getId();
			boolean addExpression = true;

			// take only expression with status "[tx] ready"
			String status = exprObj.getStatus();
			if (isReadyOnly) {
				if (status == null || !status.contains("ready")) {
					addExpression = false;
					logger.info(CNET_CONTENT_ISSUES_URL + id + " - isn't checked due to status (" + status + ")");
				}
			}

			if (addExpression) {

				exprCode = exprCode.replace("<pre><code class=\"sql\">", "").replace("</code></pre>", "");

				// don't take empty expression
				if (!exprCode.trim().isEmpty()) {

					String result = TChecker.checkExpression(exprCode);
					String deactivateAttrNote = TChecker.checkDeactivatedAttributes(exprCode);
					if (result != null || deactivateAttrNote != null) {

						exprObj.setIsExpressionValid(false);
						exprObj.setExpressionResult(result != null ? result : deactivateAttrNote);
						exprListWithErrors.add(exprObj);
						logger.info(CNET_CONTENT_ISSUES_URL + id + " - not ok");

					} else {
						logger.info(CNET_CONTENT_ISSUES_URL + id + " - ok");
					}
				} else {
					logger.info(CNET_CONTENT_ISSUES_URL + id + " - empty");
				}
			}
		}

	}

	private void parseExpressionCodesToSet(Set<ExpressionObject> exprSet) throws IOException {

		// parse expression code
		int index = 1;
		JLabel label = LoadingPanel.getLabel();
		String oldProcessingLabelText = label.getText();
		for (ExpressionObject expressionObject : exprSet) {
			if (Thread.currentThread().isInterrupted()) {
				logger.warn("Canceling process...");
				break;
			}

			label.setText(oldProcessingLabelText.concat(" (" + (index) + "/" + exprSet.size() + ")"));

			// add "/" at the beginning to be valid
			String exprJson = getJson("/" + expressionObject.getId(), Include.CHILDREN);
			if (exprJson != null) {
				JSONObject jsonObj = new JSONObject(exprJson);
				if (jsonObj.has("issue")) {
					JSONObject obj = jsonObj.getJSONObject("issue");

					if (obj.has("description")) {
						String expressionCode = obj.getString("description");
						expressionObject.setExpressionCode(expressionCode);
					} else {
						logger.error("Json doesn't contain 'description'");
					}

					if (obj.has("status")) {
						JSONObject status = obj.getJSONObject("status");
						if (status.has("name")) {
							String statusName = status.getString("name");
							expressionObject.setStatus(statusName);
						} else {
							logger.error("Json doesn't contain 'name'");
						}
					} else {
						logger.error("Json doesn't contain 'status'");
					}
				} else {
					logger.error("Json doesn't contain 'issue'");
				}
			}
			index++;
		}

	}

	private void fillExpressionSet(String json, Set<ExpressionObject> exprSet) throws IOException {

		List<TemplateObject> objList = getTemplateList(json);

		for (TemplateObject templateObject : objList) {

			if (templateObject.getName().contains("expression")) {
				exprSet.add(new ExpressionObject(templateObject));
			}
		}

	}

	private List<TemplateObject> getTemplateList(String json) {

		List<TemplateObject> objList = new ArrayList<>();
		JSONObject jsonObj = new JSONObject(json);
		if (jsonObj.has("issue")) {

			JSONObject obj = jsonObj.getJSONObject("issue");

			if (obj.has("children")) {
				JSONArray objChildrens = obj.getJSONArray("children");

				for (int i = 0; i < objChildrens.length(); i++) {

					JSONObject objLvl2 = objChildrens.getJSONObject(i);

					if (objLvl2.has("children")) {
						JSONArray objLvl2Childrens = objLvl2.getJSONArray("children");

						for (int j = 0; j < objLvl2Childrens.length(); j++) {
							JSONObject objLvl3 = objLvl2Childrens.getJSONObject(j);

							JSONObject objLvl3Tracker = objLvl3.getJSONObject("tracker");

							// get data
							Long id = objLvl3.getLong("id");
							String subject = objLvl3.getString("subject");
							String name = objLvl3Tracker.getString("name");

							// add data to list
							objList.add(new TemplateObject(id, subject, name));

						}
					}

					JSONObject objLvl2Tracker = objLvl2.getJSONObject("tracker");

					// get data
					Long id = objLvl2.getLong("id");
					String subject = objLvl2.getString("subject");
					String name = objLvl2Tracker.getString("name");

					// add data to list
					objList.add(new TemplateObject(id, subject, name));
				}

			}

			JSONObject objTracker = obj.getJSONObject("tracker");

			// get data
			Long id = obj.getLong("id");
			String subject = obj.getString("subject");
			String name = objTracker.getString("name");
			;

			// add data to list
			objList.add(new TemplateObject(id, subject, name));
		} else {
			logger.error("Json doesn't contain 'issue'");
		}

		return objList;
	}

	private enum Include {
		CHILDREN("children"), JOURNALS("journals");
		private String value;

		Include(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private String getJson(String issueLink, Include value) throws IOException {

		String[] values = issueLink.trim().split("/");
		if (values.length < 1) {
			logger.error("Link is wrong: " + issueLink);
			return null;
		}

		String id = values[values.length - 1];
		if (!id.matches("\\d+")) {
			logger.error("Id is wrong: " + id + " (" + issueLink + ")");
			return null;
		}

		// section 106039
		// item 106172
		// expr 108583
		String serviceURL = "https://cnet.plan.io/issues/" + id + ".json?include=" + value.getValue();
		URL obj = new URL(serviceURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("User-Agent", USER_AGENT);
		String apiKey = "f917b0e9c0db3d976ba081f625e5e707c775e78d";
		con.setRequestProperty("X-Redmine-API-Key", apiKey);
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream(), Charsets.toCharset(encoding)));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			return response.toString();
		} else {
			logger.error("GET request not worked, " + CNET_CONTENT_ISSUES_URL + id);
			ExpressionObject eto = new ExpressionObject(Long.parseLong(id), null, null);
			eto.setExpressionResult("<font color='red'>Can't parse info from the page. Report it.</font>");
			exprListWithErrors.add(eto);
			return null;
		}

	}

	boolean isJSONValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException ex) {
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(json);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

}

class TemplateObject {

	private Long id;
	private String subject;
	private String name;

	public TemplateObject() {
		// TODO Auto-generated constructor stub
	}

	public TemplateObject(Long id, String subject, String name) {
		this.id = id;
		this.subject = subject;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "TemplateObject [id=" + id + ", subject=" + subject + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateObject other = (TemplateObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

}

class ExpressionObject extends TemplateObject {
	private String expressionCode;
	private String expressionResult;
	private boolean isExpressionValid = true;
	private String status;

	public ExpressionObject(Long id, String subject, String name) {
		super(id, subject, name);
	}

	public ExpressionObject(TemplateObject tObj) {
		super(tObj.getId(), tObj.getSubject(), tObj.getName());
	}

	public String getExpressionCode() {
		return expressionCode;
	}

	public void setExpressionCode(String expressionCode) {
		this.expressionCode = expressionCode;
	}

	public String getExpressionResult() {
		return expressionResult;
	}

	public void setExpressionResult(String expressionResult) {
		this.expressionResult = expressionResult;
	}

	public boolean getIsExpressionValid() {
		return isExpressionValid;
	}

	public void setIsExpressionValid(boolean isExpressionValid) {
		this.isExpressionValid = isExpressionValid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ExpressionObject [expressionCode=" + expressionCode + ", expressionResult=" + expressionResult
				+ ", isExpressionValid=" + isExpressionValid + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expressionCode == null) ? 0 : expressionCode.hashCode());
		result = prime * result + ((expressionResult == null) ? 0 : expressionResult.hashCode());
		result = prime * result + (isExpressionValid ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionObject other = (ExpressionObject) obj;
		if (expressionCode == null) {
			if (other.expressionCode != null)
				return false;
		} else if (!expressionCode.equals(other.expressionCode))
			return false;
		if (expressionResult == null) {
			if (other.expressionResult != null)
				return false;
		} else if (!expressionResult.equals(other.expressionResult))
			return false;
		if (isExpressionValid != other.isExpressionValid)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

}