package editor;

import editor.ui.Editor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public final class Program implements Thread.UncaughtExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(Program.class);

	private Program() {
	}

	/**
	 * Shows the splash screen, launches the application and then disposes the splash screen.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		log.info("Application is starting...");
		try {
			Thread.setDefaultUncaughtExceptionHandler(new Program());

			if (!log.isDebugEnabled()) {
				SplashWindow.splash(Program.class.getResource("/META-INF/images/splash.jpg"));
			}
			EventQueue.invokeLater(new Editor.Runner(args));

		} catch (Exception e) {
			log.error("Failed to launch the application:", e);
			System.exit(-1);

		} finally {
			SplashWindow.disposeSplash();
		}
	}

	public void uncaughtException(Thread t, Throwable e) {
		log.error("Unhandled exception occurred:", e);

		if (e instanceof ExceptionInInitializerError) {
			System.exit(-1);
		}
	}

}
