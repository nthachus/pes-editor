package editor.util;

public final class Arrays {
	private Arrays() {
	}

	public static int indexOfIgnoreCase(String[] array, String value) {
		if (null == array) throw new NullPointerException("array");
		for (int i = 0; i < array.length; i++) {
			if (Strings.equalsIgnoreCase(array[i], value))
				return i;
		}
		return -1;
	}

	public static int indexOf(Object[] array, Object value) {
		if (null == array) throw new NullPointerException("array");
		for (int i = 0; i < array.length; i++) {
			if ((null == array[i] && null == value)
					|| (null != array[i] && null != value && array[i].equals(value)))
				return i;
		}
		return -1;
	}

}
