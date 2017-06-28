package ua.pp.hak.util;

public class Attribute {
	private int id;
	private String type;
	private String name;
	private int groupId;
	private String groupName;
	private String lastUpdate;
	private boolean isDeactivated;
	private String comment;
	private String dbName;

	public Attribute(int id, String type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public Attribute(int id, String type, String name, boolean isDeactivated, int groupId, String groupName,
			String lastUpdate) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.isDeactivated = isDeactivated;
		this.groupId = groupId;
		this.groupName = groupName;
		this.lastUpdate = lastUpdate;
	}

	public Attribute(int id, String type, String name, boolean isDeactivated, int groupId, String groupName,
			String lastUpdate, String comment, String dbName) {
		this(id, type, name, isDeactivated, groupId, groupName, lastUpdate);
		this.comment = comment;
		this.dbName = dbName;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public boolean isDeactivated() {
		return isDeactivated;
	}

	public int getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public String getComment() {
		return comment;
	}

	public String getDbName() {
		return dbName;
	}

	@Override
	public String toString() {
		return "Attribute [id=" + id + ", type=" + type + ", name=" + name + ", groupId=" + groupId + ", groupName="
				+ groupName + ", lastUpdate=" + lastUpdate + ", isDeactivated=" + isDeactivated + ", comment=" + comment
				+ ", dbName=" + dbName + "]";
	}

}
