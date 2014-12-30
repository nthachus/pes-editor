package editor.data;

import editor.util.Strings;

import java.util.Arrays;

public final class Leagues {
	private Leagues() {
	}

	public static final int TOTAL = 34;

	public static final int BASE_NAME_LEN = 20;
	public static final int NAME_LEN = 61;
	public static final int SIZE = BASE_NAME_LEN + 1 + NAME_LEN + 2;

	public static final int START_ADR = Stadiums.END_ADR + (Stadiums.SIZE + 1);

	public static int getOffset(int league) {
		return START_ADR + league * SIZE;
	}

	public static String get(OptionFile of, int league) {
		if (null == of) throw new NullPointerException("of");
		if (league < 0 || league >= TOTAL) throw new IndexOutOfBoundsException("league");

		int ofs = getOffset(league);
		int adr = ofs + BASE_NAME_LEN + 1;  // the modified league name

		String name = new String(of.getData(), adr, NAME_LEN, Strings.UTF8);
		name = Strings.fixCString(name);

		if (Strings.isEmpty(name)) {
			name = new String(of.getData(), ofs, BASE_NAME_LEN, Strings.UTF8);
			name = Strings.fixCString(name);
		}

		return name;
	}

	public static String[] get(OptionFile of) {
		if (null == of) throw new NullPointerException("of");

		String[] leagues = new String[TOTAL];
		for (int i = 0; i < TOTAL; i++)
			leagues[i] = get(of, i);

		return leagues;
	}

	public static void set(OptionFile of, int league, String name) {
		if (null == of) throw new NullPointerException("of");
		if (league < 0 || league >= TOTAL) throw new IndexOutOfBoundsException("league");

		byte[] temp = new byte[NAME_LEN + 2];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(name)) {
			byte[] sb = name.getBytes(Strings.UTF8);
			System.arraycopy(sb, 0, temp, 0, Math.min(sb.length, NAME_LEN));
		}

		int ofs = getOffset(league) + BASE_NAME_LEN + 1;

		temp[temp.length - 1] = 1;
		System.arraycopy(temp, 0, of.getData(), ofs, temp.length);
	}

	public static void importData(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) throw new NullPointerException("ofSource");
		if (null == ofDest) throw new NullPointerException("ofDest");

		int ofs = START_ADR + BASE_NAME_LEN + 1;
		for (int i = 0; i < TOTAL; i++) {
			System.arraycopy(ofSource.getData(), ofs, ofDest.getData(), ofs, NAME_LEN + 2);
			ofs += SIZE;
		}
		//System.arraycopy(ofSource.getData(), START_ADR, ofDest.getData(), START_ADR, SIZE * TOTAL);
	}

}
