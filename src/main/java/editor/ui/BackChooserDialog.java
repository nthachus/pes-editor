package editor.ui;

import editor.util.Images;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;

public class BackChooserDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -6292338671848786819L;
	private static final Logger log = LoggerFactory.getLogger(BackChooserDialog.class);

	private static final int IMG_WIDTH = 85;
	private static final int IMG_HEIGHT = 64;
	private static final int BITS_DEPTH = 1;

	private final JButton[] flagButtons = new JButton[12];
	private final WritableRaster[] rasterData = new WritableRaster[flagButtons.length];

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
		for (int i = 0; i < flagButtons.length; i++) {
			// load each flag background images
			backUrl = getClass().getResource("/META-INF/images/backFlag" + i + ".png");
			if (null != backUrl) {
				try {
					img = ImageIO.read(backUrl);
					rasterData[i] = img.getRaster();
				} catch (IOException e) {
					log.warn("Failed to load back-flag {}: {}", backUrl, e.toString());
				}
			}
			if (null == rasterData[i])
				rasterData[i] = getBlankFlagData();

			flagButtons[i] = new JButton();
			flagButtons[i].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[i].setActionCommand(Integer.toString(i));
			flagButtons[i].addActionListener(this);

			flagPanel.add(flagButtons[i]);
		}

		byte[] red = {0, -1};
		byte[] blue = {0, -1};
		byte[] green = {0, -1};
		refresh(null, red, green, blue);

		CancelButton cancelButton = new CancelButton(this);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public JButton getFlagButton(int index) {
		if (index < 0 || index >= flagButtons.length)
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		return flagButtons[index];
	}

	public int getBackFlag(Image image, byte[] red, byte[] green, byte[] blue) {
		slot = -1;

		refresh(image, red, green, blue);
		setVisible(true);

		log.debug("Background Flag chooser result: {}", slot);
		return slot;
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		slot = Integer.parseInt(evt.getActionCommand());
		setVisible(false);
	}

	private void refresh(Image image, byte[] red, byte[] green, byte[] blue) {
		log.debug("Try to refresh dialog with image: {}", image);

		ImageIcon flag;
		for (int i = 0; i < flagButtons.length; i++) {
			flag = getBackFlag(image, i, red, green, blue);
			flagButtons[i].setIcon(flag);
		}
	}

	private static volatile WritableRaster blankFlagData = null;

	private static WritableRaster getBlankFlagData() {
		log.warn("Blank Background Flag data should not be called!");

		if (null == blankFlagData) {
			int rasterSize = Images.rasterDataSize(BITS_DEPTH, IMG_WIDTH, IMG_HEIGHT);
			DataBuffer buf = new DataBufferByte(rasterSize);
			SampleModel sampleModel = new MultiPixelPackedSampleModel(
					DataBuffer.TYPE_BYTE, IMG_WIDTH, IMG_HEIGHT, BITS_DEPTH);

			blankFlagData = Raster.createWritableRaster(sampleModel, buf, null);
		}

		return blankFlagData;
	}

	public ImageIcon getBackFlag(Image image, int bgIndex, byte[] red, byte[] green, byte[] blue) {
		if (bgIndex < 0 || bgIndex >= rasterData.length)
			throw new IndexOutOfBoundsException("bgIndex#" + bgIndex);
		log.debug("Try to build Background Flag with image: {}", image);

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
			if (null != g2) g2.dispose();
		}

		return new ImageIcon(img);
	}

}
