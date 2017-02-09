package ua.pp.hak.ui;

import java.awt.Frame;

import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.SearchListener;

public class FindDialogRSTA extends FindDialog {

	private static final long serialVersionUID = 8332248462560576506L;

	public FindDialogRSTA(Frame owner, SearchListener listener) {
		super(owner, listener);
		markAllCheckBox.setEnabled(false);
	}

}
