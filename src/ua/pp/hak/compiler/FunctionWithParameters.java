package ua.pp.hak.compiler;

public class FunctionWithParameters {
	private String name;
	private int maxParameterQty;
	private int minParameterQty;
	private String[] parameterTypes;

	public FunctionWithParameters(String name, int minParameterQty, int maxParameterQty, String... parameterTypes) {
		this.name = name;
		this.minParameterQty = minParameterQty;
		this.maxParameterQty = maxParameterQty;
		this.parameterTypes = parameterTypes;
	}

	public String getName() {
		return name;
	}

	public int getMaxParameterQty() {
		return maxParameterQty;
	}

	public int getMinParameterQty() {
		return minParameterQty;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public boolean isParamsQtyValid(int qty) {
		if (qty >= minParameterQty && qty <= maxParameterQty) {
			return true;
		}
		return false;
	}

	public boolean isTypeValid(String parameter, int index) {

		if (index < 0 || index > parameterTypes.length - 1) {
			return false;
		}

		// check if text
		boolean isString = parameter.matches("\".*\"");
		// check if number
		boolean isDouble = parameter.matches("\\d+\\.\\d+");
		boolean isInteger = parameter.matches("\\d+");
		// check if function
		boolean isFunction = parameter.matches(".*\\.\\w+");
		// check if boolean
		boolean isBoolean = parameter.matches("true|false");
		// check if StringComparison
		boolean isStringComparison = parameter.matches(
				"CurrentCulture|CurrentCultureIgnoreCase|InvariantCulture|InvariantCultureIgnoreCase|Ordinal|OrdinalIgnoreCase");

		if (isString) {
			if (parameterTypes[index].equals("String") || parameterTypes[index].equals("String[]")) {
				return true;
			}
		} else if (isBoolean) {
			if (parameterTypes[index].equals("Boolean")) {
				return true;
			}
		} else if (isStringComparison) {
			if (parameterTypes[index].equals("StringComparison")) {
				return true;
			}
		} else if (isDouble) {
			if (parameterTypes[index].equals("Double")) {
				return true;
			}
		} else if (isInteger) {
			if (parameterTypes[index].equals("Int32") || parameterTypes[index].equals("Int32[]")
					|| parameterTypes[index].equals("Double")) {
				return true;
			}
		} else if (isFunction) {
			// should investigate coalesce() and in()
		} else {
			// return false (by default)
		}

		return false;
	}

}
