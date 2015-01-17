package editor.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class CopySwapIcon implements Icon, Serializable {
	private static final long serialVersionUID = -4081848473723249944L;
	private static final Logger log = LoggerFactory.getLogger(CopySwapIcon.class);

	private final boolean isSwap;

	public CopySwapIcon(boolean isSwap) {
		this.isSwap = isSwap;
	}

	/*public CopySwapIcon() {
		this(false);
	}*/

	public int getIconWidth() {
		return 10;
	}

	public int getIconHeight() {
		return 20;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (null == g) throw new NullPointerException("g");
		if (!(g instanceof Graphics2D)) throw new IllegalArgumentException("g");
		log.debug("Try to paint swap ({}) icon", isSwap);

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
