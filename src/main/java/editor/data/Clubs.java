package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class Clubs {
	private static final Logger log = LoggerFactory.getLogger(Clubs.class);

	private Clubs() {
	}

	public static final int TOTAL = 130;
	public static final int START_ADR = OptionFile.blockAddress(6);

	/**
	 * A club data record length.
	 */
	public static final int SIZE = 88;

	public static final int NAME_LEN = 48;
	public static final int ABBR_NAME_LEN = 3;

	public static final int FIRST_DEF_EMBLEM = Stats.NATION.length;
	public static final int FIRST_EMBLEM = FIRST_DEF_EMBLEM + Squads.FIRST_CLUB + TOTAL;

	public static final int TOTAL_BACK_FLAGS = 12;

	private static int getOffset(int club) {
		if (club < 0 || club >= TOTAL) {
			throw new IndexOutOfBoundsException("club#" + club);
		}
		return START_ADR + club * SIZE;
	}

	public static int getEmblem(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 60;
		return Bits.toInt16(of.getData(), adr);
	}

	public static void setEmblem(OptionFile of, int club, int index) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 60;

		boolean edited = true;
		if (index < 0) {
			index = club + FIRST_DEF_EMBLEM;
			edited = false;
		}

		short emblem = Bits.toInt16(index);
		Bits.toBytes(emblem, of.getData(), adr);
		Bits.toBytes(emblem, of.getData(), adr + 4);

		setEmblemEdited(of, club, edited);
	}

	public static void setEmblemEdited(OptionFile of, int club, boolean edited) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 68;

		byte sw = Bits.toByte(edited);
		of.getData()[adr] = sw;
		of.getData()[adr + 1] = sw;
	}

	public static void unlinkEmblem(OptionFile of, int emblem) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		for (int i = 0; i < TOTAL; i++) {
			if (emblem == getEmblem(of, i) - FIRST_EMBLEM) {
				setEmblem(of, i, -1);
			}
		}
	}

	public static String[] getNames(OptionFile of) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		String[] clubs = new String[TOTAL];
		for (int c = 0; c < clubs.length; c++) {
			clubs[c] = getName(of, c);
		}
		return clubs;
	}

	public static String getName(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club);

		return Strings.readUTF8(of.getData(), adr, NAME_LEN);
		//if (Strings.isEmpty(name)) name = "<" + club + ">";
	}

	public static void setName(OptionFile of, int club, String name) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club);

		byte[] temp = new byte[NAME_LEN + 1];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(name)) {
			try {
				byte[] buf = name.getBytes(Strings.UTF8);
				System.arraycopy(buf, 0, temp, 0, Math.min(buf.length, NAME_LEN));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}

		System.arraycopy(temp, 0, of.getData(), adr, temp.length);
		setNameEdited(of, club);
	}

	public static void setNameEdited(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 56;
		of.getData()[adr] = 1;
	}

	public static String getAbbrName(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOffset(club) + NAME_LEN + 1;
		return Strings.readANSI(of.getData(), adr, ABBR_NAME_LEN);
	}

	public static void setAbbrName(OptionFile of, int club, String name) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + NAME_LEN + 1;

		byte[] temp = new byte[ABBR_NAME_LEN];
		Arrays.fill(temp, (byte) 0);

		if (!Strings.isEmpty(name)) {
			try {
				byte[] buf = name.getBytes(Strings.ANSI);
				System.arraycopy(buf, 0, temp, 0, Math.min(buf.length, temp.length));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}

		System.arraycopy(temp, 0, of.getData(), adr, temp.length);
		setNameEdited(of, club);
	}

	public static int getStadium(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 81;

		return Bits.toInt(of.getData()[adr]);
	}

	public static void setStadium(OptionFile of, int club, int stadium) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 81;

		of.getData()[adr] = Bits.toByte(stadium);
		setStadiumEdited(of, club);
	}

	public static void setStadiumEdited(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 83;
		of.getData()[adr] = 1;
	}

	public static int getBackFlag(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 70;

		int v = Bits.toInt(of.getData()[adr]);
		// Auto-fix club background-flag ID
		if (v >= TOTAL_BACK_FLAGS) {
			do {
				v >>>= 1;
			} while (v >= TOTAL_BACK_FLAGS);
			of.getData()[adr] = (byte) v;
		}
		return v;
	}

	public static void setBackFlag(OptionFile of, int club, int backFlag) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 70;

		of.getData()[adr] = Bits.toByte(backFlag);
	}

	/**
	 * Background flag colors.
	 */
	public static byte[] getRed(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 72;
		return new byte[]{of.getData()[adr], of.getData()[adr + 4]};
	}

	public static byte[] getGreen(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 73;
		return new byte[]{of.getData()[adr], of.getData()[adr + 4]};
	}

	public static byte[] getBlue(OptionFile of, int club) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 74;
		return new byte[]{of.getData()[adr], of.getData()[adr + 4]};
	}

	public static Color getColor(OptionFile of, int club, boolean second) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 72;
		if (second) {
			adr += 4;
		}

		int r = Bits.toInt(of.getData()[adr]);
		int g = Bits.toInt(of.getData()[adr + 1]);
		int b = Bits.toInt(of.getData()[adr + 2]);

		return new Color(r, g, b);
	}

	public static void setColor(OptionFile of, int club, boolean second, Color color) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		int adr = getOffset(club) + 72;
		if (second) {
			adr += 4;
		}

		byte r = (byte) color.getRed();
		byte g = (byte) color.getGreen();
		byte b = (byte) color.getBlue();

		of.getData()[adr] = r;
		of.getData()[adr + 1] = g;
		of.getData()[adr + 2] = b;
	}

	public static void importNames(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		int adr = START_ADR;
		int len = NAME_LEN + 1 + ABBR_NAME_LEN + 4 + 1;

		for (int i = 0; i < TOTAL; i++) {
			System.arraycopy(ofSource.getData(), adr, ofDest.getData(), adr, len);
			adr += SIZE;
		}
	}

	public static void importData(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		int ofs = NAME_LEN + 1 + ABBR_NAME_LEN + 4 + 1;
		int adr = START_ADR + ofs;
		int len = SIZE - ofs;

		for (int i = 0; i < TOTAL; i++) {
			System.arraycopy(ofSource.getData(), adr, ofDest.getData(), adr, len);
			fixClubEmblem(ofDest, i);
			adr += SIZE;
		}
	}

	private static void fixClubEmblem(OptionFile of, int club) {
		int e = getEmblem(of, club);
		if (e < FIRST_EMBLEM && e != FIRST_DEF_EMBLEM + club) {
			log.warn("Try to fix club #{} emblem: {}", club, e);
			setEmblem(of, club, -1);
		}
	}

	public static void importClub(OptionFile ofSource, int clubSource, OptionFile ofDest, int clubDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		int aS = getOffset(clubSource);
		int aD = getOffset(clubDest);

		int ofs = NAME_LEN + 1 + ABBR_NAME_LEN + 2;
		byte bak = ofDest.getData()[aD + ofs];
		System.arraycopy(ofSource.getData(), aS, ofDest.getData(), aD, SIZE);
		ofDest.getData()[aD + ofs] = bak;

		setNameEdited(ofDest, clubDest);
		setEmblemEdited(ofDest, clubDest, true);
		setStadiumEdited(ofDest, clubDest);
	}

}
