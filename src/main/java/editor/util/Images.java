package editor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;
import java.util.List;

public final class Images {
	private static final Logger log = LoggerFactory.getLogger(Images.class);

	private Images() {
	}

	public static int paletteSize(int bitsPerPixel) {
		if (bitsPerPixel < 0 || bitsPerPixel == 3 || (bitsPerPixel > 4 && (bitsPerPixel % 8) != 0))
			throw new IllegalArgumentException("bitsPerPixel");

		return (1 << bitsPerPixel);
	}

	public static int bitsPerPixel(int colorsCount) {
		int bpp = (int) Math.ceil(Math.log(colorsCount) / Math.log(2));
		if (bpp <= 0) bpp = 1;
		else if (bpp == 3) bpp = 4;
		else if (bpp > 4 && (bpp % 8) != 0) bpp = (bpp + 7) / 8;
		return bpp;
	}

	public static int rasterDataSize(int bitsPerPixel, int imgSize) {
		return rasterDataSize(bitsPerPixel, imgSize, imgSize);
	}

	public static int rasterDataSize(int bitsPerPixel, int imgWidth, int imgHeight) {
		return ((imgWidth * bitsPerPixel + 7) / 8) * imgHeight;
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

	public static boolean isBlank(BufferedImage image) {
		if (null == image) return true;

		ColorModel colorModel = image.getColorModel();
		if (null == colorModel) return true;
		if (!(colorModel instanceof IndexColorModel)) return false;

		// colors palette
		IndexColorModel palette = (IndexColorModel) colorModel;
		int pSize = palette.getMapSize();
		if (pSize <= 0) return true;

		for (int i = 0, n = Math.min(pSize, 2); i < n; i++) {
			if (palette.getRGB(i) != 0) return false;
		}

		return true;
	}

	public static boolean saveComponentAsImage(Component comp, File out) {
		if (null == comp) throw new NullPointerException("comp");
		if (null == out) throw new NullPointerException("out");
		String format = Files.getExtension(out);
		if (Strings.isBlank(format)) throw new IllegalArgumentException("out");

		Graphics2D g2 = null;
		try {
			Dimension size = comp.getSize();
			BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

			g2 = img.createGraphics();
			comp.paint(g2);

			if (Files.PNG.equalsIgnoreCase(format))
				img = rgbToIndexedColor(img);

			return ImageIO.write(img, format.toLowerCase(), out);

		} catch (Exception e) {
			log.error("Failed to save Component " + comp + " as image: " + out, e);
		} finally {
			if (null != g2) g2.dispose();
		}

		return false;
	}

	// NOTE: Optimize PNG with pngtastic before saving
	public static BufferedImage rgbToIndexedColor(BufferedImage image) {
		if (null == image)
			return null;

		if (image.getType() != BufferedImage.TYPE_INT_RGB && image.getType() != BufferedImage.TYPE_INT_ARGB)
			return image;

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {

				int rgb = image.getRGB(x, y);
				int cnt = (map.containsKey(rgb) ? map.get(rgb) : 0) + 1;
				map.put(rgb, cnt);
			}
		}

		if (map.size() > 256)
			return image;

		// sort the palette
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		log.debug("Sorted palette: {}", list);

		int bitsDepth = bitsPerPixel(list.size());
		int paletteSize = paletteSize(bitsDepth);

		int[] palette = new int[paletteSize];
		for (int i = 0; i < list.size(); i++)
			palette[i] = list.get(i).getKey();

		int rasterSize = rasterDataSize(bitsDepth, image.getWidth(), image.getHeight());
		DataBuffer buf = new DataBufferByte(rasterSize);
		SampleModel sampleMod = new MultiPixelPackedSampleModel(
				DataBuffer.TYPE_BYTE, image.getWidth(), image.getHeight(), bitsDepth);
		WritableRaster raster = Raster.createWritableRaster(sampleMod, buf, null);

		ColorModel colorMod = new IndexColorModel(bitsDepth, palette.length, palette, 0,
				image.getType() == BufferedImage.TYPE_INT_ARGB, -1, DataBuffer.TYPE_BYTE);
		BufferedImage img = new BufferedImage(colorMod, raster, false, null);

		Graphics2D g2 = null;
		try {
			g2 = img.createGraphics();
			if (g2.drawImage(image, 0, 0, null))
				return img;
		} finally {
			if (null != g2) g2.dispose();
		}

		return image;
	}

}
