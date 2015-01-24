package editor.ui;

import editor.data.*;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Resources;
import editor.util.Strings;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class TransferPanel extends JPanel
		implements ActionListener, MouseListener, DropTargetListener, DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 4733686475112963352L;
	private static final Logger log = LoggerFactory.getLogger(TransferPanel.class);

	private final OptionFile of;
	private final PlayerDialog playerDia;
	private final FormationDialog teamDia;

	private volatile int releasedIndex = 0;
	private volatile JList sourceList = null;
	private volatile int sourceIndex = -1;
	private volatile int compareIndex = 0;
	private volatile int lastIndex = 0;

	public TransferPanel(OptionFile of, PlayerDialog pd, FormationDialog td) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == pd) {
			throw new NullArgumentException("pd");
		}
		if (null == td) {
			throw new NullArgumentException("td");
		}
		this.of = of;
		this.playerDia = pd;
		this.teamDia = td;

		log.debug("Transfer panel is initializing..");
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
		compareBtn.setActionCommand("Compare");
		compareBtn.addActionListener(this);

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

	public void actionPerformed(ActionEvent evt) {
		log.info("Try to perform Transfer action: {}", (null == evt) ? null : evt.getActionCommand());

		//if ("Compare".equalsIgnoreCase(evt.getActionCommand()))
		compareStats();
	}

	private void compareStats() {
		if (compareIndex == 0) {
			compareIndex = lastIndex;
			if (nameEditor.source == EventSource.squadLeft) {
				selectTeamPos(selectorL);
			} else if (nameEditor.source == EventSource.squadRight) {
				selectTeamPos(selectorR);
			}
		} else {
			compareIndex = 0;
			selectorL.getPosList().clearSelection();
			selectorR.getPosList().clearSelection();
		}
		infoPanel.refresh(lastIndex, compareIndex);
		// DEBUG
		log.debug("Compare Stats completed on last-player: {}, compare-to: {}", lastIndex, compareIndex);
	}

	private static boolean isValidSquad(int squadS) {
		return (squadS >= 0 && squadS < Squads.FIRST_EDIT_NATION)
				|| (squadS >= Squads.FIRST_CLUB && squadS < Squads.TOTAL);
	}

	private static void selectTeamPos(SelectByTeam selector) {
		int squadS = selector.getTeamBox().getSelectedIndex();
		// DEBUG
		log.debug("Try to select position for team: {}", squadS);
		if (isValidSquad(squadS)) {
			SquadList squadList = selector.getSquadList();
			int squadIndex = squadList.getSelectedIndex();
			selector.getPosList().selectPos(squadList, squadIndex);
		}
	}

	public void refresh() {
		log.info("Try to refresh Transfer panel..");

		freeList.refresh();
		selectorL.refresh();
		selectorR.refresh();

		clearForm();
	}

	private void clearForm() {
		nameEditor.setText(Strings.EMPTY);
		numEditor.setText(Strings.EMPTY);
		shirtEditor.setText(Strings.EMPTY);

		compareIndex = 0;
		lastIndex = 0;
		infoPanel.refresh(lastIndex, compareIndex);

		log.debug("Transfer form was cleared");
	}

	public void refreshLists() {
		log.info("Try to refresh lists in Transfer panel..");

		freeList.refreshForNation();
		selectorL.refreshForTeam();
		selectorR.refreshForTeam();

		clearForm();
	}

	private int getNumberAdr(EventSource source, int index) {
		SelectByTeam selector = (source == EventSource.squadLeft) ? selectorL : selectorR;
		if (index < 0 || index >= selector.getSquadList().getModel().getSize()) {
			return -1;
		}

		Player p = (Player) selector.getSquadList().getModel().getElementAt(index);
		return Squads.getNumberAdr(p.getSlotAdr());
	}

	private String getShirtNumber(EventSource source, int index) {
		int adr = getNumberAdr(source, index);
		if (adr < 0) {
			return Strings.EMPTY;
		}

		int shirt = Bits.toInt(of.getData()[adr]) + 1;
		return (shirt > 0xFF) ? Strings.EMPTY : Integer.toString(shirt);
	}

	private void setShirtNumber(EventSource source, int index, int newShirt) {
		int adr = getNumberAdr(source, index);
		if (adr < 0) {
			return;
		}

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
		if (null == e) {
			throw new NullArgumentException("e");
		}
		// DEBUG
		log.debug("Perform mouse-pressed action for button: {}, control: {}, on: {}",
				e.getButton(), e.isControlDown(), Strings.valueOf(e.getSource()));
		if (e.getButton() != MouseEvent.BUTTON1 || !e.isControlDown()) {
			return;
		}

		if (!(e.getSource() instanceof SquadList)) {
			return;
		}

		e.consume();

		SquadList list = (SquadList) e.getSource();
		showFormationDialog(list.getTeam());
	}

	private void showFormationDialog(int team) {
		if (!isValidSquadTeam(team)) {
			return;
		}
		log.info("Try to show Formation dialog for team: {}", team);

		int tt = team;
		if (tt >= Squads.FIRST_CLUB) {
			tt -= Squads.EDIT_TEAM_COUNT;
		}
		teamDia.show(tt, (String) selectorL.getTeamBox().getItemAt(team));

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
		if (null == e) {
			throw new NullArgumentException("e");
		}

		int clicks = e.getClickCount();
		// DEBUG
		log.debug("Perform mouse-clicked action for button: {}, clicks: {}, on: {}",
				e.getButton(), clicks, Strings.valueOf(e.getSource()));
		if (e.getButton() == MouseEvent.BUTTON1 && clicks > 1) {
			if (e.getSource() instanceof JList) {

				JList list = (JList) e.getSource();
				Player p = (Player) list.getSelectedValue();
				int pId = p.getIndex();
				if (pId > 0) {

					boolean toEnable = (!safeMode.isSelected() || !Squads.inNationTeam(of, pId));
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

	private int clubRelease(int player, boolean release) {
		int result = -1;

		for (int c = 0; c < Clubs.TOTAL; c++) {
			for (int sp = 0; sp < Formations.CLUB_TEAM_SIZE; sp++) {

				int squadId = c + Squads.FIRST_CLUB;
				int index = Squads.getTeamPlayer(of, squadId, sp);
				if (index != player) {
					continue;
				}

				if (result < 0) {
					if (!release || sp < Formations.PLAYER_COUNT) {
						result = squadId;
						if (release) {
							releasedIndex = sp;
						}
					}
				}

				if (release) {
					Squads.setTeamPlayer(of, squadId, sp, 0);
					Squads.setTeamSquadNum(of, squadId, sp, -1);

					if (sp >= Formations.PLAYER_COUNT) {
						Squads.tidy(of, squadId);
					} else if (autoGaps.isSelected()) {

						int t = squadId;
						if (t >= Squads.FIRST_CLUB) {
							t -= Squads.EDIT_TEAM_COUNT;
						}
						int pos = Formations.getPosition(of, t, 0, sp);
						Squads.tidy11(of, squadId, sp, pos);
					}
				}
			}
		}

		log.debug("Release ({}) succeeded player: {} to club: {}", release, player, result);
		return result;
	}

	//region Drag and Drop

	public void dragEnter(DropTargetDragEvent evt) {
	}

	public void dragExit(DropTargetEvent evt) {
	}

	public void dragOver(DropTargetDragEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (null == evt.getDropTargetContext()) {
			throw new NullArgumentException("evt.context");
		}
		if (!(evt.getDropTargetContext().getComponent() instanceof JList)) {
			throw new IllegalArgumentException("evt");
		}

		JList targetList = (JList) evt.getDropTargetContext().getComponent();
		int idx = targetList.locationToIndex(evt.getLocation());
		// DEBUG
		log.info("Drag over Players list at: {}", idx);

		Player p;
		if (idx < 0) {
			p = new Player(of, 0);
		} else {
			p = (Player) targetList.getModel().getElementAt(idx);
		}

		boolean safety = isDragSafety(safeMode.isSelected(), targetList, p);
		targetList.setSelectedIndex(idx);

		if (safety) {
			evt.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			evt.rejectDrag();
		}
	}

	public void drop(DropTargetDropEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		Transferable transferable = evt.getTransferable();
		if (null == transferable) {
			throw new NullArgumentException("evt.transferable");
		}

		if (!transferable.isDataFlavorSupported(PlayerTransferable.getDataFlavor())) {
			evt.rejectDrop();
			return;
		}

		if (null == evt.getDropTargetContext()) {
			throw new NullArgumentException("evt.context");
		}
		if (!(evt.getDropTargetContext().getComponent() instanceof JList)) {
			throw new IllegalArgumentException("evt");
		}

		JList targetList = (JList) evt.getDropTargetContext().getComponent();
		Player sourcePlayer = (Player) sourceList.getModel().getElementAt(sourceIndex);
		int playerS = sourcePlayer.getIndex();
		// DEBUG
		log.info("Drop Player [{}] {} from source index {} -> to target List at: {}",
				playerS, sourcePlayer, sourceIndex, targetList.getSelectedIndex());

		Player targetPlayer;
		if (targetList.getSelectedIndex() < 0) {
			targetPlayer = new Player(of, 0);
		} else {
			targetPlayer = (Player) targetList.getSelectedValue();
		}
		int playerT = targetPlayer.getIndex();

		if (sourceList != freeList.getFreeList() && targetList != freeList.getFreeList()) {
			SelectByTeam selectorS = (SelectByTeam) sourceList.getParent();
			SelectByTeam selectorT = (SelectByTeam) targetList.getParent();
			int squadS = selectorS.getTeamBox().getSelectedIndex();
			int squadT = selectorT.getTeamBox().getSelectedIndex();

			if (sourceList == targetList) {
				if (isValidSquad(squadS) || squadS == Squads.TOTAL) {// NOTE: should not need == Squads.TOTAL
					if (playerS != playerT) {
						evt.acceptDrop(DnDConstants.ACTION_MOVE);
						transferSwap(sourcePlayer, targetPlayer, squadS, squadT, sourceList, targetList);
					}
				}
			} else if (sourceList == selectorL.getSquadList() && targetList == selectorR.getSquadList()) {
				evt.acceptDrop(DnDConstants.ACTION_MOVE);
				transferBetweenLR(selectorL, selectorR, sourcePlayer);

			} else if (sourceList == selectorR.getSquadList() && targetList == selectorL.getSquadList()) {
				evt.acceptDrop(DnDConstants.ACTION_MOVE);
				transferBetweenLR(selectorR, selectorL, sourcePlayer);
			}
		} else if (sourceList == freeList.getFreeList() && targetList == selectorL.getSquadList()) {
			evt.acceptDrop(DnDConstants.ACTION_MOVE);
			transferFromFree(selectorL, playerS);

		} else if (sourceList == freeList.getFreeList() && targetList == selectorR.getSquadList()) {
			evt.acceptDrop(DnDConstants.ACTION_MOVE);
			transferFromFree(selectorR, playerS);

		} else if (sourceList == selectorL.getSquadList() && targetList == freeList.getFreeList()) {
			evt.acceptDrop(DnDConstants.ACTION_MOVE);
			transferRelease(selectorL, sourcePlayer, sourceIndex);

		} else if (sourceList == selectorR.getSquadList() && targetList == freeList.getFreeList()) {
			evt.acceptDrop(DnDConstants.ACTION_MOVE);
			transferRelease(selectorR, sourcePlayer, sourceIndex);

		} else {
			evt.rejectDrop();
		}

		evt.getDropTargetContext().dropComplete(true);
		// DEBUG
		log.debug("Drop completed Player from source index: {} -> to player [{}] {}",
				sourceIndex, playerT, targetPlayer);
	}

	public void dropActionChanged(DropTargetDragEvent evt) {
	}

	public void dragGestureRecognized(DragGestureEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (!(evt.getComponent() instanceof JList)) {
			throw new IllegalArgumentException("evt");
		}

		sourceList = (JList) evt.getComponent();
		sourceIndex = sourceList.getSelectedIndex();
		if (sourceIndex < 0) {
			return;
		}

		Player p = (Player) sourceList.getSelectedValue();
		if (p.getIndex() <= 0) {
			return;
		}
		// DEBUG
		log.info("Recognized drag Player: [{}] {} from source index: {}", p.getIndex(), p, sourceIndex);

		removeListListeners();

		lastIndex = 0;
		compareIndex = 0;
		infoPanel.refresh(lastIndex, compareIndex);

		nameEditor.setText(Strings.EMPTY);
		shirtEditor.setText(Strings.EMPTY);
		nameEditor.source = null;
		shirtEditor.source = null;

		if (sourceList.getParent() instanceof SelectByTeam) {
			SelectByTeam selectorS = (SelectByTeam) sourceList.getParent();
			int squadS = selectorS.getTeamBox().getSelectedIndex();

			if (isValidSquad(squadS) || squadS == Squads.TOTAL) {
				if (sourceList == selectorL.getSquadList()) {
					selectorL.getPosList().selectPos(selectorL.getSquadList(), sourceIndex);
				} else if (sourceList == selectorR.getSquadList()) {
					selectorR.getPosList().selectPos(selectorR.getSquadList(), sourceIndex);
				}
			}
		}

		PlayerTransferable tPlayer = new PlayerTransferable(p);
		evt.getDragSource().startDrag(evt, null, tPlayer, this);
		// DEBUG
		log.debug("Start drag Player: [{}] {} from source index: {}", p.getIndex(), p, sourceIndex);
	}

	public void dragDropEnd(DragSourceDropEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		// DEBUG
		log.info("Drag/drop end success: {}", evt.getDropSuccess());

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

	private boolean isDragSafety(boolean safeMode, JList targetList, Player targetPlayer) {
		Player sourcePlayer = (Player) sourceList.getModel().getElementAt(sourceIndex);
		int playerS = sourcePlayer.getIndex();
		int playerT = targetPlayer.getIndex();

		int squadS = -1;
		if (sourceList == selectorL.getSquadList()) {
			squadS = selectorL.getTeamBox().getSelectedIndex();
		} else if (sourceList == selectorR.getSquadList()) {
			squadS = selectorR.getTeamBox().getSelectedIndex();
		}

		AtomicBoolean transferFL = new AtomicBoolean(true);
		AtomicBoolean transferFR = new AtomicBoolean(true);
		AtomicBoolean transferLR = new AtomicBoolean(true);
		AtomicBoolean transferRL = new AtomicBoolean(true);
		AtomicBoolean releaseL = new AtomicBoolean(true);
		AtomicBoolean releaseR = new AtomicBoolean(true);

		if (safeMode) {
			int indexF = 0;
			boolean fEmpty = true;
			if (sourceList == freeList.getFreeList()) {
				indexF = playerS;
				fEmpty = false;
			} else if (targetList == freeList.getFreeList()) {
				indexF = playerT;
				fEmpty = false;
			}

			int indexL = 0;
			boolean lEmpty = true;
			if (sourceList == selectorL.getSquadList()) {
				indexL = playerS;
				lEmpty = false;
			} else if (targetList == selectorL.getSquadList()) {
				indexL = playerT;
				lEmpty = false;
			}

			int indexR = 0;
			boolean rEmpty = true;
			if (sourceList == selectorR.getSquadList()) {
				indexR = playerS;
				rEmpty = false;
			} else if (targetList == selectorR.getSquadList()) {
				indexR = playerT;
				rEmpty = false;
			}

			int squadL = selectorL.getTeamBox().getSelectedIndex();
			int squadR = selectorR.getTeamBox().getSelectedIndex();

			detectSafeTransfer(
					indexF, fEmpty, indexL, lEmpty, indexR, rEmpty, squadL, squadR,
					transferFL, transferFR, transferLR, transferRL, releaseL, releaseR);
		}

		return isDragSafety(targetList, squadS, playerS, playerT,
				transferFL.get(), transferFR.get(), transferLR.get(), transferRL.get(), releaseL.get(), releaseR.get());
	}

	private void detectSafeTransfer(
			int indexF, boolean fEmpty,
			int indexL, boolean lEmpty,
			int indexR, boolean rEmpty,
			int squadL, int squadR,
			AtomicBoolean transferFL, AtomicBoolean transferFR,
			AtomicBoolean transferLR, AtomicBoolean transferRL,
			AtomicBoolean releaseL, AtomicBoolean releaseR) {
		// DEBUG
		log.debug("Detect safety transfer; indexF: {}, fEmpty: {}, indexL: {}, lEmpty: {}, indexR: {}, rEmpty: {},"
						+ " squadL: {}, squadR: {}, transferFL: {}, transferFR: {}, transferLR: {}, transferRL: {},"
						+ " releaseL: {}, releaseR: {}",
				indexF, fEmpty, indexL, lEmpty, indexR, rEmpty, squadL, squadR,
				transferFL, transferFR, transferLR, transferRL, releaseL, releaseR);

		if ((indexF >= Player.FIRST_YOUNG && indexF < Player.FIRST_UNUSED)
				|| (indexF >= Player.FIRST_ML && indexF < Player.FIRST_SHOP)) {
			transferFL.set(false);
			transferFR.set(false);

		} else if (indexF >= Player.FIRST_EDIT && indexF < Player.END_EDIT - Formations.CLUB_TEAM_SIZE) {
			if (squadL >= Squads.NATION_COUNT) {
				transferFL.set(false);
			}
			if (squadR >= Squads.NATION_COUNT) {
				transferFR.set(false);
			}
		}

		detectSafeTransferFree(squadL, indexF, transferFL);
		detectSafeTransferFree(squadR, indexF, transferFR);

		detectSafeTransferSide(squadL, indexF, squadR, indexR, transferFL, transferRL, transferLR, releaseL);
		detectSafeTransferSide(squadR, indexF, squadL, indexL, transferFR, transferLR, transferRL, releaseR);

		if (squadR == squadL && (transferLR.get() || transferRL.get())) {
			transferLR.set(false);
			transferRL.set(false);
		}

		detectSafeTransferNation(squadL, fEmpty, indexF, transferFL, rEmpty, indexR, transferRL);
		detectSafeTransferNation(squadR, fEmpty, indexF, transferFR, lEmpty, indexL, transferLR);
	}

	private void detectSafeTransferFree(int squad, int freePlayer, AtomicBoolean freeTransfer) {
		// DEBUG
		log.debug("Detect safety transfer from free; squad: {}, freePlayer: {}, freeTransfer: {}",
				squad, freePlayer, freeTransfer);

		if (freeTransfer.get() && squad >= Squads.FIRST_CLUB && squad <= Squads.TOTAL) {
			int s = clubRelease(freePlayer, false);
			if (autoRelease.isSelected()) {
				if (s >= 0) {
					int c = Squads.countPlayers(of, s);
					if (c <= Formations.MIN_CLUB_SIZE) {
						freeTransfer.set(false);
					}
				}
			} else if (s >= 0) {
				freeTransfer.set(false);
			}
		}
	}

	private void detectSafeTransferSide(
			int squadFrom, int indexFree, int squadTo, int indexTo,
			AtomicBoolean transferFree, AtomicBoolean transferTo, AtomicBoolean transferFrom, AtomicBoolean release) {
		// DEBUG
		log.debug("Detect safety transfer from side; squadFrom: {}, indexFree: {}, squadTo: {}, indexTo: {},"
						+ " transferFree: {}, transferTo: {}, transferFrom: {}, release: {}",
				squadFrom, indexFree, squadTo, indexTo, transferFree, transferTo, transferFrom, release);

		if ((squadFrom >= Squads.NATION_COUNT && squadFrom < Squads.FIRST_CLUB) || squadFrom >= Squads.TOTAL - 3) {
			transferFree.set(false);
			transferTo.set(false);

			if (squadFrom >= Squads.TOTAL - 3 && squadFrom < Squads.TOTAL) {
				transferFrom.set(false);
			} else if (squadFrom >= Squads.FIRST_EDIT_NATION && squadFrom < Squads.FIRST_CLUB - 1
					&& squadTo >= Squads.FIRST_EDIT_NATION) {
				transferFrom.set(false);
			}

			release.set(false);
		} else {
			int count = Squads.countPlayers(of, squadFrom);
			int minSize = (squadFrom < Squads.FIRST_CLUB)
					? Formations.NATION_TEAM_SIZE : Formations.MIN_CLUB_SIZE;

			if (count <= minSize) {
				release.set(false);
				//if (indexTo == 0) {}
				if (autoRelease.isSelected() && squadFrom >= Squads.FIRST_CLUB) {
					transferFrom.set(false);
				}
			}

			if (Squads.inTeam(of, squadFrom, indexTo)) {
				transferTo.set(false);
				//if (squadFrom != squadTo) {}
			}
			if (Squads.inTeam(of, squadFrom, indexFree)) {
				transferFree.set(false);
			}

			if (!autoRelease.isSelected() && squadFrom >= Squads.FIRST_CLUB && squadFrom <= Squads.TOTAL) {
				int s = clubRelease(indexTo, false);
				if (s >= 0) {
					transferTo.set(false);
				}
			}
		}
	}

	private void detectSafeTransferNation(
			int squad, boolean fEmpty, int indexF, AtomicBoolean transferF,
			boolean sideEmpty, int indexSide, AtomicBoolean transferSide) {
		// DEBUG
		log.debug("Detect safety transfer from nation; squad: {}, fEmpty: {}, indexF: {}, transferF: {},"
						+ " sideEmpty: {}, indexSide: {}, transferSide: {}",
				squad, fEmpty, indexF, transferF, sideEmpty, indexSide, transferSide);

		if (squad >= Squads.FIRST_EDIT_NATION) {
			return;
		}

		boolean isF = (!fEmpty && transferF.get());
		boolean isS = (!sideEmpty && transferSide.get());

		if (isF || isS) {
			int squadNat = Squads.getNationForTeam(squad);

			if (isF) {
				int nat = Stats.getValue(of, indexF, Stats.NATIONALITY);
				if (nat != squadNat && nat != (Stats.NATION.length - 1)) {
					transferF.set(false);
				}
			}

			if (isS) {
				int nat = Stats.getValue(of, indexSide, Stats.NATIONALITY);
				if (nat != squadNat && nat != (Stats.NATION.length - 1)) {
					transferSide.set(false);
				}
			}
		}
	}

	private boolean isDragSafety(
			JList targetList, int squadS,
			int playerS, int playerT,
			boolean transferFL, boolean transferFR,
			boolean transferLR, boolean transferRL,
			boolean releaseL, boolean releaseR) {
		// DEBUG
		log.debug("Is drag safety; targetList: {}, squadS: {}, playerS: {}, playerT: {},"
						+ " transferFL: {}, transferFR: {}, transferLR: {}, transferRL: {}, releaseL: {}, releaseR: {}",
				targetList, squadS, playerS, playerT,
				transferFL, transferFR, transferLR, transferRL, releaseL, releaseR);

		if (sourceList != freeList.getFreeList() && targetList != freeList.getFreeList()) {
			if (sourceList == targetList) {
				if ((isValidSquad(squadS) || squadS == Squads.TOTAL) && playerS != playerT) {
					return true;
				}
			} else if (sourceList == selectorL.getSquadList()
					&& targetList == selectorR.getSquadList()
					&& transferLR
					&& playerS != 0) {
				return true;
			} else if (sourceList == selectorR.getSquadList()
					&& targetList == selectorL.getSquadList()
					&& transferRL
					&& playerS != 0) {
				return true;
			}
		} else if (sourceList == freeList.getFreeList()
				&& targetList == selectorL.getSquadList()
				&& transferFL) {
			return true;
		} else if (sourceList == freeList.getFreeList()
				&& targetList == selectorR.getSquadList()
				&& transferFR) {
			return true;
		} else if (sourceList == selectorL.getSquadList()
				&& targetList == freeList.getFreeList()
				&& releaseL) {
			return true;
		} else if (sourceList == selectorR.getSquadList()
				&& targetList == freeList.getFreeList()
				&& releaseR) {
			return true;
		}

		return false;
	}

	private void transferFromFree(SelectByTeam selector, int player) {
		Player p = (Player) selector.getSquadList().getSelectedValue();
		int teamId = selector.getTeamBox().getSelectedIndex();
		// DEBUG
		log.debug("Transfer from Free for player: {}, to team: {}, player: [{}] {}", player, teamId, p.getIndex(), p);

		int newIdx = -1;
		if (autoRelease.isSelected() && teamId >= Squads.FIRST_CLUB && teamId <= Squads.TOTAL) {
			newIdx = clubRelease(player, true);
		}

		Bits.toBytes((short) player, of.getData(), p.getSlotAdr());
		int numAdr = Squads.getNumberAdr(p.getSlotAdr());
		if (of.getData()[numAdr] == -1) {
			of.getData()[numAdr] = Bits.toByte(Squads.getNextNumber(of, teamId) - 1);
		}

		if (selector.getSquadList().getSelectedIndex() >= Formations.PLAYER_COUNT) {
			Squads.tidy(of, teamId);
		}

		refreshLists();

		if (newIdx >= 0) {
			SelectByTeam other = (selector == selectorL) ? selectorR : selectorL;
			other.getTeamBox().setSelectedIndex(newIdx);
			other.getPosList().clearSelection();
			other.getPosList().setSelectedIndex(releasedIndex);
		}
	}

	/**
	 * Transfer between left and right.
	 */
	private void transferBetweenLR(SelectByTeam fromList, SelectByTeam toList, Player player) {
		if (null == player) {
			throw new NullArgumentException("player");
		}

		int pId = player.getIndex();
		if (pId <= 0) {
			return;
		}

		Player pD = (Player) toList.getSquadList().getSelectedValue();
		int teamD = toList.getTeamBox().getSelectedIndex();
		int teamS = fromList.getTeamBox().getSelectedIndex();
		// DEBUG
		log.debug("Transfer between L-R for player: [{}] {}, from team: {}, to team: {}", pId, player, teamS, teamD);

		int newIdx = -1;
		if (autoRelease.isSelected() && teamD >= Squads.FIRST_CLUB && teamD <= Squads.TOTAL) {// NOTE: should be < TOTAL
			newIdx = clubRelease(pId, true);
		}

		Bits.toBytes((short) pId, of.getData(), pD.getSlotAdr());
		int numAdr = Squads.getNumberAdr(pD.getSlotAdr());
		if (of.getData()[numAdr] == -1) {
			of.getData()[numAdr] = Bits.toByte(Squads.getNextNumber(of, teamD) - 1);
		}

		if (toList.getSquadList().getSelectedIndex() >= Formations.PLAYER_COUNT) {
			Squads.tidy(of, teamD);
		}

		refreshLists();

		if (newIdx >= 0 && (teamS < Squads.FIRST_CLUB || teamS >= Squads.TOTAL - 1)) {
			fromList.getTeamBox().setSelectedIndex(newIdx);
			fromList.getPosList().clearSelection();
			fromList.getPosList().setSelectedIndex(releasedIndex);
		}
	}

	private void transferSwap(
			Player sourcePlayer, Player targetPlayer, int sourceTeam, int targetTeam,
			JList sourceList, JList targetList) {

		int adrS = sourcePlayer.getSlotAdr();
		int pidS = sourcePlayer.getIndex();
		int adrT = targetPlayer.getSlotAdr();
		int pidT = targetPlayer.getIndex();
		// DEBUG
		log.debug("Transfer swap from player: {}, team: {} -> to player: {}, team: {}",
				sourcePlayer, sourceTeam, targetPlayer, targetTeam);

		Bits.toBytes((short) pidT, of.getData(), adrS);
		Bits.toBytes((short) pidS, of.getData(), adrT);

		if (sourceTeam == targetTeam) {
			int numAdrS = Squads.getNumberAdr(adrS);
			int numAdrT = Squads.getNumberAdr(adrT);

			byte num = of.getData()[numAdrT];
			of.getData()[numAdrT] = of.getData()[numAdrS];
			of.getData()[numAdrS] = num;
		}

		if (pidS == 0 || pidT == 0) {
			if (sourceIndex >= Formations.PLAYER_COUNT) {
				Squads.tidy(of, sourceTeam);
			} else if (autoGaps.isSelected() && sourceList.getParent() instanceof SelectByTeam) {

				SelectByTeam selector = (SelectByTeam) sourceList.getParent();
				int posNum = selector.getPosList().getPosNum(sourceIndex);
				Squads.tidy11(of, sourceTeam, sourceIndex, posNum);
			}

			int targetIndex = targetList.getSelectedIndex();
			if (targetIndex >= Formations.PLAYER_COUNT) {
				Squads.tidy(of, targetTeam);
			} else if (autoGaps.isSelected() && sourceList != targetList
					&& targetList.getParent() instanceof SelectByTeam) {

				SelectByTeam selector = (SelectByTeam) targetList.getParent();
				int posNum = selector.getPosList().getPosNum(targetIndex);
				Squads.tidy11(of, targetTeam, targetIndex, posNum);
			}
		}

		refreshLists();
	}

	private void transferRelease(SelectByTeam selector, Player player, int sourceIndex) {
		log.debug("Transfer release player: {} from source: {}", player, sourceIndex);

		int adr = player.getSlotAdr();
		Bits.toBytes((short) 0, of.getData(), adr);
		int numAdr = Squads.getNumberAdr(adr);
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
		log.debug("Try to add listeners into all Lists");

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
		log.debug("Try to remove listeners from all Lists");

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
		private static final long serialVersionUID = 536793465282593280L;

		private final TransferPanel owner;
		private volatile EventSource source = null;

		public NameTextField(TransferPanel owner) {
			super(Math.round(0.4f * Player.NAME_LEN));
			this.owner = owner;

			log.debug("Initialize player name input for Transfer panel #{}", owner.hashCode());

			setDocument(new JTextFieldLimit(Player.NAME_LEN / 2));
			setToolTipText(Resources.getMessage("transfer.nameField.tip"));
			addActionListener(this);
		}

		public void valueChanged(ListSelectionEvent evt) {
			if (null == evt) {
				throw new NullArgumentException("evt");
			}
			if (evt.getValueIsAdjusting()) {
				return;
			}
			if (!(evt.getSource() instanceof JList)) {
				throw new IllegalArgumentException("evt");
			}
			log.info("Refresh player name input when source list #{} changed: {} -> {}",
					evt.getSource().hashCode(), evt.getFirstIndex(), evt.getLastIndex());

			JList listS = (JList) evt.getSource();
			if (listS.isSelectionEmpty()) {
				setText(Strings.EMPTY);
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
				setText(p.getIndex() > 0 ? p.getName() : Strings.EMPTY);
				selectAll();

				owner.lastIndex = p.getIndex();
				owner.infoPanel.refresh(owner.lastIndex, owner.compareIndex);
			}
		}

		public void actionPerformed(ActionEvent evt) {
			if (null == source) {
				return;
			}

			String text = getText();
			if (Strings.isEmpty(text) || text.length() > Player.NAME_LEN / 2) {
				return;
			}
			log.info("Update source list #{} when player name changed: {}", source.hashCode(), text);

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
		private static final long serialVersionUID = -451173065625033858L;

		private final TransferPanel owner;
		private volatile EventSource source = null;

		public NumTextField(TransferPanel owner) {
			super(2);
			this.owner = owner;

			log.debug("Initialize player number input for Transfer panel #{}", owner.hashCode());

			setDocument(new JTextFieldLimit(3));
			setToolTipText(Resources.getMessage("transfer.numField.tip"));
			addActionListener(this);
		}

		public void valueChanged(ListSelectionEvent evt) {
			if (null == evt) {
				throw new NullArgumentException("evt");
			}
			if (evt.getValueIsAdjusting()) {
				return;
			}
			if (!(evt.getSource() instanceof JList)) {
				throw new IllegalArgumentException("evt");
			}
			log.info("Refresh player number input when source list #{} changed: {} -> {}",
					evt.getSource().hashCode(), evt.getFirstIndex(), evt.getLastIndex());

			JList listS = (JList) evt.getSource();
			if (listS.isSelectionEmpty()) {
				setText(Strings.EMPTY);
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
			if (null == source) {
				return;
			}
			String text = getText();
			log.info("Update source list #{} when player number changed: {}", source.hashCode(), text);

			int num;
			try {
				num = Integer.parseInt(text);
			} catch (NumberFormatException nfe) {
				num = -1;
			}
			if (num <= 0 || num > 99) {// NOTE: max squad number is 99 ?
				setText(Strings.EMPTY);
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

			if (idx < listS.getModel().getSize() - 1) {
				listS.setSelectedIndex(idx + 1);
			}
		}
	}

	private static class ShirtTextField extends JTextField implements ListSelectionListener, ActionListener {
		private static final long serialVersionUID = 7108398217879103867L;

		private final TransferPanel owner;
		private volatile EventSource source = null;

		public ShirtTextField(TransferPanel owner) {
			super(Math.round(0.8f * Player.SHIRT_NAME_LEN));
			this.owner = owner;

			log.debug("Initialize player shirt-name input for Transfer panel #{}", owner.hashCode());

			setDocument(new JTextFieldLimit(Player.SHIRT_NAME_LEN));
			setToolTipText(Resources.getMessage("transfer.shirtName.tip"));
			addActionListener(this);
		}

		public void valueChanged(ListSelectionEvent evt) {
			if (null == evt) {
				throw new NullArgumentException("evt");
			}
			if (evt.getValueIsAdjusting()) {
				return;
			}
			if (!(evt.getSource() instanceof JList)) {
				throw new IllegalArgumentException("evt");
			}
			log.info("Refresh player shirt-name input when source list #{} changed: {} -> {}",
					evt.getSource().hashCode(), evt.getFirstIndex(), evt.getLastIndex());

			JList listS = (JList) evt.getSource();
			if (listS.isSelectionEmpty()) {
				setText(Strings.EMPTY);
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
			if (null == source) {
				return;
			}

			String text = getText();
			if (Strings.isEmpty(text) || text.length() > Player.SHIRT_NAME_LEN) {
				return;
			}
			log.info("Update source list #{} when player shirt-name changed: {}", source.hashCode(), text);

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
				if (idx >= listS.getModel().getSize() - 1) {
					idx = -1;
				}
			}

			Player p = (Player) listS.getSelectedValue();
			p.setShirtName(text);

			owner.refreshLists();
			if (idx >= 0) {
				listS.setSelectedIndex(idx + 1);
			}
		}
	}

	//endregion

}
