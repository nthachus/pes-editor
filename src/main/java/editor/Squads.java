package editor;

import editor.data.OptionFile;
import editor.data.Stat;
import editor.data.Stats;
import editor.util.Bits;

public final class Squads {
	private Squads() {
	}

	public static final int NATION_COUNT = 60;
	public static final int CLASSIC_COUNT = 7;

	public static final int NATION_NUM_ADR = OptionFile.blockAddress(5);
	public static final int FIRST_CLUB_SLOT = (NATION_COUNT + CLASSIC_COUNT) * Formations.NATION_TEAM_SIZE
			+ Player.TOTAL_EDIT;
	public static final int TOTAL_SLOTS = FIRST_CLUB_SLOT + (Clubs.TOTAL + 1) * Formations.CLUB_TEAM_SIZE
			+ Player.TOTAL_SHOP;
	public static final int NATION_ADR = NATION_NUM_ADR + TOTAL_SLOTS + 97;

	public static final int CLUB_NUM_ADR = NATION_NUM_ADR + FIRST_CLUB_SLOT;
	public static final int CLUB_ADR = NATION_ADR + FIRST_CLUB_SLOT * 2;
	public static final int END_ADR = NATION_ADR + TOTAL_SLOTS * 2;

	public static final int EXTRA_COUNT = 15;
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

	public static void fixForm(OptionFile of, int s, boolean fixJobs) {
		//System.out.println(s);
		byte[] temp = new byte[64];
		byte[] tempNum = new byte[32];
		int t;
		int size;
		int firstAdr;
		int firstAdrNum;
		// for (int t = 0; t < 202; t++) {
		if ((s >= 0 && s < 67) || (s >= 75 && s < 205)) {
			t = s;
			if (s > 74) {
				t = t - 8;
			}
			// System.out.println(t);
			if (s < 67) {
				size = 23;
				firstAdr = NATION_ADR + (s * size * 2);
				firstAdrNum = NATION_NUM_ADR + (s * size);
			} else {
				size = 32;
				firstAdr = CLUB_ADR + ((s - 75) * size * 2);
				firstAdrNum = CLUB_NUM_ADR + ((s - 75) * size);
			}
			//System.out.println(firstAdr);
			System.arraycopy(of.getData(), firstAdr, temp, 0, size * 2);
			System.arraycopy(of.getData(), firstAdrNum, tempNum, 0, size);
			for (int p = 0; p < size; p++) {
				System.arraycopy(temp, Formations.getSlot(of, t, p) * 2, of.getData(), firstAdr + (p * 2), 2);
				System.arraycopy(tempNum, Formations.getSlot(of, t, p), of.getData(), firstAdrNum + p, 1);
			}

			if (fixJobs) {
				boolean fixed;
				for (int j = 0; j < 6; j++) {
					fixed = false;
					for (byte i = 0; !fixed && i < 32; i++) {
						if (Formations.getSlot(of, t, i) == Formations.getJob(
								of, t, j)) {
							Formations.setJob(of, t, j, i);

							fixed = true;
						}
					}
				}
			}

			for (byte i = 0; i < 32; i++) {
				Formations.setSlot(of, t, i, i);
			}
		}
	}

	public static void fixAll(OptionFile of) {
		for (int i = 0; i < 211; i++) {
			fixForm(of, i, true);
		}
	}

	public static void tidy(OptionFile of, int team) {
		if ((team >= 0 && team < 67) || (team >= 75 && team < 205)) {
			// FormFixer.fixForm(of, team);
			// System.out.println(team);
			int size;
			int firstAdr;
			int firstAdrNum;
			if (team < 67) {
				size = 23;
				firstAdr = NATION_ADR + (team * size * 2);
				firstAdrNum = NATION_NUM_ADR + (team * size);
			} else {
				size = 32;
				firstAdr = CLUB_ADR + ((team - 75) * size * 2);
				firstAdrNum = CLUB_NUM_ADR + ((team - 75) * size);
			}
			byte[] tempSlot = new byte[(size - 11) * 2];
			byte[] tempNum = new byte[size - 11];
			int numAdr;
			int slotAdr;
			int tempPos = 0;
			for (int i = 11; i < size; i++) {
				slotAdr = firstAdr + (i * 2);
				numAdr = firstAdrNum + i;
				if (!(of.getData()[slotAdr] == 0 && of.getData()[slotAdr + 1] == 0)) {
					tempSlot[tempPos * 2] = of.getData()[slotAdr];
					tempSlot[(tempPos * 2) + 1] = of.getData()[slotAdr + 1];
					tempNum[tempPos] = of.getData()[numAdr];
					tempPos++;
				}
			}
			for (int j = tempPos; j < size - 11; j++) {
				tempNum[j] = -1;
			}
			System.arraycopy(tempSlot, 0, of.getData(), firstAdr + 22, tempSlot.length);
			System.arraycopy(tempNum, 0, of.getData(), firstAdrNum + 11, tempNum.length);
		}
	}

