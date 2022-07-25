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

public class LogoChooserDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 8328193997153067543L;
	private static final Logger log = LoggerFactory.getLogger(LogoChooserDialog.class);

	private final OptionFile of;
	private volatile boolean isTrans = true;
	private volatile int slot = -1;

	public LogoChooserDialog(Frame owner, OptionFile of) {
		super(owner, true);
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Logo chooser dialog is initializing..");
		initComponents();
	}

	private final JButton[] logoButtons = new JButton[Logos.TOTAL];
	private/* final*/ JLabel repLabel;

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

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JButton cancelButton = new CancelButton(this);

		repLabel = new JLabel(new ImageIcon(Logos.BLANK));

		JPanel centrePanel = new JPanel(new BorderLayout());
		centrePanel.add(repLabel, BorderLayout.NORTH);
		centrePanel.add(flagPanel, BorderLayout.CENTER);

		getContentPane().add(transButton, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(centrePanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform Logo chooser action: {}", evt.getActionCommand());

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			synchronized (this) {
				isTrans = !isTrans;
			}
			refresh();
		} else {
			slot = Integer.parseInt(evt.getActionCommand());
			setVisible(false);
		}
	}

	public void refresh() {
		log.info("Try to reload all Logos with transparency: {}", isTrans);

		Image logo;
		for (int f = 0; f < logoButtons.length; f++) {
			logo = Logos.get(of, f, !isTrans);
			logoButtons[f].setIcon(new ImageIcon(logo));
		}
	}

	public int getLogo(String title, Image image) {
		log.info("Try to choice '{}' for image: {}", title, Strings.valueOf(image));
		slot = -1;

		setTitle(title);
		repLabel.setIcon(new ImageIcon(image));
		refresh();
		setVisible(true);

		log.debug("Logo chooser dialog result: {}", slot);
		return slot;
	}

}
