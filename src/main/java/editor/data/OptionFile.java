package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Files;
import editor.util.LZAri;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

public class OptionFile {
	private static final Logger log = LoggerFactory.getLogger(OptionFile.class);

	private static final String SHARK_PORT = "\15\0\0\0SharkPortSave";
	private static final String MAGIC_MAX = "Ps2PowerSave";

	private static final String GAME_ID = "(?i:(BESLES|BASLUS)\\-.*2013OPT|.*3013OPT)";
	public static final int GAME_LEN = 19;

	public static final int LENGTH = 1095680;

	//region Properties

	private final byte[] data = new byte[LENGTH];
	private volatile byte[] headerData;

	private volatile String gameId;
	private volatile String gameName;
	private volatile String saveName;
	private volatile String notes;

	private volatile String filename;
	private volatile OfFormat format = null;
	private volatile int filesCount;

	public String getFilename() {
		return filename;
	}

	public String getGameId() {
		return gameId;
	}

	public OfFormat getFormat() {
		return format;
	}

	public byte[] getData() {
		return data;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public boolean isLoaded() {
		return (null != filename);
	}

	public static boolean isValidGameId(String gameId) {
		return (null != gameId && gameId.matches(GAME_ID));
	}

	//endregion

	@Override
	public String toString() {
		return !isLoaded() ? super.toString() : String.format(
				"{ format: %s, gameId: '%s', gameName: '%s', saveName: '%s', notes='%s', filename: '%s' }",
				getFormat(), getGameId(), gameName, getSaveName(), notes, getFilename());
	}

	//region Load Game File

	public boolean load(File file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}

		format = null;
		filename = null;
		headerData = null;

		RandomAccessFile in = null;
		log.info("Start loading save game file: {}", file.getName());
		try {
			String extension = Files.getExtension(file);
			in = new RandomAccessFile(file, "r");

			if (Files.isXPortFile(extension)) {
				loadXPortFile(in);
			} else if (Files.isEmsFile(extension)) {
				loadEmsFile(in);
			} else if (Files.isARMaxFile(extension)) {
				loadARMaxFile(in);
			}

			if (null != format) {
				if (!isValidGameId(gameId)) {
					throw new IllegalStateException("Invalid Game ID: " + gameId);
				}

				if (format != OfFormat.arMax) {
					int len = in.read(data);
					assert len == LENGTH : "Cannot read " + LENGTH + " bytes of data, actual: " + len;
				}

				checksum(data, true);
				decrypt(data);
			}

			log.info("Loading of {} save game file succeeded", format);
		} catch (Exception e) {
			log.error("Failed to load save game file:", e);
			format = null;
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		if (null != format) {
			filename = file.getName();
			return true;
		}

		return false;
	}

	private void loadARMaxFile(RandomAccessFile in) throws IOException {
		byte[] temp = new byte[(int) in.length()];
		int len = in.read(temp);
		assert len == temp.length : "Failed to read entire ARMax file, expected: " + temp.length + ", actual: " + len;

		int ofs = 0;
		String magic = new String(temp, ofs, MAGIC_MAX.length(), Strings.ANSI);
		if (!MAGIC_MAX.equals(magic)) {
			log.warn("Invalid ARMax magic: {}", magic);
			return;
		}

		ofs += MAGIC_MAX.length();
		int chk = Bits.toInt(temp, ofs);
		System.arraycopy(Bits.ZERO_INT, 0, temp, ofs, Bits.ZERO_INT.length);

		CRC32 crc32 = new CRC32();
		crc32.update(temp);
		long crc = crc32.getValue();

		if ((int) crc != chk) {
			log.warn("Invalid ARMax CRC32 0x{}, expected: 0x{}", Integer.toHexString(chk), Long.toHexString(crc));
			return;
		}

		temp = new byte[32];
		in.seek(16);
		len = in.read(temp);
		gameId = Strings.readANSI(temp, 0, len);

		len = in.read(temp);
		gameName = Strings.readANSI(temp, 0, len);

		int codeSize = Bits.swabInt(in.readInt());
		filesCount = Bits.swabInt(in.readInt());

		temp = new byte[codeSize];
		len = in.read(temp);
		assert len == codeSize : "Cannot read " + codeSize + " bytes of compressed, actual: " + len;

		LZAri lzAri = new LZAri();
		temp = lzAri.decode(temp, 0, temp.length);

		ofs = 0;
		for (int i = 0; i < filesCount; i++) {
			int size = Bits.toInt(temp, ofs);
			String title = new String(temp, ofs + 4, GAME_LEN, Strings.ANSI);

			if (size == data.length && title.equals(gameId)) {
				ofs += 36;

				headerData = new byte[ofs];
				System.arraycopy(temp, 0, headerData, 0, ofs);
				System.arraycopy(temp, ofs, data, 0, data.length);

				format = OfFormat.arMax;
				break;
			} else {
				ofs += 36 + size;
				ofs = (ofs + 23) / 16 * 16 - 8;
			}
		}
	}

	private void loadEmsFile(RandomAccessFile in) throws IOException {
		headerData = new byte[(int) in.length() - data.length];
		int len = in.read(headerData);
		assert len == headerData.length : "Invalid EMS header length: " + len + ", expected: " + headerData.length;

		gameId = new String(headerData, 64, GAME_LEN, Strings.ANSI);

		format = OfFormat.ems;
	}

	private void loadXPortFile(RandomAccessFile in) throws IOException {
		long endOfs = in.length() - data.length - 4;

		int ofs = 4;
		in.seek(ofs);
		byte[] temp = new byte[SHARK_PORT.length() - ofs];
		int len = in.read(temp);

		String magic = new String(temp, 0, len, Strings.ANSI);
		if (!SHARK_PORT.substring(ofs).equals(magic)) {
			log.warn("Invalid XPort magic: {}", magic);
			return;
		}

		in.readInt();
		ofs += SHARK_PORT.length();

		int size = Bits.swabInt(in.readInt());
		ofs += 4;
		temp = new byte[size];
		len = in.read(temp);
		ofs += size;
		gameName = new String(temp, 0, len, Strings.ANSI);

		size = Bits.swabInt(in.readInt());
		ofs += 4;
		temp = new byte[size];
		len = in.read(temp);
		ofs += size;
		saveName = new String(temp, 0, len, Strings.ANSI);

		size = Bits.swabInt(in.readInt());
		ofs += 4;
		temp = new byte[size];
		len = in.read(temp);
		ofs += size;
		notes = new String(temp, 0, len, Strings.ANSI);

		headerData = new byte[(int) endOfs - ofs];
		len = in.read(headerData);
		assert len == headerData.length : "Invalid XPort header length: " + len + ", expected: " + headerData.length;

		gameId = new String(headerData, 6, GAME_LEN, Strings.ANSI);

		format = OfFormat.xPort;
	}

	//endregion

	//region Save Game File

	public boolean save(File file) {
		if (null == format) {
			return false;
		}
		if (null == file) {
			throw new NullArgumentException("file");
		}
		log.debug("Try to save OF file to: {}", file);

		//data[49] = 1;
		//data[50] = 1;
		//data[5942] = 1;
		//data[5943] = 1;

		encrypt(data);
		checksum(data, false);

		RandomAccessFile out = null;
		try {
			out = new RandomAccessFile(file, "rw");
			if (format == OfFormat.arMax) {
				saveARMaxFile(out);
			} else {
				if (format == OfFormat.xPort) {
					saveXPortFile(out);
				} else if (format == OfFormat.ems) {
					out.write(headerData);
				}

				out.write(data);

				if (format == OfFormat.xPort) {
					out.write(Bits.ZERO_INT);// skip the last CRC32 of XPort file
				}
			}
		} catch (Exception e) {
			log.error("Failed to save game file:", e);
			return false;
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}

			decrypt(data);
		}

		log.debug("Saving of OF file '{}' completed", file.getName());
		return true;
	}

