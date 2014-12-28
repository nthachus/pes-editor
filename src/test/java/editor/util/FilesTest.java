package editor.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public final class FilesTest {
	@Test
	public void testGetExtension() throws Exception {
		String ext = Files.getExtension((String) null);
		Assert.assertNull(ext);

		ext = Files.getExtension("");
		Assert.assertEquals("", ext);

		ext = Files.getExtension("aa.bb.xx");
		Assert.assertEquals("xx", ext);

		ext = Files.getExtension("aa.bb.");
		Assert.assertEquals("", ext);

		ext = Files.getExtension(".");
		Assert.assertEquals("", ext);

		ext = Files.getExtension(" foo ");
		Assert.assertEquals("", ext);
	}

	@Test(expected = NullPointerException.class)
	public void testGetExtensionWithNullFile() throws Exception {
		Files.getExtension((File) null);
	}

	@Test
	public void testAddExtension() throws Exception {
		File fd = Files.addExtension(null, null);
		Assert.assertNull(fd);

		String fn = getClass().getSimpleName();
		File fs = new File(fn);

		fd = Files.addExtension(fs, null);
		Assert.assertEquals(fs, fd);

		fd = Files.addExtension(fs, Files.GIF);
		Assert.assertNotEquals(fs, fd);
		System.out.println("Added extension to filename: " + fd.getName());
		Assert.assertEquals(fn + Files.EXT_SEPARATOR + Files.GIF, fd.getName());

		File fd2 = Files.addExtension(fd, Files.GIF.toUpperCase());
		Assert.assertEquals(fd, fd2);
	}

	@Test
	public void testRemoveExtension() throws Exception {
		String fn = Files.removeExtension(null);
		Assert.assertNull(fn);

		fn = Files.removeExtension("");
		Assert.assertEquals("", fn);

		fn = Files.removeExtension("aa.bb .xx");
		Assert.assertEquals("aa.bb ", fn);

		fn = Files.removeExtension("aa.bb.");
		Assert.assertEquals("aa.bb", fn);

		fn = Files.removeExtension(".");
		Assert.assertEquals("", fn);

		fn = Files.removeExtension(" foo ");
		Assert.assertEquals(" foo ", fn);
	}

	@Test
	public void testIllegalFilename() throws Exception {
		Assert.assertEquals(true, Files.isFilenameLegal(null));
		Assert.assertEquals(true, Files.isFilenameLegal(""));
		Assert.assertEquals(true, Files.isFilenameLegal(" da%^()-"));
		Assert.assertEquals(false, Files.isFilenameLegal(" >"));
		Assert.assertEquals(false, Files.isFilenameLegal("!>:"));
		Assert.assertEquals(false, Files.isFilenameLegal("/"));
	}

	@Test
	public void testReadBytesWithNonExistsFile() throws Exception {
		byte[] data = Files.readBytes(new File(getClass().getName()));
		Assert.assertNull(data);
	}

	@Test(expected = NullPointerException.class)
	public void testReadBytesWithNullFile() throws Exception {
		Files.readBytes(null);
	}

}
