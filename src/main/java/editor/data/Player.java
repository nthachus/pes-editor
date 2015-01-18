package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Player implements Serializable, Comparable<Player> {
	private static final long serialVersionUID = 1536161153505853967L;

	//region Constants

	public static final int TOTAL_CLUB_PLAYERS = 3042;

	/**
	 * The first ID of Classic players.
	 */
	public static final int FIRST_CLASSIC = Squads.NATION_COUNT * Formations.NATION_TEAM_SIZE + 1;
	/**
	 * The first ID of Club players.
	 */
	public static final int FIRST_CLUB = FIRST_CLASSIC + Squads.CLASSIC_COUNT * Formations.NATION_TEAM_SIZE;
	/**
	 * The first ID of Japan plus players (NOTE: Also is the first "PES United" ID).
	 */
	public static final int FIRST_JAPAN = FIRST_CLUB + TOTAL_CLUB_PLAYERS;
	public static final int TOTAL_JAPAN_PLAYERS = 19;
	/**
	 * The first ID of Master League default players.
	 */
	public static final int FIRST_ML = FIRST_JAPAN + TOTAL_JAPAN_PLAYERS;
	public static final int TOTAL_ML_PLAYERS = 28;
	public static final int FIRST_SHOP = FIRST_ML + TOTAL_ML_PLAYERS;
	public static final int TOTAL_SHOP = 160;

	public static final int FIRST_YOUNG = FIRST_SHOP + TOTAL_SHOP;
	/**
	 * NOTE: There are 150 Free-Agents (Old and young players).
	 */
	public static final int TOTAL_YOUNG_PLAYERS = 140;
	public static final int FIRST_OLD = FIRST_YOUNG + TOTAL_YOUNG_PLAYERS;
	public static final int TOTAL_OLD_PLAYERS = 10;
	public static final int FIRST_UNUSED = FIRST_OLD + TOTAL_OLD_PLAYERS;

	/**
	 * The first custom (edited) player ID (32768).
	 */
	public static final int FIRST_EDIT = 0x8000;
	public static final int TOTAL_EDIT = 184;
	public static final int END_EDIT = FIRST_EDIT + TOTAL_EDIT;

	//endregion

	//region Player Offset

	/**
	 * Record size in bytes.
	 */
	public static final int SIZE = 124;

	public static final int START_EDIT_ADR = OptionFile.blockAddress(3);
	public static final int START_ADR = OptionFile.blockAddress(4);

	public static final int TOTAL = FIRST_UNUSED;
	public static final int END_ADR = START_ADR + TOTAL * SIZE;

	/**
	 * Entry point address of a player.
	 * NOTE: We don't have the player at index 0.
	 */
	public static int getOffset(int player) {
		if (player <= 0 || (player >= TOTAL && player < FIRST_EDIT) || player >= END_EDIT) {
			throw new IndexOutOfBoundsException("player#" + player);
		}

		if (player >= FIRST_EDIT) {
			return START_EDIT_ADR + (player - FIRST_EDIT) * SIZE;
		}

		return START_ADR + player * SIZE;
	}

	//endregion

	public static final int NAME_LEN = 32;
	public static final int SHIRT_NAME_LEN = 16;

	private final transient OptionFile of;
	private final int index;
	private final int slotAdr;

	private volatile String name;
	private volatile String shirtName = null;

	public Player(OptionFile of, int index) {
		this(of, index, 0);
	}

	public Player(OptionFile of, int index, int slotAdr) {
		this.name = getName(of, index);
		this.of = of;
		this.index = index;// NOTE: index out of range: <ERROR>
		this.slotAdr = slotAdr;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * Registered player slot address.
	 */
	public int getSlotAdr() {
		return slotAdr;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		return (this == o
				|| (null != o && getClass() == o.getClass() && index == ((Player) o).index));
	}

	@Override
	public int hashCode() {
		return index;
	}

	@SuppressWarnings("NullableProblems")
	public int compareTo(Player other) {
		if (null == other) {
			return 1;
		}

		int cmp = getName().compareTo(other.getName());
		if (cmp == 0) {
			cmp = Integer.valueOf(Stats.getValue(of, index, Stats.AGE)).compareTo(
					Stats.getValue(of, other.index, Stats.AGE));

			if (cmp == 0) {
				cmp = Integer.valueOf(index).compareTo(other.index);
			}
		}

		return cmp;
	}

	public boolean isEmpty() {
		return getName().startsWith("<");
	}

	public static String getName(OptionFile of, int index) {
		if (index == 0) {
			return Resources.getMessage("player.empty");
		}

		if (of == null) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(index);

		String nm = Strings.readUNICODE(of.getData(), adr, NAME_LEN);
		if (!Strings.isEmpty(nm)) {
			return nm;
		} else if (index >= FIRST_EDIT) {
			return Resources.getMessage("player.edited", index - FIRST_EDIT);
		} else if (index >= FIRST_UNUSED) {
			return Resources.getMessage("player.unused", index);
		}

		return Resources.getMessage("player.blank", index);
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		int adr = getOffset(index);

		byte[] temp = new byte[NAME_LEN];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(newName)) {
			try {
				byte[] buf = newName.getBytes(Strings.UNICODE);
				System.arraycopy(buf, 0, temp, 0, Math.min(buf.length, temp.length));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}

		System.arraycopy(temp, 0, of.getData(), adr, temp.length);

		Stats.setValue(of, index, Stats.CALL_NAME, 0xCDCD);
		Stats.setValue(of, index, Stats.NAME_EDITED, 1);
		Stats.setValue(of, index, Stats.CALL_EDITED, 1);

		name = getName(of, index);
	}

	public String getShirtName() {
		if (null == shirtName) {
			int adr = getOffset(index) + NAME_LEN;
			shirtName = Strings.readANSI(of.getData(), adr, SHIRT_NAME_LEN);
		}
		return shirtName;
	}

	public void setShirtName(String newName) {
		int adr = getOffset(index) + NAME_LEN;

		byte[] temp = new byte[SHIRT_NAME_LEN];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(newName)) {
			newName = newName.replaceAll(SHIRT_NAME_PATTERN, "?").trim().toUpperCase();
			try {
				byte[] buf = newName.getBytes(Strings.ANSI);
				System.arraycopy(buf, 0, temp, 0, Math.min(buf.length, temp.length));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}

		System.arraycopy(temp, 0, of.getData(), adr, temp.length);

		Stats.setValue(of, index, Stats.SHIRT_EDITED, 1);
		shirtName = null;
	}

	private static String getSpacesForLength(int len) {
		if (len < 3) {
			return "        ";
		} else if (len == 3) {
			return "    ";
		} else if (len < 6) {
			return "  ";
		} else if (len < 9) {
			return " ";
		}
		return Strings.EMPTY;
	}

	private static final String SHIRT_NAME_PATTERN = "[^a-zA-Z \\._]";

	public static String buildShirtName(String name) {
		if (Strings.isEmpty(name)) {
			return name;
		}

		name = name.trim();
		int len = name.length();
		if (len <= 0) {
			return name;
		}

		String spaces = getSpacesForLength(len);
		name = name.replaceAll(SHIRT_NAME_PATTERN, Strings.EMPTY).toUpperCase();

		StringBuilder result = new StringBuilder();
		len = name.length();
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				result.append(spaces);
			}
			result.append(name.charAt(i));
		}

		return result.toString();
	}

}
