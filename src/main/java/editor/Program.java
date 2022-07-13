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

			if (!log.isTraceEnabled()) {
				SplashWindow.splash(Program.class.getResource("/META-INF/images/splash.jpg"));
			}
			EventQueue.invokeLater(new Editor.Runner(args));

		} catch (Exception e) {
			log.error("Failed to launch the application on JVM " + getJvmInfo(), e);
			System.exit(-1);

		} finally {
			SplashWindow.disposeSplash();
		}
	}

	private static volatile String jvmInfo = null;

	public static String getJvmInfo() {
		if (null == jvmInfo) {
			jvmInfo = System.getProperty("java.version") + " / " + System.getProperty("os.name")
					+ " (" + System.getProperty("os.version") + ") " + System.getProperty("os.arch");
		}
		return jvmInfo;
	}

	public void uncaughtException(Thread t, Throwable e) {
		log.error("Unhandled exception occurred on JVM " + getJvmInfo(), e);

		if (e instanceof ExceptionInInitializerError) {
			System.exit(-1);
		}
	}

}
