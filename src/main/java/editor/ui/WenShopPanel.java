package editor.ui;

import editor.data.OptionFile;

import javax.swing.*;
import java.awt.*;

public class WenShopPanel extends JPanel {
	private final WenPanel wenPanel;
	private final ShopPanel shopPanel;

	public WenShopPanel(OptionFile optionFile) {
		super();

		wenPanel = new WenPanel(optionFile);
		shopPanel = new ShopPanel(optionFile);

		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.add(wenPanel);
		contentPane.add(shopPanel);

		add(contentPane);
	}

	public WenPanel getWenPanel() {
		return wenPanel;
	}

	public ShopPanel getShopPanel() {
		return shopPanel;
	}
}
