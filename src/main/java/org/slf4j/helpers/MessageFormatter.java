/*
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.slf4j.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats messages according to very simple substitution rules. Substitutions
 * can be made 1, 2 or more arguments.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 * @author lizongbo: proposed special treatment of array parameter values
 * @author Joern Huxhorn: pointed out double[] omission, suggested deep array copy
 */
public final class MessageFormatter {
	private MessageFormatter() {
	}

	private static final char DELIM_START = '{';
	private static final String DELIM_STR = "{}";
	private static final char ESCAPE_CHAR = '\\';

	/**
	 * Performs single argument substitution for the 'messagePattern' passed as
	 * parameter.
	 * <p/>
	 * For example,
	 * <pre>
	 * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;);
	 * </pre>
	 * will return the string "Hi there.".
	 *
	 * @param messagePattern The message pattern which will be parsed and formatted
	 * @param arg            The argument to be substituted in place of the formatting anchor
	 * @return The formatted message
	 */
	public static FormattingTuple format(String messagePattern, Object arg) {
		return arrayFormat(messagePattern, new Object[]{arg});
	}

	/**
	 * Performs a two argument substitution for the 'messagePattern' passed as
	 * parameter.
	 * <p/>
	 * For example,
	 * <pre>
	 * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
	 * </pre>
	 * will return the string "Hi Alice. My name is Bob.".
	 *
	 * @param messagePattern The message pattern which will be parsed and formatted
	 * @param arg1           The argument to be substituted in place of the first formatting
	 *                       anchor
	 * @param arg2           The argument to be substituted in place of the second formatting
	 *                       anchor
	 * @return The formatted message
	 */
	public static FormattingTuple format(final String messagePattern, Object arg1, Object arg2) {
		return arrayFormat(messagePattern, new Object[]{arg1, arg2});
	}

	private static Throwable getThrowableCandidate(Object[] argArray) {
		if (argArray == null || argArray.length == 0) {
			return null;
		}

		final Object lastEntry = argArray[argArray.length - 1];
		if (lastEntry instanceof Throwable) {
			return (Throwable) lastEntry;
		}
		return null;
	}

	/**
	 * Same principle as the {@link #format(String, Object)} and
	 * {@link #format(String, Object, Object)} methods except that any number of
	 * arguments can be passed in an array.
	 *
	 * @param messagePattern The message pattern which will be parsed and formatted
	 * @param argArray       An array of arguments to be substituted in place of formatting
	 *                       anchors
	 * @return The formatted message
	 */
	public static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray) {
		Throwable throwableCandidate = getThrowableCandidate(argArray);

		if (messagePattern == null) {
			return new FormattingTuple(null, argArray, throwableCandidate);
		}

		if (argArray == null) {
			return new FormattingTuple(messagePattern);
		}

		int i = 0;
		int j;
		StringBuffer sb = new StringBuffer(messagePattern.length() + 50);

		int L;
		for (L = 0; L < argArray.length; L++) {

			j = messagePattern.indexOf(DELIM_STR, i);

			if (j == -1) {
				// no more variables
				if (i == 0) { // this is a simple string
					return new FormattingTuple(messagePattern, argArray, throwableCandidate);
				} else { // add the tail string which contains no variables and return
					// the result.
					sb.append(messagePattern.substring(i, messagePattern.length()));
					return new FormattingTuple(sb.toString(), argArray, throwableCandidate);
				}
			} else {
				if (isEscapedDelimiter(messagePattern, j)) {
					if (!isDoubleEscaped(messagePattern, j)) {
						L--; // DELIM_START was escaped, thus should not be incremented
						sb.append(messagePattern.substring(i, j - 1));
						sb.append(DELIM_START);
						i = j + 1;
					} else {
						// The escape character preceding the delimiter start is
						// itself escaped: "abc x:\\{}"
						// we have to consume one backward slash
						sb.append(messagePattern.substring(i, j - 1));
						deeplyAppendParameter(sb, argArray[L], new HashMap<Object[], Object>());
						i = j + 2;
					}
				} else {
					// normal case
					sb.append(messagePattern.substring(i, j));
					deeplyAppendParameter(sb, argArray[L], new HashMap<Object[], Object>());
					i = j + 2;
				}
			}
		}

		// append the characters following the last {} pair.
		sb.append(messagePattern.substring(i, messagePattern.length()));
		if (L < argArray.length - 1) {
			return new FormattingTuple(sb.toString(), argArray, throwableCandidate);
		}
		return new FormattingTuple(sb.toString(), argArray, null);
	}

	private static boolean isEscapedDelimiter(String messagePattern, int delimiterStartIndex) {
		if (delimiterStartIndex == 0) {
			return false;
		}
		char potentialEscape = messagePattern.charAt(delimiterStartIndex - 1);
		return potentialEscape == ESCAPE_CHAR;
	}

	private static boolean isDoubleEscaped(String messagePattern, int delimiterStartIndex) {
		return (delimiterStartIndex >= 2
				&& messagePattern.charAt(delimiterStartIndex - 2) == ESCAPE_CHAR);
	}

	// special treatment of array values was suggested by 'lizongbo'
	private static void deeplyAppendParameter(StringBuffer sb, Object o, Map<Object[], Object> seenMap) {
		if (o == null) {
			sb.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			safeObjectAppend(sb, o);
		} else {
			// check for primitive array types because they
			// unfortunately cannot be cast to Object[]
			if (o instanceof boolean[]) {
				booleanArrayAppend(sb, (boolean[]) o);
			} else if (o instanceof byte[]) {
				byteArrayAppend(sb, (byte[]) o);
			} else if (o instanceof char[]) {
				charArrayAppend(sb, (char[]) o);
			} else if (o instanceof short[]) {
				shortArrayAppend(sb, (short[]) o);
			} else if (o instanceof int[]) {
				intArrayAppend(sb, (int[]) o);
			} else if (o instanceof long[]) {
				longArrayAppend(sb, (long[]) o);
			} else if (o instanceof float[]) {
				floatArrayAppend(sb, (float[]) o);
			} else if (o instanceof double[]) {
				doubleArrayAppend(sb, (double[]) o);
			} else {
				objectArrayAppend(sb, (Object[]) o, seenMap);
			}
		}
	}

	private static void safeObjectAppend(StringBuffer sb, Object o) {
		try {
			String oAsString = o.toString();
			sb.append(oAsString);
		} catch (Exception t) {
			Util.report("Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
			sb.append("[FAILED toString()]");
		}
	}

	private static void objectArrayAppend(StringBuffer sb, Object[] a, Map<Object[], Object> seenMap) {
		sb.append('[');
		if (!seenMap.containsKey(a)) {
			seenMap.put(a, null);
			for (int i = 0; i < a.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				deeplyAppendParameter(sb, a[i], seenMap);
			}
			// allow repeats in siblings
			seenMap.remove(a);
		} else {
			sb.append("...");
		}
		sb.append(']');
	}

	private static void booleanArrayAppend(StringBuffer sb, boolean[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}

	private static void byteArrayAppend(StringBuffer sb, byte[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}

	private static void charArrayAppend(StringBuffer sb, char[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}

	private static void shortArrayAppend(StringBuffer sb, short[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}

	private static void intArrayAppend(StringBuffer sb, int[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}

	private static void longArrayAppend(StringBuffer sb, long[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i > 0) {
				sb.append(", ");
			}
		}
		sb.append(']');
	}

	private static void floatArrayAppend(StringBuffer sb, float[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}

	private static void doubleArrayAppend(StringBuffer sb, double[] a) {
		sb.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(a[i]);
		}
		sb.append(']');
	}
}
