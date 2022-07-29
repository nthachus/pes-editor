package editor.data;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public final class SquadsTest extends BaseTest {
	@Test
	public void testAddresses() throws NoSuchFieldException, IllegalAccessException {
		Assert.assertEquals(649560, Squads.NATION_NUM_ADR);
		Assert.assertEquals(651285, Squads.CLUB_NUM_ADR);

		Assert.assertEquals(655734, Squads.NATION_ADR);
		Assert.assertEquals(659184, Squads.CLUB_ADR);

		Assert.assertEquals(212, Squads.TOTAL);
		Assert.assertEquals(15, Squads.EXTRA_COUNT);
		Assert.assertEquals(8, Squads.EDIT_TEAM_COUNT);
		Assert.assertEquals(73, Squads.LAST_EDIT_NATION);
		Assert.assertEquals(75, Squads.FIRST_CLUB);
		Assert.assertEquals(Squads.LAST_CLUB, Squads.FIRST_CLUB + Clubs.TOTAL);

		Integer firstClubSlot = (Integer) readStaticField(Squads.class, "FIRST_CLUB_SLOT", true, true);
		Assert.assertEquals(3450 / 2, firstClubSlot.intValue());
		Assert.assertEquals(3450, Squads.CLUB_ADR - Squads.NATION_ADR);
		Integer totalSlots = (Integer) readStaticField(Squads.class, "TOTAL_SLOTS", true, true);
		Assert.assertEquals(4448, totalSlots - firstClubSlot);
		Assert.assertEquals(8896, Squads.END_ADR - Squads.CLUB_ADR);
	}

	@Test
	public void testGetClassicNation() {
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
	public void testGetNationForTeam() {
		Assert.assertEquals(6, Squads.getNationForTeam(60));
		Assert.assertEquals(8, Squads.getNationForTeam(61));
		Assert.assertEquals(9, Squads.getNationForTeam(62));
		Assert.assertEquals(14, Squads.getNationForTeam(63));
		Assert.assertEquals(15, Squads.getNationForTeam(64));
		Assert.assertEquals(44, Squads.getNationForTeam(65));
		Assert.assertEquals(45, Squads.getNationForTeam(66));

		Assert.assertEquals(7, Squads.getNationForTeam(7));
	}

	@Test
	public void testPlayersInClub() {
		OptionFile of = loadOriginalOF();

		int club = 7;// MU
		int team = club + Squads.FIRST_CLUB;

		int teamSize = Squads.getTeamSize(team);
		Assert.assertEquals(32, teamSize);

		teamSize = Squads.countPlayers(of, team);
		Assert.assertEquals(27, teamSize);

		// GK
		int slot = 0;
		int p = Squads.getTeamPlayer(of, team, slot);
		Assert.assertThat(p, Matchers.greaterThan(0));
		String pName = Player.getName(of, p);
		Assert.assertEquals("DE GEA", pName);
		//
		int no = Squads.getTeamSquadNum(of, team, slot);
		Assert.assertEquals(1, no);

		// CB
		slot = 1;
		p = Squads.getTeamPlayer(of, team, slot);
		Assert.assertThat(p, Matchers.greaterThan(0));
		pName = Player.getName(of, p);
		Assert.assertEquals("CARRICK", pName);
		//
		no = Squads.getTeamSquadNum(of, team, slot);
		Assert.assertEquals(16, no);

		slot = 16;
		p = Squads.getTeamPlayer(of, team, slot);
		Assert.assertThat(p, Matchers.greaterThan(0));
		pName = Player.getName(of, p);
		Assert.assertEquals("RAFAEL", pName);
		//
		no = Squads.getTeamSquadNum(of, team, slot);
		Assert.assertEquals(2, no);
	}

	// testPlayersInNationTeam()
	// TODO: Other test-cases

}
