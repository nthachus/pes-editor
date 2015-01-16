package editor.ui;

import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class DefaultIcon implements Icon, Serializable {
	private static final long serialVersionUID = -8485807162420655130L;
	private static final Logger log = LoggerFactory.getLogger(DefaultIcon.class);

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
		log.debug("Painting Default icon is starting..");

		Graphics2D g2 = (Graphics2D) g;
		g2.translate(x, y);

		g2.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
		g2.setPaint(Color.BLACK);
		g2.drawString(Resources.getMessage("Default"), 0, 38);

		g2.translate(-x, -y);   // restore graphics object

		log.debug("Painting of Default icon succeeded");
	}

}
