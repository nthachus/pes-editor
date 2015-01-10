package editor.data;

import editor.util.Systems;
import org.junit.Assert;
import org.junit.Test;

public class FormationsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(668080, Formations.START_ADR);

		Object FORM_DATA_LEN = Systems.readStaticField(Formations.class, "FORM_DATA_LEN", true, true);
		Assert.assertEquals(31, FORM_DATA_LEN);
	}

	// TODO: Other test-cases
}
