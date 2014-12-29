package editor;

import editor.data.OptionFile;
import editor.data.Stats;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PositionPanel extends JPanel implements ActionListener {
	private final OptionFile of;
	private volatile int regPos;

	private final JComboBox<String> regBox;
	private final JCheckBox[] regCheck;

	public PositionPanel(OptionFile opf) {
		super(new BorderLayout());
		of = opf;
		setBorder(BorderFactory.createTitledBorder("Position"));
		JPanel panel = new JPanel(new GridLayout(4, 4));
		JLabel regLabel = new JLabel("Registered Position");
		JPanel regPanel = new JPanel();
		// of = opf;
		// stats = s;
		regCheck = new JCheckBox[Stats.ROLES.length];
		for (int i = 0; i < Stats.ROLES.length; i++) {
			regCheck[i] = new JCheckBox(Stats.ROLES[i].getName());

			if (i != 1) {
				regCheck[i].setActionCommand(String.valueOf(i));
				regCheck[i].addActionListener(this);
				panel.add(regCheck[i]);
			}
			if (i == 0) {
				panel.add(new JPanel());
				panel.add(new JPanel());
				panel.add(new JPanel());
			}
		}

		regBox = new JComboBox<String>();
		regBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand() == "y") {
					String p = (String) regBox.getSelectedItem();
					int r = 0;
					for (int i = 0; i < Stats.ROLES.length; i++) {
						if (Stats.ROLES[i].getName().equals(p)) {
							r = i;
						}
					}
					// System.out.println(r);
					regPos = r;

					// stats.REG_POS.setValue(player, i);
				}
			}
		});
		regPanel.add(regLabel);
		regPanel.add(regBox);
		add(panel, BorderLayout.CENTER);
		add(regPanel, BorderLayout.SOUTH);
	}

	public JComboBox<String> getRegBox() {
		return regBox;
	}

	public JCheckBox getRegCheck(int index) {
		return regCheck[index];
	}

	public void load(int player) {
		regPos = Stats.getValue(of, player, Stats.REG_POS);
		for (int i = 0; i < Stats.ROLES.length; i++) {
			if (i != 1) {
				if (Stats.getValue(of, player, Stats.ROLES[i]) == 1
						|| regPos == i) {
					regCheck[i].setSelected(true);
				} else {
					regCheck[i].setSelected(false);
				}
			}
		}
		updateRegBox();
	}

	private void updateRegBox() {
		regBox.setActionCommand("n");
		regBox.removeAllItems();
		for (int i = 0; i < Stats.ROLES.length; i++) {
			if (regCheck[i].isSelected()) {
				regBox.addItem(Stats.ROLES[i].getName());
			}
		}
		regBox.setSelectedItem(Stats.ROLES[regPos].getName());
		regBox.setActionCommand("y");
	}

	public void actionPerformed(ActionEvent e) {
		int box = 0;
		try {
			box = Integer.parseInt(e.getActionCommand());
		} catch (NumberFormatException nfe) {
		}
		if (regPos == box) {
			regCheck[box].setSelected(true);
		}
		updateRegBox();
	}

}
