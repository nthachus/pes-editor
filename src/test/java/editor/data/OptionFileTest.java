package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public final class OptionFileTest extends BaseTest {
	private static final int[] BLOCKS = {
			12, 5144, 7608, 12092, 34920, 649560, 739800, 751252, 914696, 1070332, 1074144
	};

	@Test
	public void testAddresses() throws NoSuchFieldException, IllegalAccessException {
		Assert.assertEquals(0, (OptionFile.LENGTH % 4));
		Assert.assertEquals(1095680, OptionFile.LENGTH);
		Assert.assertEquals(2058577996, readStaticField(OptionFile.class, "KEY_MASK", true, true));

		for (int i = 0; i < BLOCKS.length; i++) {
			Assert.assertEquals(BLOCKS[i], OptionFile.blockAddress(i));
		}
	}

	@Test
	public void testEncryptAndDecryptForOriginalOF() throws IOException {
		testEncryptAndDecrypt(OF_ORIGINAL);
	}

	@Test
	public void testEncryptAndDecryptForLicensedOF() throws IOException {
		testEncryptAndDecrypt(OF_LICENSED);
	}

	@Test
	public void testEncryptAndDecryptForLatestOF() throws IOException {
		testEncryptAndDecrypt(OF_LATEST);
	}

	private void testEncryptAndDecrypt(String fn) throws IOException {
		OptionFile of = loadOptionFile(fn);
		// DEBUG
		File fs = createTempFile(fn, "raw" + Files.EXT_SEPARATOR + "bin");
		of.saveData(fs);

		fs = createTempFile(fn, "recoded" + Files.EXT_SEPARATOR + of.getFormat());
		boolean res = of.save(fs);
		Assert.assertTrue(res);

		// compares with original file
		byte[] recoded = Files.readBytes(fs);
		byte[] original = Files.readBytes(getResourceFile(fn));
		Assert.assertNotNull(recoded);
		Assert.assertNotNull(original);

		if (of.getFormat() == OfFormat.xPort) {
			int adr = recoded.length - 4;
			System.arraycopy(recoded, adr, original, adr, 4);
		}

		Assert.assertArrayEquals("Incorrect recoded OF: " + fn, original, recoded);
	}

	@Test(expected = NullArgumentException.class)
	public void testLoadWithNullFile() {
		OptionFile of = new OptionFile();
		of.load(null);
	}

	@Test
	public void testLoadWithNonExistsFile() {
		OptionFile of = new OptionFile();
		boolean res = of.load(new File(getClass().getName()));
		Assert.assertFalse(res);
	}

	@Test
	public void testSaveBeforeLoad() {
		OptionFile of = new OptionFile();
		Assert.assertFalse(of.save(null));
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullFile() {
		OptionFile of = loadLatestOF();
		of.save(null);
	}

	@Test
	public void testExportRelink() {
		OptionFile of = loadLatestOF();
		boolean res = of.exportRelink(System.getProperty("java.io.tmpdir"));
		Assert.assertTrue(res);
	}

}
