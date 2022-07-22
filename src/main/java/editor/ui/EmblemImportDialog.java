package editor.ui;

import editor.data.EmblemType;
import editor.data.Emblems;
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

public class EmblemImportDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 3904576337391258950L;
	private static final Logger log = LoggerFactory.getLogger(EmblemImportDialog.class);

	private final OptionFile of2;
	private volatile boolean isTrans = true;
	private volatile int slot = -1;
	private volatile EmblemType type = null;

	public EmblemImportDialog(Frame owner, OptionFile of2) {
		super(owner, true);
		if (null == of2) {
			throw new NullArgumentException("of2");
		}
		this.of2 = of2;

		log.debug("Emblem import dialog is initializing..");
		initComponents();
	}

	private final JButton[] emblemButtons = new JButton[Emblems.TOTAL16];
	private/* final*/ JLabel fileLabel;

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(6, 10));
		Insets margin = new Insets(0, 0, 0, 0);
		Icon blankIcon = new ImageIcon(Emblems.BLANK_SMALL);

		UIUtil.javaUI();// fix button background color
		for (int i = 0; i < emblemButtons.length; i++) {
			emblemButtons[i] = new JButton(blankIcon);
			emblemButtons[i].setMargin(margin);
			emblemButtons[i].setActionCommand(Integer.toString(i));
			emblemButtons[i].addActionListener(this);

			flagPanel.add(emblemButtons[i]);
		}
		UIUtil.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JButton cancelButton = new CancelButton(this);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(transButton, BorderLayout.NORTH);
		contentPane.add(cancelButton, BorderLayout.SOUTH);
		contentPane.add(flagPanel, BorderLayout.CENTER);

		fileLabel = new JLabel(Resources.getMessage("import.label", Strings.EMPTY));

		getContentPane().add(fileLabel, BorderLayout.NORTH);
		getContentPane().add(contentPane, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform emblem-import action: {}", evt.getActionCommand());

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			synchronized (this) {
				isTrans = !isTrans;
			}
			refresh();

		} else {
			int slot = Integer.parseInt(evt.getActionCommand());
			if (slot >= Emblems.count16(of2)) {
				this.slot = Emblems.TOTAL16 - slot - 1;
			} else {
				this.slot = Emblems.TOTAL128 + slot;
			}

			setVisible(false);
		}
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	/**
	 * @see EmblemChooserDialog#refresh()
	 */
	public void refresh() {
		log.info("Try to refresh emblem import dialog for type: {}", type);

		Image icon;
		if (type == null || type == EmblemType.lowRes) {
			for (int i = 0, n = Emblems.count16(of2); i < n; i++) {
				icon = Emblems.get16(of2, i, !isTrans, true);
				emblemButtons[i].setIcon(new ImageIcon(icon));
				emblemButtons[i].setVisible(true);
			}
		}
		if (type == null || type == EmblemType.highRes) {
			for (int i = 0, n = Emblems.count128(of2); i < n; i++) {
				icon = Emblems.get128(of2, i, !isTrans, true);
				emblemButtons[emblemButtons.length - i - 1].setIcon(new ImageIcon(icon));
				emblemButtons[emblemButtons.length - i - 1].setVisible(true);
			}
		}

		int start = 0, end = 0;
		if (type == EmblemType.lowRes) {
			start = Emblems.count16(of2);
			end = emblemButtons.length;
		} else if (type == EmblemType.highRes) {
			//start = 0;
			end = emblemButtons.length - Emblems.count128(of2);
		}

		for (int i = start; i < end; i++) {
			emblemButtons[i].setVisible(false);
		}

		log.debug("Refresh completed on emblem importer for type: {}", type);
	}

	public int getEmblem(String title, EmblemType type) {
		log.info("Try to import '{}' for type: {}", title, type);

		this.type = type;
		slot = -1;

		setTitle(title);
		fileLabel.setText(Resources.getMessage("import.label", of2.getFilename()));
		refresh();
		setVisible(true);

		log.debug("Emblem importer for type {} result: {}", type, slot);
		return slot;
	}

	public void import128(OptionFile of, int slot, int replacement) {
		log.info("Try to import Emblem-128 from OF2: {} -> {}", replacement, slot);
		Emblems.importData128(of2, replacement, of, slot);
	}

	public void import16(OptionFile of, int slot, int replacement) {
		log.info("Try to import Emblem-16 from OF2: {} -> {}", replacement, slot);
		Emblems.importData16(of2, replacement, of, slot);
	}

}
