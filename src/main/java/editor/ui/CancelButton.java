package editor.ui;

import editor.lang.NullArgumentException;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 5048093651292658111L;

	private final Component dialog;

	public CancelButton(Component dialog) {
		super(Resources.getMessage("Cancel"));
		if (null == dialog) {
			throw new NullArgumentException("dialog");
		}
		this.dialog = dialog;

		addActionListener(this);
	}

	public void actionPerformed(ActionEvent evt) {
		dialog.setVisible(false);
	}
}
