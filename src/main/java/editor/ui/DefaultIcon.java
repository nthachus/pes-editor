package editor.ui;

import editor.util.Resources;

import javax.swing.*;
import java.awt.*;

public class DefaultIcon implements Icon {
	private static final int WIDTH = 64;
	private static final int HEIGHT = 64;

	public int getIconHeight() {
		return HEIGHT;
	}

	public int getIconWidth() {
		return WIDTH;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (null == g) throw new NullPointerException("g");
		if (!(g instanceof Graphics2D)) throw new IllegalArgumentException("g");

		Graphics2D g2 = (Graphics2D) g;
		g2.translate(x, y);

		g2.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
		g2.setPaint(Color.BLACK);
		g2.drawString(Resources.getMessage("Default"), 0, 38);

		g2.translate(-x, -y);   // restore graphics object
	}

}
