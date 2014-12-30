package editor.util;

import editor.data.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;

public final class ImagesTest extends BaseTest {
	private static final String IMG_4BPP = "/mu_logo-64x4.png";

	@Test
	public void testReadAndWrite() throws Exception {
		File fs = getResourceFile(IMG_4BPP);
		BufferedImage img = ImageIO.read(fs);
		Assert.assertNotNull(img);

		ColorModel colorModel = img.getColorModel();
		Assert.assertTrue(colorModel instanceof IndexColorModel);

		int bpp = (int) (Math.log(((IndexColorModel) colorModel).getMapSize()) / Math.log(2));
		int imgSize = img.getWidth();

		byte[] buffer = new byte[Images.recordSize(bpp, imgSize)];
		Images.write(buffer, imgSize, bpp, imgSize, img);

		BufferedImage img2 = (BufferedImage) Images.read(buffer, imgSize, bpp, imgSize, false, 0f);
		Assert.assertNotNull(img2);

		File tempFs = createTempFile(fs, IMG_FORMAT);
		boolean res = ImageIO.write(img2, IMG_FORMAT, tempFs);
		Assert.assertTrue(res);
	}

}
