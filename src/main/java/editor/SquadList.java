package editor;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.util.Bits;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Vector;

public class SquadList extends JList<Player> {
	private final OptionFile of;
	private volatile int team;

	public SquadList(OptionFile of, boolean setSize) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		//refresh(team);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(32);
		//setFont(new Font("Dialog", Font.BOLD, 12));

		if (setSize) {
			setPreferredSize(new Dimension(118, 576));
		}
	}

	public int getTeam() {
		return team;
	}

	public void refresh(int team, boolean normal) {
		this.team = team;
		if (!normal) {
			if (this.team > 66)
				this.team += 8;
		}

		if (this.team == 212) {
			Vector<Player> model = new Vector<Player>();
			for (int p = 1; p < Player.TOTAL; p++) {
				model.addElement(new Player(of, p, 0));
			}
			for (int p = 0; p < Player.TOTAL_EDIT; p++) {
				model.addElement(new Player(of, Player.FIRST_EDIT + p, 0));
			}

			Collections.sort(model);
			model.trimToSize();
			setListData(model);
		} else {
			Player[] players;
			int size;
			int firstAdr;
			int ft;
			int a;
			if (this.team < 73) {
				size = 23;
				firstAdr = Squads.NATION_ADR + (this.team * size * 2);
				ft = this.team;
			} else if (this.team == 73) {
				size = 14;
				firstAdr = Squads.NATION_ADR + (this.team * 23 * 2);
				ft = this.team;
			} else {
				size = 32;
				firstAdr = Squads.CLUB_ADR + ((this.team - 75) * size * 2);
				ft = this.team - 8;
			}

			players = new Player[size];
			for (int p = 0; p < size; p++) {
				a = firstAdr + (p * 2);
				if ((this.team >= 0 && this.team < 67) || (this.team >= 75 && this.team < 205)) {
					// a = 670512 + (628 * ft) + 6232 + p;
					a = firstAdr + (Formations.getSlot(of, ft, p) * 2);
				}
				players[p] = new Player(of, (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]), a);
			}
			setListData(players);
		}
	}

}
