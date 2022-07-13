package editor.data;

import editor.lang.NullArgumentException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public final class LogosTest extends BaseTest {
	@Test
	public void testAddresses() {
		Assert.assertEquals(857116, Logos.START_ADR);

		Assert.assertEquals(80, Logos.TOTAL);
		Assert.assertEquals(608, Logos.SIZE);
	}

	@Test
	public void testIsUsed() {
		OptionFile of = loadLicensedOF();

		List<Boolean> list = new ArrayList<Boolean>();
		for (int l = 0; l < Logos.TOTAL; l++) {
			list.add(Logos.isUsed(of, l));
		}

		Assert.assertThat(list, Matchers.hasItems(true, false));
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullOF() {
		Logos.set(null, 0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSaveWithInvalidSlot() {
		Logos.set(new OptionFile(), -1, null);
	}

}
