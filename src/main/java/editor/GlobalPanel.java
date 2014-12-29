package editor;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Bits;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GlobalPanel extends JPanel {

	private static final String[] statNames = {
			"1-99 Stats", "ATTACK", "DEFENSE",
			"BALANCE", "STAMINA", "TOP SPEED", "ACCELERATION", "RESPONSE",
			"AGILITY", "DRIBBLE ACCURACY", "DRIBBLE SPEED",
			"SHORT PASS ACCURACY", "SHORT PASS SPEED", "LONG PASS ACCURACY",
			"LONG PASS SPEED", "SHOT ACCURACY", "SHOT POWER", "SHOT TECHNIQUE",
			"FREE KICK ACCURACY", "CURLING", "HEADING", "JUMP", "TECHNIQUE",
			"AGGRESSION", "MENTALITY", "GOAL KEEPING", "TEAM WORK",
			"1-8 Stats", "WEAK FOOT ACCURACY", "WEAK FOOT FREQUENCY",
			"CONSISTENCY", "CONDITION / FITNESS", "AGE"
	};

	private static final String[] ops = {"+", "-", "=", "+ %", "- %"};

	private static final String[] scopes = {
			"All Players", "GK", "SW", "CB", "SB", "DMF",
			"WB", "CMF", "SMF", "AMF", "WG", "SS", "CF"
	};

	private final OptionFile of;
	private final TransferPanel transferPan;

	private JTextField numField;
	private JComboBox<String> statBox;
	private JComboBox<String> opBox;
	private JComboBox<String> scopeBox;
	private JComboBox<String> teamBox;
	private JCheckBox onlyEditPlayer;

	public GlobalPanel(OptionFile of, TransferPanel tp) {
		super();
		if (null == of) throw new NullPointerException("of");
		if (null == tp) throw new NullPointerException("tp");
		this.of = of;
		transferPan = tp;

		initComponents();
	}

	private void initComponents() {
		statBox = new JComboBox<String>(statNames);
		numField = new JTextField(2);
		opBox = new JComboBox<String>(ops);
		JPanel panel99 = new JPanel();
		JPanel scopePanel = new JPanel(new GridLayout(2, 3));
		JPanel main = new JPanel(new GridLayout(0, 1));
		scopePanel.setBorder(BorderFactory.createTitledBorder("Scope"));
		panel99.setBorder(BorderFactory.createTitledBorder("Adjustment"));
		scopeBox = new JComboBox<String>(scopes);
		teamBox = new JComboBox<String>();
		onlyEditPlayer = new JCheckBox();

		JButton doButton = new JButton("Adjust");
		doButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doAdjust();
			}
		});

		scopePanel.add(new JLabel("Registered Position:"));
		scopePanel.add(new JLabel("Exclude Team:"));
		scopePanel.add(new JLabel("Created Players:"));
		scopePanel.add(scopeBox);
		scopePanel.add(teamBox);
		scopePanel.add(onlyEditPlayer);
		panel99.add(statBox);
		panel99.add(opBox);
		panel99.add(numField);
		panel99.add(doButton);
		main.add(scopePanel);
		main.add(panel99);

		add(main);
	}

	private void doAdjust() {
		int num = getNum();
		int si = statBox.getSelectedIndex();
		int v = num;
		int oi = opBox.getSelectedIndex();
		int max = 99;
		int min = 1;
		if (si == 32) {
			max = 46;
			min = 15;
		} else if (si > 26) {
			max = 8;
		}

		if (num == 0) {
			int dm = 1;
			int dx = 99;
			if (oi == 2) {
				dm = min;
				dx = max;
			}
			JOptionPane.showMessageDialog(null, "Enter a number: " + dm
					+ "-" + dx, "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			if (si == 0) {
				for (int p = 1; p < Player.TOTAL; p++) {
					if (adj(p) && adjTeam(p)) {
						for (int i = 1; i < 27; i++) {
							switch (oi) {
								case 0:
									v = getStat(i, p) + num;
									break;
								case 1:
									v = getStat(i, p) - num;
									break;
								case 2:
									v = num;
									break;
								case 3:
									v = getStat(i, p)
											+ (getStat(i, p) * num / 100);
									break;
								case 4:
									v = getStat(i, p)
											- (getStat(i, p) * num / 100);
									break;
							}
							if (v > 99) {
								v = 99;
							}
							if (v < 1) {
								v = 1;
							}
							setStat(i, p, v);
						}
					}
				}
				if (onlyEditPlayer.isSelected()) {
					for (int p = Player.FIRST_EDIT; p < 32952; p++) {
						if (adj(p) && adjTeam(p)) {
							for (int i = 1; i < 27; i++) {
								switch (oi) {
									case 0:
										v = getStat(i, p) + num;
										break;
									case 1:
										v = getStat(i, p) - num;
										break;
									case 2:
										v = num;
										break;
									case 3:
										v = getStat(i, p)
												+ (getStat(i, p) * num / 100);
										break;
									case 4:
										v = getStat(i, p)
												- (getStat(i, p) * num / 100);
										break;
								}
								if (v > 99) {
									v = 99;
								}
								if (v < 1) {
									v = 1;
								}
								setStat(i, p, v);
							}
						}
					}
				}
			}

			if (si == 27) {
				for (int p = 1; p < Player.TOTAL; p++) {
					if (adj(p) && adjTeam(p)) {
						for (int i = 28; i < 32; i++) {
							switch (oi) {
								case 0:
									v = getStat(i, p) + num;
									break;
								case 1:
									v = getStat(i, p) - num;
									break;
								case 2:
									v = num;
									break;
								case 3:
									v = getStat(i, p)
											+ (getStat(i, p) * num / 100);
									break;
								case 4:
									v = getStat(i, p)
											- (getStat(i, p) * num / 100);
									break;
							}
							if (v > 8) {
								v = 8;
							}
							if (v < 1) {
								v = 1;
							}
							setStat(i, p, v);
						}
					}
				}
				if (onlyEditPlayer.isSelected()) {
					for (int p = Player.FIRST_EDIT; p < 32952; p++) {
						if (adj(p) && adjTeam(p)) {
							for (int i = 28; i < 32; i++) {
								switch (oi) {
									case 0:
										v = getStat(i, p) + num;
										break;
									case 1:
										v = getStat(i, p) - num;
										break;
									case 2:
										v = num;
										break;
									case 3:
										v = getStat(i, p)
												+ (getStat(i, p) * num / 100);
										break;
									case 4:
										v = getStat(i, p)
												- (getStat(i, p) * num / 100);
										break;
								}
								if (v > 8) {
									v = 8;
								}
								if (v < 1) {
									v = 1;
								}
								setStat(i, p, v);
							}
						}
					}
				}
			}

			if (si != 0 && si != 27) {
				for (int p = 1; p < Player.TOTAL; p++) {
					if (adj(p) && adjTeam(p)) {
						switch (oi) {
							case 0:
								v = getStat(si, p) + num;
								break;
							case 1:
								v = getStat(si, p) - num;
								break;
							case 2:
								v = num;
								break;
							case 3:
								v = getStat(si, p)
										+ (getStat(si, p) * num / 100);
								break;
							case 4:
								v = getStat(si, p)
										- (getStat(si, p) * num / 100);
								break;
						}
						if (v > max) {
							v = max;
						}
						if (v < min) {
							v = min;
						}
						setStat(si, p, v);
					}
				}
				if (onlyEditPlayer.isSelected()) {
					for (int p = Player.FIRST_EDIT; p < 32952; p++) {
						if (adj(p) && adjTeam(p)) {
							switch (oi) {
								case 0:
									v = getStat(si, p) + num;
									break;
								case 1:
									v = getStat(si, p) - num;
									break;
								case 2:
									v = num;
									break;
								case 3:
									v = getStat(si, p)
											+ (getStat(si, p) * num / 100);
									break;
								case 4:
									v = getStat(si, p)
											- (getStat(si, p) * num / 100);
									break;
							}
							if (v > max) {
								v = max;
							}
							if (v < 1) {
								v = 1;
							}
							setStat(si, p, v);
						}
					}
				}
			}
			JOptionPane.showMessageDialog(null,
					"Stats have been adjusted", "Stats Adjusted",
					JOptionPane.INFORMATION_MESSAGE);
					/*
					 * String messageText1 = " set to: "; switch (oi) { case 0:
					 * messageText1 = " increased by: "; break; case 1:
					 * messageText1 = " decreased by: "; break; } String
					 * messageText2 = "every player"; if
					 * (scopeBox.getSelectedIndex() != 0) { messageText2 = "all
					 * players registered in the " +
					 * scopes[scopeBox.getSelectedIndex()] + " position"; }
					 * JOptionPane.showMessageDialog(null, statNames[si] +
					 * " of "+ messageText2 + messageText1 + num,
					 * "Stats Adjusted", JOptionPane.INFORMATION_MESSAGE);
					 */
			transferPan.refresh();
		}
	}

	private int getNum() {
		int v;
		int max = 99;
		int min = 1;
		int si = statBox.getSelectedIndex();
		if (opBox.getSelectedIndex() == 2) {
			if (si == 32) {
				max = 46;
				min = 15;
			} else if (si > 26) {
				max = 8;
			}
		}
		try {
			v = Integer.parseInt(numField.getText());
			if (v < min || v > max) {
				v = 0;
			}
		} catch (NumberFormatException nfe) {
			v = 0;
		}
		return v;
	}

	private void setStat(int si, int p, int num) {
		if (si == 32) {
			num = num - 15;
		} else if (si > 26) {
			num = num - 1;
		}
		if (si > 0 && si <= 26) {
			Stats.setValue(of, p, Stats.ABILITY99[si - 1], num);
		}
		switch (si) {
			case 28:
				Stats.setValue(of, p, Stats.WEAK_FOOT_ACC, num);
				break;
			case 29:
				Stats.setValue(of, p, Stats.WEAK_FOOT_FREQ, num);
				break;
			case 30:
				Stats.setValue(of, p, Stats.CONSISTENCY, num);
				break;
			case 31:
				Stats.setValue(of, p, Stats.CONDITION, num);
				break;
			case 32:
				Stats.setValue(of, p, Stats.AGE, num);
				break;
		}
		Stats.setValue(of, p, Stats.ABILITY_EDITED, 1);
	}

	private int getStat(int si, int p) {
		int v = 0;
		if (si > 0 && si <= 26) {
			v = Stats.getValue(of, p, Stats.ABILITY99[si - 1]);
		}
		switch (si) {
			case 28:
				v = Stats.getValue(of, p, Stats.WEAK_FOOT_ACC);
				break;
			case 29:
				v = Stats.getValue(of, p, Stats.WEAK_FOOT_FREQ);
				break;
			case 30:
				v = Stats.getValue(of, p, Stats.CONSISTENCY);
				break;
			case 31:
				v = Stats.getValue(of, p, Stats.CONDITION);
				break;
			case 32:
				v = Stats.getValue(of, p, Stats.AGE);
				break;
		}
		if (si == 32) {
			v = v + 15;
		} else if (si > 26) {
			v = v + 1;
		}
		return v;
	}

	private boolean adj(int p) {
		boolean result = false;
		int si = scopeBox.getSelectedIndex();
		if (si == 0) {
			result = true;
		} else {
			int v = Stats.getValue(of, p, Stats.REG_POS);
			if (si == 1) {
				si = si - 1;
			}
			if (v == si) {
				result = true;
			}
		}
		return result;
	}

	public void updateTeamBox(String[] teams) {
		String[] clubs = new String[Clubs.TOTAL + 1];
		clubs[0] = "None";
		System.arraycopy(teams, 0, clubs, 1, Clubs.TOTAL);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(clubs);
		teamBox.setModel(model);
	}

	private boolean adjTeam(int p) {
		boolean result = true;
		int ti = teamBox.getSelectedIndex();
		if (ti != 0) {
			int firstAdr = Squads.CLUB_ADR + ((ti - 1) * 64);
			int a;
			int i;
			for (int sp = 0; result && sp < 32; sp++) {
				a = firstAdr + (sp * 2);
				i = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
				if (i == p) {
					result = false;
				}
			}
		}
		return result;
	}

}