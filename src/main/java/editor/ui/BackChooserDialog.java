package editor.ui;

import editor.util.Strings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class BackChooserDialog extends JDialog implements ActionListener {

	private final JButton[] flagButton = new JButton[12];
	private final Raster[] rasterData = new Raster[flagButton.length];

	/**
	 * Picked flag index.
	 */
	private volatile int slot = -1;

	public BackChooserDialog(Frame owner) {
		super(owner, Strings.getMessage("backFlag.title"), true);

		JPanel flagPanel = new JPanel(new GridLayout(3, 4));
		for (int i = 0; i < flagButton.length; i++) {
			// load each flag background images
			URL backUrl = getClass().getResource("/META-INF/images/backFlag" + i + ".png");
			if (null != backUrl) {
				try {
					BufferedImage img = ImageIO.read(backUrl);
					rasterData[i] = img.getData();
				} catch (IOException e) {
					System.err.println(e);
				}
			}

			flagButton[i] = new JButton();
			flagButton[i].setMargin(new Insets(0, 0, 0, 0));
			flagButton[i].setActionCommand(Integer.toString(i));
			flagButton[i].addActionListener(this);

			flagPanel.add(flagButton[i]);
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
		if (index < 0 || index >= flagButton.length) throw new ArrayIndexOutOfBoundsException("index");
		return flagButton[index];
	}

	public int getBack(Image image, byte[] red, byte[] green, byte[] blue) {
		slot = -1;
		refresh(image, red, green, blue);
		setVisible(true);
		return slot;
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		JButton btn = (JButton) evt.getSource();
		if (null == btn) throw new IllegalArgumentException("evt");

		slot = Integer.parseInt(btn.getActionCommand());
		setVisible(false);
	}

	private void refresh(Image image, byte[] red, byte[] green, byte[] blue) {
		for (int i = 0; i < flagButton.length; i++) {
			flagButton[i].setIcon(getFlagBackground(image, i, red, green, blue));
		}
	}

	public ImageIcon getFlagBackground(Image image, int bgIndex, byte[] red, byte[] green, byte[] blue) {
		if (bgIndex < 0 || bgIndex >= rasterData.length) throw new ArrayIndexOutOfBoundsException("bgIndex");

		IndexColorModel colorModel = new IndexColorModel(1, 2, red, green, blue);
		BufferedImage bi = new BufferedImage(colorModel, (WritableRaster) rasterData[bgIndex], false, null);

		BufferedImage img = new BufferedImage(85, 64, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = null;
		try {
			g2 = (Graphics2D) img.getGraphics();
			g2.drawImage(bi, 0, 0, null);
			if (image != null) {
				g2.drawImage(image, 11, 0, null);
			}
		} catch (Exception e) {
			if (null != g2) g2.dispose();
		}

		return new ImageIcon(img);
	}

}
