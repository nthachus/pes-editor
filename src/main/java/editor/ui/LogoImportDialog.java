package editor.ui;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.util.Strings;
import editor.util.Systems;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoImportDialog extends JDialog {
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

		Systems.javaUI();// fix button background color
		Image icon;
		for (int l = 0; l < flagButtons.length; l++) {

			icon = Logos.get(of, -1, false);
			flagButtons[l] = new JButton(new ImageIcon(icon));
			flagButtons[l].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[l].setActionCommand(Integer.toString(l));
			flagButtons[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					importFlag(evt);
				}
			});

			flagPanel.add(flagButtons[l]);
		}
		Systems.systemUI();

		fileLabel = new JLabel(Strings.getMessage("import.label2", ""));

		JButton transButton = new JButton(Strings.getMessage("Transparency"));
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateFlags(evt);
			}
		});

		JPanel topPan = new JPanel(new GridLayout(0, 1));
		topPan.add(fileLabel);
		topPan.add(transButton);

		CancelButton cancelButton = new CancelButton(this);

		getContentPane().add(topPan, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	private void updateFlags(ActionEvent evt) {
		if (null != evt)// toggle transparency
			isTrans = !isTrans;

		Image logo;
		for (int f = 0; f < Logos.TOTAL; f++) {
			logo = Logos.get(of2, f, !isTrans);
			flagButtons[f].setIcon(new ImageIcon(logo));
		}
	}

	public void refresh() {
		updateFlags(null);

		slot = 0;
		replacement = 0;

		fileLabel.setText(Strings.getMessage("import.label2", of2.getFilename()));
	}

	public void show(int slot, String title) {
		setTitle(title);
		this.slot = slot;
		setVisible(true);
	}

	private void importFlag(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		JButton btn = (JButton) evt.getSource();
		if (null == btn) throw new IllegalArgumentException("evt");

		replacement = Integer.parseInt(btn.getActionCommand());
		Logos.importData(of2, replacement, of, slot);

		setVisible(false);
	}

}
