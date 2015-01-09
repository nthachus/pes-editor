package editor.ui;

import editor.data.Clubs;
import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.util.Bits;
import editor.util.swing.JList;

import javax.swing.*;
import java.awt.*;

public class SquadNumberList extends JList<String> {
	private final OptionFile of;

	public SquadNumberList(OptionFile of) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(1);
		//setFont(new Font(Font.DIALOG, Font.BOLD, 12));

		setPreferredSize(new Dimension(16, 576));
	}

	/**
	 * @see SquadList#fetchPlayers(int)
	 */
	public void refresh(int team) {
		if (team < 0 || team >= Squads.TOTAL)
			throw new IndexOutOfBoundsException("team#" + team);

		int size, firstAdr, ft = team;
		if (team < Squads.LAST_EDIT_NATION) {
			size = Formations.NATION_TEAM_SIZE;
			firstAdr = Squads.NATION_NUM_ADR + team * size;
		} else if (team == Squads.LAST_EDIT_NATION) {
			size = Squads.LAST_EDIT_NATION_SIZE;
			firstAdr = Squads.NATION_NUM_ADR + team * Formations.NATION_TEAM_SIZE;
		} else {
			size = Formations.CLUB_TEAM_SIZE;
			firstAdr = Squads.CLUB_NUM_ADR + (team - Squads.FIRST_CLUB) * size;
			ft -= Squads.EDIT_TEAM_COUNT;
		}

		String[] numList = buildNumberList(team, size, firstAdr, ft);
		setListData(numList);
	}

	private String[] buildNumberList(int team, int size, int firstAdr, int ft) {
		String[] numList = new String[size];
		for (int p = 0; p < size; p++) {

			int adr;
			if (team < Squads.NATION_COUNT + Squads.CLASSIC_COUNT
					|| (team >= Squads.FIRST_CLUB && team < Squads.FIRST_CLUB + Clubs.TOTAL)) {
				adr = firstAdr + Formations.getSlot(of, ft, p);
			} else {
				adr = firstAdr + p;
			}

			int num = Bits.toInt(of.getData()[adr]) + 1;
			if (num > 0xFF) {
				numList[p] = "...";
			} else {
				numList[p] = Integer.toString(num);
			}
		}

		return numList;
	}

}
