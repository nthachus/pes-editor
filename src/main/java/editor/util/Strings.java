package editor.util;

import editor.lang.NullArgumentException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Pattern;

public final class Strings {
	private Strings() {
	}

	/**
	 * Latin-1 encoding.
	 */
	public static final String ANSI = "ISO-8859-1";
	public static final String UTF8 = "UTF-8";

	/**
	 * Shift JIS encoding.
	 */
	//public static final String S_JIS = "Shift_JIS";
	public static final String UNICODE = "UTF-16LE";

	/**
	 * Platform independent newline character.
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");

	public static final String EMPTY = "";
	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String TAB = "\t";
	public static final String SPACE = " ";

	public static final Pattern COMMA_REGEX = Pattern.compile("\\s*,\\s*");

	//region Create String from bytes array

	private static void checkBounds(byte[] bytes, int offset, int length) {
		if (null == bytes) {
			throw new NullArgumentException("bytes");
		}
		if (length < 0) {
			throw new StringIndexOutOfBoundsException(length);
		}
		if (offset < 0) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		if (offset + length > bytes.length) {
			throw new StringIndexOutOfBoundsException(offset + length);
		}
	}

	private static String readANSI(byte[] bytes, int offset, int length, String charset, boolean checkBounds) {
		if (checkBounds) {
			checkBounds(bytes, offset, length);
		}

		for (int l = 0; l < length; l++) {
			if (bytes[offset + l] == 0) {
				length = l;
				break;
			}
		}

		try {
			return (length == 0) ? EMPTY : new String(bytes, offset, length, charset);
		} catch (UnsupportedEncodingException e) {
			RuntimeException t = new UnsupportedCharsetException(charset);
			t.initCause(e);
			throw t;
		}
	}

	public static String readANSI(byte[] bytes, int offset, int length) {
		return readANSI(bytes, offset, length, ANSI, true);
	}

	public static String readANSI(byte[] bytes) {
		if (null == bytes) {
			throw new NullArgumentException("bytes");
		}
		return readANSI(bytes, 0, bytes.length, ANSI, false);
	}

	public static String readUTF8(byte[] bytes, int offset, int length) {
		return readANSI(bytes, offset, length, UTF8, true);
	}

	public static String readUTF8(byte[] bytes) {
		if (null == bytes) {
			throw new NullArgumentException("bytes");
		}
		return readANSI(bytes, 0, bytes.length, UTF8, false);
	}

	@SuppressWarnings("SameParameterValue")
	private static String readUNICODE(byte[] bytes, int offset, int length, String charset, boolean checkBounds) {
		if (checkBounds) {
			checkBounds(bytes, offset, length);
		}

		for (int l = 0, e = length - 1; l < e; l += 2) {
			if (bytes[offset + l] == 0 && bytes[offset + l + 1] == 0) {
				length = l;
				break;
			}
		}

		try {
			return (length == 0) ? EMPTY : new String(bytes, offset, length, charset);
		} catch (UnsupportedEncodingException e) {
			RuntimeException t = new UnsupportedCharsetException(charset);
			t.initCause(e);
			throw t;
		}
	}

	public static String readUNICODE(byte[] bytes, int offset, int length) {
		return readUNICODE(bytes, offset, length, UNICODE, true);
	}

	public static String readUNICODE(byte[] bytes) {
		if (null == bytes) {
			throw new NullArgumentException("bytes");
		}
		return readUNICODE(bytes, 0, bytes.length, UNICODE, false);
	}

	//endregion

	public static boolean equalsIgnoreCase(String s1, String s2) {
		return (null == s1) ? (null == s2) : s1.equalsIgnoreCase(s2);
	}

	public static boolean isEmpty(String s) {
		return (s == null || s.length() == 0);
	}

	public static boolean isBlank(String s) {
		int len;
		if (s == null || (len = s.length()) == 0) {
			return true;
		}

		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String valueOf(Object o) {
		return (null == o) ? "null" : (o.getClass().getSimpleName() + "#" + o.hashCode());
	}

}
