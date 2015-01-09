package editor.data;

import editor.util.Bits;

public final class Kits {
	private Kits() {
	}

	public static final int START_ADR = OptionFile.blockAddress(7);

	/**
	 * National Kits record size.
	 */
	public static final int SIZE_NATION = 456;
	/**
	 * Club Kits record size.
	 */
	public static final int SIZE_CLUB = 648;
	public static final int TOTAL = Formations.TOTAL;

	public static final int START_CLUB_ADR = START_ADR + (TOTAL - Clubs.TOTAL) * SIZE_NATION;
	public static final int END_ADR = START_CLUB_ADR + Clubs.TOTAL * SIZE_CLUB;

	public static final int TOTAL_LOGO_SLOT = 4;
	private static final int USED_LOGO_OFS = 360;
	private static final int IS_LICENSED_OFS = 80;

	private static int getOffset(int teamId) {
		if (teamId < 0 || teamId >= TOTAL) throw new IndexOutOfBoundsException("teamId#" + teamId);
		if (teamId < Clubs.TOTAL)
			return START_CLUB_ADR + teamId * SIZE_CLUB;
		return START_ADR + (teamId - Clubs.TOTAL) * SIZE_NATION;
	}

	private static int getSize(int teamId) {
		return (teamId < Clubs.TOTAL) ? SIZE_CLUB : SIZE_NATION;
	}

	private static int getOffset(int teamId, int logo) {
		if (logo < 0 || logo >= TOTAL_LOGO_SLOT) throw new IndexOutOfBoundsException("logo#" + logo);
		return getOffset(teamId) + USED_LOGO_OFS + logo * 24;
	}

	public static boolean isLogoUsed(OptionFile of, int team, int logo) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(team, logo) + 2;
		return (of.getData()[adr] != 0);
	}

	public static int getLogo(OptionFile of, int team, int logo) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(team, logo) + 3;
		return Bits.toInt(of.getData()[adr]);
	}

	/**
	 * @param slot Logo index.
	 */
	public static void setLogo(OptionFile of, int team, int logo, int slot) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(team, logo) + 3;
		of.getData()[adr] = Bits.toByte(slot);
	}

	public static void setLogoUnused(OptionFile of, int team, int logo) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(team, logo) + 2;
		of.getData()[adr] = 0;
		of.getData()[adr + 1] = Logos.TOTAL + 8;
	}

	private static final byte UNLICENSED_VAL = (byte) 0xFF;

	public static boolean isLicensed(OptionFile of, int team) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(team) + IS_LICENSED_OFS;
		return (of.getData()[adr] != UNLICENSED_VAL && of.getData()[adr + 1] != UNLICENSED_VAL);
	}

	public static void importData(OptionFile ofSource, int teamSource, OptionFile ofDest, int teamDest) {
		if (null == ofSource) throw new NullPointerException("ofSource");
		if (null == ofDest) throw new NullPointerException("ofDest");

		int srcAdr = getOffset(teamSource);
		int destAdr = getOffset(teamDest);

		int srcSize = getSize(teamSource);
		int destSize = getSize(teamDest);

		System.arraycopy(ofSource.getData(), srcAdr, ofDest.getData(), destAdr, Math.min(srcSize, destSize));
	}

}
