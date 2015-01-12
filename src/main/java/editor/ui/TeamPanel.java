package editor.ui;

import editor.data.*;
import editor.util.Colors;
import editor.util.Resources;
import editor.util.Systems;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

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

	private volatile String[] teams;
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

		JLabel badgeLabel = new JLabel(Resources.getMessage("teamPane.badge"));
		badgeLabel.setAlignmentX(CENTER_ALIGNMENT);

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

	public void refresh() {
		isOk = false;

		stadiumBox.setActionCommand("n");
		stadiumBox.removeAllItems();
		String name;
		for (int s = 0; s < Stadiums.TOTAL; s++) {
			name = Stadiums.get(of, s);
			stadiumBox.addItem(name);
		}
		stadiumBox.setSelectedIndex(-1);
		stadiumBox.setActionCommand("y");

		backButton.setIcon(new ImageIcon(Emblems.BLANK16));
		badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));

		teams = Clubs.getNames(of);
		int ofs = teams.length;

		String[] list = new String[ofs + Squads.NATION_COUNT + Squads.CLASSIC_COUNT];
		for (int c = 0; c < ofs; c++) {
			name = Clubs.getAbbrName(of, c);
			list[c] = name + "     " + teams[c];
		}

		System.arraycopy(Stats.NATION, 0, list, ofs, Squads.NATION_COUNT);
		ofs += Squads.NATION_COUNT;
		System.arraycopy(Squads.EXTRAS, 0, list, ofs, Squads.CLASSIC_COUNT);

		globalPan.updateTeamBox(teams);
		teamList.setListData(list);
		contentPane.setVisible(false);

		isOk = true;
	}

	/**
	 * On club name / abbreviation name changed.
	 */
	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof JTextComponent)) throw new IllegalArgumentException("evt");

		int team = teamList.getSelectedIndex();
		if (team < 0) return;

		JTextComponent tf = (JTextComponent) evt.getSource();
		String name = tf.getText();
		boolean updated = false;

		if (null != name) {
			if (tf == nameField) {
				if (name.length() <= Clubs.NAME_LEN) {
					Clubs.setName(of, team, name);
					updated = true;
				}
			} else {
				if ((name = name.trim()).length() <= Clubs.ABBR_NAME_LEN) {
					name = name.toUpperCase();
					Clubs.setAbbrName(of, team, name);
					updated = true;
				}
			}
		}

		if (updated) {
			refresh();
			transferPan.refresh();

			if (team < teamList.getModel().getSize() - 1) {
				teamList.setSelectedIndex(team + 1);
				teamList.ensureIndexIsVisible(teamList.getSelectedIndex());

				tf.requestFocusInWindow();
				tf.selectAll();
			}
		}
	}

	/**
	 * On team selected.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (null == e) throw new NullPointerException("e");
		if (!isOk || e.getValueIsAdjusting()) return;

		int team = teamList.getSelectedIndex();
		if (team < 0 || team >= teams.length) {
			nameField.setText("");
			abvEditor.setText("");

			stadiumBox.setActionCommand("n");
			stadiumBox.setSelectedIndex(-1);
			stadiumBox.setActionCommand("y");

			badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));
			contentPane.setVisible(false);

		} else {
			int flag = Clubs.getEmblem(of, team);
			if (flag >= Clubs.FIRST_EMBLEM && flag < Clubs.FIRST_EMBLEM + Emblems.TOTAL128 + Emblems.TOTAL16) {

				Image icon = Emblems.getImage(of, flag - Clubs.FIRST_EMBLEM);
				badgeButton.setIcon(new ImageIcon(icon));
			} else if (flag == team + Clubs.FIRST_DEF_EMBLEM) {
				badgeButton.setIcon(defaultIcon);
			} else {
				badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));
			}

			color1Btn.setBackground(Clubs.getColor(of, team, false));
			color2Btn.setBackground(Clubs.getColor(of, team, true));

			updateBackButton(team);

			stadiumBox.setActionCommand("n");
			int stadiumId = Clubs.getStadium(of, team);
			stadiumBox.setSelectedIndex(stadiumId);
			stadiumBox.setActionCommand("y");

			nameField.setText(teams[team]);
			abvEditor.setText(Clubs.getAbbrName(of, team));

			contentPane.setVisible(true);
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
		if (null == e) throw new NullPointerException("e");
		if (null == e.getSource()) throw new IllegalArgumentException("e");

		int team = teamList.getSelectedIndex();
		if (team < 0) return;

		int mBtn = e.getButton();
		int clicks = e.getClickCount();
		if (e.getSource() == teamList && mBtn == MouseEvent.BUTTON1 && clicks > 1) {

			if (of2.isLoaded()) {
				int kitTeam = kitImportDia.show(team);
				if (kitTeam >= 0) {
					importTeamKit(kitTeam, team);
				}
			}
		} else if (e.getSource() == badgeButton && clicks == 1) {
			if (mBtn == MouseEvent.BUTTON3 || (mBtn == MouseEvent.BUTTON1 && e.isControlDown())) {

				deleteTeamEmblem(team);
			} else if (mBtn == MouseEvent.BUTTON1) {
				choiceTeamEmblem(team);
			}
		}
	}

	private void deleteTeamEmblem(int team) {
		if (team < Clubs.TOTAL) {
			Clubs.setEmblem(of, team, -1);
			badgeButton.setIcon(defaultIcon);

			updateBackButton(team);
		}
	}

	private void choiceTeamEmblem(int team) {
		int flag = flagChooser.getEmblem(Resources.getMessage("teamPane.choiceFlag"), null);
		if (flag < 0) return;

		Image icon;
		if (flag < Emblems.TOTAL128) {
			icon = Emblems.get128(of, flag, false, false);
		} else {
			icon = Emblems.get16(of, flag - Emblems.TOTAL128, false, false);
		}

		badgeButton.setIcon(new ImageIcon(icon));
		Clubs.setEmblem(of, team, Emblems.getIndex(of, flag));

		updateBackButton(team);
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

	private void importTeamKit(int teamSource, int teamDest) {
		deleteEmblem(teamDest);

		deleteLogos(teamDest);

		if (teamDest < Clubs.TOTAL)
			importClubData(teamSource, teamDest);

		Kits.importData(of2, teamSource, of, teamDest);

		importLogos(teamSource, teamDest);

		if (null != emblemPan) emblemPan.refresh();
		logoPan.refresh();
		transferPan.refresh();
		refresh();
	}

	private void deleteEmblem(int team) {
		if (team < Clubs.TOTAL) {
			int deleteId = Clubs.getEmblem(of, team) - Clubs.FIRST_EMBLEM;
			if (deleteId >= 0 && deleteId < Emblems.TOTAL128 + Emblems.TOTAL16) {
				Emblems.deleteImage(of, deleteId);
			}
		}
	}

	private void deleteLogos(int team) {
		int[] usedLogos = getUsedLogos(team);
		boolean[] logoDelete = checkLogosToDelete(team, usedLogos);

		for (int l = 0; l < usedLogos.length; l++) {
			if (logoDelete[l] && usedLogos[l] >= 0 && usedLogos[l] < Logos.TOTAL) {
				Logos.delete(of, usedLogos[l]);
			}
		}
	}

	private int[] getUsedLogos(int team) {
		int[] usedLogos = new int[Kits.TOTAL_LOGO];
		for (int l = 0; l < usedLogos.length; l++) {
			if (Kits.isLogoUsed(of, team, l)) {
				usedLogos[l] = Kits.getLogo(of, team, l);
			} else {
				usedLogos[l] = -1;
			}
		}
		return usedLogos;
	}

	private boolean[] checkLogosToDelete(int team, int[] usedLogos) {
		boolean[] logoDelete = new boolean[usedLogos.length];
		Arrays.fill(logoDelete, true);

		for (int t = 0; t < teamList.getModel().getSize(); t++) {
			if (t == team) continue;

			for (int l = 0; l < logoDelete.length; l++) {
				if (usedLogos[l] < 0 || usedLogos[l] >= Logos.TOTAL)
					continue;

				for (int k = 0; k < logoDelete.length; k++) {
					if (Kits.getLogo(of, t, k) != usedLogos[l]) continue;

					if (Kits.isLogoUsed(of, t, k)) {
						logoDelete[l] = false;
					} else {
						Kits.setLogoUnused(of, t, k);
					}
				}
			}
		}

		return logoDelete;
	}

	private void importClubData(int clubSource, int clubDest) {
		int emblemS = Clubs.getEmblem(of2, clubSource) - Clubs.FIRST_EMBLEM;

		boolean embValid = (emblemS >= 0 && emblemS < Emblems.TOTAL128 + Emblems.TOTAL16);
		int embIndex = 0;
		if (embValid) {
			int locS = Emblems.getLocation(of2, emblemS);

			if (emblemS < Emblems.TOTAL128) {
				if (Emblems.getFree128(of) > 0) {
					Emblems.importData128(of2, locS, of, Emblems.count128(of));
					embIndex = Emblems.getIndex(of, Emblems.count128(of) - 1);
				} else {
					int rep = flagChooser.getEmblem(Resources.getMessage("teamPane.replaceFlag"), EmblemType.highRes);
					if (rep >= 0) {
						Emblems.importData128(of2, locS, of, rep);
						embIndex = Emblems.getIndex(of, rep);
					}
				}
			} else {
				if (Emblems.getFree16(of) > 0) {
					Emblems.importData16(of2, locS - Emblems.TOTAL128, of, Emblems.count16(of));
					embIndex = Emblems.getIndex(of, Emblems.count16(of) + Emblems.TOTAL128 - 1);
				} else {
					int rep = flagChooser.getEmblem(Resources.getMessage("teamPane.replaceFlag"), EmblemType.lowRes);
					if (rep >= 0) {
						Emblems.importData16(of2, locS - Emblems.TOTAL128, of, rep - Emblems.TOTAL128);
						embIndex = Emblems.getIndex(of, rep);
					}
				}
			}
		}

		Clubs.importClub(of2, clubSource, of, clubDest);
		if (embValid) {
			Clubs.setEmblem(of, clubDest, embIndex);
		}
	}

	private void importLogos(int teamSource, int teamDest) {
		for (int l = 0; l < Kits.TOTAL_LOGO; l++) {
			if (!Kits.isLogoUsed(of2, teamSource, l))
				continue;

			boolean isDupe = false;
			for (int k = 0; k < l; k++) {
				if (Kits.getLogo(of2, teamSource, l) == Kits.getLogo(of2, teamSource, k)) {
					isDupe = true;
					break;
				}
			}
			if (isDupe) continue;

			Image targetIco = Logos.get(of2, Kits.getLogo(of2, teamSource, l), false);
			int targetLogo = logoChooser.getFlag(Resources.getMessage("teamPane.replaceLogo"), targetIco);

			if (targetLogo >= 0)
				Logos.importData(of2, Kits.getLogo(of2, teamSource, l), of, targetLogo);

			for (int k = l; k < Kits.TOTAL_LOGO; k++) {
				if (Kits.getLogo(of2, teamSource, l) == Kits.getLogo(of2, teamSource, k)) {

					if (targetLogo >= 0)
						Kits.setLogo(of, teamDest, k, targetLogo);
					else
						Kits.setLogoUnused(of, teamDest, k);
				}
			}
		}
	}

}
