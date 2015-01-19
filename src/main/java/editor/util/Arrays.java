package editor.util;

import editor.lang.NullArgumentException;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public final class Arrays {
	private Arrays() {
	}

	public static int indexOfIgnoreCase(String[] array, String value) {
		if (null == array) {
			throw new NullArgumentException("array");
		}
		for (int i = 0; i < array.length; i++) {
			if (Strings.equalsIgnoreCase(array[i], value)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(Object[] array, Object value) {
		if (null == array) {
			throw new NullArgumentException("array");
		}
		for (int i = 0; i < array.length; i++) {
			if (eq(array[i], value)) {
				return i;
			}
		}
		return -1;
	}

	private static boolean eq(Object o1, Object o2) {
		return (null == o1) ? (null == o2) : o1.equals(o2);
	}

	/**
	 * Copies the specified array, truncating or padding with zeros (if necessary)
	 * so the copy has the specified length.
	 *
	 * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
	 * @throws NullPointerException       if <tt>original</tt> is null
	 */
	public static int[] copyOf(int[] original, int newLength) {
		int[] copy = new int[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

	/**
	 * Copies the specified array, truncating or padding with zeros (if necessary)
	 * so the copy has the specified length.
	 *
	 * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
	 * @throws NullPointerException       if <tt>original</tt> is null
	 */
	public static byte[] copyOf(byte[] original, int newLength) {
		byte[] copy = new byte[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

	/**
	 * Copies the specified range of the specified array into a new array.
	 * The initial index of the range (<tt>from</tt>) must lie between zero
	 * and <tt>original.length</tt>, inclusive.
	 *
	 * @throws ArrayIndexOutOfBoundsException if {@code from < 0} or {@code from > original.length}
	 * @throws IllegalArgumentException       if <tt>from &gt; to</tt>
	 * @throws NullPointerException           if <tt>original</tt> is null
	 */
	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0) {
			throw new IllegalArgumentException(from + " > " + to);
		}
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}

	public static class EntryValueComparator<K, V extends Comparable<? super V>>
			implements Comparator<Map.Entry<K, V>>, Serializable {
		private static final long serialVersionUID = 1803953350037498756L;

		public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
			if (null == o1.getValue()) {
				return (null == o2.getValue()) ? 0 : 1;
			}

			return (null == o2.getValue()) ? -1 : o2.getValue().compareTo(o1.getValue());
		}
	}

}
