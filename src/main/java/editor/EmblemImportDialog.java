package editor;

import editor.data.OptionFile;
import editor.ui.CancelButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmblemImportDialog extends JDialog {
	private final OptionFile of2;

	private volatile boolean trans = true;
	private volatile int slot;
	private volatile byte max;
	private volatile int type;

	private final JButton[] emblemButtons;
	private final JLabel fileLabel;

	public EmblemImportDialog(Frame owner, OptionFile of2) {
		super(owner, true);
		this.of2 = of2;

		JPanel flagPanel;
		max = Emblems.TOTAL16;
		flagPanel = new JPanel(new GridLayout(6, 10));
		emblemButtons = new JButton[max];
		fileLabel = new JLabel("From:");

		for (int l = 0; l < max; l++) {
			emblemButtons[l] = new JButton(new ImageIcon(Emblems.get16(of2, -1, false, true)));
			// emblemButtons[l].setIcon();
			emblemButtons[l].setMargin(new Insets(0, 0, 0, 0));
			emblemButtons[l].setActionCommand(Integer.toString(l));
			emblemButtons[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					selectEmblem(evt);
				}
			});
			flagPanel.add(emblemButtons[l]);
		}

		JButton transButton = new JButton("Transparency");
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				toggleTransparent(evt);
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

	private void toggleTransparent(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		trans = !trans;
		refresh();
		// System.out.println(trans);
				/*
				 * for (int f = 0; f < 64; f++) { if (trans) {
				 * emblemButtons[f].setIcon(of2.tranFlag[f]); } else {
				 * emblemButtons[f].setIcon(of2.flag[f]); } }
				 */
	}

	private void selectEmblem(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		slot = Integer.parseInt(((JButton) evt.getSource()).getActionCommand());
		if (slot >= Emblems.count16(of2)) {
			slot = Emblems.TOTAL16 - 1 - slot;
		} else {
			slot = slot + Emblems.TOTAL128;
		}

		setVisible(false);
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	public void refresh() {
		if (type == 0 || type == 1) {
			for (int i = 0; i < Emblems.count16(of2); i++) {
				emblemButtons[i].setIcon(new ImageIcon(Emblems.get16(of2, i, !trans, true)));
				emblemButtons[i].setVisible(true);
			}
		}
		if (type == 0 || type == 2) {
			for (int i = 0; i < Emblems.count128(of2); i++) {
				emblemButtons[Emblems.TOTAL16 - 1 - i].setIcon(new ImageIcon(
						Emblems.get128(of2, i, !trans, true)));
				emblemButtons[Emblems.TOTAL16 - 1 - i].setVisible(true);
			}
		}

		int s = Emblems.count16(of2);
		int e = Emblems.TOTAL16 - Emblems.count128(of2);
		if (type == 1) {
			e = Emblems.TOTAL16;
		}
		if (type == 2) {
			s = 0;
		}
		for (int i = s; i < e; i++) {
			emblemButtons[i].setVisible(false);
		}
	}

	public int getFlag(String title, int t) {
		type = t;
		slot = -1;
		setTitle(title);
		fileLabel.setText("  From:  " + of2.getFilename());
		refresh();
		setVisible(true);
		return slot;
	}

	public void import128(OptionFile of, int slot, int replacement) {
		Emblems.import128(of, slot, of2, replacement);
	}

	public void import16(OptionFile of, int slot, int replacement) {
		Emblems.import16(of, slot, of2, replacement);
	}

}
