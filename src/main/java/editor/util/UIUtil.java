package editor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public final class UIUtil {
	private static final Logger log = LoggerFactory.getLogger(UIUtil.class);

	private UIUtil() {
	}

	public static final Color GRAY80 = new Color(0xCC, 0xCC, 0xCC);
	public static final Color LIGHT_YELLOW = new Color(255, 255, 224);

	public static final Color CHARTREUSE0 = new Color(183, 255, 0);


	public static final String DIALOG = "Dialog";
	//public static final String DIALOG_INPUT = "DialogInput";
	public static final String SANS_SERIF = "SansSerif";
	//public static final String SERIF = "Serif";
	//public static final String MONOSPACED = "Monospaced";


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

	public static String htmlColor(Color c) {
		return (null == c) ? "null" : String.format("#%08X", c.getRGB());
	}

}