	private void saveXPortFile(RandomAccessFile out) throws IOException {
		out.writeBytes(SHARK_PORT);
		out.writeInt(0);

		out.writeInt(Bits.swabInt(gameName.length()));
		out.writeBytes(gameName);

		out.writeInt(Bits.swabInt(saveName.length()));
		out.writeBytes(saveName);

		out.writeInt(Bits.swabInt(notes.length()));
		out.writeBytes(notes);

		out.write(headerData);
	}

	private void saveARMaxFile(RandomAccessFile out) throws IOException {
		int textSize = headerData.length + data.length;
		textSize = (textSize + 23) / 16 * 16 - 8;

		byte[] temp = new byte[textSize];
		System.arraycopy(headerData, 0, temp, 0, headerData.length);
		System.arraycopy(data, 0, temp, headerData.length, data.length);

		LZAri lzAri = new LZAri();
		temp = lzAri.encode(temp, 0, temp.length);
		int codeSize = temp.length;

		byte[] header = new byte[88];
		System.arraycopy(MAGIC_MAX.getBytes(Strings.ANSI), 0, header, 0, MAGIC_MAX.length());
		System.arraycopy(gameId.getBytes(Strings.ANSI), 0, header, 16, GAME_LEN);
		System.arraycopy(gameName.getBytes(Strings.ANSI), 0, header, 48, gameName.length());
		Bits.toBytes(codeSize, header, 80);
		Bits.toBytes(filesCount, header, 84);

		CRC32 crc32 = new CRC32();
		crc32.update(header);
		crc32.update(temp);
		long crc = crc32.getValue();

		Bits.toBytes(crc, header, 12, 4);

		out.write(header);
		out.write(temp);
	}

