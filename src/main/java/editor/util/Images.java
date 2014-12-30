package editor.util;

import java.awt.*;
import java.awt.image.*;

public final class Images {
	private Images() {
	}

	private static int paletteSize(int bitsPerPixel) {
		if (bitsPerPixel < 0 || bitsPerPixel == 3 || (bitsPerPixel > 4 && (bitsPerPixel % 8) != 0))
			throw new IllegalArgumentException("bitsPerPixel");

		return (1 << bitsPerPixel);
	}

	private static int rasterDataSize(int bitsPerPixel, int imgSize) {
		return ((imgSize * bitsPerPixel + 7) / 8) * imgSize;
	}

	// NOTE: Image header size is IMG_SIZE bytes ?
	public static int recordSize(int bitsPerPixel, int imgSize) {
		return rasterDataSize(bitsPerPixel, imgSize) + 4 * paletteSize(bitsPerPixel) + imgSize;
	}

	public static Image read(byte[] data, int imgSize, int bitsDepth, int offset, boolean opaque, float scale) {
		if (null == data) throw new NullPointerException("data");

		byte[] red = new byte[paletteSize(bitsDepth)];
		byte[] green = new byte[red.length];
		byte[] blue = new byte[red.length];
		byte[] alpha = new byte[red.length];
		byte[] pixel = new byte[rasterDataSize(bitsDepth, imgSize)];

		if (offset >= 0) {
			int adr = offset;

			for (int c = 0; c < red.length; c++) {
				red[c] = data[adr++];
				green[c] = data[adr++];
				blue[c] = data[adr++];
				alpha[c] = data[adr++];
			}

			System.arraycopy(data, adr, pixel, 0, pixel.length);
			if (opaque) {
				for (int i = 0; i < alpha.length; i++)
					alpha[i] = -1;
			}
		}

		DataBuffer buf = new DataBufferByte(pixel, pixel.length, 0);
		SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, imgSize, imgSize, bitsDepth);
		WritableRaster raster = Raster.createWritableRaster(sampleModel, buf, null);

		ColorModel colorModel = new IndexColorModel(bitsDepth, red.length, red, green, blue, alpha);
		BufferedImage img = new BufferedImage(colorModel, raster, false, null);

		if (scale > 0) {
			int newSize = Math.round(scale * imgSize);
			return img.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT);
		}

		return img;
	}

	public static void write(byte[] data, int imgSize, int bitsDepth, int offset, BufferedImage image) {
		if (null == data) throw new NullPointerException("data");
		int paletteSize = paletteSize(bitsDepth);
		int rasterSize = rasterDataSize(bitsDepth, imgSize);

		if (null == image) {
			java.util.Arrays.fill(data, offset, offset + rasterSize + 4 * paletteSize, (byte) 0);
			return;
		}

		if (image.getWidth() < imgSize || image.getHeight() < imgSize)
			throw new IllegalArgumentException("image");

		WritableRaster raster = image.getRaster();
		if (null == raster) throw new NullPointerException("raster");

		ColorModel colorModel = image.getColorModel();
		if (null == colorModel || !(colorModel instanceof IndexColorModel))
			throw new NullPointerException("palette");
		// colors palette
		IndexColorModel palette = (IndexColorModel) colorModel;
		if (palette.getMapSize() > paletteSize) throw new IllegalArgumentException("palette");

		byte[] red = new byte[paletteSize];
		byte[] green = new byte[red.length];
		byte[] blue = new byte[red.length];
		byte[] alpha = new byte[red.length];
		int[] pix = new int[imgSize * imgSize];

		raster.getPixels(0, 0, imgSize, imgSize, pix);
		palette.getReds(red);
		palette.getGreens(green);
		palette.getBlues(blue);
		palette.getAlphas(alpha);

		// move transparent to the first of the colors palette
		fixColorsPalette(red, green, blue, alpha, pix);

		// flush image data
		int adr = offset;
		for (int c = 0; c < red.length; c++) {
			data[adr++] = red[c];
			data[adr++] = green[c];
			data[adr++] = blue[c];
			data[adr++] = alpha[c];
		}

		DataBufferByte buf = new DataBufferByte(rasterSize);
		SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, imgSize, imgSize, bitsDepth);
		WritableRaster rasterData = Raster.createWritableRaster(sampleModel, buf, null);
		rasterData.setPixels(0, 0, imgSize, imgSize, pix);

		System.arraycopy(buf.getData(), 0, data, adr, buf.getSize());
	}

	private static void fixColorsPalette(byte[] red, byte[] green, byte[] blue, byte[] alpha, int[] pix) {
		// move transparent to the first of the colors palette
		if (alpha[0] == 0)
			return;

		int swap = 0;
		for (int i = 1; i < alpha.length; i++) {
			if (alpha[i] == 0) {
				swap = i;
				break;
			}
		}

		if (swap <= 0)
			return;

		byte tr = red[0];
		byte tg = green[0];
		byte tb = blue[0];
		byte ta = alpha[0];

		red[0] = red[swap];
		green[0] = green[swap];
		blue[0] = blue[swap];
		alpha[0] = 0;

		red[swap] = tr;
		green[swap] = tg;
		blue[swap] = tb;
		alpha[swap] = ta;

		for (int p = 0; p < pix.length; p++) {
			if (pix[p] == 0) {
				pix[p] = swap;
			} else if (pix[p] == swap) {
				pix[p] = 0;
			}
		}
	}

}
