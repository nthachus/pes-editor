package editor.ui;

import editor.LogoImportDialog;
import editor.data.Logos;
import editor.data.OptionFile;
import editor.util.Files;
import editor.util.Images;
import editor.util.Strings;
import editor.util.UIUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class LogoPanel extends JPanel {
	private final OptionFile of;
	private final LogoImportDialog logoImportDia;

	private volatile boolean isTrans = true;

	public LogoPanel(OptionFile of, LogoImportDialog lid) {
		super();
		if (null == of) throw new NullPointerException("of");
		if (null == lid) throw new NullPointerException("lid");
		this.of = of;
		logoImportDia = lid;

		initComponents();
		refresh();
	}

	private JFileChooser chooser;
	private JFileChooser chooserPNG;
	private final JButton[] flagButtons = new JButton[Logos.TOTAL];

	private void initComponents() {
		ImageFileFilter filter = new ImageFileFilter();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle(Strings.getMessage("logo.import"));

		PngFilter pngFilter = new PngFilter();
		chooserPNG = new JFileChooser();
		chooserPNG.addChoosableFileFilter(pngFilter);
		chooserPNG.setAcceptAllFileFilterUsed(false);
		chooserPNG.setDialogTitle(Strings.getMessage("logo.export"));

		JPanel flagPanel = new JPanel(new GridLayout(8, 10));
		UIUtil.javaLookAndFeel();// fix button background color
		for (int l = 0; l < flagButtons.length; l++) {
			flagButtons[l] = new JButton();
			flagButtons[l].setBackground(new Color(0xCC, 0xCC, 0xCC));
			flagButtons[l].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[l].setActionCommand(Integer.toString(l));
			flagButtons[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					onSelectLogo(evt);
				}
			});

			flagPanel.add(flagButtons[l]);
		}
		UIUtil.systemLookAndFeel();

		JButton transButton = new JButton(Strings.getMessage("Transparency"));
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onTransparency(evt);
			}
		});

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(flagPanel, BorderLayout.CENTER);
		contentPane.add(transButton, BorderLayout.SOUTH);

		add(contentPane);
	}

	private void onTransparency(ActionEvent evt) {
		isTrans = !isTrans;
		refresh();
	}

	private void onSelectLogo(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		JButton btn = (JButton) evt.getSource();
		if (null == btn) throw new IllegalArgumentException("evt");

		int slot = Integer.parseInt(btn.getActionCommand());
		ImageIcon icon = new ImageIcon(Logos.get(of, slot, !isTrans));

		Object[] opts = getOptions(logoImportDia.isOf2Loaded());
		int returnVal = JOptionPane.showOptionDialog(null,
				Strings.getMessage("logo.title"), Strings.getMessage("logo.label"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, opts, opts[0]);

		switch (returnVal) {
			case JOptionPane.YES_OPTION:
				importLogo(slot);
				break;
			case JOptionPane.NO_OPTION:
				if (Logos.isUsed(of, slot))
					exportLogo(slot);
				break;
			case JOptionPane.CANCEL_OPTION:
				if (logoImportDia.isOf2Loaded())
					importFromOF2(slot);
				break;
		}
	}

	private static Object[] getOptions(boolean of2Loaded) {
		String s = Strings.getMessage("logo.options");
		String[] opts = s.split("\\s*,\\s*");
		if (of2Loaded || opts.length < 2)
			return opts;

		ArrayList<String> arr = new ArrayList<String>(Arrays.asList(opts));
		arr.remove(arr.size() - 2);
		return arr.toArray();
	}

	private void importFromOF2(int slot) {
		logoImportDia.show(slot, Strings.getMessage("logo.import"));
		refresh(slot);
	}

	private void importLogo(int slot) {
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		try {
			File source = chooser.getSelectedFile();
			BufferedImage image = ImageIO.read(source);

			validateImage(image);
			Logos.set(of, slot, image);
			refresh(slot);
		} catch (Exception e) {
			showAccessFailedMsg(e.getLocalizedMessage());
		}
	}

	private void exportLogo(int slot) {
		int returnVal = chooserPNG.showSaveDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		File dest = chooserPNG.getSelectedFile();
		dest = Files.addExtension(dest, Files.PNG);

		if (dest.exists()) {
			returnVal = JOptionPane.showConfirmDialog(null,
					Strings.getMessage("msg.overwrite", dest.getName(), dest.getParent()),
					Strings.getMessage("msg.overwrite.title", dest.getName()),
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);

			if (returnVal != JOptionPane.YES_OPTION) {
				return;
			} else if (!dest.delete()) {
				showAccessFailedMsg(null);
				return;
			}
		}

		writeFile(dest, slot);
	}

	private void writeFile(File dest, int slot) {
		try {
			BufferedImage image = Logos.get(of, slot, false);
			ImageIO.write(image, Files.PNG, dest);

			JOptionPane.showMessageDialog(null,
					Strings.getMessage("msg.saveSuccess", dest.getName(), dest.getParent()),
					Strings.getMessage("msg.saveSuccess.title"), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			showAccessFailedMsg(e.getLocalizedMessage());
		}
	}

	private void refresh(int slot) {
		Image icon = Logos.get(of, slot, !isTrans);
		flagButtons[slot].setIcon(new ImageIcon(icon));
	}

	public void refresh() {
		for (int l = 0; l < Logos.TOTAL; l++) {
			refresh(l);
		}
	}

	private static void validateImage(BufferedImage image) {
		if (null == image) throw new NullPointerException("image");

		if (image.getWidth() != Logos.IMG_SIZE || image.getHeight() != Logos.IMG_SIZE)
			throw new IllegalArgumentException(Strings.getMessage("msg.invalidSize", Logos.IMG_SIZE, Logos.IMG_SIZE));

		ColorModel colorMod = image.getColorModel();
		if (null == colorMod || !(colorMod instanceof IndexColorModel))
			throw new IllegalArgumentException(Strings.getMessage("msg.notIndexed"));

		int paletteSize = Images.paletteSize(Logos.BITS_DEPTH);
		if (((IndexColorModel) colorMod).getMapSize() > paletteSize)
			throw new IllegalArgumentException(Strings.getMessage("msg.manyColors", paletteSize));
	}

	private static void showAccessFailedMsg(String msg) {
		if (Strings.isBlank(msg)) msg = Strings.getMessage("msg.accessFailed");
		JOptionPane.showMessageDialog(null, msg, Strings.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

}
