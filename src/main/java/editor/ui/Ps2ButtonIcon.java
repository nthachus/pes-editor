package editor.ui;

import editor.data.ControlButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Ps2ButtonIcon implements Icon {
	private static final int WIDTH = 17;
	private static final int HEIGHT = 17;

	private final ControlButton type;

	public Ps2ButtonIcon(ControlButton type) {
		if (null == type) throw new NullPointerException("type");
		this.type = type;
	}

	public int getIconHeight() {
		return HEIGHT;
	}

	public int getIconWidth() {
		return WIDTH;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		if (null == g2) throw new NullPointerException("g");

		g2.translate(x, y);

		g2.setPaint(Color.BLACK);
		g2.fill(new Ellipse2D.Double(1, 1, 15, 15));

		if (type == ControlButton.square) {
			g2.setPaint(Color.PINK);
			g2.draw(new Rectangle2D.Double(4, 4, 8, 8));
		} else if (type == ControlButton.triangle) {
			g2.setPaint(Color.GREEN);
			g2.draw(new Line2D.Double(8, 4, 12, 12));
			g2.draw(new Line2D.Double(4, 12, 12, 12));
			g2.draw(new Line2D.Double(8, 4, 4, 12));
		} else if (type == ControlButton.circle) {
			g2.setPaint(Color.RED);
			g2.draw(new Ellipse2D.Double(4, 4, 8, 8));
		} else if (type == ControlButton.cross) {
			g2.setPaint(Color.CYAN);
			g2.draw(new Line2D.Double(4, 4, 12, 12));
			g2.draw(new Line2D.Double(4, 12, 12, 4));
		}

		g2.translate(-x, -y);   // restore graphics object
	}

}
