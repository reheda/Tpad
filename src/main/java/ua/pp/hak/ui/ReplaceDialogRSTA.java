package ua.pp.hak.ui;

import java.awt.Frame;

import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchListener;

public class ReplaceDialogRSTA extends ReplaceDialog {

	private static final long serialVersionUID = 8924921815063814598L;

	public ReplaceDialogRSTA(Frame owner, SearchListener listener) {
		super(owner, listener);
		markAllCheckBox.setEnabled(false);
	}

}
