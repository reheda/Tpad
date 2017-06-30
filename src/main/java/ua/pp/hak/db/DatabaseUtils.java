package ua.pp.hak.db;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

	private String getHashUrl(String dbName) {
		if (dbName == null) {
			return null;
		}
		String data = null;
		try {
			data = getData(dbHashURL);
		} catch (Exception e) {
			logger.error(e);
		}

		if (data != null) {
			return data.substring(data.indexOf("[" + dbName + "]") + dbName.length() + 2,
					data.indexOf("[/" + dbName + "]"));
		}

		return null;
	}

	public List<Attribute> downloadAttributes() {
		List<Attribute> attributes = new ArrayList<>();

		try {

			final String hashTpadWeb = getHashUrl("tpadweb");
			final String hashTpadWebDb = getHashUrl("tpadwebdb");

			if (hashTpadWeb == null || hashTpadWebDb == null) {
				return attributes;
			}
			attributes.addAll(downloadAttributes(hashTpadWeb, "tpadweb"));
			attributes.addAll(downloadAttributes(hashTpadWebDb, "tpadwebdb"));

		} catch (Exception e) {
			logger.error(e);
		}

		return attributes;
	}

	public boolean storeAttribute(Attribute attribute) throws SQLException {

		Connection con = null;
		PreparedStatement stmt = null;
		int affectedRows = 0;

		try {
			Class.forName("org.postgresql.Driver");

			String hashUrl = getHashUrl("tpadweb");
			if (hashUrl == null) {
				return false;
			}
			con = getConnection(hashUrl);
			stmt = con.prepareStatement(
					"INSERT INTO attributes (id,type,name,deactivated,group_id,group_name,comment) VALUES (?,?::\"enum_types\",?,?,?,?,?)");
			stmt.setInt(1, attribute.getId());
			stmt.setString(2, attribute.getType());
			stmt.setString(3, attribute.getName());
			stmt.setBoolean(4, attribute.isDeactivated());
			stmt.setInt(5, attribute.getGroupId());
			stmt.setString(6, attribute.getGroupName());
			stmt.setString(7, attribute.getComment());
			affectedRows = stmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} finally {
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

		if (affectedRows > 0) {
			return true;
		}
		return false;
	}

	public boolean updateAttribute(Attribute attribute) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		int affectedRows = 0;

		try {
			Class.forName("org.postgresql.Driver");

			String hashUrl = getHashUrl(attribute.getDbName());
			if (hashUrl == null) {
				return false;
			}
			con = getConnection(hashUrl);

			stmt = con.prepareStatement(
					"UPDATE attributes SET type=?::\"enum_types\",name=?,deactivated=?,group_id=?,group_name=?,last_update=DEFAULT,comment=? WHERE id=?");

			stmt.setString(1, attribute.getType());
			stmt.setString(2, attribute.getName());
			stmt.setBoolean(3, attribute.isDeactivated());
			stmt.setInt(4, attribute.getGroupId());
			stmt.setString(5, attribute.getGroupName());
			stmt.setString(6, attribute.getComment());
			stmt.setInt(7, attribute.getId());
			affectedRows = stmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} finally {
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

		if (affectedRows > 0) {
			return true;
		}
		return false;
	}

	public boolean deleteAttribute(Integer id, String dbName) {
		Connection con = null;
		PreparedStatement stmt = null;
		int affectedRows = 0;

		try {
			Class.forName("org.postgresql.Driver");

			String hashUrl = getHashUrl(dbName);
			if (hashUrl == null) {
				return false;
			}
			con = getConnection(hashUrl);

			stmt = con.prepareStatement("DELETE FROM attributes WHERE id=?");
			stmt.setInt(1, id);
			affectedRows = stmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		} finally {
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

		if (affectedRows > 0) {
			return true;
		}
		return false;
	}

	public Attribute downloadAttribute(Integer attrId) {
		List<Attribute> attributes = new ArrayList<>();

		try {

			final String hashTpadWeb = getHashUrl("tpadweb");
			final String hashTpadWebDb = getHashUrl("tpadwebdb");

			if (hashTpadWeb == null || hashTpadWebDb == null) {
				return null;
			}
			attributes.addAll(downloadAttribute(attrId, hashTpadWeb, "tpadweb"));
			attributes.addAll(downloadAttribute(attrId, hashTpadWebDb, "tpadwebdb"));

		} catch (Exception e) {
			logger.error(e);
		}

		if (attributes.size() > 0) {
			return attributes.get(0);
		}
		return null;
	}

	private List<Attribute> downloadAttribute(Integer attrId, String hashUrl, String dbName) {
		List<Attribute> attributes = new ArrayList<>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("org.postgresql.Driver");

			con = getConnection(hashUrl);

			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM attributes WHERE id=" + attrId);

			while (rs.next()) {
				int id = rs.getInt("id");
				String type = rs.getString("type");
				String name = rs.getString("name");
				boolean isDeactivated = rs.getBoolean("deactivated");
				int groupId = rs.getInt("group_id");
				String groupName = rs.getString("group_name");
				String lastUpdate = rs.getString("last_update");
				String comment = rs.getString("comment");

				// add to list
				attributes.add(
						new Attribute(id, type, name, isDeactivated, groupId, groupName, lastUpdate, comment, dbName));

			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
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

	private List<Attribute> downloadAttributes(String hashUrl, String dbName) {
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
				String comment = rs.getString("comment");

				// add to list
				attributes.add(
						new Attribute(id, type, name, isDeactivated, groupId, groupName, lastUpdate, comment, dbName));

			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
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

		Timestamp ts1 = null;
		Timestamp ts2 = null;
		try {
			final String hashTpadWeb = getHashUrl("tpadweb");
			final String hashTpadWebDb = getHashUrl("tpadwebdb");

			if (hashTpadWeb == null || hashTpadWebDb == null) {
				return null;
			}
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

	private Timestamp downloadLastUpdate(String hashUrl) {
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
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
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
