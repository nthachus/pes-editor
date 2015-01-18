package editor.data;

import editor.lang.NullArgumentException;
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
	public static final int SETTING_COUNT = 4;
	public static final int PLAYER_COUNT = 11;

	public static final String[] ALT_ITEMS = {
			"Normal",
			"Strategy Plan A",
			"Strategy Plan B"
	};

	public static final String[] FORM_NAMES = {
			"4-4-2",
			"4-3-1-2",
			"4-4-1-1",
			"4-2-1-3",
			"4-5-1",
			"4-1-2-3",
			"4-3-3",
			"4-3-2-1",
			"3-4-1-2",
			"3-3-2-2",
			"3-4-3",
			"5-4-1"
	};

	private static final byte[] FORM_DATA = {
			9, 63, 9, 41, 12, 87, 12, 17, 26, 77, 26, 61, 26, 43, 26, 27, 43, 66, 43, 38, 0, 7, 1, 9, 8, 20, 21, 17, 18,
			40, 36,
			9, 63, 9, 41, 12, 87, 12, 17, 18, 52, 26, 70, 26, 34, 34, 52, 43, 66, 43, 38, 0, 7, 1, 9, 8, 12, 21, 17, 26,
			40, 36,
			9, 63, 9, 41, 12, 87, 12, 17, 18, 61, 18, 43, 29, 80, 29, 24, 32, 52, 43, 52, 0, 7, 1, 9, 8, 14, 10, 23, 22,
			26, 38,
			9, 63, 9, 41, 12, 87, 12, 17, 18, 61, 18, 43, 32, 52, 43, 72, 43, 32, 43, 52, 0, 7, 1, 9, 8, 14, 10, 26, 30,
			29, 38,
			9, 63, 9, 41, 12, 87, 12, 17, 18, 52, 29, 61, 29, 43, 31, 80, 31, 24, 43, 52, 0, 7, 1, 9, 8, 12, 21, 17, 23,
			22, 38,
			9, 63, 9, 41, 12, 87, 12, 17, 18, 52, 32, 70, 32, 34, 43, 72, 43, 32, 43, 52, 0, 7, 1, 9, 8, 12, 28, 24, 30,
			29, 38,
			9, 63, 9, 41, 12, 87, 12, 17, 26, 77, 26, 52, 26, 27, 43, 72, 43, 32, 43, 52, 0, 7, 1, 9, 8, 21, 19, 17, 30,
			29, 38,
			9, 63, 9, 41, 12, 87, 12, 17, 22, 77, 22, 52, 22, 27, 34, 70, 34, 34, 43, 52, 0, 7, 1, 9, 8, 21, 19, 17, 28,
			24, 38,
			9, 72, 9, 52, 9, 32, 18, 61, 18, 43, 24, 80, 24, 24, 32, 52, 43, 66, 43, 38, 0, 7, 3, 1, 14, 10, 16, 15, 26,
			40, 36,
			9, 72, 9, 52, 9, 32, 18, 52, 24, 80, 24, 24, 32, 61, 32, 43, 43, 66, 43, 38, 0, 7, 3, 1, 12, 16, 15, 28, 24,
			40, 36,
			9, 72, 9, 52, 9, 32, 24, 80, 24, 24, 22, 61, 22, 43, 43, 72, 43, 32, 43, 52, 0, 7, 3, 1, 16, 15, 21, 17, 30,
			29, 38,
			9, 72, 9, 52, 9, 32, 12, 87, 12, 17, 18, 61, 18, 43, 31, 80, 31, 24, 43, 52, 0, 7, 3, 1, 9, 8, 14, 10, 23,
			22, 38
	};

	//endregion

	public static int getOffset(int squad) {
		if (squad < 0 || squad >= TOTAL) throw new IndexOutOfBoundsException("squad#" + squad);
		return START_ADR + SIZE * squad;
	}

	private static int getAltOffset(int squad, int alt) {
		if (alt < 0 || alt >= ALT_ITEMS.length) throw new IndexOutOfBoundsException("alt#" + alt);
		return getOffset(squad) + alt * ALT_SIZE;
	}

	private static int getPosOffset(int squad, int alt, int index) {
		if (index < 0 || index >= PLAYER_COUNT) throw new IndexOutOfBoundsException("index#" + index);
		return getAltOffset(squad, alt) + 138 + index;
	}

	public static int getPosition(OptionFile of, int squad, int alt, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getPosOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setPosition(OptionFile of, int squad, int alt, int index, int position) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getPosOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(position);
	}

	private static int getSlotOffset(int squad, int index) {
		if (index < 0 || index >= CLUB_TEAM_SIZE) throw new IndexOutOfBoundsException("index#" + index);
		return getOffset(squad) + 6 + index;
	}

	public static int getSlot(OptionFile of, int squad, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getSlotOffset(squad, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setSlot(OptionFile of, int squad, int index, int player) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getSlotOffset(squad, index);
		of.getData()[adr] = Bits.toByte(player);
	}

	private static int getJobOffset(int squad, int index) {
		if (index < 0 || index >= JOBS_COUNT) throw new IndexOutOfBoundsException("index#" + index);
		return getOffset(squad) + 111 + index;
	}

	public static int getJob(OptionFile of, int squad, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getJobOffset(squad, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setJob(OptionFile of, int squad, int index, int job) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getJobOffset(squad, index);
		of.getData()[adr] = Bits.toByte(job);
	}

	private static int getXOffset(int squad, int alt, int index) {
		if (index <= 0 || index >= PLAYER_COUNT) throw new IndexOutOfBoundsException("index#" + index);
		return getAltOffset(squad, alt) + 118 + (index - 1) * 2;
	}

	private static int getYOffset(int squad, int alt, int index) {
		if (index <= 0 || index >= PLAYER_COUNT) throw new IndexOutOfBoundsException("index#" + index);
		return getAltOffset(squad, alt) + 119 + (index - 1) * 2;
	}

	public static int getX(OptionFile of, int squad, int alt, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getXOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static int getY(OptionFile of, int squad, int alt, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getYOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setX(OptionFile of, int squad, int alt, int index, int x) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getXOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(x);
	}

	public static void setY(OptionFile of, int squad, int alt, int index, int y) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getYOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(y);
	}

	private static int getAtkOffset(int squad, int alt, int index) {
		if (index < 0 || index >= PLAYER_COUNT) throw new IndexOutOfBoundsException("index#" + index);
		return getAltOffset(squad, alt) + 149 + index;
	}

	public static boolean getAttack(OptionFile of, int squad, int alt, int index, int direction) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getAtkOffset(squad, alt, index);
		int t = Bits.toInt(of.getData()[adr]);

		return (((t >>> direction) & 1) != 0);
	}

	public static void setAttack(OptionFile of, int squad, int alt, int index, int direction) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

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
		if (index < 0 || index >= PLAYER_COUNT) throw new IndexOutOfBoundsException("index#" + index);
		return getAltOffset(squad, alt) + 160 + index;
	}

	public static int getDefence(OptionFile of, int squad, int alt, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getDefOffset(squad, alt, index);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setDefence(OptionFile of, int squad, int alt, int index, int defence) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getDefOffset(squad, alt, index);
		of.getData()[adr] = Bits.toByte(defence);
	}

	private static int getStrategyOffset(int squad, int button) {
		if (button < 0 || button >= ControlButton.size())
			throw new IndexOutOfBoundsException("button#" + button);
		return getOffset(squad) + 102 + button;
	}

	public static int getStrategy(OptionFile of, int squad, int button) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getStrategyOffset(squad, button);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setStrategy(OptionFile of, int squad, int button, int strategy) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

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
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOlCBOffset(squad);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setCBOverlap(OptionFile of, int squad, int cbOverlap) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOlCBOffset(squad);
		of.getData()[adr] = Bits.toByte(cbOverlap);
	}

	private static int getStAutoOffset(int squad) {
		return getOffset(squad) + 107;
	}

	public static boolean getStrategyAuto(OptionFile of, int squad) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getStAutoOffset(squad);
		return (of.getData()[adr] != 0);
	}

	public static void setStrategyAuto(OptionFile of, int squad, boolean auto) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getStAutoOffset(squad);
		of.getData()[adr] = Bits.toByte(auto);
	}

	private static int getTeamSetOffset(int squad, int alt, int setting) {
		if (setting < 0 || setting >= SETTING_COUNT)
			throw new IndexOutOfBoundsException("setting#" + setting);
		return getAltOffset(squad, alt) + 194 + setting;
	}

	public static int getTeamSetting(OptionFile of, int squad, int alt, int setting) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getTeamSetOffset(squad, alt, setting);
		return Bits.toInt(of.getData()[adr]);
	}

	public static void setTeamSetting(OptionFile of, int squad, int alt, int setting, int value) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getTeamSetOffset(squad, alt, setting);
		of.getData()[adr] = Bits.toByte(value);
	}

	public static Stat positionToStat(int pos) {
		if (pos <= 0) {
			return Stats.GK;
		} else if (pos < 4 || (pos > 5 && pos < 8)) {
			return Stats.CBT;
		} else if (pos == 4 || pos == 5) {
			return Stats.CWP;
		} else if (pos == 8 || pos == 9) {
			return Stats.SB;
		} else if (pos < 15) {
			return Stats.DM;
		} else if (pos == 15 || pos == 16) {
			return Stats.WB;
		} else if (pos < 22) {
			return Stats.CM;
		} else if (pos == 22 || pos == 23) {
			return Stats.SM;
		} else if (pos < 29) {
			return Stats.AM;
		} else if (pos == 29 || pos == 30) {
			return Stats.WG;
		} else if (pos < 36) {
			return Stats.SS;
		} else if (pos < 41) {
			return Stats.CF;
		}
		return Stats.GK;
	}

	public static String positionToString(int pos) {
		if (pos <= 0) {
			return "GK";
		} else if (pos < 4 || (pos > 5 && pos < 8)) {
			return "CB";
		} else if (pos == 4) {
			return "SW";//CWP
		} else if (pos == 5) {
			return "ASW";
		} else if (pos == 8) {
			return "LB";
		} else if (pos == 9) {
			return "RB";
		} else if (pos < 15) {
			return "DM";
		} else if (pos == 15) {
			return "LWB";
		} else if (pos == 16) {
			return "RWB";
		} else if (pos < 22) {
			return "CM";
		} else if (pos == 22) {
			return "LMF";
		} else if (pos == 23) {
			return "RMF";
		} else if (pos < 29) {
			return "AM";
		} else if (pos == 29) {
			return "LWF";
		} else if (pos == 30) {
			return "RWF";
		} else if (pos < 36) {
			return "SS";
		} else if (pos < 41) {
			return "CF";
		}
		return Integer.toString(pos);
	}

	private static final int FORM_DATA_LEN = FORM_DATA.length / FORM_NAMES.length;

	public static void setFormation(OptionFile of, int team, int alt, int formId) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (formId < 0 || formId >= FORM_NAMES.length) throw new IndexOutOfBoundsException("formId#" + formId);

		int adr = getXOffset(team, alt, 1);
		System.arraycopy(FORM_DATA, formId * FORM_DATA_LEN, of.getData(), adr, FORM_DATA_LEN);
	}

}
