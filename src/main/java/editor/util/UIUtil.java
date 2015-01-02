package editor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public final class UIUtil {
	private static final Logger log = LoggerFactory.getLogger(UIUtil.class);

	private UIUtil() {
	}

	public static void javaLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			log.warn(e.toString());
		}
	}

	public static void systemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.warn(e.toString());
		}
	}

}
