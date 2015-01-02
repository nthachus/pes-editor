package editor;

import editor.data.Emblems;
import editor.data.OptionFile;
import editor.ui.EmblemImportDialog;
import editor.ui.ImageFileFilter;
import editor.ui.PngFilter;
import editor.util.Files;
import editor.util.UIUtil;

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
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class EmblemPanel extends JPanel implements MouseListener {
	private final OptionFile of;
	private final EmblemImportDialog flagImpDia;
	private final TeamPanel teamPanel;

	private JFileChooser chooser = new JFileChooser();
	private JFileChooser chooserPNG = new JFileChooser();

	private JButton[] flagButton;
	private JButton addButton;
	private JButton add2Button;
	private JLabel free16Label;
	private JLabel free128Label;
	private JLabel largeFlag;

	private volatile boolean isTrans = true;

	public EmblemPanel(OptionFile opt, EmblemImportDialog fid, TeamPanel tp) {
		super();
		of = opt;
		flagImpDia = fid;
		teamPanel = tp;

		ImageFileFilter filter128 = new ImageFileFilter();
		chooser.addChoosableFileFilter(filter128);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle("Import Emblem");
		PngFilter pngFilter = new PngFilter();
		chooserPNG.addChoosableFileFilter(pngFilter);
		chooserPNG.setAcceptAllFileFilterUsed(false);
		chooserPNG.setDialogTitle("Export Emblem");
		flagButton = new JButton[Emblems.TOTAL16];
		JPanel flagPanel = new JPanel(new GridLayout(6, 10));

		UIUtil.javaLookAndFeel();// fix button background color
		for (int l = 0; l < Emblems.TOTAL16; l++) {
			flagButton[l] = new JButton();
			flagButton[l].setBackground(new Color(0xCC, 0xCC, 0xCC));
			flagButton[l].setMargin(new Insets(0, 0, 0, 0));
			flagButton[l].setActionCommand(Integer.toString(l));
			flagButton[l].addMouseListener(this);
			flagButton[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent b) {
					int slot = Integer.parseInt(((JButton) b.getSource()).getActionCommand());
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
					if (flagImpDia.isOf2Loaded()) {
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

								int check = checkImage(image);
								if (check != -1) {
									if (is128) {
										if (check < 128) {
											if (check > 15) {
												Emblems.set128(of, slot, image);
											} else {
												wasteMsg();
											}
										}
									} else {
										if (check < 16) {
											Emblems.set16(of, slot, image);
										} else {
											col16Msg();
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
					if (flagImpDia.isOf2Loaded() && n == 3) {
						int replacement;
						if (is128) {
							replacement = flagImpDia.getEmblem("Import Emblem", 2);
							if (replacement != -1) {
								flagImpDia.import128(of, slot, replacement);
							}
						} else {
							replacement = flagImpDia
									.getEmblem("Import Emblem", 1);
							if (replacement != -1) {
								replacement = replacement - Emblems.TOTAL128;
								flagImpDia.import16(of, slot, replacement);
							}
						}

						teamPanel.refresh();
						refresh();
					}
				}
			});
			flagPanel.add(flagButton[l]);
		}
		UIUtil.systemLookAndFeel();

		JButton transButton = new JButton("Transparency");
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent t) {
				isTrans = !isTrans;
				refresh();
			}
		});

		free16Label = new JLabel();
		free128Label = new JLabel();
		addButton = new JButton("Add Emblem");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent t) {
				if (Emblems.getFree128(of) > 0 || (Emblems.getFree16(of) > 0)) {

					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File source = chooser.getSelectedFile();
						try {
							BufferedImage image;
							image = ImageIO.read(source);

							int check = checkImage(image);
							if (check != -1) {
								if (check < 16) {
									Emblems.set16(of, Emblems.count16(of), image);
								} else {
									if (Emblems.getFree128(of) == 0) {
										noSpaceMsg();
									} else {
										Emblems.set128(of, Emblems.count128(of), image);
									}
								}
								teamPanel.refresh();
								refresh();
							}
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,
									"Could not open file", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}

			}
		});

		add2Button = new JButton("Add Emblem (OF2)");
		add2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent t) {
				int emblem = -1;
				if (Emblems.getFree128(of) > 0) {
					emblem = flagImpDia.getEmblem("Import Emblem", 0);
				} else if (Emblems.getFree16(of) > 0) {
					emblem = flagImpDia.getEmblem("Import Emblem", 1);
				}
				if (emblem != -1) {
					if (emblem > Emblems.TOTAL128 - 1) {
						emblem = emblem - Emblems.TOTAL128;
						flagImpDia.import16(of, Emblems.count16(of), emblem);
					} else {
						flagImpDia.import128(of, Emblems.count128(of), emblem);
					}
					teamPanel.refresh();
					refresh();
				}
			}
		});

		JPanel freePanel = new JPanel();
		JPanel freePan = new JPanel(new GridLayout(0, 1));
		freePan.add(free16Label);
		freePan.add(free128Label);
		freePan.add(addButton);
		freePan.add(add2Button);
		freePanel.add(freePan);

		largeFlag = new JLabel();
		largeFlag.setIcon(new ImageIcon(Emblems.get16(of, -1, false, false)));

		JPanel pan1 = new JPanel(new BorderLayout());
		pan1.setBorder(BorderFactory.createLineBorder(Color.gray));
		pan1.add(new JLabel("16 Colour Format"), BorderLayout.NORTH);
		pan1.add(flagPanel, BorderLayout.CENTER);
		pan1.add(new JLabel("128 Colour Format", SwingConstants.RIGHT),
				BorderLayout.SOUTH);
		JPanel pan2 = new JPanel(new BorderLayout());
		pan2.add(pan1, BorderLayout.CENTER);
		pan2.add(transButton, BorderLayout.SOUTH);
		add(pan2);
		add(largeFlag);
		add(freePanel);
		refresh();
	}

	private void savePNG(boolean is128, int slot) {
		boolean error = false;
		int returnVal = chooserPNG.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dest = chooserPNG.getSelectedFile();
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
		for (int i = 0; i < Emblems.count16(of); i++) {
			flagButton[i].setIcon(new ImageIcon(Emblems.get16(of, i, !isTrans, true)));
			flagButton[i].setVisible(true);
		}
		for (int i = 0; i < Emblems.count128(of); i++) {
			flagButton[Emblems.TOTAL16 - 1 - i].setIcon(new ImageIcon(Emblems.get128(of, i, !isTrans, true)));
			flagButton[Emblems.TOTAL16 - 1 - i].setVisible(true);
		}

		for (int i = Emblems.count16(of); i < Emblems.TOTAL16 - Emblems.count128(of); i++) {
			flagButton[i].setVisible(false);
		}
		free16Label.setText("16-colour, can stock: " + Emblems.getFree16(of));
		free128Label
				.setText("128-colour, can stock: " + Emblems.getFree128(of));
		if (flagImpDia.isOf2Loaded()) {
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

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		JButton but = (JButton) e.getSource();
		int slot = Integer.parseInt(but.getActionCommand());
		if (slot >= Emblems.count16(of)) {
			slot = Emblems.TOTAL16 - 1 - slot;
			largeFlag.setIcon(new ImageIcon(Emblems.get128(of, slot, !isTrans, false)));
		} else {
			largeFlag.setIcon(new ImageIcon(Emblems.get16(of, slot, !isTrans, false)));
		}
	}

	public void mouseExited(MouseEvent e) {
		largeFlag.setIcon(new ImageIcon(Emblems.get16(of, -1, false, false)));
	}

	public void mouseClicked(MouseEvent e) {
	}

	private int checkImage(BufferedImage image) {
		int max = -1;
		if (image.getWidth() == 64 && image.getHeight() == 64) {
			ColorModel colorMod = image.getColorModel();
			if (colorMod instanceof IndexColorModel) {
				int[] pix = new int[Emblems.IMG_SIZE * Emblems.IMG_SIZE];
				Raster rast = image.getData();
				rast.getPixels(0, 0, Emblems.IMG_SIZE, Emblems.IMG_SIZE, pix);
				for (int i = 0; i < pix.length; i++) {
					if (pix[i] > max) {
						max = pix[i];
					}
				}
				if (max > 127) {
					colourMsg();
					max = -1;
				}
			} else {
				notIndexMsg();
			}
		} else {
			sizeMsg();
		}
		return max;
	}

	private void notIndexMsg() {
		JOptionPane.showMessageDialog(null, "PNG files must be INDEXED format",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	private void noSpaceMsg() {
		JOptionPane.showMessageDialog(null,
				"Not enough space for a 128-colour emblem", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void colourMsg() {
		JOptionPane.showMessageDialog(null, "Too many colours, maximum is 128",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	private void sizeMsg() {
		JOptionPane.showMessageDialog(null, "Size must be 64x64 pixels",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	private void col16Msg() {
		JOptionPane.showMessageDialog(null,
				"Too many colours for a 16-colour slot", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void wasteMsg() {
		JOptionPane.showMessageDialog(null,
				"A 16 colour image in a 128-colour slot would waste space!",
				"Error", JOptionPane.ERROR_MESSAGE);
	}

}
