package ua.pp.hak.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ua.pp.hak.util.Attribute;

public class DatabaseUtils {
	public void connectToAndQueryDatabase(String username, String password) {

		List<Attribute> attributes = new ArrayList<>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			con = DriverManager.getConnection("jdbc:mysql://host:post/database", username,
					password);

			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"SELECT id, type, name, deactivated, group-id, group-name, last-update FROM attributes");

			while (rs.next()) {
				int id = rs.getInt("id");
				String type = rs.getString("type");
				String name = rs.getString("name");
				boolean isDeactivated = Boolean.valueOf(rs.getString("deactivated"));
				int groupId = rs.getInt("group-id");
				String groupName = rs.getString("group-name");
				String lastUpdate = rs.getString("last-update");

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

		System.out.println("print...");
		for (Attribute attribute : attributes) {
			System.out.println(attribute);
		}

	}

	public static void main(String[] args) {
		new DatabaseUtils().connectToAndQueryHerokuDatabase("username", "password");
	}

	public void connectToAndQueryHerokuDatabase(String username, String password) {
		class Account {
			String userName;
			boolean active;
			String passWord;
			String userRole;

			public Account(String userName, boolean active, String passWord, String userRole) {
				this.userName = userName;
				this.active = active;
				this.passWord = passWord;
				this.userRole = userRole;
			}

			@Override
			public String toString() {
				return "Account [userName=" + userName + ", active=" + active + ", passWord=" + passWord + ", userRole="
						+ userRole + "]";
			}

		}
		
		List<Account> accounts = new ArrayList<>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("org.postgresql.Driver");

			con = DriverManager.getConnection("jdbc:postgresql://ec2-23-23-227-188.compute-1.amazonaws.com:5432/d5p2gs52oqornv?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory&sslmode=require", username,
					password);

			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"SELECT * FROM accounts");

			while (rs.next()) {
				String userName = rs.getString("User_Name");
				boolean active = rs.getBoolean("Active");
				String passWord = rs.getString("Password");
				String userRole = rs.getString("User_Role");

				// add to list
				accounts.add(new Account(userName, active, passWord, userRole));

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

		
		System.out.println("print...");
		for (Account account : accounts) {
			System.out.println(account);
		}
		
	}
}
