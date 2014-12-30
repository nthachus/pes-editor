package editor.ui;

import editor.Clubs;
import editor.Formations;
import editor.Player;
import editor.Squads;
import editor.data.OptionFile;
import editor.util.Bits;

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
		//setFont(new Font("Dialog", Font.BOLD, 12));

		setPreferredSize(new Dimension(16, 576));
	}

	private static final int LAST_NATION_TEAM = Squads.NATION_COUNT + Squads.CLASSIC_COUNT
			+ (Player.TOTAL_EDIT - Formations.CLUB_TEAM_SIZE) / Formations.NATION_TEAM_SIZE;

	// Last editable national players slot (23 - 9)
	private static final int LAST_EDIT_TEAM_SIZE
			= (Player.TOTAL_EDIT - Formations.CLUB_TEAM_SIZE) % Formations.NATION_TEAM_SIZE;

	public void refresh(int team) {
		if (team < 0 || team >= LAST_NATION_TEAM + 2 + Clubs.TOTAL)
			throw new IndexOutOfBoundsException("team");

		int size, firstAdr, ft = team;
		if (team < LAST_NATION_TEAM) {
			size = Formations.NATION_TEAM_SIZE;
			firstAdr = Squads.NATION_NUM_ADR + team * size;
		} else if (team == LAST_NATION_TEAM) {
			size = LAST_EDIT_TEAM_SIZE;
			firstAdr = Squads.NATION_NUM_ADR + team * Formations.NATION_TEAM_SIZE;
		} else {
			size = Formations.CLUB_TEAM_SIZE;
			firstAdr = Squads.CLUB_NUM_ADR + (team - (LAST_NATION_TEAM + 2)) * size;
			ft -= 8;
		}

		String[] numList = buildNumberList(team, size, firstAdr, ft);
		setListData(numList);
	}

	private String[] buildNumberList(int team, int size, int firstAdr, int ft) {
		String[] numList = new String[size];
		for (int p = 0; p < size; p++) {

			int adr;
			if (team < Squads.NATION_COUNT + Squads.CLASSIC_COUNT || team >= LAST_NATION_TEAM + 2) {
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
