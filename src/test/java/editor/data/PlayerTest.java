package editor.data;

import editor.ui.PlayerTransferable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public final class PlayerTest extends BaseTest {
	@Test
	public void testAddresses() {
		Assert.assertEquals(34672, Player.START_ADR);
		Assert.assertEquals(11844, Player.START_EDIT_ADR);

		Assert.assertEquals(1381, Player.FIRST_CLASSIC);
		Assert.assertEquals(1542, Player.FIRST_CLUB);
		//Assert.assertEquals(4527, Player.FIRST_JAPAN);
		Assert.assertEquals(4534, Player.FIRST_ML);
		Assert.assertEquals(4562, Player.FIRST_SHOP);
		Assert.assertEquals(4722, Player.FIRST_YOUNG);
		Assert.assertEquals(4862, Player.FIRST_OLD);
		Assert.assertEquals(4872, Player.FIRST_UNUSED);

		Assert.assertEquals(0x8000, Player.FIRST_EDIT);
		Assert.assertEquals(184, Player.TOTAL_EDIT);
		Assert.assertEquals(4872, Player.TOTAL);
	}

	@Test
	public void testTransferable() {
		Assert.assertNotNull(PlayerTransferable.getDataFlavor());
	}

	@Test
	public void testGetAndSetName() {
		OptionFile of = loadOriginalOF();

		int pid = 2;
		Player p = new Player(of, pid);
		Assert.assertEquals("DRAGOVIĆ", p.getName());
		Assert.assertEquals("DRAGOVIC", p.getShirtName());

		pid = 156;
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
	public void testBuildShirtName() {
		String sn = Player.buildShirtName("abc");
		Assert.assertEquals("A    B    C", sn);

		sn = Player.buildShirtName("abcD");
		Assert.assertEquals("A  B  C  D", sn);
	}

	// TODO: More tests here!

}
