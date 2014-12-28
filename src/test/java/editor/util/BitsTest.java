package editor.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public final class BitsTest {
	@Test
	public void testToBytes() throws Exception {
		int num = 0xAA112233;
		byte[] a4 = new byte[]{0x33, 0x22, 0x11, (byte) 0xAA};
		byte[] a3 = Arrays.copyOf(a4, 3);

		int i = Bits.toInt(a4, 0);
		Assert.assertEquals(num, i);

		long n = Bits.toInt(a3, 0, a3.length);
		Assert.assertEquals(0x112233, n);

		byte[] arr = Bits.toBytes(num);
		Assert.assertArrayEquals(a4, arr);

		arr = new byte[a3.length * 2];
		Bits.toBytes(n, arr, a3.length, a3.length);
		Assert.assertArrayEquals(a3, Arrays.copyOfRange(arr, a3.length, arr.length));
	}

	@Test(expected = NullPointerException.class)
	public void testToBytesWithNullBuffer() throws Exception {
		Bits.toBytes(Byte.MIN_VALUE, null, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testToBytesWithInvalidOffset() throws Exception {
		Bits.toBytes(Short.MIN_VALUE, new byte[0], -1);
	}

}
