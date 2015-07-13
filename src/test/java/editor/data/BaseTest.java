package editor.data;

import editor.lang.NullArgumentException;
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

	protected static final String OF_ORIGINAL = "PES2014_SLES-55673_original.psu";
	protected static final String OF_LICENSED = "PES2014_SLES-55676_licensed.psu";
	protected static final String OF_LATEST = "PES2014-55673_TEMPORADA-2015_FINAL-KTS82.max";

	public static final String IMG_FORMAT = "png";

	private static String getResourcePath(String resourceName) {
		if (!Strings.isEmpty(resourceName) && !resourceName.startsWith("/")) {
			return "/" + resourceName;
		}
		return resourceName;
	}

	public static File getResourceFile(String resourceName) {
		URL url = BaseTest.class.getResource(getResourcePath(resourceName));
		Assert.assertNotNull("Resource file '" + resourceName + "' was not found.", url);
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	public static InputStream getResourceStream(String resourceName) {
		InputStream fs = BaseTest.class.getResourceAsStream(getResourcePath(resourceName));
		Assert.assertNotNull("Resource file '" + resourceName + "' was not found.", fs);
		return fs;
	}

	public static File createTempFile(File file, String extension) throws IOException {
		if (null == file) {
			throw new NullArgumentException("file");
		}
		return createTempFile(file.getName(), extension);
	}

	public static File createTempFile(String filename, String extension) throws IOException {
		if (null == filename) {
			throw new NullArgumentException("filename");
		}
		return File.createTempFile(Files.removeExtension(filename) + '_', Files.EXT_SEPARATOR + extension);
	}

	protected static OptionFile loadOptionFile(String filename) {
		File fs = getResourceFile(filename);

		OptionFile of = new OptionFile();
		boolean res = of.load(fs);

		Assert.assertTrue("Failed to load OptionFile: " + filename, res);
		Assert.assertTrue("Unable to load OF: " + filename, of.isLoaded());
		return of;
	}

	protected static OptionFile loadOriginalOF() {
		return loadOptionFile(OF_ORIGINAL);
	}

	protected static OptionFile loadLicensedOF() {
		return loadOptionFile(OF_LICENSED);
	}

	protected static OptionFile loadLatestOF() {
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
			Class<?> clazz, Object target, Class<T> ofType, Boolean isFinal, boolean forceAccess)
			throws IllegalAccessException {
		if (null == clazz) {
			throw new NullArgumentException("clazz");
		}
		if (null == ofType) {
			throw new NullArgumentException("ofType");
		}

		java.util.List<T> list = new ArrayList<T>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if (null != f
					&& (null == target) == Modifier.isStatic(f.getModifiers())
					&& (null == isFinal || isFinal == Modifier.isFinal(f.getModifiers()))
					&& ofType.isAssignableFrom(f.getType())
					&& !forceAccess == Modifier.isPublic(f.getModifiers())) {

				if (forceAccess) {
					f.setAccessible(true);
				}

				Object obj = f.get(target);
				list.add(ofType.cast(obj));
			}
		}

		return list;
	}

	public static <T> java.util.List<T> readStaticFields(
			Class<?> clazz, Class<T> ofType, Boolean isFinal, boolean forceAccess) throws IllegalAccessException {
		return readFields(clazz, null, ofType, isFinal, forceAccess);
	}

	public static Object readField(Class<?> clazz, Object target, String name, Boolean isFinal, boolean forceAccess)
			throws NoSuchFieldException, IllegalAccessException {
		if (null == clazz) {
			throw new NullArgumentException("clazz");
		}
		if (Strings.isBlank(name)) {
			throw new NullArgumentException("name");
		}

		Field f = clazz.getDeclaredField(name);
		if (null == f) {
			throw new NoSuchFieldException(name);
		}
		if (null != isFinal && isFinal != Modifier.isFinal(f.getModifiers())) {
			throw new IllegalAccessException(f.toString());
		}

		if (!Modifier.isPublic(f.getModifiers()) && forceAccess) {
			f.setAccessible(true);
		}
		return f.get(Modifier.isStatic(f.getModifiers()) ? null : target);
	}

	public static Object readStaticField(Class<?> clazz, String name, Boolean isFinal, boolean forceAccess)
			throws NoSuchFieldException, IllegalAccessException {
		return readField(clazz, null, name, isFinal, forceAccess);
	}

}
