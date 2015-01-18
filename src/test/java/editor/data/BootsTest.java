package editor.data;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

public final class BootsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(648720, Boots.START_ADR);
		Assert.assertEquals(828, Boots.TOTAL * Boots.SIZE);
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullSource() throws Exception {
		Boots.importData(null, new OptionFile());
	}

	@Test(expected = NullArgumentException.class)
	public void testImportWithNullDest() throws Exception {
		Boots.importData(new OptionFile(), null);
	}

}
