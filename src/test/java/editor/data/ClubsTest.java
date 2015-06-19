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
import java.io.IOException;

public final class ClubsTest extends BaseTest {
	@Test
	public void testAddresses() {
		Assert.assertEquals(730872, Clubs.START_ADR);

		Assert.assertEquals(143, Clubs.FIRST_DEF_EMBLEM);
		Assert.assertEquals(348, Clubs.FIRST_EMBLEM);
		Assert.assertEquals(130, Clubs.TOTAL);
	}

	@Test
	public void testGetAndSetName() {
		OptionFile of = loadOriginalOF();
		int cid = rand.nextInt(Clubs.TOTAL);

		String name = Clubs.getName(of, cid);
		Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

		String newName = name.substring(1).trim() + "!";
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

		newName = name.substring(1).trim() + "!";
		Clubs.setAbbrName(of, cid, newName);
		name = Clubs.getAbbrName(of, cid);
		Assert.assertEquals(newName, name);
		//
		Clubs.setAbbrName(of, cid, "");
		name = Clubs.getAbbrName(of, cid);
		Assert.assertThat(name, Matchers.isEmptyOrNullString());
	}

	@Test
	public void testGetEmblem() throws IOException {
		OptionFile of = loadLicensedOF();

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

			if (emblem < Clubs.FIRST_EMBLEM) {
				Assert.assertEquals(Clubs.FIRST_DEF_EMBLEM + c, emblem);
			} else {
				emblem -= Clubs.FIRST_EMBLEM;
				Assert.assertThat(emblem, Matchers.greaterThanOrEqualTo(0));
				Assert.assertThat(emblem, Matchers.lessThan(Emblems.TOTAL16 + Emblems.TOTAL128));

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
	public void testSetEmblem() {
		OptionFile of = loadLatestOF();

		int cid = 0;
		int oldEmblem = Integer.MAX_VALUE;
		while (oldEmblem >= Clubs.FIRST_EMBLEM) {
			cid = rand.nextInt(Clubs.TOTAL);
			oldEmblem = Clubs.getEmblem(of, cid);
		}

		String name = Clubs.getName(of, cid);
		Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

		int n16 = Emblems.count16(of);
		Assert.assertThat(n16, Matchers.greaterThan(0));

		int newEmblem = 0;
		BufferedImage flag = null;
		while (flag == null || Images.isBlank(flag)) {
			newEmblem = rand.nextInt(n16);
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
	public void testGetBackFlag() {
		OptionFile of = loadLatestOF();

		String name;
		for (int c = 0; c < Clubs.TOTAL; c++) {
			name = Clubs.getName(of, c);

			int backFlag = Clubs.getBackFlag(of, c);
			Color c1 = Clubs.getColor(of, c, false);
			Color c2 = Clubs.getColor(of, c, true);
			log.debug("Club [{}] '{}': backFlag #{} ( {} / {} )", c, name, backFlag, c1, c2);

			Assert.assertThat(backFlag, Matchers.greaterThanOrEqualTo(0));
			Assert.assertThat(backFlag, Matchers.lessThan(Clubs.TOTAL_BACK_FLAGS));

			if (backFlag > 0) {
				Assert.assertNotEquals(c1, c2);
			}
		}
	}

	@Test
	public void testSetBackFlag() {
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

	@Test
	public void testGetClubInfo() {
		OptionFile of = loadOriginalOF();
		int cid = 10;

		Assert.assertEquals("MANCHESTER UNITED", Clubs.getName(of, cid));
		Assert.assertEquals("MCU", Clubs.getAbbrName(of, cid));
		Assert.assertEquals(153, Clubs.FIRST_DEF_EMBLEM + cid);
		Assert.assertEquals(Clubs.FIRST_DEF_EMBLEM + cid, Clubs.getEmblem(of, cid));
		Assert.assertEquals(0, Clubs.getBackFlag(of, cid));
		Color bg = new Color(0x8F1016);
		Assert.assertEquals(bg, Clubs.getColor(of, cid, false));
		Assert.assertEquals(bg, Clubs.getColor(of, cid, true));

		int sid = 3;
		Assert.assertEquals(sid, Clubs.getStadium(of, cid));
		Assert.assertEquals("OLD TRAFFORD", Stadiums.get(of, sid));

		cid = 48;

		Assert.assertEquals("JUVENTUS F.C.", Clubs.getName(of, cid));
		Assert.assertEquals("JUV", Clubs.getAbbrName(of, cid));
		Assert.assertEquals(191, Clubs.FIRST_DEF_EMBLEM + cid);
		Assert.assertEquals(Clubs.FIRST_DEF_EMBLEM + cid, Clubs.getEmblem(of, cid));
		Assert.assertEquals(0, Clubs.getBackFlag(of, cid));
		bg = new Color(0x444444);
		Assert.assertEquals(bg, Clubs.getColor(of, cid, false));
		Assert.assertEquals(bg, Clubs.getColor(of, cid, true));

		sid = 6;
		Assert.assertEquals(sid, Clubs.getStadium(of, cid));
		Assert.assertEquals("AMERIGO ATLANTIS", Stadiums.get(of, sid));

		// patched OF
		of = loadLicensedOF();
		cid = 0;

		Assert.assertEquals("ARSENAL", Clubs.getName(of, cid));
		Assert.assertEquals("ARS", Clubs.getAbbrName(of, cid));
		int emblem = Clubs.getEmblem(of, cid) - Clubs.FIRST_EMBLEM;
		Assert.assertThat(emblem, Matchers.greaterThanOrEqualTo(0));
		Assert.assertThat(emblem, Matchers.lessThan(Emblems.TOTAL16));
		Image flag = Emblems.getImage(of, emblem);
		Assert.assertTrue(flag instanceof BufferedImage);
		Assert.assertFalse(Images.isBlank((BufferedImage) flag));
		//
		Assert.assertEquals(0, Clubs.getBackFlag(of, cid));
		Assert.assertEquals(Color.WHITE, Clubs.getColor(of, cid, false));
		Assert.assertEquals(Color.WHITE, Clubs.getColor(of, cid, true));

		sid = 4;
		Assert.assertEquals(sid, Clubs.getStadium(of, cid));
		Assert.assertEquals("ESTADIO GRAN CHACO", Stadiums.get(of, sid));
	}

	// TODO: Exception test-cases
}
