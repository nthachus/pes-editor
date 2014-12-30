/*
 * @(#)SplashWindow.java  2.2.1  2006-05-27
 *
 * Copyright (c) 2003-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is in the public domain.
 */

package editor;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * A Splash window.
 * <p/>
 * Usage: MyApplication is your application class. Create a Splasher class which
 * opens the splash window, invokes the main method of your Application class,
 * and disposes the splash window afterwards. Please note that we want to keep
 * the Splasher class and the SplashWindow class as small as possible. The less
 * code and the less classes must be loaded into the JVM to open the splash
 * screen, the faster it will appear.
 * <p/>
 * <pre>
 * class Splasher {
 * 	public static void main(String[] args) {
 * 		SplashWindow.splash(Startup.class.getResource(&quot;splash.gif&quot;));
 * 		MyApplication.main(args);
 * 		SplashWindow.disposeSplash();
 *    }
 * }
 * </pre>
 *
 * @author Werner Randelshofer
 * @version 2.2.1 2006-05-27 Abort when splash image can not be loaded.
 */
public class SplashWindow extends Window {
	/**
	 * The current instance of the splash window. (Singleton design pattern).
	 */
	private static volatile SplashWindow instance = null;

	/**
	 * The splash image which is displayed on the splash window.
	 */
	private final Image image;

	/**
	 * This attribute indicates whether the method paint(Graphics) has been
	 * called at least once since the construction of this window.<br>
	 * This attribute is used to notify method splash(Image) that the window has
	 * been drawn at least once by the AWT event dispatcher thread.<br>
	 * This attribute acts like a latch. Once set to true, it will never be
	 * changed back to false again.
	 *
	 * @see #paint
	 * @see #splash
	 */
	private volatile boolean isPaintCalled = false;

	/**
	 * Creates a new instance.
	 *
	 * @param parent The parent of the window.
	 * @param image  The splash image.
	 */
	private SplashWindow(Frame parent, Image image) {
		super(parent);
		this.image = image;

		// Load the image
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException ie) {
			System.err.println(ie);
		}

		// Abort on failure
		if (mt.isErrorID(0)) {
			setSize(0, 0);
			System.err.println("Warning: SplashWindow couldn't load splash image.");
			synchronized (this) {
				isPaintCalled = true;
				notifyAll();
			}
		} else {
			// Center the window on the screen
			int imgWidth = image.getWidth(this);
			int imgHeight = image.getHeight(this);
			setSize(imgWidth, imgHeight);

			Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenDim.width - imgWidth) / 2, (screenDim.height - imgHeight) / 2);

			// Users shall be able to close the splash window by
			// clicking on its display area. This mouse listener
			// listens for mouse clicks and disposes the splash window.
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					disposeOnClick();
				}
			});
		}
	}

	private void disposeOnClick() {
		// [*] To avoid that method splash hangs, we must set isPaintCalled to true and call notifyAll.
		// This is necessary because the mouse click may occur before the contents of the window has been painted.
		synchronized (this) {
			isPaintCalled = true;
			notifyAll();
		}
		dispose();
	}

	/**
	 * Updates the display area of the window.
	 */
	public void update(Graphics g) {
		// [*] Since the paint method is going to draw an
		// image that covers the complete area of the component we
		// do not fill the component with its background color here. This avoids flickering.
		paint(g);
	}

	/**
	 * Paints the image on the window.
	 */
	public void paint(Graphics g) {
		if (null == g) throw new NullPointerException("g");
		g.drawImage(image, 0, 0, this);

		// Notify method splash that the window has been painted.
		// [*] To improve performance we do not enter the synchronized block unless we have to.
		if (!isPaintCalled) {
			isPaintCalled = true;
			synchronized (this) {
				notifyAll();
			}
		}
	}

	/**
	 * Opens a splash window using the specified image.
	 *
	 * @param image The splash image.
	 */
	public static void splash(Image image) {
		if (instance != null || image == null)
			return;

		// Create the splash image
		instance = new SplashWindow(new Frame(), image);

		// Show the window.
		instance.setVisible(true);

		// [*] To make sure the user gets a chance to see the
		// splash window we wait until its paint method has been
		// called at least once by the AWT event dispatcher thread.
		// If more than one processor is available, we don't wait,
		// and maximize CPU throughput instead.
		if (!EventQueue.isDispatchThread() && Runtime.getRuntime().availableProcessors() == 1) {
			synchronized (instance) {
				while (!instance.isPaintCalled) {
					try {
						instance.wait();
					} catch (InterruptedException e) {
						System.err.println(e);
					}
				}
			}
		}
	}

	/**
	 * Opens a splash window using the specified image.
	 *
	 * @param imageUrl The url of the splash image.
	 */
	public static void splash(URL imageUrl) {
		if (imageUrl != null) {
			splash(Toolkit.getDefaultToolkit().createImage(imageUrl));
		}
	}

	/**
	 * Closes the splash window.
	 */
	public static void disposeSplash() {
		if (instance != null) {
			instance.getOwner().dispose();
			instance = null;
		}
	}

}
