package editor;

import editor.data.Leagues;
import editor.data.OptionFile;
import editor.data.Stadiums;
import editor.ui.LeaguePanel;
import editor.ui.StadiumPanel;
import editor.ui.WenShopPanel;
import editor.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImportPanel extends JPanel {
	private final OptionFile of;
	private final OptionFile of2;

	private final WenShopPanel wenShop;
	private final StadiumPanel stadiumPan;
	private final LeaguePanel leaguePan;
	private final TeamPanel teamPan;
	private final EmblemPanel emblemPan;
	private final LogoPanel logoPan;
	private final TransferPanel transferPan;

	private final JLabel importFile;
	private final JPanel contentPane;
	private final JButton optionsButton;
	private final JButton stadiumButton;
	private final JButton leagueButton;
	private final JButton bootsButton;
	private final JButton clubNameButton;
	private final JButton playerButton;
	private final JButton allKitButton;

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

		optionsButton = new JButton("Options / PES Points / Shop Items / Cup Gallery / Track Record / Playlist");
		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importOptions();
			}
		});

		stadiumButton = new JButton("Stadium names");// TODO: Localization!!!
		stadiumButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importStadiums();
			}
		});

		leagueButton = new JButton("League names");
		leagueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importLeagues();
			}
		});

		bootsButton = new JButton("Boots");
		bootsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importBoots();
			}
		});

		playerButton = new JButton("Players / Squads / Formations");
		playerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importPlayers();
			}
		});

		clubNameButton = new JButton("Club names");
		clubNameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importClubs();
			}
		});

		allKitButton = new JButton("Kits / Emblems / Logos / Club stadiums, flags, colours");
		allKitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importKits();
			}
		});

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

		importFile = new JLabel("To use the import options you must first use File > Open OF2...");

		add(importFile, BorderLayout.NORTH);
		add(contentPane, BorderLayout.CENTER);
	}

	private void importKits() {
		Clubs.importData(of, of2);

		for (int i = 7; i <= 8; i++) {
			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		emblemPan.refresh();
		logoPan.refresh();
		teamPan.refresh();
		transferPan.refresh();

		allKitButton.setEnabled(false);
	}

	private void importClubs() {
		Clubs.importNames(of, of2);

		teamPan.refresh();
		transferPan.refresh();

		clubNameButton.setEnabled(false);
	}

	private void importPlayers() {
		for (int i = 3; i <= 5; i++) {
			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		transferPan.refresh();

		playerButton.setEnabled(false);
	}

	private void importBoots() {
		System.arraycopy(of2.getData(), 654732, of.getData(), 654732, 828);

		bootsButton.setEnabled(false);
	}

	private void importLeagues() {
		Leagues.importData(of2, of);

		leaguePan.refresh();

		leagueButton.setEnabled(false);
	}

	private void importStadiums() {
		Stadiums.importData(of2, of);

		stadiumPan.refresh();
		teamPan.refresh();

		stadiumButton.setEnabled(false);
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
	}

	public void refresh() {
		if (!of2.isLoaded()) {
			contentPane.setVisible(false);
			importFile.setText("To use the import options you must first use File > Open OF2...");
		} else {
			importFile.setText("From:  " + of2.getFilename());
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
		optionsButton.setEnabled(false);
		stadiumButton.setEnabled(false);
		leagueButton.setEnabled(false);
		bootsButton.setEnabled(false);
		clubNameButton.setEnabled(false);
		playerButton.setEnabled(false);
		allKitButton.setEnabled(false);
	}

}
