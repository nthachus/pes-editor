package editor.ui;

import editor.data.ControlButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Ps2ButtonIcon implements Icon, Serializable {
	private static final long serialVersionUID = 5155541330279975158L;
	private static final Logger log = LoggerFactory.getLogger(Ps2ButtonIcon.class);

	private final ControlButton type;

	public Ps2ButtonIcon(ControlButton type) {
		if (null == type) throw new NullPointerException("type");
		this.type = type;
	}

	public int getIconWidth() {
		return 17;
	}

	public int getIconHeight() {
		return 17;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (null == g) throw new NullPointerException("g");
		if (!(g instanceof Graphics2D)) throw new IllegalArgumentException("g");
		log.debug("Try to paint PS2 button icon: {}", type);

		Graphics2D g2 = (Graphics2D) g;
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
