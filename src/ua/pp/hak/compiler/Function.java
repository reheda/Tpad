package ua.pp.hak.compiler;

public class Function {
	private String name;
	private String returnType;
	private String[] membersOf;

	public Function(String name, String returnType, String... membersOf) {
		this.name = name;
		this.returnType = returnType;
		this.membersOf = membersOf;
	}

	public String getName() {
		return name;
	}

	public String getReturnType() {
		return returnType;
	}

	public String[] getMembersOf() {
		return membersOf;
	}

}
