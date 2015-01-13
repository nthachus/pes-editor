package editor.ui;

import editor.data.*;
import editor.util.Bits;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GlobalPanel extends JPanel implements ActionListener {
	private final OptionFile of;
	private final TransferPanel transferPan;

	public GlobalPanel(OptionFile of, TransferPanel tp) {
		super();
		if (null == of) throw new NullPointerException("of");
		if (null == tp) throw new NullPointerException("tp");
		this.of = of;
		transferPan = tp;

		initComponents();
	}

	//region Initialize the GUI components

	private JTextField numField;
	private JComboBox statBox;
	private JComboBox opBox;
	private JComboBox scopeBox;
	private JComboBox<String> teamBox;
	private JCheckBox isExcluded;
	private JCheckBox forEditPlayer;

	private static String[] getScopes() {
		ArrayList<String> scopes = new ArrayList<String>();
		scopes.add(Resources.getMessage("All Players"));
		for (Stat st : Stats.ROLES)
			scopes.add(st.getName());

		return scopes.toArray(new String[scopes.size()]);
	}

	private static final Stat[] STAT_NAMES = {
			Stats.WEAK_FOOT_ACC,
			Stats.WEAK_FOOT_FREQ,
			Stats.CONSISTENCY,
			Stats.CONDITION,
			Stats.AGE
	};

	private static String[] getStatNames() {
		ArrayList<String> names = new ArrayList<String>();
		names.add(Resources.getMessage("1-99 Stats"));
		for (Stat st : Stats.ABILITY99)
			names.add(Resources.getMessage(st.getName()).toUpperCase());

		names.add(Resources.getMessage("1-8 Stats"));
		for (Stat st : STAT_NAMES)
			names.add(Resources.getMessage(st.getName()).toUpperCase());

		return names.toArray(new String[names.size()]);
	}

	private static Stat getStat(int nameIndex) {
		int len = Stats.ABILITY99.length;
		if (nameIndex > 0 && nameIndex <= len)
			return Stats.ABILITY99[nameIndex - 1];

		if (nameIndex > len + 1)
			return STAT_NAMES[nameIndex - len - 2];

		return null;
	}

	private static final String[] OPS = {"+", "-", "=", "+ %", "- %"};

	private void initComponents() {
		scopeBox = new JComboBox<String>(getScopes());
		teamBox = new JComboBox<String>();
		isExcluded = new JCheckBox();
		forEditPlayer = new JCheckBox();

		JPanel scopeLeft = new JPanel(new GridLayout(2, 2));
		scopeLeft.add(new JLabel(Resources.getMessage("global.regPos")));
		scopeLeft.add(new JLabel(Resources.getMessage("global.team")));
		scopeLeft.add(scopeBox);
		scopeLeft.add(teamBox);

		JPanel scopeRight = new JPanel(new GridLayout(2, 2));
		scopeRight.add(new JLabel(Resources.getMessage("global.excluded")));
		scopeRight.add(new JLabel(Resources.getMessage("global.forEdit")));
		scopeRight.add(isExcluded);
		scopeRight.add(forEditPlayer);

		JPanel scopePanel = new JPanel();
		scopePanel.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("global.scope")));
		scopePanel.add(scopeLeft);
		scopePanel.add(scopeRight);

		statBox = new JComboBox<String>(getStatNames());
		opBox = new JComboBox<String>(OPS);
		numField = new JTextField(2);
		numField.setDocument(new JTextFieldLimit(2));

		JButton adjustBtn = new JButton(Resources.getMessage("global.adjust"));
		adjustBtn.addActionListener(this);

		JPanel panel99 = new JPanel();
		panel99.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("global.adjustment")));

		panel99.add(statBox);
		panel99.add(opBox);
		panel99.add(numField);
		panel99.add(adjustBtn);

		JPanel mainPanel = new JPanel(new GridLayout(0, 1));
		mainPanel.add(scopePanel);
		mainPanel.add(panel99);

		add(mainPanel);
	}

	//endregion

	/**
	 * Do adjust.
	 */
	public void actionPerformed(ActionEvent evt) {
		int statIndex = statBox.getSelectedIndex();
		if (statIndex < 0) return;

		int min = 1, max = Stats.MAX_STAT99;
		Stat st = getStat(statIndex);
		int len = Stats.ABILITY99.length;
		if (null != st && statIndex > len) {
			min = st.minValue();
			max = st.maxValue();
		}

		int opId = opBox.getSelectedIndex();
		int dm = 1, dx = Stats.MAX_STAT99;
		if (opId == 2) {
			dm = min;
			dx = max;
		}

		int num = getNum(dm, dx);
		if (num <= 0) {
			JOptionPane.showMessageDialog(null, Resources.getMessage("msg.invalidRange", dm, dx),
					Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (statIndex == 0) {// abilities (1-99)
			adjustStats(1, len + 1, opId, num, min, max);
		} else if (statIndex == len + 1) {// 4 abilities (1-8)
			adjustStats(len + 2, len + 2 + 4, opId, num, min, max);
		} else {
			adjustStats(statIndex, statIndex + 1, opId, num, min, max);
		}

		JOptionPane.showMessageDialog(null, Resources.getMessage("msg.statsAdj"),
				Resources.getMessage("msg.statsAdj.title"), JOptionPane.INFORMATION_MESSAGE);
		transferPan.refresh();
	}

	private void adjustStats(int from, int to, int opId, int num, int min, int max) {
		for (int p = 1; p < Player.TOTAL; p++) {
			if (isPlayerInScope(p) && !inExcludedTeam(p)) {
				for (int i = from; i < to; i++)
					setStat(i, p, opId, num, min, max);
			}
		}

		if (forEditPlayer.isSelected()) {
			for (int p = Player.FIRST_EDIT; p < Player.END_EDIT; p++) {
				if (isPlayerInScope(p) && !inExcludedTeam(p)) {
					for (int i = from; i < to; i++)
						setStat(i, p, opId, num, min, max);
				}
			}
		}
	}

	/**
	 * Stat number value -or- zero on error.
	 */
	private int getNum(int min, int max) {
		int v;
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

	private void setStat(int statIndex, int player, int opId, int num, int min, int max) {
		int v = 0;
		if (opId != 2)
			v = getStat(statIndex, player);

		switch (opId) {
			case 0:
				v += num;
				break;
			case 1:
				v -= num;
				break;
			case 3:
				v += Math.round((float) v * num / 100);
				break;
			case 4:
				v -= Math.round((float) v * num / 100);
				break;
			default://==
				v = num;
				break;
		}

		if (v > max) v = max;
		else if (v < min) v = min;

		setStat(statIndex, player, v);
	}

	private void setStat(int statIndex, int player, int num) {
		Stat st = getStat(statIndex);
		if (null != st) {
			num -= st.minValue();
			Stats.setValue(of, player, st, num);

			Stats.setValue(of, player, Stats.ABILITY_EDITED, 1);
		}
	}

	private int getStat(int statIndex, int player) {
		int v = 0;
		Stat st = getStat(statIndex);
		if (null != st) {
			v = Stats.getValue(of, player, st);
			v += st.minValue();
		}
		return v;
	}

	private boolean isPlayerInScope(int player) {
		int scopeId = scopeBox.getSelectedIndex();
		if (scopeId == 0)
			return true;
		else if (scopeId > 0) {
			int v = Stats.getValue(of, player, Stats.REG_POS);
			if (Stats.regPosToRole(v) == scopeId - 1)
				return true;
		}
		return false;
	}

	/**
	 * Determine whether the selected team contains the specified player or not.
	 */
	private boolean inExcludedTeam(int player) {
		int teamId = teamBox.getSelectedIndex();
		if (teamId > 0) {
			boolean toExclude = isExcluded.isSelected();

			int adr = Squads.CLUB_ADR + (teamId - 1) * Formations.CLUB_TEAM_SIZE * 2;
			for (int sp = 0; sp < Formations.CLUB_TEAM_SIZE; sp++) {
				int id = Bits.toInt16(of.getData(), adr);
				if (id == player)
					return toExclude;
				adr += 2;
			}

			return !toExclude;
		}

		return false;
	}

	public void updateTeamBox(String[] teams) {
		if (null == teams) throw new NullPointerException("teams");

		String[] clubs = new String[teams.length + 1];
		clubs[0] = Resources.getMessage("None");
		System.arraycopy(teams, 0, clubs, 1, teams.length);

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(clubs);
		teamBox.setModel(model);
	}

}
