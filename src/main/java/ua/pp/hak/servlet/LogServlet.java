package ua.pp.hak.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/log")
public class LogServlet extends HttpServlet {
	final static String encoding = "UTF-8";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();

		String logs = readLogs(new File("logs/app.log"));
		out.write(logs);
		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	private String readLogs(File temp) {
		if (temp == null || !temp.exists()) {
			return null;
		}

		FileInputStream fin = null;
		BufferedReader din = null;

		StringBuilder sb = new StringBuilder();
		try {
			fin = new FileInputStream(temp);
			din = new BufferedReader(new InputStreamReader(fin, encoding));
			String str = " ";
			while (str != null) {
				str = din.readLine();
				if (str == null)
					break;

				sb.append(str);
				sb.append(System.getProperty("line.separator"));
			}

		} catch (IOException ioe) {

		} finally {
			try {
				din.close();
				fin.close();
			} catch (IOException excp) {

			} finally {
				if (sb.length() > 0) {
					return sb.toString();
				}
			}

		}
		return null;
	}
}
