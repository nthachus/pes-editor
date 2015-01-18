package editor.ui;

import editor.data.EmblemType;
import editor.data.Emblems;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.ArrayList;

public class EmblemPanel extends JPanel implements MouseListener, ActionListener {
	private static final long serialVersionUID = -3656723954660516377L;
	private static final Logger log = LoggerFactory.getLogger(EmblemPanel.class);

	private final OptionFile of;
	private final EmblemImportDialog flagImportDia;
	private final TeamPanel teamPanel;

	private volatile boolean isTrans = true;

	public EmblemPanel(OptionFile of, EmblemImportDialog fid, TeamPanel tp) {
		super();

		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == fid) {
			throw new NullArgumentException("fid");
		}
		if (null == tp) {
			throw new NullArgumentException("tp");
		}
		this.of = of;
		flagImportDia = fid;
		teamPanel = tp;

		log.debug("Emblem panel is initializing..");
		initComponents();

		refresh();
	}

	//region Initialize the GUI components

	private JFileChooser chooser;
	private JFileChooser pngChooser;
	private final JButton[] emblemButtons = new JButton[Emblems.TOTAL16];
	private JLabel largeFlag;
	private JLabel free16Label;
	private JLabel free128Label;
	private JButton addButton;
	private JButton add2Button;

	private void initComponents() {
		FileFilter filter128 = new ImageFileFilter();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(filter128);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle(Resources.getMessage("emblem.import"));

		FileFilter pngFilter = new PngFilter();
		pngChooser = new JFileChooser();
		pngChooser.addChoosableFileFilter(pngFilter);
		pngChooser.setAcceptAllFileFilterUsed(false);
		pngChooser.setDialogTitle(Resources.getMessage("emblem.export"));

		JPanel flagPanel = new JPanel(new GridLayout(6, 10));
		int iconSize = Math.round(0.67f * Emblems.IMG_SIZE);
		Dimension prefSize = new Dimension(iconSize, iconSize);
		Insets margin = new Insets(0, 0, 0, 0);

		UIUtil.javaUI();// fix button background color
		for (int i = 0; i < emblemButtons.length; i++) {
			emblemButtons[i] = new JButton();
			emblemButtons[i].setBackground(UIUtil.GRAY80);
			emblemButtons[i].setMargin(margin);
			emblemButtons[i].setPreferredSize(prefSize);
			emblemButtons[i].setActionCommand(Integer.toString(i));
			emblemButtons[i].addMouseListener(this);
			emblemButtons[i].addActionListener(this);

			flagPanel.add(emblemButtons[i]);
		}
		UIUtil.systemUI();

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		contentPane.add(new JLabel(Resources.getMessage("emblem.label16")), BorderLayout.NORTH);
		contentPane.add(flagPanel, BorderLayout.CENTER);
		contentPane.add(new JLabel(Resources.getMessage("emblem.label128"), SwingConstants.RIGHT), BorderLayout.SOUTH);

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JPanel bottomPane = new JPanel(new BorderLayout());
		bottomPane.add(contentPane, BorderLayout.CENTER);
		bottomPane.add(transButton, BorderLayout.SOUTH);

		largeFlag = new JLabel();
		largeFlag.setIcon(new ImageIcon(Emblems.BLANK16));

		free16Label = new JLabel();
		free128Label = new JLabel();

		addButton = new JButton(Resources.getMessage("emblem.add"));
		addButton.setActionCommand("Add");
		addButton.addActionListener(this);

		add2Button = new JButton(Resources.getMessage("emblem.add2"));
		add2Button.setActionCommand("Add2");
		add2Button.addActionListener(this);

		JPanel freePan = new JPanel(new GridLayout(0, 1));
		freePan.add(free16Label);
		freePan.add(free128Label);
		freePan.add(addButton);
		freePan.add(add2Button);

		JPanel freePane = new JPanel();
		freePane.add(freePan);

		add(bottomPane);
		add(largeFlag);
		add(freePane);
	}

	//endregion

	private void addEmblem() {
		if (Emblems.getFree128(of) <= 0 && Emblems.getFree16(of) <= 0) {
			return;
		}

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		try {
			File source = chooser.getSelectedFile();
			BufferedImage image = ImageIO.read(source);

			int palSize = validateImage(image, Emblems.PALETTE_SIZE128);
			if (palSize <= Emblems.PALETTE_SIZE16) {
				Emblems.set16(of, Emblems.count16(of), image);
			} else if (Emblems.getFree128(of) > 0) {
				Emblems.set128(of, Emblems.count128(of), image);
			} else {
				throw new IllegalStateException(Resources.getMessage("msg.emblemNoSpace", Emblems.PALETTE_SIZE128));
			}

			teamPanel.refresh();
			refresh();

			log.debug("Adding of emblem {} succeeded", source);
		} catch (Exception e) {
			showOpenFailedMsg(e.getLocalizedMessage());
		}
	}

	private void addEmblemFromOF2() {
		int emblem = -1;
		if (Emblems.getFree128(of) > 0) {
			emblem = flagImportDia.getEmblem(Resources.getMessage("emblem.import"), null);
		} else if (Emblems.getFree16(of) > 0) {
			emblem = flagImportDia.getEmblem(Resources.getMessage("emblem.import"), EmblemType.lowRes);
		}

		if (emblem >= 0) {
			if (emblem >= Emblems.TOTAL128) {
				flagImportDia.import16(of, Emblems.count16(of), emblem - Emblems.TOTAL128);
			} else {
				flagImportDia.import128(of, Emblems.count128(of), emblem);
			}

			teamPanel.refresh();
			refresh();

			log.debug("Adding of emblem from OF2 {} succeeded", emblem);
		}
	}

	private void selectEmblem(ActionEvent evt) {
		int slot = Integer.parseInt(evt.getActionCommand());

		Image icon;
		boolean is128 = false;
		if (slot >= Emblems.count16(of)) {
			is128 = true;
			slot = Emblems.TOTAL16 - slot - 1;
			icon = Emblems.get128(of, slot, !isTrans, false);
		} else {
			icon = Emblems.get16(of, slot, !isTrans, false);
		}

		Object[] opts = getOptions(flagImportDia.isOf2Loaded());
		int returnVal = JOptionPane.showOptionDialog(null,
				Resources.getMessage("emblem.title"), Resources.getMessage("emblem.label"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(icon), opts, opts[0]);

		switch (returnVal) {
			case JOptionPane.YES_OPTION:
				deleteEmblem(is128, slot);
				break;
			case JOptionPane.NO_OPTION:
				importEmblem(is128, slot);
				break;
			case JOptionPane.CANCEL_OPTION:
				saveEmblemAsPNG(is128, slot);
				break;
			case 3:
				if (flagImportDia.isOf2Loaded()) {
					importEmblemFromOF2(is128, slot);
				}
				break;
			default:
				break;
		}

		log.debug("Selecting of emblem slot {} completed", slot);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.debug("Try to perform panel action: {}", evt.getActionCommand());

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			isTrans = !isTrans;
			refresh();
		} else if ("Add".equalsIgnoreCase(evt.getActionCommand())) {
			addEmblem();
		} else if ("Add2".equalsIgnoreCase(evt.getActionCommand())) {
			addEmblemFromOF2();
		} else {
			selectEmblem(evt);
		}
	}

	private void importEmblemFromOF2(boolean is128, int slot) {
		int replacement;
		if (is128) {
			replacement = flagImportDia.getEmblem(Resources.getMessage("emblem.import"), EmblemType.highRes);
			if (replacement >= 0) {
				flagImportDia.import128(of, slot, replacement);
			}
		} else {
			replacement = flagImportDia.getEmblem(Resources.getMessage("emblem.import"), EmblemType.lowRes);
			if (replacement >= 0) {
				flagImportDia.import16(of, slot, replacement - Emblems.TOTAL128);
			}
		}

		teamPanel.refresh();
		refresh();

		log.debug("Importing of emblem (hi-res: {}) slot {} completed", is128, slot);
	}

	private void importEmblem(boolean is128, int slot) {
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		try {
			File source = chooser.getSelectedFile();
			BufferedImage image = ImageIO.read(source);

			int palSize = validateImage(image, is128 ? Emblems.PALETTE_SIZE128 : Emblems.PALETTE_SIZE16);
			if (is128) {
				if (palSize > Emblems.PALETTE_SIZE16) {
					Emblems.set128(of, slot, image);
				} else {
					throw new IllegalStateException(
							Resources.getMessage("msg.imgWasteSpace", Emblems.PALETTE_SIZE16, Emblems.PALETTE_SIZE128));
				}
			} else {
				Emblems.set16(of, slot, image);
			}

			teamPanel.refresh();
			refresh();

			log.debug("Adding of emblem (hi-res: {}) slot {} succeeded", is128, slot);
		} catch (Exception e) {
			showOpenFailedMsg(e.getLocalizedMessage());
		}
	}

	private void deleteEmblem(boolean is128, int slot) {
		if (is128) {
			Emblems.delete128(of, slot);
		} else {
			Emblems.delete16(of, slot);
		}

		teamPanel.refresh();
		refresh();

		log.debug("Deleting of emblem (hi-res: {}) slot {} completed", is128, slot);
	}

	private static Object[] getOptions(boolean of2Loaded) {
		String s = Resources.getMessage("emblem.options");
		String[] opts = s.split("\\s*,\\s*");
		if (of2Loaded || opts.length < 2) {
			return opts;
		}

		ArrayList<String> arr = new ArrayList<String>(java.util.Arrays.asList(opts));
		arr.remove(arr.size() - 2);
		return arr.toArray();
	}

	private void saveEmblemAsPNG(boolean is128, int slot) {
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

		writeFile(dest, is128, slot);
		// DEBUG
		log.debug("Saving of emblem (hi-res: {}) slot {} as PNG {} completed", is128, slot, dest.getName());
	}

	private void writeFile(File dest, boolean is128, int slot) {
		try {
			BufferedImage image;
			if (is128) {
				image = (BufferedImage) Emblems.get128(of, slot, false, false);
			} else {
				image = (BufferedImage) Emblems.get16(of, slot, false, false);
			}

			if (ImageIO.write(image, Files.PNG, dest)) {
				JOptionPane.showMessageDialog(null,
						Resources.getMessage("msg.saveSuccess", dest.getName(), dest.getParent()),
						Resources.getMessage("msg.saveSuccess.title"), JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			showAccessFailedMsg(e.getLocalizedMessage());
		}
	}

	public void refresh() {
		log.debug("Try to refresh Emblem panel with transparency: {}", isTrans);

		int n16 = Emblems.count16(of);
		int n128 = Emblems.count128(of);

		Image icon;
		for (int i = 0; i < n16; i++) {
			icon = Emblems.get16(of, i, !isTrans, true);
			emblemButtons[i].setIcon(new ImageIcon(icon));
			emblemButtons[i].setVisible(true);
		}
		for (int i = 0; i < n128; i++) {
			icon = Emblems.get128(of, i, !isTrans, true);
			emblemButtons[emblemButtons.length - i - 1].setIcon(new ImageIcon(icon));
			emblemButtons[emblemButtons.length - i - 1].setVisible(true);
		}

		for (int i = n16; i < emblemButtons.length - n128; i++) {
			emblemButtons[i].setVisible(false);
		}

		free16Label.setText(Resources.getMessage("emblem.free16", Emblems.getFree16(of)));
		free128Label.setText(Resources.getMessage("emblem.free128", Emblems.getFree128(of)));

		addButton.setEnabled(Emblems.getFree16(of) > 0);
		add2Button.setVisible(flagImportDia.isOf2Loaded());
		if (add2Button.isVisible()) {
			add2Button.setEnabled(addButton.isEnabled());
		}
	}

	//region Emblem Icon button Mouse Events

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		if (null == e) {
			throw new NullArgumentException("e");
		}
		if (!(e.getSource() instanceof AbstractButton)) {
			throw new IllegalArgumentException("e");
		}

		AbstractButton btn = (AbstractButton) e.getSource();
		int slot = Integer.parseInt(btn.getActionCommand());
		// DEBUG
		//log.debug("Try to zoom emblem: {}", slot);

		Image icon;
		if (slot >= Emblems.count16(of)) {
			slot = Emblems.TOTAL16 - slot - 1;
			icon = Emblems.get128(of, slot, !isTrans, false);
		} else {
			icon = Emblems.get16(of, slot, !isTrans, false);
		}

		largeFlag.setIcon(new ImageIcon(icon));
	}

	public void mouseExited(MouseEvent e) {
		largeFlag.setIcon(new ImageIcon(Emblems.BLANK16));
		// DEBUG
		//log.debug("Clearing zoomed emblem succeeded");
	}

	public void mouseClicked(MouseEvent e) {
	}

	//endregion

	private static int validateImage(BufferedImage image, int paletteSize) {
		if (null == image) {
			throw new NullArgumentException("image");
		}

		if (image.getWidth() != Emblems.IMG_SIZE || image.getHeight() != Emblems.IMG_SIZE) {
			throw new IllegalArgumentException(
					Resources.getMessage("msg.invalidSize", Emblems.IMG_SIZE, Emblems.IMG_SIZE));
		}

		ColorModel colorMod = image.getColorModel();
		if (!(colorMod instanceof IndexColorModel)) {
			throw new IllegalArgumentException(Resources.getMessage("msg.notIndexed"));
		}

		int colorsCount = ((IndexColorModel) colorMod).getMapSize();
		if (colorsCount > paletteSize) {
			throw new IllegalArgumentException(Resources.getMessage("msg.manyColors", paletteSize));
		}

		return colorsCount;
	}

	private static void showOpenFailedMsg(String msg) {
		if (Strings.isBlank(msg)) {
			msg = Resources.getMessage("msg.openFailed");
		}
		JOptionPane.showMessageDialog(null, msg, Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	private static void showAccessFailedMsg(String msg) {
		if (Strings.isBlank(msg)) {
			msg = Resources.getMessage("msg.accessFailed");
		}
		JOptionPane.showMessageDialog(null, msg, Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

}
