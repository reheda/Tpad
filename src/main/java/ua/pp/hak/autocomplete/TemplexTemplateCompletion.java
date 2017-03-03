package ua.pp.hak.autocomplete;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;

public class TemplexTemplateCompletion extends TemplateCompletion {

	/**
	 * A template completion for Templex.
	 *
	 * @author VR
	 * @version 1.0
	 */

	private String icon;

	public TemplexTemplateCompletion(CompletionProvider provider, String inputText, String definitionString,
			String template) {
		this(provider, inputText, definitionString, template, null);
	}

	public TemplexTemplateCompletion(CompletionProvider provider, String inputText, String definitionString,
			String template, String shortDesc) {
		this(provider, inputText, definitionString, template, shortDesc, null);
	}

	public TemplexTemplateCompletion(CompletionProvider provider, String inputText, String definitionString,
			String template, String shortDesc, String summary) {
		super(provider, inputText, definitionString, template, shortDesc, summary);
		setIcon("/images/template_obj.gif");
	}

	@Override
	public Icon getIcon() {
		return getIcon(icon);
	}

	public void setIcon(String iconId) {
		this.icon = iconId;
	}

	/**
	 * Returns an icon.
	 *
	 * @param resource
	 *            The icon to retrieve. This should either be a file, or a
	 *            resource loadable by the current ClassLoader.
	 * @return The icon.
	 */
	protected Icon getIcon(String resource) {
		URL url = getClass().getResource(resource);
		if (url == null) {
			File file = new File(resource);
			try {
				url = file.toURI().toURL();
			} catch (MalformedURLException mue) {
				mue.printStackTrace(); // Never happens
			}
		}
		return url != null ? new ImageIcon(url) : null;
	}

}
