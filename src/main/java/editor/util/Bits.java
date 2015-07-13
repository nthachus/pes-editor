package editor.util;

import editor.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bits {
	private static final Logger log = LoggerFactory.getLogger(Bits.class);

	private Bits() {
	}

	public static byte toByte(int value) {
		return (byte) (value & 0xFF);
	}

	public static byte toByte(boolean value) {
		return value ? (byte) 1 : (byte) 0;
	}

	public static int toInt(byte value) {
		return ((int) value & 0xFF);
	}

	public static int toInt(short value) {
		return ((int) value & 0xFFFF);
	}

	public static long toInt64(int value) {
		return ((long) value & 0xFFFFFFFFL);
	}

	public static int toInt(long value) {
		return (int) (value & 0xFFFFFFFFL);
	}

	public static short toInt16(int value) {
		return (short) (value & 0xFFFF);
	}

	public static byte[] toBytes(int value) {
		if (value == 0) {
			return new byte[]{0, 0, 0, 0};
		}

		byte[] temp = new byte[4];
		toBytes(value, temp, 0);
		return temp;
	}

	public static byte[] toBytes(short value) {
		if (value == 0) {
			return new byte[]{0, 0};
		}

		byte[] temp = new byte[2];
		toBytes(value, temp, 0);
		return temp;
	}

	public static long toInt(byte[] bytes, int index, int count, long maxValue) {
		if (null == bytes) {
			throw new NullArgumentException("bytes");
		}
		if (index < 0 || index + count > bytes.length) {
			throw new IndexOutOfBoundsException(String.format("%d + %d > %d", index, count, bytes.length));
		}

		long value = 0;
		for (int i = count - 1; i >= 0; i--) {
			value = (value << 8) | toInt(bytes[i + index]);
		}

		// auto-fix out-of-range value
		if (maxValue > 0 && value > maxValue) {
			long incorrect = value;
			do {
				value >>>= 1;
			} while (value > maxValue);
			toBytes(value, bytes, index, count);
			// DEBUG
			log.warn("Fixed {} bytes at {}: {} -> {} (max {})", count, index, incorrect, value, maxValue);
		}

		return value;
	}

	public static long toInt(byte[] bytes, int index, int count) {
		return toInt(bytes, index, count, -1L);
	}

	public static int toInt(byte[] buffer, int offset) {
		return (int) toInt(buffer, offset, 4);
	}

	public static int toInt16(byte[] buffer, int offset) {
		return (int) toInt(buffer, offset, 2);
	}

	public static void toBytes(long value, byte[] bytes, int index, int count) {
		if (null == bytes) {
			throw new NullArgumentException("bytes");
		}
		if (index < 0 || index + count > bytes.length) {
			throw new IndexOutOfBoundsException(String.format("%d + %d > %d", index, count, bytes.length));
		}

		for (int i = 0; i < count; i++) {
			bytes[i + index] = (byte) (0xFF & value);
			value >>>= 8;
		}
	}

	public static void toBytes(int value, byte[] buffer, int offset) {
		toBytes(value, buffer, offset, 4);
	}

	public static void toBytes(short value, byte[] buffer, int offset) {
		toBytes(value, buffer, offset, 2);
	}

	public static int swabInt(int value) {
		return (value >>> 24)
				| (value << 24)
				| ((value << 8) & 0xFF0000)
				| ((value >> 8) & 0x00FF00);
	}

	public static int bitLength(int n) {
		int i = 0;
		while (n != 0 && i++ < Integer.SIZE) {
			n = n >>> 1;
		}
		return i;
	}

}
