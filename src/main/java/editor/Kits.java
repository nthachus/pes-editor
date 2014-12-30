package editor;

import editor.data.OptionFile;

public final class Kits {
	private Kits() {
	}

	public static final int START_NATION_ADR = 751254;
	public static final int START_CLUB_ADR = 781806;

	public static final int SIZE_NATION = 456;
	public static final int SIZE_CLUB = 648;

	public static boolean logoUsed(OptionFile of, int team, int logo) {
		int a = START_CLUB_ADR + 358 + (SIZE_CLUB * team) + (logo * 24) + 2;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = START_NATION_ADR + 358 + (SIZE_NATION * team) + (logo * 24) + 2;
		}
		if (of.getData()[a] == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static byte getLogo(OptionFile of, int team, int logo) {
		int a = START_CLUB_ADR + 358 + (SIZE_CLUB * team) + (logo * 24) + 3;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = START_NATION_ADR + 358 + (SIZE_NATION * team) + (logo * 24) + 3;
		}
		return of.getData()[a];
	}

	public static void setLogo(OptionFile of, int team, int logo, byte slot) {
		int a = START_CLUB_ADR + 358 + (SIZE_CLUB * team) + (logo * 24) + 3;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = START_NATION_ADR + 358 + (SIZE_NATION * team) + (logo * 24) + 3;
		}
		of.getData()[a] = slot;
	}

	public static void setLogoUnused(OptionFile of, int team, int logo) {
		int a = START_CLUB_ADR + 358 + (SIZE_CLUB * team) + (logo * 24) + 2;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = START_NATION_ADR + 358 + (SIZE_NATION * team) + (logo * 24) + 2;
		}
		of.getData()[a] = 0;
		of.getData()[a + 1] = 88;
	}

	public static void importKit(OptionFile of1, int team1, OptionFile of2, int team2) {
		// int t = team1;
		int a1 = START_CLUB_ADR + (SIZE_CLUB * team1);
		int a2 = START_CLUB_ADR + (SIZE_CLUB * team2);
		int size = SIZE_CLUB;
		if (team1 >= Clubs.TOTAL) {
			team1 = team1 - Clubs.TOTAL;
			a1 = START_NATION_ADR + (SIZE_NATION * team1);
			team2 = team2 - Clubs.TOTAL;
			a2 = START_NATION_ADR + (SIZE_NATION * team2);
			size = SIZE_NATION;
		}
		System.arraycopy(of2.getData(), a2, of1.getData(), a1, size);

		/*
		 * if (!of1.isWE() && of2.isWE()) { Convert.kitModel(of1, t); }
		 */
	}

	public static boolean isLicensed(OptionFile of, int team) {
		int a = START_CLUB_ADR + 78 + (SIZE_CLUB * team);
		int b = START_CLUB_ADR + 79 + (SIZE_CLUB * team);
		//System.out.println(Clubs.getName(of, team) + " : " +of.data[a] +" " +of.data[b]);
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = START_NATION_ADR + 78 + (SIZE_NATION * team);
			b = START_NATION_ADR + 79 + (SIZE_NATION * team);
			//	System.out.println(Stats.NATION[team] + " : " +of.data[a] +" " +of.data[b]);
		}
		if (of.getData()[a] != -1 && of.getData()[b] != -1) {
			return true;
		}
		return false;
	}
}
