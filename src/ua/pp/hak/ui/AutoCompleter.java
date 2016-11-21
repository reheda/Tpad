package ua.pp.hak.ui;

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

		// Add completions for all keywords. A BasicCompletion is just a
		// straightforward word completion.
		provider.addCompletion(new BasicCompletion(provider, "AltCats"));
		provider.addCompletion(new BasicCompletion(provider, "BestImage"));
		provider.addCompletion(new BasicCompletion(provider, "BestImages"));
		provider.addCompletion(new BasicCompletion(provider, "Body"));
		provider.addCompletion(new BasicCompletion(provider, "BoxContents"));
		provider.addCompletion(new BasicCompletion(provider, "Brand"));
		provider.addCompletion(new BasicCompletion(provider, "BulletFeatures"));
		provider.addCompletion(new BasicCompletion(provider, "CategoryCode"));
		provider.addCompletion(new BasicCompletion(provider, "CategoryCodes"));
		provider.addCompletion(new BasicCompletion(provider, "CategoryKey"));
		provider.addCompletion(new BasicCompletion(provider, "CategoryKeys"));
		provider.addCompletion(new BasicCompletion(provider, "CategoryName"));
		provider.addCompletion(new BasicCompletion(provider, "Colors"));
		provider.addCompletion(new BasicCompletion(provider, "CompatibleProducts"));
		provider.addCompletion(new BasicCompletion(provider, "Count"));
		provider.addCompletion(new BasicCompletion(provider, "CustomerPn"));
		provider.addCompletion(new BasicCompletion(provider, "CustomerPns"));
		provider.addCompletion(new BasicCompletion(provider, "Description"));
		provider.addCompletion(new BasicCompletion(provider, "Distinct"));
		provider.addCompletion(new BasicCompletion(provider, "FancyColors"));
		provider.addCompletion(new BasicCompletion(provider, "GroupName"));
		provider.addCompletion(new BasicCompletion(provider, "HasText"));
		provider.addCompletion(new BasicCompletion(provider, "Header"));
		provider.addCompletion(new BasicCompletion(provider, "Invariant"));
		provider.addCompletion(new BasicCompletion(provider, "InvariantColors"));
		provider.addCompletion(new BasicCompletion(provider, "InvariantUnit"));
		provider.addCompletion(new BasicCompletion(provider, "InvariantValues"));
		provider.addCompletion(new BasicCompletion(provider, "IsEmpty"));
		provider.addCompletion(new BasicCompletion(provider, "ItemName"));
		provider.addCompletion(new BasicCompletion(provider, "Key"));
		provider.addCompletion(new BasicCompletion(provider, "KeySellingPoints"));
		provider.addCompletion(new BasicCompletion(provider, "Keywords"));
		provider.addCompletion(new BasicCompletion(provider, "Ksp"));
		provider.addCompletion(new BasicCompletion(provider, "LaunchDate"));
		provider.addCompletion(new BasicCompletion(provider, "Length"));
		provider.addCompletion(new BasicCompletion(provider, "LineCount"));
		provider.addCompletion(new BasicCompletion(provider, "Main"));
		provider.addCompletion(new BasicCompletion(provider, "Manufacturer"));
		provider.addCompletion(new BasicCompletion(provider, "ManufacturerNames"));
		provider.addCompletion(new BasicCompletion(provider, "MarketingText"));
		provider.addCompletion(new BasicCompletion(provider, "MimeType"));
		provider.addCompletion(new BasicCompletion(provider, "ModelName"));
		provider.addCompletion(new BasicCompletion(provider, "ModelNames"));
		provider.addCompletion(new BasicCompletion(provider, "Msds"));
		provider.addCompletion(new BasicCompletion(provider, "Name"));
		provider.addCompletion(new BasicCompletion(provider, "NonOemAccessories"));
		provider.addCompletion(new BasicCompletion(provider, "Order"));
		provider.addCompletion(new BasicCompletion(provider, "PackQuantity"));
		provider.addCompletion(new BasicCompletion(provider, "PartNumber"));
		provider.addCompletion(new BasicCompletion(provider, "ProductFeatures"));
		provider.addCompletion(new BasicCompletion(provider, "ProductId"));
		provider.addCompletion(new BasicCompletion(provider, "ProductIds"));
		provider.addCompletion(new BasicCompletion(provider, "ProductLine"));
		provider.addCompletion(new BasicCompletion(provider, "ProductLineNames"));
		provider.addCompletion(new BasicCompletion(provider, "ProductNames"));
		provider.addCompletion(new BasicCompletion(provider, "ProductSheet"));
		provider.addCompletion(new BasicCompletion(provider, "ProductType"));
		provider.addCompletion(new BasicCompletion(provider, "QuickStartGuide"));
		provider.addCompletion(new BasicCompletion(provider, "Thumbnail"));
		provider.addCompletion(new BasicCompletion(provider, "Total"));
		provider.addCompletion(new BasicCompletion(provider, "Unit"));
		provider.addCompletion(new BasicCompletion(provider, "UnitUSM"));
		provider.addCompletion(new BasicCompletion(provider, "Url"));
		provider.addCompletion(new BasicCompletion(provider, "UserManual"));
		provider.addCompletion(new BasicCompletion(provider, "Value"));
		provider.addCompletion(new BasicCompletion(provider, "Values"));
		provider.addCompletion(new BasicCompletion(provider, "ValuesAndUnits"));
		provider.addCompletion(new BasicCompletion(provider, "ValuesAndUnitsUSM"));
		provider.addCompletion(new BasicCompletion(provider, "ValuesUSM"));
		provider.addCompletion(new BasicCompletion(provider, "ValueUSM"));
		provider.addCompletion(new BasicCompletion(provider, "XmlContent"));
		provider.addCompletion(new BasicCompletion(provider, "AND"));
		provider.addCompletion(new BasicCompletion(provider, "CASE"));
		provider.addCompletion(new BasicCompletion(provider, "CAT"));
		provider.addCompletion(new BasicCompletion(provider, "DC"));
		provider.addCompletion(new BasicCompletion(provider, "DECODE"));
		provider.addCompletion(new BasicCompletion(provider, "ELSE IF"));
		provider.addCompletion(new BasicCompletion(provider, "ELSE"));
		provider.addCompletion(new BasicCompletion(provider, "END"));
		provider.addCompletion(new BasicCompletion(provider, "IF"));
		provider.addCompletion(new BasicCompletion(provider, "IN"));
		provider.addCompletion(new BasicCompletion(provider, "IS NOT NULL"));
		provider.addCompletion(new BasicCompletion(provider, "IS NULL"));
		provider.addCompletion(new BasicCompletion(provider, "LIKE"));
		provider.addCompletion(new BasicCompletion(provider, "MS"));
		provider.addCompletion(new BasicCompletion(provider, "NOT LIKE"));
		provider.addCompletion(new BasicCompletion(provider, "NOT NULL"));
		provider.addCompletion(new BasicCompletion(provider, "NULL"));
		provider.addCompletion(new BasicCompletion(provider, "OR"));
		provider.addCompletion(new BasicCompletion(provider, "SKU"));
		provider.addCompletion(new BasicCompletion(provider, "THEN"));
		provider.addCompletion(new BasicCompletion(provider, "WHEN"));

		provider.addCompletion(new BasicCompletion(provider, "Round()"));
		provider.addCompletion(new BasicCompletion(provider, "Min()"));
		provider.addCompletion(new BasicCompletion(provider, "MinMax()"));
		provider.addCompletion(new BasicCompletion(provider, "Match()"));
		provider.addCompletion(new BasicCompletion(provider, "Max()"));
		provider.addCompletion(new BasicCompletion(provider, "ListPaths()"));
		provider.addCompletion(new BasicCompletion(provider, "ListUSM()"));
		provider.addCompletion(new BasicCompletion(provider, "Last()"));
		provider.addCompletion(new BasicCompletion(provider, "FlattenWithAnd()"));
		provider.addCompletion(new BasicCompletion(provider, "HtmlEncode()"));
		provider.addCompletion(new BasicCompletion(provider, "GetLines()"));
		provider.addCompletion(new BasicCompletion(provider, "GetPath()"));
		provider.addCompletion(new BasicCompletion(provider, "First()"));
		provider.addCompletion(new BasicCompletion(provider, "DiscardNulls()"));
		provider.addCompletion(new BasicCompletion(provider, "ExtractDecimals()"));
		provider.addCompletion(new BasicCompletion(provider, "GetAncestry()"));
		provider.addCompletion(new BasicCompletion(provider, "GetDateTime()"));
		provider.addCompletion(new BasicCompletion(provider, "GetDescendants()"));
		provider.addCompletion(new BasicCompletion(provider, "GetFullColorDescription()"));
		provider.addCompletion(new BasicCompletion(provider, "ToHtml()"));
		provider.addCompletion(new BasicCompletion(provider, "ToLower()"));
		provider.addCompletion(new BasicCompletion(provider, "ToLowerFirstChar()"));
		provider.addCompletion(new BasicCompletion(provider, "ToPlainText()"));
		provider.addCompletion(new BasicCompletion(provider, "ToTitleCase()"));
		provider.addCompletion(new BasicCompletion(provider, "ToUpper()"));
		provider.addCompletion(new BasicCompletion(provider, "ToUpperFirstChar()"));
		provider.addCompletion(new BasicCompletion(provider, "COALESCE(\"text\")"));
		
		// Add a couple of "shorthand" completions. These completions don't
		// require the input text to be the same thing as the replacement text.
		provider.addCompletion(
				new ShorthandCompletion(provider, "sysout", "System.out.println(", "System.out.println("));
