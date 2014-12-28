package editor;

import editor.data.OptionFile;
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

	public static byte getPos(OptionFile of, int squad, int alt, int index) {
		return of.getData()[START_ADR + 138 + (SIZE * squad) + (alt * ALT_SIZE) + index];
	}

	public static void setPos(OptionFile of, int squad, int alt, int index, int position) {
		of.getData()[START_ADR + 138 + (SIZE * squad) + (alt * ALT_SIZE) + index] = (byte) position;
	}

	public static byte getSlot(OptionFile of, int squad, int i) {
		return of.getData()[START_ADR + 6 + (SIZE * squad) + i];
	}

	public static void setSlot(OptionFile of, int squad, int i, byte p) {
		of.getData()[START_ADR + 6 + (SIZE * squad) + i] = p;
	}

	private static int getJobOffset(int squad, int offset) {
		return START_ADR + SIZE * squad + 111 + offset;
	}

	public static int getJob(OptionFile of, int squad, int offset) {
		if (null == of) throw new NullPointerException("of");
		return Bits.toInt(of.getData()[getJobOffset(squad, offset)]);
	}

	public static void setJob(OptionFile of, int squad, int offset, int job) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[getJobOffset(squad, offset)] = Bits.toByte(job);
	}

	public static byte getX(OptionFile of, int squad, int alt, int i) {
		return of.getData()[START_ADR + 118 + (SIZE * squad) + (alt * ALT_SIZE)
				+ (2 * (i - 1))];
	}

	public static byte getY(OptionFile of, int squad, int alt, int i) {
		return of.getData()[START_ADR + 119 + (SIZE * squad) + (alt * ALT_SIZE)
				+ (2 * (i - 1))];
	}

	public static void setX(OptionFile of, int squad, int alt, int i, int x) {
		of.getData()[START_ADR + 118 + (SIZE * squad) + (alt * ALT_SIZE)
				+ (2 * (i - 1))] = (byte) x;
	}

	public static void setY(OptionFile of, int squad, int alt, int i, int y) {
		of.getData()[START_ADR + 119 + (SIZE * squad) + (alt * ALT_SIZE)
				+ (2 * (i - 1))] = (byte) y;
	}

	public static boolean getAtk(OptionFile of, int squad, int alt, int i, int direction) {
		boolean result = false;
		byte t = of.getData()[START_ADR + 149 + (SIZE * squad) + (alt * ALT_SIZE) + i];
		if (((t >>> direction) & 1) == 1) {
			result = true;
		}
		return result;
	}

	public static void setAtk(OptionFile of, int squad, int alt, int i, int direction) {
		if (direction < 0) {
			of.getData()[START_ADR + 149 + (SIZE * squad) + (alt * ALT_SIZE) + i] = 0;
		} else {
			int t = of.getData()[START_ADR + 149 + (SIZE * squad) + (alt * ALT_SIZE)
					+ i];
			t = t ^ (1 << direction);
			of.getData()[START_ADR + 149 + (SIZE * squad) + (alt * ALT_SIZE) + i] = (byte) t;
		}
	}

	public static byte getDef(OptionFile of, int squad, int alt, int i) {
		return of.getData()[START_ADR + 160 + (SIZE * squad) + (alt * ALT_SIZE) + i];
	}

	public static void setDef(OptionFile of, int squad, int alt, int i, int d) {
		of.getData()[START_ADR + 160 + (SIZE * squad) + (alt * ALT_SIZE) + i] = (byte) d;
	}

	public static byte getStrategy(OptionFile of, int squad, int button) {
		return of.getData()[START_ADR + 102 + (SIZE * squad) + button];
	}

	public static void setStrategy(OptionFile of, int squad, int button, int strategy) {
		of.getData()[START_ADR + 102 + (SIZE * squad) + button] = Bits.toByte(strategy);
	}

	/**
	 * CB Overlap.
	 */
	public static byte getStrategyOlCB(OptionFile of, int squad) {
		return of.getData()[START_ADR + 106 + (SIZE * squad)];
	}

	public static void setStrategyOlCB(OptionFile of, int squad, int cbOverlap) {
		of.getData()[START_ADR + 106 + (SIZE * squad)] = Bits.toByte(cbOverlap);
	}

	public static boolean getStrategyAuto(OptionFile of, int squad) {
		if (null == of) throw new NullPointerException("of");
		return (of.getData()[START_ADR + SIZE * squad + 107] != 0);
	}

	public static void setStrategyAuto(OptionFile of, int squad, boolean auto) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[START_ADR + SIZE * squad + 107] = Bits.toByte(auto);
	}

	private static int getTeamOffset(int squad, int alt, int set) {
		return START_ADR + SIZE * squad + 194 + alt * ALT_SIZE + set;
	}

	public static int getTeam(OptionFile of, int squad, int alt, int set) {
		if (null == of) throw new NullPointerException("of");
		return Bits.toInt(of.getData()[getTeamOffset(squad, alt, set)]);
	}

	public static void setTeam(OptionFile of, int squad, int alt, int set, int value) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[getTeamOffset(squad, alt, set)] = Bits.toByte(value);
	}

}
