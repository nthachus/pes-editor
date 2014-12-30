package editor.data;

import org.junit.Assert;
import org.junit.Test;

public final class LogosTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(866044, Logos.START_ADR);

		Assert.assertEquals(80, Logos.TOTAL);
		Assert.assertEquals(608, Logos.SIZE);
	}

}
