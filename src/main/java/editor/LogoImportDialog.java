package editor;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.ui.CancelButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoImportDialog extends JDialog {
	private final OptionFile of;
	private final OptionFile of2;

	private volatile boolean trans = true;
	private volatile int slot;
	private volatile int replacement;

	private final JButton[] flagButton;// = new JButton[64];
	private final JLabel fileLabel;

	public LogoImportDialog(Frame owner, OptionFile opt, OptionFile opf2) {
		super(owner, true);// "Import Flag / Emblem"
		of = opt;
		of2 = opf2;
		fileLabel = new JLabel("From:");
		JPanel flagPanel;// = new JPanel(new GridLayout(8, 8));

		// if (logoType) {
		int max = Logos.TOTAL;
		flagPanel = new JPanel(new GridLayout(8, 10));
		/*
		 * } else { max = Flags.TOTAL; emptyFlag = of2.emptyFlag; flag =
		 * of2.flag; tranFlag = of2.tranFlag; flagPanel = new JPanel(new
		 * GridLayout(8, 8)); }
		 */
		flagButton = new JButton[max];

		for (int l = 0; l < max; l++) {
			flagButton[l] = new JButton(new ImageIcon(Logos.get(of, -1, false)));
			flagButton[l].setMargin(new Insets(0, 0, 0, 0));
			flagButton[l].setActionCommand(Integer.toString(l));
			flagButton[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent b) {
					replacement = Integer.parseInt(((JButton) b.getSource()).getActionCommand());
					importFlag();
					setVisible(false);
				}
			});
			flagPanel.add(flagButton[l]);
		}

		JButton transButton = new JButton("Transparency");
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent t) {
				trans = !trans;
				updateFlags();
			}
		});

		CancelButton cancelButton = new CancelButton(this);
		JPanel topPan = new JPanel(new GridLayout(0, 1));
		topPan.add(fileLabel);
		topPan.add(transButton);
		getContentPane().add(topPan, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

		slot = 0;
		replacement = 0;

		setResizable(false);
		pack();
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	private void updateFlags() {
		for (int f = 0; f < Logos.TOTAL; f++) {
			flagButton[f].setIcon(new ImageIcon(Logos.get(of2, f, !trans)));
		}
	}

	public void refresh() {
		updateFlags();

		slot = 0;
		replacement = 0;

		fileLabel.setText("  From:  " + of2.getFilename());
	}

	public void show(int i, String title) {
		setTitle(title);
		slot = i;
		setVisible(true);
	}

	private void importFlag() {
		Logos.importData(of2, replacement, of, slot);
	}

}
