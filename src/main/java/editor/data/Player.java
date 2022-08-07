package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player implements Serializable, Comparable<Player> {
	private static final long serialVersionUID = 1536161153505853967L;
	private static final Logger log = LoggerFactory.getLogger(Player.class);

	//region Constants

	public static final int TOTAL_CLUB_PLAYERS = 2985;

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
	public static final int TOTAL_JAPAN_PLAYERS = 7;
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
		if (isInvalidId(player)) {
			throw new IndexOutOfBoundsException("player#" + player);
		}

		if (player >= FIRST_EDIT) {
			return START_EDIT_ADR + (player - FIRST_EDIT) * SIZE;
		}

		return START_ADR + player * SIZE;
	}

	public static boolean isInvalidId(int player) {
		return (player <= 0 || player >= END_EDIT || (player >= TOTAL && player < FIRST_EDIT));
	}

	//endregion

	public static final int NAME_LEN = 32;
	public static final int SHIRT_NAME_LEN = 16;

	private final OptionFile of;
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

	public int compareTo(Player other) {
		if (null == other) {
			return 1;
		}

		int cmp = getName().compareTo(other.getName());
		if (cmp == 0) {
			cmp = Integer.valueOf(Stats.getValue(of, index, Stats.AGE)).compareTo( //NOSONAR java:S1158
					Stats.getValue(of, other.index, Stats.AGE));

			if (cmp == 0) {
				cmp = Integer.valueOf(index).compareTo(other.index); //NOSONAR java:S1158
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

	private static final String SHIRT_NAME_PATTERN = "[^a-zA-Z ._]";

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

	public static void importNames(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		Player pS, pD;
		List<String> cmp;
		for (int p = 1; p < FIRST_UNUSED; p++) {
			pS = new Player(ofSource, p);
			pD = new Player(ofDest, p);

			if (!pS.isEmpty() && !pS.getName().equals(pD.getName())) {
				cmp = comparePlayerStats(ofSource, ofDest, p);
				if (null == cmp) {
					pD.setName(pS.getName());
					pD.setShirtName(pS.getShirtName());
				} else {
					log.warn("Cannot import difference player [{}] '{}' -> '{}' {}", p, pS, pD, cmp);
				}
			}
		}
	}

	private static List<String> comparePlayerStats(OptionFile ofSource, OptionFile ofDest, int player) {
		List<String> dif = new ArrayList<String>();
		int count = 0;

		for (Stat st : Stats.ABILITY99) {
			int vS = Stats.getValue(ofSource, player, st);
			int vD = Stats.getValue(ofDest, player, st);

			if (vS != vD) {
				if (count < 4) {
					dif.add((count == 3) ? "..." : (st + ": " + vS + " -> " + vD));
				}
				count++;
			}
		}

		return (count < Stats.ABILITY99.length / 5) ? null : dif;
	}

}
