package editor.util;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;

public final class SystemsTest {
	@Test
	public void testInstanceOf() throws Exception {
		Assert.assertFalse(null instanceof JButton);
	}

}
