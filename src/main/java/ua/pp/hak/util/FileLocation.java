package ua.pp.hak.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileLocation {
	final static Logger logger = LogManager.getLogger(FileLocation.class);

	public static String getJarOrNotPath(String resource) {
		File file = null;
		URL res = FileLocation.class.getClass().getResource(resource);
		if (res.toString().startsWith("jar:")) {
			try {
				InputStream input = FileLocation.class.getClass().getResourceAsStream(resource);
				file = File.createTempFile("tempfile", ".tmp");
				OutputStream out = new FileOutputStream(file);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.close();
				file.deleteOnExit();
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		} else {
			// this will probably work in your IDE, but not from a JAR
			String filePath;
			try {
				filePath = URLDecoder.decode(res.getFile(), "UTF-8");
				file = new File(filePath);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			}
		}

		if (file != null && !file.exists()) {
			throw new RuntimeException("Error: File " + file + " not found!");

		}
		return file.getPath();
	}
}
