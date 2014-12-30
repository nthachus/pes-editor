package editor.data;

import org.junit.Assert;
import org.junit.Test;

public final class KitsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(751252, Kits.START_ADR);// NOTE: Kits.START_ADR should be 751254
		Assert.assertEquals(781804, Kits.START_CLUB_ADR);// 781806

		Assert.assertEquals(456, Kits.SIZE_NATION);
		Assert.assertEquals(648, Kits.SIZE_CLUB);
	}

	// TODO: Verify licensed teams

}
