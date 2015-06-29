package editor.util;

import editor.Program;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public final class SystemsTest {
	private static final Logger log = LoggerFactory.getLogger(SystemsTest.class);

	@Test
	public void testComparator() {
		Assert.assertFalse(null instanceof JButton);
		Assert.assertFalse("".equalsIgnoreCase(null));
	}

	@Test
	@SuppressWarnings("StringEquality")
	public void testJvmInfo() {
		String info = Program.getJvmInfo();
		Assert.assertNotNull(info);
		Assert.assertTrue(info == Program.getJvmInfo());
		// DEBUG
		log.debug("JVM info: {}", info);

		Assert.assertTrue(info.matches("[.\\d_]+.*/\\s.+\\([.\\d_]+.*\\)\\s.+"));
	}

}
