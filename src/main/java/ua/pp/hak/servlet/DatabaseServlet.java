package ua.pp.hak.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

import ua.pp.hak.db.DatabaseUtils;
import ua.pp.hak.util.Attribute;

@WebServlet("/db/*")
public class DatabaseServlet extends HttpServlet {

	final static Logger logger = LogManager.getLogger(DatabaseServlet.class);
	// @Override
	// protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	// throws ServletException, IOException {
	// String json = new Gson().toJson(new
	// DatabaseUtils().downloadAttributes());
	// resp.getWriter().write(json);
	//
	// }

	// @Override
	// protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	// throws ServletException, IOException {
	// String json = new Gson().toJson(new
	// DatabaseUtils().downloadAttributes());
	// resp.getWriter().write(json);
	// }

	/**
	 * Store record "/photo"
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
		// resp.addHeader("Access-Control-Allow-Headers","Origin, Content-Type,
		// X-Auth-Token, X-Requested-With");
		resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
		System.out.println("post");
		System.out.println(req.getParameter("comment"));
		// Map map = req.getParameterMap();
		// for (Object key: map.keySet())
		// {
		// String keyStr = (String)key;
		// String[] value = (String[])map.get(keyStr);
		// System.out.println((String)key + " : " + Arrays.toString(value));
		// }
		resp.getWriter().write(new Gson().toJson("post"));
	}

	/**
	 * Update record "/photo/{photo}"
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("put");
		System.out.println("comment=" + req.getParameter("comment"));
		Map map = req.getParameterMap();
		for (Object key : map.keySet()) {
			String keyStr = (String) key;
			String[] value = (String[]) map.get(keyStr);
			System.out.println((String) key + "   :   " + Arrays.toString(value));
		}
		resp.getWriter().write(new Gson().toJson("put"));
	}

	/**
	 * Destroy record "/photo/{photo}"
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("delete");
		System.out.println("comment=" + req.getParameter("comment"));
		System.out.println(req.getRequestURI());

		Enumeration headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			System.out.println("Header Name - " + headerName + ", Value - " + req.getHeader(headerName));
		}

		Enumeration params = req.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = (String) params.nextElement();
			System.out.println("Parameter Name - " + paramName + ", Value - " + req.getParameter(paramName));
		}

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			/* report an error */ }

		System.out.println(jb.toString());
		
		System.out.println(new Gson().fromJson(jb.toString(), Attribute.class));
		
		resp.getWriter().write(new Gson().toJson("delete"));
	}

	/**
	 * Index records "/photo" Show record "/photo/{photo}"
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		out.println("GET request handling");
		out.println(request.getPathInfo());
		out.println(request.getParameterMap());
		try {
			RestRequest resourceValues = new RestRequest(request.getPathInfo());
			Integer id = resourceValues.getId();
			out.println(id);
			if (id != null) {
				Attribute attr = new DatabaseUtils().downloadAttribute(id);
				if (attr != null) {
					out.println(new Gson().toJson(attr));
				} else {
					out.println("cant download attr");

				}
			} else {
				List<Attribute> attrs = new DatabaseUtils().downloadAttributes();
				out.println(new Gson().toJson(attrs));
			}
		} catch (ServletException e) {
			response.setStatus(400);
			response.resetBuffer();
			e.printStackTrace();
			out.println(e.toString());
		}
		out.close();
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

}
