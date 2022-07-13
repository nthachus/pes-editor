package editor.data;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public final class KitsTest extends BaseTest {
	@Test
	public void testAddresses() {
		Assert.assertEquals(742324, Kits.START_ADR);
		Assert.assertEquals(772876, Kits.START_CLUB_ADR);

		Assert.assertEquals(456, Kits.SIZE_NATION);
		Assert.assertEquals(648, Kits.SIZE_CLUB);
	}

	/**
	 * Verify licensed teams.
	 */
	@Test
	public void testIsLicensed() {
		OptionFile of = loadOriginalOF();

		List<Boolean> list = new ArrayList<Boolean>();
		for (int t = 0; t < Kits.TOTAL; t++) {
			list.add(Kits.isLicensed(of, t));
		}

		Assert.assertThat(list, Matchers.hasItems(true, false));
	}

	@Test
	public void testHasLogo() {
		OptionFile of = loadLatestOF();

		List<Boolean> list = new ArrayList<Boolean>();
		List<Integer> logoList = new ArrayList<Integer>();

		for (int t = 0; t < Kits.TOTAL; t++) {
			for (int l = 0; l < Kits.TOTAL_LOGO; l++) {
				list.add(Kits.isLogoUsed(of, t, l));
				logoList.add(Kits.getLogo(of, t, l));
			}
		}

		Assert.assertThat(list, Matchers.hasItems(true, false));

		Assert.assertThat(logoList, Matchers.hasItem(0));
		Matcher<Iterable<? super Integer>> m = Matchers.hasItem(Matchers.greaterThan(0));
		Assert.assertThat(logoList, m);
	}

	// TODO: More tests here!

}
