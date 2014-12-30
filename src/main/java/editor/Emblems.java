package editor;

import editor.data.OptionFile;
import editor.util.Bits;
import editor.util.Images;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Emblems {
	private Emblems() {
	}

	public static final int TYPE_INHERIT = 0;
	public static final int TYPE_16 = 1;
	public static final int TYPE_128 = 2;

	public static final int START_IDX_TABLE_ADR = OptionFile.blockAddress(8) + 4;
	public static final int SIZE_IDX_TABLE = 100;
	public static final byte UNUSED_IDX_VALUE = 0x5D;

	public static final int START_ADR = START_IDX_TABLE_ADR + SIZE_IDX_TABLE;

	//region Image Constants

	public static final int TOTAL16 = 60;
	public static final int TOTAL128 = 30;

	/**
	 * The emblem image with and height (64px x 64px).
	 */
	public static final int IMG_SIZE = 64;
	/**
	 * The hi-res indexed-color image format (8 bits-per-pixel).
	 */
	public static final int BPP128 = 8;
	/**
	 * The low-res indexed-color image format (4 bits-per-pixel).
	 */
	public static final int BPP16 = 4;

	/**
	 * A hi-res club emblem data record length (5184 bytes).
	 */
	private static final int SIZE128 = Images.recordSize(BPP128, IMG_SIZE);

	/**
	 * A low-res club emblem data record length (2176 bytes).
	 */
	private static final int SIZE16 = Images.recordSize(BPP16, IMG_SIZE);

	//endregion

	private static int getOffset(boolean hiRes, int slot) {
		if (slot < 0 || (hiRes && slot >= TOTAL128) || (!hiRes && slot >= TOTAL16))
			throw new IndexOutOfBoundsException("slot");

		if (!hiRes)
			return START_ADR + (TOTAL128 - 1) * SIZE128 - (slot / 2) * SIZE128 + (slot % 2) * SIZE16;
		return START_ADR + slot * SIZE128;
	}

	public static Image get128(OptionFile of, int slot, boolean opaque, boolean small) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(true, slot) + IMG_SIZE;
		return Images.read(of.getData(), IMG_SIZE, BPP128, adr, opaque, small ? 0.5f : 0f);
	}

	public static Image get16(OptionFile of, int slot, boolean opaque, boolean small) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(false, slot) + IMG_SIZE;
		return Images.read(of.getData(), IMG_SIZE, BPP16, adr, opaque, small ? 0.5f : 0f);
	}

	// emblem index table: [hiCount] [lowCount] [..HighResTotal] [..LowResTotal]
	public static boolean set128(OptionFile of, int slot, BufferedImage image) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(true, slot);
		try {
			Images.write(of.getData(), IMG_SIZE, BPP128, adr + IMG_SIZE, image);
			of.getData()[adr] = Bits.toByte(null != image); // is used?

			if (slot == count128(of)) {
				setCount128(of, slot + 1);

				for (int i = 0; i < TOTAL128; i++) {
					int idxAdr = START_IDX_TABLE_ADR + 2 + i;
					if (of.getData()[idxAdr] == UNUSED_IDX_VALUE) {
						of.getData()[idxAdr] = Bits.toByte(slot);

						int id = Clubs.firstFlag + i;
						Bits.toBytes(Bits.toInt16(id), of.getData(), adr + 4);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}

		return true;
	}

	public static boolean set16(OptionFile of, int slot, BufferedImage image) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(false, slot);
		try {
			Images.write(of.getData(), IMG_SIZE, BPP128, adr + IMG_SIZE, image);
			of.getData()[adr] = Bits.toByte(null != image); // is used?

			if (slot == count16(of)) {
				setCount16(of, slot + 1);

				for (int i = 0; i < TOTAL16; i++) {
					int idxAdr = START_IDX_TABLE_ADR + 2 + TOTAL128 + i;
					if (of.getData()[idxAdr] == UNUSED_IDX_VALUE) {
						of.getData()[idxAdr] = Bits.toByte(TOTAL128 + slot);

						int id = Clubs.firstFlag + TOTAL128 + i;
						Bits.toBytes(Bits.toInt16(id), of.getData(), adr + 4);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}

		return true;
	}

	public static int count128(OptionFile of) {
		if (null == of) throw new NullPointerException("of");
		return Bits.toInt(of.getData()[START_IDX_TABLE_ADR]);
	}

	public static void setCount128(OptionFile of, int count) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[START_IDX_TABLE_ADR] = Bits.toByte(count);
	}

	public static int count16(OptionFile of) {
		if (null == of) throw new NullPointerException("of");
		return Bits.toInt(of.getData()[START_IDX_TABLE_ADR + 1]);
	}

	public static void setCount16(OptionFile of, int count) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[START_IDX_TABLE_ADR + 1] = Bits.toByte(count);
	}

	public static int getFree16(OptionFile of) {
		return TOTAL16 - count128(of) * 2 - count16(of);
	}

	public static int getFree128(OptionFile of) {
		return getFree16(of) / 2;
	}

	public static Image getImage(OptionFile of, int emblem) {
		if (null == of) throw new NullPointerException("of");
		if (emblem < 0 || emblem >= TOTAL128 + TOTAL16) throw new IndexOutOfBoundsException("emblem");

		int adr = START_IDX_TABLE_ADR + 2 + emblem;
		int slot = Bits.toInt(of.getData()[adr]);

		if (emblem < TOTAL128)
			return get128(of, slot, false, false);

		return get16(of, slot - TOTAL128, false, false);
	}

	public static int getLoc(OptionFile of, int e) {// TODO: !!!
		int a = START_ADR - 98 + e;
		return Bits.toInt(of.getData()[a]);
	}

	public static void deleteImage(OptionFile of, int e) {
		int a = START_ADR - 98 + e;
		if (e < TOTAL128) {
			delete128(of, of.getData()[a]);
		} else {
			int slot = Bits.toInt(of.getData()[a]) - TOTAL128;
			delete16(of, slot);
		}
	}

	public static byte[] getIndex(OptionFile of, int slot) {
		byte[] index = new byte[2];
		if (slot < TOTAL128) {
			System.arraycopy(of.getData(), START_ADR + (slot * SIZE128) + 4, index, 0, 2);
		} else {
			slot = slot - TOTAL128;
			int startPos = (START_ADR + ((TOTAL128 - 1) * SIZE128))
					- ((slot / 2) * SIZE128) + ((slot % 2) * SIZE16);
			System.arraycopy(of.getData(), startPos + 4, index, 0, 2);
		}
		return index;
	}

	public static void delete16(OptionFile of, int slot) {
		byte[] index = getIndex(of, slot + TOTAL128);

		int si = getInt(of, index) - (Clubs.firstFlag + TOTAL128);
		of.getData()[START_ADR - 68 + si] = UNUSED_IDX_VALUE;
		Clubs.unAssEmblem(of, si + TOTAL128);
		/*
		 * for (int i = 0; i < 138; i++) { byte[] tb = new byte[2]; int a =
		 * 797484 + 6236 + (i 140); int sa = 797492 + 6236 + (i 140); int d =
		 * 111 + i; System.arraycopy(of.data, a, tb, 0 , 2); if (tb[0] ==
		 * index[0] && tb[1] == index[1]) { System.arraycopy(of.getBytes(d), 0,
		 * of.data, a, 2); System.arraycopy(of.getBytes(d), 0, of.data, a + 4,
		 * 2); of.data[sa] = 0; of.data[sa + 1] = 0; } }
		 */
		int source = (START_ADR + ((TOTAL128 - 1) * SIZE128))
				- (((count16(of) - 1) / 2) * SIZE128)
				+ (((count16(of) - 1) % 2) * SIZE16);
		if (slot != count16(of) - 1) {

			int dest = (START_ADR + ((TOTAL128 - 1) * SIZE128))
					- (((slot) / 2) * SIZE128) + ((slot % 2) * SIZE16);
			System.arraycopy(of.getData(), source, of.getData(), dest, SIZE16);
			si = getInt(of, getIndex(of, slot + TOTAL128))
					- (Clubs.firstFlag + TOTAL128);
			of.getData()[START_ADR - 68 + si] = Bits.toByte(slot + TOTAL128);
		}

		for (int i = source; i < source + SIZE16; i++) {
			of.getData()[i] = 0;
		}
		setCount16(of, Bits.toByte(count16(of) - 1));
	}

	public static void delete128(OptionFile of, int slot) {
		byte[] index = getIndex(of, slot);
		int si = getInt(of, index) - Clubs.firstFlag;
		of.getData()[START_ADR - 98 + si] = UNUSED_IDX_VALUE;
		Clubs.unAssEmblem(of, si);
		/*
		 * for (int i = 0; i < 138; i++) { byte[] tb = new byte[2]; int a =
		 * 797484 + 6236 + (i 140); int sa = 797492 + 6236 + (i 140); int d =
		 * 111 + i; System.arraycopy(of.data, a, tb, 0 , 2); if (tb[0] ==
		 * index[0] && tb[1] == index[1]) { System.arraycopy(of.getBytes(d), 0,
		 * of.data, a, 2); System.arraycopy(of.getBytes(d), 0, of.data, a + 4,
		 * 2); of.data[sa] = 0; of.data[sa + 1] = 0; } }
		 */
		int source = START_ADR + ((count128(of) - 1) * SIZE128);
		if (slot != count128(of) - 1) {
			int dest = START_ADR + (slot * SIZE128);
			System.arraycopy(of.getData(), source, of.getData(), dest, SIZE128);
			si = getInt(of, getIndex(of, slot)) - Clubs.firstFlag;
			of.getData()[START_ADR - 98 + si] = Bits.toByte(slot);
		}

		for (int i = source; i < source + SIZE128; i++) {
			of.getData()[i] = 0;
		}
		setCount128(of, Bits.toByte(count128(of) - 1));
	}

	private static int getInt(OptionFile of, byte[] buffer) {
		if (null != buffer && buffer.length > 0) {
			if (buffer.length > 1)
				return (int) Bits.toInt(buffer, 0, buffer.length);
			else
				return Bits.toInt(buffer[0]);
		}
		return 0;
	}

	public static void import16(OptionFile of1, int slot1, OptionFile of2, int slot2) {
		int startPos1 = (START_ADR + ((TOTAL128 - 1) * SIZE128))
				- ((slot1 / 2) * SIZE128) + ((slot1 % 2) * SIZE16);
		int startPos2 = (START_ADR + ((TOTAL128 - 1) * SIZE128))
				- ((slot2 / 2) * SIZE128) + ((slot2 % 2) * SIZE16);
		System.arraycopy(of2.getData(), startPos2 + 64, of1.getData(), startPos1 + 64,
				SIZE16 - 64);

		if (slot1 == count16(of1)) {
			of1.getData()[startPos1] = 1;
			setCount16(of1, (byte) (count16(of1) + 1));

			boolean done = false;
			for (int i = 0; !done && i < TOTAL16; i++) {
				int a = START_ADR - 68 + i;
				if (of1.getData()[a] == UNUSED_IDX_VALUE) {
					of1.getData()[a] = Bits.toByte(TOTAL128 + slot1);
					int id = (Clubs.firstFlag + TOTAL128) + i;
					System.arraycopy(Bits.toBytes(Bits.toInt16(id)), 0, of1.getData(),
							startPos1 + 4, 2);
					done = true;
				}
			}
		}
	}

	public static void import128(OptionFile of1, int slot1, OptionFile of2, int slot2) {
		int startPos1 = START_ADR + (slot1 * SIZE128);
		int startPos2 = START_ADR + (slot2 * SIZE128);
		System.arraycopy(of2.getData(), startPos2 + 64, of1.getData(), startPos1 + 64,
				SIZE128 - 64);

		if (slot1 == count128(of1)) {
			of1.getData()[startPos1] = 1;
			setCount128(of1, (byte) (count128(of1) + 1));
			boolean done = false;
			for (int i = 0; !done && i < TOTAL128; i++) {
				int a = START_ADR - 98 + i;
				if (of1.getData()[a] == UNUSED_IDX_VALUE) {
					of1.getData()[a] = Bits.toByte(slot1);
					int id = Clubs.firstFlag + i;
					System.arraycopy(Bits.toBytes(Bits.toInt16(id)), 0, of1.getData(),
							startPos1 + 4, 2);
					done = true;
				}
			}
		}
	}

}
