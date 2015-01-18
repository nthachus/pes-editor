package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public final class Emblems {
	private static final Logger log = LoggerFactory.getLogger(Emblems.class);

	private Emblems() {
	}

	public static final int TOTAL16 = 60;
	public static final int TOTAL128 = 30;

	private static final int START_IDX_TABLE_ADR = OptionFile.blockAddress(8) + 4;
	private static final int SIZE_IDX_TABLE = 2 + TOTAL128 + TOTAL16 + 8;
	private static final byte UNUSED_IDX_VALUE = 0x5D;

	public static final int START_ADR = START_IDX_TABLE_ADR + SIZE_IDX_TABLE;

	/**
	 * The emblem image with and height (64px x 64px).
	 */
	public static final int IMG_SIZE = 64;
	/**
	 * The hi-res indexed-color image format (8 bits-per-pixel).
	 */
	private static final int BPP128 = 8;
	/**
	 * The low-res indexed-color image format (4 bits-per-pixel).
	 */
	private static final int BPP16 = 4;

	/**
	 * A hi-res club emblem data record length (5184 bytes).
	 */
	public static final int SIZE128 = Images.recordSize(BPP128, IMG_SIZE);

	/**
	 * A low-res club emblem data record length (2176 bytes).
	 */
	public static final int SIZE16 = Images.recordSize(BPP16, IMG_SIZE);

	public static final int PALETTE_SIZE16 = Images.paletteSize(BPP16);
	public static final int PALETTE_SIZE128 = Images.paletteSize(BPP128);

	public static final Image BLANK16 = Images.read(null, IMG_SIZE, BPP16, -1, false, 0f);
	public static final Image BLANK_SMALL = Images.read(null, IMG_SIZE, BPP16, -1, false, 0.58f);


	private static int getOffset(boolean hiRes, int slot) {
		if (slot < 0 || (hiRes && slot >= TOTAL128) || (!hiRes && slot >= TOTAL16)) {
			throw new IndexOutOfBoundsException("slot#" + slot);
		}

		if (!hiRes) {
			return START_ADR + (TOTAL128 - 1) * SIZE128 - (slot / 2) * SIZE128 + (slot % 2) * SIZE16;
		}
		return START_ADR + slot * SIZE128;
	}

	public static Image get128(OptionFile of, int slot, boolean opaque, boolean small) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOffset(true, slot) + IMG_SIZE;
		return Images.read(of.getData(), IMG_SIZE, BPP128, adr, opaque, small ? 0.58f : 0f);
	}

	public static Image get16(OptionFile of, int slot, boolean opaque, boolean small) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOffset(false, slot) + IMG_SIZE;
		return Images.read(of.getData(), IMG_SIZE, BPP16, adr, opaque, small ? 0.58f : 0f);
	}

	// emblem index table: [hiCount] [lowCount] [..HighResTotal] [..LowResTotal]
	private static int getIndexOffset(boolean hiRes, int slot) {
		int ofs = START_IDX_TABLE_ADR + 2 + slot;
		if (!hiRes) {
			ofs += TOTAL128;
		}
		return ofs;
	}

	public static boolean set128(OptionFile of, int slot, BufferedImage image) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOffset(true, slot);
		try {
			Images.write(of.getData(), IMG_SIZE, BPP128, adr + IMG_SIZE, image);
			of.getData()[adr] = Bits.toByte(null != image); // is used?

			if (slot == count128(of)) {
				setCount128(of, slot + 1);

				for (int i = 0; i < TOTAL128; i++) {
					int idxAdr = getIndexOffset(true, i);
					if (of.getData()[idxAdr] == UNUSED_IDX_VALUE) {
						of.getData()[idxAdr] = Bits.toByte(slot);

						int id = Clubs.FIRST_EMBLEM + i;
						Bits.toBytes(Bits.toInt16(id), of.getData(), adr + 4);
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to save emblem 128:", e);
			return false;
		}

		return true;
	}

	public static boolean set16(OptionFile of, int slot, BufferedImage image) {
		if (null == of) {
			throw new NullArgumentException("of");
		}

		int adr = getOffset(false, slot);
		try {
			Images.write(of.getData(), IMG_SIZE, BPP128, adr + IMG_SIZE, image);
			of.getData()[adr] = Bits.toByte(null != image); // is used?

			if (slot == count16(of)) {
				setCount16(of, slot + 1);

				for (int i = 0; i < TOTAL16; i++) {
					int idxAdr = getIndexOffset(false, i);
					if (of.getData()[idxAdr] == UNUSED_IDX_VALUE) {
						of.getData()[idxAdr] = Bits.toByte(TOTAL128 + slot);

						int id = Clubs.FIRST_EMBLEM + TOTAL128 + i;
						Bits.toBytes(Bits.toInt16(id), of.getData(), adr + 4);
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to save emblem 16:", e);
			return false;
		}

		return true;
	}

	public static int count128(OptionFile of) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		return Bits.toInt(of.getData()[START_IDX_TABLE_ADR]);
	}

	private static void setCount128(OptionFile of, int count) {
		//if (null == of) { throw new NullArgumentException("of"); }
		of.getData()[START_IDX_TABLE_ADR] = Bits.toByte(count);
	}

	public static int count16(OptionFile of) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		return Bits.toInt(of.getData()[START_IDX_TABLE_ADR + 1]);
	}

	private static void setCount16(OptionFile of, int count) {
		//if (null == of) { throw new NullArgumentException("of"); }
		of.getData()[START_IDX_TABLE_ADR + 1] = Bits.toByte(count);
	}

	public static int getFree16(OptionFile of) {
		return TOTAL16 - count128(of) * 2 - count16(of);
	}

	public static int getFree128(OptionFile of) {
		return getFree16(of) / 2;
	}

	public static Image getImage(OptionFile of, int emblem) {
		int slot = getLocation(of, emblem);
		if (slot < 0) {
			return BLANK16;
		}

		if (emblem < TOTAL128) {
			return get128(of, slot, false, false);
		}

		return get16(of, slot - TOTAL128, false, false);
	}

	public static int getLocation(OptionFile of, int emblem) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (emblem < 0 || emblem >= TOTAL128 + TOTAL16) {
			throw new IndexOutOfBoundsException("emblem#" + emblem);
		}

		int adr = getIndexOffset(true, emblem);
		byte idx = of.getData()[adr];
		return (idx == UNUSED_IDX_VALUE) ? -1 : Bits.toInt(idx);
	}

	public static void deleteImage(OptionFile of, int emblem) {
		int slot = getLocation(of, emblem);
		if (slot < 0) {
			return;
		}

		if (emblem < TOTAL128) {
			delete128(of, slot);
		} else {
			delete16(of, slot - TOTAL128);
		}
	}

	public static int getIndex(OptionFile of, int slot) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (slot < 0 || slot >= TOTAL128 + TOTAL16) {
			throw new IndexOutOfBoundsException("slot#" + slot);
		}

		int adr = (slot < TOTAL128) ? getOffset(true, slot) : getOffset(false, slot - TOTAL128);
		return Bits.toInt16(of.getData(), adr + 4);
	}

	public static void delete16(OptionFile of, int slot) {
		int index = getIndex(of, slot + TOTAL128);
		int sourceIdx = index - (Clubs.FIRST_EMBLEM + TOTAL128);

		int adr = getIndexOffset(false, sourceIdx);
		of.getData()[adr] = UNUSED_IDX_VALUE;
		Clubs.unlinkEmblem(of, sourceIdx + TOTAL128);

		int newCount = count16(of) - 1;
		int source = getOffset(false, newCount);
		if (slot != newCount) {

			int dest = getOffset(false, slot);
			System.arraycopy(of.getData(), source, of.getData(), dest, SIZE16);

			index = getIndex(of, slot + TOTAL128);
			sourceIdx = index - (Clubs.FIRST_EMBLEM + TOTAL128);
			adr = getIndexOffset(false, sourceIdx);
			of.getData()[adr] = Bits.toByte(slot + TOTAL128);
		}

		Arrays.fill(of.getData(), source, source + SIZE16, (byte) 0);
		setCount16(of, newCount);
	}

	public static void delete128(OptionFile of, int slot) {
		int index = getIndex(of, slot);
		int sourceIdx = index - Clubs.FIRST_EMBLEM;

		int adr = getIndexOffset(true, sourceIdx);
		of.getData()[adr] = UNUSED_IDX_VALUE;
		Clubs.unlinkEmblem(of, sourceIdx);

		int newCount = count128(of) - 1;
		int source = getOffset(true, newCount);
		if (slot != newCount) {

			int dest = getOffset(true, slot);
			System.arraycopy(of.getData(), source, of.getData(), dest, SIZE128);

			index = getIndex(of, slot);
			sourceIdx = index - Clubs.FIRST_EMBLEM;
			adr = getIndexOffset(true, sourceIdx);
			of.getData()[adr] = Bits.toByte(slot);
		}

		Arrays.fill(of.getData(), source, source + SIZE128, (byte) 0);
		setCount128(of, newCount);
	}

	public static void importData16(OptionFile ofSource, int slotSource, OptionFile ofDest, int slotDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		int adrSource = getOffset(false, slotSource);
		int adrDest = getOffset(false, slotDest);
		System.arraycopy(ofSource.getData(), adrSource + IMG_SIZE,
				ofDest.getData(), adrDest + IMG_SIZE, SIZE16 - IMG_SIZE);

		int cntDest = count16(ofDest);
		if (slotDest == cntDest) {
			ofDest.getData()[adrDest] = 1;// is used
			setCount16(ofDest, cntDest + 1);

			int adr = getIndexOffset(false, 0);
			for (int i = 0; i < TOTAL16; i++, adr++) {
				if (ofDest.getData()[adr] == UNUSED_IDX_VALUE) {

					ofDest.getData()[adr] = Bits.toByte(TOTAL128 + slotDest);
					int id = (Clubs.FIRST_EMBLEM + TOTAL128) + i;
					Bits.toBytes(Bits.toInt16(id), ofDest.getData(), adrDest + 4);
					break;
				}
			}
		}
	}

	public static void importData128(OptionFile ofSource, int slotSource, OptionFile ofDest, int slotDest) {
		if (null == ofSource) {
			throw new NullArgumentException("ofSource");
		}
		if (null == ofDest) {
			throw new NullArgumentException("ofDest");
		}

		int adrSource = getOffset(true, slotSource);
		int adrDest = getOffset(true, slotDest);
		System.arraycopy(ofSource.getData(), adrSource + IMG_SIZE,
				ofDest.getData(), adrDest + IMG_SIZE, SIZE128 - IMG_SIZE);

		int cntDest = count128(ofDest);
		if (slotDest == cntDest) {
			ofDest.getData()[adrDest] = 1;// is used
			setCount128(ofDest, cntDest + 1);

			int adr = getIndexOffset(true, 0);
			for (int i = 0; i < TOTAL128; i++, adr++) {
				if (ofDest.getData()[adr] == UNUSED_IDX_VALUE) {

					ofDest.getData()[adr] = Bits.toByte(slotDest);
					int id = Clubs.FIRST_EMBLEM + i;
					Bits.toBytes(Bits.toInt16(id), ofDest.getData(), adrDest + 4);
					break;
				}
			}
		}
	}

}
