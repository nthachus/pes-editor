package editor.data;

import org.junit.Assert;
import org.junit.Test;

public class EmblemsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(914800, Emblems.START_ADR);

		Assert.assertEquals(5184, Emblems.SIZE128);
		Assert.assertEquals(2176, Emblems.SIZE16);
	}

}
