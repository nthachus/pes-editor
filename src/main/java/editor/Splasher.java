package editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Splasher {
	private static final Logger log = LoggerFactory.getLogger(Splasher.class);

	/**
	 * Shows the splash screen, launches the application and then disposes the splash screen.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		try {
			SplashWindow.splash(Splasher.class.getResource("/META-INF/images/splash.jpg"));
			editor.ui.Editor.main(args);
		} catch (Exception e) {
			log.error("Failed to launch the application:", e);
			System.exit(-1);
		} finally {
			SplashWindow.disposeSplash();
		}
	}

}
