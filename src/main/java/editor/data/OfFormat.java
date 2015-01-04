package editor.data;

import editor.util.Files;

public enum OfFormat {
	/**
	 * SharkPort/X-Port Version 2 (PS2 Memory Card Management).
	 */
	xPort,
	/**
	 * EMS Memory Adapter / Memory Linker (.psu) Save File.
	 */
	ems,
	/**
	 * Action Replay Max Save File (ARMax v3).
	 */
	arMax;

	private static final String[] EXTENSIONS = {Files.XPS, Files.PSU, Files.MAX};

	@Override
	public String toString() {
		return EXTENSIONS[ordinal()];
	}
}
