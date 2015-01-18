package editor.data;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

public final class LeaguesTest extends BaseTest {
	private static final String FIRST_LEAGUE = "International League";

	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(84, Leagues.SIZE);
		Assert.assertEquals(9057 - Leagues.SIZE - 1, Leagues.START_ADR);
	}

	@Test
	public void testLoadAndSave() throws Exception {
		OptionFile of = loadLatestOF();

		String[] list = Leagues.get(of);
		Assert.assertNotNull(list);
		// DEBUG
		log.debug("Loaded {}: {}", "leagues", list);

		for (String s : list) {
			Assert.assertNotNull(s);
		}
		Assert.assertEquals(FIRST_LEAGUE, list[0]);

		String ln = FIRST_LEAGUE + " " + getClass().getSimpleName();
		Leagues.set(of, 0, ln);
		Assert.assertEquals(ln, Leagues.get(of, 0));
	}

	@Test
	public void testImport() throws Exception {
		OptionFile of = loadOriginalOF();
		OptionFile of2 = loadLatestOF();

		// first difference league name
		int i = 0;
		for (; i < Leagues.TOTAL; i++) {
			if (!Leagues.get(of2, i).equals(Leagues.get(of, i))) {
				break;
			}
		}
		Assert.assertTrue(i < Leagues.TOTAL);

		Leagues.importData(of2, of);
		Assert.assertEquals(Leagues.get(of2, i), Leagues.get(of, i));
	}

	@Test(expected = NullArgumentException.class)
	public void testLoadAllWithNullOF() throws Exception {
		Leagues.get(null);
	}

	@Test(expected = NullArgumentException.class)
	public void testLoadWithNullOF() throws Exception {
		Leagues.get(null, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testLoadWithInvalidIndex() throws Exception {
		Leagues.get(new OptionFile(), -1);
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullOF() throws Exception {
		Leagues.set(null, 0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSaveWithInvalidIndex() throws Exception {
		Leagues.set(new OptionFile(), -1, null);
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullSource() throws Exception {
		Leagues.importData(null, new OptionFile());
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullDest() throws Exception {
		Leagues.importData(new OptionFile(), null);
	}

}
