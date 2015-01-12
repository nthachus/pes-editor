package editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Program {
	private static final Logger log;

	static {
		// fix gateway time-out error
		System.setProperty("mail.smtp.timeout", "2000");
		System.setProperty("mail.smtp.connectiontimeout", "3000");

		log = LoggerFactory.getLogger(Program.class);
	}

	/**
	 * Shows the splash screen, launches the application and then disposes the splash screen.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		log.info("Application is starting...");
		try {
			SplashWindow.splash(Program.class.getResource("/META-INF/images/splash.jpg"));
			editor.ui.Editor.main(args);
		} catch (Exception e) {
			log.error("Failed to launch the application:", e);
			System.exit(-1);
		} finally {
			SplashWindow.disposeSplash();
		}
	}

}
