package editor.util;

import editor.lang.NullArgumentException;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public final class StringsTest {
	private volatile Locale defLocale;

	// @BeforeClass setUpClass()
	@Before
	public void setUp() {
		defLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@After
	public void tearDown() {
		Locale.setDefault(defLocale);
	}

	@Test
	public void testConstants() {
		//Assert.assertNotNull(Strings.ANSI);
		//Assert.assertNotNull(Strings.UTF8);
		//Assert.assertNotNull(Strings.S_JIS);
		//Assert.assertNotNull(Strings.UNICODE);

		Assert.assertNotNull(Strings.NEW_LINE);
		Assert.assertTrue(Strings.NEW_LINE.contains("\n"));
	}

	@Test
	public void testReadANSI() throws UnsupportedEncodingException {
		String s = Strings.readANSI(new byte[0]);
		Assert.assertEquals(0, s.length());

		s = Strings.readANSI("Con cho!\0la\0\0".getBytes(Strings.ANSI));
		Assert.assertEquals("Con cho!", s);

		s = Strings.readANSI("Con chó!\0la\0\0".getBytes(Strings.ANSI), 0, 7);
		Assert.assertEquals("Con chó", s);

		s = Strings.readANSI(new byte[]{0});
		Assert.assertEquals("", s);
	}

	@Test(expected = NullArgumentException.class)
	public void testReadNullANSIString() {
		Strings.readANSI(null);
	}

	@Test
	public void testReadUnicode() throws UnsupportedEncodingException {
		String s = Strings.readUNICODE(new byte[0]);
		Assert.assertEquals(0, s.length());

		s = Strings.readUNICODE("Con chó chết!\0là thế\0\0".getBytes(Strings.UNICODE));
		Assert.assertEquals("Con chó chết!", s);

		s = Strings.readUNICODE("Con chó chết!\0là thế\0\0".getBytes(Strings.UNICODE), 0, 14);
		Assert.assertEquals("Con chó", s);

		s = Strings.readUNICODE(new byte[]{0});
		Assert.assertEquals(1, s.length());

		s = Strings.readUNICODE(new byte[]{0, 0});
		Assert.assertEquals("", s);
	}

	@Test(expected = NullArgumentException.class)
	public void testReadNullUnicodeString() {
		Strings.readUNICODE(null);
	}

	@Test
	public void testReadUtf8() throws UnsupportedEncodingException {
		String s = Strings.readUTF8(new byte[0]);
		Assert.assertEquals(0, s.length());

		s = Strings.readUTF8("Con chó chết!\0là thế\0\0".getBytes(Strings.UTF8));
		Assert.assertEquals("Con chó chết!", s);

		s = Strings.readUTF8("Cồn chó chết!\0là thế\0\0".getBytes(Strings.UTF8), 0, 10);
		Assert.assertEquals("Cồn chó", s);

		s = Strings.readUTF8(new byte[]{0});
		Assert.assertEquals("", s);
	}

	@Test
	public void testEqualsIgnoreCase() {
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
	public void testIsEmpty() {
		Assert.assertEquals(true, Strings.isEmpty(null));
		Assert.assertEquals(true, Strings.isEmpty(""));
		Assert.assertEquals(false, Strings.isEmpty(" "));
		Assert.assertEquals(false, Strings.isEmpty("bob"));
		Assert.assertEquals(false, Strings.isEmpty("  bob  "));
	}

	@Test
	public void testIsBlank() {
		Assert.assertEquals(true, Strings.isBlank(null));
		Assert.assertEquals(true, Strings.isBlank(""));
		Assert.assertEquals(true, Strings.isBlank(" "));
		Assert.assertEquals(true, Strings.isBlank(" \t \n\r "));
		Assert.assertEquals(false, Strings.isBlank("bob"));
		Assert.assertEquals(false, Strings.isBlank("  bob  "));
	}

	@Test
	public void testMessageResource() {
		Assert.assertNotNull(Resources.getMessages());

		String notFoundKey = getClass().getName();
		String msg = Resources.getMessage(notFoundKey);
		Assert.assertEquals(notFoundKey, msg);

		String existsKey = "Error";
		msg = Resources.getMessage(existsKey);
		Assert.assertEquals(existsKey, msg);

		String existsMsg = "Current:  -10";
		msg = Resources.getMessage("wen.label", -10);
		Assert.assertEquals(existsMsg, msg);
	}

	@Test
	public void testFallbackMessage() {
		Locale.setDefault(new Locale("vi"));
		Assert.assertNotNull(Resources.getMessages());

		String val = "Tốc độ";
		String msg = Resources.getMessage("Speed");
		Assert.assertEquals(val, msg);

		val = "Slide Tackle";
		msg = Resources.getMessage("Sliding.tip");
		Assert.assertThat(msg, Matchers.anyOf(Matchers.isEmptyOrNullString(), Matchers.equalTo(val)));
	}

}
