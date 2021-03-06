package editor.ui;

import editor.data.OptionFile;

import javax.swing.*;
import java.awt.*;

public class WenShopPanel extends JPanel {
	private static final long serialVersionUID = -3595204138128563538L;

	private final WenPanel wenPanel;
	private final ShopPanel shopPanel;

	public WenShopPanel(OptionFile of) {
		super();

		wenPanel = new WenPanel(of);
		shopPanel = new ShopPanel(of);

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
