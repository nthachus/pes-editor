package editor.data;

import editor.ui.PlayerTransferable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public final class PlayerTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(34920, Player.START_ADR);
		Assert.assertEquals(12092, Player.START_EDIT_ADR);

		Assert.assertEquals(1381, Player.FIRST_CLASSIC);
		Assert.assertEquals(1542, Player.FIRST_CLUB);
		Assert.assertEquals(4584, Player.FIRST_JAPAN);
		Assert.assertEquals(4603, Player.FIRST_ML);
		Assert.assertEquals(4631, Player.FIRST_SHOP);
		Assert.assertEquals(4791, Player.FIRST_YOUNG);
		Assert.assertEquals(4931, Player.FIRST_OLD);
		Assert.assertEquals(4941, Player.FIRST_UNUSED);

		Assert.assertEquals(0x8000, Player.FIRST_EDIT);
		Assert.assertEquals(184, Player.TOTAL_EDIT);
		Assert.assertEquals(4941, Player.TOTAL);
	}

	@Test
	public void testTransferable() throws Exception {
		Assert.assertNotNull(PlayerTransferable.getDataFlavor());
	}

	@Test
	public void testGetAndSetName() throws Exception {
		OptionFile of = loadOriginalOF();

		int pid = 2;
		Player p = new Player(of, pid);
		Assert.assertEquals("DRAGOVIĆ", p.getName());
		Assert.assertEquals("DRAGOVIC", p.getShirtName());

		pid = 148;
		p = new Player(of, pid);
		Assert.assertEquals("ASHLEY YOUNG", p.getName());
		Assert.assertEquals("YOUNG", p.getShirtName());


		pid = rand.nextInt(Player.TOTAL - 1) + 1;
		p = new Player(of, pid);

		String name = p.getName();
		Assert.assertThat(name, Matchers.not(Matchers.isEmptyOrNullString()));

		String shirtName = p.getShirtName();
		Assert.assertThat(shirtName, Matchers.not(Matchers.isEmptyOrNullString()));

		String newName = name.substring(1).trim() + "!";
		p.setName(newName);
		name = p.getName();
		Assert.assertEquals(newName, name);

		String newShirt = shirtName.substring(1).trim() + "_";
		p.setShirtName(newShirt);
		shirtName = p.getShirtName();
		Assert.assertEquals(newShirt, shirtName);
	}

	@Test
	public void testBuildShirtName() throws Exception {
		String sn = Player.buildShirtName("abc");
		Assert.assertEquals("A    B    C", sn);

		sn = Player.buildShirtName("abcD");
		Assert.assertEquals("A  B  C  D", sn);
	}

	// TODO: More tests here!

}
