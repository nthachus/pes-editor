package editor.ui;

import editor.util.Strings;

import javax.swing.*;
import java.awt.*;

public class CsvSwitchPanel extends JPanel {
	private final JCheckBox head;
	private final JCheckBox extra;
	private final JCheckBox create;

	public CsvSwitchPanel() {
		super(new GridLayout(0, 1));

		head = new JCheckBox(Strings.getMessage("csv.headings"));
		extra = new JCheckBox(Strings.getMessage("csv.extra"));
		create = new JCheckBox(Strings.getMessage("csv.create"));

		add(head);
		add(extra);
		add(create);
	}

	public JCheckBox getHead() {
		return head;
	}

	public JCheckBox getExtra() {
		return extra;
	}

	public JCheckBox getCreate() {
		return create;
	}
}
