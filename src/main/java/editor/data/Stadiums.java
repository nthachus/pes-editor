package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Strings;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class Stadiums {
	private Stadiums() {
	}

	public static final int TOTAL = 17;

	public static final int NAME_LEN = 60;
	public static final int SIZE = NAME_LEN + 1;

	public static final int START_ADR = OptionFile.blockAddress(2);
	private static final int SWITCH_ADR = START_ADR + SIZE * TOTAL;
	public static final int END_ADR = SWITCH_ADR + TOTAL;

	private static int getOffset(int stadium) {
		if (stadium < 0 || stadium >= TOTAL) {
			throw new IndexOutOfBoundsException("stadium#" + stadium);
		}
		return START_ADR + stadium * SIZE;
	}

	public static String get(OptionFile of, int stadium) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int ofs = getOffset(stadium);
		return Strings.readUTF8(of.getData(), ofs, NAME_LEN);
	}

	public static String[] get(OptionFile of) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		String[] stadiums = new String[TOTAL];
		for (int i = 0; i < TOTAL; i++) {
			stadiums[i] = get(of, i);
		}

		return stadiums;
	}

	public static void set(OptionFile of, int stadium, String name) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int ofs = getOffset(stadium);
		byte[] temp = new byte[SIZE];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(name)) {
			try {
				byte[] sb = name.getBytes(Strings.UTF8);
				System.arraycopy(sb, 0, temp, 0, Math.min(sb.length, NAME_LEN));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}

		System.arraycopy(temp, 0, of.getData(), ofs, temp.length);

		int switchAdr = SWITCH_ADR + stadium;
		of.getData()[switchAdr] = 1;
	}

	public static void importData(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		System.arraycopy(ofSource.getData(), START_ADR, ofDest.getData(), START_ADR, SIZE * (TOTAL + 1));
	}

}
