package editor;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.ui.CancelButton;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LogoChooserDialog extends JDialog {
	private JButton[] flagButton;

	private boolean trans = true;

	private final OptionFile of;

	byte slot;

	private JLabel repLabel;

	public LogoChooserDialog(Frame owner, OptionFile opt) {
		super(owner, true);
		of = opt;
		JPanel flagPanel;
		flagPanel = new JPanel(new GridLayout(8, 10));
		flagButton = new JButton[Logos.TOTAL];

		for (int l = 0; l < Logos.TOTAL; l++) {
			flagButton[l] = new JButton(new ImageIcon(Logos.get(of, -1, false)));
			// flagButton[l].setIcon();
			flagButton[l].setMargin(new Insets(0, 0, 0, 0));
			flagButton[l].setActionCommand(Integer.toString(l));
			flagButton[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent b) {
					slot = (byte) (Integer.parseInt(((JButton) b.getSource()).getActionCommand()));
					setVisible(false);
				}
			});
			flagPanel.add(flagButton[l]);
		}

		JButton transButton = new JButton("Transparency");
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent t) {
				trans = !trans;
				refresh();
				// System.out.println(trans);
				/*
				 * for (int f = 0; f < 64; f++) { if (trans) {
				 * flagButton[f].setIcon(of.tranFlag[f]); } else {
				 * flagButton[f].setIcon(of.flag[f]); } }
				 */
			}
		});
		CancelButton cancelButton = new CancelButton(this);

		repLabel = new JLabel(new ImageIcon(Logos.get(of, -1, false)));
		JPanel centrePanel = new JPanel(new BorderLayout());
		// JLabel repText = new JLabel("
		centrePanel.add(repLabel, BorderLayout.NORTH);
		centrePanel.add(flagPanel, BorderLayout.CENTER);
		getContentPane().add(transButton, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(centrePanel, BorderLayout.CENTER);
		slot = 88;
		setResizable(false);
		pack();
	}

	public void refresh() {
		for (int f = 0; f < Logos.TOTAL; f++) {
			flagButton[f].setIcon(new ImageIcon(Logos.get(of, f, !trans)));
		}
		// slot = 99;
	}

	public byte getFlag(String title, Image image) {
		slot = 88;
		setTitle(title);
		repLabel.setIcon(new ImageIcon(image));
		refresh();
		setVisible(true);
		return slot;
	}

}
