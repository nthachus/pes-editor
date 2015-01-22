package editor.data;

import editor.lang.NullArgumentException;
import editor.lang.SimpleEntry;
import editor.util.Bits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Squads {
	private static final Logger log = LoggerFactory.getLogger(Squads.class);

	private Squads() {
	}

	public static final int NATION_COUNT = 60;
	public static final int CLASSIC_COUNT = 7;

	public static final int NATION_NUM_ADR = OptionFile.blockAddress(5);// TODO: private all _ADR fields
	private static final int FIRST_CLUB_SLOT = (NATION_COUNT + CLASSIC_COUNT) * Formations.NATION_TEAM_SIZE
			+ Player.TOTAL_EDIT;// 7*23 + 32
	private static final int TOTAL_SLOTS = FIRST_CLUB_SLOT + (Clubs.TOTAL + 1) * Formations.CLUB_TEAM_SIZE
			+ Player.TOTAL_SHOP;// (Clubs.TOTAL + 2) * 32
	public static final int NATION_ADR = NATION_NUM_ADR + TOTAL_SLOTS + 97;// 56

	public static final int CLUB_NUM_ADR = NATION_NUM_ADR + FIRST_CLUB_SLOT;
	public static final int CLUB_ADR = NATION_ADR + FIRST_CLUB_SLOT * 2;
	public static final int END_ADR = NATION_ADR + TOTAL_SLOTS * 2;

	public static final int EDIT_TEAM_COUNT
			= (Player.TOTAL_EDIT - Formations.CLUB_TEAM_SIZE + Formations.NATION_TEAM_SIZE - 1)
			/ Formations.NATION_TEAM_SIZE + 1;
	public static final int SHOP_TEAM_COUNT
			= (Player.TOTAL_SHOP + Formations.CLUB_TEAM_SIZE - 1) / Formations.CLUB_TEAM_SIZE;
	public static final int EXTRA_COUNT = CLASSIC_COUNT + EDIT_TEAM_COUNT;

	public static final int LAST_CLUB = NATION_COUNT + EXTRA_COUNT + Clubs.TOTAL;
	public static final int TOTAL = LAST_CLUB + 2 + SHOP_TEAM_COUNT;

	public static final String[] EXTRAS = {
			"Classic England",
			"Classic France",
			"Classic Germany",
			"Classic Italy",
			"Classic Netherlands",
			"Classic Argentina",
			"Classic Brazil",
			"<Edited> National 1",
			"<Edited> National 2",
			"<Edited> National 3",
			"<Edited> National 4",
			"<Edited> National 5",
			"<Edited> National 6",
			"<Edited> National 7",
			"<Edited>",
			"<Japan Plus>",
			"<ML Default>",
			"<Shop 1>",
			"<Shop 2>",
			"<Shop 3>",
			"<Shop 4>",
			"<Shop 5>"
	};

	public static final int FIRST_EDIT_NATION = NATION_COUNT + CLASSIC_COUNT;
	public static final int FIRST_CLUB = FIRST_EDIT_NATION + EDIT_TEAM_COUNT;

	public static final int EDIT_CLUB = FIRST_CLUB - 1;
	public static final int LAST_EDIT_NATION = EDIT_CLUB - 1;
	/**
	 * Last editable national players slot (23 - 9).
	 */
	public static final int LAST_EDIT_NATION_SIZE
			= (Player.TOTAL_EDIT - Formations.CLUB_TEAM_SIZE) % Formations.NATION_TEAM_SIZE;


	private static void ensureValidTeam(int team) {
		if (team < 0 || team >= TOTAL) {
			throw new IndexOutOfBoundsException("team#" + team);
		}
	}

	public static int getTeamSize(int team) {
		return (team < EDIT_CLUB) ? Formations.NATION_TEAM_SIZE : Formations.CLUB_TEAM_SIZE;
	}

	public static int getOffset(int team) {
		if (team < EDIT_CLUB) {
			return NATION_ADR + team * Formations.NATION_TEAM_SIZE * 2;
		}
		return CLUB_ADR + (team - FIRST_CLUB) * Formations.CLUB_TEAM_SIZE * 2;
	}

	public static int getNumOffset(int team) {
		if (team < EDIT_CLUB) {
			return NATION_NUM_ADR + team * Formations.NATION_TEAM_SIZE;
		}
		return CLUB_NUM_ADR + (team - FIRST_CLUB) * Formations.CLUB_TEAM_SIZE;
	}

	private static void ensureValidSlot(int team, int slot) {
		ensureValidTeam(team);
		if (slot < 0 || slot >= getTeamSize(team)) {
			throw new IndexOutOfBoundsException("slot#" + slot);
		}
	}

	public static int getTeamPlayer(OptionFile of, int team, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidSlot(team, index);

		int adr = getOffset(team) + index * 2;
		return Bits.toInt16(of.getData(), adr);
	}

	public static void setTeamPlayer(OptionFile of, int team, int index, int player) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidSlot(team, index);

		int adr = getOffset(team) + index * 2;
		Bits.toBytes((short) player, of.getData(), adr);
	}

	public static int getTeamSquadNum(OptionFile of, int team, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidSlot(team, index);

		int adr = getNumOffset(team) + index;
		return Bits.toInt(of.getData()[adr]) + 1;// TODO: if squad-number > 0xFF -> return -1
	}

	public static void setTeamSquadNum(OptionFile of, int team, int index, int squadNumber) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidSlot(team, index);

		int adr = getNumOffset(team) + index;
		of.getData()[adr] = Bits.toByte(squadNumber - 1);
	}

	public static int countPlayers(OptionFile of, int team) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidTeam(team);

		int count = 0;
		for (int p = 0, size = getTeamSize(team); p < size; p++) {
			int id = getTeamPlayer(of, team, p);
			if (id > 0) {
				count++;
			}
		}

		log.debug("Counting result: {} players in team: {}", count, team);
		return count;
	}

	public static int getNextNumber(OptionFile of, int team) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidTeam(team);

		int size = getTeamSize(team);
		for (int i = 1; i <= 99; i++) {

			boolean spare = true;
			for (int p = 0; p < size; p++) {
				int num = getTeamSquadNum(of, team, p);
				if (num == i) {
					spare = false;
					break;
				}
			}

			if (spare) {
				log.debug("Found first unused number: {} in team: {}", i, team);
				return i;
			}
		}

		return 256;
	}

	public static boolean inNationTeam(OptionFile of, int playerId) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		if (playerId > 0) {
			for (int t = 0; t < FIRST_EDIT_NATION; t++) {
				if (inTeam(of, t, playerId)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean inTeam(OptionFile of, int team, int playerId) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidTeam(team);

		if (playerId > 0) {
			for (int p = 0, size = getTeamSize(team); p < size; p++) {
				int id = getTeamPlayer(of, team, p);
				if (id == playerId) {
					return true;
				}
			}
		}
		return false;
	}

	//region From/To Classic Team

	public static int getClassicNation(String nation) {
		for (int i = 0; i < CLASSIC_COUNT; i++) {
			if (EXTRAS[i].substring(8).equalsIgnoreCase(nation)) {
				return NATION_COUNT + i;
			}
		}
		return -1;
	}

	public static int getClassicNation(int nation) {
		if (nation < 0 || nation >= Stats.NATION.length) {
			throw new ArrayIndexOutOfBoundsException("nation#" + nation);
		}

		int cNat = getClassicNation(Stats.NATION[nation]);
		return (cNat < 0) ? nation : cNat;
	}

	public static int getNationForTeam(int team) {
		int idx = team - NATION_COUNT;
		if (idx >= 0 && idx < CLASSIC_COUNT) {

			String cCountry = EXTRAS[idx].substring(8);
			for (int i = 0; i < NATION_COUNT; i++) {
				if (cCountry.equalsIgnoreCase(Stats.NATION[i])) {
					return i;
				}
			}
		}
		return team;
	}

	//endregion

	private static boolean isFixedTeam(int squad) {
		return ((squad >= FIRST_EDIT_NATION && squad < FIRST_CLUB) || squad >= FIRST_CLUB + Clubs.TOTAL);
	}

	public static void fixFormation(OptionFile of, int squad, boolean fixJobs) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidTeam(squad);
		if (!fixJobs) {
			log.debug("Try to fix formation for team: {}", squad);
		}

		if (isFixedTeam(squad)) {
			return;
		}

		int formationTeam = squad;
		if (squad >= FIRST_CLUB) {
			formationTeam -= EDIT_TEAM_COUNT;
		}

		int size = getTeamSize(squad);
		int firstAdr = getOffset(squad);
		int firstNumAdr = getNumOffset(squad);

		byte[] temp = new byte[size * 2];
		byte[] tempNum = new byte[size];
		System.arraycopy(of.getData(), firstAdr, temp, 0, temp.length);
		System.arraycopy(of.getData(), firstNumAdr, tempNum, 0, tempNum.length);

		for (int p = 0; p < size; p++) {
			int fSlot = Formations.getSlot(of, formationTeam, p);
			System.arraycopy(temp, fSlot * 2, of.getData(), firstAdr + p * 2, 2);
			System.arraycopy(tempNum, fSlot, of.getData(), firstNumAdr + p, 1);
		}

		if (fixJobs) {
			for (int j = 0; j < Formations.JOBS_COUNT; j++) {
				for (int i = 0; i < size; i++) {
					if (Formations.getSlot(of, formationTeam, i) == Formations.getJob(of, formationTeam, j)) {
						Formations.setJob(of, formationTeam, j, i);
						break;
					}
				}
			}
		}

		for (int i = 0; i < size; i++) {
			Formations.setSlot(of, formationTeam, i, i);
		}

		if (!fixJobs) {
			log.debug("Fixing formation for team {} succeeded", squad);
		}
	}

	public static void fixAll(OptionFile of) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		log.debug("Try to fix formation for all {} teams", TOTAL);

		for (int s = 0; s < TOTAL; s++) {
			fixFormation(of, s, true);
		}

		log.debug("Fixing formation for all teams succeeded");
	}

	public static void tidy(OptionFile of, int team) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidTeam(team);
		log.debug("Try to tidy team: {}", team);

		if (isFixedTeam(team)) {
			return;
		}

		//fixFormation(of, team);

		int size = getTeamSize(team);
		int firstAdr = getOffset(team);
		int firstNumAdr = getNumOffset(team);

		byte[] tempSlot = new byte[(size - Formations.PLAYER_COUNT) * 2];
		byte[] tempNum = new byte[size - Formations.PLAYER_COUNT];
		int tempPos = 0;
		for (int i = Formations.PLAYER_COUNT; i < size; i++) {

			int slotAdr = firstAdr + i * 2;
			int numAdr = firstNumAdr + i;
			if (of.getData()[slotAdr] != 0 || of.getData()[slotAdr + 1] != 0) {

				tempSlot[tempPos * 2] = of.getData()[slotAdr];
				tempSlot[(tempPos * 2) + 1] = of.getData()[slotAdr + 1];
				tempNum[tempPos] = of.getData()[numAdr];

				tempPos++;
			}
		}

		Arrays.fill(tempNum, tempPos, tempNum.length, (byte) 0xFF);

		System.arraycopy(tempSlot, 0, of.getData(), firstAdr + Formations.PLAYER_COUNT * 2, tempSlot.length);
		System.arraycopy(tempNum, 0, of.getData(), firstNumAdr + Formations.PLAYER_COUNT, tempNum.length);

		log.debug("Tidy of team {} succeeded", team);
	}

	private static int getRolePos(Stat role) {
		if (role == Stats.GK) {
			return 0;
		} else if (role == Stats.CBT || role == Stats.CWP) {
			return 1;
		} else if (role == Stats.SB || role == Stats.WB) {
			return 2;
		} else if (role == Stats.DM) {
			return 3;
		} else if (role == Stats.CM) {
			return 4;
		} else if (role == Stats.SM) {
			return 5;
		} else if (role == Stats.AM || role == Stats.SS) {
			return 6;
		} else if (role == Stats.WG) {
			return 8;
		} else if (role == Stats.CF) {
			return 7;
		}
		return 0;
	}

	private static List<Map.Entry<Integer, Integer>> getPlayerScores(OptionFile of, int size, int pos, int offset) {
		List<Map.Entry<Integer, Integer>> list =
				new ArrayList<Map.Entry<Integer, Integer>>(size - Formations.PLAYER_COUNT);

		for (int i = Formations.PLAYER_COUNT; i < size; i++) {
			int adr = offset + i * 2;
			int playerIdx = Bits.toInt16(of.getData(), adr);
			if (playerIdx == 0) {
				break;
			}

			int score;
			switch (pos) {
				case 0:
					score = Stats.getValue(of, playerIdx, Stats.DEFENCE)
							+ Stats.getValue(of, playerIdx, Stats.BALANCE)
							+ Stats.getValue(of, playerIdx, Stats.RESPONSE)
							+ Stats.getValue(of, playerIdx, Stats.GK_ABILITY)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 1:
					score = Stats.getValue(of, playerIdx, Stats.DEFENCE)
							+ Stats.getValue(of, playerIdx, Stats.BALANCE)
							+ Stats.getValue(of, playerIdx, Stats.RESPONSE)
							+ Stats.getValue(of, playerIdx, Stats.SPEED)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 2:
					score = Stats.getValue(of, playerIdx, Stats.DEFENCE)
							+ Stats.getValue(of, playerIdx, Stats.BALANCE)
							+ Stats.getValue(of, playerIdx, Stats.RESPONSE)
							+ Stats.getValue(of, playerIdx, Stats.STAMINA)
							+ Stats.getValue(of, playerIdx, Stats.SPEED)
							+ Stats.getValue(of, playerIdx, Stats.L_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 3:
					score = Stats.getValue(of, playerIdx, Stats.DEFENCE)
							+ Stats.getValue(of, playerIdx, Stats.BALANCE)
							+ Stats.getValue(of, playerIdx, Stats.RESPONSE)
							+ Stats.getValue(of, playerIdx, Stats.S_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 4:
					score = Stats.getValue(of, playerIdx, Stats.DEFENCE)
							+ Stats.getValue(of, playerIdx, Stats.ATTACK)
							+ Stats.getValue(of, playerIdx, Stats.DRIBBLE_ACC)
							+ Stats.getValue(of, playerIdx, Stats.S_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.TECH)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 5:
					score = Stats.getValue(of, playerIdx, Stats.ATTACK)
							+ Stats.getValue(of, playerIdx, Stats.SPEED)
							+ Stats.getValue(of, playerIdx, Stats.STAMINA)
							+ Stats.getValue(of, playerIdx, Stats.DRIBBLE_ACC)
							+ Stats.getValue(of, playerIdx, Stats.L_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 6:
					score = Stats.getValue(of, playerIdx, Stats.ATTACK)
							+ Stats.getValue(of, playerIdx, Stats.DRIBBLE_ACC)
							+ Stats.getValue(of, playerIdx, Stats.S_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.TECH)
							+ Stats.getValue(of, playerIdx, Stats.AGILITY)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 7:
					score = Stats.getValue(of, playerIdx, Stats.ATTACK)
							+ Stats.getValue(of, playerIdx, Stats.RESPONSE)
							+ Stats.getValue(of, playerIdx, Stats.SHOT_ACC)
							+ Stats.getValue(of, playerIdx, Stats.SHOT_TEC)
							+ Stats.getValue(of, playerIdx, Stats.TECH)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				case 8:
					score = Stats.getValue(of, playerIdx, Stats.ATTACK)
							+ Stats.getValue(of, playerIdx, Stats.SPEED)
							+ Stats.getValue(of, playerIdx, Stats.DRIBBLE_ACC)
							+ Stats.getValue(of, playerIdx, Stats.DRIBBLE_SPEED)
							+ Stats.getValue(of, playerIdx, Stats.S_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.L_PASS_ACC)
							+ Stats.getValue(of, playerIdx, Stats.AGILITY)
							+ Stats.getValue(of, playerIdx, Stats.TECH)
							+ Stats.getValue(of, playerIdx, Stats.TEAM_WORK);
					break;
				default:
					score = 0;
					break;
			}

			list.add(new SimpleEntry<Integer, Integer>(playerIdx, score));
		}

		return list;
	}

	public static void tidy11(OptionFile of, int team, int freePos, int selPos) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		ensureValidTeam(team);
		log.debug("Try to tidy 11 for team: {}, free-position: {}, selected-position: {}", team, freePos, selPos);

		if (isFixedTeam(team)) {
			return;
		}

		Stat role = Formations.positionToStat(selPos);
		int rolePos = getRolePos(role);

		int size = getTeamSize(team);
		int firstAdr = getOffset(team);
		int firstNumAdr = getNumOffset(team);

		List<Map.Entry<Integer, Integer>> playerScores = getPlayerScores(of, size, rolePos, firstAdr);

		int bestPosPlayer = 0, bestPosScore = 0;
		int bestPlayer = 0, bestScore = 0;
		Map.Entry<Integer, Integer> pIdxScore;

		for (int i = 0; i < playerScores.size(); i++) {
			pIdxScore = playerScores.get(i);
			if (pIdxScore.getKey() == 0) {
				continue;
			}

			boolean isPos = Stats.getValue(of, pIdxScore.getKey(), role) != 0;
			if (isPos && pIdxScore.getValue() > bestPosScore) {
				bestPosScore = pIdxScore.getValue();
				bestPosPlayer = i;
			} else if (!isPos && pIdxScore.getValue() > bestScore) {
				bestScore = pIdxScore.getValue();
				bestPlayer = i;
			}
		}

		if (bestPosScore != 0) {
			bestPlayer = bestPosPlayer;
		}

		Bits.toBytes(playerScores.get(bestPlayer).getKey().shortValue(), of.getData(), firstAdr + freePos * 2);
		bestPlayer += Formations.PLAYER_COUNT;
		Bits.toBytes((short) 0, of.getData(), firstAdr + bestPlayer * 2);

		of.getData()[firstNumAdr + freePos] = of.getData()[firstNumAdr + bestPlayer];
		of.getData()[firstNumAdr + bestPlayer] = (byte) 0xFF;

		tidy(of, team);
	}

}
