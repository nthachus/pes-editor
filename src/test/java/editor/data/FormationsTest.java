package editor.data;

import org.junit.Assert;
import org.junit.Test;

public final class FormationsTest extends BaseTest {
	@Test
	public void testAddresses() throws NoSuchFieldException, IllegalAccessException {
		Assert.assertEquals(668080, Formations.START_ADR);

		Object FORM_DATA_LEN = readStaticField(Formations.class, "FORM_DATA_LEN", true, true);
		Assert.assertEquals(31, FORM_DATA_LEN);

		Assert.assertEquals(197, Formations.TOTAL);
	}

	// TODO: Other test-cases
}
