package editor;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.data.Stats;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Player implements Serializable, Comparable<Player> {
	private static final long serialVersionUID = 1L;

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
		if (player <= 0 || (player >= TOTAL && player < FIRST_EDIT) || player >= FIRST_EDIT + TOTAL_EDIT)
			throw new IndexOutOfBoundsException("player");

		if (player >= FIRST_EDIT)
			return START_EDIT_ADR + (player - FIRST_EDIT) * SIZE;

		return START_ADR + player * SIZE;
	}

	//endregion

	private final OptionFile of;

	public String name;
	public int index;
	public int adr;

	public Player(OptionFile of, int index, int squadNumAdr) {
		if (of == null) throw new NullPointerException();
		this.of = of;

		boolean end;
		this.index = index;
		adr = squadNumAdr;
		if (index == 0) {
			name = "<empty>";
		} else if (index < 0 || (index >= TOTAL && index < FIRST_EDIT) || index > 32951) {
			name = "<ERROR>";
			this.index = 0;
		} else {
			// adr = 31568 + (i * 124);
			int a = START_ADR;
			int offSet = index * 124;
			if (index >= FIRST_EDIT) {
				// adr = 8744 + (i * 124);
				a = START_EDIT_ADR;
				offSet = (index - FIRST_EDIT) * 124;
			}
			byte[] nameBytes = new byte[32];
			System.arraycopy(of.getData(), a + offSet, nameBytes, 0, 32);
			end = false;
			int len = 0;
			for (int j = 0; !end && j < nameBytes.length - 1; j = j + 2) {
				if (nameBytes[j] == 0 && nameBytes[j + 1] == 0) {
					end = true;
					len = j;
				}
			}
			try {
				name = new String(nameBytes, 0, len, "UTF-16LE");
			} catch (UnsupportedEncodingException e) {
				name = "<Error " + Integer.toString(this.index) + ">";
			}

			if (name.equals("") && this.index >= FIRST_EDIT) {
				// name = "<???>";
				name = "<Edited " + Integer.toString(this.index - FIRST_EDIT) + ">";
			} else if (name.equals("")) {
				if (this.index >= FIRST_UNUSED) {
					name = "<Unused " + Integer.toString(this.index) + ">";
				} else {
					name = "<L " + Integer.toString(this.index) + ">";
				}
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public int compareTo(Player other) {
		if (null == other)
			return 1;

		int cmp = name.compareTo(other.name);
		if (cmp == 0) {
			cmp = Integer.compare(Stats.getValue(of, index, Stats.AGE),
					Stats.getValue(of, other.index, Stats.AGE));
		}
		return cmp;
	}

	public void setName(String newName) {
		int len = newName.length();
		if (index != 0 && len <= 15) {
			byte[] newNameBytes = new byte[32];
			byte[] t;
			try {
				t = newName.getBytes("UTF-16LE");
			} catch (UnsupportedEncodingException e) {
				t = new byte[30];
			}
			if (t.length <= 30) {
				System.arraycopy(t, 0, newNameBytes, 0, t.length);
			} else {
				System.arraycopy(t, 0, newNameBytes, 0, 30);
			}
			int a = START_ADR;
			int offSet = index * 124;
			if (index >= FIRST_EDIT) {
				// adr = 8744 + (i * 124);
				a = START_EDIT_ADR;
				offSet = (index - FIRST_EDIT) * 124;
			}
			System.arraycopy(newNameBytes, 0, of.getData(), a + offSet, 32);
			// of.data[a + offSet + 48] = -51;
			// of.data[a + offSet + 49] = -51;
			Stats.setValue(of, index, Stats.CALL_NAME, 0xcdcd);

			Stats.setValue(of, index, Stats.NAME_EDITED, 1);
			Stats.setValue(of, index, Stats.CALL_EDITED, 1);
			// of.data[a + offSet + 50] = 1;
			name = newName;
		}
	}

	public String getShirtName() {
		String sn = "";
		int a = START_ADR + 32 + (index * 124);
		if (index >= FIRST_EDIT) {
			a = START_EDIT_ADR + 32 + ((index - FIRST_EDIT) * 124);
		}
		if (of.getData()[a] != 0) {
			byte[] sb = new byte[16];
			System.arraycopy(of.getData(), a, sb, 0, 16);
			for (int i = 0; i < 16; i++) {
				if (sb[i] == 0) {
					sb[i] = 33;
				}
			}
			sn = new String(sb);
			sn = sn.replaceAll("!", "");
		}
		return sn;
	}

	public void setShirtName(String n) {
		if (n.length() < 16 && index != 0) {
			int a = START_ADR + 32 + (index * 124);
			if (index >= FIRST_EDIT) {
				a = START_EDIT_ADR + 32 + ((index - FIRST_EDIT) * 124);
			}
			byte[] t = new byte[16];
			n = n.toUpperCase();
			byte[] nb = n.getBytes();
			for (int i = 0; i < nb.length; i++) {
				if ((nb[i] < 65 || nb[i] > 90) && nb[i] != 46 && nb[i] != 32
						&& nb[i] != 95) {
					nb[i] = 32;
				}
			}
			System.arraycopy(nb, 0, t, 0, nb.length);
			System.arraycopy(t, 0, of.getData(), a, 16);
			Stats.setValue(of, index, Stats.SHIRT_EDITED, 1);
		}
	}

	public void makeShirt(String n) {
		// System.out.println(n);
		String result = "";
		String spaces = "";
		int len = n.length();
		if (len < 9 && len > 5) {
			spaces = " ";
		} else if (len < 6 && len > 3) {
			spaces = "  ";
		} else if (len == 3) {
			spaces = "    ";
		} else if (len == 2) {
			spaces = "        ";
		}
		n = n.toUpperCase();
		byte[] nb = n.getBytes();
		for (int i = 0; i < nb.length; i++) {
			if ((nb[i] < 65 || nb[i] > 90) && nb[i] != 46 && nb[i] != 32
					&& nb[i] != 95) {
				nb[i] = 32;
			}
		}
		n = new String(nb);
		// System.out.println(n);
		for (int i = 0; i < n.length() - 1; i++) {
			result = result + n.substring(i, i + 1) + spaces;
		}
		result = result + n.substring(n.length() - 1, n.length());
		setShirtName(result);
	}

}
