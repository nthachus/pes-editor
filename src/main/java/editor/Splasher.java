package editor;

public class Splasher {
	/**
	 * Shows the splash screen, launches the application and then disposes the splash screen.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		try {
			SplashWindow.splash(Splasher.class.getResource("/META-INF/images/splash.jpg"));
			editor.ui.Editor.main(args);
		} finally {
			SplashWindow.disposeSplash();
		}
	}

}
