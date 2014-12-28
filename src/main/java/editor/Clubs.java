package editor;

import editor.data.OptionFile;
import editor.util.Bits;

import java.awt.*;
import java.io.UnsupportedEncodingException;

public final class Clubs {
	private Clubs() {
	}

	public static final int TOTAL = 130;

	public static final int startAdr = 739800;
	public static final int size = 88;
	public static final int firstFlag = 349;
	public static final int firstDefEmblem = 144;

	public static int getEmblem(OptionFile of, int club) {
		int adr = startAdr + 60 + size * club;
		return Bits.toInt16(of.getData(), adr);
	}

	public static void importClub(OptionFile of1, int c1, OptionFile of2, int c2) {
		int a1 = startAdr + (size * c1);
		int a2 = startAdr + (size * c2);
		byte t = of1.getData()[a1 + 54];
		System.arraycopy(of2.getData(), a2, of1.getData(), a1, size);
		of1.getData()[a1 + 54] = t;
		setNameEdited(of1, c1);
		setEmblemEdited(of1, c1, true);
		setStadiumEdited(of1, c1);
	}

	public static void setEmblem(OptionFile of, int club, byte[] index) {
		boolean edited = true;
		if (index == null) {
			index = Bits.toBytes(Bits.toInt16(club + firstDefEmblem));
			edited = false;
		}
		int a = startAdr + 60 + (size * club);
		System.arraycopy(index, 0, of.getData(), a, 2);
		System.arraycopy(index, 0, of.getData(), a + 4, 2);
		setEmblemEdited(of, club, edited);
	}

	public static void unAssEmblem(OptionFile of, int emblem) {
		for (int i = 0; i < TOTAL; i++) {
			if (emblem == getEmblem(of, i) - firstFlag) {
				setEmblem(of, i, null);
			}
		}
	}

	public static String[] getNames(OptionFile of) {
		String[] clubs = new String[TOTAL];
		for (int c = 0; c < clubs.length; c++) {
			clubs[c] = getName(of, c);
		}
		return clubs;
	}

	public static String getName(OptionFile of, int c) {
		String club = "";
		int len = 0;
		int a = startAdr + (c * size);
		if (of.getData()[a] != 0) {
			for (int i = 0; i < 49; i++) {
				if (len == 0 && of.getData()[a + i] == 0) {
					len = i;
				}
			}
			try {
				club = new String(of.getData(), a, len, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				club = "<" + String.valueOf(c) + ">";
			}
		} else {
			club = "<" + String.valueOf(c) + ">";
		}
		return club;
	}

	public static String getAbv(OptionFile of, int c) {
		String abv = "";
		int a = startAdr + 49 + (c * size);
		abv = new String(of.getData(), a, 3);
		return abv;
	}

	public static void setAbv(OptionFile of, int c, String text) {
		int a = startAdr + 49 + (c * size);
		byte[] tb = new byte[3];
		byte[] sb = text.getBytes();
		System.arraycopy(sb, 0, tb, 0, 3);
		System.arraycopy(tb, 0, of.getData(), a, 3);
		setNameEdited(of, c);
	}

	public static void setName(OptionFile of, int c, String text) {
		int a = startAdr + (c * size);
		byte[] tb = new byte[49];
		byte[] sb;
		try {
			sb = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			sb = new byte[48];
		}
		if (sb.length <= 48) {
			System.arraycopy(sb, 0, tb, 0, sb.length);
		} else {
			System.arraycopy(sb, 0, tb, 0, 48);
		}
		System.arraycopy(tb, 0, of.getData(), a, 49);
		setNameEdited(of, c);
	}

	public static int getStadium(OptionFile of, int c) {
		int a = startAdr + 81 + (c * size);
		return of.getData()[a];
	}

	public static void setStadium(OptionFile of, int c, int s) {
		int a = startAdr + 81 + (c * size);
		of.getData()[a] = Bits.toByte(s);
		setStadiumEdited(of, c);
	}

	public static byte getBack(OptionFile of, int c) {
		int a = startAdr + 70 + (c * size);
		return of.getData()[a];
	}

	public static void setBack(OptionFile of, int c, int b) {
		int a = startAdr + 70 + (c * size);
		of.getData()[a] = Bits.toByte(b);
		// int sa = 797507 + 6236 + (t * 140);
		// of.data[sa] = 1;
	}

	public static byte[] getRed(OptionFile of, int c) {
		int a = Clubs.startAdr + 72 + (c * Clubs.size);
		byte[] red = new byte[2];
		red[0] = of.getData()[a];
		red[1] = of.getData()[a + 4];
		return red;
	}

	public static byte[] getGreen(OptionFile of, int c) {
		int a = Clubs.startAdr + 73 + (c * Clubs.size);
		byte[] red = new byte[2];
		red[0] = of.getData()[a];
		red[1] = of.getData()[a + 4];
		return red;
	}

	public static byte[] getBlue(OptionFile of, int c) {
		int a = Clubs.startAdr + 74 + (c * Clubs.size);
		byte[] red = new byte[2];
		red[0] = of.getData()[a];
		red[1] = of.getData()[a + 4];
		return red;
	}

	public static Color getColor(OptionFile of, int c, boolean two) {
		int a = startAdr + 72 + (c * size);
		if (two) a += 4;

		int red = Bits.toInt(of.getData()[a]);
		int green = Bits.toInt(of.getData()[a + 1]);
		int blue = Bits.toInt(of.getData()[a + 2]);

		return new Color(red, green, blue);
	}

	public static void setColor(OptionFile of, int c, boolean two, Color color) {
		int a = startAdr + 72 + (c * size);
		if (two) {
			a = a + 4;
		}
		byte r = (byte) color.getRed();
		byte g = (byte) color.getGreen();
		byte b = (byte) color.getBlue();
		of.getData()[a] = r;
		of.getData()[a + 1] = g;
		of.getData()[a + 2] = b;
	}

	public static void importNames(OptionFile of1, OptionFile of2) {
		for (int i = 0; i < TOTAL; i++) {
			System.arraycopy(of2.getData(), startAdr + (i * size), of1.getData(),
					startAdr + (i * size), 57);
		}
	}

	public static void importData(OptionFile of1, OptionFile of2) {
		for (int i = 0; i < TOTAL; i++) {
			System.arraycopy(of2.getData(), startAdr + (i * size) + 57, of1.getData(),
					startAdr + (i * size) + 57, 31);
		}
	}

	public static void setNameEdited(OptionFile of, int c) {
		int a = startAdr + (c * size) + 56;
		of.getData()[a] = 1;
	}

	public static void setEmblemEdited(OptionFile of, int c, boolean e) {
		byte sw = 0;
		if (e) {
			sw = 1;
		}
		int sa = startAdr + 68 + (size * c);
		of.getData()[sa] = sw;
		of.getData()[sa + 1] = sw;
	}

	public static void setStadiumEdited(OptionFile of, int c) {
		int a = startAdr + 83 + (c * size);
		of.getData()[a] = 1;
	}

}
