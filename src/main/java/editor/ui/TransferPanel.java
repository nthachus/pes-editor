package editor.ui;

import editor.data.*;
import editor.util.Bits;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.swing.JList;
import editor.util.swing.JTextFieldLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TransferPanel extends JPanel
		implements MouseListener, DropTargetListener, DragSourceListener, DragGestureListener {
	private static final Logger log = LoggerFactory.getLogger(TransferPanel.class);

	private final OptionFile of;
	private final PlayerDialog playerDia;
	private final FormationDialog teamDia;

	private volatile int releasedIndex = 0;
	private volatile Component sourceComp = null;
	private volatile int sourceIndex = -1;
	private volatile int compIndex = 0;
	private volatile int lastIndex = 0;

	public TransferPanel(OptionFile of, PlayerDialog pd, FormationDialog td) {
		super();

		if (null == of) throw new NullPointerException("of");
		if (null == pd) throw new NullPointerException("pd");
		if (null == td) throw new NullPointerException("td");
		this.of = of;
		this.playerDia = pd;
		this.teamDia = td;

		initComponents();
	}

	//region Initialize the GUI components

	private/* final*/ SelectByTeam selectorL;
	private/* final*/ SelectByTeam selectorR;
	private/* final*/ SelectByNation freeList;
	private/* final*/ NameTextField nameEditor;
	private/* final*/ NumTextField numEditor;
	private/* final*/ InfoPanel infoPanel;
	private/* final*/ ShirtTextField shirtEditor;
	private/* final*/ JCheckBox autoRelease;
	private/* final*/ JCheckBox autoGaps;
	private/* final*/ JCheckBox safeMode;

	private void initComponents() {
		autoRelease = new JCheckBox(Resources.getMessage("transfer.autoRelease"));
		autoRelease.setToolTipText(Resources.getMessage("transfer.autoRelease.tip"));
		autoRelease.setSelected(true);

		autoGaps = new JCheckBox(Resources.getMessage("transfer.autoGaps"));
		autoGaps.setToolTipText(Resources.getMessage("transfer.autoGaps.tip"));
		autoGaps.setSelected(false);

		safeMode = new JCheckBox(Resources.getMessage("transfer.safeMode"));
		safeMode.setToolTipText(Resources.getMessage("transfer.safeMode.tip"));
		safeMode.setSelected(true);

		JButton compareBtn = new JButton(Resources.getMessage("transfer.compare"));
		compareBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compareStats();
			}
		});

		freeList = new SelectByNation(of);
		freeList.getFreeList().setToolTipText(Resources.getMessage("transfer.freeList.tip"));
		selectorL = new SelectByTeam(of, true);
		selectorL.getSquadList().setToolTipText(Resources.getMessage("transfer.selectorL.tip"));
		selectorR = new SelectByTeam(of, true);
		selectorR.getSquadList().setToolTipText(Resources.getMessage("transfer.selectorR.tip"));

		nameEditor = new NameTextField(this);
		numEditor = new NumTextField(this);
		shirtEditor = new ShirtTextField(this);

		infoPanel = new InfoPanel(of, selectorL);

		addListListeners();
		freeList.getFreeList().addMouseListener(this);
		selectorL.getSquadList().addMouseListener(this);
		selectorR.getSquadList().addMouseListener(this);

		new DropTarget(freeList.getFreeList(), this);
		new DropTarget(selectorL.getSquadList(), this);
		new DropTarget(selectorR.getSquadList(), this);
		DragSource sourceF = new DragSource();
		sourceF.createDefaultDragGestureRecognizer(freeList.getFreeList(), DnDConstants.ACTION_MOVE, this);
		DragSource sourceL = new DragSource();
		sourceL.createDefaultDragGestureRecognizer(selectorL.getSquadList(), DnDConstants.ACTION_MOVE, this);
		DragSource sourceR = new DragSource();
		sourceR.createDefaultDragGestureRecognizer(selectorR.getSquadList(), DnDConstants.ACTION_MOVE, this);

		JPanel editPanel = new JPanel(new GridLayout(0, 1));
		editPanel.add(nameEditor);
		editPanel.add(shirtEditor);

		JPanel optPanel = new JPanel(new GridLayout(0, 1));
		optPanel.add(autoRelease);
		optPanel.add(autoGaps);
		optPanel.add(safeMode);

		JPanel editOptPan = new JPanel();
		editOptPan.add(numEditor);
		editOptPan.add(editPanel);
		editOptPan.add(optPanel);

		JPanel editOptInfoPan = new JPanel(new BorderLayout());
		editOptInfoPan.add(editOptPan, BorderLayout.NORTH);
		editOptInfoPan.add(infoPanel, BorderLayout.CENTER);
		editOptInfoPan.add(compareBtn, BorderLayout.SOUTH);

		JPanel lPanel = new JPanel(new BorderLayout());
		lPanel.add(selectorL, BorderLayout.CENTER);

		JPanel rPanel = new JPanel(new BorderLayout());
		rPanel.add(selectorR, BorderLayout.CENTER);

		JPanel listPane = new JPanel(new GridLayout(0, 3));
		listPane.add(freeList);
		listPane.add(lPanel);
		listPane.add(rPanel);

		add(listPane);
		add(editOptInfoPan);
	}

	//endregion

	private void compareStats() {
		if (compIndex == 0) {
			compIndex = lastIndex;
			if (nameEditor.source == EventSource.squadLeft) {
				selectTeamPos(selectorL);
			} else if (nameEditor.source == EventSource.squadRight) {
				selectTeamPos(selectorR);
			}
		} else {
			compIndex = 0;
			selectorL.getPosList().clearSelection();
			selectorR.getPosList().clearSelection();
		}
		infoPanel.refresh(lastIndex, compIndex);
	}

	private static boolean isValidSquad(int squadS) {
		return (squadS >= 0 && squadS < Squads.FIRST_EDIT_NATION)
				|| (squadS >= Squads.FIRST_CLUB && squadS < Squads.TOTAL);
	}

	private static void selectTeamPos(SelectByTeam selector) {
		int squadS = selector.getTeamBox().getSelectedIndex();
		if (isValidSquad(squadS)) {
			SquadList squadList = selector.getSquadList();
			int squadIndex = squadList.getSelectedIndex();
			selector.getPosList().selectPos(squadList, squadIndex);
		}
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
		freeList.getFreeList().refresh(freeList.getNationBox().getSelectedIndex(), freeList.isAlphaOrder());

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

	private static int getNumberAdr(int adr) {
		return Squads.NATION_NUM_ADR + (adr - Squads.NATION_ADR) / 2;
	}

	private int getNumberAdr(EventSource source, int index) {
		SelectByTeam selector = (source == EventSource.squadLeft) ? selectorL : selectorR;
		if (index < 0 || index >= selector.getSquadList().getModel().getSize())
			return -1;

		int adr = selector.getSquadList().getModel().getElementAt(index).getSlotAdr();
		return getNumberAdr(adr);
	}

	private String getShirtNumber(EventSource source, int index) {
		int adr = getNumberAdr(source, index);
		if (adr < 0) return "";

		int shirt = Bits.toInt(of.getData()[adr]) + 1;
		return (shirt > 0xFF) ? "" : Integer.toString(shirt);
	}

	private void setShirtNumber(EventSource source, int index, int newShirt) {
		int adr = getNumberAdr(source, index);
		if (adr < 0) return;

		int shirt = Bits.toInt(of.getData()[adr]) + 1;
		if (shirt <= 0xFF) {
			of.getData()[adr] = Bits.toByte(newShirt - 1);
		}
	}

	//region Mouse Events

	private static boolean isValidSquadTeam(int team) {
		return (team >= 0 && team < Squads.FIRST_EDIT_NATION)
				|| (team >= Squads.FIRST_CLUB && team < Squads.LAST_CLUB);
	}

	public void mousePressed(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");
		if (e.getButton() != MouseEvent.BUTTON1 || !e.isControlDown())
			return;

		if (!(e.getSource() instanceof SquadList))
			return;

		e.consume();

		SquadList list = (SquadList) e.getSource();
		showFormationDialog(list.getTeam());
	}

	private void showFormationDialog(int team) {
		if (!isValidSquadTeam(team))
			return;

		int tt = team;
		if (tt >= Squads.FIRST_CLUB) tt -= Squads.EDIT_TEAM_COUNT;
		teamDia.show(tt, selectorL.getTeamBox().getItemAt(team));

		Squads.fixFormation(of, team, false);
		refreshLists();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");

		int clicks = e.getClickCount();
		if (e.getButton() == MouseEvent.BUTTON1 && clicks > 1) {
			if (e.getSource() instanceof JList<?>) {

				JList list = (JList) e.getSource();
				Player p = (Player) list.getSelectedValue();
				int pId = p.getIndex();
				if (pId > 0) {

					boolean toEnable = (!safeMode.isSelected() || !inNationSquad(pId));
					playerDia.getGeneralPan().getNationBox().setEnabled(toEnable);
					playerDia.show(p);

					refreshLists();
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3 && clicks == 1) {
			if (e.getSource() instanceof SquadList) {

				SquadList list = (SquadList) e.getSource();
				showFormationDialog(list.getTeam());
			}
		}
	}

	//endregion

	private int clubRelease(int p, boolean rel) {// TODO: !!!
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
					of.getData()[getNumberAdr(a)] = -1;
					if (sp >= 22) {
						Squads.tidy(of, sqi);
					} else {
						if (autoGaps.isSelected()) {
							int t = sqi;
							if (t > 74) {
								t = t - 8;
							}
							Squads.tidy11(of, sqi, sp / 2, Formations.getPosition(
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
		for (byte i = 0; num == -1 && i < Stats.MAX_STAT99; i++) {
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

	private boolean inNationSquad(int pi) {
		for (int i = 0; i < 67; i++) {
			if (inSquad(i, pi))
				return true;
		}
		return false;
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

	//region Drag and Drop

	public void dragEnter(DropTargetDragEvent evt) {
	}

	public void dragExit(DropTargetEvent evt) {
	}

	public void dragOver(DropTargetDragEvent evt) {
		JList targetList = (JList) (evt.getDropTargetContext().getComponent());
		int i = targetList.locationToIndex(evt.getLocation());
		Player p;
		if (i != -1) {
			p = (Player) (targetList.getModel().getElementAt(i));
		} else {
			p = new Player(of, 0);
		}
		boolean chk = checkSafeDrag(safeMode.isSelected(), targetList, p);
		targetList.setSelectedIndex(i);
		if (chk) {
			evt.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			evt.rejectDrag();
		}
	}

	public void drop(DropTargetDropEvent evt) {
		Transferable transferable = evt.getTransferable();
		if (transferable.isDataFlavorSupported(PlayerTransferable.getDataFlavor())) {
			JList sourceList = (JList) sourceComp;
			JList targetList = (JList) (evt.getDropTargetContext().getComponent());
			Player sourcePlayer = (Player) (sourceList.getModel().getElementAt(sourceIndex));
			int indexS = sourcePlayer.getIndex();
			Player targetPlayer;
			int indexT;
			if (targetList.getSelectedIndex() != -1) {
				targetPlayer = (Player) (targetList.getSelectedValue());
				indexT = targetPlayer.getIndex();
			} else {
				targetPlayer = new Player(of, 0);
				indexT = 0;
			}

			if (sourceList != freeList.getFreeList()
					&& targetList != freeList.getFreeList()) {
				int squadS = ((SelectByTeam) (sourceList.getParent())).getTeamBox()
						.getSelectedIndex();
				int squadT = ((SelectByTeam) (targetList.getParent())).getTeamBox()
						.getSelectedIndex();
				if (sourceList == targetList) {
					if (isValidSquad(squadS) || squadS == Squads.TOTAL) {
						if (indexS != indexT) {
							evt.acceptDrop(DnDConstants.ACTION_MOVE);
							transferS(sourcePlayer, targetPlayer, squadS, squadT, sourceList, targetList);
						}
					}
				} else if (sourceList == selectorL.getSquadList()
						&& targetList == selectorR.getSquadList()) {
					evt.acceptDrop(DnDConstants.ACTION_MOVE);
					transferLR(sourcePlayer);
				} else if (sourceList == selectorR.getSquadList()
						&& targetList == selectorL.getSquadList()) {
					evt.acceptDrop(DnDConstants.ACTION_MOVE);
					transferRL(sourcePlayer);
				}
			} else if (sourceList == freeList.getFreeList()
					&& targetList == selectorL.getSquadList()) {
				evt.acceptDrop(DnDConstants.ACTION_MOVE);
				transferFL(indexS);
			} else if (sourceList == freeList.getFreeList()
					&& targetList == selectorR.getSquadList()) {
				evt.acceptDrop(DnDConstants.ACTION_MOVE);
				transferFR(indexS);
			} else if (sourceList == selectorL.getSquadList()
					&& targetList == freeList.getFreeList()) {
				evt.acceptDrop(DnDConstants.ACTION_MOVE);
				transferRelease(selectorL, sourcePlayer, sourceIndex);
			} else if (sourceList == selectorR.getSquadList()
					&& targetList == freeList.getFreeList()) {
				evt.acceptDrop(DnDConstants.ACTION_MOVE);
				transferRelease(selectorR, sourcePlayer, sourceIndex);
			} else {
				evt.rejectDrop();
			}

			evt.getDropTargetContext().dropComplete(true);
		} else {
			evt.rejectDrop();
		}
	}

	public void dropActionChanged(DropTargetDragEvent evt) {
	}

	public void dragGestureRecognized(DragGestureEvent evt) {
		sourceComp = evt.getComponent();
		if (sourceComp instanceof JList) {
			JList list = (JList) sourceComp;
			sourceIndex = list.getSelectedIndex();
			Player p = (Player) list.getSelectedValue();
			if (sourceIndex != -1 && p.getIndex() != 0) {
				removeListListeners();
				lastIndex = 0;
				compIndex = 0;
				infoPanel.refresh(lastIndex, compIndex);
				nameEditor.setText("");
				shirtEditor.setText("");
				nameEditor.source = null;
				shirtEditor.source = null;
				PlayerTransferable playerTran = new PlayerTransferable(p);
				if (list != freeList.getFreeList()) {
					int squadS = ((SelectByTeam) (list.getParent())).getTeamBox().getSelectedIndex();
					if (isValidSquad(squadS) || squadS == Squads.TOTAL) {
						if (list == selectorL.getSquadList()) {
							selectorL.getPosList().selectPos(selectorL.getSquadList(),
									selectorL.getSquadList().getSelectedIndex());
						} else if (list == selectorR.getSquadList()) {
							selectorR.getPosList().selectPos(selectorR.getSquadList(),
									selectorR.getSquadList().getSelectedIndex());
						}
					}
				}
				evt.getDragSource().startDrag(evt, null, playerTran, this);

			}
		}
	}

	public void dragDropEnd(DragSourceDropEvent evt) {
		if (!evt.getDropSuccess()) {
			refreshLists();
		}
		addListListeners();
	}

	public void dragEnter(DragSourceDragEvent evt) {
	}

	public void dragExit(DragSourceEvent evt) {
	}

	public void dragOver(DragSourceDragEvent evt) {
	}

	public void dropActionChanged(DragSourceDragEvent evt) {
	}

	//endregion

	private boolean checkSafeDrag(boolean safe, JList targetList, Player targetPlayer) {
		boolean tranFL = true;
		boolean tranFR = true;
		boolean tranLR = true;
		boolean tranRL = true;
		boolean relL = true;
		boolean relR = true;
		boolean fEmpty = true;
		boolean lEmpty = true;
		boolean rEmpty = true;

		JList sourceList = (JList) sourceComp;
		int indexS = ((Player) (sourceList.getModel().getElementAt(sourceIndex))).getIndex();
		int indexT = targetPlayer.getIndex();
		int squadS = -1;

		int indexF = 0;
		if (sourceList == freeList.getFreeList()) {
			indexF = indexS;
			fEmpty = false;
		} else if (targetList == freeList.getFreeList()) {
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

		int squadL = selectorL.getTeamBox().getSelectedIndex();
		int squadR = selectorR.getTeamBox().getSelectedIndex();
		if (safe) {
			int minSizeL = 16;
			int minSizeR = 16;
			if (squadL < 75) {
				minSizeL = 23;
			}
			if (squadR < 75) {
				minSizeR = 23;
			}

			if (indexF >= Player.FIRST_YOUNG && indexF < Player.FIRST_EDIT) {
				tranFL = false;
				tranFR = false;
			}

			if (indexF >= Player.FIRST_ML && indexF < Player.FIRST_SHOP) {
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
				if (autoRelease.isSelected()) {
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
				if (autoRelease.isSelected()) {
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
					if (autoRelease.isSelected() && squadL > 74) {
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

				if (!autoRelease.isSelected() && squadL > 74 && squadL < 213) {
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
					if (autoRelease.isSelected() && squadR > 74) {
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

				if (!autoRelease.isSelected() && squadR > 74 && squadR < 213) {
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
				int squadNat = Squads.getNationForTeam(squadL);
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
				int squadNat = Squads.getNationForTeam(squadR);
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

		if (sourceList != freeList.getFreeList() && targetList != freeList.getFreeList()) {
			if (sourceList == targetList) {
				if (isValidSquad(squadS) || squadS == Squads.TOTAL) {
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
		} else if (sourceList == freeList.getFreeList()
				&& targetList == selectorL.getSquadList() && tranFL) {
			result = true;
		} else if (sourceList == freeList.getFreeList()
				&& targetList == selectorR.getSquadList() && tranFR) {
			result = true;
		} else if (sourceList == selectorL.getSquadList()
				&& targetList == freeList.getFreeList() && relL) {
			result = true;
		} else if (sourceList == selectorR.getSquadList()
				&& targetList == freeList.getFreeList() && relR) {
			result = true;
		}
		return result;
	}

	private void transferFL(int index) {
		int adr = selectorL.getSquadList().getSelectedValue().getSlotAdr();
		int ti = selectorL.getTeamBox().getSelectedIndex();
		int n = -1;
		if (ti >= 75 && ti < 213 && autoRelease.isSelected()) {
			n = clubRelease(index, true);
		}
		of.getData()[adr] = Bits.toByte(index);
		of.getData()[adr + 1] = Bits.toByte(index >>> 8);
		if (of.getData()[getNumberAdr(adr)] == -1) {
			of.getData()[getNumberAdr(adr)] = getNextNum(ti);
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
		int adr = selectorR.getSquadList().getSelectedValue().getSlotAdr();
		int ti = selectorR.getTeamBox().getSelectedIndex();
		int n = -1;
		if (ti >= 75 && ti < 213 && autoRelease.isSelected()) {
			n = clubRelease(index, true);
		}
		of.getData()[adr] = Bits.toByte(index);
		of.getData()[adr + 1] = Bits.toByte(index >>> 8);
		if (of.getData()[getNumberAdr(adr)] == -1) {
			of.getData()[getNumberAdr(adr)] = getNextNum(ti);
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
		int adrR = selectorR.getSquadList().getSelectedValue().getSlotAdr();
		int index = player.getIndex();
		if (index != 0) {
			int tiR = selectorR.getTeamBox().getSelectedIndex();
			int tiL = selectorL.getTeamBox().getSelectedIndex();
			int n = -1;
			if (tiR >= 75 && tiR < 213 && autoRelease.isSelected()) {
				n = clubRelease(index, true);
			}

			of.getData()[adrR] = Bits.toByte(index);
			of.getData()[adrR + 1] = Bits.toByte(index >>> 8);
			if (of.getData()[getNumberAdr(adrR)] == -1) {
				of.getData()[getNumberAdr(adrR)] = getNextNum(tiR);
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
		int adrL = selectorL.getSquadList().getSelectedValue().getSlotAdr();
		int index = player.getIndex();
		if (index != 0) {
			int tiL = selectorL.getTeamBox().getSelectedIndex();
			int tiR = selectorR.getTeamBox().getSelectedIndex();
			int n = -1;
			if (tiL >= 75 && tiL < 213 && autoRelease.isSelected()) {
				n = clubRelease(index, true);
			}

			of.getData()[adrL] = Bits.toByte(index);
			of.getData()[adrL + 1] = Bits.toByte(index >>> 8);
			if (of.getData()[getNumberAdr(adrL)] == -1) {
				of.getData()[getNumberAdr(adrL)] = getNextNum(tiL);
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
		int adrS = playerS.getSlotAdr();
		int indexS = playerS.getIndex();
		int adrT = playerT.getSlotAdr();
		int indexT = playerT.getIndex();

		of.getData()[adrS] = Bits.toByte(indexT);
		of.getData()[adrS + 1] = Bits.toByte(indexT >>> 8);
		of.getData()[adrT] = Bits.toByte(indexS);
		of.getData()[adrT + 1] = Bits.toByte(indexS >>> 8);

		if (tiS == tiT) {
			byte t = of.getData()[getNumberAdr(adrT)];
			of.getData()[getNumberAdr(adrT)] = of.getData()[getNumberAdr(adrS)];
			of.getData()[getNumberAdr(adrS)] = t;
		}

		if (indexS == 0 || indexT == 0) {
			if (sourceIndex > 10) {
				Squads.tidy(of, tiS);
			} else {
				if (autoGaps.isSelected()) {
					Squads.tidy11(
							of,
							tiS,
							sourceIndex,
							((SelectByTeam) (sourceList.getParent())).getPosList().getPosNum(sourceIndex));
				}
			}
			if (targetList.getSelectedIndex() > 10) {
				Squads.tidy(of, tiT);
			} else {
				if (autoGaps.isSelected() && sourceList != targetList) {
					Squads.tidy11(
							of,
							tiT,
							targetList.getSelectedIndex(),
							((SelectByTeam) (targetList.getParent())).getPosList().getPosNum(
									targetList.getSelectedIndex()));
				}
			}
		}

		refreshLists();
	}

	private void transferRelease(SelectByTeam selector, Player player, int sourceIndex) {
		int adr = player.getSlotAdr();
		Bits.toBytes((short) 0, of.getData(), adr);
		int numAdr = getNumberAdr(adr);
		of.getData()[numAdr] = -1;

		if (sourceIndex >= Formations.PLAYER_COUNT) {
			int teamIdx = selector.getTeamBox().getSelectedIndex();
			Squads.tidy(of, teamIdx);

		} else if (autoGaps.isSelected()) {
			int teamIdx = selector.getTeamBox().getSelectedIndex();
			int posNum = selector.getPosList().getPosNum(sourceIndex);
			Squads.tidy11(of, teamIdx, sourceIndex, posNum);
		}

		refreshLists();
	}

	private void addListListeners() {
		selectorL.getSquadList().addListSelectionListener(nameEditor);
		selectorR.getSquadList().addListSelectionListener(nameEditor);
		freeList.getFreeList().addListSelectionListener(nameEditor);

		selectorL.getSquadList().addListSelectionListener(shirtEditor);
		selectorR.getSquadList().addListSelectionListener(shirtEditor);
		freeList.getFreeList().addListSelectionListener(shirtEditor);

		selectorL.getSquadList().addListSelectionListener(numEditor);// NOTE: handle on getNumList()
		selectorR.getSquadList().addListSelectionListener(numEditor);
	}

	private void removeListListeners() {
		selectorL.getSquadList().removeListSelectionListener(nameEditor);
		selectorR.getSquadList().removeListSelectionListener(nameEditor);
		freeList.getFreeList().removeListSelectionListener(nameEditor);

		selectorL.getSquadList().removeListSelectionListener(shirtEditor);
		selectorR.getSquadList().removeListSelectionListener(shirtEditor);
		freeList.getFreeList().removeListSelectionListener(shirtEditor);

		selectorL.getSquadList().removeListSelectionListener(numEditor);
		selectorR.getSquadList().removeListSelectionListener(numEditor);
	}

	//region Nested Classes

	private static enum EventSource {
		freeList,
		squadLeft,
		squadRight
	}

	/**
	 * Player name TextBox.
	 */
	private static class NameTextField extends JTextField implements ListSelectionListener, ActionListener {
		private final TransferPanel owner;
		private volatile EventSource source = null;

		public NameTextField(TransferPanel owner) {
			super(Math.round(0.4f * Player.NAME_LEN));
			this.owner = owner;

			setDocument(new JTextFieldLimit(Player.NAME_LEN));
			setToolTipText(Resources.getMessage("transfer.nameField.tip"));
			addActionListener(this);
		}

		public void valueChanged(ListSelectionEvent evt) {
			if (null == evt) throw new NullPointerException("evt");
			if (evt.getValueIsAdjusting()) return;
			if (!(evt.getSource() instanceof JList<?>)) throw new IllegalArgumentException("evt");

			JList listS = (JList) evt.getSource();
			if (listS.isSelectionEmpty()) {
				setText("");
				source = null;
				owner.lastIndex = 0;

			} else {
				if (listS == owner.selectorL.getSquadList()) {
					source = EventSource.squadLeft;
				} else if (listS == owner.selectorR.getSquadList()) {
					source = EventSource.squadRight;
				} else {
					source = EventSource.freeList;
				}

				Player p = (Player) listS.getSelectedValue();
				setText(p.getIndex() > 0 ? p.getName() : "");
				selectAll();

				owner.lastIndex = p.getIndex();
				owner.infoPanel.refresh(owner.lastIndex, owner.compIndex);
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (null == source) return;

			String text = getText();
			if (Strings.isEmpty(text) || text.length() > Player.NAME_LEN)
				return;

			JList listS;
			if (source == EventSource.squadLeft) {
				listS = owner.selectorL.getSquadList();
			} else if (source == EventSource.squadRight) {
				listS = owner.selectorR.getSquadList();
			} else {
				listS = owner.freeList.getFreeList();
			}

			int idx = listS.getSelectedIndex();
			Player p = (Player) listS.getSelectedValue();
			if (!text.equals(p.getName())) {
				p.setName(text);
				String shirt = Player.buildShirtName(text);
				p.setShirtName(shirt);

				owner.refreshLists();
			}

			if (idx < listS.getModel().getSize() - 1) {
				if (source != EventSource.freeList || !owner.freeList.isAlphaOrder()) {

					listS.setSelectedIndex(idx + 1);
					if (source == EventSource.freeList)
						listS.ensureIndexIsVisible(idx + 1);
				}
			}
		}
	}

	private static class NumTextField extends JTextField implements ListSelectionListener, ActionListener {
		private final TransferPanel owner;
		private volatile EventSource source = null;

		public NumTextField(TransferPanel owner) {
			super(2);
			this.owner = owner;

			setDocument(new JTextFieldLimit(3));
			setToolTipText(Resources.getMessage("transfer.numField.tip"));
			addActionListener(this);
		}

		public void valueChanged(ListSelectionEvent evt) {
			if (null == evt) throw new NullPointerException("evt");
			if (evt.getValueIsAdjusting()) return;
			if (!(evt.getSource() instanceof JList<?>)) throw new IllegalArgumentException("evt");

			JList listS = (JList) evt.getSource();
			if (listS.isSelectionEmpty()) {
				setText("");
				source = null;

			} else {
				if (listS == owner.selectorL.getSquadList()) {
					source = EventSource.squadLeft;
				} else {
					source = EventSource.squadRight;
				}

				String squadNum = owner.getShirtNumber(source, listS.getSelectedIndex());
				setText(squadNum);

				owner.selectorL.getNumList().clearSelection();
				owner.selectorR.getNumList().clearSelection();
				selectAll();
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (null == source) return;

			int num;
			try {
				num = Integer.parseInt(getText());
			} catch (NumberFormatException nfe) {
				num = -1;
			}
			if (num <= 0 || num > Stats.MAX_STAT99) {
				setText("");
				return;
			}

			JList listS;
			if (source == EventSource.squadLeft) {
				listS = owner.selectorL.getSquadList();
			} else {
				listS = owner.selectorR.getSquadList();
			}

			int idx = listS.getSelectedIndex();
			owner.setShirtNumber(source, idx, num);

			owner.selectorR.getNumList().refresh(owner.selectorR.getTeamBox().getSelectedIndex());
			owner.selectorL.getNumList().refresh(owner.selectorL.getTeamBox().getSelectedIndex());

			if (idx < listS.getModel().getSize() - 1)
				listS.setSelectedIndex(idx + 1);
		}
	}

	private static class ShirtTextField extends JTextField implements ListSelectionListener, ActionListener {
		private final TransferPanel owner;
		private volatile EventSource source = null;

		public ShirtTextField(TransferPanel owner) {
			super(Math.round(0.8f * Player.SHIRT_NAME_LEN));
			this.owner = owner;

			setDocument(new JTextFieldLimit(Player.SHIRT_NAME_LEN));
			setToolTipText(Resources.getMessage("transfer.shirtName.tip"));
			addActionListener(this);
		}

		public void valueChanged(ListSelectionEvent evt) {
			if (null == evt) throw new NullPointerException("evt");
			if (evt.getValueIsAdjusting()) return;
			if (!(evt.getSource() instanceof JList<?>)) throw new IllegalArgumentException("evt");

			JList listS = (JList) evt.getSource();
			if (listS.isSelectionEmpty()) {
				setText("");
				source = null;

			} else {
				if (listS == owner.selectorL.getSquadList()) {
					source = EventSource.squadLeft;
				} else if (listS == owner.selectorR.getSquadList()) {
					source = EventSource.squadRight;
				} else {
					source = EventSource.freeList;
				}

				Player p = (Player) listS.getSelectedValue();
				setText(p.getShirtName());
				selectAll();
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (null == source) return;

			String text = getText();
			if (Strings.isEmpty(text) || text.length() > Player.SHIRT_NAME_LEN)
				return;

			JList listS;
			if (source == EventSource.squadLeft) {
				listS = owner.selectorL.getSquadList();
			} else if (source == EventSource.squadRight) {
				listS = owner.selectorR.getSquadList();
			} else {
				listS = owner.freeList.getFreeList();
			}

			int idx = -1;
			if (source != EventSource.freeList) {
				idx = listS.getSelectedIndex();
				if (idx >= listS.getModel().getSize() - 1)
					idx = -1;
			}

			Player p = (Player) listS.getSelectedValue();
			p.setShirtName(text);

			owner.refreshLists();
			if (idx >= 0)
				listS.setSelectedIndex(idx + 1);
		}
	}

	//endregion

}
