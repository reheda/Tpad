import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

// The Redo action
public class RedoAction extends AbstractAction {

	private UndoManager manager;

	public RedoAction(UndoManager manager) {
		this.manager = manager;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			manager.redo();
		} catch (CannotRedoException e) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

}
