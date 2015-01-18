package editor.data;

import org.junit.Assert;
import org.junit.Test;

public final class SquadsTest extends BaseTest {
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

	@Test
	public void testGetClassicNation() throws Exception {
		Assert.assertEquals(7, Squads.getClassicNation(7));

		Assert.assertEquals(60, Squads.getClassicNation(6));
		Assert.assertEquals(61, Squads.getClassicNation(8));
		Assert.assertEquals(62, Squads.getClassicNation(9));
		Assert.assertEquals(63, Squads.getClassicNation(14));
		Assert.assertEquals(64, Squads.getClassicNation(15));
		Assert.assertEquals(65, Squads.getClassicNation(44));
		Assert.assertEquals(66, Squads.getClassicNation(45));
	}

	@Test
	public void testGetNationForTeam() throws Exception {
		Assert.assertEquals(6, Squads.getNationForTeam(60));
		Assert.assertEquals(8, Squads.getNationForTeam(61));
		Assert.assertEquals(9, Squads.getNationForTeam(62));
		Assert.assertEquals(14, Squads.getNationForTeam(63));
		Assert.assertEquals(15, Squads.getNationForTeam(64));
		Assert.assertEquals(44, Squads.getNationForTeam(65));
		Assert.assertEquals(45, Squads.getNationForTeam(66));

		Assert.assertEquals(7, Squads.getNationForTeam(7));
	}

	// TODO: Other test-cases
}
