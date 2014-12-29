package editor.util;

public final class Bits {
	private Bits() {
	}

	public static byte toByte(int value) {
		return (byte) (value & 0xFF);
	}

	public static byte toByte(long value) {
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

	public static final byte[] ZERO_INT = new byte[]{0, 0, 0, 0};
	public static final byte[] ZERO_INT16 = new byte[]{0, 0};

	public static byte[] toBytes(int value) {
		if (value == 0)
			return ZERO_INT;

		byte[] temp = new byte[ZERO_INT.length];
		toBytes(value, temp, 0);
		return temp;
	}

	public static byte[] toBytes(short value) {
		if (value == 0)
			return ZERO_INT16;

		byte[] temp = new byte[ZERO_INT16.length];
		toBytes(value, temp, 0);
		return temp;
	}

	public static long toInt(byte[] bytes, int index, int count) {
		if (null == bytes) throw new NullPointerException("bytes");
		if (index < 0 || index + count > bytes.length)
			throw new IndexOutOfBoundsException(String.format("%d + %d > %d", index, count, bytes.length));

		long value = 0;
		for (int i = count - 1; i >= 0; i--)
			value = (value << 8) | toInt(bytes[i + index]);

		return value;
	}

	public static int toInt(byte[] buffer, int offset) {
		return (int) toInt(buffer, offset, 4);
	}

	public static int toInt16(byte[] buffer, int offset) {
		return (int) toInt(buffer, offset, 2);
	}

	public static void toBytes(long value, byte[] bytes, int index, int count) {
		if (null == bytes) throw new NullPointerException("bytes");
		if (index < 0 || index + count > bytes.length)
			throw new IndexOutOfBoundsException(String.format("%d + %d > %d", index, count, bytes.length));

		for (int i = 0; i < count; i++) {
			bytes[i + index] = toByte(value);
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

}