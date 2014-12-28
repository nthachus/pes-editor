package editor;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Bits;

import javax.swing.*;
import java.util.Collections;
import java.util.Vector;

public class NationalityList extends JList<Player> {
	private final OptionFile of;

	public NationalityList(OptionFile opf) {
		super();
		of = opf;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(32);
	}

	public void refresh(int nation, boolean alpha) {
		int a;
		int index;
		Vector<Player> model = new Vector<Player>();
		if (nation == Stats.NATION.length + 4) {
			for (int p = 1; p < Player.TOTAL; p++) {
				model.addElement(new Player(of, p, 0));
			}
			for (int p = Player.FIRST_EDIT; p < 32768; p++) {
				model.addElement(new Player(of, p, 0));
			}
		} else if (nation == Stats.NATION.length + 3) {
			boolean free;

			for (int p = 1; p < Player.firstClassic; p++) {
				free = true;
				a = Squads.CLUB_ADR - 2;
				do {
					a = a + 2;
					index = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
					if (index == p) {
						free = false;
					}
				} while (a < Squads.CLUB_ADR + (Clubs.TOTAL * 64) - 2 + 1
						&& index != p);
				if (free) {
					model.addElement(new Player(of, p, 0));
				}
			}

			for (int p = Player.firstClub; p < Player.firstML; p++) {
				free = true;
				a = Squads.CLUB_ADR - 2;
				do {
					a = a + 2;
					index = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
					if (index == p) {
						free = false;
					}
				} while (a < Squads.CLUB_ADR + (Clubs.TOTAL * 64) - 2
						&& index != p);
				if (free) {
					model.addElement(new Player(of, p, 0));
				}
			}
		} else if (nation == Stats.NATION.length + 2) {
			for (int p = Player.firstClub; p < Player.firstPESUnited; p++) {
				int dupe = getDupe(p);
				if (dupe != -1) {
					model.addElement(new Player(of, p, 0));
					model.addElement(new Player(of, dupe, 0));
				}
			}
		} else if (nation == Stats.NATION.length + 1) {
			for (int p = Player.firstYoung; p < Player.firstOld; p++) {
				model.addElement(new Player(of, p, 0));
			}
		} else if (nation == Stats.NATION.length) {
			for (int p = Player.firstOld; p < Player.FIRST_UNUSED; p++) {
				model.addElement(new Player(of, p, 0));
			}
		} else {
			for (int p = 1; p < Player.TOTAL; p++) {
				if (Stats.getValue(of, p, Stats.NATIONALITY) == nation) {
					model.addElement(new Player(of, p, 0));
				}
			}
			for (int p = Player.FIRST_EDIT; p < 32952; p++) {
				if (Stats.getValue(of, p, Stats.NATIONALITY) == nation) {
					model.addElement(new Player(of, p, 0));
				}
			}
		}
		if (nation != Stats.NATION.length + 2 && alpha) {
			Collections.sort(model);
		}
		model.trimToSize();
		setListData(model);
	}

	private int getDupe(int p) {
		for (int i = 1; i < Player.firstClassic; i++) {
			boolean isDupe = true;
			if (Stats.getValue(of, p, Stats.NATIONALITY) != Stats.getValue(of,
					i, Stats.NATIONALITY)) {
				isDupe = false;
			} else {
				int score = 0;
				if (Stats.getValue(of, p, Stats.AGE) == Stats.getValue(of, i,
						Stats.AGE)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.HEIGHT) == Stats.getValue(of,
						i, Stats.HEIGHT)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.WEIGHT) == Stats.getValue(of,
						i, Stats.WEIGHT)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.FOOT) == Stats.getValue(of, i,
						Stats.FOOT)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.FAVORITE_SIDE) == Stats.getValue(of,
						i, Stats.FAVORITE_SIDE)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.REG_POS) == Stats.getValue(of,
						i, Stats.REG_POS)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.ATTACK) == Stats.getValue(of,
						i, Stats.ATTACK)) {
					score++;
				}
				if (Stats.getValue(of, p, Stats.ACCELERATION) == Stats.getValue(of, i,
						Stats.ACCELERATION)) {
					score++;
				}
				if (score < 7) {
					isDupe = false;
				}
			}
			if (isDupe) {
				return i;
			}
		}
		return -1;
	}

}
