package editor.ui;

import editor.data.OptionFile;
import editor.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ShopPanel extends JPanel {
	public static final int PLAYER_COUNT = 160;

	private final OptionFile of;
	private final JLabel status;

	public ShopPanel(OptionFile optionFile) {
		super();
		if (null == optionFile) throw new NullPointerException("optionFile");
		of = optionFile;

		status = new JLabel();

		JButton lock = new JButton(Strings.getMessage("Lock"));
		lock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onLock(evt);
			}
		});

		JButton unlock = new JButton(Strings.getMessage("Unlock"));
		unlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onUnlock(evt);
			}
		});

		JPanel buttonsPane = new JPanel();
		buttonsPane.add(lock);
		buttonsPane.add(unlock);

		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.setBorder(BorderFactory.createTitledBorder(Strings.getMessage("shop.title")));
		contentPane.add(buttonsPane);
		contentPane.add(status);

		add(contentPane);
	}

	public void refresh() {
		status.setText("");
	}

	private void onLock(ActionEvent evt) {
		Arrays.fill(of.getData(), 5144, 5144 + PLAYER_COUNT / 8, (byte) 0);
		Arrays.fill(of.getData(), 5164, 5170, (byte) 0);

		status.setText(Strings.getMessage("Locked"));
	}

	private void onUnlock(ActionEvent evt) {
		Arrays.fill(of.getData(), 5144, 5144 + PLAYER_COUNT / 8, (byte) 0xFF);

		of.getData()[5164] = 0x38;
		of.getData()[5165] = 0x09;
		of.getData()[5166] = (byte) 0xFE;
		of.getData()[5167] = (byte) 0xFF;
		of.getData()[5168] = (byte) 0xCF;
		of.getData()[5169] = 0x7F;

		status.setText(Strings.getMessage("Unlocked"));
	}

}
