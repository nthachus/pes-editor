package editor.ui;

import editor.EmblemPanel;
import editor.GlobalPanel;
import editor.TeamPanel;
import editor.TransferPanel;
import editor.data.CsvMaker;
import editor.data.OfFormat;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;

public final class Editor extends JFrame {
	private static final Logger log = LoggerFactory.getLogger(Editor.class);

	private final OptionFile of;
	private final OptionFile of2;

	public static final float LINE_HEIGHT = 4f / 3;

	//region Initialize Components

	private final JFileChooser opFileChooser;
	private final OptionFileFilter opFileFilter;

	private final EmblemPanel flagPanel;
	private final LogoPanel imagePanel;
	private final TransferPanel transferPan;
	private final WenShopPanel wenShop;
	private final StadiumPanel stadiumPan;
	private final TeamPanel teamPan;
	private final LeaguePanel leaguePan;
	private final JTabbedPane tabbedPane;
	private final PlayerImportDialog playerImportDia;
	private final LogoImportDialog logoImportDia;
	private final ImportPanel importPanel;
	private final HelpDialog helpDia;

	private final CsvMaker csvMaker = new CsvMaker();
	private final CsvSwitchPanel csvSwitch;
	private final JFileChooser csvChooser;

	public Editor() {
		super(Resources.getMessage("editor.title"));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		initIcon();

		of = new OptionFile();
		of2 = new OptionFile();

		csvSwitch = new CsvSwitchPanel();

		csvChooser = new JFileChooser();
		csvChooser.addChoosableFileFilter(new CsvFilter());
		csvChooser.setAcceptAllFileFilterUsed(false);
		csvChooser.setAccessory(csvSwitch);

		playerImportDia = new PlayerImportDialog(this, of, of2);
		PlayerDialog playerDia = new PlayerDialog(this, of, playerImportDia);
		FormationDialog teamDia = new FormationDialog(this, of);
		//
		transferPan = new TransferPanel(of, playerDia, teamDia);

		logoImportDia = new LogoImportDialog(this, of, of2);
		imagePanel = new LogoPanel(of, logoImportDia);

		EmblemChooserDialog emblemChooser = new EmblemChooserDialog(this, of);
		GlobalPanel globalPan = new GlobalPanel(of, transferPan);
		KitImportDialog kitImportDia = new KitImportDialog(this, of2);
		LogoChooserDialog logoChooser = new LogoChooserDialog(this, of);
		teamPan = new TeamPanel(
				of, transferPan, emblemChooser, of2, imagePanel, globalPan, kitImportDia, logoChooser);

		EmblemImportDialog emblemImportDia = new EmblemImportDialog(this, of2);
		flagPanel = new EmblemPanel(of, emblemImportDia, teamPan);
		teamPan.setEmblemPan(flagPanel);

		wenShop = new WenShopPanel(of);
		stadiumPan = new StadiumPanel(of, teamPan);
		leaguePan = new LeaguePanel(of);
		importPanel = new ImportPanel(
				of, of2, wenShop, stadiumPan, leaguePan, teamPan, flagPanel, imagePanel, transferPan);

		helpDia = new HelpDialog(this);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Resources.getMessage("editor.tab1"), null, transferPan, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab2"), null, teamPan, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab3"), null, flagPanel, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab4"), null, imagePanel, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab5"), null, stadiumPan, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab6"), null, leaguePan, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab7"), null, wenShop, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab8"), null, globalPan, null);
		tabbedPane.addTab(Resources.getMessage("editor.tab9"), null, importPanel, null);

		opFileFilter = new OptionFileFilter();
		//
		opFileChooser = new JFileChooser(loadSettings());
		opFileChooser.setAcceptAllFileFilterUsed(false);
		opFileChooser.addChoosableFileFilter(opFileFilter);
		opFileChooser.setAccessory(new OptionPreviewPanel(opFileChooser));

		buildMenu();
		getContentPane().add(tabbedPane);

		setResizable(false);
		pack();

		tabbedPane.setVisible(false);
	}

	private void initIcon() {
		URL imageUrl = getClass().getResource("/META-INF/images/icon.png");
		if (imageUrl != null) {
			ImageIcon icon = new ImageIcon(imageUrl);
			setIconImage(icon.getImage());
		}
	}

	//endregion

	//region Initialize Menu

	private JMenuItem open2Item;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private JMenuItem csvItem;
	private JMenuItem convertItem;

	private void buildMenu() {
		JMenuItem openItem = new JMenuItem(Resources.getMessage("menu.open"));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openFile();
			}
		});

		open2Item = new JMenuItem(Resources.getMessage("menu.open2"));
		open2Item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openOF2();
			}
		});

		saveItem = new JMenuItem(Resources.getMessage("menu.save"));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveFile();
			}
		});

		saveAsItem = new JMenuItem(Resources.getMessage("menu.saveAs"));
		saveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveFileAs();
			}
		});

		JMenuItem exitItem = new JMenuItem(Resources.getMessage("menu.exit"));
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		JMenu file = new JMenu(Resources.getMessage("menu.file"));
		file.add(openItem);
		file.add(open2Item);
		file.add(saveItem);
		file.add(saveAsItem);
		file.add(exitItem);

		csvItem = new JMenuItem(Resources.getMessage("menu.makeCsv"));
		csvItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exportCsv();
			}
		});

		convertItem = new JMenuItem(Resources.getMessage("menu.convert"));
		convertItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importFromOF2();
			}
		});

		JMenu tool = new JMenu(Resources.getMessage("menu.tool"));
		tool.add(csvItem);
		tool.add(convertItem);

		JMenuItem helpItem = new JMenuItem(Resources.getMessage("menu.helpPage"));
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				helpDia.setVisible(true);
			}
		});

		JMenuItem aboutItem = new JMenuItem(Resources.getMessage("menu.about"));
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				about();
			}
		});

		JMenu help = new JMenu(Resources.getMessage("menu.help"));
		help.add(helpItem);
		help.add(aboutItem);

		JMenuBar mb = new JMenuBar();
		mb.add(file);
		mb.add(tool);
		mb.add(help);

		setJMenuBar(mb);

		csvItem.setEnabled(false);
		open2Item.setEnabled(false);
		saveItem.setEnabled(false);
		saveAsItem.setEnabled(false);
		convertItem.setEnabled(false);
	}

	//endregion

	private void refreshTitle(String filename) {
		String s = Resources.getMessage("editor.title");
		if (!Strings.isEmpty(filename))
			s = Resources.getMessage("title.format", s, filename);

		setTitle(s);
	}

	//region Events Handler

	private volatile File currentFile = null;

	private void openFile() {
		int returnVal = opFileChooser.showOpenDialog(getContentPane());
		saveSettings();
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		File fs = opFileChooser.getSelectedFile();
		if (!opFileFilter.accept(fs))
			return;

		if (fs.isFile() && of.load(fs)) {
			currentFile = fs;

			Squads.fixAll(of);

			flagPanel.refresh();
			imagePanel.refresh();
			transferPan.refresh();
			wenShop.getWenPanel().refresh();
			wenShop.getShopPanel().refresh();
			stadiumPan.refresh();
			teamPan.refresh();
			leaguePan.refresh();

			importPanel.refresh();
			csvItem.setEnabled(true);
			open2Item.setEnabled(true);
			saveItem.setEnabled(true);
			saveAsItem.setEnabled(true);
			convertItem.setEnabled(of2.isLoaded());

			refreshTitle(currentFile.getName());
			tabbedPane.setVisible(true);
		} else {
			refreshTitle(null);
			tabbedPane.setVisible(false);

			csvItem.setEnabled(false);
			open2Item.setEnabled(false);
			saveItem.setEnabled(false);
			saveAsItem.setEnabled(false);
			convertItem.setEnabled(false);

			showOpenFailMsg();
		}
	}

	/**
	 * Imports OptionFile from OF2.
	 */
	private void importFromOF2() {
		if (null == currentFile)
			return;

		for (int i = 2; i <= 8; i++) {
			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		flagPanel.refresh();
		imagePanel.refresh();
		transferPan.refresh();
		stadiumPan.refresh();
		teamPan.refresh();
		leaguePan.refresh();
		importPanel.disableAll();
		convertItem.setEnabled(false);
	}

	private void exportCsv() {
		if (null == currentFile)
			return;

		int returnVal = csvChooser.showSaveDialog(getContentPane());
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		File dest = csvChooser.getSelectedFile();
		if (!Files.isFilenameLegal(dest.getName())) {
			showIllegalNameMsg();
			return;
		}

		boolean head = csvSwitch.getHead().isSelected();
		boolean extra = csvSwitch.getExtra().isSelected();
		boolean create = csvSwitch.getCreate().isSelected();

		dest = Files.addExtension(dest, Files.CSV);
		if (dest.exists()) {
			returnVal = showOverwriteConfirm(dest);
			if (returnVal != JOptionPane.YES_OPTION) {
				return;
			} else if (!dest.delete()) {
				showSaveFailMsg();
				return;
			}
		}

		if (csvMaker.makeFile(of, dest, head, extra, create)) {
			showSaveOkMsg(dest);
		} else {
			showSaveFailMsg();
		}
	}

	private void saveFileAs() {
		if (null == currentFile)
			return;

		int returnVal = opFileChooser.showSaveDialog(getContentPane());
		saveSettings();
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		File dest = opFileChooser.getSelectedFile();
		if (!Files.isFilenameLegal(dest.getName())) {
			showIllegalNameMsg();
			return;
		}

		if (of.getFormat() == OfFormat.xPort) {
			dest = Files.addExtension(dest, Files.XPS);
		} else if (of.getFormat() == OfFormat.ems) {
			dest = Files.addExtension(dest, Files.PSU);
		} else if (of.getFormat() == OfFormat.arMax) {
			dest = Files.addExtension(dest, Files.MAX);
		}

		if (dest.exists()) {
			returnVal = showOverwriteConfirm(dest);
			if (returnVal != JOptionPane.YES_OPTION) {
				return;
			} else if (!dest.delete()) {
				showSaveFailMsg();
				return;
			}
		}

		if (of.save(dest)) {
			currentFile = dest;
			refreshTitle(currentFile.getName());
			showSaveOkMsg(dest);
			opFileChooser.setSelectedFile(null);
		} else {
			showSaveFailMsg();
		}
	}

	private void saveFile() {
		if (null == currentFile)
			return;

		if (currentFile.delete() && of.save(currentFile)) {
			showSaveOkMsg(currentFile);
			opFileChooser.setSelectedFile(null);
		} else {
			showSaveFailMsg();
		}
	}

	private void openOF2() {
		int returnVal = opFileChooser.showOpenDialog(getContentPane());
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		File fs2 = opFileChooser.getSelectedFile();
		if (!opFileFilter.accept(fs2))
			return;

		if (fs2.isFile() && of2.load(fs2)) {
			Squads.fixAll(of2);

			playerImportDia.refresh();
			logoImportDia.refresh();
			importPanel.refresh();
			flagPanel.refresh();
			teamPan.getList().setToolTipText(Resources.getMessage("teamPane.tooltip"));
			convertItem.setEnabled(of.isLoaded());

		} else {
			teamPan.getList().setToolTipText(null);
			flagPanel.refresh();
			importPanel.refresh();
			convertItem.setEnabled(false);

			showOpenFailMsg();
		}
	}

	//endregion

	private int showOverwriteConfirm(File dest) {
		return JOptionPane.showConfirmDialog(getContentPane(),
				Resources.getMessage("msg.overwrite", dest.getName(), dest.getParent()),
				Resources.getMessage("msg.overwrite.title"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
	}

	private void showSaveFailMsg() {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("msg.saveFailed"), Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	private void showOpenFailMsg() {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("msg.openFailed"), Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	private void showSaveOkMsg(File dest) {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("msg.saveSuccess", dest.getName(), dest.getParent()),
				Resources.getMessage("msg.saveSuccess.title"), JOptionPane.INFORMATION_MESSAGE);
	}

	private void showIllegalNameMsg() {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("msg.illegalName"), Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	//region Application Settings

	private final File settingsFile = new File("pes-editor.settings");

	private boolean saveSettings() {
		File dir = opFileChooser.getCurrentDirectory();
		if (null == dir)
			return true;

		FileOutputStream out = null;
		ObjectOutputStream sw = null;
		try {
			out = new FileOutputStream(settingsFile, false);
			sw = new ObjectOutputStream(out);

			sw.writeObject(dir);
		} catch (IOException e) {
			log.error("Failed to save settings:", e);
			return false;
		} finally {
			if (null != sw) {
				try {
					sw.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}
		return true;
	}

	private File loadSettings() {
		if (!settingsFile.exists()) {
			about();
			return null;
		}

		File dir = null;
		FileInputStream in = null;
		ObjectInputStream sr = null;
		try {
			in = new FileInputStream(settingsFile);
			sr = new ObjectInputStream(in);

			dir = (File) sr.readObject();
			if (null != dir && !dir.exists()) {
				dir = null;
			}
		} catch (Exception e) {
			log.error("Failed to load settings:", e);
		} finally {
			if (null != sr) {
				try {
					sr.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}
		return dir;
	}

	//endregion

	private void about() {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("about.content"), Resources.getMessage("about.title"),
				JOptionPane.PLAIN_MESSAGE, new ImageIcon(getIconImage()));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				log.info("Main form is initializing...");
				try {
					Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
						public void uncaughtException(Thread thread, Throwable throwable) {
							log.error("Unhandled exception occurred:", throwable);
						}
					});
					Systems.systemUI();

					Editor form = new Editor();
					form.setVisible(true);
					// DEBUG
					log.info("Main form has been initialized.");
					form.openFile();

				} catch (Exception e) {
					log.error("Failed to initialize Editor form:", e);
					System.exit(-1);
				}
			}
		});
	}

}
