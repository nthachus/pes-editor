package editor;

import editor.data.*;
import editor.ui.*;
import editor.util.Colors;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.Systems;
import editor.util.swing.JComboBox;
import editor.util.swing.JList;
import editor.util.swing.JTextFieldLimit;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TeamPanel extends JPanel
		implements ActionListener, ListSelectionListener, MouseListener {

	private final OptionFile of;
	private final OptionFile of2;
	private final TransferPanel transferPan;
	private final EmblemChooserDialog flagChooser;
	private final LogoPanel logoPan;
	private final LogoChooserDialog logoChooser;
	private final GlobalPanel globalPan;
	private final KitImportDialog kitImportDia;

	private volatile EmblemPanel emblemPan;

	private String[] team = new String[Clubs.TOTAL];
	private volatile boolean isOk = false;

	public TeamPanel(
			OptionFile of, OptionFile of2,
			TransferPanel tran, EmblemChooserDialog fc,
			LogoPanel imgPan, GlobalPanel gp, KitImportDialog kid, LogoChooserDialog lc) {
		super(new BorderLayout());

		if (null == of) throw new NullPointerException("of");
		if (null == of2) throw new NullPointerException("of2");
		this.of = of;
		this.of2 = of2;
		transferPan = tran;
		flagChooser = fc;
		logoChooser = lc;
		logoPan = imgPan;
		kitImportDia = kid;
		globalPan = gp;

		initComponents();
	}

	//region Initialize the GUI components

	private/* final*/ DefaultIcon defaultIcon;
	private/* final*/ BackChooserDialog backChooser;

	private/* final*/ JList<String> teamList;
	private/* final*/ JTextField nameField;
	private/* final*/ JTextField abvEditor;
	private/* final*/ JButton badgeButton;
	private/* final*/ JButton backButton;
	private/* final*/ JComboBox<String> stadiumBox;
	private/* final*/ JPanel contentPane;
	private/* final*/ JButton color1Btn;
	private/* final*/ JButton color2Btn;

	private void initComponents() {
		defaultIcon = new DefaultIcon();
		backChooser = new BackChooserDialog(null);

		Systems.javaUI();// fix button background color

		backButton = new JButton(new ImageIcon(Emblems.BLANK16));
		backButton.setBackground(Colors.GRAY80);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSelectBackFlag();
			}
		});

		color1Btn = new JButton();
		color1Btn.setPreferredSize(new Dimension(20, 20));
		color1Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSelectBgColor(1);
			}
		});

		color2Btn = new JButton();
		color2Btn.setPreferredSize(new Dimension(20, 20));
		color2Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSelectBgColor(2);
			}
		});

		JLabel badgeLabel = new JLabel(Resources.getMessage("teamPane.badge"));
		badgeLabel.setAlignmentX(CENTER_ALIGNMENT);

		badgeButton = new JButton(new ImageIcon(Emblems.BLANK16));
		badgeButton.setBackground(Colors.GRAY80);
		badgeButton.setToolTipText(Resources.getMessage("teamPane.badgeTip"));
		badgeButton.setAlignmentX(CENTER_ALIGNMENT);
		badgeButton.addMouseListener(this);

		Systems.systemUI();

		JButton copyBtn = new JButton(new CopySwapIcon(false));
		copyBtn.setToolTipText(Resources.getMessage("teamPane.copyTip"));
		copyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onCopyToBackColor2();
			}
		});

		JButton swapBtn = new JButton(new CopySwapIcon(true));
		swapBtn.setToolTipText(Resources.getMessage("teamPane.swapTip"));
		swapBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSwapBackColors();
			}
		});

		stadiumBox = new JComboBox<String>();
		stadiumBox.setAlignmentX(CENTER_ALIGNMENT);
		stadiumBox.setPreferredSize(new Dimension(375, 25));
		stadiumBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSelectStadium(evt);
			}
		});

		teamList = new JList<String>();
		teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		teamList.setLayoutOrientation(JList.VERTICAL);
		teamList.setVisibleRowCount(Formations.PLAYER_COUNT);
		teamList.addListSelectionListener(this);
		teamList.addMouseListener(this);

		nameField = new JTextField(Clubs.NAME_LEN / 3);
		nameField.setDocument(new JTextFieldLimit(Clubs.NAME_LEN));
		nameField.setToolTipText(Resources.getMessage("teamPane.nameTip"));
		nameField.addActionListener(this);

		abvEditor = new JTextField(Math.round(1.5f * Clubs.ABBR_NAME_LEN));
		abvEditor.setDocument(new JTextFieldLimit(Clubs.ABBR_NAME_LEN));
		abvEditor.setToolTipText(Resources.getMessage("teamPane.abbrTip"));
		abvEditor.addActionListener(this);

		JPanel namePan = new JPanel();
		namePan.add(nameField);

		JPanel abbrNamePan = new JPanel();
		abbrNamePan.add(abvEditor);

		JPanel colorBtnPan = new JPanel(new GridLayout(0, 1));
		colorBtnPan.add(color1Btn);
		colorBtnPan.add(color2Btn);

		JPanel bgColorPan = new JPanel(new BorderLayout());
		bgColorPan.add(copyBtn, BorderLayout.WEST);
		bgColorPan.add(colorBtnPan, BorderLayout.CENTER);
		bgColorPan.add(swapBtn, BorderLayout.EAST);

		JPanel backPanel = new JPanel(new BorderLayout());
		backPanel.add(bgColorPan, BorderLayout.NORTH);
		backPanel.add(backButton, BorderLayout.SOUTH);

		JLabel flagLabel = new JLabel(Resources.getMessage("teamPane.flag"));
		flagLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel flagPanel = new JPanel();
		flagPanel.add(backPanel);

		JLabel stadiumLabel = new JLabel(Resources.getMessage("teamPane.stadium"));
		stadiumLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel stadiumPan = new JPanel();
		stadiumPan.add(stadiumBox);

		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(namePan);
		contentPane.add(abbrNamePan);
		contentPane.add(badgeLabel);
		contentPane.add(badgeButton);
		contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPane.add(flagLabel);
		contentPane.add(flagPanel);
		contentPane.add(Box.createRigidArea(new Dimension(0, 30)));
		contentPane.add(stadiumLabel);
		contentPane.add(stadiumPan);
		contentPane.add(new JPanel());

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(teamList);

		add(scroll, BorderLayout.WEST);
		add(contentPane, BorderLayout.CENTER);
	}

	//endregion

	public JList<String> getList() {
		return teamList;
	}

	public void setEmblemPan(EmblemPanel emblemPan) {
		this.emblemPan = emblemPan;
	}

	private void onSelectBackFlag() {
		int team = teamList.getSelectedIndex();
		if (team < 0) return;

		int flagId = backChooser.getBack(getEmblemImage(team),
				Clubs.getRed(of, team), Clubs.getGreen(of, team), Clubs.getBlue(of, team));
		if (flagId >= 0) {
			Clubs.setBackFlag(of, team, flagId);
			backButton.setIcon(backChooser.getFlagButton(flagId).getIcon());
		}
	}

	private void onSelectBgColor(int colorNo) {
		int team = teamList.getSelectedIndex();
		if (team < 0) return;

		boolean isSecond = (colorNo == 2);
		Color newColor = JColorChooser.showDialog(null,
				Resources.getMessage("teamPane.choiceBg", colorNo), Clubs.getColor(of, team, isSecond));

		if (newColor != null) {
			Clubs.setColor(of, team, isSecond, newColor);
			if (isSecond)
				color2Btn.setBackground(newColor);
			else
				color1Btn.setBackground(newColor);

			updateBackButton(team);
		}
	}

	private void onCopyToBackColor2() {
		int team = teamList.getSelectedIndex();
		if (team < 0) return;

		Clubs.setColor(of, team, true, Clubs.getColor(of, team, false));
		color2Btn.setBackground(color1Btn.getBackground());

		updateBackButton(team);
	}

	private void onSwapBackColors() {
		int team = teamList.getSelectedIndex();
		if (team < 0) return;

		Color c = Clubs.getColor(of, team, false);
		Clubs.setColor(of, team, false, Clubs.getColor(of, team, true));
		Clubs.setColor(of, team, true, c);

		color1Btn.setBackground(Clubs.getColor(of, team, false));
		color2Btn.setBackground(Clubs.getColor(of, team, true));

		updateBackButton(team);
	}

	private void onSelectStadium(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!"y".equalsIgnoreCase(evt.getActionCommand()))
			return;

		int stadiumId = stadiumBox.getSelectedIndex();
		int team = teamList.getSelectedIndex();
		if (stadiumId >= 0 && team >= 0) {
			Clubs.setStadium(of, team, stadiumId);
		}
	}

	public void refresh() {// TODO: !!!
		String[] listText = new String[67 + Clubs.TOTAL];
		stadiumBox.setActionCommand("n");
		stadiumBox.removeAllItems();
		for (int s = 0; s < Stadiums.TOTAL; s++) {
			stadiumBox.addItem(Stadiums.get(of, s));
		}

		stadiumBox.setSelectedIndex(-1);
		stadiumBox.setActionCommand("y");
		backButton.setIcon(new ImageIcon(Emblems.BLANK16));
		badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));
		team = Clubs.getNames(of);
		for (int t = 0; t < Clubs.TOTAL; t++) {
			listText[t] = Clubs.getAbbrName(of, t) + "     " + team[t];
		}
		globalPan.updateTeamBox(team);
		System.arraycopy(Stats.NATION, 0, listText, Clubs.TOTAL, 60);
		for (int n = 0; n < 7; n++) {
			listText[n + Clubs.TOTAL + 60] = Squads.EXTRAS[n];
		}
		isOk = false;
		teamList.setListData(listText);
		contentPane.setVisible(false);
		isOk = true;
	}

	/**
	 * On club name / abbreviation name changed.
	 */
	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof JTextField)) throw new IllegalArgumentException("evt");

		if (evt.getSource() == nameField) {
			String text = nameField.getText();
			if (!Strings.isEmpty(text) && text.length() <= 48) {
				int t = teamList.getSelectedIndex();
				Clubs.setName(of, t, text);
				refresh();
				transferPan.refresh();
				if (t < teamList.getModel().getSize() - 1) {
					teamList.setSelectedIndex(t + 1);
					teamList.ensureIndexIsVisible(teamList.getSelectedIndex());
					nameField.requestFocusInWindow();
					nameField.selectAll();
				}

			}
		} else {
			String text = abvEditor.getText();
			if (text.length() == 3) {
				text = text.toUpperCase();
				int t = teamList.getSelectedIndex();
				Clubs.setAbbrName(of, t, text);
				refresh();
				transferPan.refresh();
				if (t < teamList.getModel().getSize() - 1) {
					teamList.setSelectedIndex(t + 1);
					teamList.ensureIndexIsVisible(teamList.getSelectedIndex());
					abvEditor.requestFocusInWindow();
					abvEditor.selectAll();
				}

			}
		}
	}

	/**
	 * On team selected.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (isOk && !e.getValueIsAdjusting()) {
			int team = teamList.getSelectedIndex();
			if (team >= 0 && team < Clubs.TOTAL) {
				if (!contentPane.isVisible()) {
					contentPane.setVisible(true);
				}

				int f = Clubs.getEmblem(of, team);
				if (f >= Clubs.FIRST_EMBLEM
						&& f < Clubs.FIRST_EMBLEM + Emblems.TOTAL128 + Emblems.TOTAL16) {
					f = f - Clubs.FIRST_EMBLEM;
					badgeButton.setIcon(new ImageIcon(Emblems.getImage(of, f)));
				} else {
					if (f == team + Clubs.FIRST_DEF_EMBLEM) {
						badgeButton.setIcon(defaultIcon);
					} else {
						badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));
					}
				}

				color1Btn.setBackground(Clubs.getColor(of, team, false));
				color2Btn.setBackground(Clubs.getColor(of, team, true));

				updateBackButton(team);

				stadiumBox.setActionCommand("n");
				stadiumBox.setSelectedIndex(Clubs.getStadium(of, team));
				stadiumBox.setActionCommand("y");
				nameField.setText(this.team[team]);

				abvEditor.setText(Clubs.getAbbrName(of, team));

			} else {
				nameField.setText("");
				abvEditor.setText("");
				stadiumBox.setActionCommand("n");
				stadiumBox.setSelectedIndex(-1);
				stadiumBox.setActionCommand("y");
				badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));
				contentPane.setVisible(false);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * On teams list / emblem button clicked.
	 */
	public void mouseClicked(MouseEvent e) {
		int clicks = e.getClickCount();
		int team = teamList.getSelectedIndex();
		if (e.getSource() == teamList && e.getButton() == MouseEvent.BUTTON1
				&& clicks == 2) {
			if (of2.isLoaded()) {
				if (team != -1) {
					int t2 = kitImportDia.show(team);
					if (t2 != -1) {
						importKit(team, t2);
					}
				}
			}
		}

		if (e.getSource() == badgeButton && clicks == 1) {
			if ((e.getButton() == MouseEvent.BUTTON3 || (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()))) {

				if (team != -1 && team < Clubs.TOTAL) {
					Clubs.setEmblem(of, team, -1);
					badgeButton.setIcon(defaultIcon);
					updateBackButton(team);
				}

			} else if (e.getButton() == MouseEvent.BUTTON1) {
				if (team != -1) {
					int f = flagChooser.getEmblem("Choose Emblem", Emblems.TYPE_INHERIT);
					if (f != -1) {
						if (f < Emblems.TOTAL128) {
							badgeButton.setIcon(new ImageIcon(Emblems.get128(of, f, false, false)));
						} else {
							badgeButton.setIcon(new ImageIcon(Emblems.get16(of, f - Emblems.TOTAL128, false, false)));
						}
						Clubs.setEmblem(of, team, Emblems.getIndex(of, f));
						updateBackButton(team);
					}
				}
			}
		}
	}

	private void updateBackButton(int team) {
		int flag = Clubs.getBackFlag(of, team);
		ImageIcon icon = backChooser.getFlagBackground(getEmblemImage(team), flag,
				Clubs.getRed(of, team), Clubs.getGreen(of, team), Clubs.getBlue(of, team));
		backButton.setIcon(icon);
	}

	private Image getEmblemImage(int team) {
		int flag = Clubs.getEmblem(of, team);
		if (flag >= Clubs.FIRST_EMBLEM && flag < Clubs.FIRST_EMBLEM + Emblems.TOTAL128 + Emblems.TOTAL16) {
			ImageIcon icon = (ImageIcon) badgeButton.getIcon();
			return icon.getImage();
		}
		return null;
	}

	private void importKit(int t1, int t2) {
		if (t1 < Clubs.TOTAL) {
			int deleteId = Clubs.getEmblem(of, t1) - Clubs.FIRST_EMBLEM;
			if (deleteId >= 0 && deleteId < Emblems.TOTAL128 + Emblems.TOTAL16) {
				Emblems.deleteImage(of, deleteId);
			}
		}

		int[] logos = new int[4];
		boolean[] delete = new boolean[4];
		for (int l = 0; l < 4; l++) {
			delete[l] = true;
			if (Kits.isLogoUsed(of, t1, l)) {
				logos[l] = Kits.getLogo(of, t1, l);
			} else {
				logos[l] = -1;
			}
		}
		for (int t = 0; t < Clubs.TOTAL + Squads.NATION_COUNT + Squads.CLASSIC_COUNT; t++) {
			for (int l = 0; t != t1 && l < 4; l++) {
				if (logos[l] >= 0) {
					for (int k = 0; k < 4; k++) {
						if (Kits.getLogo(of, t, k) == logos[l]) {
							if (Kits.isLogoUsed(of, t, k)) {
								delete[l] = false;
							} else {
								Kits.setLogoUnused(of, t, k);
							}
						}
					}
				}
			}
		}

		for (int l = 0; l < 4; l++) {
			if (delete[l] && logos[l] >= 0 && logos[l] < 80) {
				Logos.delete(of, logos[l]);
			}
		}

		if (t1 < Clubs.TOTAL) {
			int emblem2 = Clubs.getEmblem(of2, t2) - Clubs.FIRST_EMBLEM;
			int embIndex = 0;
			if (emblem2 >= 0 && emblem2 < Emblems.TOTAL128 + Emblems.TOTAL16) {

				if (emblem2 < Emblems.TOTAL128) {
					if (Emblems.getFree128(of) > 0) {
						Emblems.importData128(of2, Emblems.getLocation(of2, emblem2), of, Emblems.count128(of));
						embIndex = Emblems.getIndex(of, Emblems.count128(of) - 1);
					} else {
						int rep = flagChooser.getEmblem("Replace Emblem", Emblems.TYPE_128);
						if (rep != -1) {
							Emblems.importData128(of2, Emblems.getLocation(of2, emblem2), of, rep);
							embIndex = Emblems.getIndex(of, rep);
						} else {
							embIndex = 0;
						}
					}
				} else {
					if (Emblems.getFree16(of) > 0) {
						Emblems.importData16(of2, Emblems.getLocation(of2, emblem2) - Emblems.TOTAL128, of,
								Emblems.count16(of));
						embIndex = Emblems.getIndex(of, Emblems.count16(of) + Emblems.TOTAL128 - 1);
					} else {
						int rep = flagChooser.getEmblem("Replace Emblem", Emblems.TYPE_16);
						if (rep != -1) {
							Emblems.importData16(of2, Emblems.getLocation(of2, emblem2) - Emblems.TOTAL128, of,
									rep - Emblems.TOTAL128);
							embIndex = Emblems.getIndex(of, rep);
						} else {
							embIndex = 0;
						}
					}
				}
			}

			Clubs.importClub(of, t1, of2, t2);
			if (emblem2 >= 0 && emblem2 < Emblems.TOTAL128 + Emblems.TOTAL16) {
				Clubs.setEmblem(of, t1, embIndex);
			}
		}

		Kits.importData(of2, t2, of, t1);

		for (int l = 0; l < 4; l++) {
			if (Kits.isLogoUsed(of2, t2, l)) {
				boolean dupe = false;
				for (int k = 0; !dupe && k < l; k++) {
					if (Kits.getLogo(of2, t2, l) == Kits.getLogo(of2, t2, k)) {
						dupe = true;
					}
				}
				if (!dupe) {
					int targetLogo = logoChooser.getFlag("Choose logo to replace",
							Logos.get(of2, Kits.getLogo(of2, t2, l), false));
					if (targetLogo >= 0) {
						Logos.importData(of2, Kits.getLogo(of2, t2, l), of, targetLogo);
						for (int k = l; k < 4; k++) {
							if (Kits.getLogo(of2, t2, l) == Kits.getLogo(of2, t2, k)) {
								Kits.setLogo(of, t1, k, targetLogo);
							}
						}
					} else {
						for (int k = l; k < 4; k++) {
							if (Kits.getLogo(of2, t2, l) == Kits.getLogo(of2, t2, k)) {
								Kits.setLogoUnused(of, t1, k);
							}
						}
					}
				}
			}
		}

		if (null != emblemPan) emblemPan.refresh();
		logoPan.refresh();
		transferPan.refresh();
		refresh();
	}

}
