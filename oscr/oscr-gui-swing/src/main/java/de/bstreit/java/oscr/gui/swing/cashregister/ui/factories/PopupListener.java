package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

class PopupListener extends MouseAdapter {

	private final JPopupMenu popup;
	private boolean active = false;

	public PopupListener(JPopupMenu popup) {
		super();
		this.popup = popup;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (active && e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void setActive(boolean present) {
		active = present;
	}
}