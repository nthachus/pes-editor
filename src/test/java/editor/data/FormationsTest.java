package editor.data;

import org.junit.Assert;
import org.junit.Test;

public final class FormationsTest extends BaseTest {
	@Test
	public void testAddresses() throws Exception {
		Assert.assertEquals(659152, Formations.START_ADR);

		Object FORM_DATA_LEN = readStaticField(Formations.class, "FORM_DATA_LEN", true, true);
		Assert.assertEquals(31, FORM_DATA_LEN);
	}

	// TODO: Other test-cases
}
