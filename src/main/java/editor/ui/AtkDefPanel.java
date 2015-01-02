package editor.ui;

import editor.PitchPanel;
import editor.data.Formations;
import editor.data.OptionFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class AtkDefPanel extends JPanel implements MouseListener {
	private static final int BOX_SIZE = 98;
	private static final int SQR_WIDTH = 84;
	private static final int SQR_SIZE = 14;
	private static final int SQR_MID = SQR_WIDTH / 2;
	private static final int SQR_QRT = SQR_MID / 2;

	private final OptionFile of;
	private final JComboBox altBox;

	private volatile int squad = 0;
	private volatile int selectedIndex = -1;
	private volatile PitchPanel pitch;

	private final Rectangle2D[] atkSquares = new Rectangle2D[8];

	public AtkDefPanel(OptionFile of, JComboBox altBox) {
		super();
		if (null == of) throw new NullPointerException("of");
		if (null == altBox) throw new NullPointerException("altBox");
		this.of = of;
		this.altBox = altBox;

		atkSquares[0] = new Rectangle2D.Double(0, SQR_MID, SQR_SIZE, SQR_SIZE);
		atkSquares[1] = new Rectangle2D.Double(0, 0, SQR_SIZE, SQR_SIZE);
		atkSquares[2] = new Rectangle2D.Double(SQR_MID, 0, SQR_SIZE, SQR_SIZE);
		atkSquares[3] = new Rectangle2D.Double(SQR_WIDTH, 0, SQR_SIZE, SQR_SIZE);
		atkSquares[4] = new Rectangle2D.Double(SQR_WIDTH, SQR_MID, SQR_SIZE, SQR_SIZE);
		atkSquares[5] = new Rectangle2D.Double(SQR_WIDTH, SQR_WIDTH, SQR_SIZE, SQR_SIZE);
		atkSquares[6] = new Rectangle2D.Double(SQR_MID, SQR_WIDTH, SQR_SIZE, SQR_SIZE);
		atkSquares[7] = new Rectangle2D.Double(0, SQR_WIDTH, SQR_SIZE, SQR_SIZE);

		setOpaque(true);
		setPreferredSize(new Dimension(BOX_SIZE, BOX_SIZE));
		setBackground(Color.black);
		addMouseListener(this);
	}

	public void setSquad(int squad) {
		this.squad = squad;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public void setPitch(PitchPanel pitch) {
		this.pitch = pitch;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (null == g2) throw new NullPointerException("g");

		Rectangle2D bound = new Rectangle2D.Double(0, 0, BOX_SIZE, BOX_SIZE);
		if (selectedIndex < 0) {
			g2.setPaint(Color.gray);
			g2.fill(bound);
		} else {
			g2.setPaint(Color.black);
			g2.fill(bound);

			drawAtkSquares(g2);

			drawDefDirections(g2);
		}
	}

	private static Color getPositionColor(int pos) {
		if (pos <= 0) {
			return Color.yellow;
		} else if (pos < 10) {
			return Color.cyan;
		} else if (pos < 29) {
			return Color.green;
		} else if (pos < 41) {
			return Color.red;
		}
		return Color.black;
	}

	private void drawAtkSquares(Graphics2D g2) {
		int pos = Formations.getPosition(of, squad, altBox.getSelectedIndex(), selectedIndex);
		Color posColor = getPositionColor(pos);

		g2.setPaint(posColor);
		g2.fill(new Ellipse2D.Double(SQR_MID, SQR_MID, SQR_SIZE, SQR_SIZE));

		for (int i = 0; i < atkSquares.length; i++) {
			if (Formations.getAttack(of, squad, altBox.getSelectedIndex(), selectedIndex, i)) {
				g2.setPaint(posColor);
				g2.fill(atkSquares[i]);
			} else if (selectedIndex != 0 || (i == 0 || i == 4)) {
				g2.setPaint(Color.gray);
				g2.draw(atkSquares[i]);
			}
		}
	}

	private void drawDefDirections(Graphics2D g2) {
		int def = Formations.getDefence(of, squad, altBox.getSelectedIndex(), selectedIndex);

		if (def == 1) {
			g2.setPaint(Color.gray);
			g2.draw(new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE));
			g2.setPaint(Color.blue);
			g2.fill(new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE));
		} else if (def == 0) {
			g2.setPaint(Color.blue);
			g2.fill(new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE));
			g2.fill(new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE));
		} else {
			g2.setPaint(Color.gray);
			g2.draw(new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE));
			g2.draw(new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE));
		}
	}

	public void mousePressed(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");
		int def = Formations.getDefence(of, squad, altBox.getSelectedIndex(), selectedIndex);

		// Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse.
		if (new Ellipse2D.Double(SQR_MID, SQR_MID, SQR_SIZE, SQR_SIZE).contains(e.getX(), e.getY())) {
			Formations.setAttack(of, squad, altBox.getSelectedIndex(), selectedIndex, -1);
			Formations.setDefence(of, squad, altBox.getSelectedIndex(), selectedIndex, 2);

		} else if (new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE).contains(e.getX(), e.getY())) {
			Formations.setDefence(of, squad, altBox.getSelectedIndex(), selectedIndex, (def == 0) ? 1 : 0);

		} else if (new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE).contains(e.getX(), e.getY())) {
			Formations.setDefence(of, squad, altBox.getSelectedIndex(), selectedIndex, (def == 2) ? 1 : 2);

		} else {
			int count = countAtkDirections();
			for (int i = 0; i < atkSquares.length; i++) {
				if (atkSquares[i].contains(e.getX(), e.getY())) {
					if (Formations.getAttack(of, squad, altBox.getSelectedIndex(), selectedIndex, i)
							|| (count < 2 && (selectedIndex != 0 || i == 0 || i == 4))) {
						Formations.setAttack(of, squad, altBox.getSelectedIndex(), selectedIndex, i);
					}
					break;
				}
			}
		}

		repaint();
		if (null != pitch) pitch.repaint();
	}

	private int countAtkDirections() {
		int count = 0;
		for (int j = 0; j < atkSquares.length; j++) {
			if (Formations.getAttack(of, squad, altBox.getSelectedIndex(), selectedIndex, j)) {
				count++;
			}
		}
		// TODO: log.debug("", count);
		return count;
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

}
