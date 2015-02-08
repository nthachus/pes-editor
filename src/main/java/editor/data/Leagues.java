package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Strings;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class Leagues {
	private Leagues() {
	}

	public static final int TOTAL = 33;

	private static final int BASE_NAME_LEN = 20;
	public static final int NAME_LEN = 61;
	public static final int SIZE = BASE_NAME_LEN + 1 + NAME_LEN + 2;

	public static final int START_ADR = Stadiums.END_ADR + (Stadiums.SIZE + 1);

	private static int getOffset(int league) {
		if (league < 0 || league >= TOTAL) {
			throw new IndexOutOfBoundsException("league#" + league);
		}
		return START_ADR + league * SIZE;
	}

	public static String get(OptionFile of, int league) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int ofs = getOffset(league);
		int adr = ofs + BASE_NAME_LEN + 1;  // the modified league name

		String name = Strings.readUTF8(of.getData(), adr, NAME_LEN);
		if (Strings.isEmpty(name)) {
			name = Strings.readUTF8(of.getData(), ofs, BASE_NAME_LEN);
		}
		return name;
	}

	public static String[] get(OptionFile of) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		String[] leagues = new String[TOTAL];
		for (int i = 0; i < TOTAL; i++) {
			leagues[i] = get(of, i);
		}

		return leagues;
	}

	public static void set(OptionFile of, int league, String name) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int ofs = getOffset(league) + BASE_NAME_LEN + 1;
		byte[] temp = new byte[NAME_LEN + 2];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(name)) {
			try {
				byte[] sb = name.getBytes(Strings.UTF8);
				System.arraycopy(sb, 0, temp, 0, Math.min(sb.length, NAME_LEN));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}

		temp[temp.length - 1] = 1;
		System.arraycopy(temp, 0, of.getData(), ofs, temp.length);
	}

	public static void importData(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		int ofs = START_ADR + BASE_NAME_LEN + 1;
		for (int i = 0; i < TOTAL; i++) {
			System.arraycopy(ofSource.getData(), ofs, ofDest.getData(), ofs, NAME_LEN + 2);
			ofs += SIZE;
		}
		//System.arraycopy(ofSource.getData(), START_ADR, ofDest.getData(), START_ADR, SIZE * TOTAL);
	}

}