//		provider.addCompletion(new ShorthandCompletion(provider, "a", "A[]", "A[]"));
//		provider.addCompletion(new ShorthandCompletion(provider, "A", "A[]", "A[]"));
//		provider.addCompletion(new ShorthandCompletion(provider, "AtLeast", "AtLeast(minimum)", "AtLeast(minimum)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "AtMost", "AtMost(maximum)", "AtMost(maximum)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Erase", "Erase(\"value\")", "Erase(\"value\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "EraseTextSurroundedBy", "EraseTextSurroundedBy(\"surroundingCharacters\")", "EraseTextSurroundedBy(\"surroundingCharacters\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Format", "Format(\"literalFormat\")", "Format(\"literalFormat\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "GetLine", "GetLine(3)", "GetLine(3)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "GetLineBody", "GetLineBody(2)", "GetLineBody(2)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Flatten", "Flatten(\"\")", "Flatten(\"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "IfLike", "IfLike(\"pattern\", \"replacement\")", "IfLike(\"pattern\", \"replacement\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "IfLongerThan", "IfLongerThan(2, \"replacementText\")", "IfLongerThan(2, \"replacementText\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "IsDescendantOf", "IsDescendantOf(\"categoryKey\")", "IsDescendantOf(\"categoryKey\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Match 1 arg", "Match(1)", "Match(1)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Match 3 args", "Match(2, 2, 1)", "Match(2, 2, 1)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Pick", "Pick(2)", "Pick(2)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Pluralize", "Pluralize()", "Pluralize()"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Postfix", "Postfix(\"suffix\")", "Postfix(\"suffix\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Prefix", "Prefix(\"prefix\")", "Prefix(\"prefix\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "RegexReplace", "RegexReplace(\"pattern\", \"substitution\")", "RegexReplace(\"pattern\", \"substitution\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Replace", "Replace(\"oldValue\", \"newValue\")", "Replace(\"oldValue\", \"newValue\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Shorten", "Shorten(1)", "Shorten(1)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Shorten 2 agrs", "Shorten(1, \"\")", "Shorten(1, \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Skip", "Skip(5)", "Skip(5)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Split 1 arg", "Split(\"separator1\")", "Split(\"separator1\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Split 3 args", "Split(\"separator1\", \"separator2\", \"\")", "Split(\"separator1\", \"separator2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "MultiplyBy", "MultiplyBy(factor)", "MultiplyBy(factor)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Take", "Take(2)", "Take(2)"));
//		provider.addCompletion(new ShorthandCompletion(provider, "ToText", "ToText(\"literalFormat\")", "ToText(\"literalFormat\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Where 1arg", "Where(\"pattern1\")", "Where(\"pattern1\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "Where 3args", "Where(\"pattern1\", \"pattern2\", \"\")", "Where(\"pattern1\", \"pattern2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereCategory", "WhereCategory(\"codeOrKey1\", \"codeOrKey2\", \"\")", "WhereCategory(\"codeOrKey1\", \"codeOrKey2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereManufacturer", "WhereManufacturer(\"mfFilter1\", \"mfFilter2\", \"\")", "WhereManufacturer(\"mfFilter1\", \"mfFilter2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereModelName", "WhereModelName(\"modelFilter1\", \"modelFilter2\", \"\")", "WhereModelName(\"modelFilter1\", \"modelFilter2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereNot 1arg", "WhereNot(\"pattern1\")", "WhereNot(\"pattern1\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereNot 3args", "WhereNot(\"pattern1\", \"pattern2\", \"\")", "WhereNot(\"pattern1\", \"pattern2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereProductLine", "WhereProductLine(\"plFilter1\", \"plFilter2\", \"\")", "WhereProductLine(\"plFilter1\", \"plFilter2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereUnit", "WhereUnit(\"unitFilter1\", \"unitFilter2\", \"\")", "WhereUnit(\"unitFilter1\", \"unitFilter2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "WhereUnitOrValue", "WhereUnitOrValue(\"unitOrValueFilter1\", \"unitOrValueFilter2\", \"\")", "WhereUnitOrValue(\"unitOrValueFilter1\", \"unitOrValueFilter2\", \"\")"));
//		provider.addCompletion(new ShorthandCompletion(provider, "UseSeparators", "UseSeparators(\"separator1\", \"separator2\", \"\")", "UseSeparators(\"separator1\", \"separator2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "A[]"));
		provider.addCompletion(new BasicCompletion(provider, "AtLeast(minimum)"));
		provider.addCompletion(new BasicCompletion(provider, "AtMost(maximum)"));
		provider.addCompletion(new BasicCompletion(provider, "Erase(\"value\")"));
		provider.addCompletion(new BasicCompletion(provider, "EraseTextSurroundedBy(\"surroundingCharacters\")"));
		provider.addCompletion(new BasicCompletion(provider, "Format(\"literalFormat\")"));
		provider.addCompletion(new BasicCompletion(provider, "GetLine(3)"));
		provider.addCompletion(new BasicCompletion(provider, "GetLineBody(2)"));
		provider.addCompletion(new BasicCompletion(provider, "Flatten(\"\")"));
		provider.addCompletion(new BasicCompletion(provider, "IfLike(\"pattern\", \"replacement\")"));
		provider.addCompletion(new BasicCompletion(provider, "IfLongerThan(2, \"replacementText\")"));
		provider.addCompletion(new BasicCompletion(provider, "IsDescendantOf(\"categoryKey\")"));
		provider.addCompletion(new BasicCompletion(provider, "Match(1)"));
		provider.addCompletion(new BasicCompletion(provider, "Match(2, 2, 1)"));
		provider.addCompletion(new BasicCompletion(provider, "Pick(2)"));
		provider.addCompletion(new BasicCompletion(provider, "Pluralize()"));
		provider.addCompletion(new BasicCompletion(provider, "Postfix(\"suffix\")"));
		provider.addCompletion(new BasicCompletion(provider, "Prefix(\"prefix\")"));
		provider.addCompletion(new BasicCompletion(provider, "RegexReplace(\"pattern\", \"substitution\")"));
		provider.addCompletion(new BasicCompletion(provider, "Replace(\"oldValue\", \"newValue\")"));
		provider.addCompletion(new BasicCompletion(provider, "Shorten(1)"));
		provider.addCompletion(new BasicCompletion(provider, "Shorten(1, \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "Skip(5)"));
		provider.addCompletion(new BasicCompletion(provider, "Split(\"separator1\")"));
		provider.addCompletion(new BasicCompletion(provider, "Split(\"separator1\", \"separator2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "MultiplyBy(factor)"));
		provider.addCompletion(new BasicCompletion(provider, "Take(2)"));
		provider.addCompletion(new BasicCompletion(provider, "ToText(\"literalFormat\")"));
		provider.addCompletion(new BasicCompletion(provider, "Where(\"pattern1\")"));
		provider.addCompletion(new BasicCompletion(provider, "Where(\"pattern1\", \"pattern2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereCategory(\"codeOrKey1\", \"codeOrKey2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereManufacturer(\"mfFilter1\", \"mfFilter2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereModelName(\"modelFilter1\", \"modelFilter2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereNot(\"pattern1\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereNot(\"pattern1\", \"pattern2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereProductLine(\"plFilter1\", \"plFilter2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereUnit(\"unitFilter1\", \"unitFilter2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "WhereUnitOrValue(\"unitOrValueFilter1\", \"unitOrValueFilter2\", \"\")"));
		provider.addCompletion(new BasicCompletion(provider, "UseSeparators(\"separator1\", \"separator2\", \"\")"));

		return provider;

	}
}
