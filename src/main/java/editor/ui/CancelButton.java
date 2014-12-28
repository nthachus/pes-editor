package editor.ui;

import editor.util.Strings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelButton extends JButton {

	public CancelButton(final JDialog dialog) {
		super(Strings.getMessage("Cancel"));
		if (null == dialog) throw new NullPointerException("dialog");

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dialog.setVisible(false);
			}
		});
	}

}
