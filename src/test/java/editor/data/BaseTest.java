package editor.data;

import editor.util.Files;
import editor.util.Strings;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public abstract class BaseTest {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected static final String OF_ORIGINAL = "PES2013_SLES-55666_EN_Top.psu";
	protected static final String OF_LICENSED = "PES2013_OF_FredPSG_V1.2.max";
	protected static final String OF_LATEST = "PES2013_VRS_2.0_UK-Dany.xps";

	protected static final String[] OF_ALL = {OF_ORIGINAL, OF_LICENSED, OF_LATEST};

	public static final String IMG_FORMAT = "png";

	public static String getResourcePath(String resourceName) {
		if (!Strings.isEmpty(resourceName) && !resourceName.startsWith("/"))
			return "/" + resourceName;
		return resourceName;
	}

	public static File getResourceFile(String resourceName) throws URISyntaxException {
		URL url = BaseTest.class.getResource(getResourcePath(resourceName));
		Assert.assertNotNull("Resource file '" + resourceName + "' was not found.", url);
		return new File(url.toURI());
	}

	public static InputStream getResourceStream(String resourceName) {
		InputStream fs = BaseTest.class.getResourceAsStream(getResourcePath(resourceName));
		Assert.assertNotNull("Resource file '" + resourceName + "' was not found.", fs);
		return fs;
	}

	public static File createTempFile(File file, String extension) throws IOException {
		if (null == file) throw new NullPointerException("file");
		return createTempFile(file.getName(), extension);
	}

	public static File createTempFile(String filename, String extension) throws IOException {
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

	protected static final Random rand = new Random();

	public static String randomString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = (char) (rand.nextInt(128 - 32) + 32);
			sb.append(c);
		}
		return sb.toString();
	}

	public static String randomString(int minLength, int maxLength) {
		int l = rand.nextInt(maxLength + 1 - minLength) + minLength;
		return randomString(l);
	}

	public static Color randomColor() {
		return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}

	public static <T> java.util.List<T> readFields(
			Class<?> clazz, Object target, Class<T> ofType, Boolean isFinal, boolean forceAccess) throws Exception {
		if (null == clazz) throw new NullPointerException("clazz");
		if (null == ofType) throw new NullPointerException("ofType");

		java.util.List<T> list = new ArrayList<T>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if ((null == target) == Modifier.isStatic(f.getModifiers())
					&& (null == isFinal || isFinal == Modifier.isFinal(f.getModifiers()))
					&& ofType.isAssignableFrom(f.getType())) {

				if (forceAccess)
					f.setAccessible(true);

				Object obj = f.get(target);
				list.add(ofType.cast(obj));
			}
		}

		return list;
	}

	public static <T> java.util.List<T> readStaticFields(
			Class<?> clazz, Class<T> ofType, Boolean isFinal, boolean forceAccess) throws Exception {
		return readFields(clazz, null, ofType, isFinal, forceAccess);
	}

	public static Object readField(Class<?> clazz, Object target, String name, Boolean isFinal, boolean forceAccess)
			throws Exception {
		if (null == clazz) throw new NullPointerException("clazz");
		if (Strings.isBlank(name)) throw new NullPointerException("name");

		Field f = clazz.getDeclaredField(name);

		if ((null == target) != Modifier.isStatic(f.getModifiers())
				|| (null != isFinal && isFinal != Modifier.isFinal(f.getModifiers())))
			throw new IllegalAccessException(f.toString());

		if (forceAccess)
			f.setAccessible(true);

		return f.get(target);
	}

	public static Object readStaticField(Class<?> clazz, String name, Boolean isFinal, boolean forceAccess)
			throws Exception {
		return readField(clazz, null, name, isFinal, forceAccess);
	}

}
