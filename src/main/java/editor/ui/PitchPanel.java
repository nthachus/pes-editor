package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class PitchPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -6469818605753384346L;

	private static final int ADJ = 14;
	private static final int DIA = 14;

	private final OptionFile of;
	private final FormationPanel parent;
	private final SquadList squadList;
	private final AtkDefPanel atkDefPan;
	private final JComboBox altBox;
	private final SquadNumberList numList;

	private volatile int squad = 0;
	private volatile int selectedIdx = -1;

	private volatile boolean showAttack = true;
	private volatile boolean showDefence = true;
	private volatile boolean showNumber = true;
	private volatile boolean roleOn = true;
	private volatile int xAdj = 0;
	private volatile int yAdj = 0;

	public PitchPanel(
			OptionFile of, FormationPanel parent,
			SquadList squadList, AtkDefPanel adp, JComboBox altBox, SquadNumberList numList) {
		super();

		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == parent) {
			throw new NullArgumentException("parent");
		}
		if (null == squadList) {
			throw new NullArgumentException("squadList");
		}
		if (null == adp) {
			throw new NullArgumentException("adp");
		}
		if (null == altBox) {
			throw new NullArgumentException("altBox");
		}
		if (null == numList) {
			throw new NullArgumentException("numList");
		}
		this.of = of;
		this.parent = parent;
		this.squadList = squadList;
		this.atkDefPan = adp;
		this.altBox = altBox;
		this.numList = numList;

		setOpaque(true);
		setPreferredSize(new Dimension(329 + ADJ * 2, 200 + ADJ * 2));
		setBackground(Color.BLACK);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setSquad(int squad) {
		this.squad = squad;
	}

	public void setSelectedIndex(int selected) {
		this.selectedIdx = selected;
	}

	public void setShowAttack(boolean isShowAttack) {
		showAttack = isShowAttack;
	}

	public void setShowDefence(boolean isShowDefence) {
		showDefence = isShowDefence;
	}

	public void setShowNumber(boolean isShowNumber) {
		showNumber = isShowNumber;
	}

	public void setRoleOn(boolean isRoleOn) {
		roleOn = isRoleOn;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (null == g) {
			throw new NullArgumentException("g");
		}
		if (!(g instanceof Graphics2D)) {
			throw new IllegalArgumentException("g");
		}

		Graphics2D g2 = (Graphics2D) g;
		drawStadiumLayout(g2);

		Color c;
		int alt = altBox.getSelectedIndex();
		for (int p = 0; p < Formations.PLAYER_COUNT; p++) {
			int pos = Formations.getPosition(of, squad, alt, p);
			int x = getXForPlayer(p);
			int y = getYForPlayer(p);
			//log.debug("x: {}, y: {}", x, y);

			c = getPositionColor(p, pos);
			if (null != c) {
				g2.setPaint(c);
			}
			g2.fill(new Ellipse2D.Double(x, y, DIA, DIA));
			//g2.draw(new Ellipse2D.Double(x, y, DIA, DIA));

			drawPositionLabel(g2, p, pos, x, y);
			drawAtkDirections(g2, p, x, y);
			drawNumbers(g2, p, x, y);
			drawDefDirections(g2, p, x, y);
		}
	}

	private void drawStadiumLayout(Graphics2D g2) {
		g2.setPaint(Color.BLACK);
		g2.fill(new Rectangle2D.Double(0, 0, 329 + ADJ * 2, 200 + ADJ * 2));
		g2.setPaint(Color.GRAY);
		//g2.setStroke(stroke);
		g2.draw(new Rectangle2D.Double(13, 13, 329 + 2, 200 + 2));
		g2.draw(new Line2D.Double(178, 13, 178, 215));
		g2.draw(new Ellipse2D.Double(178 - 33, 114 - 33, 66, 66));
		g2.draw(new Rectangle2D.Double(13, 62, 46, 104));
		g2.draw(new Rectangle2D.Double(298, 62, 46, 104));
		g2.draw(new Rectangle2D.Double(13, 85, 17, 58));
		g2.draw(new Rectangle2D.Double(327, 85, 17, 58));
		g2.draw(new Arc2D.Double(40, 89, 38, 49, 270, 180, Arc2D.OPEN));
		g2.draw(new Arc2D.Double(279, 89, 38, 49, 90, 180, Arc2D.OPEN));
	}

	private Color getPositionColor(int player, int pos) {
		if (player == selectedIdx) {
			return Color.WHITE;
		} else {
			if (pos <= 0) {
				return Color.YELLOW;
			} else if (pos < 10) {
				return Color.CYAN;
			} else if (pos < 29) {
				return Color.GREEN;
			} else if (pos < 41) {
				return Color.RED;
			}
		}
		return null;
	}

	private void drawPositionLabel(Graphics2D g2, int player, int pos, int x, int y) {
		if (!roleOn) {
			return;
		}

		g2.setFont(new Font(UIUtil.DIALOG, Font.BOLD, 10));
		int adjX = 0;
		if (pos == 30 || pos == 16 || pos == 4) {
			adjX = -1;
		}

		int alt = altBox.getSelectedIndex();
		boolean up = Formations.getAttack(of, squad, alt, player, 2);
		boolean down = Formations.getAttack(of, squad, alt, player, 6);

		if (up && down) {
			g2.drawString(Formations.positionToString(pos).substring(0, 1), x + 15, y + 6);
			g2.drawString(Formations.positionToString(pos).substring(1), x + 15, y + 16);

		} else if (pos == 9 || pos == 16 || pos == 23 || pos == 30) {
			if (!down) {
				g2.drawString(Formations.positionToString(pos), x + adjX, y + 24);
			} else {
				g2.drawString(Formations.positionToString(pos), x + adjX, y - 2);
			}
		} else {
			if (up) {
				g2.drawString(Formations.positionToString(pos), x + adjX, y + 24);
			} else {
				g2.drawString(Formations.positionToString(pos), x + adjX, y - 2);
			}
		}
	}

	private void drawAtkDirections(Graphics2D g2, int player, int x, int y) {
		if (!showAttack) {
			return;
		}

		int x1 = x + ADJ / 2;
		int y1 = y + ADJ / 2;
		int alt = altBox.getSelectedIndex();
		for (int i = 0; i < 8; i++) {
			if (!Formations.getAttack(of, squad, alt, player, i)) {
				continue;
			}

			int x2, y2;
			switch (i) {
				case 0:
					x2 = x1 - 21;
					y2 = y1;
					break;
				case 1:
					x2 = x1 - 15;
					y2 = y1 - 15;
					break;
				case 2:
					x2 = x1;
					y2 = y1 - 21;
					break;
				case 3:
					x2 = x1 + 15;
					y2 = y1 - 15;
					break;
				case 4:
					x2 = x1 + 21;
					y2 = y1;
					break;
				case 5:
					x2 = x1 + 15;
					y2 = y1 + 15;
					break;
				case 6:
					x2 = x1;
					y2 = y1 + 21;
					break;
				case 7:
					x2 = x1 - 15;
					y2 = y1 + 15;
					break;
				default:
					x2 = x1;
					y2 = y1;
					break;
			}

			g2.draw(new Line2D.Double(x1, y1, x2, y2));
		}
	}

	private void drawNumbers(Graphics2D g2, int player, int x, int y) {
		if (!showNumber) {
			return;
		}

		String numText = (String) numList.getModel().getElementAt(player);
		int textAdj = 0;
		if (numText.length() == 1) {
			textAdj = 3;
		}
		if (numText.startsWith("1")) {
			textAdj--;
		}

		g2.setFont(new Font(UIUtil.DIALOG, Font.BOLD, 10));
		g2.setPaint(Color.BLACK);
		g2.drawString(numText, x + 2 + textAdj, y + 11);
	}

	private void drawDefDirections(Graphics2D g2, int player, int x, int y) {
		if (!showDefence) {
			return;
		}

		g2.setPaint(Color.BLUE);

		final int size = 6;
		int x1 = x + ADJ / 2 - 13 - size / 2;
		int y1 = y + ADJ / 2 - 5 - size / 2;
		int x2 = x + ADJ / 2 - 13 - size / 2;
		int y2 = y + ADJ / 2 + 5 - size / 2;

		if (Formations.getDefence(of, squad, altBox.getSelectedIndex(), player) != 0) {
			g2.fill(new Ellipse2D.Double(x2, y2, size, size));
		} else {
			g2.fill(new Ellipse2D.Double(x1, y1, size, size));
			g2.fill(new Ellipse2D.Double(x2, y2, size, size));
		}
	}

	public void mousePressed(MouseEvent e) {
		if (null == e) {
			throw new NullArgumentException("e");
		}

		selectedIdx = -1;
		findPressedIndex(e);

		parent.setFromPitch(true);
		if (selectedIdx < 0) {
			Ellipse2D circle = new Ellipse2D.Double(ADJ, 90 + ADJ, DIA, DIA);
			if (circle.contains(e.getX(), e.getY())) {
				selectedIdx = 0;
				squadList.setSelectedIndex(selectedIdx);
				atkDefPan.setSelectedIndex(selectedIdx);
			} else {
				squadList.clearSelection();
				atkDefPan.setSelectedIndex(selectedIdx);
			}
		} else {
			squadList.setSelectedIndex(selectedIdx);
			atkDefPan.setSelectedIndex(selectedIdx);
		}

		repaint();
		atkDefPan.repaint();
	}

	private int getXForPlayer(int index) {
		if (index <= 0) {
			return ADJ;
		}
		int x = Formations.getX(of, squad, altBox.getSelectedIndex(), index);
		x = (x - 2) * 7 + ADJ;
		return x;
	}

	private int getYForPlayer(int index) {
		if (index <= 0) {
			return 90 + ADJ;
		}
		int y = Formations.getY(of, squad, altBox.getSelectedIndex(), index);
		y = (y - 6) * 2 + ADJ;
		return y;
	}

	private void findPressedIndex(MouseEvent e) {
		// Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse
		Ellipse2D circle;
		for (int i = 1; i < Formations.PLAYER_COUNT; i++) {
			int x = getXForPlayer(i);
			int y = getYForPlayer(i);

			circle = new Ellipse2D.Double(x, y, DIA, DIA);
			if (circle.contains(e.getX(), e.getY())) {
				selectedIdx = i;
				xAdj = e.getX() - x;
				yAdj = e.getY() - y;

				break;
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (null == e) {
			throw new NullArgumentException("e");
		}
		if (selectedIdx <= 0) {
			return;
		}

		int alt = altBox.getSelectedIndex();
		int pos = Formations.getPosition(of, squad, alt, selectedIdx);

		int x = adjustDraggedX(e.getX(), pos);
		int y = adjustDraggedY(e.getY(), pos);
		//log.debug("{}, {}", x, y);

		Formations.setX(of, squad, alt, selectedIdx, x);
		Formations.setY(of, squad, alt, selectedIdx, y);

		repaint();
	}

	private int adjustDraggedX(int x, int pos) {
		x -= xAdj;
		x = Math.min(Math.max(x, ADJ), 315 + ADJ);
		x = (x - ADJ) / 7 + 2;

		if (pos > 0 && pos < 10) {
			x = Math.min(x, 15);
		} else if (pos >= 10 && pos < 29) {
			x = Math.min(Math.max(x, 16), 34);
		} else if (pos >= 29 && pos < 41) {
			x = Math.max(x, 35);
		}

		return x;
	}

	private int adjustDraggedY(int y, int pos) {
		y -= yAdj;
		y = Math.min(Math.max(y, ADJ), 186 + ADJ);
		y = (y - ADJ) / 2 + 6;

		if (pos == 8 || pos == 15 || pos == 22 || pos == 29) {
			y = Math.min(y, 50);
		} else if (pos == 9 || pos == 16 || pos == 23 || pos == 30) {
			y = Math.max(y, 54);
		}

		return y;
	}

	public void mouseMoved(MouseEvent e) {
	}

}
