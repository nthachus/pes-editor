package editor.data;

import editor.util.Bits;
import editor.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public final class Logos {
	private static final Logger log = LoggerFactory.getLogger(Logos.class);

	private Logos() {
	}

	/**
	 * There are total 80 football association logos.
	 */
	public static final int TOTAL = 80;
	public static final int START_ADR = Kits.END_ADR;

	/**
	 * The logo image with and height (32px x 32px).
	 */
	public static final int IMG_SIZE = 32;
	public static final int BITS_DEPTH = 4;

	public static final int SIZE = Images.recordSize(BITS_DEPTH, IMG_SIZE);
	//public static final int PALETTE_SIZE = Images.paletteSize(BITS_DEPTH);

	public static final BufferedImage BLANK = (BufferedImage) Images.read(null, IMG_SIZE, BITS_DEPTH, -1, false, 0f);


	private static int getOffset(int slot) {
		if (slot < 0 || slot >= TOTAL) throw new IndexOutOfBoundsException("slot");
		return START_ADR + slot * SIZE;
	}

	public static boolean isUsed(OptionFile of, int slot) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(slot);
		return (of.getData()[adr] != 0);
	}

	public static BufferedImage get(OptionFile of, int slot, boolean opaque) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(slot) + IMG_SIZE;
		return (BufferedImage) Images.read(of.getData(), IMG_SIZE, BITS_DEPTH, adr, opaque, 0f);
	}

	public static boolean set(OptionFile of, int slot, BufferedImage image) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(slot);
		try {
			Images.write(of.getData(), IMG_SIZE, BITS_DEPTH, adr + IMG_SIZE, image);
			of.getData()[adr] = Bits.toByte(null != image); // is used?

		} catch (Exception e) {
			log.error("Failed to save logo:", e);
			return false;
		}

		return true;
	}

	public static void delete(OptionFile of, int slot) {
		if (null == of) throw new NullPointerException("of");

		int adr = getOffset(slot);
		Arrays.fill(of.getData(), adr, adr + SIZE, (byte) 0);
	}

	public static void importData(OptionFile ofSource, int slotSource, OptionFile ofDest, int slotDest) {
		if (null == ofSource) throw new NullPointerException("ofSource");
		if (null == ofDest) throw new NullPointerException("ofDest");

		int aS = getOffset(slotSource);
		int aD = getOffset(slotDest);
		System.arraycopy(ofSource.getData(), aS, ofDest.getData(), aD, SIZE);
	}

}
