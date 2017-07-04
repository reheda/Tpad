package ua.pp.hak.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ua.pp.hak.db.DatabaseUtils;
import ua.pp.hak.ui.DatabaseUpdateDialog;
import ua.pp.hak.util.Attribute;

@WebServlet("/db/*")
public class DatabaseServlet extends HttpServlet {

	final static Logger logger = LogManager.getLogger(DatabaseServlet.class);

	/**
	 * Store record "/photo"
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		logger.info("----------");

		logger.info("POST request. URI: " + req.getRequestURI());
		outputAll(req);

		boolean success = false;
		String message = "";
		PrintWriter out = resp.getWriter();
		JsonObject jsonAnswer = new JsonObject();

		if (!hasAccess(req)) {
			message = "Access is denied.";
			jsonAnswer.addProperty("success", success);
			jsonAnswer.addProperty("message", message);

			logger.info("Response: " + jsonAnswer.toString());
			out.write(jsonAnswer.toString());
			return;
		}

		RestRequest resourceValues = new RestRequest(req.getPathInfo());
		if (resourceValues.getId() == null) {

			String json = readRequest(req);

			logger.info("Request json: " + json);

			Attribute attribute = new Gson().fromJson(json, Attribute.class);

			if (attribute != null) {

				try {
					success = new DatabaseUtils().storeAttribute(attribute);
					if (success) {
						message = "Attribute " + attribute.getId() + " was added.";
					}
				} catch (SQLException e) {
					message = e.getMessage();
				}
			}
		} else {
			message = "Invalid URI.";
		}

		jsonAnswer.addProperty("success", success);
		jsonAnswer.addProperty("message", message);

		logger.info("Response: " + jsonAnswer.toString());
		out.write(jsonAnswer.toString());

		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}

		updateDatabase();

	}

	/**
	 * Update record "/photo/{photo}"
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		logger.info("----------");

		logger.info("PUT request. URI: " + req.getRequestURI());
		outputAll(req);

		JsonObject jsonAnswer = new JsonObject();
		boolean success = false;
		String message = "";
		PrintWriter out = resp.getWriter();

		if (!hasAccess(req)) {
			message = "Access is denied.";
			jsonAnswer.addProperty("success", success);
			jsonAnswer.addProperty("message", message);

			logger.info("Response: " + jsonAnswer.toString());
			out.write(jsonAnswer.toString());
			return;
		}

		RestRequest resourceValues = new RestRequest(req.getPathInfo());
		if (resourceValues.getId() != null) {

			String json = readRequest(req);

			logger.info("Request json: " + json);

			Attribute attribute = new Gson().fromJson(json, Attribute.class);

			logger.info(attribute);
			if (attribute != null) {
				try {
					success = new DatabaseUtils().updateAttribute(attribute);
					if (!success) {
						message = "Attribute " + attribute.getId() + " was not found to update.";
					} else {
						message = "Attribute " + attribute.getId() + " was updated.";
					}
				} catch (SQLException e) {
					message = e.getMessage();
				}
			}
		} else {
			message = "Invalid URI.";
		}

		jsonAnswer.addProperty("success", success);
		jsonAnswer.addProperty("message", message);

		logger.info("Response: " + jsonAnswer.toString());
		out.write(jsonAnswer.toString());

		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}
		
		updateDatabase();
	}

	/**
	 * Destroy record "/photo/{photo}"
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		logger.info("----------");

		logger.info("DELETE request. URI: " + req.getRequestURI());
		outputAll(req);

		JsonObject jsonAnswer = new JsonObject();
		boolean success = false;
		String message = "";
		PrintWriter out = resp.getWriter();

		if (!hasAccess(req)) {
			message = "Access is denied.";
			jsonAnswer.addProperty("success", success);
			jsonAnswer.addProperty("message", message);

			logger.info("Response: " + jsonAnswer.toString());
			out.write(jsonAnswer.toString());
			return;
		}

		try {

			RestRequest resourceValues = new RestRequest(req.getPathInfo());
			Integer id = resourceValues.getId();
			if (id != null) {

				String json = readRequest(req);

				logger.info("Request json: " + json);

				JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
				String dbName = jobj.get("dbName").getAsString();
				success = new DatabaseUtils().deleteAttribute(id, dbName);
				if (!success) {
					message = "Attribute " + id + " was not found to delete.";
				} else {
					message = "Attribute " + id + " was deleted.";
				}
			} else {
				message = "Invalid URI.";
			}
		} catch (ServletException e) {
			resp.setStatus(400);
			resp.resetBuffer();
			e.printStackTrace();
			message = e.toString();
		} finally {
			jsonAnswer.addProperty("success", success);
			jsonAnswer.addProperty("message", message);

			logger.info("Response: " + jsonAnswer.toString());
			out.write(jsonAnswer.toString());

			try {
				out.close();
			} catch (Exception e) {
				// do nothing
			}
		}

		updateDatabase();
	}

	/**
	 * Index records "/photo" Show record "/photo/{photo}"
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		logger.info("----------");
		logger.info("GET request. URI: " + req.getRequestURI());
		outputAll(req);

		boolean success = false;
		String message = "";
		JsonObject jsonAnswer = new JsonObject();

		PrintWriter out = resp.getWriter();
		try {
			RestRequest resourceValues = new RestRequest(req.getPathInfo());
			Integer id = resourceValues.getId();
			if (id != null) {
				Attribute attr = new DatabaseUtils().downloadAttribute(id);
				if (attr != null) {
					String responseStr = new Gson().toJson(attr);
					logger.info("Response: " + responseStr);
					out.write(responseStr);
				} else {
					message = "Attribute " + id + " was not found.";
					jsonAnswer.addProperty("success", success);
					jsonAnswer.addProperty("message", message);

					logger.info("Response: " + jsonAnswer.toString());
					out.write(jsonAnswer.toString());

				}
			} else {
				List<Attribute> attrs = new DatabaseUtils().downloadAttributes();
				out.write(new Gson().toJson(attrs));
			}
		} catch (ServletException e) {
			resp.setStatus(400);
			resp.resetBuffer();
			e.printStackTrace();
			message = e.toString();

			jsonAnswer.addProperty("success", success);
			jsonAnswer.addProperty("message", message);

			logger.info("Response: " + jsonAnswer.toString());
			out.write(jsonAnswer.toString());
		}

		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	private class RestRequest {
		// Accommodate two requests, one for all resources, another for a
		// specific resource
		private Pattern regExAllPattern = Pattern.compile("/attrs");
		private Pattern regExIdPattern = Pattern.compile("/attrs/([0-9]*)");

		private Integer id;

		public RestRequest(String pathInfo) throws ServletException {
			// if (pathInfo == null) {
			// throw new ServletException("Invalid URI");
			// }
			// regex parse pathInfo
			Matcher matcher;

			// Check for ID case first, since the All pattern would also match
			matcher = regExIdPattern.matcher(pathInfo);
			if (matcher.find()) {
				id = Integer.parseInt(matcher.group(1));
				return;
			}

			matcher = regExAllPattern.matcher(pathInfo);
			if (matcher.find())
				return;

			throw new ServletException("Invalid URI");
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}

	private boolean hasAccess(HttpServletRequest req) {
		String userToken = req.getHeader("usertoken");
		if (userToken == null
				|| !userToken.equals("2166d474feac44e1c801dd3ce4dff78ee32125de39f4065205a57165c6564040")) {
			return false;
		}
		return true;
	}

	private void outputAll(HttpServletRequest req) {
		Map map = req.getParameterMap();
		for (Object key : map.keySet()) {
			String keyStr = (String) key;
			String[] value = (String[]) map.get(keyStr);

			logger.info((String) key + " : " + Arrays.toString(value));
		}

		Enumeration headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();

			logger.info("Header Name - " + headerName + ", Value - " + req.getHeader(headerName));
		}

		Enumeration params = req.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = (String) params.nextElement();

			logger.info("Parameter Name - " + paramName + ", Value - " + req.getParameter(paramName));
		}
	}

	private synchronized String readRequest(HttpServletRequest req) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			/* report an error */
		}
		return jb.toString();
	}

	private void updateDatabase() {
		try {
			Timestamp webLastUpdate = new DatabaseUtils().downloadLastUpdate();
			if (webLastUpdate != null) {
				String formattedDate = DatabaseUtils.dateFormat.format(webLastUpdate);
				DatabaseUpdateDialog.updateDatabase(formattedDate);
			} else {
				logger.error("webLastUpdate is NULL!");
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
