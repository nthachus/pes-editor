package editor;

import editor.data.OptionFile;
import editor.data.Stats;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Player implements Serializable, Comparable {
	/**
	 * Record size in bytes.
	 */
	public static final int SIZE = 124;

	public static final int START_ADR = 34920;
	public static final int START_EDIT_ADR = 12092;
	//public static int firstJapan = 4485;
	public static final int firstML = 4603;
	public static final int firstShop = 4631;
	public static final int firstYoung = 4791;
	public static final int firstOld = 4931;
	public static final int FIRST_UNUSED = 4941;
	public static final int FIRST_EDIT = 32768;
	public static final int TOTAL = 4941;
	public static final int TOTAL_EDIT = 184;
	public static final int TOTAL_SHOP = 160;
	public static final int firstClassic = 1381;
	public static final int firstClub = 1542;
	public static final int firstPESUnited = 4584;

	public String name;
	public int index;
	public int adr;
	private OptionFile of;

	public Player(OptionFile opf, int i, int sa) {
		of = opf;
		if (of == null)
			throw new NullPointerException();
		boolean end;
		index = i;
		adr = sa;
		if (i == 0) {
			name = "<empty>";
		} else if (i < 0 || (i >= TOTAL && i < FIRST_EDIT) || i > 32951) {
			name = "<ERROR>";
			index = 0;
		} else {
			// adr = 31568 + (i * 124);
			int a = START_ADR;
			int offSet = i * 124;
			if (i >= FIRST_EDIT) {
				// adr = 8744 + (i * 124);
				a = START_EDIT_ADR;
				offSet = (i - FIRST_EDIT) * 124;
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
				name = "<Error " + String.valueOf(index) + ">";
			}

			if (name.equals("") && index >= FIRST_EDIT) {
				// name = "<???>";
				name = "<Edited " + String.valueOf(index - FIRST_EDIT) + ">";
			} else if (name.equals("")) {
				if (index >= FIRST_UNUSED) {
					name = "<Unused " + String.valueOf(index) + ">";
				} else {
					name = "<L " + String.valueOf(index) + ">";
				}
			}
		}
	}

	public String toString() {
		return name;
	}

	public int compareTo(Object o) {
		Player n = (Player) o;
		int cmp = name.compareTo(n.name);
		if (cmp == 0) {
			cmp = new Integer(Stats.getValue(of, index, Stats.AGE))
					.compareTo(new Integer(Stats.getValue(of, n.index, Stats.AGE)));
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

	public static int getOffset(int player) {
		if (player >= FIRST_EDIT)
			return START_EDIT_ADR + (player - FIRST_EDIT) * SIZE;

		return START_ADR + player * SIZE;
	}

}
