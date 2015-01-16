package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class AtkDefPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = -785512082815155690L;
	private static final Logger log = LoggerFactory.getLogger(AtkDefPanel.class);

	private static final int BOX_SIZE = 98;
	private static final int SQR_LEN = 84;
	private static final int SQR_SIZE = 14;
	private static final int SQR_MID = SQR_LEN / 2;
	private static final int SQR_QRT = SQR_MID / 2;

	private final OptionFile of;
	private final JComboBox altBox;

	private volatile int squad = 0;
	private volatile int selectedIndex = -1;
	private volatile PitchPanel pitch;

	private final Rectangle2D[] atkSquares = new Rectangle2D[8];// 8 directions

	public AtkDefPanel(OptionFile of, JComboBox altBox) {
		super();
		if (null == of) throw new NullPointerException("of");
		if (null == altBox) throw new NullPointerException("altBox");
		this.of = of;
		this.altBox = altBox;

		log.debug("Attack/Defense panel is initializing..");
		initComponents();
	}

	private void initComponents() {
		atkSquares[0] = new Rectangle2D.Double(0, SQR_MID, SQR_SIZE, SQR_SIZE);
		atkSquares[1] = new Rectangle2D.Double(0, 0, SQR_SIZE, SQR_SIZE);
		atkSquares[2] = new Rectangle2D.Double(SQR_MID, 0, SQR_SIZE, SQR_SIZE);
		atkSquares[3] = new Rectangle2D.Double(SQR_LEN, 0, SQR_SIZE, SQR_SIZE);
		atkSquares[4] = new Rectangle2D.Double(SQR_LEN, SQR_MID, SQR_SIZE, SQR_SIZE);
		atkSquares[5] = new Rectangle2D.Double(SQR_LEN, SQR_LEN, SQR_SIZE, SQR_SIZE);
		atkSquares[6] = new Rectangle2D.Double(SQR_MID, SQR_LEN, SQR_SIZE, SQR_SIZE);
		atkSquares[7] = new Rectangle2D.Double(0, SQR_LEN, SQR_SIZE, SQR_SIZE);

		setOpaque(true);
		setPreferredSize(new Dimension(BOX_SIZE, BOX_SIZE));
		setBackground(Color.BLACK);
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
		if (null == g) throw new NullPointerException("g");
		if (!(g instanceof Graphics2D)) throw new IllegalArgumentException("g");
		log.debug("Try to paint panel with selected-index: {}, squad: {}", selectedIndex, squad);

		Graphics2D g2 = (Graphics2D) g;
		Rectangle2D bound = new Rectangle2D.Double(0, 0, BOX_SIZE, BOX_SIZE);

		if (selectedIndex < 0) {
			g2.setPaint(Color.GRAY);
			g2.fill(bound);
		} else {
			g2.setPaint(Color.BLACK);
			g2.fill(bound);

			drawAtkSquares(g2);

			drawDefDirections(g2);
		}

		log.debug("Painting of Attack/Defense panel completed");
	}

	private static Color getPositionColor(int pos) {
		if (pos <= 0) {
			return Color.YELLOW;
		} else if (pos < 10) {
			return Color.CYAN;
		} else if (pos < 29) {
			return Color.GREEN;
		} else if (pos < 41) {
			return Color.RED;
		}
		return Color.BLACK;
	}

	private void drawAtkSquares(Graphics2D g2) {
		int alt = altBox.getSelectedIndex();
		int pos = Formations.getPosition(of, squad, alt, selectedIndex);
		Color posColor = getPositionColor(pos);

		g2.setPaint(posColor);
		g2.fill(new Ellipse2D.Double(SQR_MID, SQR_MID, SQR_SIZE, SQR_SIZE));

		for (int i = 0; i < atkSquares.length; i++) {
			if (Formations.getAttack(of, squad, alt, selectedIndex, i)) {
				g2.setPaint(posColor);
				g2.fill(atkSquares[i]);
			} else if (selectedIndex != 0 || (i == 0 || i == 4)) {
				g2.setPaint(Color.GRAY);
				g2.draw(atkSquares[i]);
			}
		}
	}

	private void drawDefDirections(Graphics2D g2) {
		int alt = altBox.getSelectedIndex();
		int def = Formations.getDefence(of, squad, alt, selectedIndex);

		if (def == 1) {
			g2.setPaint(Color.GRAY);
			g2.draw(new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE));
			g2.setPaint(Color.BLUE);
			g2.fill(new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE));
		} else if (def == 0) {
			g2.setPaint(Color.BLUE);
			g2.fill(new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE));
			g2.fill(new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE));
		} else {
			g2.setPaint(Color.GRAY);
			g2.draw(new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE));
			g2.draw(new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE));
		}
	}

	public void mousePressed(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");
		log.debug("Perform mouse pressed with selected-index: {}, squad: {}", selectedIndex, squad);

		int alt = Integer.MIN_VALUE;
		// Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse.
		if (new Ellipse2D.Double(SQR_MID, SQR_MID, SQR_SIZE, SQR_SIZE).contains(e.getX(), e.getY())) {
			alt = altBox.getSelectedIndex();
			Formations.setAttack(of, squad, alt, selectedIndex, -1);
			Formations.setDefence(of, squad, alt, selectedIndex, 2);

		} else if (new Ellipse2D.Double(SQR_QRT, SQR_QRT, SQR_SIZE, SQR_SIZE).contains(e.getX(), e.getY())) {
			alt = altBox.getSelectedIndex();
			int def = Formations.getDefence(of, squad, alt, selectedIndex);
			Formations.setDefence(of, squad, alt, selectedIndex, (def == 0) ? 1 : 0);

		} else if (new Ellipse2D.Double(SQR_QRT, SQR_MID * 3 / 2, SQR_SIZE, SQR_SIZE).contains(e.getX(), e.getY())) {
			alt = altBox.getSelectedIndex();
			int def = Formations.getDefence(of, squad, alt, selectedIndex);
			Formations.setDefence(of, squad, alt, selectedIndex, (def == 2) ? 1 : 2);

		} else {
			int count = countAtkDirections();
			for (int i = 0; i < atkSquares.length; i++) {
				if (atkSquares[i].contains(e.getX(), e.getY())) {

					alt = altBox.getSelectedIndex();
					if (Formations.getAttack(of, squad, alt, selectedIndex, i)
							|| (count < 2 && (selectedIndex != 0 || i == 0 || i == 4))) {
						Formations.setAttack(of, squad, alt, selectedIndex, i);
					}
					break;
				}
			}
		}

		if (alt > Integer.MIN_VALUE) {
			repaint();
			if (null != pitch) pitch.repaint();

			log.debug("Setting Attack/Defense on click succeeded");
		}
	}

	private int countAtkDirections() {
		int count = 0;
		int alt = altBox.getSelectedIndex();
		for (int j = 0; j < atkSquares.length; j++) {
			if (Formations.getAttack(of, squad, alt, selectedIndex, j)) {
				count++;
			}
		}
		//log.debug("Attack directions count: {}", count);
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
