package editor.data;

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
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SaveGameInfo.class);

	private volatile String gameName = "";
	private volatile String saveName = "";
	private volatile String notes = "";
	private volatile String game = "";

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
		if (null == file) throw new NullPointerException("file");

		if (!file.isFile())
			return false;

		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file, "r");
			gameName = saveName = notes = game = "";

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
			if (rf != null) {
				try {
					rf.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		return false;
	}

	private void readXPortFile(RandomAccessFile rf) throws IOException {
		rf.seek(21);
		int size = Bits.swabInt(rf.readInt());
		byte[] temp = new byte[size];
		rf.read(temp);
		gameName = new String(temp);

		size = Bits.swabInt(rf.readInt());
		temp = new byte[size];
		rf.read(temp);
		saveName = new String(temp);

		size = Bits.swabInt(rf.readInt());
		temp = new byte[size];
		rf.read(temp);
		notes = new String(temp);

		rf.skipBytes(6);
		temp = new byte[OptionFile.GAME_LEN];
		rf.read(temp);
		game = new String(temp);
	}

	private void readARMaxFile(RandomAccessFile rf) throws IOException {
		rf.seek(16);
		byte[] temp = new byte[OptionFile.GAME_LEN];
		rf.read(temp);
		game = new String(temp);

		rf.seek(48);
		temp = new byte[32];
		rf.read(temp);
		gameName = Strings.fixCString(new String(temp));
	}

	private void readEmsFile(RandomAccessFile rf) throws IOException {
		rf.seek(64);
		byte[] temp = new byte[OptionFile.GAME_LEN];
		rf.read(temp);
		game = new String(temp);
	}

	@Override
	public String toString() {
		return String.format("{ game: '%s', gameName: '%s', saveName: '%s', notes: '%s' }",
				getGame(), getGameName(), getSaveName(), getNotes());
	}

}
