package editor.data;

import editor.Player;
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

}
