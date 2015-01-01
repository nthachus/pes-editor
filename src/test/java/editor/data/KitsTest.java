package editor.data;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public final class KitsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(751252, Kits.START_ADR);// NOTE: Kits.START_ADR should be 751254
		Assert.assertEquals(781804, Kits.START_CLUB_ADR);// 781806

		Assert.assertEquals(456, Kits.SIZE_NATION);
		Assert.assertEquals(648, Kits.SIZE_CLUB);
	}

	/**
	 * Verify licensed teams.
	 */
	@Test
	public void testIsLicensed() throws Exception {
		OptionFile of = loadOriginalOF();

		List<Boolean> list = new ArrayList<Boolean>();
		for (int t = 0; t < Kits.TOTAL; t++) {
			list.add(Kits.isLicensed(of, t));
		}

		Assert.assertThat(list, Matchers.hasItems(true, false));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHasLogo() throws Exception {
		OptionFile of = loadLatestOF();

		List<Boolean> list = new ArrayList<Boolean>();
		List<Integer> logoList = new ArrayList<Integer>();
		for (int t = 0; t < Kits.TOTAL; t++) {
			for (int l = 0; l < Kits.TOTAL_LOGO_SLOT; l++) {
				list.add(Kits.isLogoUsed(of, t, l));
				logoList.add(Kits.getLogo(of, t, l));
			}
		}

		Assert.assertThat(list, Matchers.hasItems(true, false));
		Assert.assertThat(logoList, Matchers.hasItems(Matchers.is(0), Matchers.greaterThan(0)));
	}

}
