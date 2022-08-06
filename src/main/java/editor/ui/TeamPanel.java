package editor.ui;

import editor.data.*;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class TeamPanel extends JPanel implements ActionListener, ListSelectionListener, MouseListener {
	private static final long serialVersionUID = -7358055199745460533L;
	private static final Logger log = LoggerFactory.getLogger(TeamPanel.class);

	private final OptionFile of;
	private final OptionFile of2;
	private final TransferPanel transferPan;
	private final EmblemChooserDialog flagChooser;
	private final LogoPanel logoPan;
	private final LogoChooserDialog logoChooser;
	private final GlobalPanel globalPan;
	private final KitImportDialog kitImportDia;

	private/* volatile*/ EmblemPanel emblemPan;

	private final String[] teams = new String[Clubs.TOTAL];
	private volatile boolean isOk = false;

	public TeamPanel(
			OptionFile of, OptionFile of2,
			TransferPanel tp, EmblemChooserDialog fc,
			LogoPanel imgPan, GlobalPanel gp, KitImportDialog kid, LogoChooserDialog lc
	) {
		super(new BorderLayout());

		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == of2) {
			throw new NullArgumentException("of2");
		}
		this.of = of;
		this.of2 = of2;
		transferPan = tp;
		flagChooser = fc;
		logoChooser = lc;
		logoPan = imgPan;
		kitImportDia = kid;
		globalPan = gp;

		log.debug("Initialize Team panel with Transfer panel #{} ...", tp.hashCode());
		initComponents();
	}

	//region Initialize the GUI components

	private/* final*/ transient DefaultIcon defaultIcon;
	private/* final*/ BackChooserDialog backChooser;

	private/* final*/ JList/*<String>*/ teamList;
	private/* final*/ JTextField nameField;
	private/* final*/ JTextField abvEditor;
	private/* final*/ JButton badgeButton;
	private/* final*/ JButton backButton;
	private/* final*/ JComboBox/*<String>*/ stadiumBox;
	private/* final*/ JPanel contentPane;
	private/* final*/ JButton color1Btn;
	private/* final*/ JButton color2Btn;

	private void initComponents() {
		defaultIcon = new DefaultIcon();
		backChooser = new BackChooserDialog(null);

		UIUtil.javaUI();// fix button background color

		backButton = new JButton(new ImageIcon(Emblems.BLANK16));
		backButton.setBackground(UIUtil.GRAY80);
		backButton.setActionCommand("BackFlag");
		backButton.addActionListener(this);

		color1Btn = new JButton();
		color1Btn.setPreferredSize(new Dimension(20, 20));
		color1Btn.setActionCommand("Color1");
		color1Btn.addActionListener(this);

		color2Btn = new JButton();
		color2Btn.setPreferredSize(new Dimension(20, 20));
		color2Btn.setActionCommand("Color2");
		color2Btn.addActionListener(this);

		badgeButton = new JButton(new ImageIcon(Emblems.BLANK16));
		badgeButton.setBackground(UIUtil.GRAY80);
		badgeButton.setToolTipText(Resources.getMessage("teamPane.badgeTip"));
		badgeButton.setAlignmentX(CENTER_ALIGNMENT);
		badgeButton.addMouseListener(this);

		UIUtil.systemUI();

		JButton copyBtn = new JButton(new CopySwapIcon(false));
		copyBtn.setToolTipText(Resources.getMessage("teamPane.copyTip"));
		copyBtn.setActionCommand("CopyColor");
		copyBtn.addActionListener(this);

		JButton swapBtn = new JButton(new CopySwapIcon(true));
		swapBtn.setToolTipText(Resources.getMessage("teamPane.swapTip"));
		swapBtn.setActionCommand("SwapColor");
		swapBtn.addActionListener(this);

		stadiumBox = new JComboBox/*<String>*/();
		stadiumBox.setAlignmentX(CENTER_ALIGNMENT);
		stadiumBox.setPreferredSize(new Dimension(375, 25));
		stadiumBox.addActionListener(this);

		teamList = new JList/*<String>*/();
		teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		teamList.setLayoutOrientation(JList.VERTICAL);
		teamList.setVisibleRowCount(Formations.PLAYER_COUNT);
		teamList.addListSelectionListener(this);
		teamList.addMouseListener(this);

		nameField = new JTextField(Clubs.NAME_LEN / 3);
		nameField.setDocument(new JTextFieldLimit(Clubs.NAME_LEN * 2 / 3));
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
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(teamList);

		add(scroll, BorderLayout.WEST);
		add(contentPane, BorderLayout.CENTER);
	}

	//endregion

	public JList getList() {
		return teamList;
	}

	public void setEmblemPan(EmblemPanel emblemPan) {
		this.emblemPan = emblemPan;
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		int team = teamList.getSelectedIndex();
		if (team < 0) {
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("Perform action: {} for team: {}, on: {}",
					evt.getActionCommand(), team, Strings.valueOf(evt.getSource()));
		}

		if ("BackFlag".equalsIgnoreCase(evt.getActionCommand())) {
			selectBackFlag(team);
		} else if ("Color1".equalsIgnoreCase(evt.getActionCommand())) {
			selectBgColor(team, 1);
		} else if ("Color2".equalsIgnoreCase(evt.getActionCommand())) {
			selectBgColor(team, 2);
		} else if ("CopyColor".equalsIgnoreCase(evt.getActionCommand())) {
			copyToBackColor2(team);
		} else if ("SwapColor".equalsIgnoreCase(evt.getActionCommand())) {
			swapBackColors(team);
		} else if (evt.getSource() == stadiumBox) {
			stadiumChanged(team, evt);
		} else {
			if (!(evt.getSource() instanceof JTextComponent)) {
				throw new IllegalArgumentException("evt");
			}
			clubNameChanged(team, (JTextComponent) evt.getSource());
		}
	}

	private void selectBackFlag(int team) {
		int flagId = backChooser.getBackFlag(getEmblemImage(team),
				Clubs.getRed(of, team), Clubs.getGreen(of, team), Clubs.getBlue(of, team));
		if (flagId >= 0) {
			Clubs.setBackFlag(of, team, flagId);
			backButton.setIcon(backChooser.getFlagButton(flagId).getIcon());
		}
		// DEBUG
		log.debug("Select completed BackFlag {} for team {}", flagId, team);
	}

	private void selectBgColor(int team, int colorNo) {
		boolean isSecond = (colorNo == 2);
		Color newColor = JColorChooser.showDialog(null,
				Resources.getMessage("teamPane.choiceBg", colorNo), Clubs.getColor(of, team, isSecond));

		log.debug("Selected BG {} color: {}", colorNo, newColor);
		if (newColor != null) {
			Clubs.setColor(of, team, isSecond, newColor);
			if (isSecond) {
				color2Btn.setBackground(newColor);
			} else {
				color1Btn.setBackground(newColor);
			}

			updateBackButton(team);
		}
	}

	private void copyToBackColor2(int team) {
		Clubs.setColor(of, team, true, Clubs.getColor(of, team, false));
		color2Btn.setBackground(color1Btn.getBackground());

		updateBackButton(team);
	}

	private void swapBackColors(int team) {
		Color c = Clubs.getColor(of, team, false);
		Clubs.setColor(of, team, false, Clubs.getColor(of, team, true));
		Clubs.setColor(of, team, true, c);

		color1Btn.setBackground(Clubs.getColor(of, team, false));
		color2Btn.setBackground(Clubs.getColor(of, team, true));

		updateBackButton(team);
	}

	private void stadiumChanged(int team, ActionEvent evt) {
		if (!"y".equalsIgnoreCase(evt.getActionCommand())) {
			return;
		}

		int stadiumId = stadiumBox.getSelectedIndex();
		if (stadiumId >= 0) {
			Clubs.setStadium(of, team, stadiumId);

			log.debug("Updating of team {} with stadium {} succeeded", team, stadiumId);
		}
	}

	public void refresh() {
		log.info("Team panel is refreshing..");
		isOk = false;

		stadiumBox.setActionCommand("n");
		stadiumBox.removeAllItems();
		String name;
		for (int s = 0; s < Stadiums.TOTAL; s++) {
			name = Stadiums.get(of, s);
			stadiumBox.addItem(name);
		}
		stadiumBox.setSelectedIndex(-1);

		backButton.setIcon(new ImageIcon(Emblems.BLANK16));
		badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));

		System.arraycopy(Clubs.getNames(of), 0, teams, 0, teams.length);
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

		stadiumBox.setActionCommand("y");
		isOk = true;
		// DEBUG
		log.debug("Refresh completed for {} Teams on panel", list.length);
	}

	/**
	 * On club name / abbreviation name changed.
	 */
	private void clubNameChanged(int team, JTextComponent tf) {
		String name = tf.getText();
		boolean updated = false;
		// DEBUG
		log.debug("Try to change club {} name/abbr name to '{}'", team, name);
		if (null != name) {
			if (tf == nameField) {
				if (name.length() <= Clubs.NAME_LEN * 2 / 3) {
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
			// DEBUG
			log.debug("Change succeeded on club {} name/abbr name to '{}'", team, name);
		}
	}

	/**
	 * On team selected.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (null == e) {
			throw new NullArgumentException("e");
		}
		if (!isOk || e.getValueIsAdjusting()) {
			return;
		}

		int team = teamList.getSelectedIndex();
		// DEBUG
		log.info("On team {} was selected", team);
		if (team < 0 || team >= teams.length) {
			nameField.setText(Strings.EMPTY);
			abvEditor.setText(Strings.EMPTY);

			stadiumBox.setActionCommand("n");
			stadiumBox.setSelectedIndex(-1);
			stadiumBox.setActionCommand("y");

			badgeButton.setIcon(new ImageIcon(Emblems.BLANK16));
			contentPane.setVisible(false);

		} else {
			contentPane.setVisible(true);

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

			log.debug("Show up team [{}] {}", team, teams[team]);
		}
	}

	public void mousePressed(MouseEvent e) {
		// Handle mouse click event only
	}

	public void mouseReleased(MouseEvent e) {
		// Handle mouse click event only
	}

	public void mouseEntered(MouseEvent e) {
		// Handle mouse click event only
	}

	public void mouseExited(MouseEvent e) {
		// Handle mouse click event only
	}

	/**
	 * On teams list / emblem button clicked.
	 */
	public void mouseClicked(MouseEvent e) {
		if (null == e) {
			throw new NullArgumentException("e");
		}
		if (null == e.getSource()) {
			throw new NullArgumentException("e.source");
		}

		int team = teamList.getSelectedIndex();
		if (team < 0) {
			return;
		}

		int mBtn = e.getButton();
		int clicks = e.getClickCount();
		// DEBUG
		log.info("On team/emblem list clicked, mouse: {}, clicks: {}", mBtn, clicks);
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
		if (flag < 0) {
			return;
		}
		log.debug("Try to update team {} emblem: {}", team, flag);

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
		ImageIcon icon = backChooser.getBackFlag(getEmblemImage(team), flag,
				Clubs.getRed(of, team), Clubs.getGreen(of, team), Clubs.getBlue(of, team));
		backButton.setIcon(icon);
		// DEBUG
		log.debug("Update succeeded on BackFlag button for team {}, flag: {}", team, flag);
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

		if (teamDest < Clubs.TOTAL) {
			importClubData(teamSource, teamDest);
		}

		Kits.importData(of2, teamSource, of, teamDest);

		importLogos(teamSource, teamDest);

		if (null != emblemPan) {
			emblemPan.refresh();
		}
		logoPan.refresh();
		transferPan.refresh();

		refresh();
		log.debug("Import completed for team Kit: {} -> {}", teamSource, teamDest);
	}

	private void deleteEmblem(int team) {
		if (team < Clubs.TOTAL) {
			int deleteId = Clubs.getEmblem(of, team) - Clubs.FIRST_EMBLEM;
			if (deleteId >= 0 && deleteId < Emblems.TOTAL128 + Emblems.TOTAL16) {
				Emblems.deleteImage(of, deleteId);

				log.debug("Emblem {} was deleted for team {}", deleteId, team);
			}
		}
	}

	private void deleteLogos(int team) {
		int[] usedLogos = getUsedLogos(team);
		boolean[] logoDelete = checkLogosToDelete(team, usedLogos);

		for (int l = 0; l < usedLogos.length; l++) {
			if (logoDelete[l] && usedLogos[l] >= 0 && usedLogos[l] < Logos.TOTAL) {
				Logos.delete(of, usedLogos[l]);

				log.debug("Logo {} was deleted for team {}", usedLogos[l], team);
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
		// DEBUG
		log.debug("Team {} used Logos: {}", team, usedLogos);
		return usedLogos;
	}

	private boolean[] checkLogosToDelete(int team, int[] usedLogos) {
		boolean[] logoDelete = new boolean[usedLogos.length];
		Arrays.fill(logoDelete, true);

		for (int t = 0, sz = teamList.getModel().getSize(); t < sz; t++) {
			if (t == team) {
				continue;
			}

			for (int l = 0; l < logoDelete.length; l++) {
				if (usedLogos[l] < 0 || usedLogos[l] >= Logos.TOTAL) {
					continue;
				}

				for (int k = 0; k < logoDelete.length; k++) {
					if (Kits.getLogo(of, t, k) != usedLogos[l]) {
						continue;
					}

					if (Kits.isLogoUsed(of, t, k)) {
						logoDelete[l] = false;
					} else {
						Kits.setLogoUnused(of, t, k);
					}
				}
			}
		}

		log.debug("Team {} has deletable Logos: {}", team, logoDelete);
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
		// DEBUG
		log.debug("Import data completed from Club: {} -> {}", clubSource, clubDest);
	}

	private void importLogos(int teamSource, int teamDest) {
		for (int l = 0; l < Kits.TOTAL_LOGO; l++) { //NOSONAR java:S1186
			if (!Kits.isLogoUsed(of2, teamSource, l)) {
				continue;
			}

			boolean isDupe = false;
			for (int k = 0; k < l; k++) {
				if (Kits.getLogo(of2, teamSource, l) == Kits.getLogo(of2, teamSource, k)) {
					isDupe = true;
					break;
				}
			}
			if (isDupe) {
				continue;
			}

			Image targetIco = Logos.get(of2, Kits.getLogo(of2, teamSource, l), false);
			int targetLogo = logoChooser.getLogo(Resources.getMessage("teamPane.replaceLogo"), targetIco);

			if (targetLogo >= 0) {
				Logos.importData(of2, Kits.getLogo(of2, teamSource, l), of, targetLogo);
			}

			for (int k = l; k < Kits.TOTAL_LOGO; k++) {
				if (Kits.getLogo(of2, teamSource, l) == Kits.getLogo(of2, teamSource, k)) {

					if (targetLogo >= 0) {
						Kits.setLogo(of, teamDest, k, targetLogo);
					} else {
						Kits.setLogoUnused(of, teamDest, k);
					}
				}
			}
		}
		// DEBUG
		log.debug("Import logos completed from team: {} -> {}", teamSource, teamDest);
	}

}
