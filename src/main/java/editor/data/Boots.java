package editor.data;

import editor.Player;

public final class Boots {
	private Boots() {
	}

	/**
	 * There are total 9 boot types.
	 */
	public static final int TOTAL = 9;

	/**
	 * Each boot data record length is 92 bytes.
	 */
	public static final int SIZE = 92;

	public static final int START_ADR = Player.END_ADR + 1116;

	public static void importData(OptionFile ofSource, OptionFile ofDest) {
		if (null == ofSource) throw new NullPointerException("ofSource");
		if (null == ofDest) throw new NullPointerException("ofDest");

		System.arraycopy(ofSource.getData(), START_ADR, ofDest.getData(), START_ADR, SIZE * TOTAL);
	}

}
