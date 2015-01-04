package editor.data;

import editor.util.Bits;

public final class Formations {
	private Formations() {
	}

	public static final int NATION_TEAM_SIZE = 23;
	public static final int CLUB_TEAM_SIZE = 32;

	public static final int START_ADR = Squads.END_ADR + 192;
	public static final int TOTAL = Squads.NATION_COUNT + Squads.CLASSIC_COUNT + Clubs.TOTAL;

	/**
	 * Record size.
	 */
	public static final int SIZE = 364;
	public static final int ALT_SIZE = 82;

	//region Constants

	public static final int JOBS_COUNT = 6;

	//endregion

	public static int getOffset(int squad) {
		if (squad < 0 || squad >= TOTAL) throw new IndexOutOfBoundsException("squad");
		return START_ADR + SIZE * squad;
	}

	private static int getAltOffset(int squad, int alt) {
		// TODO: validate alt
		return getOffset(squad) + alt * ALT_SIZE;
	}

	private static int getPosOffset(int squad, int alt, int index) {
		if (index <= 0 || index >= 11) throw new IndexOutOfBoundsException("index");
		return getAltOffset(squad, alt) + 138 + index;
	}

	public static int getPosition(OptionFile of, int squad, int alt, int index) {
		if (null == of) throw new NullPointerException("of");

		int adr = getPosOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setPosition(OptionFile of, int squad, int alt, int index, int position) {
		if (null == of) throw new NullPointerException("of");

		int adr = getPosOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(position);
	}

	private static int getSlotOffset(int squad, int index) {
		// TODO: validate index
		return getOffset(squad) + 6 + index;
	}

	public static int getSlot(OptionFile of, int squad, int index) {
		if (null == of) throw new NullPointerException("of");

		int adr = getSlotOffset(squad, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setSlot(OptionFile of, int squad, int index, int player) {
		if (null == of) throw new NullPointerException("of");

		int adr = getSlotOffset(squad, index);
		of.getData()[adr] = Bits.toByte(player);
	}

	private static int getJobOffset(int squad, int index) {
		if (index < 0 || index >= JOBS_COUNT) throw new IndexOutOfBoundsException("index");
		return getOffset(squad) + 111 + index;
	}

	public static int getJob(OptionFile of, int squad, int index) {
		if (null == of) throw new NullPointerException("of");

		int adr = getJobOffset(squad, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setJob(OptionFile of, int squad, int index, int job) {
		if (null == of) throw new NullPointerException("of");

		int adr = getJobOffset(squad, index);
		of.getData()[adr] = Bits.toByte(job);
	}

	private static int getXOffset(int squad, int alt, int index) {
		// TODO: validate index
		return getAltOffset(squad, alt) + 118 + (index - 1) * 2;
	}

	private static int getYOffset(int squad, int alt, int index) {
		// TODO: validate index
		return getAltOffset(squad, alt) + 119 + (index - 1) * 2;
	}

	public static int getX(OptionFile of, int squad, int alt, int index) {
		if (null == of) throw new NullPointerException("of");

		int adr = getXOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static int getY(OptionFile of, int squad, int alt, int index) {
		if (null == of) throw new NullPointerException("of");

		int adr = getYOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setX(OptionFile of, int squad, int alt, int index, int x) {
		if (null == of) throw new NullPointerException("of");

		int adr = getXOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(x);
	}

	public static void setY(OptionFile of, int squad, int alt, int index, int y) {
		if (null == of) throw new NullPointerException("of");

		int adr = getYOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(y);
	}

	private static int getAtkOffset(int squad, int alt, int index) {
		// TODO: validate index
		return getAltOffset(squad, alt) + 149 + index;
	}

	public static boolean getAttack(OptionFile of, int squad, int alt, int index, int direction) {
		if (null == of) throw new NullPointerException("of");

		int adr = getAtkOffset(squad, alt, index);
		int t = Bits.toInt(of.getData()[adr]);

		return (((t >>> direction) & 1) != 0);
	}

	public static void setAttack(OptionFile of, int squad, int alt, int index, int direction) {
		if (null == of) throw new NullPointerException("of");

		int adr = getAtkOffset(squad, alt, index);
		if (direction < 0) {
			of.getData()[adr] = 0;
		} else {
			int t = Bits.toInt(of.getData()[adr]);
			t = t ^ (1 << direction);
			of.getData()[adr] = Bits.toByte(t);
		}
	}

	private static int getDefOffset(int squad, int alt, int index) {
		// TODO: validate index
		return getAltOffset(squad, alt) + 160 + index;
	}

	public static int getDefence(OptionFile of, int squad, int alt, int index) {
		if (null == of) throw new NullPointerException("of");

		int adr = getDefOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setDefence(OptionFile of, int squad, int alt, int index, int direction) {
		if (null == of) throw new NullPointerException("of");

		int adr = getDefOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(direction);
	}

	private static int getStrategyOffset(int squad, int button) {
		// TODO: validate button
		return getOffset(squad) + 102 + button;
	}

	public static int getStrategy(OptionFile of, int squad, int button) {
		if (null == of) throw new NullPointerException("of");

		int adr = getStrategyOffset(squad, button);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setStrategy(OptionFile of, int squad, int button, int strategy) {
		if (null == of) throw new NullPointerException("of");

		int adr = getStrategyOffset(squad, button);
		of.getData()[adr] = Bits.toByte(strategy);
	}

	private static int getOlCBOffset(int squad) {
		return getOffset(squad) + 106;
	}

	/**
	 * CB Overlap.
	 */
	public static int getCBOverlap(OptionFile of, int squad) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOlCBOffset(squad);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setCBOverlap(OptionFile of, int squad, int cbOverlap) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOlCBOffset(squad);
		of.getData()[adr] = Bits.toByte(cbOverlap);
	}

	private static int getStAutoOffset(int squad) {
		return getOffset(squad) + 107;
	}

	public static boolean getStrategyAuto(OptionFile of, int squad) {
		if (null == of) throw new NullPointerException("of");

		int adr = getStAutoOffset(squad);
		return (of.getData()[adr] != 0);
	}

	public static void setStrategyAuto(OptionFile of, int squad, boolean auto) {
		if (null == of) throw new NullPointerException("of");

		int adr = getStAutoOffset(squad);
		of.getData()[adr] = Bits.toByte(auto);
	}

	private static int getTeamOffset(int squad, int alt, int set) {
		// TODO: validate set
		return getAltOffset(squad, alt) + 194 + set;
	}

	public static int getTeam(OptionFile of, int squad, int alt, int set) {
		if (null == of) throw new NullPointerException("of");

		int adr = getTeamOffset(squad, alt, set);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setTeam(OptionFile of, int squad, int alt, int set, int value) {
		if (null == of) throw new NullPointerException("of");

		int adr = getTeamOffset(squad, alt, set);
		of.getData()[adr] = Bits.toByte(value);
	}

}
