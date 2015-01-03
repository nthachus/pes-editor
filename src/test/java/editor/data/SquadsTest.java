package editor.data;

import org.junit.Assert;
import org.junit.Test;

public class SquadsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(649560, Squads.NATION_NUM_ADR);
		Assert.assertEquals(651285, Squads.CLUB_NUM_ADR);

		Assert.assertEquals(655734, Squads.NATION_ADR);
		Assert.assertEquals(659184, Squads.CLUB_ADR);

		Assert.assertEquals(212, Squads.TOTAL);
		Assert.assertEquals(15, Squads.EXTRA_COUNT);
		Assert.assertEquals(8, Squads.EDIT_TEAM_COUNT);
		Assert.assertEquals(73, Squads.LAST_EDIT_NATION);
		Assert.assertEquals(75, Squads.FIRST_CLUB);
	}

	// TODO: Other test-cases
}
