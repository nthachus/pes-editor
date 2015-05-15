package editor.ui;

import editor.data.CsvMaker;
import editor.data.OfFormat;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.Locale;

public class Editor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 212475114885790986L;
	private static final Logger log = LoggerFactory.getLogger(Editor.class);

	private final OptionFile of;
	private final OptionFile of2;
	private final CsvMaker csvMaker;

	public Editor() {
		super();

		of = new OptionFile();
		of2 = new OptionFile();
		csvMaker = new CsvMaker();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		initIcon();

		File lastDir = loadSettings();
		setTitle(Resources.getMessage("editor.title"));

		initComponents(lastDir);
	}

	//region Initialize the GUI components

	private/* final*/ JFileChooser opFileChooser;
	private/* final*/ transient javax.swing.filechooser.FileFilter opFileFilter;

	private/* final*/ EmblemPanel flagPanel;
	private/* final*/ LogoPanel imagePanel;
	private/* final*/ TransferPanel transferPan;
	private/* final*/ WenShopPanel wenShop;
	private/* final*/ StadiumPanel stadiumPan;
	private/* final*/ TeamPanel teamPan;
	private/* final*/ LeaguePanel leaguePan;
	private/* final*/ JTabbedPane tabbedPane;
	private/* final*/ PlayerImportDialog playerImportDia;
	private/* final*/ LogoImportDialog logoImportDia;
	private/* final*/ ImportPanel importPanel;
	private/* final*/ HelpDialog helpDia;

	private/* final*/ CsvSwitchPanel csvSwitch;
	private/* final*/ JFileChooser csvChooser;

	private void initComponents(File lastDir) {
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
				of, of2, transferPan, emblemChooser, imagePanel, globalPan, kitImportDia, logoChooser);

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
		opFileChooser = new JFileChooser(lastDir);
		opFileChooser.setAcceptAllFileFilterUsed(false);
		opFileChooser.addChoosableFileFilter(opFileFilter);
		opFileChooser.setAccessory(new OptionPreviewPanel(opFileChooser));

		buildMenu();

		setResizable(true);
		getContentPane().add(tabbedPane);

		setResizable(false);
		pack();

		tabbedPane.setVisible(false);
	}

	private JMenuItem open2Item;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private JMenuItem csvItem;
	private JMenuItem convertItem;
	private JMenuItem relinkItem;

	private void buildMenu() {
		log.debug("Building menu-bar is starting..");

		JMenuItem openItem = new JMenuItem(Resources.getMessage("menu.open"));
		openItem.setActionCommand("Open");
		openItem.addActionListener(this);

		open2Item = new JMenuItem(Resources.getMessage("menu.open2"));
		open2Item.setActionCommand("Open2");
		open2Item.addActionListener(this);

		saveItem = new JMenuItem(Resources.getMessage("menu.save"));
		saveItem.setActionCommand("Save");
		saveItem.addActionListener(this);

		saveAsItem = new JMenuItem(Resources.getMessage("menu.saveAs"));
		saveAsItem.setActionCommand("SaveAs");
		saveAsItem.addActionListener(this);

		JMenuItem exitItem = new JMenuItem(Resources.getMessage("menu.exit"));
		exitItem.setActionCommand("Exit");
		exitItem.addActionListener(this);

		JMenu file = new JMenu(Resources.getMessage("menu.file"));
		file.add(openItem);
		file.add(open2Item);
		file.add(saveItem);
		file.add(saveAsItem);
		file.add(exitItem);

		csvItem = new JMenuItem(Resources.getMessage("menu.makeCsv"));
		csvItem.setActionCommand("MakeCsv");
		csvItem.addActionListener(this);

		convertItem = new JMenuItem(Resources.getMessage("menu.convert"));
		convertItem.setActionCommand("Convert");
		convertItem.addActionListener(this);

		relinkItem = new JMenuItem(Resources.getMessage("menu.relink"));
		relinkItem.setActionCommand("Relink");
		relinkItem.addActionListener(this);

		JMenu tool = new JMenu(Resources.getMessage("menu.tool"));
		tool.add(csvItem);
		tool.add(convertItem);
		tool.add(relinkItem);

		JMenu language = new JMenu(Resources.getMessage("menu.language"));
		buildLanguageMenu(language);

		JMenuItem helpItem = new JMenuItem(Resources.getMessage("menu.helpPage"));
		helpItem.setActionCommand("Help");
		helpItem.addActionListener(this);

		JMenuItem aboutItem = new JMenuItem(Resources.getMessage("menu.about"));
		aboutItem.setActionCommand("About");
		aboutItem.addActionListener(this);

		JMenu help = new JMenu(Resources.getMessage("menu.help"));
		help.add(helpItem);
		help.add(aboutItem);

		JMenuBar mb = new JMenuBar();
		mb.add(file);
		mb.add(tool);
		mb.add(language);
		mb.add(help);

		setJMenuBar(mb);

		enableMenuItems(false);
		log.debug("Building of menu-bar is succeeded");
	}

	private void enableMenuItems(boolean enable) {
		csvItem.setEnabled(enable);
		open2Item.setEnabled(enable);
		saveItem.setEnabled(enable);
		saveAsItem.setEnabled(enable);
		convertItem.setEnabled(enable && of2.isLoaded());
		relinkItem.setEnabled(enable);
	}

	private void initIcon() {
		URL iconUrl = Editor.class.getResource("/META-INF/images/icon.png");
		if (iconUrl != null) {
			ImageIcon icon = new ImageIcon(iconUrl);
			setIconImage(icon.getImage());
		}
	}

	private void buildLanguageMenu(JMenu language) {
		Locale loc, current = Locale.getDefault();
		JMenuItem mi;
		for (int i = 0; i < Resources.SUPPORTED_LOCALES.length; i++) {
			loc = Resources.SUPPORTED_LOCALES[i];

			mi = new JMenuItem(loc.getDisplayLanguage(loc));
			mi.setActionCommand("Language" + i);
			mi.addActionListener(this);
			mi.setEnabled(!loc.equals(current));

			language.add(mi);
		}
	}

	//endregion

	private void refreshTitle(String filename) {
		String s = Resources.getMessage("editor.title");
		if (!Strings.isEmpty(filename)) {
			s = Resources.getMessage("title.format", s, filename);
		}

		setTitle(s);
	}

	//region Event Handlers

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.debug("Try to perform action: {}", evt.getActionCommand());

		if ("Open".equalsIgnoreCase(evt.getActionCommand())) {
			openFile(null);
		} else if ("Open2".equalsIgnoreCase(evt.getActionCommand())) {
			openOF2();
		} else if ("Save".equalsIgnoreCase(evt.getActionCommand())) {
			saveFile();
		} else if ("SaveAs".equalsIgnoreCase(evt.getActionCommand())) {
			saveFileAs();
		} else if ("MakeCsv".equalsIgnoreCase(evt.getActionCommand())) {
			exportCsv();
		} else if ("Convert".equalsIgnoreCase(evt.getActionCommand())) {
			importFromOF2();
		} else if ("Relink".equalsIgnoreCase(evt.getActionCommand())) {
			exportRelink();
		} else if (null != evt.getActionCommand() && evt.getActionCommand().startsWith("Language")) {
			int i = Integer.parseInt(evt.getActionCommand().substring(8));
			switchLanguage(Resources.SUPPORTED_LOCALES[i]);

		} else if ("Help".equalsIgnoreCase(evt.getActionCommand())) {
			helpDia.setVisible(true);
		} else if ("About".equalsIgnoreCase(evt.getActionCommand())) {
			about();
		} else/* if ("Exit".equalsIgnoreCase(evt.getActionCommand()))*/ {
			processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	private volatile File currentFile = null;

	private void openFile(String filePath) {
		File fs;
		if (!Strings.isBlank(filePath)) {
			fs = new File(filePath);
		} else {
			int returnVal = opFileChooser.showOpenDialog(getContentPane());
			saveSettings();
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			fs = opFileChooser.getSelectedFile();
		}

		if (!opFileFilter.accept(fs)) {
			return;
		}
		log.debug("Try to open file: {}", fs);

		if (fs.isFile() && of.load(fs)) {
			currentFile = fs;
			refreshTitle(currentFile.getName());

			Squads.fixAll(of);

			refreshComponents();
			enableMenuItems(true);

			log.debug("Open succeeded on input file: {}", fs.getName());
		} else {
			refreshTitle(null);
			tabbedPane.setVisible(false);

			enableMenuItems(false);

			log.debug("Failed to open file: {}", fs);
			showOpenFailMsg();
		}
	}

	private void refreshComponents() {
		flagPanel.refresh();
		imagePanel.refresh();
		transferPan.refresh();
		wenShop.getWenPanel().refresh();
		wenShop.getShopPanel().refresh();
		stadiumPan.refresh();
		teamPan.refresh();
		leaguePan.refresh();
		importPanel.refresh();

		tabbedPane.setVisible(true);
	}

	private void switchLanguage(Locale locale) {
		if (locale.equals(Locale.getDefault())) {
			return;
		}
		Locale.setDefault(locale);
		saveSettings();

		// backup the index of the currently selected tab
		int idx = tabbedPane.getSelectedIndex();

		getContentPane().remove(tabbedPane);
		initComponents(opFileChooser.getCurrentDirectory());

		if (null != currentFile) {
			tabbedPane.setSelectedIndex(idx);

			refreshTitle(currentFile.getName());
			refreshComponents();
			enableMenuItems(true);
		} else {
			refreshTitle(null);
		}
	}

	/**
	 * Imports OptionFile from OF2.
	 */
	private void importFromOF2() {
		if (null == currentFile) {
			return;
		}
		log.debug("Try to import all data from OF2: {}", of2.getFilename());

		for (int i = 2; i <= 8; i++) {
			int adr = OptionFile.blockAddress(i);
			System.arraycopy(of2.getData(), adr, of.getData(), adr, OptionFile.blockSize(i));
		}

		importPanel.disableAll();
		convertItem.setEnabled(false);

		flagPanel.refresh();
		imagePanel.refresh();
		stadiumPan.refresh();
		teamPan.refresh();
		leaguePan.refresh();
		transferPan.refresh();

		log.debug("Importing from OF2 {} succeeded", of2.getFilename());
	}

	private void exportCsv() {
		if (null == currentFile) {
			return;
		}

		int returnVal = csvChooser.showSaveDialog(getContentPane());
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File dest = csvChooser.getSelectedFile();
		if (!Files.isFilenameLegal(dest.getName())) {
			showIllegalNameMsg();
			return;
		}
		log.debug("Try to export to CSV file: {}", dest);

		boolean head = csvSwitch.isHeadChecked();
		//boolean extra = csvSwitch.isExtraChecked();
		boolean create = csvSwitch.isEditChecked();

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

		if (csvMaker.makeFile(of, dest, head/*, extra*/, create)) {
			showSaveOkMsg(dest);
		} else {
			showSaveFailMsg();
		}
	}

	private void exportRelink() {
		if (null == currentFile) {
			return;
		}

		String dest = currentFile.getParent();
		if (of.exportRelink(dest)) {
			showSaveOkMsg("Relink files", dest);
		} else {
			showSaveFailMsg();
		}
	}

	private void saveFileAs() {
		if (null == currentFile) {
			return;
		}

		int returnVal = opFileChooser.showSaveDialog(getContentPane());
		saveSettings();
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File dest = opFileChooser.getSelectedFile();
		if (!Files.isFilenameLegal(dest.getName())) {
			showIllegalNameMsg();
			return;
		}
		log.debug("Try to save OF file as: {}", dest);

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
		if (null == currentFile) {
			return;
		}

		if (currentFile.delete() && of.save(currentFile)) {
			showSaveOkMsg(currentFile);
			opFileChooser.setSelectedFile(null);
		} else {
			showSaveFailMsg();
		}
	}

	private void openOF2() {
		int returnVal = opFileChooser.showOpenDialog(getContentPane());
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File fs2 = opFileChooser.getSelectedFile();
		if (!opFileFilter.accept(fs2)) {
			return;
		}
		log.debug("Try to open OF2 file: {}", fs2);

		if (fs2.isFile() && of2.load(fs2)) {
			Squads.fixAll(of2);

			playerImportDia.refresh();
			logoImportDia.refresh();
			importPanel.refresh();
			flagPanel.refresh();
			teamPan.getList().setToolTipText(Resources.getMessage("teamPane.tooltip"));
			convertItem.setEnabled(of.isLoaded());

			log.debug("Open succeeded on OF2 file: {}", fs2);
		} else {
			teamPan.getList().setToolTipText(null);
			convertItem.setEnabled(false);
			flagPanel.refresh();
			importPanel.refresh();

			log.debug("Failed to open OF2 file: {}", fs2);
			showOpenFailMsg();
		}
	}

	//endregion

	private int showOverwriteConfirm(File dest) {
		return JOptionPane.showConfirmDialog(getContentPane(),
				Resources.getMessage("msg.overwrite", dest.getName(), dest.getParent()),
				Resources.getMessage("msg.overwrite.title", dest.getName()),
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
		showSaveOkMsg(dest.getName(), dest.getParent());
	}

	private void showSaveOkMsg(String filename, String path) {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("msg.saveSuccess", filename, path),
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
		/*if (null == dir) {
			return true;
		}*/

		ObjectOutputStream sw = null;
		try {
			sw = new ObjectOutputStream(new FileOutputStream(settingsFile, false));
			sw.writeObject(new Object[]{dir, Locale.getDefault()});

		} catch (IOException e) {
			log.warn("Failed to save settings: {}", e.toString());
			return false;
		} finally {
			Files.closeStream(sw);
		}
		return true;
	}

	private File loadSettings() {
		if (!settingsFile.exists()) {
			return null;
		}

		File dir = null;
		ObjectInputStream sr = null;
		try {
			sr = new ObjectInputStream(new FileInputStream(settingsFile));
			Object o = sr.readObject();

			if (o instanceof Object[]) {
				Object[] settings = (Object[]) o;
				dir = (File) settings[0];
				if (null != dir && !dir.exists()) {
					dir = null;
				}
				Locale.setDefault((Locale) settings[1]);
			}
		} catch (Exception e) {
			log.warn("Failed to load settings: {}", e.toString());
		} finally {
			Files.closeStream(sr);
		}
		return dir;
	}

	//endregion

	private void about() {
		JOptionPane.showMessageDialog(getContentPane(),
				Resources.getMessage("about.content"), Resources.getMessage("about.title"),
				JOptionPane.PLAIN_MESSAGE, new ImageIcon(getIconImage()));
	}

	public static class Runner implements Runnable {
		private final String filePath;

		public Runner(String[] args) {
			filePath = (null != args && args.length > 0) ? args[0] : null;
		}

		public void run() {
			log.info("Main form is initializing...");
			Editor form;
			try {
				UIUtil.systemUI();

				form = new Editor();
				if (!form.settingsFile.exists() && !log.isTraceEnabled()) {
					form.about();
				}
				form.setVisible(true);
				// DEBUG
				log.info("Main form has been initialized.");

			} catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
			form.openFile(filePath);
		}
	}

}
