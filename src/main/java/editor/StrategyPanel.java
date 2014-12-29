package editor;

import editor.data.ControlButton;
import editor.data.OptionFile;
import editor.ui.Ps2ButtonIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StrategyPanel extends JPanel {
	private final OptionFile of;
	private final SquadList list;

	private volatile boolean ok = false;
	private volatile int squad = 0;
	//private volatile boolean auto = false;

	private final JComboBox<SweItem> overBox;
	private final JComboBox[] butBox = new JComboBox[4];
	//private final JButton autoButton;

	public StrategyPanel(OptionFile opf, SquadList l, PositionList pl) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Strategy"));
		of = opf;
		list = l;
		ActionListener act = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ok) {
					int b = Integer.parseInt(e.getActionCommand());
					byte strat = (byte) butBox[b].getSelectedIndex();
					// System.out.println(b + ", " + strat);
					Formations.setStrategy(of, squad, b, strat);
					// of.data[670608 + b + (628 * squad) + 6232] = strat;
					// && of.data[670612 + (628 * squad) + 6232] == 0
					if (strat == 6 && Formations.getStrategyOlCB(of, squad) == 0) {
						for (int i = 1; Formations.getStrategyOlCB(of, squad) == 0
								&& i < 11; i++) {
							byte pos = Formations.getPos(of, squad, 0, i);
							if (pos > 0 && pos < 8) {
								Formations.setStrategyOlCB(of, squad, i);
								// of.data[670612 + (628 * squad) + 6232] =
								// (byte) i;
							}
						}
					}
					refresh(squad);
				}
			}
		};

		String[] items = {
				"No Strategy", "Centre Att.", "R. Side Att.",
				"L. Side Att.", "Opp. Side Att.", "Change Sides", "CB Overlap",
				"Pressure", "Counter Attack", "Offside Trap",
				"Strategy Plan A", "Strategy Plan B"
		};

		JLabel[] labels = new JLabel[4];
		for (int i = 0; i < 4; i++) {
			labels[i] = new JLabel();
			labels[i].setPreferredSize(new Dimension(42, 17));
			labels[i].setText(null);
			labels[i].setIcon(new Ps2ButtonIcon(ControlButton.valueOf(i)));

			butBox[i] = new JComboBox<String>(items);
			butBox[i].setActionCommand(String.valueOf(i));
			butBox[i].addActionListener(act);
		}
		/*
		 * autoButton = new JButton("Manual"); autoButton.setPreferredSize(new
		 * Dimension(93, 26)); autoButton.addActionListener(new ActionListener()
		 * { public void actionPerformed(ActionEvent e) { if (ok) { auto =
		 * !auto; if (auto) { //of.data[670613 + (628 squad) + 6232] = 1;
		 * autoButton.setText("Semi-Auto"); } else { //of.data[670613 + (628
		 * squad) + 6232] = 0; autoButton.setText("Manual"); }
		 * Formations.setStrategyAuto(of, squad, auto); refresh(squad); } } });
		 */
		overBox = new JComboBox<SweItem>();
		overBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ok) {
					SweItem item = (SweItem) overBox.getSelectedItem();
					if (item != null) {
						Formations.setStrategyOlCB(of, squad, item.index);
						// of.data[670612 + (628 * squad) + 6232] = item.index;
					}
				}
			}
		});

		GridBagConstraints c = new GridBagConstraints();

		// c.anchor = GridBagConstraints.EAST;
		int x, y;
		for (int i = 0; i < 4; i++) {
			if (i < 2) {
				x = i + 1;
				y = 0;
			} else {
				x = i - 1;
				y = 2;
			}
			c.gridx = x;
			c.gridy = y;
			add(labels[i], c);

			c.gridx = x;
			c.gridy = y + 1;
			add(butBox[i], c);
		}

		// c.gridx = 0;
		// c.gridy = 0;
		// add(autoButton, c);

		c.gridx = 0;
		c.gridy = 4;
		add(new JLabel("Overlap CB:"), c);
		c.gridwidth = 1;

		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 4;
		add(overBox, c);
	}

	public void refresh(int s) {
		squad = s;
		ok = false;
		// int sa = of.data[670613 + (628 * squad) + 6232];
		/*
		 * if (Formations.getStrategyAuto(of, squad)) { auto = true;
		 * autoButton.setText("Semi-Auto"); for (int i = 0; i < 4; i++) { if (i
		 * == 0) { label[i].setText("L2"); } else { label[i].setText("AUTO " +
		 * i); } label[i].setIcon(null); } } else { auto = false;
		 * autoButton.setText("Manual"); for (int i = 0; i < 4; i++) {
		 * label[i].setText(null); label[i].setIcon(new Ps2ButtonIcon(i)); } }
		 */
		boolean ol = false;
		for (int i = 0; i < 4; i++) {
			int strat = Formations.getStrategy(of, squad, i);// of.data[670608 + i
			// + (628 * squad) +
			// 6232];
			butBox[i].setSelectedIndex(strat);
			if (strat == 6) {
				ol = true;
			}
		}

		overBox.removeAllItems();

		// int sw = of.data[670612 + (628 * squad) + 6232];
		// if (ol) {
		byte count = 0;
		byte sel = -1;
		for (byte i = 1; i < 11; i++) {
			byte pos = Formations.getPos(of, squad, 0, i);
			if (pos > 0 && pos < 8) {
				if (i == Formations.getStrategyOlCB(of, squad)) {
					sel = count;
				}
				overBox.addItem(new SweItem(i));
				count++;
			}
		}
		overBox.setSelectedIndex(sel);
		// }

		if (ol && overBox.getItemCount() != 0) {
			overBox.setEnabled(true);
		} else {
			overBox.setEnabled(false);
		}
		ok = true;
	}

	private class SweItem {
		String name;

		byte index;

		public SweItem(byte i) {
			index = i;
			name = list.getModel().getElementAt(index).name;
		}

		public String toString() {
			return name;
		}

	}

}
