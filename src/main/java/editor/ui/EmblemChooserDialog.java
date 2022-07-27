package editor.ui;

import editor.data.EmblemType;
import editor.data.Emblems;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmblemChooserDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 2638397173587686242L;
	private static final Logger log = LoggerFactory.getLogger(EmblemChooserDialog.class);

	private final OptionFile of;
	private volatile boolean isTrans = true;
	private volatile int slot = -1;
	private volatile EmblemType type = null;

	private final JButton[] emblemButtons = new JButton[Emblems.TOTAL16];

	public EmblemChooserDialog(Frame owner, OptionFile of) {
		super(owner, true);
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Emblem chooser dialog is initializing..");
		initComponents();
	}

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(6, 10));

		int iconSize = Math.round(0.69f * Emblems.IMG_SIZE);
		Dimension prefSize = new Dimension(iconSize, iconSize);
		Insets margin = new Insets(0, 0, 0, 0);
		Icon blankIcon = new ImageIcon(Emblems.BLANK16);

		UIUtil.javaUI();// fix button background color
		for (int i = 0; i < emblemButtons.length; i++) {
			emblemButtons[i] = new JButton(blankIcon);
			emblemButtons[i].setMargin(margin);
			emblemButtons[i].setPreferredSize(prefSize);
			emblemButtons[i].setActionCommand(Integer.toString(i));
			emblemButtons[i].addActionListener(this);

			flagPanel.add(emblemButtons[i]);
		}
		UIUtil.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JButton cancelButton = new CancelButton(this);
		getContentPane().add(transButton, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform emblem-chooser action: {}", evt.getActionCommand());

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			synchronized (this) {
				isTrans = !isTrans;
			}
			refresh();

		} else {
			int n = Integer.parseInt(evt.getActionCommand());
			if (n >= Emblems.count16(of)) {
				slot = Emblems.TOTAL16 - n - 1;
			} else {
				slot = Emblems.TOTAL128 + n;
			}

			setVisible(false);
		}
	}

	public void refresh() {
		log.info("Try to refresh emblem chooser dialog for type: {}", type);

		Image icon;
		if (type == null || type == EmblemType.lowRes) {
			for (int i = 0, n = Emblems.count16(of); i < n; i++) {
				icon = Emblems.get16(of, i, !isTrans, true);
				emblemButtons[i].setIcon(new ImageIcon(icon));
				emblemButtons[i].setVisible(true);
			}
		}
		if (type == null || type == EmblemType.highRes) {
			for (int i = 0, n = Emblems.count128(of); i < n; i++) {
				icon = Emblems.get128(of, i, !isTrans, true);
				emblemButtons[emblemButtons.length - i - 1].setIcon(new ImageIcon(icon));
				emblemButtons[emblemButtons.length - i - 1].setVisible(true);
			}
		}

		int start = 0, end = 0;
		if (type == EmblemType.lowRes) {
			start = Emblems.count16(of);
			end = emblemButtons.length;
		} else if (type == EmblemType.highRes) {
			//start = 0;
			end = emblemButtons.length - Emblems.count128(of);
		}

		for (int i = start; i < end; i++) {
			emblemButtons[i].setVisible(false);
		}

		log.debug("Refresh completed on emblem chooser for type: {}", type);
	}

	public int getEmblem(String title, EmblemType type) {
		log.info("Try to choice '{}' for type: {}", title, type);

		this.type = type;
		slot = -1;

		setTitle(title);
		refresh();
		setVisible(true);

		log.debug("Emblem chooser for type {} result: {}", type, slot);
		return slot;
	}

}
