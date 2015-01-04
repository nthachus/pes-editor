package editor.data;

import editor.util.Bits;

import java.util.*;

public final class Squads {
	private Squads() {
	}

	public static final int NATION_COUNT = 60;
	public static final int CLASSIC_COUNT = 7;

	public static final int NATION_NUM_ADR = OptionFile.blockAddress(5);
	private static final int FIRST_CLUB_SLOT = (NATION_COUNT + CLASSIC_COUNT) * Formations.NATION_TEAM_SIZE
			+ Player.TOTAL_EDIT;
	private static final int TOTAL_SLOTS = FIRST_CLUB_SLOT + (Clubs.TOTAL + 1) * Formations.CLUB_TEAM_SIZE
			+ Player.TOTAL_SHOP;
	public static final int NATION_ADR = NATION_NUM_ADR + TOTAL_SLOTS + 97;

	public static final int CLUB_NUM_ADR = NATION_NUM_ADR + FIRST_CLUB_SLOT;
	public static final int CLUB_ADR = NATION_ADR + FIRST_CLUB_SLOT * 2;
	public static final int END_ADR = NATION_ADR + TOTAL_SLOTS * 2;

	public static final int EDIT_TEAM_COUNT = (int) Math.ceil((double)
			(Player.TOTAL_EDIT - Formations.CLUB_TEAM_SIZE) / Formations.NATION_TEAM_SIZE) + 1;
	public static final int SHOP_TEAM_COUNT = (int) Math.ceil((double) Player.TOTAL_SHOP / Formations.CLUB_TEAM_SIZE);
	public static final int EXTRA_COUNT = CLASSIC_COUNT + EDIT_TEAM_COUNT;

	public static final int TOTAL = NATION_COUNT + EXTRA_COUNT + Clubs.TOTAL + 2 + SHOP_TEAM_COUNT;

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

	public static final int LAST_EDIT_NATION = FIRST_CLUB - 2;
	/**
	 * Last editable national players slot (23 - 9).
	 */
	public static final int LAST_EDIT_NATION_SIZE
			= (Player.TOTAL_EDIT - Formations.CLUB_TEAM_SIZE) % Formations.NATION_TEAM_SIZE;


	private static int getTeamSize(int team) {
		return (team < FIRST_EDIT_NATION) ? Formations.NATION_TEAM_SIZE : Formations.CLUB_TEAM_SIZE;
	}

	private static int getOffset(int team) {
		if (team < FIRST_EDIT_NATION)
			return NATION_ADR + team * Formations.NATION_TEAM_SIZE * 2;
		return CLUB_ADR + (team - FIRST_CLUB) * Formations.CLUB_TEAM_SIZE * 2;
	}

	private static int getNumOffset(int team) {
		if (team < FIRST_EDIT_NATION)
			return NATION_NUM_ADR + team * Formations.NATION_TEAM_SIZE;
		return CLUB_NUM_ADR + (team - FIRST_CLUB) * Formations.CLUB_TEAM_SIZE;
	}

	public static void fixFormation(OptionFile of, int squad, boolean fixJobs) {
		if (null == of) throw new NullPointerException("of");
		if (squad < 0 || squad >= TOTAL) throw new IndexOutOfBoundsException("squad");

		if ((squad >= FIRST_EDIT_NATION && squad < FIRST_CLUB) || squad >= FIRST_CLUB + Clubs.TOTAL)
			return;

		int team = squad;
		if (squad >= FIRST_CLUB) team -= EDIT_TEAM_COUNT;

		int size = getTeamSize(squad);
		int firstAdr = getOffset(squad);
		int firstNumAdr = getNumOffset(squad);

		byte[] temp = new byte[size * 2];
		byte[] tempNum = new byte[size];
		System.arraycopy(of.getData(), firstAdr, temp, 0, temp.length);
		System.arraycopy(of.getData(), firstNumAdr, tempNum, 0, tempNum.length);

		for (int p = 0; p < size; p++) {
			int fSlot = Formations.getSlot(of, team, p);
			System.arraycopy(temp, fSlot * 2, of.getData(), firstAdr + p * 2, 2);
			System.arraycopy(tempNum, fSlot, of.getData(), firstNumAdr + p, 1);
		}

		if (fixJobs) {
			for (int j = 0; j < Formations.JOBS_COUNT; j++) {
				for (int i = 0; i < size; i++) {
					if (Formations.getSlot(of, team, i) == Formations.getJob(of, team, j)) {
						Formations.setJob(of, team, j, i);
						break;
					}
				}
			}
		}

		for (int i = 0; i < size; i++) {
			Formations.setSlot(of, team, i, i);
		}
	}

	public static void fixAll(OptionFile of) {
		if (null == of) throw new NullPointerException("of");
		for (int s = 0; s < TOTAL; s++) {
			fixFormation(of, s, true);
		}
	}

