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

		String logs = readLogs(new File("logs/app.log"), 500);
		if (logs == null) {
			logs = "";
		}
		out.write(logs);
		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	private String readLogs(File file, int lines) {
		// https://stackoverflow.com/a/7322581/6346515

		// If your line endings are \r\n or crlf or some other "double newline
		// style newline", then you will have to specify n*2 lines to get the
		// last n lines because it counts 2 lines for every line.
		lines *= 2;

		if (file == null || !file.exists()) {
			return null;
		}

		java.io.RandomAccessFile fileHandler = null;
		try {
			fileHandler = new java.io.RandomAccessFile(file, "r");
			long fileLength = fileHandler.length() - 1;
			StringBuilder sb = new StringBuilder();
			int line = 0;

			for (long filePointer = fileLength; filePointer != -1; filePointer--) {
				fileHandler.seek(filePointer);
				int readByte = fileHandler.readByte();

				if (readByte == 0xA) {
					if (filePointer < fileLength) {
						line = line + 1;
					}
				} else if (readByte == 0xD) {
					if (filePointer < fileLength - 1) {
						line = line + 1;
					}
				}
				if (line >= lines) {
					break;
				}
				sb.append((char) readByte);
			}

			String lastLine = sb.reverse().toString();
			return lastLine;
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fileHandler != null)
				try {
					fileHandler.close();
				} catch (IOException e) {
				}
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
