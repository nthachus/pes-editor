package editor.util;

import editor.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class Files {
	private static final Logger log = LoggerFactory.getLogger(Files.class);

	private Files() {
	}

	/**
	 * The extension separator character.
	 */
	public static final char EXT_SEPARATOR = '.';

	/**
	 * SharkPort/X-Port Version 2.
	 */
	public static final String XPS = "xps";
	/**
	 * EMS Memory Adapter.
	 */
	public static final String PSU = "psu";
	/**
	 * Action Replay Max Save File (ARMax v3).
	 */
	public static final String MAX = "max";

	public static final String PNG = "png";
	public static final String GIF = "gif";

	public static final String CSV = "csv";

	/**
	 * Get the extension of a file.
	 */
	public static String getExtension(File file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}
		return getExtension(file.getName());
	}

	/**
	 * Get the extension of a filename.
	 */
	public static String getExtension(String filename) {
		if (Strings.isEmpty(filename)) {
			return filename;
		}

		int i = filename.lastIndexOf(EXT_SEPARATOR);
		if (i > 0 && i < filename.length() - 1) {
			return filename.substring(i + 1);
		}

		return Strings.EMPTY;
	}

	public static File addExtension(File file, String extension) {
		if (null != file && null != extension) {
			String ext = getExtension(file);
			if (Strings.isEmpty(ext) || !ext.equalsIgnoreCase(extension)) {
				try {
					return new File(file.getCanonicalPath() + EXT_SEPARATOR + extension);
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}
		return file;
	}

	public static String removeExtension(String filename) {
		if (!Strings.isEmpty(filename)) {
			int i = filename.lastIndexOf(EXT_SEPARATOR);
			if (i >= 0) {
				return filename.substring(0, i);
			}
		}
		return filename;
	}

	public static boolean isXPortFile(String extension) {
		return XPS.equalsIgnoreCase(extension);
	}

	public static boolean isARMaxFile(String extension) {
		return MAX.equalsIgnoreCase(extension);
	}

	public static boolean isEmsFile(String extension) {
		return PSU.equalsIgnoreCase(extension);
	}

	public static boolean isFilenameLegal(String filename) {
		return (null == filename || !filename.matches(".*[|<>\"?*:/\\\\].*"));
	}

	public static byte[] readBytes(File file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}

		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);

			byte[] buffer = new byte[fs.available()];
			int res = fs.read(buffer);
			if (res < 0) {
				log.error("Cannot read entire file: {}", file.getPath());
				return null;
			}
			return buffer;

		} catch (Exception e) {
			log.error("Failed to read entire file:", e);
			return null;
		} finally {
			closeStream(fs);
		}
	}

	public static void closeStream(Closeable stream) {
		if (null != stream) {
			try {
				stream.close();
			} catch (IOException e) {
				log.warn(e.toString());
			}
		}
	}

}
