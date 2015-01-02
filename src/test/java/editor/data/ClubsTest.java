package editor.data;

import org.junit.Assert;
import org.junit.Test;

public final class ClubsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(739800, Clubs.START_ADR);

		Assert.assertEquals(144, Clubs.FIRST_DEF_EMBLEM);
		Assert.assertEquals(349, Clubs.FIRST_EMBLEM);
		Assert.assertEquals(130, Clubs.TOTAL);
	}

	// TODO: Other test-cases!
}
