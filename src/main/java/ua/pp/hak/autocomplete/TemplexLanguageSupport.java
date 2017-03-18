package ua.pp.hak.autocomplete;

import javax.swing.ListCellRenderer;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for Templex.
 *
 * @author VR
 * @version 1.0
 */
public class TemplexLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas editing C.
	 */
	private TemplexCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public TemplexLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
		setAutoActivationEnabled(true);
		setAutoActivationDelay(100);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new TemplexCellRenderer();
	}


	private TemplexCompletionProvider getProvider() {
		if (provider==null) {
			provider = new TemplexCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void install(RSyntaxTextArea textArea) {

		TemplexCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}


}