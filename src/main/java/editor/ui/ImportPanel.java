package editor.ui;

import editor.data.*;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImportPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 2005805172621830625L;
	private static final Logger log = LoggerFactory.getLogger(ImportPanel.class);

	private final OptionFile of;
	private final OptionFile of2;

	private final WenShopPanel wenShop;
	private final StadiumPanel stadiumPan;
	private final LeaguePanel leaguePan;
	private final TeamPanel teamPan;
	private final EmblemPanel emblemPan;
	private final LogoPanel logoPan;
	private final TransferPanel transferPan;

	public ImportPanel(
			OptionFile of, OptionFile of2,
			WenShopPanel wenShop, StadiumPanel stadiumPan, LeaguePanel leaguePan, TeamPanel teamPan,
			EmblemPanel emblemPan, LogoPanel logoPan, TransferPanel transferPan) {
		super(new BorderLayout());

		if (null == of) throw new NullPointerException("of");
		if (null == of2) throw new NullPointerException("of2");
		this.of = of;
		this.of2 = of2;
		this.wenShop = wenShop;
		this.stadiumPan = stadiumPan;
		this.leaguePan = leaguePan;
		this.teamPan = teamPan;
		this.emblemPan = emblemPan;
		this.logoPan = logoPan;
		this.transferPan = transferPan;

		log.debug("Import panel is initializing..");
		initComponents();
	}

	//region Initialize the GUI components

	private/* final*/ JLabel msgLabel;
	private/* final*/ JPanel contentPane;
	private/* final*/ JButton optionsButton;
	private/* final*/ JButton stadiumButton;
	private/* final*/ JButton leagueButton;
	private/* final*/ JButton bootsButton;
	private/* final*/ JButton clubNameButton;
	private/* final*/ JButton playerButton;
	private/* final*/ JButton allKitButton;

	private void initComponents() {
		optionsButton = new JButton(Resources.getMessage("import.options"));
		optionsButton.setActionCommand("Options");
		optionsButton.addActionListener(this);

		stadiumButton = new JButton(Resources.getMessage("import.stadiums"));
		stadiumButton.setActionCommand("Stadiums");
		stadiumButton.addActionListener(this);

		leagueButton = new JButton(Resources.getMessage("import.leagues"));
		leagueButton.setActionCommand("Leagues");
		leagueButton.addActionListener(this);

		bootsButton = new JButton(Resources.getMessage("import.boots"));
		bootsButton.setActionCommand("Boots");
		bootsButton.addActionListener(this);

		playerButton = new JButton(Resources.getMessage("import.players"));
		playerButton.setActionCommand("Players");
		playerButton.addActionListener(this);

		clubNameButton = new JButton(Resources.getMessage("import.clubs"));
		clubNameButton.setActionCommand("Clubs");
		clubNameButton.addActionListener(this);

		allKitButton = new JButton(Resources.getMessage("import.kits"));
		allKitButton.setActionCommand("Kits");
		allKitButton.addActionListener(this);

		JPanel buttonsPan = new JPanel(new GridLayout(0, 1));
		buttonsPan.add(optionsButton);
		buttonsPan.add(stadiumButton);
		buttonsPan.add(leagueButton);
		buttonsPan.add(bootsButton);
		buttonsPan.add(playerButton);
		buttonsPan.add(clubNameButton);
		buttonsPan.add(allKitButton);

		contentPane = new JPanel();
		contentPane.add(buttonsPan);
		contentPane.setEnabled(false);

		msgLabel = new JLabel(Resources.getMessage("import.todo"));

		add(msgLabel, BorderLayout.NORTH);
		add(contentPane, BorderLayout.CENTER);
	}

	//endregion

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		log.debug("Try to perform Import action: {}", evt.getActionCommand());

		if ("Options".equalsIgnoreCase(evt.getActionCommand())) {
			importOptions();
		} else if ("Stadiums".equalsIgnoreCase(evt.getActionCommand())) {
			importStadiums();
		} else if ("Leagues".equalsIgnoreCase(evt.getActionCommand())) {
			importLeagues();
		} else if ("Boots".equalsIgnoreCase(evt.getActionCommand())) {
			importBoots();
		} else if ("Players".equalsIgnoreCase(evt.getActionCommand())) {
			importPlayers();
		} else if ("Clubs".equalsIgnoreCase(evt.getActionCommand())) {
			importClubs();
		} else/* if ("Kits".equalsIgnoreCase(evt.getActionCommand()))*/ {
			importKits();
		}
	}

	private void importKits() {
		Clubs.importData(of2, of);

		for (int i = 7; i <= 8; i++) {
			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		emblemPan.refresh();
		logoPan.refresh();
		teamPan.refresh();
		transferPan.refresh();

		allKitButton.setEnabled(false);
		// DEBUG
		log.debug("Kits was imported from OF2");
	}

	private void importClubs() {
		Clubs.importNames(of2, of);

		teamPan.refresh();
		transferPan.refresh();

		clubNameButton.setEnabled(false);
		// DEBUG
		log.debug("Club names was imported from OF2");
	}

	private void importPlayers() {
		for (int i = 3; i <= 5; i++) {
			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		transferPan.refresh();

		playerButton.setEnabled(false);
		// DEBUG
		log.debug("Players was imported from OF2");
	}

	private void importBoots() {
		Boots.importData(of2, of);

		bootsButton.setEnabled(false);
		// DEBUG
		log.debug("Boots was imported from OF2");
	}

	private void importLeagues() {
		Leagues.importData(of2, of);

		leaguePan.refresh();

		leagueButton.setEnabled(false);
		// DEBUG
		log.debug("Leagues was imported from OF2");
	}

	private void importStadiums() {
		Stadiums.importData(of2, of);

		stadiumPan.refresh();
		teamPan.refresh();

		stadiumButton.setEnabled(false);
		// DEBUG
		log.debug("Stadiums was imported from OF2");
	}

	private void importOptions() {
		for (int i = 0; i <= 10; i++) {
			if (i > 1 && i < 9) continue;

			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		wenShop.getWenPanel().refresh();
		wenShop.getShopPanel().refresh();

		optionsButton.setEnabled(false);
		// DEBUG
		log.debug("Options was imported from OF2");
	}

	public void refresh() {
		log.debug("Try to refresh Import panel for OF2: {}", of2.isLoaded());

		if (!of2.isLoaded()) {
			contentPane.setVisible(false);
			msgLabel.setText(Resources.getMessage("import.todo"));
		} else {
			msgLabel.setText(Resources.getMessage("import.label", of2.getFilename()));
			contentPane.setVisible(true);

			optionsButton.setEnabled(Strings.equalsIgnoreCase(of.getGameId(), of2.getGameId()));
			stadiumButton.setEnabled(true);
			leagueButton.setEnabled(true);
			bootsButton.setEnabled(true);
			clubNameButton.setEnabled(true);
			playerButton.setEnabled(true);
			allKitButton.setEnabled(true);
		}
	}

	public void disableAll() {
		log.debug("Try to disable all buttons in Import panel..");

		optionsButton.setEnabled(false);
		stadiumButton.setEnabled(false);
		leagueButton.setEnabled(false);
		bootsButton.setEnabled(false);
		clubNameButton.setEnabled(false);
		playerButton.setEnabled(false);
		allKitButton.setEnabled(false);
	}

}
