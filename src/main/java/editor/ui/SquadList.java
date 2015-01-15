package editor.ui;

import editor.data.*;
import editor.util.Bits;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Vector;

public class SquadList extends JList<Player> {
	private static final long serialVersionUID = 6246402135860985745L;

	private final OptionFile of;
	private volatile int team;

	public SquadList(OptionFile of, boolean setSize) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		//refresh(team);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(Formations.CLUB_TEAM_SIZE);
		//setFont(new Font(Font.DIALOG, Font.BOLD, 12));

		if (setSize) {
			setPreferredSize(new Dimension(118, 576));
		}
	}

	public int getTeam() {
		return team;
	}

	public void refresh(int team, boolean normal) {
		if (team < 0 || team > Squads.TOTAL)
			throw new IndexOutOfBoundsException("team#" + team);

		if (!normal) {
			if (team >= Squads.FIRST_EDIT_NATION)
				team += Squads.EDIT_TEAM_COUNT;
		}

		this.team = team;
		if (team >= Squads.TOTAL) {
			fetchAllPlayers();
		} else {
			fetchPlayers(team);
		}
	}

	private void fetchAllPlayers() {
		Vector<Player> model = new Vector<Player>();
		for (int p = 1; p < Player.TOTAL; p++) {
			model.addElement(new Player(of, p));
		}
		for (int p = 0; p < Player.TOTAL_EDIT; p++) {
			model.addElement(new Player(of, Player.FIRST_EDIT + p));
		}

		Collections.sort(model);

		model.trimToSize();
		setListData(model);
	}

	/**
	 * @see SquadNumberList#refresh(int)
	 */
	private void fetchPlayers(int team) {
		int size, firstAdr, ft = team;
		if (team < Squads.LAST_EDIT_NATION) {
			size = Formations.NATION_TEAM_SIZE;
			firstAdr = Squads.NATION_ADR + team * size * 2;
		} else if (team == Squads.LAST_EDIT_NATION) {
			size = Squads.LAST_EDIT_NATION_SIZE;
			firstAdr = Squads.NATION_ADR + team * Formations.NATION_TEAM_SIZE * 2;
		} else {
			size = Formations.CLUB_TEAM_SIZE;
			firstAdr = Squads.CLUB_ADR + (team - Squads.FIRST_CLUB) * size * 2;
			ft -= Squads.EDIT_TEAM_COUNT;
		}

		Player[] players = new Player[size];
		for (int p = 0; p < players.length; p++) {
			int adr;
			if (team < Squads.NATION_COUNT + Squads.CLASSIC_COUNT
					|| (team >= Squads.FIRST_CLUB && team < Squads.FIRST_CLUB + Clubs.TOTAL)) {
				adr = firstAdr + Formations.getSlot(of, ft, p) * 2;
			} else {
				adr = firstAdr + p * 2;
			}

			int pId = Bits.toInt16(of.getData(), adr);
			players[p] = new Player(of, pId, adr);
		}

		setListData(players);
	}

}
