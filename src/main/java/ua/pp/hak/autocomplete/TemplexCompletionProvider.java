package ua.pp.hak.autocomplete;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;


/**
 * A completion provider for the C programming language.  It provides
 * code completion support and parameter assistance for the C Standard Library.
 * This information is read from an XML file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TemplexCompletionProvider extends LanguageAwareCompletionProvider {


	/**
	 * Constructor.
	 */
	public TemplexCompletionProvider() {
		setDefaultCompletionProvider(createCodeCompletionProvider());
		setStringCompletionProvider(createStringCompletionProvider());
		setCommentCompletionProvider(createCommentCompletionProvider());
	}


	/**
	 * Adds shorthand completions to the code completion provider.
	 *
	 * @param codeCP The code completion provider.
	 */
	protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
		// codeCP.addCompletion(new ShorthandCompletion(codeCP, "main", "int
		// main(int argc, char **argv)"));
		codeCP.addCompletion(new ShorthandCompletion(codeCP, "line",
				"-------------------------------------------------------------\n"));
		
		codeCP.addCompletion(new TemplexTemplateCompletion(codeCP, "Data[String value]", "Data", "Data[\"${}\"]${cursor}", "String",
				"<strong>String Data[String value]</strong><br /><hr /><br />Empty description.<br /><br /><hr />Defined in: <em>RelatedProduct</em>"));
		codeCP.addCompletion(new TemplexTemplateCompletion(codeCP, "BulletFeatures[Int32 value]", "BulletFeatures","BulletFeatures[${0}]${cursor}", "String",
				"<strong>String BulletFeatures[Int32 value]</strong><br /><hr /><br />Empty description.<br /><br /><hr />Defined in: <em>TemplexGenerator</em>"));
		codeCP.addCompletion(new TemplexTemplateCompletion(codeCP, "Ksp[Int32 value]","Ksp", "Ksp[${0}]${cursor}", "String",
				"<strong>String Ksp[Int32 value]</strong><br /><hr /><br />Empty description.<br /><br /><hr />Defined in: <em>DigitalContent</em>"));
		codeCP.addCompletion(new TemplexTemplateCompletion(codeCP, "A[Int32 attrCode]","A", "A[${}]${cursor}", "TemplexAttribute",
				"<strong>TemplexAttribute A[Int32 attrCode]</strong><br /><hr /><br />"
				+ "The number between square brackets contains the ID of the attribute, for instance the attribute [35].<br /><br /><hr />Defined in: <em>Nothing</em>"));
		codeCP.addCompletion(new ShorthandCompletion(codeCP, "REFERENCE", "$TXCLIENTIDENTIFIER$", "String",
				"<strong>String $TXCLIENTIDENTIFIER$</strong><br /><hr /><br />"
						+ "References allows you to reuse the result of another expression. The following types of references are available:<br /><br />"
						+ "<em>Template item reference</em><br />"
						+ "You can reference another template item using the $ notation<br /><br />"
						+ "<strong>Note</strong> the following limitations:" + "<ul>"
						+ "<li>Referenced expressions cannot reference other expressions</li>"
						+ "<li>While items from other sections can be referenced, these must export into the same 'file'</li>"
						+ "<li>Something that's not an item meant for export cannot be referenced</li>" + "</ul>"
						+ "<br /><br /><hr />Defined in: <em>Nothing</em>"));

//for (int i=0; i<5000; i++) {
//	codeCP.addCompletion(new BasicCompletion(codeCP, "Number" + i));
//}
	}


	/**
	 * Returns the provider to use when editing code.
	 *
	 * @return The provider.
	 * @see #createCommentCompletionProvider()
	 * @see #createStringCompletionProvider()
	 * @see #loadCodeCompletionsFromXml(DefaultCompletionProvider)
	 * @see #addShorthandCompletions(DefaultCompletionProvider)
	 */
	protected CompletionProvider createCodeCompletionProvider() {
		DefaultCompletionProvider cp = new TemplexSourceCompletionProvider();
		loadCodeCompletionsFromXml(cp);
		addShorthandCompletions(cp);
		return cp;

	}


	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createStringCompletionProvider()
	 */
	protected CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		return cp;
	}


	/**
	 * Returns the completion provider to use when the caret is in a string.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createCommentCompletionProvider()
	 */
	protected CompletionProvider createStringCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
//		cp.addCompletion(new BasicCompletion(cp, "%c", "char", "Prints a character"));
//		cp.addCompletion(new BasicCompletion(cp, "%i", "signed int", "Prints a signed integer"));
//		cp.addCompletion(new BasicCompletion(cp, "%f", "float", "Prints a float"));
//		cp.addCompletion(new BasicCompletion(cp, "%s", "string", "Prints a string"));
//		cp.addCompletion(new BasicCompletion(cp, "%u", "unsigned int", "Prints an unsigned integer"));
//		cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline", "Prints a newline"));
		return cp;
	}


	/**
	 * Returns the name of the XML resource to load (on classpath or a file).
	 *
	 * @return The resource to load.
	 */
	protected String getXmlResource() {
		return "templex.xml";
	}


	/**
	 * Called from {@link #createCodeCompletionProvider()} to actually load
	 * the completions from XML.  Subclasses that override that method will
	 * want to call this one.
	 *
	 * @param cp The code completion provider.
	 */
	protected void loadCodeCompletionsFromXml(DefaultCompletionProvider cp) {
		// First try loading resource (running from demo jar), then try
		// accessing file (debugging in Eclipse).
		ClassLoader cl = getClass().getClassLoader();
		String res = getXmlResource();
		if (res!=null) { // Subclasses may specify a null value
			InputStream in = cl.getResourceAsStream(res);
			try {
				if (in!=null) {
					cp.loadFromXML(in);
					in.close();
				}
				else {
					cp.loadFromXML(new File(res));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}


}