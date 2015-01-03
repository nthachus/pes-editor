package editor;

import editor.data.OptionFile;
import editor.data.Player;
import editor.data.Stats;
import editor.ui.CancelButton;
import editor.ui.SelectByTeam;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlayerImportDialog extends JDialog implements ListSelectionListener, MouseListener {
	private final OptionFile of;
	private final OptionFile of2;

	private volatile int index;
	private volatile int replacement;

	private JLabel fileLabel;
	private SelectByTeam plList;
	private InfoPanel infoPanel;
	private JRadioButton allButton;
	private JRadioButton statsButton;

	public PlayerImportDialog(Frame owner, OptionFile opf, OptionFile opf2) {
		super(owner, "Import Player", true);
		of = opf;
		of2 = opf2;
		fileLabel = new JLabel("From:");
		plList = new SelectByTeam(of2, false);
		infoPanel = new InfoPanel(of2);
		plList.getSquadList().addListSelectionListener(this);
		plList.getSquadList().addMouseListener(this);
		CancelButton cancelButton = new CancelButton(this);

		allButton = new JRadioButton(
				"Import everything (name, appearance, stats, etc.)");
		statsButton = new JRadioButton(
				"Import only the stats editable on the 'Edit Player' dialog");
		JRadioButton exStatsButton = new JRadioButton(
				"Import everything except stats (name, appearance, etc.)");
		ButtonGroup group = new ButtonGroup();
		group.add(allButton);
		group.add(statsButton);
		group.add(exStatsButton);
		allButton.setSelected(true);

		JPanel topPanel = new JPanel(new GridLayout(4, 1));
		topPanel.add(fileLabel);
		topPanel.add(allButton);
		topPanel.add(statsButton);
		topPanel.add(exStatsButton);

		getContentPane().add(plList, BorderLayout.WEST);
		getContentPane().add(infoPanel, BorderLayout.CENTER);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(topPanel, BorderLayout.NORTH);

		index = 0;
		replacement = 0;
		pack();
		setResizable(false);
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	public void show(int i) {
		index = i;
		setVisible(true);
	}

	public void refresh() {
		plList.refresh();
		fileLabel.setText("  From:  " + of2.getFilename());
		index = 0;
		replacement = 0;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			if (!plList.getSquadList().isSelectionEmpty()) {
				infoPanel.refresh(plList.getSquadList().getSelectedValue().getIndex(), 0);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		int clicks = e.getClickCount();
		JList list = (JList) (e.getSource());
		int pi = ((Player) list.getSelectedValue()).getIndex();
		if (clicks == 2 && pi != 0) {
			replacement = pi;
			importPlayer();
			setVisible(false);
		}
	}

	private void importPlayer() {
		int ia = Player.START_ADR + (index * 124);
		if (index >= Player.FIRST_EDIT) {
			ia = Player.START_EDIT_ADR + ((index - Player.FIRST_EDIT) * 124);
		}
		int ra = Player.START_ADR + (replacement * 124);
		if (replacement >= Player.FIRST_EDIT) {
			ra = Player.START_EDIT_ADR + ((replacement - Player.FIRST_EDIT) * 124);
		}
		if (allButton.isSelected()) {
			System.arraycopy(of2.getData(), ra, of.getData(), ia, 124);
			Stats.setValue(of, index, Stats.NAME_EDITED, 1);
			Stats.setValue(of, index, Stats.CALL_EDITED, 1);
			Stats.setValue(of, index, Stats.SHIRT_EDITED, 1);
			Stats.setValue(of, index, Stats.ABILITY_EDITED, 1);
			/*
			 * if (!of.isWE() && of2.isWE()) { Convert.player(of, index,
			 * Convert.WE2007_PES6); } if (of.isWE() && !of2.isWE()) {
			 * Convert.player(of, index, Convert.PES6_WE2007); }
			 */
		} else if (statsButton.isSelected()) {
			Stats.setValue(of, index, Stats.NATIONALITY, Stats.getValue(of2,
					replacement, Stats.NATIONALITY));
			Stats.setValue(of, index, Stats.AGE, Stats.getValue(of2,
					replacement, Stats.AGE));
			Stats.setValue(of, index, Stats.HEIGHT, Stats.getValue(of2,
					replacement, Stats.HEIGHT));
			Stats.setValue(of, index, Stats.WEIGHT, Stats.getValue(of2,
					replacement, Stats.WEIGHT));
			Stats.setValue(of, index, Stats.FOOT, Stats.getValue(of2,
					replacement, Stats.FOOT));
			Stats.setValue(of, index, Stats.FAVORITE_SIDE, Stats.getValue(of2,
					replacement, Stats.FAVORITE_SIDE));
			Stats.setValue(of, index, Stats.WEAK_FOOT_ACC, Stats.getValue(of2,
					replacement, Stats.WEAK_FOOT_ACC));
			Stats.setValue(of, index, Stats.WEAK_FOOT_FREQ, Stats.getValue(of2,
					replacement, Stats.WEAK_FOOT_FREQ));
			Stats.setValue(of, index, Stats.CONDITION, Stats.getValue(of2,
					replacement, Stats.CONDITION));
			Stats.setValue(of, index, Stats.CONSISTENCY, Stats.getValue(of2,
					replacement, Stats.CONSISTENCY));
			Stats.setValue(of, index, Stats.INJURY, Stats.getValue(of2,
					replacement, Stats.INJURY));
			Stats.setValue(of, index, Stats.DRIBBLE_STYLE, Stats.getValue(of2,
					replacement, Stats.DRIBBLE_STYLE));
			Stats.setValue(of, index, Stats.PK_STYLE, Stats.getValue(of2,
					replacement, Stats.PK_STYLE));
			Stats.setValue(of, index, Stats.FREE_KICK, Stats.getValue(of2,
					replacement, Stats.FREE_KICK));
			Stats.setValue(of, index, Stats.DK_STYLE, Stats.getValue(of2,
					replacement, Stats.DK_STYLE));
			Stats.setValue(of, index, Stats.REG_POS, Stats.getValue(of2,
					replacement, Stats.REG_POS));

			for (int i = 0; i < Stats.ROLES.length; i++) {
				Stats.setValue(of, index, Stats.ROLES[i], Stats.getValue(of2,
						replacement, Stats.ROLES[i]));
			}
			for (int i = 0; i < Stats.ABILITY99.length; i++) {
				Stats.setValue(of, index, Stats.ABILITY99[i], Stats.getValue(
						of2, replacement, Stats.ABILITY99[i]));
			}
			for (int i = 0; i < Stats.ABILITY_SPECIAL.length; i++) {
				Stats.setValue(of, index, Stats.ABILITY_SPECIAL[i], Stats
						.getValue(of2, replacement, Stats.ABILITY_SPECIAL[i]));
			}
			Stats.setValue(of, index, Stats.ABILITY_EDITED, 1);
		} else {
			byte[] temp = new byte[124];
			System.arraycopy(of2.getData(), ra, temp, 0, 124);

			Stats.setValue(of2, replacement, Stats.NATIONALITY, Stats.getValue(
					of, index, Stats.NATIONALITY));
			Stats.setValue(of2, replacement, Stats.AGE, Stats.getValue(of,
					index, Stats.AGE));
			Stats.setValue(of2, replacement, Stats.HEIGHT, Stats.getValue(of,
					index, Stats.HEIGHT));
			Stats.setValue(of2, replacement, Stats.WEIGHT, Stats.getValue(of,
					index, Stats.WEIGHT));
			Stats.setValue(of2, replacement, Stats.FOOT, Stats.getValue(of,
					index, Stats.FOOT));
			Stats.setValue(of2, replacement, Stats.FAVORITE_SIDE, Stats.getValue(of,
					index, Stats.FAVORITE_SIDE));
			Stats.setValue(of2, replacement, Stats.WEAK_FOOT_ACC, Stats.getValue(of,
					index, Stats.WEAK_FOOT_ACC));
			Stats.setValue(of2, replacement, Stats.WEAK_FOOT_FREQ, Stats.getValue(of,
					index, Stats.WEAK_FOOT_FREQ));
			Stats.setValue(of2, replacement, Stats.CONDITION, Stats.getValue(
					of, index, Stats.CONDITION));
			Stats.setValue(of2, replacement, Stats.CONSISTENCY, Stats.getValue(
					of, index, Stats.CONSISTENCY));
			Stats.setValue(of2, replacement, Stats.INJURY, Stats.getValue(of,
					index, Stats.INJURY));
			Stats.setValue(of2, replacement, Stats.DRIBBLE_STYLE, Stats.getValue(of,
					index, Stats.DRIBBLE_STYLE));
			Stats.setValue(of2, replacement, Stats.PK_STYLE, Stats.getValue(of,
					index, Stats.PK_STYLE));
			Stats.setValue(of2, replacement, Stats.FREE_KICK, Stats.getValue(of,
					index, Stats.FREE_KICK));
			Stats.setValue(of2, replacement, Stats.DK_STYLE, Stats.getValue(of,
					index, Stats.DK_STYLE));
			Stats.setValue(of2, replacement, Stats.REG_POS, Stats.getValue(of,
					index, Stats.REG_POS));

			for (int i = 0; i < Stats.ROLES.length; i++) {
				Stats.setValue(of2, replacement, Stats.ROLES[i], Stats
						.getValue(of, index, Stats.ROLES[i]));
			}
			for (int i = 0; i < Stats.ABILITY99.length; i++) {
				Stats.setValue(of2, replacement, Stats.ABILITY99[i], Stats
						.getValue(of, index, Stats.ABILITY99[i]));
			}
			for (int i = 0; i < Stats.ABILITY_SPECIAL.length; i++) {
				Stats.setValue(of2, replacement, Stats.ABILITY_SPECIAL[i], Stats
						.getValue(of, index, Stats.ABILITY_SPECIAL[i]));
			}

			System.arraycopy(of2.getData(), ra, of.getData(), ia, 124);
			Stats.setValue(of, index, Stats.NAME_EDITED, 1);
			Stats.setValue(of, index, Stats.CALL_EDITED, 1);
			Stats.setValue(of, index, Stats.SHIRT_EDITED, 1);
			/*
			 * if (!of.isWE() && of2.isWE()) { Convert.player(of, index,
			 * Convert.WE2007_PES6); } if (of.isWE() && !of2.isWE()) {
			 * Convert.player(of, index, Convert.PES6_WE2007); }
			 */

			System.arraycopy(temp, 0, of2.getData(), ra, 124);
		}
	}
}
