package editor;

import editor.data.OptionFile;
import editor.ui.CancelButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmblemImportDialog extends JDialog {
	private JButton[] flagButton;

	private boolean trans = true;

	OptionFile of;

	int slot;

	byte max;

	int type;

	private volatile boolean of2Open = false;

	JLabel fileLabel;

	public EmblemImportDialog(Frame owner, OptionFile opt) {
		super(owner, true);
		of = opt;
		JPanel flagPanel;
		max = Emblems.TOTAL16;
		flagPanel = new JPanel(new GridLayout(6, 10));
		flagButton = new JButton[max];
		fileLabel = new JLabel("From:");

		for (int l = 0; l < max; l++) {
			flagButton[l] = new JButton(new ImageIcon(Emblems.get16(of, -1,
					false, true)));
			// flagButton[l].setIcon();
			flagButton[l].setMargin(new Insets(0, 0, 0, 0));
			flagButton[l].setActionCommand((new Integer(l)).toString());
			flagButton[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent b) {
					slot = (new Integer(((JButton) b.getSource())
							.getActionCommand())).intValue();
					if (slot >= Emblems.count16(of)) {
						slot = Emblems.TOTAL16 - 1 - slot;
					} else {
						slot = slot + Emblems.TOTAL128;
					}
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
		JPanel pan1 = new JPanel(new BorderLayout());
		pan1.add(transButton, BorderLayout.NORTH);
		pan1.add(cancelButton, BorderLayout.SOUTH);
		pan1.add(flagPanel, BorderLayout.CENTER);
		getContentPane().add(fileLabel, BorderLayout.NORTH);
		getContentPane().add(pan1, BorderLayout.CENTER);
		slot = -1;
		setResizable(false);
		pack();
	}

	public boolean isOf2Open() {
		return of2Open;
	}

	public void setOf2Open(boolean of2Open) {
		this.of2Open = of2Open;
	}

	public void refresh() {
		if (type == 0 || type == 1) {
			for (int i = 0; i < Emblems.count16(of); i++) {
				flagButton[i].setIcon(new ImageIcon(Emblems.get16(of, i,
						!trans, true)));
				flagButton[i].setVisible(true);
			}
		}
		if (type == 0 || type == 2) {
			for (int i = 0; i < Emblems.count128(of); i++) {
				flagButton[Emblems.TOTAL16 - 1 - i].setIcon(new ImageIcon(
						Emblems.get128(of, i, !trans, true)));
				flagButton[Emblems.TOTAL16 - 1 - i].setVisible(true);
			}
		}

		int s = Emblems.count16(of);
		int e = Emblems.TOTAL16 - Emblems.count128(of);
		if (type == 1) {
			e = Emblems.TOTAL16;
		}
		if (type == 2) {
			s = 0;
		}
		for (int i = s; i < e; i++) {
			flagButton[i].setVisible(false);
		}
	}

	public int getFlag(String title, int t) {
		type = t;
		slot = -1;
		setTitle(title);
		fileLabel.setText("  From:  " + of.getFilename());
		refresh();
		setVisible(true);
		return slot;
	}

}
