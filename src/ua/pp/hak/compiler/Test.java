package ua.pp.hak.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	static List<Attribute> attibutes = StAXParser.parse();

	static String getAttributeType(int attr) {
		for (Attribute attribute : attibutes) {
			if (attribute.getId() == attr) {
				return attribute.getType();
			}
		}
		return null;
	}

	final static String TYPE_SIMPLE = "Simple";
	final static String TYPE_MULTI_VALUED = "Multi-valued";
	final static String TYPE_SIMPLE_NUMERIC = "Simple numeric";
	final static String TYPE_REPEATING = "Repeating";
	final static String TYPE_REPEATING_NUMERIC = "Repeating numeric";
	final static String TYPE_MULTI_VALUED_NUMERIC = "Multi-valued numeric";

	private static ArrayList<Function> functions;
	private static ArrayList<FunctionWithParameters> functionsWithParams;

	private static String NEW_LINE = "\n";

	public static void main(String[] args) {
		// String expr = "IF Request.Package.HasText.Replace() > 2 THEN 3";
		// String expr = "IF Request.Package.IsBiggerThan(20 , 15,15
		// ,0).ToText(\"asdasd\").Replace(\"True\",\"Yes\").Replace(\"False\",\"No\")
		// IS NOT NULL THEN 3";
		String expr = "CASE A[5784].Value\n"+
				  "WHEN A[601].Value THEN A[601].Value\n"+
				   "ELSE A[601].Value\n"+
				"END;";

		System.out.println(checkExpression(expr));

	}

	static void initFunctionsWithParams() {
		final int MAX_LIMIT = 1000;
		functionsWithParams = new ArrayList<>();
		functionsWithParams.add(new FunctionWithParameters("GetAncestry()", 0, 1, "Boolean"));
		functionsWithParams.add(new FunctionWithParameters("GetDescendants()", 0, 1, "Boolean"));
		functionsWithParams.add(new FunctionWithParameters("ToLower()", 0, 1, "Boolean"));
		functionsWithParams.add(new FunctionWithParameters("ExtractDecimals()", 0, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("GetDateTime()", 0, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("AtLeast()", 1, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("AtMost()", 1, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("MultiplyBy()", 1, 1, "Double"));
		functionsWithParams.add(new FunctionWithParameters("ToTitleCase()", 0, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("GetLine()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("GetLineBody()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("Skip()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("Take()", 1, 1, "Int32"));
		functionsWithParams.add(new FunctionWithParameters("Flatten()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("GetFullColorDescription()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("ListPaths()", 0, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("EraseTextSurroundedBy()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Format()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("IsDescendantOf()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Postfix()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Prefix()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("ToText()", 1, 1, "String"));
		functionsWithParams.add(new FunctionWithParameters("Match()", 0, MAX_LIMIT, "Int32[]"));
		functionsWithParams.add(new FunctionWithParameters("Split()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("UseSeparators()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("Where()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereNot()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereUnit()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereUnitOrValue()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereCategory()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereManufacturer()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereModelName()", 1, MAX_LIMIT, "String[]"));
		functionsWithParams.add(new FunctionWithParameters("WhereProductLine()", 1, MAX_LIMIT, "String[]"));

		functionsWithParams.add(new FunctionWithParameters("IfLike()", 2, 2, "String", "String"));

		functionsWithParams.add(new FunctionWithParameters("IfLongerThan()", 2, 2, "Int32", "String"));

		functionsWithParams.add(new FunctionWithParameters("FlattenWithAnd()", 0, 2, "Int32", "String"));

		functionsWithParams.add(new FunctionWithParameters("RegexReplace()", 2, 2, "String", "String"));

		functionsWithParams.add(new FunctionWithParameters("ListUSM()", 0, 2, "String", "String"));

		functionsWithParams.add(new FunctionWithParameters("Erase()", 1, 3, "String", "Boolean", "String"));

		functionsWithParams.add(new FunctionWithParameters("Shorten()", 1, 3, "Int32", "String", "String"));

		functionsWithParams
				.add(new FunctionWithParameters("Replace()", 2, 4, "String", "String", "String", "StringComparison"));

		functionsWithParams
				.add(new FunctionWithParameters("IsBiggerThan()", 4, 4, "Double", "Double", "Double", "Double"));

	}

	static void initFunctions() {
		functions = new ArrayList<>();
		functions.add(new Function("Main", "AlternativeCategory", "ProductCategories"));
		functions.add(new Function("HasText", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("IsEmpty", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "String"));
		functions.add(new Function("IsDescendantOf()", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("LaunchDate", "DateTime", "Sku"));
		functions.add(new Function("GetDateTime()", "DateTimeOffset", "SystemObject"));
		functions.add(new Function("Count", "ExpressionResultNumeric", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "PdmMultivalueAttribute"));
		functions.add(
				new Function("Total", "ExpressionResultNumeric", "PdmMultivalueAttribute", "PdmRepeatingAttribute"));
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
		functions.add(new Function("HtmlEncode()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("First()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("DiscardNulls()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("ExtractDecimals()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToLower()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("ToLowerFirstChar()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToTitleCase()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("ToUpper()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("ToUpperFirstChar()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Erase()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("EraseTextSurroundedBy()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("GetLine()", "ExpressionResultLiteral", "Specs"));
		functions.add(new Function("GetLineBody()", "ExpressionResultLiteral", "Specs"));
		functions.add(new Function("Flatten()", "ExpressionResultLiteral", "ExpressionResultList"));
		functions.add(new Function("IfLike()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("IfLongerThan()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Pluralize()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("Postfix()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric", "String"));
		functions.add(new Function("Prefix()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric", "String"));
		functions.add(new Function("RegexReplace()", "ExpressionResult", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Replace()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("Shorten()", "ExpressionResult", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric", "String"));
		functions.add(new Function("Split()", "ExpressionResultList", "ExpressionResultList", "ExpressionResultLiteral",
				"ExpressionResultNumeric"));
		functions.add(new Function("UseSeparators()", "ExpressionResult", "ExpressionResultList"));
		functions.add(new Function("InvariantValues", "ExpressionResultList", "PdmAttributeSet",
				"PdmMultivalueAttribute", "PdmRepeatingAttribute"));
		functions.add(new Function("Values", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("ValuesAndUnits", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("ValuesAndUnitsUSM", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("ValuesUSM", "ExpressionResultList", "PdmAttributeSet", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("Format()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Skip()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Take()", "ExpressionResultList", "ExpressionResultList"));
		functions.add(new Function("Where()", "ExpressionResult", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("WhereNot()", "ExpressionResult", "ExpressionResultList", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
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
		functions.add(new Function("CategoryKey", "ExpressionResultNumeric", "RelatedProduct"));
		functions.add(new Function("Length", "ExpressionResultNumeric", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "ProductPackage"));
		functions.add(new Function("LineCount", "ExpressionResultNumeric", "Specs"));
		functions.add(new Function("NonOemAccessories", "ExpressionResultNumeric", "Sku"));
		functions.add(new Function("Order", "ExpressionResultNumeric", "SpecLine"));
		functions.add(new Function("PackQuantity", "ExpressionResultNumeric", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductId", "ExpressionResultNumeric", "Sku", "RelatedProduct"));
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
		functions.add(new Function("CategoryCode", "ExpressionResultLiteral", "ProductCategories", "RelatedProduct",
				"RelatedProductList"));
		functions.add(new Function("CustomerPn", "ExpressionResultLiteral", "RelatedProduct", "RelatedProductList"));
		functions.add(new Function("Description", "ExpressionResultLiteral", "Sku", "DigitalContentItem"));
		functions.add(new Function("GroupName", "ExpressionResultLiteral", "PdmAttribute", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute"));
		functions.add(new Function("Header", "ExpressionResultLiteral", "SpecLine"));
		functions.add(new Function("Invariant", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("InvariantUnit", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("ItemName", "ExpressionResultLiteral", "Sku"));
		functions.add(new Function("Key", "ExpressionResultLiteral", "AlternativeCategory"));
		functions.add(new Function("Manufacturer", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("MimeType", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ModelName", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("Name", "ExpressionResultLiteral", "Sku", "PdmAttribute", "PdmMultivalueAttribute",
				"PdmRepeatingAttribute", "SpecSection"));
		functions.add(new Function("PartNumber", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductLine", "ExpressionResultLiteral", "Sku", "RelatedProduct"));
		functions.add(new Function("ProductType", "ExpressionResultLiteral", "Sku"));
		functions.add(new Function("Unit", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("UnitUSM", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("Value", "ExpressionResultLiteral", "PdmAttribute", "Gtin"));
		functions.add(new Function("ValueUSM", "ExpressionResultLiteral", "PdmAttribute"));
		functions.add(new Function("XmlContent", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ListPaths()", "ExpressionResultLiteral", "ProductCategories"));
		functions.add(new Function("ListUSM()", "ExpressionResultLiteral", "PdmAttributeSet"));
		functions.add(new Function("GetFullColorDescription()", "ExpressionResultLiteral", "TemplexGenerator"));
		functions.add(new Function("ToHtml()", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ToPlainText()", "ExpressionResultLiteral", "DigitalContentItem"));
		functions.add(new Function("ToText()", "ExpressionResultLiteral", "ExpressionResultList",
				"ExpressionResultLiteral", "ExpressionResultNumeric", "DateTimeOffset"));
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
		functions.add(new Function("HasValue", "ExpressionResultLiteral", "ExpressionResultNumeric"));
		functions.add(new Function("Height", "ExpressionResultNumeric", "ProductPackage"));
		functions.add(new Function("Width", "ExpressionResultNumeric", "ProductPackage"));
		functions.add(new Function("Weight", "ExpressionResultNumeric", "ProductPackage"));
		functions.add(new Function("Package", "ProductPackage", "RelatedProduct"));
		functions.add(new Function("IsBiggerThan()", "ExpressionResultLiteral", "ProductPackage"));
		functions.add(new Function("BulletFeatures[]", "ExpressionResultLiteral", "TemplexGenerator"));
		functions.add(new Function("Ksp[]", "ExpressionResultLiteral", "DigitalContent"));
		functions.add(new Function("\"\"", "You can invoke nothing on it"));
		functions.add(new Function("Year", "ExpressionResultNumeric", "DateTime"));
		functions.add(new Function("Month", "ExpressionResultNumeric", "DateTime"));
		functions.add(new Function("Day", "ExpressionResultNumeric", "DateTime"));

	}

	private static String checkExpression(String expr) {
		initFunctions();
		initFunctionsWithParams();
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

		// clean expression to make it parsable
		exprCleaned = exprCleaned.replaceAll("\\n+", " ").replaceAll("\\s+", " ").replaceAll("(?i)ELSE IF", "ELSEIF")
				.replaceAll(" \\. ", ".").replaceAll(" ?\\[ ?", "[").replaceAll(" ?\\] ?\\.", "].")
				.replaceAll(" ?\\( ?", "(").replaceAll(" ?\\) ?\\.", ").");

		System.out.println(exprCleaned);

		// split by semicolon
		String[] statements = exprCleaned.split(";");

		for (int i = 0; i < statements.length; i++) {
			// check if expression is returnValue or ifThenElseStatement
			if (statements[i].toUpperCase().contains("IF ") || statements[i].contains(" ELSEIF ")) {

				// if its ifThenElseStatement
				error = checkIfThenElseStatement(statements[i]);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
			} else if (statements[i].toUpperCase().contains("CASE ") || statements[i].toUpperCase().contains("WHEN")
					|| statements[i].toUpperCase().contains("END")) {
				
				error = checkCaseStatement(statements[i]);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}
				
			} else {
				error = checkReturnValue(statements[i]);
				if (error != null) {
					errors.append(error);

					return errors.toString();
				}
			}
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
		int elseIfCounter = str.length() - str.replaceAll("(?i)ELSEIF ", "ELSEI ").length();
		int thenCounter = str.length() - str.replaceAll("(?i)THEN ", "THE ").length();

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
			errors.append("Invalid IF THEN ELSE statements.");
			errors.append(structure);

			return errors.toString();
		}

		// check conditions
		{
			String regex = "(?i)IF (.*?) THEN ";
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
		}

		// check return values
		String regex = "(?i)(?: THEN | ELSE )(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(exprCleaned.replaceAll(" ELSEIF ", "\n ELSEIF ").replaceAll(" ELSE ", "\n ELSE "));
		while (m.find()) {
			String returnValue = m.group(1);
			error = checkReturnValue(returnValue);
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

		// check brackets
		if (!matchBrackets(condition)) {
			return "Bracket is not closed/opened";
		}
		// check brackets matching
		if (!isParenthesisMatch(condition)) {
			return "Bracket is not using prorely";
		}

		String contitonCleaned = condition;

		StringBuilder errors = new StringBuilder();
		String error = null;
		String structure = NEW_LINE + NEW_LINE + "Valid condition:" + NEW_LINE + "value1 operator value2";

		List<String> conditions = Arrays.asList(contitonCleaned.split("(?i) AND | OR "));

		for (String con : conditions) {

			String conCleaned = con;

			final String COALESCE_TEXT = "COALESCE(";
			final String IN_TEXT = " IN(";

			// erase content surrounded by brackets
			if (conCleaned.toUpperCase().contains(IN_TEXT)) {
				int point = getLastBracketIndex(conCleaned, conCleaned.indexOf(IN_TEXT));
				conCleaned = conCleaned.substring(0, conCleaned.indexOf(IN_TEXT) + IN_TEXT.length())
						+ conCleaned.substring(point);
			}

			if (conCleaned.toUpperCase().contains(COALESCE_TEXT)) {
				int point = getLastBracketIndex(conCleaned, conCleaned.indexOf(COALESCE_TEXT));
				conCleaned = conCleaned.substring(0, conCleaned.indexOf(COALESCE_TEXT) + COALESCE_TEXT.length())
						+ conCleaned.substring(point);
			}

			conCleaned = conCleaned.replaceAll("\\(.*?\\)", "()");

			int operatorsQty = conCleaned.length() - conCleaned.replaceAll(">=", "1").replaceAll("<=", "1")
					.replaceAll("<", "").replaceAll(">", "").replaceAll("=", "").replaceAll("(?i) LIKE ", " LIK ")
					.replaceAll("(?i) IS ", " I ").replaceAll("(?i) IN\\(", " IN")
					.replaceAll("HasText(?!\\.)", "HasTex").replaceAll("IsEmpty(?!\\.)", "IsEmpt")
					.replaceAll("IsDescendantOf\\(\\)(?!\\.)", "IsDescendantOf(")
					.replaceAll("HasValue(?!\\.)", "HasValu").replaceAll("IsBiggerThan\\(\\)(?!\\.)", "IsBiggerThan(")
					.length();

			if (operatorsQty > 1) {
				error = "Condition '" + conCleaned + "' have to contains only 1 operator";
				errors.append(error);
				errors.append(structure);

				return errors.toString();
			} else if (operatorsQty == 0) {
				error = "Condition '" + conCleaned + "' have to contains operator";
				errors.append(error);
				errors.append(structure);

				return errors.toString();
			}

			int underScroreCount = conCleaned.length() - conCleaned.replaceAll("_", "").length();
			if (underScroreCount > 0) {
				error = "Condition DON'T have to contains underscore";
				errors.append(error);

				return errors.toString();
			}

			// check values
			if (conCleaned.toUpperCase().contains(" IS ")) {
				String[] values = conCleaned.split("(?i) IS ");
				if (values.length == 2) {

					// check right part
					String rightPart = values[1].trim();
					if (!rightPart.matches("(?i)NOT NULL") && !rightPart.matches("(?i)NULL")) {
						return "You shouldn't check for '" + rightPart + "'. Expected check for NULL";
					}

					// check left part
					error = checkValue(values[0]);
					if (error != null) {
						errors.append(error);
						return errors.toString();
					}

					// check parameters
					error = chechParametersInCondition(con);
					if (error != null) {
						errors.append(error);
						return errors.toString();
					}

				} else {
					System.err.println("Please report this expression!");
				}

			} else {

				// check values
				String[] values = conCleaned.split("(?i)>=|<=|>|<|=| LIKE | IN\\(\\)");
				for (int i = 0; i < values.length; i++) {
					error = checkValue(values[i]);
					if (error != null) {
						errors.append(error);
						return errors.toString();
					}

				}

				// check parameters
				error = chechParametersInCondition(con);
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
		valueCleaned = valueCleaned.replaceAll("BulletFeatures\\[\\d+\\]", "BulletFeatures[]")
				.replaceAll("Ksp\\[\\d+\\]", "Ksp[]");

		String[] functns = valueCleaned.split("\\.");

		String previousType = null;

		for (int i = 0; i < functns.length; i++) {
			// check if function exists
			if (!isFunction(functns[i]) && !isPdm(functns[i]) && !isNumber(functns[i]) && !isReference(functns[i])) {
				return "Function '" + functns[i] + "' doesn't exist";
			}

			// if decimal or reference do nothing
			if (!isNumber(functns[i]) && !isReference(functns[i])) {

				// check if pdm attribute or not
				if (isPdm(functns[i])) {
					int attr = Integer.parseInt(functns[i].replaceAll("[^0-9]", ""));
					String attrType = getAttributeType(attr);
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
						return "Function '" + functns[i] + "' shouldn't be invoked on '" + previousType + "'";
					}

					previousType = getFunctionReturnType(functns[i], previousType);
				}

			}

		}

		return null;
	}

	private static boolean isNumber(String str) {
		return str.matches("\\d+");
	}

	private static boolean isReference(String functionName) {
		return functionName.matches("\\$.+\\$");
	}

	private static boolean isPdm(String functionName) {
		return functionName.matches("A\\[\\d+\\]");
	}

	private static boolean isFunction(String functionName) {

		if (functionName.equals("Coalesce()")) {
			functionName = functionName.toUpperCase();
		}

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

		if (functionName.equals("Coalesce()")) {
			functionName = functionName.toUpperCase();
		}

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

	private static String chechParametersInCondition(String condition) {
		StringBuilder errors = new StringBuilder();
		String error = null;

		String conCleaned = condition;

		String regex = "\\.(\\w+) ?\\((.*?)\\)";
		Pattern p = Pattern.compile(regex);

		final String COALESCE_TEXT = "COALESCE(";
		final String IN_TEXT = " IN(";

		if (condition.contains(COALESCE_TEXT)) {
			int point = getLastBracketIndex(condition, condition.indexOf(COALESCE_TEXT));
			conCleaned = condition.substring(condition.indexOf(COALESCE_TEXT) + COALESCE_TEXT.length(), point);

			// check values and parameters
			String[] values = conCleaned.split(", |,");
			for (int i = 0; i < values.length; i++) {
				error = checkValue(values[i]);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}

				Matcher m = p.matcher(values[i]);
				while (m.find()) {
					String functionName = m.group(1) + "()";
					String parameters = m.group(2);
					error = checkParameters(functionName, parameters);
					if (error != null) {
						errors.append(error);

						return errors.toString();
					}
				}

			}
		}

		if (condition.contains(IN_TEXT)) {
			int point = getLastBracketIndex(condition, condition.indexOf(IN_TEXT));
			conCleaned = condition.substring(condition.indexOf(IN_TEXT) + IN_TEXT.length(), point);

			// check values and parameters
			String[] values = conCleaned.split(", |,");
			for (int i = 0; i < values.length; i++) {
				error = checkValue(values[i]);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}

				Matcher m = p.matcher(values[i]);
				while (m.find()) {
					String functionName = m.group(1) + "()";
					String parameters = m.group(2);
					error = checkParameters(functionName, parameters);
					if (error != null) {
						errors.append(error);

						return errors.toString();
					}
				}

			}
		}

		if (!condition.contains(COALESCE_TEXT) && !condition.contains(IN_TEXT)) {

			Matcher m = p.matcher(condition);
			while (m.find()) {
				String functionName = m.group(1) + "()";
				String parameters = m.group(2);
				error = checkParameters(functionName, parameters);
				if (error != null) {
					errors.append(error);

					return errors.toString();
				}
			}

		}
		return null;
	}

	private static String checkParameters(String functionName, String parameters) {
		// Replace("before", "after")
		// functionName = Replace
		// parameters = "\"before\", \"after\""

		String[] params = parameters.split(", |,");

		int paramsQty = params.length;

		for (FunctionWithParameters func : functionsWithParams) {

			if (func.getName().equals(functionName)) {

				if (func.isParamsQtyValid(paramsQty)) {

					for (int i = 0; i < params.length; i++) {
						if (!params[i].isEmpty() && !func.isTypeValid(params[i].trim(), i)) {
							return "Function '" + func.getName() + "' has incorrect type. '" + params[i].trim()
									+ "' shouldn't be on position #" + (i + 1);
						}
					}

					// if all parameters are valid
					return null;

				} else {
					return "'" + functionName + "' shouldn't have '" + paramsQty + "' parameters";
				}
			}
		}

		if (!parameters.isEmpty()) {
			return "'" + functionName + "' shouldn't have parameters";
		}

		return null;
	}

	private static boolean isParenthesisMatch(String str) {

		Stack<Character> stack = new Stack<Character>();

		char c;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			if (c == '(')
				stack.push(c);
			else if (c == '[')
				stack.push(c);
			else if (c == ')')
				if (stack.empty())
					return false;
				else if (stack.peek() == '(')
					stack.pop();
				else
					return false;
			else if (c == ']')
				if (stack.empty())
					return false;
				else if (stack.peek() == '[')
					stack.pop();
				else
					return false;
		}
		return stack.empty();
	}

	private static int getLastBracketIndex(String str, int startIndex) {

		if (startIndex < 0) {
			return 0;
		}

		Stack<Character> stack = new Stack<Character>();

		char c;
		for (int i = startIndex; i < str.length(); i++) {

			c = str.charAt(i);

			if (c == '(')
				stack.push(c);
			else if (c == ')')
				if (stack.empty())
					return 0;
				else if (stack.size() == 1 && stack.peek() == '(')
					return i;
				else if (stack.peek() == '(')
					stack.pop();
				else
					return 0;

		}

		return 0;
	}

	private static String checkReturnValue(String returnValue) {
		StringBuilder errors = new StringBuilder();
		String error = null;

		final String COALESCE_TEXT = "COALESCE(";

		if (!returnValue.contains(COALESCE_TEXT)) {
			// check values
			String[] values = returnValue.replaceAll("\\(.*?\\)", "()").split(" _ | _|_ |_");
			for (int i = 0; i < values.length; i++) {
				error = checkValue(values[i]);
				if (error != null) {
					errors.append(error);
					return errors.toString();
				}

			}
		}

		// check parameters
		error = chechParametersInCondition(returnValue);
		if (error != null) {
			errors.append(error);
			return errors.toString();
		}

		return null;
	}
	
	private static String checkCaseStatement(String caseStatement){
		StringBuilder errors = new StringBuilder();
		String error = null;
		String structure = NEW_LINE + NEW_LINE + "Valid structure:" + NEW_LINE + "CASE value"
				+ NEW_LINE + "[ WHEN value THEN returnValue ]" + NEW_LINE + "[ ELSE returnValue ]"+ NEW_LINE + "END";
		
		boolean isCaseStatementValid = caseStatement
				.matches("(?i)^ ?CASE .*?( WHEN .*? THEN .*?)+?( ELSE .*?)? END");

		if (!isCaseStatementValid) {
			errors.append("Invalid CASE statements. ");
			errors.append(structure);

			return errors.toString();
		}
		


		// check values
		String regex = "(?i)(?:CASE | WHEN | THEN | ELSE )(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(caseStatement.replaceAll(" WHEN ", "\n WHEN ").replaceAll(" THEN ", "\n THEN ").replaceAll(" ELSE ", "\n ELSE ").replaceAll(" END", "\nEND"));
		while (m.find()) {
			String returnValue = m.group(1);
			error = checkReturnValue(returnValue);
			if (error != null) {
				errors.append(error);

				return errors.toString();
			}
		}

	
		return null;
	}
}