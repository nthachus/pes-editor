package editor.data;

/**
 * Game pad button types.
 */
public enum ControlButton {
	/**
	 * Red circle.
	 */
	circle,
	/**
	 * Blue (cyan) cross.
	 */
	cross,
	/**
	 * Green triangle.
	 */
	triangle,
	/**
	 * Pink square.
	 */
	square;

	private static volatile ControlButton[] values = null;

	public static int size() {
		if (values == null) {
			values = values();
		}
		return values.length;
	}

	public static ControlButton valueOf(int ordinal) {
		if (ordinal < 0 || ordinal >= size()) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(ordinal));
		}
		return values[ordinal];
	}

}
