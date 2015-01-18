package editor.util;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

public final class ArraysTest {
	@Test
	public void testIndexOf() throws Exception {
		Object[] arr = new Object[]{"", null, getClass()};
		int idx = Arrays.indexOf(arr, null);
		Assert.assertEquals(1, idx);

		idx = Arrays.indexOf(arr, "");
		Assert.assertEquals(0, idx);

		idx = Arrays.indexOf(arr, "foo");
		Assert.assertTrue(idx < 0);

		idx = Arrays.indexOf(arr, getClass());
		Assert.assertEquals(2, idx);
	}

	@Test(expected = NullArgumentException.class)
	public void testIndexOfWithNullArray() throws Exception {
		Arrays.indexOf(null, null);
	}

}
