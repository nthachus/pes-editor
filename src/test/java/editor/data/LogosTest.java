package editor.data;

import editor.lang.NullArgumentException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public final class LogosTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(866044, Logos.START_ADR);

		Assert.assertEquals(80, Logos.TOTAL);
		Assert.assertEquals(608, Logos.SIZE);
	}

	@Test
	public void testIsUsed() throws Exception {
		OptionFile of = loadLatestOF();

		List<Boolean> list = new ArrayList<Boolean>();
		for (int l = 0; l < Logos.TOTAL; l++) {
			list.add(Logos.isUsed(of, l));
		}

		Assert.assertThat(list, Matchers.hasItems(true, false));
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullOF() throws Exception {
		Logos.set(null, 0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSaveWithInvalidSlot() throws Exception {
		Logos.set(new OptionFile(), -1, null);
	}

}
