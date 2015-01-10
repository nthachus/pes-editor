package editor.data;

import editor.util.Files;
import editor.util.Images;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class ClubsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(739800, Clubs.START_ADR);

		Assert.assertEquals(144, Clubs.FIRST_DEF_EMBLEM);
		Assert.assertEquals(349, Clubs.FIRST_EMBLEM);
		Assert.assertEquals(130, Clubs.TOTAL);
	}

	@Test
	public void testGetEmblem() throws Exception {
		OptionFile of = loadLatestOF();

		String name, abv;
		BufferedImage flag;
		File tempFs;
		for (int c = 0; c < Clubs.TOTAL; c++) {

			name = Clubs.getName(of, c);
			Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

			abv = Clubs.getAbbrName(of, c);
			Assert.assertThat(abv, Matchers.not(Matchers.isEmptyOrNullString()));

			int emblem = Clubs.getEmblem(of, c);
			log.debug("Club [{}] '{}' - '{}': emblem #{}", c, abv, name, emblem);

			if (emblem < Clubs.FIRST_EMBLEM)
				Assert.assertEquals(Clubs.FIRST_DEF_EMBLEM + c, emblem);
			else {
				emblem -= Clubs.FIRST_EMBLEM;
				Assert.assertThat(emblem, Matchers.greaterThanOrEqualTo(0));
				Assert.assertThat(emblem, Matchers.lessThan(Emblems.TOTAL16));

				flag = (BufferedImage) Emblems.getImage(of, emblem);
				Assert.assertNotNull(flag);
				Assert.assertFalse(Images.isBlank(flag));

				tempFs = createTempFile(of.getFilename(), abv + "-emblem#" + emblem + Files.EXT_SEPARATOR + IMG_FORMAT);
				boolean res = ImageIO.write(flag, IMG_FORMAT, tempFs);
				Assert.assertTrue(res);
			}
		}
	}

	@Test
	public void testGetBackFlag() throws Exception {
		OptionFile of = loadLatestOF();

		String name;
		for (int c = 0; c < Clubs.TOTAL; c++) {
			name = Clubs.getName(of, c);

			int backFlag = Clubs.getBackFlag(of, c);
			Color c1 = Clubs.getColor(of, c, false);
			Color c2 = Clubs.getColor(of, c, true);
			log.debug("Club [{}] '{}': backFlag #{} ( {} / {} )", c, name, backFlag, c1, c2);

			Assert.assertThat(backFlag, Matchers.greaterThanOrEqualTo(0));
			Assert.assertThat(backFlag, Matchers.lessThan(12));

			if (backFlag > 0)
				Assert.assertNotEquals(c1, c2);
		}
	}

	// TODO: Other test-cases! testGetBackFlag(), testSetBackFlag( +colors )

}
