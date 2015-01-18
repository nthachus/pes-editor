package editor.ui;

import editor.data.*;
import editor.lang.NullArgumentException;
import editor.util.Bits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collections;
import java.util.Vector;

public class NationalityList extends JList/*<Player>*/ {
	private static final long serialVersionUID = -4852231786111601408L;
	private static final Logger log = LoggerFactory.getLogger(NationalityList.class);

	private final OptionFile of;

	public NationalityList(OptionFile of) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(Formations.CLUB_TEAM_SIZE);
	}

	@SuppressWarnings("unchecked")
	public void refresh(int nation, boolean alphaOrder) {
		int extraCount = SelectByNation.getExtraNations().length;
		int total = Stats.NATION.length + extraCount;
		if (nation < 0 || nation >= total) {
			throw new IndexOutOfBoundsException("nation#" + nation);
		}
		// DEBUG
		log.debug("Reload Nationality list #{} for country: {}, sort: {}", hashCode(), nation, alphaOrder);

		Vector<Player> model;
		if (nation < Stats.NATION.length) {
			model = getNationPlayers(nation);
		} else if (extraCount >= 5 && nation == total - 5) {
			model = getOldPlayers();
		} else if (extraCount >= 4 && nation == total - 4) {
			model = getYoungPlayers();
		} else if (extraCount >= 3 && nation == total - 3) {
			model = getDuplicatedPlayers();
			alphaOrder = false;
		} else if (extraCount >= 2 && nation == total - 2) {
			model = getFreeAgents();
		} else/* if (extraCount >= 1 && nation == total - 1)*/ {
			model = getAllPlayers();
		}

		if (alphaOrder) {
			Collections.sort(model);
		}

		setListData(model);
		// DEBUG
		log.debug("Loaded {} players for Nationality list #{}", model.size(), hashCode());
	}

	private Vector<Player> getOldPlayers() {
		Vector<Player> model = new Vector<Player>(Player.TOTAL_OLD_PLAYERS);
		for (int i = 0; i < Player.TOTAL_OLD_PLAYERS; i++) {
			model.add(new Player(of, Player.FIRST_OLD + i));
		}
		return model;
	}

	private Vector<Player> getYoungPlayers() {
		Vector<Player> model = new Vector<Player>(Player.TOTAL_YOUNG_PLAYERS);
		for (int i = 0; i < Player.TOTAL_YOUNG_PLAYERS; i++) {
			model.add(new Player(of, Player.FIRST_YOUNG + i));
		}
		return model;
	}

	private Vector<Player> getFreeAgents() {
		Vector<Player> model = new Vector<Player>((Player.FIRST_CLASSIC + Player.FIRST_ML - Player.FIRST_CLUB) / 2);
		for (int p = 1; p < Player.FIRST_CLASSIC; p++) {
			if (isFreeAgent(p)) {
				model.add(new Player(of, p));
			}
		}

		for (int p = Player.FIRST_CLUB; p < Player.FIRST_ML; p++) {
			if (isFreeAgent(p)) {
				model.add(new Player(of, p));
			}
		}
		return model;
	}

	private boolean isFreeAgent(int player) {
		int endAdr = Squads.CLUB_ADR + Clubs.TOTAL * Formations.CLUB_TEAM_SIZE * 2;
		for (int adr = Squads.CLUB_ADR; adr < endAdr; adr += 2) {
			if (Bits.toInt16(of.getData(), adr) == player) {
				return false;
			}
		}
		return true;
	}

	private Vector<Player> getAllPlayers() {
		Vector<Player> model = new Vector<Player>(Player.TOTAL + Player.TOTAL_EDIT - 1);
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
		return model;
	}

	private Vector<Player> getNationPlayers(int nation) {
		Vector<Player> model = new Vector<Player>(Player.TOTAL / 4);
		for (int p = 1; p < Player.TOTAL; p++) {
			if (Stats.getValue(of, p, Stats.NATIONALITY) == nation) {
				model.add(new Player(of, p));
			}
		}

		for (int p = Player.FIRST_EDIT; p < Player.END_EDIT; p++) {
			if (Stats.getValue(of, p, Stats.NATIONALITY) == nation) {
				model.add(new Player(of, p));
			}
		}
		return model;
	}

	private Vector<Player> getDuplicatedPlayers() {
		Vector<Player> model = new Vector<Player>(Player.TOTAL_CLUB_PLAYERS / 2);
		for (int p = Player.FIRST_CLUB; p < Player.FIRST_JAPAN; p++) {
			int dupe = getDupe(p);
			if (dupe >= 0) {
				model.add(new Player(of, p));
				model.add(new Player(of, dupe));
			}
		}
		return model;
	}

	private int getDupe(int player) {
		for (int i = 1; i < Player.FIRST_CLASSIC; i++) {
			if (Stats.getValue(of, player, Stats.NATIONALITY) != Stats.getValue(of, i, Stats.NATIONALITY)) {
				continue;
			}

			int score = 0;
			if (Stats.getValue(of, player, Stats.AGE) == Stats.getValue(of, i, Stats.AGE)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.HEIGHT) == Stats.getValue(of, i, Stats.HEIGHT)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.WEIGHT) == Stats.getValue(of, i, Stats.WEIGHT)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.FOOT) == Stats.getValue(of, i, Stats.FOOT)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.FAVORITE_SIDE) == Stats.getValue(of, i, Stats.FAVORITE_SIDE)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.REG_POS) == Stats.getValue(of, i, Stats.REG_POS)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.ATTACK) == Stats.getValue(of, i, Stats.ATTACK)) {
				score++;
			}
			if (Stats.getValue(of, player, Stats.ACCELERATION) == Stats.getValue(of, i, Stats.ACCELERATION)) {
				score++;
			}

			if (score >= 7) {
				return i;
			}
		}
		return -1;
	}

}
