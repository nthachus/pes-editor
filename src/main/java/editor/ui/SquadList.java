package editor.ui;

import editor.data.*;
import editor.lang.NullArgumentException;
import editor.util.Bits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SquadList extends JList/*<Player>*/ {
	private static final long serialVersionUID = 6246402135860985745L;
	private static final Logger log = LoggerFactory.getLogger(SquadList.class);

	private final OptionFile of;
	private volatile int team;

	public SquadList(OptionFile of, boolean setSize) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		//refresh(team);

		log.debug("Initialize Squad list #{} with fixed-size: {}", hashCode(), setSize);
		initComponents(setSize);
	}

	private void initComponents(boolean setSize) {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(Formations.CLUB_TEAM_SIZE);
		//setFont(new Font(UIUtil.DIALOG, Font.BOLD, 12));

		if (setSize) {
			setPreferredSize(new Dimension(118, 576));
		}
	}

	public int getTeam() {
		return team;
	}

	public void refresh(int team, boolean isNormal) {
		if (team < 0 || team > Squads.TOTAL) {
			throw new IndexOutOfBoundsException("team#" + team);
		}
		log.info("Try to reload Squad list #{} for team: {}, normal-mode: {}", hashCode(), team, isNormal);

		if (!isNormal
				&& team >= Squads.FIRST_EDIT_NATION) {
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
		java.util.List<Player> model = new ArrayList<Player>(Player.TOTAL + Player.TOTAL_EDIT - 1);
		for (int p = 1; p < Player.TOTAL; p++) {
			model.add(new Player(of, p));
		}

		Player o;
		for (int p = Player.FIRST_EDIT; p < Player.END_EDIT; p++) {
			o = new Player(of, p);
			if (!o.isEmpty()) {
				model.add(o);
			}
		}

		Player[] listData = model.toArray(new Player[model.size()]);
		Arrays.sort(listData);

		setListData(listData);
		// DEBUG
		log.debug("Squad list #{} is reloaded with all ({}) players", hashCode(), listData.length);
	}

	/**
	 * @see SquadNumberList#refresh(int)
	 */
	private void fetchPlayers(int team) {
		int size, ft = team;
		int firstAdr = Squads.getOffset(team);
		if (team < Squads.LAST_EDIT_NATION) {
			size = Formations.NATION_TEAM_SIZE;
		} else if (team == Squads.LAST_EDIT_NATION) {
			size = Squads.LAST_EDIT_NATION_SIZE;
		} else {
			size = Formations.CLUB_TEAM_SIZE;
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
			if (Player.isInvalidId(pId)) {
				pId = 0;
				Bits.toBytes((short) 0, of.getData(), adr);
			}
			players[p] = new Player(of, pId, adr);
		}

		setListData(players);
		// DEBUG
		log.debug("Squad list #{} was reloaded for team: {}", hashCode(), team);
	}

}
