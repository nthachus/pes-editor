package editor.ui;

import editor.util.Resources;

import javax.swing.*;
import java.awt.*;

public class CsvSwitchPanel extends JPanel {
	private static final long serialVersionUID = -841354619971897911L;

	private final JCheckBox head;
	//private final JCheckBox extra;
	private final JCheckBox create;

	public CsvSwitchPanel() {
		super(new GridLayout(0, 1));

		head = new JCheckBox(Resources.getMessage("csv.headings"));
		//extra = new JCheckBox(Resources.getMessage("csv.extra"));
		create = new JCheckBox(Resources.getMessage("csv.create"));

		add(head);
		//add(extra);
		add(create);
	}

	public JCheckBox getHead() {
		return head;
	}

	/*public JCheckBox getExtra() {
		return extra;
	}*/

	public JCheckBox getCreate() {
		return create;
	}
}
