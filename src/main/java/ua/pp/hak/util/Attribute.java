package ua.pp.hak.util;

public class Attribute {
	private int id;
	private String type;
	private String name;
	private int groupId;
	private String groupName;
	private String lastUpdate;
	private boolean isDeactivated;

	public Attribute(int id, String type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public Attribute(int id, String type, String name, boolean isDeactivated, int groupId, String groupName, String lastUpdate) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.isDeactivated = isDeactivated;
		this.groupId = groupId;
		this.groupName = groupName;
		this.lastUpdate = lastUpdate;
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

}
