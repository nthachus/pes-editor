package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public final class OptionFileTest extends BaseTest {
	private final static int[] BLOCKS = {
			12, 5144, 7608, 12092, 34920, 649560, 739800, 751252, 914696, 1070332, 1074144
	};

	@Test
	public void testAddresses() throws Exception {
		Assert.assertTrue((OptionFile.LENGTH % 4) == 0);
		Assert.assertEquals(1095680, OptionFile.LENGTH);
		Assert.assertEquals(2058577996, OptionFile.KEY_MASK);

		for (int i = 0; i < BLOCKS.length; i++) {
			Assert.assertEquals(BLOCKS[i], OptionFile.blockAddress(i));
		}
	}

	@Test
	public void testEncryptAndDecryptForOriginalOF() throws Exception {
		testEncryptAndDecrypt(OF_ORIGINAL);
	}

	@Test
	public void testEncryptAndDecryptForLicensedOF() throws Exception {
		testEncryptAndDecrypt(OF_LICENSED);
	}

	@Test
	public void testEncryptAndDecryptForLatestOF() throws Exception {
		testEncryptAndDecrypt(OF_LATEST);
	}

	private void testEncryptAndDecrypt(String fn) throws Exception {
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
	public void testLoadWithNullFile() throws Exception {
		OptionFile of = new OptionFile();
		of.load(null);
	}

	@Test
	public void testLoadWithNonExistsFile() throws Exception {
		OptionFile of = new OptionFile();
		boolean res = of.load(new File(getClass().getName()));
		Assert.assertEquals(false, res);
	}

	@Test
	public void testSaveBeforeLoad() throws Exception {
		OptionFile of = new OptionFile();
		Assert.assertEquals(false, of.save(null));
	}

	@Test(expected = NullArgumentException.class)
	public void testSaveWithNullFile() throws Exception {
		OptionFile of = loadLatestOF();
		of.save(null);
	}

}
