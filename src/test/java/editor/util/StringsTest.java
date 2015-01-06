package editor.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public final class StringsTest {
	private volatile Locale defLocale;

	@Before
	public void setUp() throws Exception {
		defLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@After
	public void tearDown() throws Exception {
		Locale.setDefault(defLocale);
	}

	@Test
	public void testConstants() throws Exception {
		Assert.assertNotNull(Strings.ANSI);
		Assert.assertNotNull(Strings.UTF8);
		Assert.assertNotNull(Strings.S_JIS);
		Assert.assertNotNull(Strings.UNICODE);

		Assert.assertNotNull(Strings.NEW_LINE);
		Assert.assertTrue(Strings.NEW_LINE.contains("\n"));
	}

	@Test
	public void testFixCString() throws Exception {
		String s = Strings.fixCString(null);
		Assert.assertNull(s);

		s = Strings.fixCString("");
		Assert.assertTrue(s.length() == 0);

		s = Strings.fixCString("Con cho!\0la\0\0");
		Assert.assertEquals("Con cho!", s);

		s = Strings.fixCString("\0");
		Assert.assertEquals("", s);
	}

	@Test
	public void testEqualsIgnoreCase() throws Exception {
		boolean res = Strings.equalsIgnoreCase(null, null);
		Assert.assertEquals(true, res);

		res = Strings.equalsIgnoreCase(null, "");
		Assert.assertEquals(false, res);

		res = Strings.equalsIgnoreCase("", null);
		Assert.assertEquals(false, res);

		res = Strings.equalsIgnoreCase("", "");
		Assert.assertEquals(true, res);

		res = Strings.equalsIgnoreCase("Aa", "aA");
		Assert.assertEquals(true, res);
	}

	@Test
	public void testIsEmpty() throws Exception {
		Assert.assertEquals(true, Strings.isEmpty(null));
		Assert.assertEquals(true, Strings.isEmpty(""));
		Assert.assertEquals(false, Strings.isEmpty(" "));
		Assert.assertEquals(false, Strings.isEmpty("bob"));
		Assert.assertEquals(false, Strings.isEmpty("  bob  "));
	}

	@Test
	public void testIsBlank() throws Exception {
		Assert.assertEquals(true, Strings.isBlank(null));
		Assert.assertEquals(true, Strings.isBlank(""));
		Assert.assertEquals(true, Strings.isBlank(" "));
		Assert.assertEquals(true, Strings.isBlank(" \t \n\r "));
		Assert.assertEquals(false, Strings.isBlank("bob"));
		Assert.assertEquals(false, Strings.isBlank("  bob  "));
	}

	@Test
	public void testMessageResource() throws Exception {
		Assert.assertNotNull(Resources.getMessages(false));

		String notFoundKey = getClass().getName();
		String msg = Resources.getMessage(notFoundKey);
		Assert.assertEquals(notFoundKey, msg);

		String existsKey = "Error";
		msg = Resources.getMessage(existsKey);
		Assert.assertEquals(existsKey, msg);

		String existsMsg = "Current:  -10 (5)";
		msg = Resources.getMessage("wen.label", -10, 5);
		Assert.assertEquals(existsMsg, msg);
	}

}
