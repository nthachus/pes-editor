package editor.data;

import editor.util.Strings;

import java.util.Arrays;

public final class Stadiums {
	private Stadiums() {
	}

	public static final int TOTAL = 21;

	public static final int NAME_LEN = 60;
	public static final int SIZE = NAME_LEN + 1;

	public static final int START_ADR = OptionFile.blockAddress(2);
	public static final int SWITCH_ADR = START_ADR + SIZE * TOTAL;
	public static final int END_ADR = SWITCH_ADR + TOTAL;

	public static int getOffset(int stadium) {
		return START_ADR + stadium * SIZE;
	}

	public static String get(OptionFile of, int stadium) {
		if (null == of) throw new NullPointerException("of");
		if (stadium < 0 || stadium >= TOTAL) throw new IndexOutOfBoundsException("stadium");

		int ofs = getOffset(stadium);
		String name = new String(of.getData(), ofs, NAME_LEN, Strings.UTF8);
		name = Strings.fixCString(name);

		return name;
	}

	public static String[] get(OptionFile of) {
		if (null == of) throw new NullPointerException("of");

		String[] stadiums = new String[TOTAL];
		for (int i = 0; i < TOTAL; i++)
			stadiums[i] = get(of, i);

		return stadiums;
	}

	public static void set(OptionFile of, int stadium, String name) {
		if (null == of) throw new NullPointerException("of");
		if (stadium < 0 || stadium >= TOTAL) throw new IndexOutOfBoundsException("stadium");

		byte[] temp = new byte[SIZE];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(name)) {
			byte[] sb = name.getBytes(Strings.UTF8);
			System.arraycopy(sb, 0, temp, 0, Math.min(sb.length, NAME_LEN));
		}

		int ofs = getOffset(stadium);
		int switchAdr = SWITCH_ADR + stadium;

		System.arraycopy(temp, 0, of.getData(), ofs, temp.length);
		of.getData()[switchAdr] = 1;
	}

	public static void importData(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) throw new NullPointerException("ofSource");
		if (null == ofDest) throw new NullPointerException("ofDest");

		System.arraycopy(ofSource.getData(), START_ADR, ofDest.getData(), START_ADR, SIZE * (TOTAL + 1));
	}

}
