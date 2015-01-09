package editor;

import editor.data.Emblems;
import editor.data.OptionFile;
import editor.ui.EmblemImportDialog;
import editor.ui.ImageFileFilter;
import editor.ui.PngFilter;
import editor.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

public class EmblemPanel extends JPanel implements MouseListener, ActionListener {
	private final OptionFile of;
	private final EmblemImportDialog flagImportDia;
	private final TeamPanel teamPanel;

	private volatile boolean isTrans = true;

	public EmblemPanel(OptionFile of, EmblemImportDialog fid, TeamPanel tp) {
		super();

		if (null == of) throw new NullPointerException("of");
		if (null == fid) throw new NullPointerException("fid");
		if (null == tp) throw new NullPointerException("tp");
		this.of = of;
		flagImportDia = fid;
		teamPanel = tp;

		initComponents();
		refresh();
	}

	//region Initialize the GUI components

	private JFileChooser chooser;
	private JFileChooser pngChooser;
	private final JButton[] flagButtons = new JButton[Emblems.TOTAL16];
	private JLabel largeFlag;
	private JLabel free16Label;
	private JLabel free128Label;
	private JButton addButton;
	private JButton add2Button;

	private void initComponents() {
		ImageFileFilter filter128 = new ImageFileFilter();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(filter128);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle(Resources.getMessage("emblem.import"));

		PngFilter pngFilter = new PngFilter();
		pngChooser = new JFileChooser();
		pngChooser.addChoosableFileFilter(pngFilter);
		pngChooser.setAcceptAllFileFilterUsed(false);
		pngChooser.setDialogTitle(Resources.getMessage("emblem.export"));

		JPanel flagPanel = new JPanel(new GridLayout(6, 10));
		int iconSize = Math.round(0.67f * Emblems.IMG_SIZE);

		Systems.javaUI();// fix button background color
		for (int i = 0; i < flagButtons.length; i++) {
			flagButtons[i] = new JButton();
			flagButtons[i].setBackground(Colors.GRAY80);
			flagButtons[i].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[i].setPreferredSize(new Dimension(iconSize, iconSize));
			flagButtons[i].setActionCommand(Integer.toString(i));
			flagButtons[i].addMouseListener(this);
			flagButtons[i].addActionListener(this);

			flagPanel.add(flagButtons[i]);
		}
		Systems.systemUI();

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		contentPane.add(new JLabel(Resources.getMessage("emblem.label16")), BorderLayout.NORTH);
		contentPane.add(flagPanel, BorderLayout.CENTER);
		contentPane.add(new JLabel(Resources.getMessage("emblem.label128"), SwingConstants.RIGHT), BorderLayout.SOUTH);

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onTransparency(/*evt*/);
			}
		});

		JPanel bottomPane = new JPanel(new BorderLayout());
		bottomPane.add(contentPane, BorderLayout.CENTER);
		bottomPane.add(transButton, BorderLayout.SOUTH);

		largeFlag = new JLabel();
		largeFlag.setIcon(new ImageIcon(Emblems.BLANK16));

		free16Label = new JLabel();
		free128Label = new JLabel();

		addButton = new JButton(Resources.getMessage("emblem.add"));
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onAddEmblem();
			}
		});

		add2Button = new JButton(Resources.getMessage("emblem.add2"));
		add2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onImportEmblem();
			}
		});

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

	private void onTransparency(/*ActionEvent evt*/) {
		isTrans = !isTrans;
		refresh();
	}

	private void onAddEmblem() {
		if (Emblems.getFree128(of) <= 0 && Emblems.getFree16(of) <= 0)
			return;

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

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

		} catch (Exception e) {
			showOpenFailedMsg(e.getLocalizedMessage());
		}
	}

	private void onImportEmblem() {
		int emblem = -1;
		if (Emblems.getFree128(of) > 0) {
			emblem = flagImportDia.getEmblem(Resources.getMessage("emblem.import"), Emblems.TYPE_INHERIT);
		} else if (Emblems.getFree16(of) > 0) {
			emblem = flagImportDia.getEmblem(Resources.getMessage("emblem.import"), Emblems.TYPE_16);
		}

		if (emblem >= 0) {
			if (emblem >= Emblems.TOTAL128) {
				flagImportDia.import16(of, Emblems.count16(of), emblem - Emblems.TOTAL128);
			} else {
				flagImportDia.import128(of, Emblems.count128(of), emblem);
			}

			teamPanel.refresh();
			refresh();
		}
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof AbstractButton)) throw new NullPointerException("evt");

		AbstractButton btn = (AbstractButton) evt.getSource();
		int slot = Integer.parseInt(btn.getActionCommand());

		ImageIcon icon;
		boolean is128 = false;
		if (slot >= Emblems.count16(of)) {
			is128 = true;
			slot = Emblems.TOTAL16 - 1 - slot;
			icon = new ImageIcon(Emblems.get128(of, slot, !isTrans, false));
		} else {
			icon = new ImageIcon(Emblems.get16(of, slot, !isTrans, false));
		}
		Object[] options;
		Object[] options1 = {
				"Delete", "Import PNG / GIF",
				"Export as PNG", "Import (OF2)", "Cancel"
		};
		Object[] options2 = {
				"Delete", "Import PNG / GIF",
				"Export as PNG", "Cancel"
		};
		if (flagImportDia.isOf2Loaded()) {
			options = options1;
		} else {
			options = options2;
		}
		int n = JOptionPane.showOptionDialog(null, "Options:",
				"Emblem", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, icon, options,
				options[0]);

		if (n == 0) {
			if (is128) {
				Emblems.delete128(of, slot);
			} else {
				Emblems.delete16(of, slot);
			}
			teamPanel.refresh();
			refresh();
		}
		if (n == 1) {
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File source = chooser.getSelectedFile();
				try {
					BufferedImage image;
					image = ImageIO.read(source);

					int check = validateImage(image, is128 ? Emblems.PALETTE_SIZE128 : Emblems.PALETTE_SIZE16);
					if (check != -1) {
						if (is128) {
							if (check < Emblems.PALETTE_SIZE128) {
								if (check > 15) {
									Emblems.set128(of, slot, image);
								} else {
									showWasteSpaceMsg();
								}
							}
						} else {
							if (check < Emblems.PALETTE_SIZE16) {
								Emblems.set16(of, slot, image);
							} else {
								showManyColorsMsg(Emblems.PALETTE_SIZE16);
							}
						}
						teamPanel.refresh();
						refresh();
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Could not open file", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if (n == 2) {
			savePNG(is128, slot);
		}
		if (flagImportDia.isOf2Loaded() && n == 3) {
			int replacement;
			if (is128) {
				replacement = flagImportDia.getEmblem("Import Emblem", 2);
				if (replacement != -1) {
					flagImportDia.import128(of, slot, replacement);
				}
			} else {
				replacement = flagImportDia.getEmblem("Import Emblem", 1);
				if (replacement != -1) {
					replacement = replacement - Emblems.TOTAL128;
					flagImportDia.import16(of, slot, replacement);
				}
			}

			teamPanel.refresh();
			refresh();
		}
	}

	private void savePNG(boolean is128, int slot) {
		boolean error = false;
		int returnVal = pngChooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dest = pngChooser.getSelectedFile();
			dest = Files.addExtension(dest, Files.PNG);

			if (dest.exists()) {
				int n = JOptionPane.showConfirmDialog(null, dest.getName()
								+ "\nAlready exists in:\n" + dest.getParent()
								+ "\nAre you sure you want to overwrite this file?",
						"Overwrite:  " + dest.getName(),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null);
				if (n == 0) {
					boolean deleted = dest.delete();
					if (!deleted) {
						JOptionPane.showMessageDialog(null,
								"Could not access file", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					return;
				}
			}

			if (writeFile(dest, is128, slot)) {
				JOptionPane.showMessageDialog(null, dest.getName()
								+ "\nSaved in:\n" + dest.getParent(),
						"File Successfully Saved",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				error = true;
			}
			if (error) {
				JOptionPane.showMessageDialog(null, "Could not access file",
						"Error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	private boolean writeFile(File dest, boolean is128, int slot) {
		boolean ok;
		BufferedImage image;
		if (is128) {
			image = (BufferedImage) Emblems.get128(of, slot, false, false);
		} else {
			image = (BufferedImage) Emblems.get16(of, slot, false, false);
		}
		try {
			ImageIO.write(image, "png", dest);
			ok = true;
		} catch (IOException e) {
			ok = false;
		}
		return ok;
	}

	public void refresh() {
		Image icon;
		for (int i = 0; i < Emblems.count16(of); i++) {
			icon = Emblems.get16(of, i, !isTrans, true);
			flagButtons[i].setIcon(new ImageIcon(icon));
			flagButtons[i].setVisible(true);
		}
		for (int i = 0; i < Emblems.count128(of); i++) {
			icon = Emblems.get128(of, i, !isTrans, true);
			flagButtons[Emblems.TOTAL16 - 1 - i].setIcon(new ImageIcon(icon));
			flagButtons[Emblems.TOTAL16 - 1 - i].setVisible(true);
		}

		for (int i = Emblems.count16(of); i < Emblems.TOTAL16 - Emblems.count128(of); i++) {
			flagButtons[i].setVisible(false);
		}
		free16Label.setText(Resources.getMessage("emblem.free16", Emblems.getFree16(of)));
		free128Label.setText(Resources.getMessage("emblem.free128", Emblems.getFree128(of)));
		if (flagImportDia.isOf2Loaded()) {
			add2Button.setVisible(true);
		} else {
			add2Button.setVisible(false);
		}
		if (Emblems.getFree16(of) > 0) {
			addButton.setEnabled(true);
			add2Button.setEnabled(true);
		} else {
			addButton.setEnabled(false);
			add2Button.setEnabled(false);
		}
	}

	//region Emblem Icon button Mouse Events

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");
		if (!(e.getSource() instanceof AbstractButton)) throw new IllegalArgumentException("e");

		AbstractButton btn = (AbstractButton) e.getSource();
		int slot = Integer.parseInt(btn.getActionCommand());

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
	}

	public void mouseClicked(MouseEvent e) {
	}

	//endregion

	private static int validateImage(BufferedImage image, int paletteSize) {
		if (null == image) throw new NullPointerException("image");

		if (image.getWidth() != Emblems.IMG_SIZE || image.getHeight() != Emblems.IMG_SIZE)
			throw new IllegalArgumentException(
					Resources.getMessage("msg.invalidSize", Emblems.IMG_SIZE, Emblems.IMG_SIZE));

		ColorModel colorMod = image.getColorModel();
		if (null == colorMod || !(colorMod instanceof IndexColorModel))
			throw new IllegalArgumentException(Resources.getMessage("msg.notIndexed"));

		int colorsCount = ((IndexColorModel) colorMod).getMapSize();
		if (colorsCount > paletteSize)
			throw new IllegalArgumentException(Resources.getMessage("msg.manyColors", paletteSize));

		return colorsCount;
	}

	private static void showManyColorsMsg(int paletteSize) {
		JOptionPane.showMessageDialog(null, Resources.getMessage("msg.emblemManyColors", paletteSize),
				Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	private static void showWasteSpaceMsg() {
		JOptionPane.showMessageDialog(null,
				Resources.getMessage("msg.imgWasteSpace", Emblems.PALETTE_SIZE16, Emblems.PALETTE_SIZE128),
				Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	private static void showOpenFailedMsg(String msg) {
		if (Strings.isBlank(msg)) msg = Resources.getMessage("msg.openFailed");
		JOptionPane.showMessageDialog(null, msg, Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

}
