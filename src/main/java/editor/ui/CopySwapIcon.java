package editor.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class CopySwapIcon implements Icon {
	private static final int WIDTH = 10;
	private static final int HEIGHT = 20;

	private final boolean isSwap;

	public CopySwapIcon(boolean isSwap) {
		this.isSwap = isSwap;
	}

	public CopySwapIcon() {
		this(false);
	}

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

		g2.setPaint(Color.BLACK);
		g2.draw(new Line2D.Double(5, 0, 5, 20));

		g2.draw(new Line2D.Double(5, 20, 0, 15));
		g2.draw(new Line2D.Double(5, 20, 10, 15));

		if (isSwap) {
			g2.draw(new Line2D.Double(5, 0, 0, 5));
			g2.draw(new Line2D.Double(5, 0, 10, 5));
		}

		g2.translate(-x, -y);   // restore graphics object
	}

}
