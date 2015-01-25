package editor.ui;

import editor.data.Clubs;
import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.lang.NullArgumentException;
import editor.util.Bits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class SquadNumberList extends JList/*<String>*/ {
	private static final long serialVersionUID = -3655153993225424165L;
	private static final Logger log = LoggerFactory.getLogger(SquadNumberList.class);

	private final OptionFile of;

	public SquadNumberList(OptionFile of) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Squad-number list #{} is initializing..", hashCode());
		initComponents();
	}

	private void initComponents() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(1);
		//setFont(new Font(UIUtil.DIALOG, Font.BOLD, 12));

		setPreferredSize(new Dimension(18, 576));//16
	}

	/**
	 * @see SquadList#fetchPlayers(int)
	 */
	public void refresh(int team) {
		if (team < 0 || team >= Squads.TOTAL) {
			throw new IndexOutOfBoundsException("team#" + team);
		}
		log.info("Try to refresh Squad-number list for team: {}", team);

		int size, ft = team;
		int firstAdr = Squads.getNumOffset(team);
		if (team < Squads.LAST_EDIT_NATION) {
			size = Formations.NATION_TEAM_SIZE;
		} else if (team == Squads.LAST_EDIT_NATION) {
			size = Squads.LAST_EDIT_NATION_SIZE;
		} else {
			size = Formations.CLUB_TEAM_SIZE;
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

		log.debug("Squad-number list #{} was built with {} items, for team {} (formation-team: {})",
				hashCode(), size, team, ft);
		return numList;
	}

}
