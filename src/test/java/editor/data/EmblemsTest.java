package editor.data;

import editor.util.Arrays;
import editor.util.Files;
import editor.util.Images;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class EmblemsTest extends BaseTest {
	@Test
	public void testAddresses() {
		Assert.assertEquals(905872, Emblems.START_ADR);

		Assert.assertEquals(5184, Emblems.SIZE128);
		Assert.assertEquals(2176, Emblems.SIZE16);
	}

	@Test
	public void testGetAllEmblems() throws IOException {
		OptionFile of = loadLatestOF();

		int n16 = Emblems.count16(of);
		int n128 = Emblems.count128(of);
		Assert.assertThat(n16 + n128, Matchers.greaterThan(0));

		List<BufferedImage> list = new ArrayList<BufferedImage>();
		BufferedImage flag;
		for (int i = 0, n = Emblems.TOTAL128 + Emblems.TOTAL16; i < n; i++) {
			flag = (BufferedImage) Emblems.getImage(of, i);
			if (null != flag && !Images.isBlank(flag)) {
				list.add(flag);
			}
		}
		Assert.assertThat(list.size(), Matchers.greaterThanOrEqualTo(n16 + n128));

		saveEmblemAsPNG(of, list);
	}

	private static void saveEmblemAsPNG(OptionFile of, List<BufferedImage> list) throws IOException {
		File tempFs = createTempFile(of.getFilename(), String.format("%d%s%s", 1, Files.EXT_SEPARATOR, IMG_FORMAT));
		boolean res = ImageIO.write(list.get(0), IMG_FORMAT, tempFs);
		Assert.assertTrue(res);

		if (list.size() > 1) {
			tempFs = createTempFile(of.getFilename(),
					String.format("%d%s%s", list.size(), Files.EXT_SEPARATOR, IMG_FORMAT));
			res = ImageIO.write(list.get(list.size() - 1), IMG_FORMAT, tempFs);
			Assert.assertTrue(res);
		}
	}

	@Test
	public void testFixIndexesTable() {
		final String[] ofFilenames = {OF_ORIGINAL, OF_LICENSED, OF_LATEST};
		final String ofFilename = ofFilenames[rand.nextInt(ofFilenames.length)];
		OptionFile of = loadOptionFile(ofFilename);

		byte[] before = Arrays.copyOfRange(of.getData(), Emblems.IDX_TABLE_ADR, Emblems.IDX_TABLE_ADR + Emblems.TOTAL + 2);
		boolean updated = Emblems.fixIndexesTable(of);

		byte[] after = Arrays.copyOfRange(of.getData(), Emblems.IDX_TABLE_ADR, Emblems.IDX_TABLE_ADR + Emblems.TOTAL + 2);
		if (!updated) {
			Assert.assertArrayEquals(before, after);
		} else {
			Assert.assertNotEquals(java.util.Arrays.toString(before), java.util.Arrays.toString(after));
		}
	}

}
