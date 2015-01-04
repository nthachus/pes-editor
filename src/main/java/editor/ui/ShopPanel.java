package editor.ui;

import editor.data.OptionFile;
import editor.data.Player;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ShopPanel extends JPanel {
	private static final int PLAYER_COUNT = Player.TOTAL_SHOP;
	private static final int START_ADR = 5144;

	private final OptionFile of;
	private final JLabel status;

	public ShopPanel(OptionFile optionFile) {
		super();
		if (null == optionFile) throw new NullPointerException("optionFile");
		of = optionFile;

		status = new JLabel();

		JButton lock = new JButton(Resources.getMessage("Lock"));
		lock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onLock(evt);
			}
		});

		JButton unlock = new JButton(Resources.getMessage("Unlock"));
		unlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onUnlock(evt);
			}
		});

		JPanel buttonsPane = new JPanel();
		buttonsPane.add(lock);
		buttonsPane.add(unlock);

		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("shop.title")));
		contentPane.add(buttonsPane);
		contentPane.add(status);

		add(contentPane);
	}

	public void refresh() {
		status.setText("");
	}

	private void onLock(ActionEvent evt) {
		int ofs = START_ADR + PLAYER_COUNT / 8;
		Arrays.fill(of.getData(), START_ADR, ofs, (byte) 0);
		Arrays.fill(of.getData(), ofs, ofs + 6, (byte) 0);

		status.setText(Resources.getMessage("Locked"));
	}

	private void onUnlock(ActionEvent evt) {
		int ofs = START_ADR + PLAYER_COUNT / 8;
		Arrays.fill(of.getData(), START_ADR, ofs, (byte) 0xFF);

		of.getData()[ofs] = 0x38;
		of.getData()[++ofs] = 0x09;
		of.getData()[++ofs] = (byte) 0xFE;
		of.getData()[++ofs] = (byte) 0xFF;
		of.getData()[++ofs] = (byte) 0xCF;
		of.getData()[++ofs] = 0x7F;

		status.setText(Resources.getMessage("Unlocked"));
	}

}
