package editor.ui;

import editor.data.OptionFile;
import editor.util.Bits;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class OptionFileFilter extends FileFilter implements Serializable {
	private static final long serialVersionUID = 2713989120983395257L;
	private static final Logger log = LoggerFactory.getLogger(OptionFileFilter.class);

	public boolean accept(File file) {
		if (file == null)
			return false;

		if (file.isDirectory())
			return true;

		String extension = Files.getExtension(file);
		return (Files.isXPortFile(extension) && isXPortOptionFile(file))
				|| (Files.isEmsFile(extension) && isEmsOptionFile(file))
				|| (Files.isARMaxFile(extension) && isARMaxOptionFile(file));
	}

	public String getDescription() {
		return Resources.getMessage("of.title");
	}

	private static boolean isEmsOptionFile(File f) {
		if (!f.canRead())
			return false;

		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(f, "r");

			rf.seek(64);
			byte[] identBytes = new byte[19];
			int len = rf.read(identBytes);
			String identCheck = new String(identBytes, 0, len, Strings.ANSI);

			return OptionFile.isValidGameId(identCheck);

		} catch (IOException e) {
			log.warn("{} is not a Memory Linker file: {}", f, e);
		} finally {
			if (null != rf) {
				try {
					rf.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		return false;
	}

	private static boolean isXPortOptionFile(File f) {
		if (!f.canRead())
			return false;

		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(f, "r");

			rf.seek(21);
			int skip = Bits.swabInt(rf.readInt());
			if (rf.skipBytes(skip) == skip) {
				skip = Bits.swabInt(rf.readInt());
				if (rf.skipBytes(skip) == skip) {
					skip = Bits.swabInt(rf.readInt()) + 6;
					if (rf.skipBytes(skip) == skip) {

						byte[] identBytes = new byte[19];
						int len = rf.read(identBytes);
						String identCheck = new String(identBytes, 0, len, Strings.ANSI);
						return OptionFile.isValidGameId(identCheck);
					}
				}
			}
		} catch (IOException e) {
			log.warn("{} is not an XPort file: {}", f, e);
		} finally {
			if (null != rf) {
				try {
					rf.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		return false;
	}

	private static boolean isARMaxOptionFile(File f) {
		if (!f.canRead())
			return false;

		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(f, "r");

			rf.seek(16);
			byte[] identBytes = new byte[19];
			int len = rf.read(identBytes);
			String identCheck = new String(identBytes, 0, len, Strings.ANSI);

			return OptionFile.isValidGameId(identCheck);

		} catch (IOException e) {
			log.warn("{} is not an ARMax file: {}", f, e);
		} finally {
			if (null != rf) {
				try {
					rf.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		return false;
	}

}
