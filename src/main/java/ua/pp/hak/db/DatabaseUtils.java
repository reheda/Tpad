package ua.pp.hak.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ua.pp.hak.util.Attribute;
import ua.pp.hak.util.EncryptUtils;

public class DatabaseUtils {

	private static Connection getConnection() throws SQLException {
		String key = "tpad-pg";
		String hashUrl = "HhQDBxcACAcEBhZIAxYYSk5LSBNVWUVRSRxJSkZBWUkcRldaEw4JXQUTEV1QSkwdBg4fDwVaA0kXHwxeGERURl8FBxpDURoGDlxYAQARRl4RXhUVSRUNAl4VFREaAA9fBRYGVhEFXgMQGwIFWR4SAUBHUlAVQ18WQ1JTTEMDEhYEABpAXhdIA1EcQVQQFVJSHEAGEEMHBkxIUBdIWQEYQARHQAMGTElfEElYARRWFAccDAtJFVoGFRARRAIC";
		String dbUrl = EncryptUtils.xorMessage(EncryptUtils.base64decode(hashUrl), key);

		return DriverManager.getConnection(dbUrl);
	}

	public static void main(String[] args) {
		new DatabaseUtils().connectToAndQueryHerokuDatabase();
	}

	public void connectToAndQueryHerokuDatabase() {
		long start, elapsedTime;
		start = System.nanoTime();
		List<Attribute> attributes = new ArrayList<>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("org.postgresql.Driver");

			con = getConnection();

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
				e.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Save...");

		DatabaseStAXWriter.save(attributes);
		elapsedTime = System.nanoTime() - start;
		System.out.println("Elapsed time to check: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");
	}
}