	//endregion

	public boolean saveData(File file) {
		if (!isLoaded()) {
			return false;
		}
		if (null == file) {
			throw new NullArgumentException("file");
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, false);
			out.write(data);

		} catch (Exception e) {
			log.error("Failed to dump raw data:", e);
			return false;
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		return true;
	}

	//region Encryption / Decryption Methods

	private static final int[][] BLOCKS = {
			{12, 4852},
			{5144, 2296},
			{7608, 4472},    // Stadiums + Leagues
			{12092, 22816},  // Editable Players
			{34920, 613800}, // Players + Boots
			{649560, 90227}, // Teams + Formations
			{739800, 11440}, // Clubs
			{751252, 163432},// Kits + Logos
			{914696, 155624},// Emblems
			{1070332, 2600},
			{1074144, 20752}
	};

	private static final int[] KEYS = /*446*/{
			0x7ab36882, 0x7ab3689e, 0x7ab368bd, 0x7ab3689f, 0x7ab368a4, 0x7ab368c3, 0x7ab36899, 0x7ab368b9, 0x7ab36894,
			0x7ab368b5, 0x7ab368af, 0x7ab3687d, 0x7ab368b4, 0x7ab368bf, 0x7ab368b0, 0x7ab36894, 0x7ab368b5, 0x7ab368c5,
			0x7ab368c3, 0x7ab36897, 0x7ab36890, 0x7ab3687d, 0x7ab3689e, 0x7ab368a5, 0x7ab3688e, 0x7ab368c4, 0x7ab368bd,
			0x7ab3687d, 0x7ab368c3, 0x7ab3689c, 0x7ab36881, 0x7ab368ad, 0x7ab36892, 0x7ab36883, 0x7ab368a2, 0x7ab368c2,
			0x7ab3687d, 0x7ab368ad, 0x7ab36884, 0x7ab368c0, 0x7ab36899, 0x7ab3688f, 0x7ab368b4, 0x7ab368b6, 0x7ab368b7,
			0x7ab36885, 0x7ab368a5, 0x7ab3689e, 0x7ab3689d, 0x7ab368c3, 0x7ab3689c, 0x7ab368a6, 0x7ab368c4, 0x7ab368bf,
			0x7ab368a3, 0x7ab368bd, 0x7ab36898, 0x7ab3687f, 0x7ab368a1, 0x7ab36881, 0x7ab3689a, 0x7ab36880, 0x7ab368a6,
			0x7ab368be, 0x7ab368b2, 0x7ab368b6, 0x7ab368b9, 0x7ab36882, 0x7ab3687e, 0x7ab368a6, 0x7ab368af, 0x7ab368a3,
			0x7ab36891, 0x7ab3688e, 0x7ab3687f, 0x7ab368c4, 0x7ab368bd, 0x7ab368b4, 0x7ab36894, 0x7ab368b4, 0x7ab368bd,
			0x7ab368b8, 0x7ab3689f, 0x7ab368a4, 0x7ab368a4, 0x7ab368a3, 0x7ab368a4, 0x7ab368b2, 0x7ab368b0, 0x7ab36896,
			0x7ab36894, 0x7ab368be, 0x7ab368ae, 0x7ab3687d, 0x7ab368c2, 0x7ab368a2, 0x7ab368c4, 0x7ab36894, 0x7ab368c2,
			0x7ab368b1, 0x7ab368a5, 0x7ab368c3, 0x7ab36898, 0x7ab36894, 0x7ab3687f, 0x7ab368b3, 0x7ab3689c, 0x7ab368b5,
			0x7ab368a6, 0x7ab368c5, 0x7ab36884, 0x7ab368bd, 0x7ab36896, 0x7ab36882, 0x7ab368c6, 0x7ab3687e, 0x7ab368bd,
			0x7ab368b2, 0x7ab368b3, 0x7ab36890, 0x7ab36880, 0x7ab368b7, 0x7ab36895, 0x7ab3689b, 0x7ab36894, 0x7ab368a5,
			0x7ab368be, 0x7ab368bf, 0x7ab368c2, 0x7ab368be, 0x7ab368a1, 0x7ab368a4, 0x7ab3689d, 0x7ab36899, 0x7ab368a4,
			0x7ab368b4, 0x7ab368b8, 0x7ab3688f, 0x7ab368a3, 0x7ab36881, 0x7ab36891, 0x7ab368bd, 0x7ab368b9, 0x7ab368b3,
			0x7ab368b9, 0x7ab3688e, 0x7ab36895, 0x7ab36896, 0x7ab36891, 0x7ab36892, 0x7ab3687f, 0x7ab368b4, 0x7ab368bb,
			0x7ab3689d, 0x7ab3688e, 0x7ab36882, 0x7ab368c1, 0x7ab368a0, 0x7ab36891, 0x7ab368a3, 0x7ab368af, 0x7ab368bf,
			0x7ab368b0, 0x7ab3687c, 0x7ab368c4, 0x7ab36885, 0x7ab368a4, 0x7ab3689d, 0x7ab368c6, 0x7ab368b4, 0x7ab36898,
			0x7ab3687c, 0x7ab36899, 0x7ab368b8, 0x7ab368c5, 0x7ab3689a, 0x7ab36895, 0x7ab368be, 0x7ab36882, 0x7ab3687d,
			0x7ab3687d, 0x7ab3687f, 0x7ab3687f, 0x7ab368ba, 0x7ab36890, 0x7ab368b3, 0x7ab368af, 0x7ab3688f, 0x7ab36893,
			0x7ab36894, 0x7ab36881, 0x7ab368bc, 0x7ab368b2, 0x7ab368c3, 0x7ab368b3, 0x7ab368b1, 0x7ab368ba, 0x7ab368b1,
			0x7ab368c4, 0x7ab368c4, 0x7ab368be, 0x7ab368c6, 0x7ab368b3, 0x7ab368bf, 0x7ab368ba, 0x7ab368bd, 0x7ab368b5,
			0x7ab368ad, 0x7ab3689d, 0x7ab36885, 0x7ab3688d, 0x7ab368b2, 0x7ab36882, 0x7ab368a6, 0x7ab368b8, 0x7ab368b2,
			0x7ab368c1, 0x7ab368b6, 0x7ab36891, 0x7ab3689d, 0x7ab368a2, 0x7ab3687c, 0x7ab36882, 0x7ab368a5, 0x7ab368c5,
			0x7ab3687d, 0x7ab368c5, 0x7ab368c1, 0x7ab368a2, 0x7ab36883, 0x7ab368be, 0x7ab368b7, 0x7ab368b0, 0x7ab36895,
			0x7ab36884, 0x7ab368c2, 0x7ab3687e, 0x7ab3687c, 0x7ab3687f, 0x7ab368bc, 0x7ab368a3, 0x7ab3689b, 0x7ab3689d,
			0x7ab36895, 0x7ab36882, 0x7ab368b5, 0x7ab3688f, 0x7ab368a2, 0x7ab36884, 0x7ab368bc, 0x7ab368ba, 0x7ab368bd,
			0x7ab368c0, 0x7ab3688e, 0x7ab36897, 0x7ab36883, 0x7ab3687f, 0x7ab368b1, 0x7ab3688e, 0x7ab368b4, 0x7ab36880,
			0x7ab368a0, 0x7ab368a0, 0x7ab3689d, 0x7ab3687f, 0x7ab36880, 0x7ab368b0, 0x7ab368b4, 0x7ab368c6, 0x7ab368b9,
			0x7ab368b0, 0x7ab36898, 0x7ab368a5, 0x7ab368a4, 0x7ab3688d, 0x7ab368c2, 0x7ab3687c, 0x7ab36881, 0x7ab36885,
			0x7ab368ad, 0x7ab36890, 0x7ab36881, 0x7ab368a3, 0x7ab36895, 0x7ab36899, 0x7ab368b8, 0x7ab368a0, 0x7ab368b2,
			0x7ab368b7, 0x7ab368ad, 0x7ab36881, 0x7ab3687c, 0x7ab368b3, 0x7ab368ae, 0x7ab36896, 0x7ab368b7, 0x7ab36896,
			0x7ab3688e, 0x7ab3689e, 0x7ab368b6, 0x7ab368b8, 0x7ab368bd, 0x7ab36894, 0x7ab36893, 0x7ab368bc, 0x7ab368bc,
			0x7ab3689c, 0x7ab368a6, 0x7ab36885, 0x7ab368b8, 0x7ab368ad, 0x7ab36896, 0x7ab368b9, 0x7ab368be, 0x7ab368ae,
			0x7ab368c0, 0x7ab368a6, 0x7ab36881, 0x7ab368b5, 0x7ab368a6, 0x7ab368bc, 0x7ab36897, 0x7ab368af, 0x7ab3689f,
			0x7ab368b1, 0x7ab368c4, 0x7ab368bf, 0x7ab3687e, 0x7ab368b7, 0x7ab36897, 0x7ab36893, 0x7ab3688f, 0x7ab368b0,
			0x7ab36880, 0x7ab3689f, 0x7ab368b7, 0x7ab36894, 0x7ab368c2, 0x7ab3689f, 0x7ab36898, 0x7ab368b6, 0x7ab368c3,
			0x7ab36885, 0x7ab3687f, 0x7ab368a4, 0x7ab3687e, 0x7ab368be, 0x7ab3687c, 0x7ab368bc, 0x7ab368b5, 0x7ab3689e,
			0x7ab36899, 0x7ab368a6, 0x7ab36881, 0x7ab368bb, 0x7ab36895, 0x7ab3689d, 0x7ab368a6, 0x7ab3689f, 0x7ab368be,
			0x7ab36880, 0x7ab368c4, 0x7ab368b5, 0x7ab368bc, 0x7ab368a1, 0x7ab368ad, 0x7ab368c1, 0x7ab36891, 0x7ab3687d,
			0x7ab368a0, 0x7ab368bd, 0x7ab3689b, 0x7ab368b1, 0x7ab36897, 0x7ab36881, 0x7ab36885, 0x7ab3689e, 0x7ab36882,
			0x7ab3689b, 0x7ab36893, 0x7ab368a3, 0x7ab3688d, 0x7ab368c4, 0x7ab368c4, 0x7ab3689b, 0x7ab368ad, 0x7ab368c4,
			0x7ab368b4, 0x7ab368ad, 0x7ab36884, 0x7ab368a5, 0x7ab3687f, 0x7ab368af, 0x7ab368a1, 0x7ab3687d, 0x7ab3687c,
			0x7ab368b5, 0x7ab368b1, 0x7ab368be, 0x7ab368c3, 0x7ab368b0, 0x7ab36895, 0x7ab36885, 0x7ab36882, 0x7ab368c0,
			0x7ab368a5, 0x7ab368b8, 0x7ab368a2, 0x7ab36899, 0x7ab368b3, 0x7ab368a1, 0x7ab3687d, 0x7ab3689f, 0x7ab36897,
			0x7ab368a0, 0x7ab3687f, 0x7ab368c1, 0x7ab3689c, 0x7ab368a2, 0x7ab368c5, 0x7ab368a6, 0x7ab3687e, 0x7ab368b3,
			0x7ab36894, 0x7ab3689a, 0x7ab368bc, 0x7ab36890, 0x7ab3689d, 0x7ab368c3, 0x7ab36892, 0x7ab3689a, 0x7ab368b8,
			0x7ab368a2, 0x7ab368c3, 0x7ab368c5, 0x7ab368a2, 0x7ab368c5, 0x7ab36880, 0x7ab36885, 0x7ab368bb, 0x7ab3689f,
			0x7ab368c4, 0x7ab368a1, 0x7ab368a6, 0x7ab36890, 0x7ab3684c
	};

