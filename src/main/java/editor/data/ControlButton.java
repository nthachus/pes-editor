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

	public static ControlButton valueOf(int ordinal) {
		if (values == null) values = values();
		if (ordinal < 0 || ordinal >= values.length) throw new ArrayIndexOutOfBoundsException("ordinal");
		return values[ordinal];
	}

}
