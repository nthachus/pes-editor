package editor.ui;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.util.Resources;
import editor.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoImportDialog extends JDialog implements ActionListener {
	private final OptionFile of;
	private final OptionFile of2;

	private volatile boolean isTrans = true;
	private volatile int slot = 0;
	private volatile int replacement = 0;

	private final JButton[] flagButtons = new JButton[Logos.TOTAL];
	private/* final*/ JLabel fileLabel;

	public LogoImportDialog(Frame owner, OptionFile of, OptionFile of2) {
		super(owner, true);

		if (null == of) throw new NullPointerException("of");
		if (null == of2) throw new NullPointerException("of2");
		this.of = of;
		this.of2 = of2;

		initComponents();
	}

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(8, 10));

		UIUtil.javaUI();// fix button background color
		for (int l = 0; l < flagButtons.length; l++) {
			flagButtons[l] = new JButton(new ImageIcon(Logos.BLANK));
			flagButtons[l].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[l].setActionCommand(Integer.toString(l));
			flagButtons[l].addActionListener(this);

			flagPanel.add(flagButtons[l]);
		}
		UIUtil.systemUI();

		fileLabel = new JLabel(Resources.getMessage("import.label", ""));

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JPanel topPanel = new JPanel(new GridLayout(0, 1));
		topPanel.add(fileLabel);
		topPanel.add(transButton);

		CancelButton cancelButton = new CancelButton(this);

		getContentPane().add(topPanel, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	private void updateFlags() {
		Image logo;
		for (int f = 0; f < Logos.TOTAL; f++) {
			logo = Logos.get(of2, f, !isTrans);
			flagButtons[f].setIcon(new ImageIcon(logo));
		}
	}

	public void refresh() {
		updateFlags();

		slot = 0;
		replacement = 0;

		fileLabel.setText(Resources.getMessage("import.label", of2.getFilename()));
	}

	public void show(int slot, String title) {
		setTitle(title);
		this.slot = slot;
		setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			isTrans = !isTrans; // toggle transparency
			updateFlags();

		} else {
			replacement = Integer.parseInt(evt.getActionCommand());
			Logos.importData(of2, replacement, of, slot);

			setVisible(false);
		}
	}

}
