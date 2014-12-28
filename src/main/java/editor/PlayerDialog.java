/*
 * Copyright 2008-9 Compulsion
 * <pes_compulsion@yahoo.co.uk>
 * <http://www.purplehaze.eclipse.co.uk/>
 * <http://uk.geocities.com/pes_compulsion/>
 *
 * This file is part of PES Editor.
 *
 * PES Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PES Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PES Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package editor;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.ui.Ability99Panel;
import editor.ui.CancelButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerDialog extends JDialog {// implements ListSelectionListener,
	// MouseListener {
	OptionFile of;

	// OptionFile of2;
	// OptionFile of2;
	// JButton cancelBut;
	// JButton importBut;
	// JLabel fileLabel;
	// JLabel oldPlLabel;
	// PlayerList plList;
	// InfoPanel2 infoPanel;
	// boolean of2Open;
	int index;

	Player player;

	GeneralAbilityPanel genPanel;

	PositionPanel posPanel;

	Ability99Panel abiPanel;

	SpecialAbilityPanel spePanel;

	JButton acceptButton;

	JButton cancelButton;

	JButton importButton;

	// int replacement;

	PlayerImportDialog plImpDia;

	public PlayerDialog(Frame owner, OptionFile opf, PlayerImportDialog pid) {
		super(owner, "Edit Player", true);
		JPanel panel = new JPanel();
		JPanel lPanel = new JPanel(new BorderLayout());
		JPanel bPanel = new JPanel();
		acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				if (check()) {
					updateStats();
					setVisible(false);
				}
			}
		});
		CancelButton cancelButton = new CancelButton(this);
		importButton = new JButton("Import (OF2)");
		importButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent i) {
				// System.out.println(index);
				plImpDia.show(index);
				setVisible(false);
			}
		});
		of = opf;
		plImpDia = pid;
		// of2 = opf2;
		genPanel = new GeneralAbilityPanel(of);
		posPanel = new PositionPanel(of);
		abiPanel = new Ability99Panel(of);
		spePanel = new SpecialAbilityPanel(of);

		bPanel.add(acceptButton);
		bPanel.add(cancelButton);
		bPanel.add(importButton);
		lPanel.add(genPanel, BorderLayout.NORTH);
		lPanel.add(posPanel, BorderLayout.CENTER);
		lPanel.add(bPanel, BorderLayout.SOUTH);
		panel.add(lPanel);
		panel.add(abiPanel);
		panel.add(spePanel);
		getContentPane().add(panel);
		pack();
		setResizable(false);
	}

	public void show(Player p) {
		index = p.index;
		player = p;
		setTitle("Edit Player - " + String.valueOf(index) + " - " + p.name);
		if (plImpDia.isOf2Open()) {
			importButton.setVisible(true);
		} else {
			importButton.setVisible(false);
		}
		genPanel.load(index);
		posPanel.load(index);
		abiPanel.load(index);
		spePanel.load(index);
		setVisible(true);
	}

	private boolean check() {
		boolean ok = true;
		int v;
		for (int i = 0; i < Stats.ABILITY99.length; i++) {
			try {
				v = Integer.parseInt(abiPanel.getField(i).getText());
				if (v < 1 || v > 99) {
					ok = false;
				}
			} catch (NumberFormatException nfe) {
				ok = false;
			}
		}
		try {
			v = new Integer(genPanel.heightField.getText()).intValue();
			if (v < 148 || v > 211) {
				ok = false;
			}
		} catch (NumberFormatException nfe) {
			ok = false;
		}
		try {
			v = new Integer(genPanel.weightField.getText()).intValue();
			if (v < 1 || v > 127) {
				ok = false;
			}
		} catch (NumberFormatException nfe) {
			ok = false;
		}
		try {
			v = new Integer(genPanel.ageField.getText()).intValue();
			if (v < 15 || v > 46) {
				ok = false;
			}
		} catch (NumberFormatException nfe) {
			ok = false;
		}
		return ok;
	}

	private void updateStats() {
		for (int i = 0; i < Stats.ROLES.length; i++) {
			if (i != 1) {
				Stats.setValue(of, index, Stats.ROLES[i],
						boToInt(posPanel.checkBox[i].isSelected()));
			}
		}
		int v = 0;
		for (int i = 0; i < Stats.ROLES.length; i++) {
			if (((String) (posPanel.regBox.getSelectedItem()))
					.equals(Stats.ROLES[i].getName())) {
				v = i;
			}
		}
		Stats.setValue(of, index, Stats.REG_POS, v);

		Stats.setValue(of, index, Stats.HEIGHT, genPanel.heightField.getText());

		int item = genPanel.footBox.getSelectedIndex();
		int foot = item / 3;
		int side = item - (foot * 3);
		Stats.setValue(of, index, Stats.FOOT, foot);
		Stats.setValue(of, index, Stats.FAVORITE_SIDE, side);
		Stats.setValue(of, index, Stats.WEAK_FOOT_ACC, (String) (genPanel.wfaBox
				.getSelectedItem()));
		Stats.setValue(of, index, Stats.WEAK_FOOT_FREQ, (String) (genPanel.wffBox
				.getSelectedItem()));

		for (int i = 0; i < Stats.ABILITY99.length; i++) {
			Stats.setValue(of, index, Stats.ABILITY99[i], abiPanel.getField(i).getText());
		}

		Stats.setValue(of, index, Stats.CONSISTENCY, (String) (genPanel.consBox
				.getSelectedItem()));
		Stats.setValue(of, index, Stats.CONDITION, (String) (genPanel.condBox
				.getSelectedItem()));

		for (int i = 0; i < Stats.ABILITY_SPECIAL.length; i++) {
			Stats.setValue(of, index, Stats.ABILITY_SPECIAL[i],
					boToInt(spePanel.checkBox[i].isSelected()));
		}

		Stats.setValue(of, index, Stats.INJURY, (String) (genPanel.injuryBox
				.getSelectedItem()));
		Stats.setValue(of, index, Stats.FREE_KICK, (String) (genPanel.fkBox
				.getSelectedItem()));
		Stats.setValue(of, index, Stats.PK_STYLE, (String) (genPanel.pkBox
				.getSelectedItem()));
		Stats.setValue(of, index, Stats.AGE, genPanel.ageField.getText());
		Stats.setValue(of, index, Stats.WEIGHT, genPanel.weightField.getText());
		Stats.setValue(of, index, Stats.NATIONALITY,
				(String) (genPanel.nationBox.getSelectedItem()));
		Stats.setValue(of, index, Stats.DRIBBLE_STYLE, (String) (genPanel.dribBox
				.getSelectedItem()));
		Stats.setValue(of, index, Stats.DK_STYLE, (String) (genPanel.dkBox
				.getSelectedItem()));

		Stats.setValue(of, index, Stats.ABILITY_EDITED, 1);
	}

	private int boToInt(boolean b) {
		int i = 0;
		if (b) {
			i = 1;
		}
		return i;
	}

}
