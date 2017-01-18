package ua.pp.hak.util;

public class Attribute {
	private int id;
	private String type;
	private String name;
	
	public Attribute(int id, String type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
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
	
	
}