	public static void tidy11(OptionFile of, int team, int freePos, int selPos) {
		if ((team >= 0 && team < 67) || (team >= 75 && team < 205)) {
			//	System.out.println(team);
			Stat stat = Stats.GK;
			int[] score = new int[21];
			int pos = 0;
			if ((selPos > 0 && selPos < 4) || (selPos > 5 && selPos < 8)) {
				stat = Stats.CBT;
				pos = 1;
			}
			if (selPos == 4 || selPos == 5) {
				stat = Stats.CWP;
				pos = 1;
			}
			if (selPos == 8) {
				stat = Stats.SB;
				pos = 2;
			}
			if (selPos == 9) {
				stat = Stats.SB;
				pos = 2;
			}
			if (selPos > 9 && selPos < 15) {
				stat = Stats.DM;
				pos = 3;
			}
			if (selPos == 15) {
				stat = Stats.WB;
				pos = 2;
			}
			if (selPos == 16) {
				stat = Stats.WB;
				pos = 2;
			}
			if (selPos > 16 && selPos < 22) {
				stat = Stats.CM;
				pos = 4;
			}
			if (selPos == 22) {
				stat = Stats.SM;
				pos = 5;
			}
			if (selPos == 23) {
				stat = Stats.SM;
				pos = 5;
			}
			if (selPos > 23 && selPos < 29) {
				stat = Stats.AM;
				pos = 6;
			}
			if (selPos > 35 && selPos < 41) {
				stat = Stats.CF;
				pos = 7;
			}
			if (selPos > 30 && selPos < 36) {
				stat = Stats.SS;
				pos = 6;
			}
			if (selPos == 29) {
				stat = Stats.WG;
				pos = 8;
			}
			if (selPos == 30) {
				stat = Stats.WG;
				pos = 8;
			}

			int size;
			int firstAdr;
			int firstAdrNum;
			if (team < 67) {
				size = 23;
				firstAdr = NATION_ADR + (team * size * 2);
				firstAdrNum = NATION_NUM_ADR + (team * size);
			} else {
				size = 32;
				firstAdr = CLUB_ADR + ((team - 75) * size * 2);
				firstAdrNum = CLUB_NUM_ADR + ((team - 75) * size);
			}

			int a;
			int c = 0;
			int pi = -1;
			int[] playerIndex = new int[21];
			for (int i = 11; pi != 0 && i < size; i++) {
				c = i - 11;
				a = firstAdr + (i * 2);
				pi = Bits.toInt(of.getData()[a + 1]) << 8 | Bits.toInt(of.getData()[a]);
				if (pi != 0) {
					playerIndex[c] = pi;
					switch (pos) {
						case 0:
							score[c] = Stats.getValue(of, pi, Stats.DEFENCE)
									+ Stats.getValue(of, pi, Stats.BALANCE)
									+ Stats.getValue(of, pi, Stats.RESPONSE)
									+ Stats.getValue(of, pi, Stats.GK_ABILITY)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 1:
							score[c] = Stats.getValue(of, pi, Stats.DEFENCE)
									+ Stats.getValue(of, pi, Stats.BALANCE)
									+ Stats.getValue(of, pi, Stats.RESPONSE)
									+ Stats.getValue(of, pi, Stats.SPEED)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 2:
							score[c] = Stats.getValue(of, pi, Stats.DEFENCE)
									+ Stats.getValue(of, pi, Stats.BALANCE)
									+ Stats.getValue(of, pi, Stats.RESPONSE)
									+ Stats.getValue(of, pi, Stats.STAMINA)
									+ Stats.getValue(of, pi, Stats.SPEED)
									+ Stats.getValue(of, pi, Stats.L_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 3:
							score[c] = Stats.getValue(of, pi, Stats.DEFENCE)
									+ Stats.getValue(of, pi, Stats.BALANCE)
									+ Stats.getValue(of, pi, Stats.RESPONSE)
									+ Stats.getValue(of, pi, Stats.S_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 4:
							score[c] = Stats.getValue(of, pi, Stats.DEFENCE)
									+ Stats.getValue(of, pi, Stats.ATTACK)
									+ Stats.getValue(of, pi, Stats.DRIBBLE_ACC)
									+ Stats.getValue(of, pi, Stats.S_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.TECH)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 5:
							score[c] = Stats.getValue(of, pi, Stats.ATTACK)
									+ Stats.getValue(of, pi, Stats.SPEED)
									+ Stats.getValue(of, pi, Stats.STAMINA)
									+ Stats.getValue(of, pi, Stats.DRIBBLE_ACC)
									+ Stats.getValue(of, pi, Stats.L_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 6:
							score[c] = Stats.getValue(of, pi, Stats.ATTACK)
									+ Stats.getValue(of, pi, Stats.DRIBBLE_ACC)
									+ Stats.getValue(of, pi, Stats.S_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.TECH)
									+ Stats.getValue(of, pi, Stats.AGILITY)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 7:
							score[c] = Stats.getValue(of, pi, Stats.ATTACK)
									+ Stats.getValue(of, pi, Stats.RESPONSE)
									+ Stats.getValue(of, pi, Stats.SHOT_ACC)
									+ Stats.getValue(of, pi, Stats.SHOT_TEC)
									+ Stats.getValue(of, pi, Stats.TECH)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
						case 8:
							score[c] = Stats.getValue(of, pi, Stats.ATTACK)
									+ Stats.getValue(of, pi, Stats.SPEED)
									+ Stats.getValue(of, pi, Stats.DRIBBLE_ACC)
									+ Stats.getValue(of, pi, Stats.DRIBBLE_SPEED)
									+ Stats.getValue(of, pi, Stats.S_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.L_PASS_ACC)
									+ Stats.getValue(of, pi, Stats.AGILITY)
									+ Stats.getValue(of, pi, Stats.TECH)
									+ Stats.getValue(of, pi, Stats.TEAM_WORK);
							break;
					}
				}
			}

			int bestPosPlayer = 0;
			int bestPosScore = 0;
			int bestPlayer = 0;
			int bestScore = 0;
			int isPos = 0;
			for (int i = 0; i < 21; i++) {
				if (playerIndex[i] != 0) {
					isPos = Stats.getValue(of, playerIndex[i], stat);
					if (isPos == 1 && score[i] > bestPosScore) {
						bestPosScore = score[i];
						bestPosPlayer = i;
					}
					if (isPos == 0 && score[i] > bestScore) {
						bestScore = score[i];
						bestPlayer = i;
					}
				}
			}
			// System.out.println(pos);
			// System.out.println(bestPlayer);
			// System.out.println(bestPosPlayer);
			if (bestPosScore != 0) {
				bestPlayer = bestPosPlayer;
			}
			bestPlayer = bestPlayer + 11;
			//System.out.println(bestPlayer);
			of.getData()[firstAdr + (2 * freePos)] = Bits.toByte(playerIndex[bestPlayer - 11]);
			of.getData()[firstAdr + (2 * freePos) + 1] = Bits.toByte(playerIndex[bestPlayer - 11] >>> 8);
			of.getData()[firstAdr + (2 * bestPlayer)] = 0;
			of.getData()[firstAdr + (2 * bestPlayer) + 1] = 0;
			of.getData()[firstAdrNum + freePos] = of.getData()[firstAdrNum + bestPlayer];
			of.getData()[firstAdrNum + bestPlayer] = -1;
			tidy(of, team);
		}
	}

}
