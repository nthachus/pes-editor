package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Player;
import editor.data.Squads;
import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Images;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class FormationPanel extends JPanel
		implements ActionListener, ListSelectionListener, DropTargetListener, DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 8650634277665523195L;
	private static final Logger log = LoggerFactory.getLogger(FormationPanel.class);

	private final OptionFile of;

	private final AtomicInteger def = new AtomicInteger(0);
	private final AtomicInteger mid = new AtomicInteger(0);
	private final AtomicInteger atk = new AtomicInteger(0);

	private volatile int team;
	private volatile int sourceIndex = -1;
	private volatile boolean isOk = false;
	private volatile boolean isFromPitch = false;

	public FormationPanel(OptionFile of) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Formation panel is initializing..");
		initComponents();
	}

	//region Initialize the GUI components

	private SquadList squadList;
	private PositionList posList;
	private JobList sFK;
	private JobList lFK;
	private JobList rCorner;
	private JobList lCorner;
	private JobList pk;
	private JobList captain;
	private JComboBox/*<String>*/ formNamesBox;
	private PitchPanel pitchPanel;
	private AtkDefPanel atkDefPanel;
	private JComboBox/*<Role>*/ roleBox;
	private JComboBox altBox;
	private SquadNumberList numList;
	private JFileChooser pngChooser;
	private TeamSettingPanel teamSettingPan;
	private StrategyPanel strategyPan;

	private void initComponents() {
		FileFilter pngFilter = new PngFilter();
		pngChooser = new JFileChooser();
		pngChooser.addChoosableFileFilter(pngFilter);
		pngChooser.setAcceptAllFileFilterUsed(false);
		pngChooser.setDialogTitle(Resources.getMessage("formation.snapTitle"));

		numList = new SquadNumberList(of);

		altBox = new JComboBox/*<String>*/(Resources.getMessages(Formations.ALT_ITEMS));
		altBox.addActionListener(this);

		roleBox = new JComboBox/*<Role>*/();
		roleBox.setPreferredSize(new Dimension(56, 25));
		roleBox.addActionListener(this);

		squadList = new SquadList(of, true);
		//squadList.setToolTipText(Resources.getMessage("formation.squadTip"));
		squadList.addListSelectionListener(this);

		new DropTarget(squadList, this);
		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(squadList, DnDConstants.ACTION_MOVE, this);

		posList = new PositionList(of, false);
		teamSettingPan = new TeamSettingPanel(of);
		strategyPan = new StrategyPanel(of, squadList);

		atkDefPanel = new AtkDefPanel(of, altBox);
		pitchPanel = new PitchPanel(of, this, squadList, atkDefPanel, altBox, numList);
		atkDefPanel.setPitch(pitchPanel);

		lFK = new JobList(of, 0, " F-L ", Color.YELLOW);
		lFK.setToolTipText(Resources.getMessage("formation.lFK"));
		sFK = new JobList(of, 1, " F-S ", Color.YELLOW);
		sFK.setToolTipText(Resources.getMessage("formation.sFK"));
		lCorner = new JobList(of, 2, " C-L ", Color.CYAN);
		lCorner.setToolTipText(Resources.getMessage("formation.lCorner"));
		rCorner = new JobList(of, 3, " C-R", Color.CYAN);
		rCorner.setToolTipText(Resources.getMessage("formation.rCorner"));
		pk = new JobList(of, 4, " PK ", Color.GREEN);
		pk.setToolTipText(Resources.getMessage("formation.PK"));
		captain = new JobList(of, 5, " C ", Color.RED);
		captain.setToolTipText(Resources.getMessage("formation.captain"));

		formNamesBox = new JComboBox/*<String>*/();
		//formNamesBox.setEnabled(false);
		formNamesBox.addActionListener(this);

		JButton snapButton = new JButton(Resources.getMessage("formation.snapshot"));
		snapButton.setToolTipText(Resources.getMessage("formation.snapTip"));
		snapButton.setActionCommand("Snapshot");
		snapButton.addActionListener(this);

		JPanel rolePickPan = new JPanel(new GridLayout(0, 6));
		rolePickPan.add(lFK);
		rolePickPan.add(sFK);
		rolePickPan.add(lCorner);
		rolePickPan.add(rCorner);
		rolePickPan.add(pk);
		rolePickPan.add(captain);

		JPanel numPosPane = new JPanel(new BorderLayout());
		numPosPane.add(numList, BorderLayout.WEST);
		numPosPane.add(posList, BorderLayout.EAST);

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(numPosPane, BorderLayout.WEST);
		mainPane.add(squadList, BorderLayout.CENTER);
		mainPane.add(rolePickPan, BorderLayout.EAST);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(mainPane, BorderLayout.CENTER);
		contentPane.add(formNamesBox, BorderLayout.NORTH);

		JPanel settingPane = new JPanel();
		settingPane.add(atkDefPanel);
		settingPane.add(roleBox);

		JPanel topPane = new JPanel(new GridLayout(1, 3));
		topPane.add(altBox);
		topPane.add(formNamesBox);
		topPane.add(snapButton);

		JPanel bottomPane = new JPanel(new BorderLayout());
		bottomPane.add(teamSettingPan, BorderLayout.NORTH);
		bottomPane.add(pitchPanel, BorderLayout.CENTER);
		bottomPane.add(settingPane, BorderLayout.SOUTH);

		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(topPane, BorderLayout.NORTH);
		rightPane.add(bottomPane, BorderLayout.CENTER);
		rightPane.add(strategyPan, BorderLayout.SOUTH);

		add(contentPane);
		add(rightPane);
	}

	//endregion

	public void setFromPitch(boolean isFromPitch) {
		this.isFromPitch = isFromPitch;
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform Formation action '{}' on: {}", evt.getActionCommand(), Strings.valueOf(evt.getSource()));

		if (evt.getSource() == altBox) {
			altChanged(evt);
		} else if (evt.getSource() == roleBox) {
			roleChanged(evt);
		} else if (evt.getSource() == formNamesBox) {
			formationChanged(evt);
		} else/* if ("Snapshot".equalsIgnoreCase(evt.getActionCommand()))*/ {
			saveStrategyAsPNG();
		}
	}

	private void altChanged(ActionEvent evt) {
		if (!"y".equalsIgnoreCase(evt.getActionCommand())) {
			return;
		}

		int alt = altBox.getSelectedIndex();
		//countFormations();
		posList.setAlt(alt);
		posList.refresh(team);
		//squadList.refresh(team);
		updateRoleBox();

		teamSettingPan.setAlt(alt);
		teamSettingPan.refresh(team);

		pitchPanel.repaint();
		atkDefPanel.repaint();

		log.debug("Alt was changed to {}, for team {}", alt, team);
	}

	private void roleChanged(ActionEvent evt) {
		if (!"y".equalsIgnoreCase(evt.getActionCommand())) {
			return;
		}

		int squadIndex = squadList.getSelectedIndex();
		if (squadIndex < 0 || squadIndex >= Formations.PLAYER_COUNT) {
			return;
		}

		Role role = (Role) roleBox.getSelectedItem();
		if (null == role || role.index < 0) {
			return;
		}

		int alt = altBox.getSelectedIndex();
		int oldPos = Formations.getPosition(of, team, alt, squadIndex);
		fixCoordinateForNewRole(alt, squadIndex, oldPos, role.index);

		Formations.setPosition(of, team, alt, squadIndex, role.index);
		//countFormations();

		if (oldPos > 0 && oldPos < 8 && (role.index < 1 || role.index > 7)) {
			if (alt == 0 && squadIndex == Formations.getCBOverlap(of, team)) {
				Formations.setCBOverlap(of, team, 0);
			}
		}

		updateRoleBox();

		posList.refresh(team);
		teamSettingPan.refresh(team);
		strategyPan.refresh(team);

		pitchPanel.repaint();
		atkDefPanel.repaint();

		log.debug("Role was changed to {}, for squad: {}, alt: {}, team: {}", role, squadIndex, alt, team);
	}

	private void fixCoordinateForNewRole(int alt, int squadIndex, int oldPos, int roleId) {
		if (oldPos == roleId) {
			return;
		}
		log.debug("Try to fix coordinate for new role {}; with alt: {}, squad: {}, old-pos: {}, team: {}",
				roleId, alt, squadIndex, oldPos, team);

		if (oldPos < 10) {
			if (roleId >= 10) {
				if (roleId < 29) {
					Formations.setX(of, team, alt, squadIndex, 25);
				} else {
					Formations.setX(of, team, alt, squadIndex, 41);
				}
			}
		} else if (oldPos < 29) {
			if (roleId < 10) {
				Formations.setX(of, team, alt, squadIndex, 8);
			} else if (roleId >= 29) {
				Formations.setX(of, team, alt, squadIndex, 41);
			}
		} else {
			if (roleId < 29) {
				if (roleId < 10) {
					Formations.setX(of, team, alt, squadIndex, 8);
				} else {
					Formations.setX(of, team, alt, squadIndex, 25);
				}
			}
		}

		if (roleId == 8 || roleId == 15 || roleId == 22 || roleId == 29) {
			if (oldPos != 8 && oldPos != 15 && oldPos != 22 && oldPos != 29
					&& Formations.getY(of, team, alt, squadIndex) > 50) {
				Formations.setY(of, team, alt, squadIndex, 28);
			}
		} else if (roleId == 9 || roleId == 16 || roleId == 23 || roleId == 30) {
			if (oldPos != 9 && oldPos != 16 && oldPos != 23 && oldPos != 30
					&& Formations.getY(of, team, alt, squadIndex) < 54) {
				Formations.setY(of, team, alt, squadIndex, 76);
			}
		}
	}

	private void formationChanged(ActionEvent evt) {
		if (!"y".equalsIgnoreCase(evt.getActionCommand())) {
			return;
		}

		int formId = formNamesBox.getSelectedIndex();
		if (formId < 0) {
			return;
		}

		int alt = altBox.getSelectedIndex();
		if (formId > 0) {
			Formations.setFormation(of, team, alt, formId - 1);
		}

		if (alt == 0) {
			int pos = Formations.getPosition(of, team, 0, Formations.getCBOverlap(of, team));
			if (pos < 1 || pos > 7) {
				Formations.setCBOverlap(of, team, 0);
			}
		}

		//countFormations();
		posList.refresh(team);
		strategyPan.refresh(team);
		teamSettingPan.refresh(team);

		pitchPanel.repaint();
		atkDefPanel.repaint();

		updateRoleBox();

		log.debug("Formation was changed to {}, for alt: {}, team: {}", formId, alt, team);
	}

	private int getNumTeam() {
		int tt = team;
		if (tt >= Squads.FIRST_EDIT_NATION) {
			tt += Squads.EDIT_TEAM_COUNT;
		}
		return tt;
	}

	public void refresh(int team) {
		log.info("Try to refresh Formation panel for team: {}", team);

		isOk = false;
		this.team = team;

		altBox.setActionCommand("n");
		altBox.setSelectedIndex(0);

		//countFormations();
		squadList.refresh(team, false);

		int tt = getNumTeam();
		numList.refresh(tt);

		int alt = altBox.getSelectedIndex();
		posList.setAlt(alt);
		posList.refresh(team);

		updateRoleBox();

		sFK.refresh(team);
		lFK.refresh(team);
		rCorner.refresh(team);
		lCorner.refresh(team);
		pk.refresh(team);
		captain.refresh(team);

		teamSettingPan.setAlt(alt);
		teamSettingPan.refresh(team);

		strategyPan.refresh(team);

		pitchPanel.setSelectedIndex(-1);
		pitchPanel.setSquad(team);
		pitchPanel.repaint();

		atkDefPanel.setSelectedIndex(-1);
		atkDefPanel.setSquad(team);
		atkDefPanel.repaint();

		altBox.setActionCommand("y");
		isOk = true;

		log.debug("Refresh completed on Formation panel for team: {}, alt: {}", team, alt);
	}

	/**
	 * On the squad list selection changed.
	 */
	public void valueChanged(ListSelectionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("On the squad list selection changed, from-pitch: {}, ok: {}", isFromPitch, isOk);

		if (isFromPitch) {
			isFromPitch = false;
			updateRoleBox();

		} else if (!evt.getValueIsAdjusting() && isOk) {
			int squadIndex = squadList.getSelectedIndex();

			updateRoleBox();

			if (squadIndex >= 0 && squadIndex < Formations.PLAYER_COUNT) {
				pitchPanel.setSelectedIndex(squadIndex);
				atkDefPanel.setSelectedIndex(squadIndex);
			} else {
				pitchPanel.setSelectedIndex(-1);
				atkDefPanel.setSelectedIndex(-1);
			}

			pitchPanel.repaint();
			atkDefPanel.repaint();
			//posList.selectPos(squadList, squadIndex);

			log.debug("Squad list on Formation panel was changed to {}", squadIndex);
		}
	}

	private void updateRoleBox() {
		log.debug("Try to update Role box for team: {}", team);
		roleBox.setActionCommand("n");

		countFormations();

		roleBox.removeAllItems();

		int squadIndex = squadList.getSelectedIndex();
		roleBox.setEnabled(squadIndex >= 0 && squadIndex < Formations.PLAYER_COUNT);

		if (squadIndex == 0) {
			roleBox.addItem(new Role(0));

		} else if (roleBox.isEnabled()) {
			int alt = altBox.getSelectedIndex();
			int selPos = Formations.getPosition(of, team, alt, squadIndex);

			addItemsToRoleBox(alt, selPos);
		}

		roleBox.setActionCommand("y");
		// DEBUG
		log.debug("Role box was updated for squad: {}", squadIndex);
	}

	private void addItemsToRoleBox(int alt, int selPos) {
		log.debug("Add items to Role box with alt: {}, select-pos: {}", alt, selPos);

		Role last = null;
		Role first = new Role(selPos);
		roleBox.addItem(first);

		int count = 1;
		boolean isCB = false;

		Role role;
		for (int r = 1; r < 41; r++) {
			boolean isFree = isRoleFree(alt, selPos, r);
			if (!isFree) {
				continue;
			}

			role = new Role(r);
			if (first.name.equalsIgnoreCase(role.name)) {
				continue;
			}

			if (last == null) {
				last = role;
				roleBox.addItem(role);
				count++;
			} else if (!last.name.equalsIgnoreCase(role.name)
					&& !(role.isCB() && isCB)) {
				last = role;
				roleBox.addItem(last);
				count++;
			}

			if (role.isCB()) {
				isCB = true;
			}
		}
		// DEBUG
		log.debug("{} Role items was added", count);
	}

	private boolean isRoleFree(int alt, int selPos, int r) {
		log.debug("Check Role free for {}, alt: {}, select-pos: {}", r, alt, selPos);

		if (r == 5) {
			return false;
		} else {
			if (r == 15) {
				for (int p = 0; p < Formations.PLAYER_COUNT; p++) {
					int pos = Formations.getPosition(of, team, alt, p);
					if (pos != selPos && (pos == 8 || pos == 22)) {
						return false;
					}
				}
			} else if (r == 16) {
				for (int p = 0; p < Formations.PLAYER_COUNT; p++) {
					int pos = Formations.getPosition(of, team, alt, p);
					if (pos != selPos && (pos == 9 || pos == 23)) {
						return false;
					}
				}
			} else if ((r == 8 || r == 22) && selPos != 15) {
				for (int p = 0; p < Formations.PLAYER_COUNT; p++) {
					int pos = Formations.getPosition(of, team, alt, p);
					if (pos == 15) {
						return false;
					}
				}
			} else if ((r == 9 || r == 23) && selPos != 16) {
				for (int p = 0; p < Formations.PLAYER_COUNT; p++) {
					int pos = Formations.getPosition(of, team, alt, p);
					if (pos == 16) {
						return false;
					}
				}
			}

			if (isDef(selPos)) {
				if (def.get() <= 2 && !isDef(r)) {
					return false;
				} else if (mid.get() >= 6 && isMid(r)) {
					return false;
				} else if (atk.get() >= 5 && isAtk(r)) {
					return false;
				}
			} else if (isMid(selPos)) {
				if (mid.get() <= 2 && !isMid(r)) {
					return false;
				} else if (def.get() >= 5 && isDef(r)) {
					return false;
				} else if (atk.get() >= 5 && isAtk(r)) {
					return false;
				}
			} else if (isAtk(selPos)) {
				if (atk.get() <= 1 && !isAtk(r)) {
					return false;
				} else if (mid.get() >= 6 && isMid(r)) {
					return false;
				} else if (def.get() >= 5 && isDef(r)) {
					return false;
				}
			}
		}

		for (int p = 0; p < Formations.PLAYER_COUNT; p++) {
			int pos = Formations.getPosition(of, team, alt, p);
			if (pos == r) {
				return false;
			}
		}

		return true;
	}

	private void countFormations() {
		log.debug("Try to bind data for Formations list");
		formNamesBox.setActionCommand("n");

		def.set(0);
		mid.set(0);
		atk.set(0);
		int mid2 = 0;

		for (int i = 1; i < Formations.PLAYER_COUNT; i++) {
			int pos = Formations.getPosition(of, team, altBox.getSelectedIndex(), i);
			if (isDef(pos)) {
				def.incrementAndGet();
			} else if (isMid(pos)) {
				if (pos > 23 && pos < 29) {
					mid2++;
				}
				mid.incrementAndGet();
			} else if (isAtk(pos)) {
				atk.incrementAndGet();
			}
		}

		String myForm;
		if (mid2 > 0 && mid2 < 3) {
			if (mid.addAndGet(-mid2) == 0) {
				myForm = def + "-" + mid2 + "-" + atk;
			} else {
				myForm = def + "-" + mid + "-" + mid2 + "-" + atk;
			}
		} else {
			myForm = def + "-" + mid + "-" + atk;
		}
		// DEBUG
		log.debug("Current formation: {}", myForm);

		ArrayList<String> formNames = new ArrayList<String>(Arrays.asList(Formations.FORM_NAMES));
		formNames.add(0, myForm);
		formNamesBox.setModel(new DefaultComboBoxModel/*<String>*/(
				formNames.toArray(/*new String[formNames.size()]*/)));
		formNamesBox.setActionCommand("y");
	}

	private void saveStrategyAsPNG() {
		int returnVal = pngChooser.showSaveDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File dest = pngChooser.getSelectedFile();
		dest = Files.addExtension(dest, Files.PNG);

		if (dest.exists()) {
			returnVal = JOptionPane.showConfirmDialog(null,
					Resources.getMessage("msg.overwrite", dest.getName(), dest.getParent()),
					Resources.getMessage("msg.overwrite.title", dest.getName()),
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
			if (returnVal != JOptionPane.YES_OPTION) {
				return;
			} else if (!dest.delete()) {
				showAccessFailedMsg(null);
				return;
			}
		}

		try {
			if (Images.saveComponentAsImage(pitchPanel, dest)) {
				JOptionPane.showMessageDialog(null,
						Resources.getMessage("msg.saveSuccess", dest.getName(), dest.getParent()),
						Resources.getMessage("msg.saveSuccess.title"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				showAccessFailedMsg(null);
			}
		} catch (IOException e) {
			showAccessFailedMsg(e.getLocalizedMessage());
		}

		log.debug("Strategy panel was saved as PNG: {}", dest.getName());
	}

	private static void showAccessFailedMsg(String msg) {
		if (Strings.isBlank(msg)) {
			msg = Resources.getMessage("msg.accessFailed");
		}
		JOptionPane.showMessageDialog(null, msg, Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
	}

	private static boolean isDef(int role) {
		return (role > 0 && role < 10);
	}

	private static boolean isMid(int role) {
		return (role >= 10 && role < 29);
	}

	private static boolean isAtk(int role) {
		return (role >= 29 && role < 41);
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
		if (null == evt.getLocation()) {
			throw new NullArgumentException("evt.location");
		}
		log.debug("Drag over {}", evt.getLocation());

		int squadIndex = squadList.locationToIndex(evt.getLocation());
		squadList.setSelectedIndex(squadIndex);

		if (squadIndex >= 0 && squadIndex != sourceIndex
				&& ((Player) squadList.getModel().getElementAt(squadIndex)).getIndex() > 0) {
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

		isOk = false;
		int squadIndex = squadList.getSelectedIndex();
		// DEBUG
		log.debug("Drop Player to squad: {}, team: {}, source-squad: {}", squadIndex, team, sourceIndex);

		evt.acceptDrop(DnDConstants.ACTION_MOVE);

		int tempSlot = Formations.getSlot(of, team, sourceIndex);
		Formations.setSlot(of, team, sourceIndex, Formations.getSlot(of, team, squadIndex));
		Formations.setSlot(of, team, squadIndex, tempSlot);

		if (sourceIndex < Formations.PLAYER_COUNT && squadIndex < Formations.PLAYER_COUNT) {
			for (int j = 0; j < Formations.JOBS_COUNT; j++) {

				int tempJob = Formations.getJob(of, team, j);
				if (tempJob == sourceIndex) {
					Formations.setJob(of, team, j, squadIndex);
				} else if (tempJob == squadIndex) {
					Formations.setJob(of, team, j, sourceIndex);
				}
			}

			sFK.refresh(team);
			lFK.refresh(team);
			rCorner.refresh(team);
			lCorner.refresh(team);
			pk.refresh(team);
			captain.refresh(team);
		}

		int tt = getNumTeam();
		numList.refresh(tt);

		squadList.refresh(team, false);
		teamSettingPan.refresh(team);
		strategyPan.refresh(team);
		pitchPanel.repaint();

		evt.getDropTargetContext().dropComplete(true);

		isOk = true;
	}

	public void dropActionChanged(DropTargetDragEvent evt) {
	}

	public void dragGestureRecognized(DragGestureEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (null == evt.getDragSource()) {
			throw new NullArgumentException("evt.dragSource");
		}

		sourceIndex = squadList.getSelectedIndex();
		// DEBUG
		log.debug("Drag recognized for source-squad: {}", sourceIndex);
		Player p = (Player) squadList.getSelectedValue();

		if (sourceIndex >= 0 && p.getIndex() > 0) {
			posList.selectPos(squadList, sourceIndex);

			roleBox.setActionCommand("n");
			roleBox.removeAllItems();
			roleBox.setEnabled(false);
			roleBox.setActionCommand("y");

			pitchPanel.setSelectedIndex(-1);
			atkDefPanel.setSelectedIndex(-1);
			pitchPanel.repaint();
			atkDefPanel.repaint();

			PlayerTransferable pTransfer = new PlayerTransferable(p);
			evt.getDragSource().startDrag(evt, null, pTransfer, this);

			log.debug("Drag gesture was recognized for player [{}] {}", p.getIndex(), p.getName());
		} else {
			log.debug("Nothing was selected!");
		}
	}

	public void dragDropEnd(DragSourceDropEvent evt) {
		squadList.clearSelection();
		posList.clearSelection();

		log.debug("Drag-n-drop was ended");
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

	private static class Role implements Serializable {
		private static final long serialVersionUID = 3234599033413239419L;

		private final int index;
		private final String name;

		public Role(int index) {
			this.index = index;
			this.name = Formations.positionToString(index);
		}

		public boolean isCB() {
			return name.contains("CB");
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
