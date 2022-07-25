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

public class OptionFileFilter extends FileFilter {
	private static final Logger log = LoggerFactory.getLogger(OptionFileFilter.class);

	public boolean accept(File file) {
		if (file == null) {
			return false;
		}
		if (file.isDirectory()) {
			return true;
		}

		//log.debug("Try to filter OF: {}", file);
		if (!file.canRead()) {
			return false;
		}

		String extension = Files.getExtension(file);

		String identCheck = null;
		if (Files.isXPortFile(extension)) {
			identCheck = readIdentFromXPort(file);
		} else if (Files.isEmsFile(extension)) {
			identCheck = readIdentFromEms(file);
		} else if (Files.isARMaxFile(extension)) {
			identCheck = readIdentFromARMax(file);
		}

		return OptionFile.isValidGameId(identCheck);
	}

	public String getDescription() {
		return Resources.getMessage("of.title");
	}

	private static String readIdentFromEms(File f) {
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(f, "r");

			rf.seek(64);
			byte[] identBytes = new byte[OptionFile.GAME_LEN];
			int len = rf.read(identBytes);

			String identCheck = new String(identBytes, 0, len, Strings.ANSI);
			// DEBUG
			log.debug("Retrieved identity {} from EMS OF: {}", identCheck, f.getName());
			return identCheck;

		} catch (IOException e) {
			log.warn("{} is not a Memory Linker file: {}", f, e);
		} finally {
			Files.closeStream(rf);
		}
		return null;
	}

	private static String readIdentFromXPort(File f) {
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

						byte[] identBytes = new byte[OptionFile.GAME_LEN];
						int len = rf.read(identBytes);

						String identCheck = new String(identBytes, 0, len, Strings.ANSI);
						// DEBUG
						log.debug("Retrieved identity {} from XPort OF: {}", identCheck, f.getName());
						return identCheck;
					}
				}
			}
		} catch (IOException e) {
			log.warn("{} is not an XPort file: {}", f, e);
		} finally {
			Files.closeStream(rf);
		}
		return null;
	}

	private static String readIdentFromARMax(File f) {
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(f, "r");

			rf.seek(16);
			byte[] identBytes = new byte[OptionFile.GAME_LEN];
			int len = rf.read(identBytes);
			String identCheck = new String(identBytes, 0, len, Strings.ANSI);

			// DEBUG
			log.debug("Retrieved identity {} from ARMax OF: {}", identCheck, f.getName());
			return identCheck;

		} catch (IOException e) {
			log.warn("{} is not an ARMax file: {}", f, e);
		} finally {
			Files.closeStream(rf);
		}
		return null;
	}

}
