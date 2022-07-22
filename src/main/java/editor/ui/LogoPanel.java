package editor.ui;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class LogoPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -2109453947661826598L;
	private static final Logger log = LoggerFactory.getLogger(LogoPanel.class);

	private final OptionFile of;
	private final LogoImportDialog logoImportDia;

	private volatile boolean isTrans = true;

	public LogoPanel(OptionFile of, LogoImportDialog lid) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == lid) {
			throw new NullArgumentException("lid");
		}
		this.of = of;
		logoImportDia = lid;

		log.debug("Initialize Logo panel with import dialog: {}", Strings.valueOf(lid));
		initComponents();

		//refresh();
	}

	private JFileChooser chooser;
	private JFileChooser pngChooser;
	private final JButton[] logoButtons = new JButton[Logos.TOTAL];

	private void initComponents() {
		FileFilter filter = new ImageFileFilter();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle(Resources.getMessage("logo.import"));

		FileFilter pngFilter = new PngFilter();
		pngChooser = new JFileChooser();
		pngChooser.addChoosableFileFilter(pngFilter);
		pngChooser.setAcceptAllFileFilterUsed(false);
		pngChooser.setDialogTitle(Resources.getMessage("logo.export"));

		JPanel flagPanel = new JPanel(new GridLayout(8, 10));
		int iconSize = Math.round(1.2f * Logos.IMG_SIZE);
		Dimension prefSize = new Dimension(iconSize, iconSize);
		Insets margin = new Insets(0, 0, 0, 0);

		UIUtil.javaUI();// fix button background color
		for (int l = 0; l < logoButtons.length; l++) {
			logoButtons[l] = new JButton();
			logoButtons[l].setBackground(UIUtil.GRAY80);
			logoButtons[l].setMargin(margin);
			logoButtons[l].setPreferredSize(prefSize);
			logoButtons[l].setActionCommand(Integer.toString(l));
			logoButtons[l].addActionListener(this);

			flagPanel.add(logoButtons[l]);
		}
		UIUtil.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(flagPanel, BorderLayout.CENTER);
		contentPane.add(transButton, BorderLayout.SOUTH);

		add(contentPane);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Try to perform logo action: {}", evt.getActionCommand());

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			synchronized (this) {
				isTrans = !isTrans;
			}
			refresh();

		} else {
			int slot = Integer.parseInt(evt.getActionCommand());
			ImageIcon icon = new ImageIcon(Logos.get(of, slot, !isTrans));

			Object[] opts = getOptions(logoImportDia.isOf2Loaded());
			int returnVal = JOptionPane.showOptionDialog(null,
					Resources.getMessage("logo.title"), Resources.getMessage("logo.label"),
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, opts, opts[0]);

			switch (returnVal) {
				case JOptionPane.YES_OPTION:
					importLogo(slot);
					break;
				case JOptionPane.NO_OPTION:
					if (Logos.isUsed(of, slot)) {
						saveLogoAsPNG(slot);
					}
					break;
				case JOptionPane.CANCEL_OPTION:
					if (logoImportDia.isOf2Loaded()) {
						importFromOF2(slot);
					}
					break;
				default:
					break;
			}
		}
	}

	private static Object[] getOptions(boolean of2Loaded) {
		String s = Resources.getMessage("logo.options");
		String[] opts = Strings.COMMA_REGEX.split(s);
		if (of2Loaded || opts.length < 2) {
			return opts;
		}

		ArrayList<String> arr = new ArrayList<String>(Arrays.asList(opts));
		arr.remove(arr.size() - 2);
		return arr.toArray();
	}

	private void importFromOF2(int slot) {
		logoImportDia.show(slot, Resources.getMessage("logo.import"));
		refresh(slot);

		log.debug("Importing of logo {} from OF2 completed", slot);
	}

	private void importLogo(int slot) {
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		try {
			File source = chooser.getSelectedFile();
			BufferedImage image = ImageIO.read(source);

			validateImage(image);
			Logos.set(of, slot, image);
			refresh(slot);

			log.debug("Succeeded to add logo {} from file '{}'", slot, source.getName());
		} catch (Exception e) {
			showAccessFailedMsg(e.getLocalizedMessage());
		}
	}

	private void saveLogoAsPNG(int slot) {
		int returnVal = pngChooser.showSaveDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File dest = pngChooser.getSelectedFile();
		dest = Files.addExtension(dest, Files.PNG);

		if (dest.exists()) {
			returnVal = JOptionPane.showConfirmDialog(null,
					Resources.getMessage("msg.overwrite", dest.getName(), dest.getParent()),
					Resources.getMessage("msg.overwrite.title", dest.getName()),
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);

			if (returnVal != JOptionPane.YES_OPTION) {
				return;
			} else if (!dest.delete()) {
				showAccessFailedMsg(null);
				return;
			}
		}

		writeFile(dest, slot);
		// DEBUG
		log.debug("Saving of logo {} to file '{}' succeeded", slot, dest.getName());
	}

	private void writeFile(File dest, int slot) {
		try {
			BufferedImage image = Logos.get(of, slot, false);
			if (ImageIO.write(image, Files.PNG, dest)) {
				JOptionPane.showMessageDialog(null,
						Resources.getMessage("msg.saveSuccess", dest.getName(), dest.getParent()),
						Resources.getMessage("msg.saveSuccess.title"), JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			showAccessFailedMsg(e.getLocalizedMessage());
		}
	}

	private void refresh(int slot) {
		Image icon = Logos.get(of, slot, !isTrans);
		logoButtons[slot].setIcon(new ImageIcon(icon));
	}

	public void refresh() {
		log.info("Refresh all Panel logos with transparency: {}", isTrans);

		for (int l = 0; l < logoButtons.length; l++) {
			refresh(l);
		}
	}

	private static void validateImage(BufferedImage image) {
		if (null == image) {
			throw new NullArgumentException("image");
		}

		if (image.getWidth() != Logos.IMG_SIZE || image.getHeight() != Logos.IMG_SIZE) {
			throw new IllegalArgumentException(Resources.getMessage("msg.invalidSize", Logos.IMG_SIZE, Logos.IMG_SIZE));
		}

		ColorModel colorMod = image.getColorModel();
		if (!(colorMod instanceof IndexColorModel)) {
			throw new IllegalArgumentException(Resources.getMessage("msg.notIndexed"));
		}

		int paletteSize = Images.paletteSize(Logos.BITS_DEPTH);
		if (((IndexColorModel) colorMod).getMapSize() > paletteSize) {
			throw new IllegalArgumentException(Resources.getMessage("msg.manyColors", paletteSize));
		}
	}

	private static void showAccessFailedMsg(String msg) {
		if (Strings.isBlank(msg)) {
			msg = Resources.getMessage("msg.accessFailed");
		}
		JOptionPane.showMessageDialog(null, msg, Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

}
