package editor.ui;

import editor.data.Clubs;
import editor.lang.NullArgumentException;
import editor.util.Images;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.net.URL;

public class BackChooserDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -6292338671848786819L;
	private static final Logger log = LoggerFactory.getLogger(BackChooserDialog.class);

	private static final int IMG_WIDTH = 85;
	private static final int IMG_HEIGHT = 64;
	private static final int BITS_DEPTH = 1;

	private final JButton[] flagButtons = new JButton[Clubs.TOTAL_BACK_FLAGS];
	private final transient WritableRaster[] rasterData = new WritableRaster[flagButtons.length];

	/**
	 * Picked flag index.
	 */
	private volatile int slot = -1;

	public BackChooserDialog(Frame owner) {
		super(owner, Resources.getMessage("backFlag.title"), true);

		log.debug("Background Flag chooser dialog is initializing..");
		initComponents();
	}

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(3, 4));

		URL backUrl;
		BufferedImage img;
		Insets margin = new Insets(0, 0, 0, 0);

		for (int i = 0; i < flagButtons.length; i++) {
			// load each flag background images
			backUrl = getClass().getResource("/META-INF/images/backFlag" + i + ".png");
			try {
				//noinspection ConstantConditions
				img = ImageIO.read(backUrl);
				rasterData[i] = img.getRaster();
			} catch (Exception e) {
				throw new IllegalStateException("Failed to load back-flag: " + backUrl, e);
			}

			flagButtons[i] = new JButton();
			flagButtons[i].setMargin(margin);
			flagButtons[i].setIcon(new ImageIcon(img));
			flagButtons[i].setActionCommand(Integer.toString(i));
			flagButtons[i].addActionListener(this);

			flagPanel.add(flagButtons[i]);
		}

		JButton cancelButton = new CancelButton(this);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public JButton getFlagButton(int index) {
		if (index < 0 || index >= flagButtons.length) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		}
		return flagButtons[index];
	}

	public int getBackFlag(Image image, byte[] red, byte[] green, byte[] blue) {
		log.info("Try to choice Background Flag for image: {}", Strings.valueOf(image));
		slot = -1;

		refresh(image, red, green, blue);
		setVisible(true);

		log.debug("Background Flag chooser result: {}", slot);
		return slot;
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform BackFlag chooser action: {}", evt.getActionCommand());

		slot = Integer.parseInt(evt.getActionCommand());
		setVisible(false);
	}

	private void refresh(Image image, byte[] red, byte[] green, byte[] blue) {
		log.info("Try to refresh dialog with image: {}", Strings.valueOf(image));

		ImageIcon flag;
		for (int i = 0; i < flagButtons.length; i++) {
			flag = getBackFlag(image, i, red, green, blue);
			flagButtons[i].setIcon(flag);
		}
	}

	public ImageIcon getBackFlag(Image image, int bgIndex, byte[] red, byte[] green, byte[] blue) {
		if (bgIndex < 0 || bgIndex >= rasterData.length) {
			throw new IndexOutOfBoundsException("bgIndex#" + bgIndex);
		}
		if (null != image) {
			log.info("Try to build Background Flag with image: {}", Strings.valueOf(image));
		}

		IndexColorModel colorModel = new IndexColorModel(BITS_DEPTH, Images.paletteSize(BITS_DEPTH), red, green, blue);
		BufferedImage bi = new BufferedImage(colorModel, rasterData[bgIndex], false, null);

		BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = null;
		try {
			g2 = img.createGraphics();
			g2.drawImage(bi, 0, 0, null);
			if (image != null) {
				int x = (IMG_WIDTH - image.getWidth(null)) / 2;
				int y = (IMG_HEIGHT - image.getHeight(null)) / 2;
				g2.drawImage(image, x, y, null);
			}
		} finally {
			if (null != g2) {
				g2.dispose();
			}
		}

		return new ImageIcon(img);
	}

}
