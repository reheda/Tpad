package ua.pp.hak.util;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

public class AutoCompleter {
	/**
	 * Create a simple provider that adds some completions.
	 */
	public static CompletionProvider createCompletionProvider() {

		// A DefaultCompletionProvider is the simplest concrete implementation
		// of CompletionProvider. This provider has no understanding of
		// language semantics. It simply checks the text entered up to the
		// caret position for a match against known completions. This is all
		// that is needed in the majority of cases.
		DefaultCompletionProvider provider = new DefaultCompletionProvider();

		final String [] functions = {
				"AltCats",
				"BestImage",
				"BestImages",
				"Body",
				"BoxContents",
				"Brand",
				"BulletFeatures",
				"CategoryCode",
				"CategoryCodes",
				"CategoryKey",
				"CategoryKeys",
				"CategoryName",
				"Colors",
				"CompatibleProducts",
				"Count",
				"CustomerPn",
				"CustomerPns",
				"Description",
				"Distinct",
				"FancyColors",
				"GroupName",
				"HasText",
				"Header",
				"Invariant",
				"InvariantColors",
				"InvariantUnit",
				"InvariantValues",
				"IsEmpty",
				"ItemName",
				"Key",
				"KeySellingPoints",
				"Keywords",
				"Ksp",
				"LaunchDate",
				"Length",
				"LineCount",
				"Main",
				"Manufacturer",
				"ManufacturerNames",
				"MarketingText",
				"MimeType",
				"ModelName",
				"ModelNames",
				"Msds",
				"Name",
				"NonOemAccessories",
				"Order",
				"PackQuantity",
				"PartNumber",
				"ProductFeatures",
				"ProductId",
				"ProductIds",
				"ProductLine",
				"ProductLineNames",
				"ProductNames",
				"ProductSheet",
				"ProductType",
				"QuickStartGuide",
				"Thumbnail",
				"Total",
				"Unit",
				"UnitUSM",
				"Url",
				"UserManual",
				"Value",
				"Values",
				"ValuesAndUnits",
				"ValuesAndUnitsUSM",
				"ValuesUSM",
				"ValueUSM",
				"XmlContent",
				"AND",
				"CASE",
				"CAT",
				"DC",
				"ELSE IF",
				"ELSE",
				"END",
				"IF",
				"IN",
				"IS NOT NULL",
				"IS NULL",
				"LIKE",
				"MS",
				"NOT LIKE",
				"NOT NULL",
				"NULL",
				"OR",
				"SKU",
				"THEN",
				"WHEN",
				"Round()",
				"Min()",
				"MinMax()",
				"Match()",
				"Max()",
				"ListPaths()",
				"ListUSM()",
				"Last()",
				"FlattenWithAnd()",
				"HtmlEncode()",
				"GetLines()",
				"GetPath()",
				"First()",
				"DiscardNulls()",
				"ExtractDecimals()",
				"GetAncestry()",
				"GetDateTime()",
				"GetDescendants()",
				"GetFullColorDescription()",
				"ToHtml()",
				"ToLower()",
				"ToLowerFirstChar()",
				"ToPlainText()",
				"ToTitleCase()",
				"ToUpper()",
				"ToUpperFirstChar()",
				"COALESCE(\"text\")",
				"A[]",
				"AtLeast(minimum)",
				"AtMost(maximum)",
				"Erase(\"value\")",
				"EraseTextSurroundedBy(\"surroundingCharacters\")",
				"Format(\"literalFormat\")",
				"GetLine(3)",
				"GetLineBody(2)",
				"Flatten(\"\")",
				"IfLike(\"pattern\", \"replacement\")",
				"IfLongerThan(2, \"replacementText\")",
				"IsDescendantOf(\"categoryKey\")",
				"Match(1)",
				"Match(2, 2, 1)",
				"Pick(2)",
				"Pluralize()",
				"Postfix(\"suffix\")",
				"Prefix(\"prefix\")",
				"RegexReplace(\"pattern\", \"substitution\")",
				"Replace(\"oldValue\", \"newValue\")",
				"Shorten(1)",
				"Shorten(1, \"\")",
				"Skip(5)",
				"Split(\"separator1\")",
				"Split(\"separator1\", \"separator2\", \"\")",
				"MultiplyBy(factor)",
				"Take(2)",
				"ToText(\"literalFormat\")",
				"Where(\"pattern1\")",
				"Where(\"pattern1\", \"pattern2\", \"\")",
				"WhereCategory(\"codeOrKey1\", \"codeOrKey2\", \"\")",
				"WhereManufacturer(\"mfFilter1\", \"mfFilter2\", \"\")",
				"WhereModelName(\"modelFilter1\", \"modelFilter2\", \"\")",
				"WhereNot(\"pattern1\")",
				"WhereNot(\"pattern1\", \"pattern2\", \"\")",
				"WhereProductLine(\"plFilter1\", \"plFilter2\", \"\")",
				"WhereUnit(\"unitFilter1\", \"unitFilter2\", \"\")",
				"WhereUnitOrValue(\"unitOrValueFilter1\", \"unitOrValueFilter2\", \"\")",
				"UseSeparators(\"separator1\", \"separator2\", \"\")",
				"DECODE(\"htmlText\")"
				};

		
		// Add completions for all keywords. A BasicCompletion is just a
		// straightforward word completion.
		for (int i = 0; i < functions.length; i++) {
			provider.addCompletion(new BasicCompletion(provider, functions[i]));
		}

		// Add a couple of "shorthand" completions. These completions don't
		// require the input text to be the same thing as the replacement text.
		provider.addCompletion(new ShorthandCompletion(provider, "line",
				"-------------------------------------------------------------",
				"-------------------------------------------------------------"));

		return provider;

	}
}