	public static final int KEY_MASK = KEYS[KEYS.length - 1];

	public static int blockAddress(int index) {
		return BLOCKS[index][0];
	}

	public static int blockSize(int index) {
		return BLOCKS[index][1];
	}

	private static void decrypt(byte[] data) {
		int k, v, p;
		for (int i = 1; i < BLOCKS.length; i++) {
			k = 0;
			for (int ofs = BLOCKS[i][0], endOfs = BLOCKS[i][0] + BLOCKS[i][1] - 4; ofs <= endOfs; ofs += 4) {
				v = Bits.toInt(data, ofs);
				p = Bits.toInt((Bits.toInt64(v) - KEYS[k]) + KEY_MASK) ^ KEY_MASK;

				Bits.toBytes(p, data, ofs);

				if (++k >= KEYS.length) {
					k = 0;
				}
			}
		}
	}

	private static void encrypt(byte[] data) {
		int k, p, v;
		for (int i = 1; i < BLOCKS.length; i++) {
			k = 0;
			for (int ofs = BLOCKS[i][0], endOfs = BLOCKS[i][0] + BLOCKS[i][1] - 4; ofs <= endOfs; ofs += 4) {
				p = Bits.toInt(data, ofs);
				v = Bits.toInt((Bits.toInt64(p ^ KEY_MASK) - KEY_MASK) + KEYS[k]);

				Bits.toBytes(v, data, ofs);

				if (++k >= KEYS.length) {
					k = 0;
				}
			}
		}
	}

	private static void checksum(byte[] data, boolean validate) {
		int adr, chk;
		for (int[] block : BLOCKS) {
			adr = block[0] - 8;
			if (adr < 0) {
				continue;
			}

			// calculates simple checksum for each blocks
			chk = 0;
			for (int ofs = block[0], endOfs = block[0] + block[1] - 4; ofs <= endOfs; ofs += 4) {
				chk = Bits.toInt(Bits.toInt64(chk) + Bits.toInt(data, ofs));
			}

			// 4 bytes checksum before each blocks
			if (validate) {
				int crc = Bits.toInt(data, adr);
				if (crc != chk) {
					throw new IllegalStateException(String.format("Invalid checksum 0x%X, expected: 0x%X", crc, chk));
				}
			} else {
				Bits.toBytes(chk, data, adr);
			}
		}
	}

	//endregion
}
