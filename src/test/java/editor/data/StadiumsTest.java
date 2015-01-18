package editor.data;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

public final class StadiumsTest extends BaseTest {
	private static final String FIRST_STADIUM = "CLUB HOUSE";

	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(61, Stadiums.SIZE);
		Assert.assertEquals(7608, Stadiums.START_ADR);
	}

	@Test
	public void testLoadAndSave() throws Exception {
		OptionFile of = loadLatestOF();

		String[] list = Stadiums.get(of);
		Assert.assertNotNull(list);
		// DEBUG
		log.debug("Loaded {}: {}", "stadiums", list);

		for (String s : list) {
			Assert.assertNotNull(s);
		}
		Assert.assertEquals(FIRST_STADIUM, list[0]);

		String sn = FIRST_STADIUM + " " + getClass().getSimpleName();
		Stadiums.set(of, 0, sn);
		Assert.assertEquals(sn, Stadiums.get(of, 0));

		Stadiums.set(of, 0, null);
		Assert.assertEquals("", Stadiums.get(of, 0));
	}

	@Test
	public void testImport() throws Exception {
		OptionFile of = loadOriginalOF();
		OptionFile of2 = loadLatestOF();

		// first difference stadium name
		int i = 0;
		for (; i < Stadiums.TOTAL; i++) {
			if (!Stadiums.get(of2, i).equals(Stadiums.get(of, i))) {
				break;
			}
		}
		Assert.assertTrue(i < Stadiums.TOTAL);

		Stadiums.importData(of2, of);
		Assert.assertEquals(Stadiums.get(of2, i), Stadiums.get(of, i));
	}

	@Test(expected = NullArgumentException.class)
	public void testLoadAllWithNullOF() throws Exception {
		Stadiums.get(null);
	}

	@Test(expected = NullArgumentException.class)
	public void testLoadWithNullOF() throws Exception {
		Stadiums.get(null, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testLoadWithInvalidIndex() throws Exception {
		Stadiums.get(new OptionFile(), -1);
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullOF() throws Exception {
		Stadiums.set(null, 0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSaveWithInvalidIndex() throws Exception {
		Stadiums.set(new OptionFile(), -1, null);
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullSource() throws Exception {
		Stadiums.importData(null, new OptionFile());
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullDest() throws Exception {
		Stadiums.importData(new OptionFile(), null);
	}

	@Test
	public void testGetName() throws Exception {
		OptionFile of = loadOriginalOF();

		Assert.assertEquals("STADIO OLIMPICO", Stadiums.get(of, 8));
		Assert.assertEquals("SANTIAGO BERNABEU", Stadiums.get(of, 11));
	}
}
