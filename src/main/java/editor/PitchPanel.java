package editor;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.ui.AtkDefPanel;
import editor.ui.SquadList;
import editor.ui.SquadNumberList;
import editor.util.swing.IndexColorComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class PitchPanel extends JPanel implements IndexColorComponent, MouseListener, MouseMotionListener {
	private static final boolean SHOW_ATTACK = true;
	private static final boolean SHOW_DEFENCE = true;
	private static final boolean SHOW_NUMBER = true;
	private static final boolean SHOW_ROLE = true;

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

	private volatile int xAdj = 0;
	private volatile int yAdj = 0;

	public PitchPanel(
			OptionFile of, FormationPanel parent,
			SquadList squadList, AtkDefPanel adp, JComboBox altBox, SquadNumberList numList) {
		super();

		if (null == of) throw new NullPointerException("of");
		if (null == parent) throw new NullPointerException("parent");
		if (null == squadList) throw new NullPointerException("squadList");
		if (null == adp) throw new NullPointerException("adp");
		if (null == altBox) throw new NullPointerException("altBox");
		if (null == numList) throw new NullPointerException("numList");
		this.of = of;
		this.parent = parent;
		this.squadList = squadList;
		this.atkDefPan = adp;
		this.altBox = altBox;
		this.numList = numList;

		setOpaque(true);
		setPreferredSize(new Dimension(329 + ADJ * 2, 200 + ADJ * 2));
		setBackground(COLORS[0]);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setSquad(int squad) {
		this.squad = squad;
	}

	public void setSelectedIndex(int selected) {
		this.selectedIdx = selected;
	}

	private static final Color[] COLORS = {
			Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.GREEN, Color.RED, Color.BLUE, Color.GRAY
	};

	public Color[] getPalette() {
		return COLORS;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (null == g2) throw new NullPointerException("g");

		// TODO: !!!
		g2.setPaint(COLORS[0]);
		g2.fill(new Rectangle2D.Double(0, 0, 329 + (ADJ * 2), 200 + (ADJ * 2)));
		g2.setPaint(COLORS[7]);
		// g2.setStroke(stroke);
		g2.draw(new Rectangle2D.Double(13, 13, 329 + 2, 200 + 2));
		g2.draw(new Line2D.Double(178, 13, 178, 215));
		g2.draw(new Ellipse2D.Double(178 - 33, 114 - 33, 66, 66));
		g2.draw(new Rectangle2D.Double(13, 62, 46, 104));
		g2.draw(new Rectangle2D.Double(298, 62, 46, 104));
		g2.draw(new Rectangle2D.Double(13, 85, 17, 58));
		g2.draw(new Rectangle2D.Double(327, 85, 17, 58));
		g2.draw(new Arc2D.Double(40, 89, 38, 49, 270, 180, Arc2D.OPEN));
		g2.draw(new Arc2D.Double(279, 89, 38, 49, 90, 180, Arc2D.OPEN));
		int x;
		int y;
		int pos;
		// g2.setPaint(COLORS[2]);
		// g2.fill(new Ellipse2D.Double(0 + ADJ, 90 + ADJ, DIA, DIA));
		for (int p = 0; p < 11; p++) {
			pos = Formations.getPosition(of, squad, altBox.getSelectedIndex(), p);
			// System.out.println(pos);
			if (p == 0) {
				x = ADJ;
				y = 90 + ADJ;
				// g2.setPaint(COLORS[2]);
			} else {
				x = ((Formations.getX(of, squad, altBox.getSelectedIndex(), p) - 2) * 7) + ADJ;
				y = ((Formations.getY(of, squad, altBox.getSelectedIndex(), p) - 6) * 2) + ADJ;
				// pos = of.data[670642 + (628 * squad) + 6232 + p];
			}
			if (p == selectedIdx) {
				g2.setPaint(COLORS[1]);
			} else {
				if (pos == 0) {
					g2.setPaint(COLORS[2]);
				} else if (pos > 0 && pos < 10) {
					g2.setPaint(COLORS[3]);
				} else if (pos > 9 && pos < 29) {
					g2.setPaint(COLORS[4]);
				} else if (pos > 28 && pos < 41) {
					g2.setPaint(COLORS[5]);
				}
			}
			g2.fill(new Ellipse2D.Double(x, y, DIA, DIA));
			// g2.draw(new Ellipse2D.Double(x, y, DIA, DIA));

			// draw position label
			if (SHOW_ROLE) {
				g2.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
				int adjx = 0;
				if (pos == 30 || pos == 16 || pos == 4) {
					adjx = -1;
				}
				boolean up = Formations.getAttack(of, squad, altBox.getSelectedIndex(), p, 2);
				boolean down = Formations.getAttack(of, squad, altBox.getSelectedIndex(), p, 6);
				if (up && down) {
					g2.drawString(Formations.positionToString(pos).substring(0, 1), x + 15, y + 6);
					g2.drawString(Formations.positionToString(pos).substring(1, 2), x + 15, y + 16);
				} else if (pos == 9 || pos == 16 || pos == 23 || pos == 30) {
					if (!down) {
						g2.drawString(Formations.positionToString(pos), x + adjx, y + 24);
					} else {
						g2.drawString(Formations.positionToString(pos), x + adjx, y - 2);
					}
				} else {
					if (up) {
						g2.drawString(Formations.positionToString(pos), x + adjx, y + 24);
					} else {
						g2.drawString(Formations.positionToString(pos), x + adjx, y - 2);
					}
				}
			}

			if (SHOW_ATTACK) {
				int x1 = x + 7;
				int y1 = y + 7;
				int x2 = x1;
				int y2 = y1;
				for (int i = 0; i < 8; i++) {
					if (Formations.getAttack(of, squad, altBox.getSelectedIndex(),
							p, i)) {
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
						}
						g2.draw(new Line2D.Double(x1, y1, x2, y2));
					}
				}
			}

			if (SHOW_NUMBER) {
				g2.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
				g2.setPaint(COLORS[0]);
				String numText = numList.getModel().getElementAt(p);
				int ta = 0;
				if (numText.length() == 1) {
					ta = 3;
				}
				if (numText.startsWith("1")) {
					ta = ta - 1;
				}
				g2.drawString(numText, x + 2 + ta, y + 11);
			} /*
			 * else if (SHOW_ROLE) { g2.setFont(new Font(Font.DIALOG, Font.PLAIN,
			 * 12)); g2.setPaint(COLORS[0]);
			 * g2.drawString(getPosLabel(pos).substring(0, 1), x + 3, y + 12); }
			 */

			if (SHOW_DEFENCE) {
				g2.setPaint(COLORS[6]);
				int size = 6;
				int x1 = (x + 7) - 13 - (size / 2);
				int y1 = (y + 7) - 5 - (size / 2);
				int x2 = (x + 7) - 13 - (size / 2);
				int y2 = (y + 7) + 5 - (size / 2);
				if (Formations.getDefence(of, squad, altBox.getSelectedIndex(), p) == 1) {
					g2.fill(new Ellipse2D.Double(x2, y2, size, size));
				} else if (Formations.getDefence(of, squad, altBox
						.getSelectedIndex(), p) == 0) {
					g2.fill(new Ellipse2D.Double(x1, y1, size, size));
					g2.fill(new Ellipse2D.Double(x2, y2, size, size));
				}
			}
			// System.out.println(x + ", " + y);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");

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

	private void findPressedIndex(MouseEvent e) {
		// Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse
		Ellipse2D circle;
		for (int i = 1; i < 11; i++) {
			int x = Formations.getX(of, squad, altBox.getSelectedIndex(), i);
			int y = Formations.getY(of, squad, altBox.getSelectedIndex(), i);
			x = (x - 2) * 7 + ADJ;
			y = (y - 6) * 2 + ADJ;

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
		if (null == e) throw new NullPointerException("e");
		if (selectedIdx <= 0) return;

		int pos = Formations.getPosition(of, squad, altBox.getSelectedIndex(), selectedIdx);

		int x = adjustDraggedX(e.getX(), pos);
		int y = adjustDraggedY(e.getY(), pos);
		//log.debug("{}, {}", x, y);

		Formations.setX(of, squad, altBox.getSelectedIndex(), selectedIdx, x);
		Formations.setY(of, squad, altBox.getSelectedIndex(), selectedIdx, y);

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
