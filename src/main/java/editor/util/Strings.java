package editor.util;

import java.nio.charset.Charset;

public final class Strings {
	private Strings() {
	}

	/**
	 * Latin-1 encoding.
	 */
	public static final Charset ANSI = Charset.forName("ISO-8859-1");
	public static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * Shift JIS encoding.
	 */
	public static final Charset S_JIS = Charset.forName("Shift_JIS");
	public static final Charset UNICODE = Charset.forName("UTF-16LE");

	/**
	 * Platform independent newline character.
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");

	public static String fixCString(String s) {
		if (null != s && s.length() > 0) {
			int p = s.indexOf('\0');
			if (p >= 0) return s.substring(0, p);
		}
		return s;
	}

	public static boolean equalsIgnoreCase(String s1, String s2) {
		return (null == s1 && null == s2)
				|| (null != s1 && null != s2 && s1.equalsIgnoreCase(s2));
	}

	public static boolean isEmpty(String s) {
		return (s == null || s.length() == 0);
	}

	public static boolean isBlank(String s) {
		int len;
		if (s == null || (len = s.length()) == 0)
			return true;

		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(s.charAt(i)))
				return false;
		}
		return true;
	}

}
