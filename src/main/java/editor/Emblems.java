package editor;

import editor.data.OptionFile;
import editor.util.Bits;

import java.awt.*;
import java.awt.image.*;

public final class Emblems {
	private Emblems() {
	}

	public static final int TYPE_INHERIT = 0;
	public static final int TYPE_16 = 1;
	public static final int TYPE_128 = 2;

	public static final int START_ADR = 914800;

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
	 * 2 ^ BitsPerPixel (256).
	 */
	private static final int PALETTE_SIZE128 = (1 << BPP128);
	/**
	 * 4096 bytes.
	 */
	public static final int RASTER_SIZE128 = ((IMG_SIZE * BPP128 + 7) / 8) * IMG_SIZE;

	/**
	 * A hi-res club emblem data record length (5184 bytes).
	 * NOTE: Emblem header size is 64 bytes ?
	 */
	private static final int SIZE128 = RASTER_SIZE128 + 4 * PALETTE_SIZE128 + IMG_SIZE;

	/**
	 * 2 ^ BitsPerPixel (16).
	 */
	private static final int PALETTE_SIZE16 = (1 << BPP16);
	/**
	 * 2048 bytes.
	 */
	public static final int RASTER_SIZE16 = ((IMG_SIZE * BPP16 + 7) / 8) * IMG_SIZE;

	/// <summary>
	/// A low-res club emblem data record length (2176 bytes).
	/// </summary>
	private static final int SIZE16 = RASTER_SIZE16 + 4 * PALETTE_SIZE16 + IMG_SIZE;

	//endregion

	private static int getOffset(boolean hiRes, int slot) {
		if (!hiRes)
			return START_ADR + (TOTAL128 - 1) * SIZE128 - (slot / 2) * SIZE128 + (slot % 2) * SIZE16;
		return START_ADR + slot * SIZE128;
	}

	private static int total(boolean hiRes) {
		return hiRes ? TOTAL128 : TOTAL16;
	}

	private static int paletteSize(boolean hiRes) {
		return hiRes ? PALETTE_SIZE128 : PALETTE_SIZE16;
	}

	private static int rasterDataSize(boolean hiRes) {
		return hiRes ? RASTER_SIZE128 : RASTER_SIZE16;
	}

	private static int bitsPerPixel(boolean hiRes) {
		return hiRes ? BPP128 : BPP16;
	}

	private static Image get(OptionFile of, boolean hiRes, int slot, boolean opaque, boolean small) {
		if (null == of) throw new NullPointerException("of");

		byte[] red = new byte[paletteSize(hiRes)];
		byte[] green = new byte[red.length];
		byte[] blue = new byte[red.length];
		byte[] alpha = new byte[red.length];
		byte[] pixel = new byte[rasterDataSize(hiRes)];

		if (slot >= 0 && slot < total(hiRes)) {
			int startPos = getOffset(hiRes, slot);
			int adr = startPos + IMG_SIZE;

			for (int c = 0; c < red.length; c++) {
				red[c] = of.getData()[adr++];
				green[c] = of.getData()[adr++];
				blue[c] = of.getData()[adr++];
				alpha[c] = of.getData()[adr++];
			}

			System.arraycopy(of.getData(), adr, pixel, 0, pixel.length);
			if (opaque) {
				for (int i = 0; i < alpha.length; i++)
					alpha[i] = -1;
			}
		}

		DataBuffer data = new DataBufferByte(pixel, pixel.length, 0);
		int bpp = bitsPerPixel(hiRes);
		SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, IMG_SIZE, IMG_SIZE, bpp);
		WritableRaster raster = Raster.createWritableRaster(sampleModel, data, null);

		ColorModel colorModel = new IndexColorModel(bpp, red.length, red, green, blue, alpha);
		BufferedImage img = new BufferedImage(colorModel, raster, false, null);

