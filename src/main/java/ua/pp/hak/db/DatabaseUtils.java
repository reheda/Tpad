package ua.pp.hak.db;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.Attribute;
import ua.pp.hak.util.EncryptUtils;

public class DatabaseUtils {
	final static Logger logger = LogManager.getLogger(DatabaseUtils.class);
	private final static String dbHashURL = "http://tpad.hak.pp.ua/db-hash.html";
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static Connection getConnection(String hashUrl) throws SQLException {
		String key = "tpad-pg";
		String dbUrl = EncryptUtils.xorMessage(EncryptUtils.base64decode(hashUrl), key);

		return DriverManager.getConnection(dbUrl);
	}

	private static String getData(String address) throws Exception {
		URL url = new URL(address);

		InputStream html = null;

		html = url.openStream();

		int c = 0;
		StringBuffer buffer = new StringBuffer("");

		while (c != -1) {
			c = html.read();

			buffer.append((char) c);
		}
		return buffer.toString();
	}

	public List<Attribute> downloadAttributes() {
		List<Attribute> attributes = new ArrayList<>();
		String data = null;
		try {
			data = getData(dbHashURL);
		} catch (Exception e) {
			logger.error(e);
		}
		if (data == null) {
			return attributes;
		}

		try {

			final String hashTpadWeb = data.substring(data.indexOf("[tpadweb]") + 9, data.indexOf("[/tpadweb]"));
			final String hashTpadWebDb = data.substring(data.indexOf("[tpadwebdb]") + 11, data.indexOf("[/tpadwebdb]"));

			attributes.addAll(downloadAttributes(hashTpadWeb));
			attributes.addAll(downloadAttributes(hashTpadWebDb));

		} catch (Exception e) {
			logger.error(e);
		}

		return attributes;
	}

	public List<Attribute> downloadAttributes(String hashUrl) {
		List<Attribute> attributes = new ArrayList<>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("org.postgresql.Driver");

			con = getConnection(hashUrl);

			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM attributes");

			while (rs.next()) {
				int id = rs.getInt("id");
				String type = rs.getString("type");
				String name = rs.getString("name");
				boolean isDeactivated = rs.getBoolean("deactivated");
				int groupId = rs.getInt("group_id");
				String groupName = rs.getString("group_name");
				String lastUpdate = rs.getString("last_update");

				// add to list
				attributes.add(new Attribute(id, type, name, isDeactivated, groupId, groupName, lastUpdate));

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// do nothing
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				// do nothing
			}
			try {
				con.close();
			} catch (SQLException e) {
				// do nothing
			}
		}

		return attributes;
	}

	public Timestamp downloadLastUpdate() {
		String data = null;
		try {
			data = getData(dbHashURL);
		} catch (Exception e) {
			logger.error(e);
		}
		if (data == null) {
			return null;
		}

		Timestamp ts1 = null;
		Timestamp ts2 = null;
		try {
			final String hashTpadWeb = data.substring(data.indexOf("[tpadweb]") + 9, data.indexOf("[/tpadweb]"));
			final String hashTpadWebDb = data.substring(data.indexOf("[tpadwebdb]") + 11, data.indexOf("[/tpadwebdb]"));

			ts1 = downloadLastUpdate(hashTpadWeb);
			ts2 = downloadLastUpdate(hashTpadWebDb);
		} catch (Exception e) {
			logger.error(e);
		}

		if (ts1 != null && ts2 != null) {
			return ts1.before(ts2) ? ts2 : ts1;
		}

		return null;

	}

	public Timestamp downloadLastUpdate(String hashUrl) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		Timestamp lastUpdate = null;
		try {
			Class.forName("org.postgresql.Driver");

			con = getConnection(hashUrl);

			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT MAX(updated) as max FROM update_log where table_name='attributes'");

			while (rs.next()) {
				lastUpdate = new Timestamp(rs.getTimestamp("max").getTime());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// do nothing
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				// do nothing
			}
			try {
				con.close();
			} catch (SQLException e) {
				// do nothing
			}
		}

		return lastUpdate;
	}
}
