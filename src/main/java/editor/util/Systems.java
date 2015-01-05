package editor.util;

import editor.util.swing.IndexColorComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class Systems {
	private static final Logger log = LoggerFactory.getLogger(Systems.class);

	private Systems() {
	}

	public static void javaUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			log.warn(e.toString());
		}
	}

	public static void systemUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.warn(e.toString());
		}
	}

	public static boolean saveComponentAsImage(Component comp, File out) {
		if (null == comp) throw new NullPointerException("comp");
		if (null == out) throw new NullPointerException("out");
		if (!(comp instanceof IndexColorComponent)) throw new IllegalArgumentException("comp");

		Graphics2D g2 = null;
		try {
			Color[] palette = ((IndexColorComponent) comp).getPalette();
			int bpp = (int) Math.ceil(Math.log(palette.length) / Math.log(2));
			int palSize = (1 << bpp);

			byte[] red = new byte[palSize];
			byte[] green = new byte[palSize];
			byte[] blue = new byte[palSize];
			byte[] alpha = new byte[palSize];

			for (int i = 0; i < palette.length; i++) {
				red[i] = (byte) palette[i].getRed();
				green[i] = (byte) palette[i].getGreen();
				blue[i] = (byte) palette[i].getBlue();
				alpha[i] = (byte) palette[i].getAlpha();
			}

			Dimension size = comp.getSize();
			IndexColorModel colMod = new IndexColorModel(bpp, palSize, red, green, blue, alpha);
			BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_INDEXED, colMod);

			g2 = image.createGraphics();
			comp.paint(g2);

			return ImageIO.write(image, Files.getExtension(out).toLowerCase(), out);

		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (null != g2) g2.dispose();
		}

		return false;
	}

	public static <T> List<T> readFields(
			Class<?> clazz, Object target, Class<T> ofType, Boolean isFinal, boolean forceAccess) throws Exception {
		if (null == clazz) throw new NullPointerException("clazz");
		if (null == ofType) throw new NullPointerException("ofType");

		List<T> list = new ArrayList<T>();

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

	public static <T> List<T> readStaticFields(
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

	public static void writeField(Class<?> clazz, Object target, String name, boolean forceAccess, Object value)
			throws Exception {
		if (null == clazz) throw new NullPointerException("clazz");
		if (Strings.isBlank(name)) throw new NullPointerException("name");

		Field f = clazz.getDeclaredField(name);
		if ((null == target) != Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers()))
			throw new IllegalAccessException(f.toString());

		if (forceAccess)
			f.setAccessible(true);

		f.set(target, value);
	}

	public static void writeStaticField(Class<?> clazz, String name, boolean forceAccess, Object value)
			throws Exception {
		writeField(clazz, null, name, forceAccess, value);
	}

	public static Object invokeMethod(
			Class<?> clazz, Object target, String name, Class<?>[] argTypes, boolean forceAccess, Object... args)
			throws Exception {
		if (null == clazz) throw new NullPointerException("clazz");
		if (Strings.isBlank(name)) throw new NullPointerException("name");

		Method m = clazz.getDeclaredMethod(name, argTypes);
		if ((null == target) != Modifier.isStatic(m.getModifiers()))
			throw new IllegalAccessException(m.toString());

		if (forceAccess)
			m.setAccessible(true);

		return m.invoke(target, args);
	}

	public static Object invokeStaticMethod(
			Class<?> clazz, String name, Class<?>[] argTypes, boolean forceAccess, Object... args) throws Exception {
		return invokeMethod(clazz, null, name, argTypes, forceAccess, args);
	}

}
