package ua.pp.hak.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	static HashMap<Integer, String> attibutes = StAXParser.parse();

	static String getAttributeType(int attr) {
		return attibutes.get(attr);
	}

	final static String TYPE_SIMPLE = "Simple";
	final static String TYPE_MULTI_VALUED = "Multi-valued";
	final static String TYPE_SIMPLE_NUMERIC = "Simple numeric";
	final static String TYPE_REPEATING = "Repeating";
	final static String TYPE_REPEATING_NUMERIC = "Repeating numeric";
	final static String TYPE_MULTI_VALUED_NUMERIC = "Multi-valued numeric";

	private static ArrayList<Function> functions;

	private static String NEW_LINE = "\n";

	public static void main(String[] args) {
		String expr = "  IF A[335].Values LIKE \"%in%\"  AND CAT.Main.Key>=1 --asdasd\n OR CAT.Main.Key>1 THEN \"asda else sd\" --- sdfsdf\n"
				+ "ELSE IF A[606].Value > 3 THEN \"blasd\" " + " ELSE IF A[8184].Values>3 THEN " + "ELSE \"ooooo\"  ";

		System.out.println(checkExpression(expr, 0, 0));
	}

	static void initFunctions() {
		functions = new ArrayList<>();
		functions.add(new Function("Main", "AlternativeCategory", "ProductCategories"));
		functions.add(new Function("HasText", "ExpressionResultLiteral", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("IsEmpty", "ExpressionResultLiteral", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("IsDescendantOf()", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("LaunchDate", "DateTime", "Sku"));
		functions.add(new Function("GetDateTime()", "DateTimeOffset", "SystemObject"));
		functions.add(new Function("Count", "Int32", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "PdmMultivalueAttribute"));
		functions.add(new Function("Total", "Int32", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("BestImage", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("BoxContents", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("KeySellingPoints", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Ksp", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("MarketingText", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Msds", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("ProductFeatures", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("ProductSheet", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("QuickStartGuide", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Thumbnail", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("UserManual", "DigitalContentItem", "DigitalContent"));
		functions.add(new Function("Distinct", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("Min()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("MinMax()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("Max()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("Last()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("FlattenWithAnd()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("HtmlEncode()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("First()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("DiscardNulls()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("ExtractDecimals()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToLower()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToLowerFirstChar()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToTitleCase()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToUpper()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToUpperFirstChar()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Erase()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("EraseTextSurroundedBy()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("GetLine()", "ExpressionResultLiteral", "Specs"));
		functions.add(new Function("GetLineBody()", "ExpressionResultLiteral", "Specs"));
		functions.add(new Function("Flatten()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("IfLike()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("IfLongerThan()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Pluralize()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "Int32"));
		functions.add(new Function("Postfix()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("Prefix()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("RegexReplace()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Replace()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Shorten()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("Split()", "ExpressionResultList", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("UseSeparators()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("InvariantValues", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("Values", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("ValuesAndUnits", "ExpressionResultList", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("ValuesAndUnitsUSM", "ExpressionResultList", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("ValuesUSM", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("Format()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Skip()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Take()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Where()", "ExpressionResultList", "ExpressionResultList", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereNot()", "ExpressionResultList", "ExpressionResultList", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("BulletFeatures", "ExpressionResultList", "TemplexGenerator"));
		functions.add(new Function("CategoryCodes", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("CategoryKeys", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("Colors", "ExpressionResultList", "Sku"));
		functions.add(new Function("CustomerPns", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("FancyColors", "ExpressionResultList", "Sku"));
		functions.add(new Function("InvariantColors", "ExpressionResultList", "Sku"));
		functions.add(new Function("Keywords", "ExpressionResultList", "Sku"));
		functions.add(new Function("ManufacturerNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ModelNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ProductIds", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ProductLineNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("ProductNames", "IEnumerable`1", "RelatedProductList"));
		functions.add(new Function("GetLines()", "ExpressionResultList", "Specs"));
		functions.add(new Function("GetAncestry()", "IEnumerable`1", "AlternativeCategory"));
		functions.add(new Function("GetDescendants()", "IEnumerable`1", "AlternativeCategory"));
		functions.add(new Function("CategoryKey", "Int32", "RelatedProduct"));
		functions.add(new Function("Length", "Int32", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "ProductPackage"));
		functions.add(new Function("LineCount", "Int32", "Specs"));
		functions.add(new Function("NonOemAccessories", "Int32", "Sku"));
		functions.add(new Function("Order", "Int32", "SpecLine"));
		functions.add(new Function("PackQuantity", "Int32", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductId", "Int32", "Sku", "RelatedProduct"));
		functions.add(new Function("AltCats", "List`1", "ProductCategories"));
		functions.add(new Function("BestImages", "List`1", "DigitalContent"));
		functions.add(new Function("Round()", "ExpressionResultNumeric", "ExpressionResultNumeric"));
		functions.add(new Function("AtLeast()", "ExpressionResultNumeric", "ExpressionResultNumeric"));
		functions.add(new Function("AtMost()", "ExpressionResultNumeric", "ExpressionResultNumeric"));
		functions.add(new Function("MultiplyBy()", "ExpressionResultNumeric", "ExpressionResultNumeric"));
		functions.add(new Function("Match()", "PdmAttributeSet", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereUnit()", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereUnitOrValue()", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("WhereCategory()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("WhereManufacturer()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("WhereModelName()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("WhereProductLine()", "RelatedProductList", "RelatedProductList"));
		functions.add(new Function("Body", "ExpressionResultLiteral", "SpecLine"));
		functions.add(new Function("Brand", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("CategoryCode", "ExpressionResultLiteral", "ProductCategories", "RelatedProduct", "RelatedProductList"));
		functions.add(new Function("CustomerPn", "ExpressionResultLiteral", "RelatedProduct", "RelatedProductList"));
		functions.add(new Function("Description", "ExpressionResultLiteral", "Sku", "DigitalContentItem"));
		functions.add(new Function("GroupName", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("Header", "ExpressionResultLiteral", "SpecLine"));
		functions.add(new Function("Invariant", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("InvariantUnit", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("ItemName", "ExpressionResultLiteral", "Sku"));
		functions.add(new Function("Key", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("Manufacturer", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("MimeType", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ModelName", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("Name", "ExpressionResultLiteral", "Sku", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute", "SpecSection"));
		functions.add(new Function("PartNumber", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductLine", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductType", "ExpressionResultLiteral", "Sku"));
		functions.add(new Function("Unit", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("UnitUSM", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("Value", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute", "Gtin"));
		functions.add(new Function("ValueUSM", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("XmlContent", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ListPaths()", "ExpressionResultLiteral", "ProductCategories"));
		functions.add(new Function("ListUSM()", "ExpressionResultLiteral", "PdmAttributeSet"));
		functions.add(new Function("GetFullColorDescription()", "ExpressionResultLiteral", "TemplexGenerator"));
		functions.add(new Function("ToHtml()", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ToPlainText()", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ToText()", "ExpressionResultLiteral", "ExpressionResultList", "ExpressionResultLiteral", "ExpressionResultNumeric", "DateTimeOffset"));
		functions.add(new Function("Url", "Uri", "DigitalContentItem"));
		functions.add(new Function("CompatibleProducts", "ExpressionResultList", "Specs"));
		functions.add(new Function("CAT", "ProductCategories"));
		functions.add(new Function("SKU", "Sku"));
		functions.add(new Function("Request", "RelatedProduct"));
		functions.add(new Function("ParentProducts", "RelatedProductList"));
		functions.add(new Function("MS", "Specs"));
		functions.add(new Function("Sys", "SystemObject"));
		functions.add(new Function("DC", "DigitalContent"));
		functions.add(new Function("Generator", "TemplexGenerator"));
		functions.add(new Function("ES", "Specs"));
		functions.add(new Function("COALESCE()", "You can invoke nothing on it"));
		functions.add(new Function("NOT NULL", "You can invoke nothing on it"));
		functions.add(new Function("NULL", "You can invoke nothing on it"));
		functions.add(new Function("Data[\"\"]", "ExpressionResultLiteral", "RelatedProduct"));
		functions.add(new Function("Gtin", "Gtin", "RelatedProduct"));
		functions.add(new Function("Format", "ExpressionResultLiteral", "Gtin"));
		functions.add(new Function("HasValue", "ExpressionResultLiteral", "Int32"));
		functions.add(new Function("Height", "Int32", "ProductPackage"));
		functions.add(new Function("Width", "Int32", "ProductPackage"));
		functions.add(new Function("Weight", "Int32", "ProductPackage"));
		functions.add(new Function("Package", "ProductPackage", "RelatedProduct"));
		functions.add(new Function("IsBiggerThan()", "ExpressionResultLiteral", "ProductPackage"));
		functions.add(new Function("BulletFeatures[]", "ExpressionResultLiteral", "TemplexGenerator"));
		functions.add(new Function("Ksp[]", "ExpressionResultLiteral", "DigitalContent"));
		functions.add(new Function("\"\"", "You can invoke nothing on it"));




	}

	private static String checkExpression(String expr, int p0, int p1) {
		initFunctions();
		String exprCleaned = null;
		StringBuilder errors = new StringBuilder();
		String error = null;
		// check comments
		if (!isCommentsValid(expr)) {
			error = "Comment can't contains quote";
			errors.append(error);

			return errors.toString();
		}

		// erase comments. will erase until line feed
		exprCleaned = expr.replaceAll("--.*", "");

		// check quotes
		if (!matchQuotes(expr)) {
			error = "Quote is not closed";
			errors.append(error);

			return errors.toString();
		}

		// erase text surrounded by quotes
		exprCleaned = exprCleaned.replaceAll("\".*?\"", "\"\"");

		// check brackets
		if (!matchBrackets(exprCleaned)) {
			error = "Bracket is not closed/opened";
			errors.append(error);

			return errors.toString();
		}

		// erase text surrounded by brackets
		exprCleaned = exprCleaned.replaceAll("\\(.*?\\)", "()");

		// clean expression to make it parsable
		exprCleaned = exprCleaned.replaceAll("\\n+", " ").replaceAll("\\s+", " ").replaceAll(" \\. ", ".")
				.replaceAll(" ?\\[ ?", "[").replaceAll(" ?\\] ?\\.", "].").replaceAll(" ?\\( ?", "(")
				.replaceAll(" ?\\) ?\\.", ").").replaceAll("BulletFeatures\\[\\d+\\]", "BulletFeatures[]")
				.replaceAll("Ksp\\[\\d+\\]", "Ksp[]").replaceAll("(?i)ELSE IF", "ELSEIF");

		System.out.println(exprCleaned);

		// check if expression is returnValue or ifThenElseStatement
		if (exprCleaned.toUpperCase().contains(" IF ") || exprCleaned.contains(" THEN ")
				|| exprCleaned.contains(" ELSEIF ") || exprCleaned.contains(" ELSE ")) {

			// if its ifThenElseStatement
			error = checkIfThenElseStatement(exprCleaned);
			errors.append(error);

			return errors.toString();

		} else {
			System.out.println("not implemented");
		}

		return null;
	}

	private static String checkElseStatementQuantity(String str) {
		String temp = str.replaceAll("(?i) ELSE ", " ELS ");
		int elseCounter = str.length() - temp.length();
		if (elseCounter > 1) {
			return "You are using ELSE statement " + elseCounter + " times, but have to use only 1 time";

		}

		return null;
	}

	private static String checkIfStatementQuantity(String str) {
		String temp = str.replaceAll("(?i)^IF ", "I ").replaceAll("(?i) IF ", " I ");
		int ifCounter = str.length() - temp.length();
		if (ifCounter > 1) {
			return "You are using IF statement " + ifCounter + " times, but have to use only 1 time";

		}

		return null;
	}

	private static String checkThenStatementQuantity(String str) {
		int ifCounter = str.length() - str.replaceAll("(?i)^IF ", "I ").replaceAll("(?i) IF ", " I ").length();
		int elseIfCounter = str.length() - str.replaceAll("(?i) ELSEIF ", " ELSEI ").length();
		int thenCounter = str.length() - str.replaceAll("(?i) THEN ", " THE ").length();

		if (thenCounter != elseIfCounter + ifCounter) {
			return "You are using THEN statement " + thenCounter + " times, but have to use "
					+ (elseIfCounter + ifCounter) + " time(s)";
		}

		return null;
	}

	private static boolean isCommentsValid(String textExpr) {
		boolean isValid = true;
		String[] lines = textExpr.split("\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.contains("--") && line.contains("\"") && line.indexOf("--") < line.lastIndexOf("\"")) {
				isValid = false;
			}
		}

		return isValid;
	}

	private static boolean matchQuotes(String str) {
		int count = str.length() - str.replace("\"", "").length();

		if (count % 2 == 0) {
			return true;
		}

		return false;
	}

	private static boolean matchBrackets(String str) {
		int leftBracketCount = str.length() - str.replace("(", "").length();
		int rightBracketCount = str.length() - str.replace(")", "").length();

		if (leftBracketCount == rightBracketCount) {
			return true;
		}

		return false;
	}

	private static String checkIfThenElseStatement(String exprCleaned) {

		StringBuilder errors = new StringBuilder();
		String error = null;
		String structure = NEW_LINE + NEW_LINE + "Valid structure:" + NEW_LINE + "IF statement THEN returnValue"
				+ NEW_LINE + "[ ELSE IF statement THEN returnValue ]" + NEW_LINE + "[ ELSE returnValue ]";

		// count "if" words. qty should be 1 or 0
		error = checkIfStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);

			return errors.toString();

		}

		// count "else" words. qty should be 1 or 0
		error = checkElseStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);

			return errors.toString();
		}

		// check "then" words. should be same quantity as qty("elseif") +1
		error = checkThenStatementQuantity(exprCleaned);
		if (error != null) {
			errors.append(error);
			errors.append(structure);

			return errors.toString();
		}

		boolean isIfThenElseStatementValid = exprCleaned
				.matches("(?i)^ ?IF .*? THEN .*?( ELSEIF .*? THEN .*?)*?( ELSE .*?)?");

		if (!isIfThenElseStatementValid) {
			errors.append(structure);

			return errors.toString();
		}

		// check conditions
		String regex = "IF (.*?) THEN ";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(exprCleaned);
		while (m.find()) {
			String condition = m.group(1);
			error = checkCondition(condition);
			if (error != null) {
				errors.append(error);

				return errors.toString();
			}
		}

		return null;
	}

	private static String checkCondition(String condition) {
		// -if without "AND" and "OR":
		// -should have 1 and only 1 occurence one of the following ("<", ">",
		// "=", "LIKE", "IS")
		// -should NOT have "_"
		// -should have correct comment (without quotes)
		// -should have pair quotes
		//
		// -if with "AND" and "OR":
		// -split by "AND" and "OR" then do the same as without "AND" and "OR"
		StringBuilder errors = new StringBuilder();
		String error = null;

		String structure = NEW_LINE + NEW_LINE + "Valid condition:" + NEW_LINE + "value1 operator value2";

		List<String> conditions = Arrays.asList(condition.split(" AND | OR "));

		for (String con : conditions) {
			int operatorsQty = con.length() - con.replaceAll(">=", "1").replaceAll("<=", "1").replaceAll("<", "")
					.replaceAll(">", "").replaceAll("=", "").replaceAll(" LIKE ", " LIK ").replaceAll(" IS ", " I ")
					.replaceAll(" IN ", " I ").replaceAll("HasText", "HasTex").replaceAll("IsEmpty", "IsEmpt")
					.replaceAll("IsDescendantOf\\(\\)(?!\\.)", "IsDescendantOf(").replaceAll("HasValue", "HasValu")
					.replaceAll("(?!\\.)IsBiggerThan\\(\\)(?!\\.)", "IsBiggerThan(").length();
			if (operatorsQty > 1) {
				error = "Condition '"+ con +"' have to contains only 1 operator";
				errors.append(error);
				errors.append(structure);

				return errors.toString();
			} else if (operatorsQty == 0) {
				error = "Condition '"+ con +"' have to contains operator";
				errors.append(error);
				errors.append(structure);

				return errors.toString();
			}

			int underScroreCount = con.length() - con.replaceAll("_", "").length();
			if (underScroreCount > 0) {
				error = "Condition DON'T have to contains underscore";
				errors.append(error);

				return errors.toString();
			}

			// check values
			String[] values = con.split(">=|<=|>|<|=|LIKE|IS|IN");
			for (int i = 0; i < values.length; i++) {
				error = checkValue(values[i]);
				if (error != null) {
					errors.append(error);

					return errors.toString();
				}

			}

		}

		return null;
	}

	private static String checkValue(String value) {
		// add variant if check value is just string surrounded by quotes

		String valueCleaned = value.trim();
		String[] functns = valueCleaned.split("\\.");

		String previousType = null;

		for (int i = 0; i < functns.length; i++) {
			// check if function exists
			if (!isFunction(functns[i]) && !isPdm(functns[i]) && !isNumber(functns[i])) {
				return "Function '" + functns[i] + "' doesn't exist";
			}

			// if decimal do nothing
			if (!isNumber(functns[i])) {

				// check if pdm attribute or not
				if (isPdm(functns[i])) {
					String attrType = getAttributeType(Integer.parseInt(functns[i].replaceAll("[^0-9]", "")));
					if (attrType != null) {
						switch (attrType) {
						case TYPE_SIMPLE:
							previousType = "PdmAttribute";
							break;
						case TYPE_SIMPLE_NUMERIC:
							previousType = "PdmAttribute";
							break;
						case TYPE_MULTI_VALUED:
							previousType = "PdmMultivalueAttribute";
							break;
						case TYPE_MULTI_VALUED_NUMERIC:
							previousType = "PdmMultivalueAttribute";
							break;
						case TYPE_REPEATING:
							previousType = "PdmRepeatingAttribute";
							break;
						case TYPE_REPEATING_NUMERIC:
							previousType = "PdmRepeatingAttribute";
							break;
						default:
							break;
						}
					} else {
						return "Attribute '" + functns[i] + "' doesn't exist";
					}

				} else {
					if (!isFunctionMemberOfValid(functns[i], previousType)) {
						return "Function '" + functns[i] + "' can't be invoked on '" + previousType + "'";
					}

					previousType = getFunctionReturnType(functns[i], previousType);
				}

			}

		}

		return null;
	}

	private static boolean isNumber(String functionName) {
		return functionName.matches("\\d+");
	}

	private static boolean isPdm(String functionName) {
		return functionName.matches("A\\[\\d+\\]");
	}

	private static boolean isFunction(String functionName) {

		for (Function function : functions) {
			if (function.getName().equals(functionName)) {
				return true;
			}
		}

		return false;
	}

	private static String getFunctionReturnType(String functionName, String previousType) {
		for (Function function : functions) {
			if (function.getName().equals(functionName)) {
				if (previousType != null && "ExpressionResult".equals(function.getReturnType())) {
					return previousType;
				} else {
					return function.getReturnType();
				}
			}
		}

		return null;
	}

	private static boolean isFunctionMemberOfValid(String functionName, String previousType) {

		for (Function function : functions) {
			if (function.getName().equals(functionName)) {
				String[] membersOf = function.getMembersOf();
				if (membersOf.length == 0 && previousType == null) {
					return true;
				} else if (previousType != null) {

					for (int i = 0; i < membersOf.length; i++) {
						if (membersOf[i].equals(previousType)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}