	public static void tidy(OptionFile of, int team) {
		if (null == of) throw new NullPointerException("of");
		if (team < 0 || team >= TOTAL) throw new IndexOutOfBoundsException("team");

		if ((team >= FIRST_EDIT_NATION && team < FIRST_CLUB) || team >= FIRST_CLUB + Clubs.TOTAL)
			return;

		//fixFormation(of, team);

		int size = getTeamSize(team);
		int firstAdr = getOffset(team);
		int firstNumAdr = getNumOffset(team);

		byte[] tempSlot = new byte[(size - 11) * 2];
		byte[] tempNum = new byte[size - 11];
		int tempPos = 0;
		for (int i = 11; i < size; i++) {

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

		System.arraycopy(tempSlot, 0, of.getData(), firstAdr + 11 * 2, tempSlot.length);
		System.arraycopy(tempNum, 0, of.getData(), firstNumAdr + 11, tempNum.length);
	}

	private static Map.Entry<Integer, Stat> getRolePos(int selPos) {
		Stat stat = Stats.GK;
		int pos = 0;

		if ((selPos > 0 && selPos < 4) || (selPos > 5 && selPos < 8)) {
			stat = Stats.CBT;
			pos = 1;
		} else if (selPos == 4 || selPos == 5) {
			stat = Stats.CWP;
			pos = 1;
		} else if (selPos == 8) {
			stat = Stats.SB;
			pos = 2;
		} else if (selPos == 9) {
			stat = Stats.SB;
			pos = 2;
		} else if (selPos > 9 && selPos < 15) {
			stat = Stats.DM;
			pos = 3;
		} else if (selPos == 15) {
			stat = Stats.WB;
			pos = 2;
		} else if (selPos == 16) {
			stat = Stats.WB;
			pos = 2;
		} else if (selPos > 16 && selPos < 22) {
			stat = Stats.CM;
			pos = 4;
		} else if (selPos == 22) {
			stat = Stats.SM;
			pos = 5;
		} else if (selPos == 23) {
			stat = Stats.SM;
			pos = 5;
		} else if (selPos > 23 && selPos < 29) {
			stat = Stats.AM;
			pos = 6;
		} else if (selPos > 35 && selPos < 41) {
			stat = Stats.CF;
			pos = 7;
		} else if (selPos > 30 && selPos < 36) {
			stat = Stats.SS;
			pos = 6;
		} else if (selPos == 29) {
			stat = Stats.WG;
			pos = 8;
		} else if (selPos == 30) {
			stat = Stats.WG;
			pos = 8;
		}

		return new AbstractMap.SimpleEntry<Integer, Stat>(pos, stat);
	}

	private static List<Map.Entry<Integer, Integer>> getPlayerScores(OptionFile of, int size, int pos, int offset) {
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(size - 11);

		for (int i = 11; i < size; i++) {
			int adr = offset + i * 2;
			int playerIdx = Bits.toInt16(of.getData(), adr);
			if (playerIdx == 0) break;

			int score = 0;
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
			}

			list.add(new AbstractMap.SimpleEntry<Integer, Integer>(playerIdx, score));
		}

		return list;
	}

	public static void tidy11(OptionFile of, int team, int freePos, int selPos) {
		if (null == of) throw new NullPointerException("of");
		if (team < 0 || team >= TOTAL) throw new IndexOutOfBoundsException("team");

		if ((team >= FIRST_EDIT_NATION && team < FIRST_CLUB) || team >= FIRST_CLUB + Clubs.TOTAL)
			return;

		Map.Entry<Integer, Stat> rolePos = getRolePos(selPos);

		int size = getTeamSize(team);
		int firstAdr = getOffset(team);
		int firstNumAdr = getNumOffset(team);

		List<Map.Entry<Integer, Integer>> playerScores = getPlayerScores(of, size, rolePos.getKey(), firstAdr);

		int bestPosPlayer = 0, bestPosScore = 0;
		int bestPlayer = 0, bestScore = 0;
		Map.Entry<Integer, Integer> pIdxScore;

		for (int i = 0; i < playerScores.size(); i++) {
			pIdxScore = playerScores.get(i);
			if (pIdxScore.getKey() == 0) continue;

			int isPos = Stats.getValue(of, pIdxScore.getKey(), rolePos.getValue());
			if (isPos == 1 && pIdxScore.getValue() > bestPosScore) {
				bestPosScore = pIdxScore.getValue();
				bestPosPlayer = i;
			} else if (isPos == 0 && pIdxScore.getValue() > bestScore) {
				bestScore = pIdxScore.getValue();
				bestPlayer = i;
			}
		}

		if (bestPosScore != 0)
			bestPlayer = bestPosPlayer;

		Bits.toBytes(playerScores.get(bestPlayer).getKey().shortValue(), of.getData(), firstAdr + freePos * 2);
		bestPlayer += 11;
		Bits.toBytes((short) 0, of.getData(), firstAdr + bestPlayer * 2);

		of.getData()[firstNumAdr + freePos] = of.getData()[firstNumAdr + bestPlayer];
		of.getData()[firstNumAdr + bestPlayer] = (byte) 0xFF;

		tidy(of, team);
	}

}
