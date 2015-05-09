package editor.data;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

public final class LeaguesTest extends BaseTest {
	private static final String FIRST_LEAGUE = "International League";

	@Test
	public void testAddresses() {
		Assert.assertEquals(84, Leagues.SIZE);
		Assert.assertEquals(9057 - Leagues.SIZE - 1, Leagues.START_ADR);
	}

	@Test
	public void testLoadAndSave() {
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
	public void testImport() {
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
	public void testLoadAllWithNullOF() {
		Leagues.get(null);
	}

	@Test(expected = NullArgumentException.class)
	public void testLoadWithNullOF() {
		Leagues.get(null, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testLoadWithInvalidIndex() {
		Leagues.get(new OptionFile(), -1);
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullOF() {
		Leagues.set(null, 0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSaveWithInvalidIndex() {
		Leagues.set(new OptionFile(), -1, null);
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullSource() {
		Leagues.importData(null, new OptionFile());
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullDest() {
		Leagues.importData(new OptionFile(), null);
	}

}