		if (small)
			return img.getScaledInstance(IMG_SIZE / 2, IMG_SIZE / 2, Image.SCALE_DEFAULT);
		return img;
	}

	public static Image get128(OptionFile of, int slot, boolean opaque, boolean small) {
		return get(of, true, slot, opaque, small);
	}

	public static Image get16(OptionFile of, int slot, boolean opaque, boolean small) {
		return get(of, false, slot, opaque, small);
	}

	public static boolean set128(OptionFile of, int slot, BufferedImage image) {// TODO: !!!
		boolean ok = false;
		try {
			byte[] red = new byte[256];
			byte[] green = new byte[256];
			byte[] blue = new byte[256];
			byte[] alpha = new byte[256];
			int[] pix = new int[RASTER_SIZE128];
			int a;
			Raster rast = image.getData();
			ColorModel colorMod = image.getColorModel();

			if (colorMod instanceof IndexColorModel) {
				IndexColorModel index = (IndexColorModel) colorMod;
				if (image.getWidth() == 64 && image.getHeight() == 64) {
					rast.getPixels(0, 0, 64, 64, pix);
					index.getReds(red);
					index.getGreens(green);
					index.getBlues(blue);
					index.getAlphas(alpha);

					if (alpha[0] != 0) {
						int swap = 0;
						byte tr;
						byte tg;
						byte tb;
						byte ta;
						for (int c = 1; c < 128; c++) {
							if (swap == 0 && alpha[c] == 0) {
								swap = c;
							}
						}
						if (swap != 0) {
							tr = red[0];
							tg = green[0];
							tb = blue[0];
							ta = alpha[0];
							// System.out.println(red[3] + ", " + green[3] + ",
							// " + blue[3]);
							red[0] = red[swap];
							green[0] = green[swap];
							blue[0] = blue[swap];
							alpha[0] = 0;
							red[swap] = tr;
							green[swap] = tg;
							blue[swap] = tb;
							alpha[swap] = ta;

							for (int p = 0; p < RASTER_SIZE128; p++) {
								if (pix[p] == 0) {
									pix[p] = swap;
								} else if (pix[p] == swap) {
									pix[p] = 0;
								}
							}
							// System.out.println(swap);
						}
					}

					for (int c = 0; c < PALETTE_SIZE128; c++) {
						a = START_ADR + 64 + (slot * SIZE128) + (c * 4);
						of.getData()[a] = red[c];
						of.getData()[a + 1] = green[c];
						of.getData()[a + 2] = blue[c];
						of.getData()[a + 3] = alpha[c];
						if (alpha[c] != -1) {
						}
					}

					for (int p = 0; p < RASTER_SIZE128; p++) {
						a = START_ADR + 1088 + (slot * SIZE128) + p;
						of.getData()[a] = Bits.toByte(pix[p]);
					}
					of.getData()[START_ADR + (slot * SIZE128)] = 1;

					if (slot == count128(of)) {
						setCount128(of, (byte) (count128(of) + 1));

						boolean done = false;
						for (int i = 0; !done && i < TOTAL128; i++) {
							a = START_ADR - 98 + i;
							if (of.getData()[a] == 93) {
								of.getData()[a] = Bits.toByte(slot);
								int id = Clubs.firstFlag + i;
								System.arraycopy(Bits.toBytes(Bits.toInt16(id)), 0, of.getData(),
										START_ADR + (slot * SIZE128) + 4, 2);
								done = true;
							}
						}
					}
					ok = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok;
	}

	public static boolean set16(OptionFile of, int slot, BufferedImage image) {
		boolean ok = false;
		try {
			byte[] red = new byte[256];
			byte[] green = new byte[256];
			byte[] blue = new byte[256];
			byte[] alpha = new byte[256];
			int[] pix = new int[RASTER_SIZE128];
			int a;
			int startPos = (START_ADR + ((TOTAL128 - 1) * SIZE128))
					- ((slot / 2) * SIZE128) + ((slot % 2) * SIZE16);
			Raster rast = image.getData();
			ColorModel colorMod = image.getColorModel();

			if (colorMod instanceof IndexColorModel) {
				IndexColorModel index = (IndexColorModel) colorMod;
				if (image.getWidth() == 64 && image.getHeight() == 64) {
					rast.getPixels(0, 0, 64, 64, pix);
					index.getReds(red);
					index.getGreens(green);
					index.getBlues(blue);
					index.getAlphas(alpha);

					if (alpha[0] != 0) {
						int swap = 0;
						byte tr;
						byte tg;
						byte tb;
						byte ta;
						for (int c = 1; c < 16; c++) {
							if (swap == 0 && alpha[c] == 0) {
								swap = c;
							}
						}
						if (swap != 0) {
							tr = red[0];
							tg = green[0];
							tb = blue[0];
							ta = alpha[0];
							// System.out.println(red[3] + ", " + green[3] + ",
							// " + blue[3]);
							red[0] = red[swap];
							green[0] = green[swap];
							blue[0] = blue[swap];
							alpha[0] = 0;
							red[swap] = tr;
							green[swap] = tg;
							blue[swap] = tb;
							alpha[swap] = ta;

							for (int p = 0; p < RASTER_SIZE128; p++) {
								if (pix[p] == 0) {
									pix[p] = swap;
								} else if (pix[p] == swap) {
									pix[p] = 0;
								}
							}
							// System.out.println(swap);
						}
					}

					for (int c = 0; c < PALETTE_SIZE16; c++) {
						a = startPos + 64 + (c * 4);
						of.getData()[a] = red[c];
						of.getData()[a + 1] = green[c];
						of.getData()[a + 2] = blue[c];
						of.getData()[a + 3] = alpha[c];
						if (alpha[c] != -1) {
						}
					}

					for (int p = 0; p < RASTER_SIZE128; p = p + 2) {
						a = startPos + 128 + (p / 2);
						of.getData()[a] = Bits.toByte((pix[p] << 4) | (pix[p + 1]));
					}
					of.getData()[startPos] = 1;

					if (slot == count16(of)) {
						setCount16(of, (byte) (count16(of) + 1));

						boolean done = false;
						for (int i = 0; !done && i < TOTAL16; i++) {
							a = START_ADR - 68 + i;
							if (of.getData()[a] == 93) {
								of.getData()[a] = Bits.toByte(TOTAL128 + slot);
								int id = Clubs.firstFlag + TOTAL128 + i;
								System.arraycopy(Bits.toBytes(Bits.toInt16(id)), 0, of.getData(),
										startPos + 4, 2);
								done = true;
							}
						}
					}
					ok = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok;
	}

	public static int count128(OptionFile of) {
		if (null == of) throw new NullPointerException("of");
		return Bits.toInt(of.getData()[START_ADR - 100]);
	}

	public static int count16(OptionFile of) {
		if (null == of) throw new NullPointerException("of");
		return Bits.toInt(of.getData()[START_ADR - 99]);
	}

	public static void setCount16(OptionFile of, int count) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[START_ADR - 99] = Bits.toByte(count);
	}

	public static void setCount128(OptionFile of, int count) {
		if (null == of) throw new NullPointerException("of");
		of.getData()[START_ADR - 100] = Bits.toByte(count);
	}

	public static byte getFree16(OptionFile of) {
		return (byte) (TOTAL16 - (count128(of) * 2) - count16(of));
	}

	public static byte getFree128(OptionFile of) {
		return (byte) ((TOTAL16 - (count128(of) * 2) - count16(of)) / 2);
	}

	public static Image getImage(OptionFile of, int e) {
		Image image = null;
		int a = START_ADR - 98 + e;
		if (e < TOTAL128) {
			image = get128(of, of.getData()[a], false, false);
		} else {
			int slot = Bits.toInt(of.getData()[a]) - TOTAL128;
			image = get16(of, slot, false, false);
		}
		return image;
	}

	public static int getLoc(OptionFile of, int e) {
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
		of.getData()[START_ADR - 68 + si] = 93;
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
		of.getData()[START_ADR - 98 + si] = 93;
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
				if (of1.getData()[a] == 93) {
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
				if (of1.getData()[a] == 93) {
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
