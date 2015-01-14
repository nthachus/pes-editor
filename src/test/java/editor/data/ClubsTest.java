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
	public void testGetAndSetName() throws Exception {
		OptionFile of = loadOriginalOF();
		int cid = rand.nextInt(Clubs.TOTAL);

		String name = Clubs.getName(of, cid);
		Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

		String newName = name + "!";
		Clubs.setName(of, cid, newName);
		name = Clubs.getName(of, cid);
		Assert.assertEquals(newName, name);
		//
		Clubs.setName(of, cid, null);
		name = Clubs.getName(of, cid);
		Assert.assertEquals("", name);

		// abbreviation name
		name = Clubs.getAbbrName(of, cid);
		Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

		newName = name.substring(1) + "!";
		Clubs.setAbbrName(of, cid, newName);
		name = Clubs.getAbbrName(of, cid);
		Assert.assertEquals(newName, name);
		//
		Clubs.setAbbrName(of, cid, "");
		name = Clubs.getAbbrName(of, cid);
		Assert.assertThat(name, Matchers.isEmptyOrNullString());
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
	public void testSetEmblem() throws Exception {
		OptionFile of = loadLatestOF();

		int cid = 0;
		int oldEmblem = Integer.MAX_VALUE;
		while (oldEmblem >= Clubs.FIRST_EMBLEM) {
			cid = rand.nextInt(Clubs.TOTAL);
			oldEmblem = Clubs.getEmblem(of, cid);
		}

		String name = Clubs.getName(of, cid);
		Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

		int n128 = Emblems.count128(of);
		Assert.assertThat(n128, Matchers.greaterThan(0));

		int newEmblem = 0;
		BufferedImage flag = null;
		while (flag == null || Images.isBlank(flag)) {
			newEmblem = rand.nextInt(n128);
			flag = (BufferedImage) Emblems.getImage(of, newEmblem);
		}
		Assert.assertNotNull(flag);
		Assert.assertFalse(Images.isBlank(flag));

		newEmblem += Clubs.FIRST_EMBLEM;
		Clubs.setEmblem(of, cid, newEmblem);
		int emblem = Clubs.getEmblem(of, cid);

		Assert.assertNotEquals(oldEmblem, emblem);
		Assert.assertEquals(newEmblem, emblem);
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

	@Test
	public void testSetBackFlag() throws Exception {
		OptionFile of = loadLatestOF();
		int cid = rand.nextInt(Clubs.TOTAL);

		// update backFlag & colors
		int oldBackFlag = Clubs.getBackFlag(of, cid);
		Color oldC1 = Clubs.getColor(of, cid, false);
		Color oldC2 = Clubs.getColor(of, cid, true);

		int newBackFlag = (oldBackFlag > 0) ? oldBackFlag - 1 : oldBackFlag + 1;
		Color newC1 = randomColor();
		Color newC2 = randomColor();

		Clubs.setBackFlag(of, cid, newBackFlag);
		Clubs.setColor(of, cid, false, newC1);
		Clubs.setColor(of, cid, true, newC2);

		int backFlag = Clubs.getBackFlag(of, cid);
		Color c1 = Clubs.getColor(of, cid, false);
		Color c2 = Clubs.getColor(of, cid, true);

		Assert.assertNotEquals(oldBackFlag, backFlag);
		Assert.assertNotEquals(oldC1, c1);
		Assert.assertNotEquals(oldC2, c2);

		Assert.assertEquals(newBackFlag, backFlag);
		Assert.assertEquals(newC1, c1);
		Assert.assertEquals(newC2, c2);
	}

	// TODO: Other test-cases!

}
