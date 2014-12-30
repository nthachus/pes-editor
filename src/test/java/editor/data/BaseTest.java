package editor.data;

import editor.util.Files;
import editor.util.Strings;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class BaseTest {
	protected static final String OF_ORIGINAL = "PES2013_SLES-55666_EN_Top.psu";
	protected static final String OF_LICENSED = "PES2013_OF_FredPSG_V1.2.max";
	protected static final String OF_LATEST = "PES2013_VRS_2.0_UK-Dany.xps";

	protected static final String[] OF_ALL = {OF_ORIGINAL, OF_LICENSED, OF_LATEST};

	protected static final String IMG_FORMAT = "png";

	protected static String getResourcePath(String resourceName) {
		if (!Strings.isEmpty(resourceName) && !resourceName.startsWith("/"))
			return "/" + resourceName;
		return resourceName;
	}

	protected static File getResourceFile(String resourceName) throws URISyntaxException {
		URL url = BaseTest.class.getResource(getResourcePath(resourceName));
		Assert.assertNotNull("Resource file '" + resourceName + "' was not found.", url);
		return new File(url.toURI());
	}

	protected static InputStream getResourceStream(String resourceName) {
		InputStream fs = BaseTest.class.getResourceAsStream(getResourcePath(resourceName));
		Assert.assertNotNull("Resource file '" + resourceName + "' was not found.", fs);
		return fs;
	}

	protected static File createTempFile(File file, String extension) throws IOException {
		if (null == file) throw new NullPointerException("file");
		return createTempFile(file.getName(), extension);
	}

	protected static File createTempFile(String filename, String extension) throws IOException {
		if (null == filename) throw new NullPointerException("filename");
		return File.createTempFile(Files.removeExtension(filename) + '_', Files.EXT_SEPARATOR + extension);
	}

	protected static OptionFile loadOptionFile(String filename) throws Exception {
		File fs = getResourceFile(filename);

		OptionFile of = new OptionFile();
		boolean res = of.load(fs);

		Assert.assertTrue("Failed to load OptionFile: " + filename, res);
		Assert.assertTrue("Unable to load OF: " + filename, of.isLoaded());
		return of;
	}

	protected static OptionFile loadOriginalOF() throws Exception {
		return loadOptionFile(OF_ORIGINAL);
	}

	protected static OptionFile loadLatestOF() throws Exception {
		return loadOptionFile(OF_LATEST);
	}

}
