/*
 * Copyright 2008-9 Compulsion
 * <pes_compulsion@yahoo.co.uk>
 * <http://www.purplehaze.eclipse.co.uk/>
 * <http://uk.geocities.com/pes_compulsion/>
 *
 * This file is part of PES Editor.
 *
 * PES Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PES Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PES Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package editor;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.ui.SelectByTeam;
import editor.util.Bits;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TransferPanel extends JPanel implements MouseListener,
		DropTargetListener, DragSourceListener, DragGestureListener {

	private SelectByTeam selectorL;

	private SelectByTeam selectorR;

	private SelectByNation freeList;

	private OptionFile of;

	private NameEditor nameEditor;

	private NumEditor numEditor;

	private InfoPanel infoPanel;

	private ShirtNameEditor shirtEditor;

	private PlayerDialog playerDia;

	private FormationDialog teamDia;

	private JCheckBox autoRel = new JCheckBox("Auto Release");

	private JCheckBox autoRep = new JCheckBox("Auto Sub");

	private JCheckBox safeMode = new JCheckBox("Safe Mode");

	private JButton compare;

	private int releasedIndex = 0;

	private DragSource sourceF = null;

	private DragSource sourceL = null;

	private DragSource sourceR = null;

	private Component sourceComp = null;

	private int sourceIndex = -1;

	private DataFlavor localPlayerFlavor;

	private int compIndex = 0;

	private int lastIndex = 0;

	public TransferPanel(PlayerDialog pd, OptionFile opf, FormationDialog td) {
		super();
		of = opf;
		teamDia = td;
		playerDia = pd;
		autoRel
				.setToolTipText(
						"When a player is transfered to a club squad he will be automatically released from his old squad");
		autoRel.setSelected(true);
		autoRep
				.setToolTipText(
						"Gaps made in a team's first 11 will be automatically filled with the most appropriate sub");
		autoRep.setSelected(true);
		safeMode
				.setToolTipText("Only transfers that are possible in-game will be allowed");
		safeMode.setSelected(true);

		compare = new JButton("Compare Stats");
		compare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent rl) {
				if (compIndex == 0) {
					compIndex = lastIndex;
					if (nameEditor.source == 2) {
						int squadS = selectorL.getTeamBox().getSelectedIndex();
						if (squadS < 67 || (squadS > 74 && squadS < 212)) {
							selectorL.getPosList().selectPos(selectorL.getSquadList(),
									selectorL.getSquadList().getSelectedIndex());
						}
					} else if (nameEditor.source == 3) {
						int squadS = selectorR.getTeamBox().getSelectedIndex();
						if (squadS < 67 || (squadS > 74 && squadS < 212)) {
							selectorR.getPosList().selectPos(selectorR.getSquadList(),
									selectorR.getSquadList().getSelectedIndex());
						}
					}
				} else {
					compIndex = 0;
					selectorL.getPosList().clearSelection();
					selectorR.getPosList().clearSelection();
				}
				infoPanel.refresh(lastIndex, compIndex);
			}
		});

		freeList = new SelectByNation(of);
		selectorL = new SelectByTeam(of, true);
		nameEditor = new NameEditor();
		numEditor = new NumEditor();
		shirtEditor = new ShirtNameEditor();
		JPanel editPanel = new JPanel(new GridLayout(0, 1));
		JPanel optPanel = new JPanel(new GridLayout(0, 1));
		JPanel lPanel = new JPanel(new BorderLayout());
		JPanel rPanel = new JPanel(new BorderLayout());
		selectorR = new SelectByTeam(of, true);
		addListen();
		freeList.freeList.addMouseListener(this);
		selectorL.getSquadList().addMouseListener(this);
		selectorR.getSquadList().addMouseListener(this);

		String localPlayerType = DataFlavor.javaJVMLocalObjectMimeType
				+ ";class=editor.Player";
		try {
			localPlayerFlavor = new DataFlavor(localPlayerType);
		} catch (ClassNotFoundException e) {
			System.out
					.println("FormTransferHandler: unable to create data flavor");
		}
		new DropTarget(freeList.freeList, this);
		new DropTarget(selectorL.getSquadList(), this);
		new DropTarget(selectorR.getSquadList(), this);
		sourceF = new DragSource();
		sourceF.createDefaultDragGestureRecognizer(freeList.freeList,
				DnDConstants.ACTION_MOVE, this);
		sourceL = new DragSource();
		sourceL.createDefaultDragGestureRecognizer(selectorL.getSquadList(),
				DnDConstants.ACTION_MOVE, this);
		sourceR = new DragSource();
		sourceR.createDefaultDragGestureRecognizer(selectorR.getSquadList(),
				DnDConstants.ACTION_MOVE, this);

		infoPanel = new InfoPanel(selectorL, of);

		selectorL.getSquadList()
				.setToolTipText("Double click to edit player, right click to edit formation");
		selectorR.getSquadList()
				.setToolTipText("Double click to edit player, right click to edit formation");
		freeList.freeList.setToolTipText("Double click to edit player");

		editPanel.add(nameEditor);
		editPanel.add(shirtEditor);
		optPanel.add(autoRel);
		optPanel.add(autoRep);
		optPanel.add(safeMode);

		JPanel editOptPan = new JPanel();
		JPanel editOptInfoPan = new JPanel(new BorderLayout());
		editOptPan.add(numEditor);
		editOptPan.add(editPanel);
		editOptPan.add(optPanel);
		editOptInfoPan.add(editOptPan, BorderLayout.NORTH);
		editOptInfoPan.add(infoPanel, BorderLayout.CENTER);
		editOptInfoPan.add(compare, BorderLayout.SOUTH);

		lPanel.add(selectorL, BorderLayout.CENTER);
		rPanel.add(selectorR, BorderLayout.CENTER);
		JPanel listPan = new JPanel(new GridLayout(0, 3));
		listPan.add(freeList);
		listPan.add(lPanel);
		listPan.add(rPanel);

		add(listPan);
		add(editOptInfoPan);
	}

	private int getNumAdr(int a) {
		return Squads.NATION_NUM_ADR + ((a - Squads.NATION_ADR) / 2);
	}

	public void refresh() {
		freeList.refresh();
		selectorL.refresh();
		selectorR.refresh();
		nameEditor.setText("");
		numEditor.setText("");
		shirtEditor.setText("");
		compIndex = 0;
		lastIndex = 0;
		infoPanel.refresh(lastIndex, compIndex);
	}

	public void refreshLists() {
		freeList.freeList.refresh(freeList.nationBox.getSelectedIndex(),
				freeList.alpha);
		selectorL.getSquadList().refresh(selectorL.getTeamBox().getSelectedIndex(), true);
		selectorR.getSquadList().refresh(selectorR.getTeamBox().getSelectedIndex(), true);
		selectorL.getNumList().refresh(selectorL.getTeamBox().getSelectedIndex());
		selectorR.getNumList().refresh(selectorR.getTeamBox().getSelectedIndex());
		selectorL.getPosList().refresh(selectorL.getTeamBox().getSelectedIndex());
		selectorR.getPosList().refresh(selectorR.getTeamBox().getSelectedIndex());
		nameEditor.setText("");
		numEditor.setText("");
		shirtEditor.setText("");
		compIndex = 0;
		lastIndex = 0;
		infoPanel.refresh(lastIndex, compIndex);
	}

	private class NameEditor extends JTextField implements
			ListSelectionListener, ActionListener {

		int source = 0;

		public NameEditor() {
			super(13);
			addActionListener(this);
			setToolTipText("Enter new name and press return");
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				if (e.getSource() == freeList.freeList) {
					if (freeList.freeList.isSelectionEmpty()) {
						setText("");
						lastIndex = 0;
					} else {
						setText(((Player) freeList.freeList.getSelectedValue()).name);
						source = 1;
						selectAll();
						lastIndex = ((Player) freeList.freeList
								.getSelectedValue()).index;
					}
				}
				if (e.getSource() == selectorL.getSquadList()) {
					if (selectorL.getSquadList().isSelectionEmpty()) {
						setText("");
						lastIndex = 0;
					} else {
						if (((Player) selectorL.getSquadList().getSelectedValue()).index != 0) {
							setText(((Player) selectorL.getSquadList()
									.getSelectedValue()).name);
						} else {
							setText("");
						}
						source = 2;
						selectAll();
						lastIndex = ((Player) selectorL.getSquadList()
								.getSelectedValue()).index;
					}
				}
				if (e.getSource() == selectorR.getSquadList()) {
					if (selectorR.getSquadList().isSelectionEmpty()) {
						setText("");
						lastIndex = 0;
					} else {
						if (((Player) selectorR.getSquadList().getSelectedValue()).index != 0) {
							setText(((Player) selectorR.getSquadList()
									.getSelectedValue()).name);
						} else {
							setText("");
						}
						source = 3;
						selectAll();
						lastIndex = ((Player) selectorR.getSquadList()
								.getSelectedValue()).index;
					}
				}
				infoPanel.refresh(lastIndex, compIndex);
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (source == 1 && !freeList.freeList.isSelectionEmpty()
					&& getText().length() < 16 && getText().length() != 0) {
				int i = freeList.freeList.getSelectedIndex();
				if (!(((Player) freeList.freeList.getSelectedValue()).name
						.equals(getText()))) {
					((Player) freeList.freeList.getSelectedValue())
							.setName(getText());
					((Player) freeList.freeList.getSelectedValue())
							.makeShirt(getText());
					refreshLists();
				}
				if (!freeList.alpha
						&& i < freeList.freeList.getModel().getSize() - 1) {
					freeList.freeList.setSelectedIndex(i + 1);
					freeList.freeList.ensureIndexIsVisible(i + 1);
				}
			}
			if (source == 2 && !selectorL.getSquadList().isSelectionEmpty()
					&& getText().length() < 16 && getText().length() != 0) {
				int i = selectorL.getSquadList().getSelectedIndex();
				if (!(((Player) selectorL.getSquadList().getSelectedValue()).name
						.equals(getText()))) {
					((Player) selectorL.getSquadList().getSelectedValue())
							.setName(getText());
					((Player) selectorL.getSquadList().getSelectedValue())
							.makeShirt(getText());
					refreshLists();
				}
				if (i < selectorL.getSquadList().getModel().getSize() - 1) {
					selectorL.getSquadList().setSelectedIndex(i + 1);
				}
			}
			if (source == 3 && !selectorR.getSquadList().isSelectionEmpty()
					&& getText().length() < 16 && getText().length() != 0) {
				int i = selectorR.getSquadList().getSelectedIndex();
				if (!(((Player) selectorR.getSquadList().getSelectedValue()).name
						.equals(getText()))) {
					((Player) selectorR.getSquadList().getSelectedValue())
							.setName(getText());
					((Player) selectorR.getSquadList().getSelectedValue())
							.makeShirt(getText());
					refreshLists();
				}
				if (i < selectorR.getSquadList().getModel().getSize() - 1) {
					selectorR.getSquadList().setSelectedIndex(i + 1);
				}
			}
		}
	}

	private class NumEditor extends JTextField implements
			ListSelectionListener, ActionListener {

		int source = 0;

		public NumEditor() {
			super(2);
			addActionListener(this);
			setToolTipText("Enter new squad number and press return");
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == selectorL.getNumList()) {
				if (selectorL.getNumList().isSelectionEmpty()) {
					setText("");
				} else {
					source = 2;
					setText(String.valueOf(getShirt(source, selectorL.getNumList()
							.getSelectedIndex())));
					selectorR.getNumList().clearSelection();
					selectAll();
				}
			}
			if (e.getSource() == selectorR.getNumList()) {
				if (selectorR.getNumList().isSelectionEmpty()) {
					setText("");
				} else {
					source = 3;
					setText(String.valueOf(getShirt(source, selectorR.getNumList()
							.getSelectedIndex())));
					selectorL.getNumList().clearSelection();
					selectAll();
				}
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (source == 2 && !selectorL.getNumList().isSelectionEmpty()) {
				int i = selectorL.getNumList().getSelectedIndex();
				try {
					int v = new Integer(getText()).intValue();
					if (v != 0 && v <= 99) {
						setShirt(source, i, v);
					}
					selectorR.getNumList().refresh(selectorR.getTeamBox()
							.getSelectedIndex());
					selectorL.getNumList().refresh(selectorL.getTeamBox()
							.getSelectedIndex());
					if (i < selectorL.getSquadList().getModel().getSize() - 1) {
						selectorL.getNumList().setSelectedIndex(i + 1);
					}
				} catch (NumberFormatException nfe) {
				}
			}
			if (source == 3 && !selectorR.getNumList().isSelectionEmpty()) {
				int i = selectorR.getNumList().getSelectedIndex();
				try {
					int v = new Integer(getText()).intValue();
					if (v != 0 && v <= 99) {
						setShirt(source, i, v);
					}
					selectorR.getNumList().refresh(selectorR.getTeamBox()
							.getSelectedIndex());
					selectorL.getNumList().refresh(selectorL.getTeamBox()
							.getSelectedIndex());
					if (i < selectorR.getSquadList().getModel().getSize() - 1) {
						selectorR.getNumList().setSelectedIndex(i + 1);
					}
				} catch (NumberFormatException nfe) {
				}
			}
		}
	}

	public int getShirt(int s, int i) {
		int a;
		if (s == 2) {
			a = selectorL.getSquadList().getModel().getElementAt(i).adr;
		} else {
			a = selectorR.getSquadList().getModel().getElementAt(i).adr;
		}
		a = getNumAdr(a);
		int shirt = Bits.toInt(of.getData()[a]) + 1;
		if (shirt == 256) {
			shirt = 0;
		}
		return shirt;
	}

	public void setShirt(int s, int i, int newShirt) {
		int a;
		if (s == 2) {
			a = selectorL.getSquadList().getModel().getElementAt(i).adr;
		} else {
			a = selectorR.getSquadList().getModel().getElementAt(i).adr;
		}
		a = getNumAdr(a);
		int shirt = Bits.toInt(of.getData()[a]) + 1;
		if (shirt != 256) {
			of.getData()[a] = Bits.toByte(newShirt - 1);
		}
	}

	private class ShirtNameEditor extends JTextField implements ListSelectionListener, ActionListener {
		int source = 0;

		public ShirtNameEditor() {
			super(13);
			addActionListener(this);
			setToolTipText("Enter new shirt name and press return");
		}

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (e.getSource() == freeList.freeList) {
					if (freeList.freeList.isSelectionEmpty()) {
						setText("");
					} else {
						setText(((Player) freeList.freeList.getSelectedValue())
								.getShirtName());
						source = 1;
						selectAll();
					}
				}
				if (e.getSource() == selectorL.getSquadList()) {
					if (selectorL.getSquadList().isSelectionEmpty()) {
						setText("");
					} else {
						setText(((Player) selectorL.getSquadList()
								.getSelectedValue()).getShirtName());
						source = 2;
						selectAll();
					}
				}
				if (e.getSource() == selectorR.getSquadList()) {
					if (selectorR.getSquadList().isSelectionEmpty()) {
						setText("");
					} else {
						setText(((Player) selectorR.getSquadList()
								.getSelectedValue()).getShirtName());
						source = 3;
						selectAll();
					}
				}
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (source == 1 && !freeList.freeList.isSelectionEmpty()
					&& getText().length() < 16) {
				((Player) freeList.freeList.getSelectedValue())
						.setShirtName(getText());
				refreshLists();
			}
			if (source == 2 && !selectorL.getSquadList().isSelectionEmpty()
					&& getText().length() < 16) {
				int i = selectorL.getSquadList().getSelectedIndex();
				((Player) selectorL.getSquadList().getSelectedValue())
						.setShirtName(getText());
				refreshLists();
				if (i < selectorL.getSquadList().getModel().getSize() - 1) {
					selectorL.getSquadList().setSelectedIndex(i + 1);
				}
			}
			if (source == 3 && !selectorR.getSquadList().isSelectionEmpty()
					&& getText().length() < 16) {
				int i = selectorR.getSquadList().getSelectedIndex();
				((Player) selectorR.getSquadList().getSelectedValue())
						.setShirtName(getText());
				refreshLists();
				if (i < selectorR.getSquadList().getModel().getSize() - 1) {
					selectorR.getSquadList().setSelectedIndex(i + 1);
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
			if (e.getSource() != freeList.freeList) {
				e.consume();
				SquadList list = (SquadList) (e.getSource());
				int t = list.getTeam();
				if (t >= 0 && t < 66) {
					teamDia.show(t, (String) selectorL.getTeamBox().getItemAt(t));
					Squads.fixForm(of, t, false);
					refreshLists();
				}
				if (t >= 75 && t < 205) {
					teamDia
							.show(t - 8, (String) selectorL.getTeamBox()
									.getItemAt(t));
					Squads.fixForm(of, t, false);
					refreshLists();
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		int clicks = e.getClickCount();
		if (e.getButton() == MouseEvent.BUTTON1 && clicks == 2) {
			JList list = (JList) (e.getSource());
			Player p = ((Player) list.getSelectedValue());
			int pi = p.index;
			if (pi != 0) {
				if (safeMode.isSelected()) {
					if (inNatSquad(pi)) {
						playerDia.genPanel.nationBox.setEnabled(false);
					} else {
						playerDia.genPanel.nationBox.setEnabled(true);
					}
				} else {
					playerDia.genPanel.nationBox.setEnabled(true);
				}
				playerDia.show(p);
				refreshLists();
			}
		} else if (clicks == 1 && e.getButton() == MouseEvent.BUTTON3) {
			if (e.getSource() != freeList.freeList) {
				SquadList list = (SquadList) (e.getSource());
				int t = list.getTeam();
				if (t >= 0 && t < 67) {
					teamDia.show(t, (String) selectorL.getTeamBox().getItemAt(t));
					Squads.fixForm(of, t, false);
					refreshLists();
				}
				if (t >= 75 && t < 205) {
					teamDia
							.show(t - 8, (String) selectorL.getTeamBox()
									.getItemAt(t));
					Squads.fixForm(of, t, false);
					refreshLists();
				}
			}
		}
	}

	private int clubRelease(int p, boolean rel) {
		int a = Squads.CLUB_ADR - 2;
		int index;
		int result = -1;
		int sqi;
		int sp;
		do {
			a = a + 2;
			index = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
			if (index == p) {
				sqi = ((a - Squads.CLUB_ADR) / 64) + 75;
				sp = (a - Squads.CLUB_ADR) % 64;
				if (result == -1) {
					if (!rel || sp < 22) {
						result = sqi;
						if (rel) {
							releasedIndex = sp / 2;
						}

					}
				}
				if (rel) {
					of.getData()[a] = 0;
					of.getData()[a + 1] = 0;
					of.getData()[getNumAdr(a)] = -1;
					if (sp >= 22) {
						Squads.tidy(of, sqi);
					} else {
						if (autoRep.isSelected()) {
							int t = sqi;
							if (t > 74) {
								t = t - 8;
							}
							Squads.tidy11(of, sqi, sp / 2, Formations.getPos(
									of, t, 0, sp / 2));
						}
					}
				}
			}
		} while (a < Squads.CLUB_ADR + (Clubs.TOTAL * 64) - 2);// && index != p);
		return result;
	}

	private byte getNextNum(int s) {
		int size;
		int firstAdr;
		byte num = -1;
		int a;
		byte n;
		boolean spare;
		if (s < 75) {
			size = 23;
			firstAdr = Squads.NATION_NUM_ADR + (s * size);
		} else {
			size = 32;
			firstAdr = Squads.CLUB_NUM_ADR + ((s - 75) * size);
		}
		for (byte i = 0; num == -1 && i < 99; i++) {
			spare = true;
			for (int p = 0; spare && p < size; p++) {
				a = firstAdr + p;
				n = of.getData()[a];
				if (n == i) {
					spare = false;
				}
			}
			if (spare) {
				num = i;
			}
		}
		if (num == -1) {
			num = 0;
		}
		return num;
	}

	private int countPlayers(int squad) {
		int size;
		int firstAdr;
		int i;
		int count = 0;
		int a;
		if (squad < 75) {
			size = 23;
			firstAdr = Squads.NATION_ADR + (squad * size * 2);
		} else {
			size = 32;
			firstAdr = Squads.CLUB_ADR + ((squad - 75) * size * 2);
		}
		for (int p = 0; p < size; p++) {
			a = firstAdr + (p * 2);
			i = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
			if (i != 0) {
				count++;
			}
		}
		return count;
	}

	private boolean inNatSquad(int pi) {
		boolean in = false;
		for (int i = 0; in == false && i < 67; i++) {
			if (inSquad(i, pi)) {
				in = true;
			}
		}
		return in;
	}

	private boolean inSquad(int squad, int pi) {
		boolean in = false;
		if (pi != 0) {
			int size;
			int firstAdr;
			int i;
			int a;
			if (squad < 75) {
				size = 23;
				firstAdr = Squads.NATION_ADR + (squad * size * 2);
			} else {
				size = 32;
				firstAdr = Squads.CLUB_ADR + ((squad - 75) * size * 2);
			}
			for (int p = 0; !in && p < size; p++) {
				a = firstAdr + (p * 2);
				i = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
				if (i == pi) {
					in = true;
				}
			}
		}
		return in;
	}

	public void dragEnter(DropTargetDragEvent event) {
	}

	public void dragExit(DropTargetEvent event) {
	}

	public void dragOver(DropTargetDragEvent event) {
		JList targetList = (JList) (event.getDropTargetContext().getComponent());
		int i = targetList.locationToIndex(event.getLocation());
		Player p;
		if (i != -1) {
			p = (Player) (targetList.getModel().getElementAt(i));
		} else {
			p = new Player(of, 0, 0);
		}
		boolean chk = checkSafeDrag(safeMode.isSelected(), targetList, p);
		targetList.setSelectedIndex(i);
		if (chk) {
			event.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			event.rejectDrag();
		}
	}

	public void drop(DropTargetDropEvent event) {
		Transferable transferable = event.getTransferable();
		if (transferable.isDataFlavorSupported(localPlayerFlavor)) {
			JList sourceList = (JList) sourceComp;
			JList targetList = (JList) (event.getDropTargetContext()
					.getComponent());
			Player sourcePlayer = (Player) (sourceList.getModel()
					.getElementAt(sourceIndex));
			int indexS = sourcePlayer.index;
			Player targetPlayer;
			int indexT;
			if (targetList.getSelectedIndex() != -1) {
				targetPlayer = (Player) (targetList.getSelectedValue());
				indexT = targetPlayer.index;
			} else {
				targetPlayer = new Player(of, 0, 0);
				indexT = 0;
			}

			if (sourceList != freeList.freeList
					&& targetList != freeList.freeList) {
				int squadS = ((SelectByTeam) (sourceList.getParent())).getTeamBox()
						.getSelectedIndex();
				int squadT = ((SelectByTeam) (targetList.getParent())).getTeamBox()
						.getSelectedIndex();
				if (sourceList == targetList) {
					if (squadS < 67 || (squadS > 74 && squadS < 213)) {
						if (indexS != indexT) {
							event.acceptDrop(DnDConstants.ACTION_MOVE);
							transferS(sourcePlayer, targetPlayer, squadS,
									squadT, sourceList, targetList);
						}
					}
				} else if (sourceList == selectorL.getSquadList()
						&& targetList == selectorR.getSquadList()) {
					event.acceptDrop(DnDConstants.ACTION_MOVE);
					transferLR(sourcePlayer);
				} else if (sourceList == selectorR.getSquadList()
						&& targetList == selectorL.getSquadList()) {
					event.acceptDrop(DnDConstants.ACTION_MOVE);
					transferRL(sourcePlayer);
				}
			} else if (sourceList == freeList.freeList
					&& targetList == selectorL.getSquadList()) {
				event.acceptDrop(DnDConstants.ACTION_MOVE);
				transferFL(indexS);
			} else if (sourceList == freeList.freeList
					&& targetList == selectorR.getSquadList()) {
				event.acceptDrop(DnDConstants.ACTION_MOVE);
				transferFR(indexS);
			} else if (sourceList == selectorL.getSquadList()
					&& targetList == freeList.freeList) {
				event.acceptDrop(DnDConstants.ACTION_MOVE);
				tranRelL(sourcePlayer, sourceIndex);
			} else if (sourceList == selectorR.getSquadList()
					&& targetList == freeList.freeList) {
				event.acceptDrop(DnDConstants.ACTION_MOVE);
				tranRelR(sourcePlayer, sourceIndex);
			} else {
				event.rejectDrop();
			}

			event.getDropTargetContext().dropComplete(true);
		} else {
			event.rejectDrop();
		}
	}

	public void dropActionChanged(DropTargetDragEvent event) {
	}

	public void dragGestureRecognized(DragGestureEvent event) {
		sourceComp = event.getComponent();
		if (sourceComp instanceof JList) {
			JList list = (JList) sourceComp;
			sourceIndex = list.getSelectedIndex();
			Player p = (Player) list.getSelectedValue();
			if (sourceIndex != -1 && p.index != 0) {
				removeListen();
				lastIndex = 0;
				compIndex = 0;
				infoPanel.refresh(lastIndex, compIndex);
				nameEditor.setText("");
				shirtEditor.setText("");
				nameEditor.source = 0;
				shirtEditor.source = 0;
				PlayerTransferable playerTran = new PlayerTransferable(p);
				if (list != freeList.freeList) {
					int squadS = ((SelectByTeam) (list.getParent())).getTeamBox()
							.getSelectedIndex();
					if (squadS < 67 || (squadS > 74 && squadS < 213)) {
						if (list == selectorL.getSquadList()) {
							selectorL.getPosList().selectPos(selectorL.getSquadList(),
									selectorL.getSquadList().getSelectedIndex());
						} else if (list == selectorR.getSquadList()) {
							selectorR.getPosList().selectPos(selectorR.getSquadList(),
									selectorR.getSquadList().getSelectedIndex());
						}
					}
				}
				event.getDragSource().startDrag(event, null, playerTran, this);

			}
		}
	}

	public void dragDropEnd(DragSourceDropEvent event) {
		if (!event.getDropSuccess()) {
			refreshLists();
		}
		addListen();
	}

	public void dragEnter(DragSourceDragEvent event) {
	}

	public void dragExit(DragSourceEvent event) {
	}

	public void dragOver(DragSourceDragEvent event) {
	}

	public void dropActionChanged(DragSourceDragEvent event) {
	}

	public class PlayerTransferable implements Transferable {
		Player data;

		public PlayerTransferable(Player p) {
			data = p;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return data;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{localPlayerFlavor};
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (localPlayerFlavor.equals(flavor)) {
				return true;
			}
			return false;
		}
	}

	private boolean checkSafeDrag(
			boolean safe, JList targetList,
			Player targetPlayer) {
		boolean tranFL = true;
		boolean tranFR = true;
		boolean tranLR = true;
		boolean tranRL = true;
		boolean relL = true;
		boolean relR = true;
		boolean fEmpty = true;
		boolean lEmpty = true;
		boolean rEmpty = true;
		int squadL = -1;
		int squadR = -1;

		JList sourceList = (JList) sourceComp;
		int indexS = ((Player) (sourceList.getModel().getElementAt(sourceIndex))).index;
		int indexT = targetPlayer.index;
		int squadS = -1;

		int indexF = 0;
		if (sourceList == freeList.freeList) {
			indexF = indexS;
			fEmpty = false;
		} else if (targetList == freeList.freeList) {
			indexF = indexT;
			fEmpty = false;
		}
		int indexL = 0;
		if (sourceList == selectorL.getSquadList()) {
			indexL = indexS;
			lEmpty = false;
			squadS = ((SelectByTeam) (sourceList.getParent())).getTeamBox()
					.getSelectedIndex();
		} else if (targetList == selectorL.getSquadList()) {
			indexL = indexT;
			lEmpty = false;
		}
		int indexR = 0;
		if (sourceList == selectorR.getSquadList()) {
			indexR = indexS;
			rEmpty = false;
			squadS = ((SelectByTeam) (sourceList.getParent())).getTeamBox()
					.getSelectedIndex();
		} else if (targetList == selectorR.getSquadList()) {
			indexR = indexT;
			rEmpty = false;
		}

		squadL = selectorL.getTeamBox().getSelectedIndex();
		squadR = selectorR.getTeamBox().getSelectedIndex();
		if (safe) {
			int minSizeL = 16;
			int minSizeR = 16;
			if (squadL < 75) {
				minSizeL = 23;
			}
			if (squadR < 75) {
				minSizeR = 23;
			}

			if (indexF >= Player.firstYoung && indexF < Player.FIRST_EDIT) {
				tranFL = false;
				tranFR = false;
			}

			if (indexF >= Player.firstML && indexF < Player.firstShop) {
				tranFL = false;
				tranFR = false;
			}

			if (indexF >= Player.FIRST_EDIT && indexF < 32920 && squadL > 59) {
				tranFL = false;
			}

			if (indexF >= Player.FIRST_EDIT && indexF < 32920 && squadR > 59) {
				tranFR = false;
			}

			if (squadL > 74 && squadL < 213) {
				int s = clubRelease(indexF, false);
				if (autoRel.isSelected()) {
					if (s != -1) {
						int c = countPlayers(s);
						if (c <= 16) {
							tranFL = false;
						}
					}
				} else {
					if (s != -1) {
						tranFL = false;
					}
				}
			}

			if (squadR > 74 && squadR < 213) {
				int s = clubRelease(indexF, false);
				if (autoRel.isSelected()) {
					if (s != -1) {
						int c = countPlayers(s);
						if (c <= 16) {
							tranFR = false;
						}
					}
				} else {
					if (s != -1) {
						tranFR = false;
					}
				}
			}

			if ((squadL > 59 && squadL < 75) || squadL > 208) {
				tranFL = false;
				if (squadL > 208 && squadL < 212) {
					tranLR = false;
				} else {
					if (squadL > 66 && squadL < 74 && squadR > 66) {
						tranLR = false;
					}
				}
				tranRL = false;
				relL = false;
			} else {
				int countL = countPlayers(squadL);
				if (countL <= minSizeL) {
					relL = false;
					if (indexR == 0) {
					}
					if (autoRel.isSelected() && squadL > 74) {
						tranLR = false;
					}
				}
				if (inSquad(squadL, indexR)) {
					tranRL = false;
					if (squadL != squadR) {
					}
				}
				if (inSquad(squadL, indexF)) {
					tranFL = false;
				}

				if (!autoRel.isSelected() && squadL > 74 && squadL < 213) {
					int s = clubRelease(indexR, false);
					if (s != -1) {
						tranRL = false;
					}
				}

			}

			if ((squadR > 59 && squadR < 75) || squadR > 208) {
				tranLR = false;
				tranFR = false;
				if (squadR > 208 && squadR < 212) {
					tranRL = false;
				} else {
					if (squadR > 66 && squadR < 74 && squadL > 66) {
						tranRL = false;
					}
				}
				relR = false;
			} else {
				int countR = countPlayers(squadR);
				if (countR <= minSizeR) {
					relR = false;
					if (indexL == 0) {
					}
					if (autoRel.isSelected() && squadR > 74) {
						tranRL = false;
					}
				}
				if (inSquad(squadR, indexL)) {
					tranLR = false;
					if (squadL != squadR) {
					}
				}

				if (inSquad(squadR, indexF)) {
					tranFR = false;
				}

				if (!autoRel.isSelected() && squadR > 74 && squadR < 213) {
					int s = clubRelease(indexL, false);
					if (s != -1) {
						tranLR = false;
					}
				}
			}

			if (squadR == squadL) {
				tranLR = false;
				tranRL = false;
			}

			if (squadL < 67) {
				int squadNat = squadL;
				switch (squadNat) {
					case 60:
						squadNat = 6;
						break;
					case 61:
						squadNat = 8;
						break;
					case 62:
						squadNat = 9;
						break;
					case 63:
						squadNat = 13;
						break;
					case 64:
						squadNat = 15;
						break;
					case 65:
						squadNat = 44;
						break;
					case 66:
						squadNat = 45;
						break;
				}

				int nat;
				if (!fEmpty) {
					nat = Stats.getValue(of, indexF, Stats.NATIONALITY);
					if (nat != (Stats.NATION.length - 1) && nat != squadNat) {
						tranFL = false;
					}
				}
				if (!rEmpty) {
					nat = Stats.getValue(of, indexR, Stats.NATIONALITY);
					if (nat != (Stats.NATION.length - 1) && nat != squadNat) {
						tranRL = false;
					}
				}
			}

			if (squadR < 67) {
				int squadNat = squadR;
				switch (squadNat) {
					case 60:
						squadNat = 6;
						break;
					case 61:
						squadNat = 8;
						break;
					case 62:
						squadNat = 9;
						break;
					case 63:
						squadNat = 13;
						break;
					case 64:
						squadNat = 15;
						break;
					case 65:
						squadNat = 44;
						break;
					case 66:
						squadNat = 45;
						break;
				}

				int nat;
				if (!fEmpty) {
					nat = Stats.getValue(of, indexF, Stats.NATIONALITY);
					if (nat != (Stats.NATION.length - 1) && nat != squadNat) {
						tranFR = false;
					}
				}
				if (!lEmpty) {
					nat = Stats.getValue(of, indexL, Stats.NATIONALITY);
					if (nat != (Stats.NATION.length - 1) && nat != squadNat) {
						tranLR = false;
					}
				}
			}

		}
		boolean result = false;

		if (sourceList != freeList.freeList && targetList != freeList.freeList) {
			if (sourceList == targetList) {
				if (squadS < 67 || (squadS > 74 && squadS < 213)) {
					if (indexS != indexT) {
						result = true;
					}
				}
			} else if (sourceList == selectorL.getSquadList()
					&& targetList == selectorR.getSquadList() && tranLR
					&& indexS != 0) {
				result = true;
			} else if (sourceList == selectorR.getSquadList()
					&& targetList == selectorL.getSquadList() && tranRL
					&& indexS != 0) {
				result = true;
			}
		} else if (sourceList == freeList.freeList
				&& targetList == selectorL.getSquadList() && tranFL) {
			result = true;
		} else if (sourceList == freeList.freeList
				&& targetList == selectorR.getSquadList() && tranFR) {
			result = true;
		} else if (sourceList == selectorL.getSquadList()
				&& targetList == freeList.freeList && relL) {
			result = true;
		} else if (sourceList == selectorR.getSquadList()
				&& targetList == freeList.freeList && relR) {
			result = true;
		}
		return result;
	}

	private void transferFL(int index) {
		int adr = ((Player) (selectorL.getSquadList().getSelectedValue())).adr;
		int ti = selectorL.getTeamBox().getSelectedIndex();
		int n = -1;
		if (ti >= 75 && ti < 213 && autoRel.isSelected()) {
			n = clubRelease(index, true);
		}
		of.getData()[adr] = Bits.toByte(index);
		of.getData()[adr + 1] = Bits.toByte(index >>> 8);
		if (of.getData()[getNumAdr(adr)] == -1) {
			of.getData()[getNumAdr(adr)] = getNextNum(ti);
		}
		if (selectorL.getSquadList().getSelectedIndex() > 10) {
			Squads.tidy(of, ti);
		}
		refreshLists();
		if (n != -1) {
			selectorR.getTeamBox().setSelectedIndex(n);
			selectorR.getPosList().clearSelection();
			selectorR.getPosList().setSelectedIndex(releasedIndex);
		}
	}

	private void transferFR(int index) {
		int adr = ((Player) (selectorR.getSquadList().getSelectedValue())).adr;
		int ti = selectorR.getTeamBox().getSelectedIndex();
		int n = -1;
		if (ti >= 75 && ti < 213 && autoRel.isSelected()) {
			n = clubRelease(index, true);
		}
		of.getData()[adr] = Bits.toByte(index);
		of.getData()[adr + 1] = Bits.toByte(index >>> 8);
		if (of.getData()[getNumAdr(adr)] == -1) {
			of.getData()[getNumAdr(adr)] = getNextNum(ti);
		}
		if (selectorR.getSquadList().getSelectedIndex() > 10) {
			Squads.tidy(of, ti);
		}
		refreshLists();
		if (n != -1) {
			selectorL.getTeamBox().setSelectedIndex(n);
			selectorL.getPosList().clearSelection();
			selectorL.getPosList().setSelectedIndex(releasedIndex);
		}
	}

	private void transferLR(Player player) {
		int adrR = ((Player) (selectorR.getSquadList().getSelectedValue())).adr;
		int index = player.index;
		if (index != 0) {
			int tiR = selectorR.getTeamBox().getSelectedIndex();
			int tiL = selectorL.getTeamBox().getSelectedIndex();
			int n = -1;
			if (tiR >= 75 && tiR < 213 && autoRel.isSelected()) {
				n = clubRelease(index, true);
			}

			of.getData()[adrR] = Bits.toByte(index);
			of.getData()[adrR + 1] = Bits.toByte(index >>> 8);
			if (of.getData()[getNumAdr(adrR)] == -1) {
				of.getData()[getNumAdr(adrR)] = getNextNum(tiR);
			}
			if (selectorR.getSquadList().getSelectedIndex() > 10) {
				Squads.tidy(of, selectorR.getTeamBox().getSelectedIndex());
			}

			refreshLists();
			if (n != -1 && (tiL < 75 || tiL > 210)) {
				selectorL.getTeamBox().setSelectedIndex(n);
				selectorL.getPosList().clearSelection();
				selectorL.getPosList().setSelectedIndex(releasedIndex);
			}
		}
	}

	private void transferRL(Player player) {
		int adrL = ((Player) (selectorL.getSquadList().getSelectedValue())).adr;
		int index = player.index;
		if (index != 0) {
			int tiL = selectorL.getTeamBox().getSelectedIndex();
			int tiR = selectorR.getTeamBox().getSelectedIndex();
			int n = -1;
			if (tiL >= 75 && tiL < 213 && autoRel.isSelected()) {
				n = clubRelease(index, true);
			}

			of.getData()[adrL] = Bits.toByte(index);
			of.getData()[adrL + 1] = Bits.toByte(index >>> 8);
			if (of.getData()[getNumAdr(adrL)] == -1) {
				of.getData()[getNumAdr(adrL)] = getNextNum(tiL);
			}
			if (selectorL.getSquadList().getSelectedIndex() > 10) {
				Squads.tidy(of, selectorL.getTeamBox().getSelectedIndex());
			}

			refreshLists();
			if (n != -1 && (tiR < 75 || tiR > 210)) {
				selectorR.getTeamBox().setSelectedIndex(n);
				selectorR.getPosList().clearSelection();
				selectorR.getPosList().setSelectedIndex(releasedIndex);
			}
		}
	}

	private void transferS(
			Player playerS, Player playerT, int tiS, int tiT,
			JList sourceList, JList targetList) {
		int adrS = playerS.adr;
		int indexS = playerS.index;
		int adrT = playerT.adr;
		int indexT = playerT.index;

		of.getData()[adrS] = Bits.toByte(indexT);
		of.getData()[adrS + 1] = Bits.toByte(indexT >>> 8);
		of.getData()[adrT] = Bits.toByte(indexS);
		of.getData()[adrT + 1] = Bits.toByte(indexS >>> 8);

		if (tiS == tiT) {
			byte t = of.getData()[getNumAdr(adrT)];
			of.getData()[getNumAdr(adrT)] = of.getData()[getNumAdr(adrS)];
			of.getData()[getNumAdr(adrS)] = t;
		}

		if (indexS == 0 || indexT == 0) {
			if (sourceIndex > 10) {
				Squads.tidy(of, tiS);
			} else {
				if (autoRep.isSelected()) {
					Squads.tidy11(
							of,
							tiS,
							sourceIndex,
							((SelectByTeam) (sourceList.getParent())).getPosList().posNum[sourceIndex]);
				}
			}
			if (targetList.getSelectedIndex() > 10) {
				Squads.tidy(of, tiT);
			} else {
				if (autoRep.isSelected() && sourceList != targetList) {
					Squads.tidy11(
							of,
							tiT,
							targetList.getSelectedIndex(),
							((SelectByTeam) (targetList.getParent())).getPosList().posNum[targetList
									.getSelectedIndex()]);
				}
			}
		}

		refreshLists();
	}

	private void tranRelL(Player player, int si) {
		int adr = player.adr;
		of.getData()[adr] = 0;
		of.getData()[adr + 1] = 0;
		of.getData()[getNumAdr(adr)] = -1;
		if (si > 10) {
			Squads.tidy(of, selectorL.getTeamBox().getSelectedIndex());
		} else {
			if (autoRep.isSelected()) {
				Squads.tidy11(of, selectorL.getTeamBox().getSelectedIndex(), si,
						selectorL.getPosList().posNum[si]);
			}
		}
		refreshLists();
	}

	private void tranRelR(Player player, int si) {
		int adr = player.adr;
		of.getData()[adr] = 0;
		of.getData()[adr + 1] = 0;
		of.getData()[getNumAdr(adr)] = -1;
		if (si > 10) {
			Squads.tidy(of, selectorR.getTeamBox().getSelectedIndex());
		} else {
			if (autoRep.isSelected()) {
				Squads.tidy11(of, selectorR.getTeamBox().getSelectedIndex(), si,
						selectorR.getPosList().posNum[si]);
			}
		}
		refreshLists();
	}

	private void addListen() {
		selectorL.getSquadList().addListSelectionListener(nameEditor);
		selectorR.getSquadList().addListSelectionListener(nameEditor);
		freeList.freeList.addListSelectionListener(nameEditor);
		selectorL.getSquadList().addListSelectionListener(shirtEditor);
		selectorR.getSquadList().addListSelectionListener(shirtEditor);
		freeList.freeList.addListSelectionListener(shirtEditor);
		selectorL.getNumList().addListSelectionListener(numEditor);
		selectorR.getNumList().addListSelectionListener(numEditor);
	}

	private void removeListen() {
		selectorL.getSquadList().removeListSelectionListener(nameEditor);
		selectorR.getSquadList().removeListSelectionListener(nameEditor);
		freeList.freeList.removeListSelectionListener(nameEditor);
		selectorL.getSquadList().removeListSelectionListener(shirtEditor);
		selectorR.getSquadList().removeListSelectionListener(shirtEditor);
		freeList.freeList.removeListSelectionListener(shirtEditor);
		selectorL.getNumList().removeListSelectionListener(numEditor);
		selectorR.getNumList().removeListSelectionListener(numEditor);
	}

}
