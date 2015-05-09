package editor.util;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;

public final class SystemsTest {
	@Test
	public void testComparator() {
		Assert.assertFalse(null instanceof JButton);
		Assert.assertFalse("".equalsIgnoreCase(null));
	}

}
