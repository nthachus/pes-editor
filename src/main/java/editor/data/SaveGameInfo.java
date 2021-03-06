package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Files;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class SaveGameInfo implements Serializable {
	private static final long serialVersionUID = -4795934280992874495L;
	private static final Logger log = LoggerFactory.getLogger(SaveGameInfo.class);

	private volatile String gameName = Strings.EMPTY;
	private volatile String saveName = Strings.EMPTY;
	private volatile String notes = Strings.EMPTY;
	private volatile String game = Strings.EMPTY;

	/**
	 * Game ID.
	 */
	public String getGame() {
		return game;
	}

	public String getGameName() {
		return gameName;
	}

	public String getSaveName() {
		return saveName;
	}

	public String getNotes() {
		return notes;
	}

	public boolean getInfo(File file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}
		if (!file.isFile()) {
			return false;
		}
		// DEBUG
		log.debug("Try to get save game info from file: {}", file.getName());

		gameName = saveName = notes = game = Strings.EMPTY;
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file, "r");

			String extension = Files.getExtension(file);
			if (Files.isXPortFile(extension)) {
				readXPortFile(rf);
			} else if (Files.isARMaxFile(extension)) {
				readARMaxFile(rf);
			} else if (Files.isEmsFile(extension)) {
				readEmsFile(rf);
			} else {
				throw new IOException("Unsupported file type: " + extension);
			}

			return true;
		} catch (IOException e) {

			log.error("I/O error while getting save game info:", e);
		} finally {
			Files.closeStream(rf);
		}
		return false;
	}

	private void readXPortFile(RandomAccessFile rf) throws IOException {
		rf.seek(21);
		int size = Bits.swabInt(rf.readInt());
		byte[] temp = new byte[size];
		int len = rf.read(temp);
		gameName = new String(temp, 0, len, Strings.ANSI);

		size = Bits.swabInt(rf.readInt());
		temp = new byte[size];
		len = rf.read(temp);
		saveName = new String(temp, 0, len, Strings.ANSI);

		size = Bits.swabInt(rf.readInt());
		temp = new byte[size];
		len = rf.read(temp);
		notes = new String(temp, 0, len, Strings.ANSI);

		len = rf.skipBytes(6);
		assert len == 6 : "Cannot skip next 6 bytes in XPort file, result: " + len;

		temp = new byte[OptionFile.GAME_LEN];
		len = rf.read(temp);
		game = new String(temp, 0, len, Strings.ANSI);

		log.debug("Getting XPort save game '{}' successfully", gameName);
	}

	private void readARMaxFile(RandomAccessFile rf) throws IOException {
		rf.seek(16);
		byte[] temp = new byte[OptionFile.GAME_LEN];
		int len = rf.read(temp);
		game = new String(temp, 0, len, Strings.ANSI);

		rf.seek(48);
		temp = new byte[32];
		len = rf.read(temp);
		gameName = Strings.readANSI(temp, 0, len);

		log.debug("Getting ARMax save game '{}' successfully", game);
	}

	private void readEmsFile(RandomAccessFile rf) throws IOException {
		rf.seek(64);
		byte[] temp = new byte[OptionFile.GAME_LEN];
		int len = rf.read(temp);
		game = new String(temp, 0, len, Strings.ANSI);

		log.debug("Getting ARMax save game '{}' successfully", game);
	}

	@Override
	public String toString() {
		return String.format("{ game: '%s', gameName: '%s', saveName: '%s', notes: '%s' }",
				getGame(), getGameName(), getSaveName(), getNotes());
	}

}
