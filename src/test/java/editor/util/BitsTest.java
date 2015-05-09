package editor.util;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

public final class BitsTest {
	@Test
	public void testToBytes() {
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

	@Test(expected = NullArgumentException.class)
	public void testToBytesWithNullBuffer() {
		Bits.toBytes(Byte.MIN_VALUE, null, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testToBytesWithInvalidOffset() {
		Bits.toBytes(Short.MIN_VALUE, new byte[0], -1);
	}

	@Test
	public void testGetBitLength() {
		Assert.assertEquals(0, Bits.bitLength(0));
		Assert.assertEquals(1, Bits.bitLength(1));
		Assert.assertEquals(2, Bits.bitLength(2));
		Assert.assertEquals(2, Bits.bitLength(3));
		Assert.assertEquals(3, Bits.bitLength(5));
		Assert.assertEquals(4, Bits.bitLength(10));
		Assert.assertEquals(5, Bits.bitLength(26));
		Assert.assertEquals(6, Bits.bitLength(0x3F));
		Assert.assertEquals(7, Bits.bitLength(0x7F));
		Assert.assertEquals(9, Bits.bitLength(0x1FF));
		Assert.assertEquals(16, Bits.bitLength(0xE1FF));
	}

}
