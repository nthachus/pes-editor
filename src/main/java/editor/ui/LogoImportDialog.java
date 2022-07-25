package editor.ui;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoImportDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -8784286274094872109L;
	private static final Logger log = LoggerFactory.getLogger(LogoImportDialog.class);

	private final OptionFile of;
	private final OptionFile of2;

	private volatile boolean isTrans = true;
	private volatile int slot = 0;
	private volatile int replacement = 0;

	private final JButton[] logoButtons = new JButton[Logos.TOTAL];
	private/* final*/ JLabel fileLabel;

	public LogoImportDialog(Frame owner, OptionFile of, OptionFile of2) {
		super(owner, true);

		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == of2) {
			throw new NullArgumentException("of2");
		}
		this.of = of;
		this.of2 = of2;

		log.debug("Logo Import dialog is initializing..");
		initComponents();
	}

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(8, 10));
		Insets margin = new Insets(0, 0, 0, 0);
		Icon blankIcon = new ImageIcon(Logos.BLANK);

		UIUtil.javaUI();// fix button background color
		for (int l = 0; l < logoButtons.length; l++) {
			logoButtons[l] = new JButton(blankIcon);
			logoButtons[l].setMargin(margin);
			logoButtons[l].setActionCommand(Integer.toString(l));
			logoButtons[l].addActionListener(this);

			flagPanel.add(logoButtons[l]);
		}
		UIUtil.systemUI();

		fileLabel = new JLabel(Resources.getMessage("import.label", Strings.EMPTY));

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JPanel topPanel = new JPanel(new GridLayout(0, 1));
		topPanel.add(fileLabel);
		topPanel.add(transButton);

		JButton cancelButton = new CancelButton(this);

		getContentPane().add(topPanel, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	private void reloadLogos() {
		Image logo;
		for (int f = 0; f < logoButtons.length; f++) {
			logo = Logos.get(of2, f, !isTrans);
			logoButtons[f].setIcon(new ImageIcon(logo));
		}

		log.debug("Reload completed on {} Logos with transparency: {}", logoButtons.length, isTrans);
	}

	public void refresh() {
		log.info("Refresh logo import dialog with OF2: {}", of2.getFilename());

		reloadLogos();

		slot = 0;
		replacement = 0;

		fileLabel.setText(Resources.getMessage("import.label", of2.getFilename()));
	}

	public void show(int slot, String title) {
		log.info("Show Logo dialog to import to {}", slot);

		setTitle(title);
		this.slot = slot;

		setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform import action: {} for logo: {}", evt.getActionCommand(), slot);

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			synchronized (this) {
				isTrans = !isTrans; // toggle transparency
			}
			reloadLogos();

		} else {
			replacement = Integer.parseInt(evt.getActionCommand());
			Logos.importData(of2, replacement, of, slot);

			setVisible(false);
		}
	}

}
