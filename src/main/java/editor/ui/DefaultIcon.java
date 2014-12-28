package editor.ui;

import editor.util.Strings;

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
		Graphics2D g2 = (Graphics2D) g;
		if (null == g2) throw new NullPointerException("g");

		g2.translate(x, y);

		g2.setFont(new Font("Dialog", Font.BOLD, 18));
		g2.setPaint(Color.black);
		g2.drawString(Strings.getMessage("Default"), 0, 38);

		g2.translate(-x, -y);   // restore graphics object
	}

}
