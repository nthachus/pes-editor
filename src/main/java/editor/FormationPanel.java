package editor;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Player;
import editor.ui.*;
import editor.util.Files;
import editor.util.Images;
import editor.util.Resources;
import editor.util.swing.DefaultComboBoxModel;
import editor.util.swing.JComboBox;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class FormationPanel extends JPanel
		implements ListSelectionListener, DropTargetListener, DragSourceListener, DragGestureListener {

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
		if (null == of) throw new NullPointerException("of");
		this.of = of;

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
	private JComboBox<String> formNamesBox;
	private PitchPanel pitchPanel;
	private AtkDefPanel atkDefPanel;
	private JComboBox<Role> roleBox;
	private JComboBox<String> altBox;
	private SquadNumberList numList;
	private JFileChooser pngChooser;
	private TeamSettingPanel teamSettingPan;
	private StrategyPanel strategyPan;

	private void initComponents() {
		PngFilter pngFilter = new PngFilter();
		pngChooser = new JFileChooser();
		pngChooser.addChoosableFileFilter(pngFilter);
		pngChooser.setAcceptAllFileFilterUsed(false);
		pngChooser.setDialogTitle(Resources.getMessage("formation.snapTitle"));

		numList = new SquadNumberList(of);

		altBox = new JComboBox<String>(Formations.ALT_ITEMS);
		altBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onAltChanged(evt);
			}
		});

		roleBox = new JComboBox<Role>();
		roleBox.setPreferredSize(new Dimension(56, 25));
		roleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onRoleChanged(evt);
			}
		});

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

		formNamesBox = new JComboBox<String>();
		formNamesBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onFormationChanged(evt);
			}
		});
		//formNamesBox.setEnabled(false);

		JButton snapButton = new JButton(Resources.getMessage("formation.snapshot"));
		snapButton.setToolTipText(Resources.getMessage("formation.snapTip"));
		snapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveStrategyAsPNG();
			}
		});

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

	private void onAltChanged(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!"y".equalsIgnoreCase(evt.getActionCommand()))
			return;

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
	}

	private void onRoleChanged(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!"y".equalsIgnoreCase(evt.getActionCommand()))
			return;

		int squadIndex = squadList.getSelectedIndex();
		if (squadIndex < 0 || squadIndex >= Formations.PLAYER_COUNT)
			return;

		Role role = roleBox.getSelectedItem();
		if (null == role || role.index < 0)
			return;

		int alt = altBox.getSelectedIndex();
		int oldPos = Formations.getPosition(of, team, alt, squadIndex);
		if (oldPos != role.index) {
			//log.debug("{}", oldPos);
			if (oldPos < 10) {
				if (role.index > 9) {
					if (role.index < 29) {
						Formations.setX(of, team, alt, squadIndex, 25);
					} else {
						Formations.setX(of, team, alt, squadIndex, 41);
					}
				}
			}

			if (oldPos > 9 && oldPos < 29) {
				if (role.index < 10) {
					Formations.setX(of, team, alt, squadIndex, 8);
				} else if (role.index > 28) {
					Formations.setX(of, team, alt, squadIndex, 41);
				}
			}

			if (oldPos > 28) {
				if (role.index < 29) {
					if (role.index < 10) {
						Formations.setX(of, team, alt, squadIndex, 8);
					} else {
						Formations.setX(of, team, alt, squadIndex, 25);
					}
				}
			}

			if (role.index == 8 || role.index == 15 || role.index == 22 || role.index == 29) {
				if (oldPos != 8 && oldPos != 15 && oldPos != 22 && oldPos != 29) {
					if (Formations.getY(of, team, alt, squadIndex) > 50) {
						Formations.setY(of, team, alt, squadIndex, 28);
					}
				}
			}

			if (role.index == 9 || role.index == 16 || role.index == 23 || role.index == 30) {
				if (oldPos != 9 && oldPos != 16 && oldPos != 23 && oldPos != 30) {
					if (Formations.getY(of, team, alt, squadIndex) < 54) {
						Formations.setY(of, team, alt, squadIndex, 76);
					}
				}
			}
		}

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
	}

	private void onFormationChanged(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!"y".equalsIgnoreCase(evt.getActionCommand()))
			return;

		int formId = formNamesBox.getSelectedIndex();
		if (formId < 0) return;

		int alt = altBox.getSelectedIndex();
		if (formId > 0) {
			Formations.setFormation(of, team, alt, formId - 1);
		}

		if (alt == 0) {
			int pos = Formations.getPosition(of, team, 0, Formations.getCBOverlap(of, team));
			if (pos < 1 || pos > 7)
				Formations.setCBOverlap(of, team, 0);
		}

		//countFormations();
		posList.refresh(team);
		strategyPan.refresh(team);
		teamSettingPan.refresh(team);

		pitchPanel.repaint();
		atkDefPanel.repaint();

		updateRoleBox();
	}

	public void refresh(int t) {
		team = t;
		altBox.setActionCommand("n");
		altBox.setSelectedIndex(0);
		altBox.setActionCommand("y");
		// countFormations();
		isOk = false;
		squadList.refresh(t, false);
		isOk = true;
		int tt = t;
		if (t > 66) {
			tt = t + 8;
		}
		numList.refresh(tt);
		posList.setAlt(altBox.getSelectedIndex());
		posList.refresh(t);
		updateRoleBox();
		sFK.refresh(t);
		lFK.refresh(t);
		rCorner.refresh(t);
		lCorner.refresh(t);
		pk.refresh(t);
		captain.refresh(t);
		teamSettingPan.setAlt(altBox.getSelectedIndex());
		teamSettingPan.refresh(t);
		strategyPan.refresh(t);
		pitchPanel.setSelectedIndex(-1);
		pitchPanel.setSquad(t);
		pitchPanel.repaint();
		atkDefPanel.setSelectedIndex(-1);
		atkDefPanel.setSquad(t);
		atkDefPanel.repaint();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (isFromPitch) {
			isFromPitch = false;
			updateRoleBox();
		} else {
			if (!e.getValueIsAdjusting() && isOk) {
				int i = squadList.getSelectedIndex();
				updateRoleBox();
				if (i >= 0 && i < Formations.PLAYER_COUNT) {
					pitchPanel.setSelectedIndex(i);
					atkDefPanel.setSelectedIndex(i);
				} else {
					pitchPanel.setSelectedIndex(-1);
					atkDefPanel.setSelectedIndex(-1);
				}
				pitchPanel.repaint();
				atkDefPanel.repaint();
				// posList.selectPos(squadList, i);
			}
		}
	}

	private static class Role implements Serializable {
		private static final long serialVersionUID = 1L;

		private final int index;
		private final String name;

		public Role(int index) {
			this.index = index;
			name = Formations.positionToString(index);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private void updateRoleBox() {
		countFormations();

		roleBox.setActionCommand("n");
		roleBox.removeAllItems();
		roleBox.setEnabled(true);

		int si = squadList.getSelectedIndex();
		if (si <= 0 || si >= Formations.PLAYER_COUNT) {
			if (si == 0) {
				roleBox.addItem(new Role(0));
			} else {
				roleBox.setEnabled(false);
			}
		} else {
			int selPos = Formations.getPosition(of, team, altBox.getSelectedIndex(), si);
			int count = 0;
			boolean free;
			boolean cbt = false;
			int pos;
			Role last = null;
			Role first = new Role(selPos);
			roleBox.addItem(first);
			for (int r = 1; r < 41; r++) {
				// if ((isDef(r) && isDef(pos)) || isMid(r) && isMid(pos)) ||
				// isAtt(r) && isAtt(pos))) {
				free = true;
				// } else {
				if (r == 5) {
					free = false;
				} else {

					if (r == 15) {
						for (int p = 0; free && p < Formations.PLAYER_COUNT; p++) {
							pos = Formations.getPosition(of, team, altBox
									.getSelectedIndex(), p);
							if (pos != selPos) {
								if (pos == 8 || pos == 22) {
									free = false;
								}
							}
						}
					}

					if (r == 16) {
						for (int p = 0; free && p < Formations.PLAYER_COUNT; p++) {
							pos = Formations.getPosition(of, team, altBox
									.getSelectedIndex(), p);
							if (pos != selPos) {
								if (pos == 9 || pos == 23) {
									free = false;
								}
							}
						}
					}

					if (selPos != 15 && (r == 8 || r == 22)) {
						for (int p = 0; free && p < Formations.PLAYER_COUNT; p++) {
							pos = Formations.getPosition(of, team, altBox
									.getSelectedIndex(), p);
							if (pos == 15) {
								free = false;
							}
						}
					}

					if (selPos != 16 && (r == 9 || r == 23)) {
						for (int p = 0; free && p < Formations.PLAYER_COUNT; p++) {
							pos = Formations.getPosition(of, team, altBox
									.getSelectedIndex(), p);
							if (pos == 16) {
								free = false;
							}
						}
					}

					if (isDef(selPos)) {
						if (def.get() <= 2 && !isDef(r)) {
							free = false;
						}
						if (mid.get() >= 6 && isMid(r)) {
							free = false;
						}
						if (atk.get() >= 5 && isAtt(r)) {
							free = false;
						}
					}

					if (isMid(selPos)) {
						if (mid.get() <= 2 && !isMid(r)) {
							free = false;
						}
						if (def.get() >= 5 && isDef(r)) {
							free = false;
						}
						if (atk.get() >= 5 && isAtt(r)) {
							free = false;
						}
					}

					if (isAtt(selPos)) {
						if (atk.get() <= 1 && !isAtt(r)) {
							free = false;
						}
						if (mid.get() >= 6 && isMid(r)) {
							free = false;
						}
						if (def.get() >= 5 && isDef(r)) {
							free = false;
						}
					}
				}
				// }

				for (int p = 0; free && p < Formations.PLAYER_COUNT; p++) {
					// System.out.println(r + ", " + p);
					pos = Formations.getPosition(of, team, altBox.getSelectedIndex(), p);
					// System.out.println(a + "=" + of.data[a]);
					if (pos == r) {
						free = false;
					}
				}
				if (free) {
					Role role = new Role(r);
					if (!(first.name.equals(role.name))) {
						if (last == null) {
							last = role;
							roleBox.addItem(role);
							count++;
						} else {
							if (!(last.name.equals(role.name))) {
								if (!role.name.equals("CBT")
										|| (role.name.equals("CBT") && !cbt)) {
									last = new Role(r);
									roleBox.addItem(last);
									count++;
								}
							}
						}
						if (role.name.equals("CBT")) {
							cbt = true;
						}
					}
				}
			}

			// roleBox.setSelectedItem();
		}

		roleBox.setActionCommand("y");
	}

	private void countFormations() {
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
			} else if (isAtt(pos)) {
				atk.incrementAndGet();
			}
		}

		//System.out.println(def +" " +mid +" " +mid2 +" " +atk);
		formNamesBox.setActionCommand("n");

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

		ArrayList<String> formNames = new ArrayList<String>(Arrays.asList(Formations.FORM_NAMES));
		formNames.add(0, myForm);
		DefaultComboBoxModel<String> model
				= new DefaultComboBoxModel<String>(formNames.toArray(new String[formNames.size()]));
		formNamesBox.setModel(model);
		formNamesBox.setActionCommand("y");
		// System.out.println(def + "-" + mid + "-" + atk);
	}

	private void saveStrategyAsPNG() {
		int returnVal = pngChooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dest = pngChooser.getSelectedFile();
			dest = Files.addExtension(dest, Files.PNG);

			if (dest.exists()) {
				int n = JOptionPane.showConfirmDialog(null, dest.getName()
								+ "\nAlready exists in:\n" + dest.getParent()
								+ "\nAre you sure you want to overwrite this file?",
						"Overwrite:  " + dest.getName(),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null);
				if (n == 0) {
					boolean deleted = dest.delete();
					if (!deleted) {
						JOptionPane.showMessageDialog(null,
								"Could not access file", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					return;
				}
			}
			//log.debug("{}, {}", dest, slotChooser.slot);

			if (Images.saveComponentAsImage(pitchPanel, dest)) {
				JOptionPane.showMessageDialog(null, dest.getName()
								+ "\nSaved in:\n" + dest.getParent(),
						"File Successfully Saved",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Could not access file", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean isDef(int r) {
		boolean result = false;
		if (r > 0 && r < 10) {
			result = true;
		}
		return result;
	}

	private boolean isMid(int r) {
		boolean result = false;
		if (r > 9 && r < 29) {
			result = true;
		}
		return result;
	}

	private boolean isAtt(int r) {
		boolean result = false;
		if (r > 28 && r < 41) {
			result = true;
		}
		return result;
	}

	public void dragEnter(DropTargetDragEvent event) {
	}

	public void dragExit(DropTargetEvent event) {
	}

	public void dragOver(DropTargetDragEvent event) {
		int i = squadList.locationToIndex(event.getLocation());
		Player p = squadList.getModel().getElementAt(i);
		squadList.setSelectedIndex(i);
		if (i != -1 && i != sourceIndex && p.getIndex() != 0) {
			event.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			event.rejectDrag();
		}
	}

	public void drop(DropTargetDropEvent event) {
		Transferable transferable = event.getTransferable();
		int ti = squadList.getSelectedIndex();
		if (transferable.isDataFlavorSupported(PlayerTransferable.getDataFlavor())) {
			event.acceptDrop(DnDConstants.ACTION_MOVE);

			int tb = Formations.getSlot(of, team, sourceIndex);
			Formations.setSlot(of, team, sourceIndex, Formations.getSlot(of, team, ti));
			Formations.setSlot(of, team, ti, tb);
			if (sourceIndex < Formations.PLAYER_COUNT && ti < Formations.PLAYER_COUNT) {
				for (int j = 0; j < 6; j++) {
					if (Formations.getJob(of, team, j) == sourceIndex) {
						Formations.setJob(of, team, j, ti);
					} else if (Formations.getJob(of, team, j) == ti) {
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
			isOk = false;
			int tt = team;
			if (team > 66) {
				tt = team + 8;
			}
			numList.refresh(tt);
			squadList.refresh(team, false);
			teamSettingPan.refresh(team);
			strategyPan.refresh(team);
			pitchPanel.repaint();
			// ti = -1;
			isOk = true;
			event.getDropTargetContext().dropComplete(true);
		} else {
			event.rejectDrop();
		}
	}

	public void dropActionChanged(DropTargetDragEvent event) {
	}

	public void dragGestureRecognized(DragGestureEvent event) {
		sourceIndex = squadList.getSelectedIndex();
		Player p = squadList.getSelectedValue();
		if (sourceIndex != -1 && p.getIndex() != 0) {
			posList.selectPos(squadList, sourceIndex);

			roleBox.setActionCommand("n");
			roleBox.removeAllItems();
			roleBox.setEnabled(false);
			roleBox.setActionCommand("y");
			pitchPanel.setSelectedIndex(-1);
			atkDefPanel.setSelectedIndex(-1);
			pitchPanel.repaint();
			atkDefPanel.repaint();
			PlayerTransferable playerTran = new PlayerTransferable(p);
			event.getDragSource().startDrag(event, null, playerTran, this);
		} else {
			// System.out.println( "nothing was selected");
		}
	}

	public void dragDropEnd(DragSourceDropEvent event) {
		squadList.clearSelection();
		posList.clearSelection();

		/*
		 * //if (event.getDropSuccess()){} int ti =
		 * squadList.getSelectedIndex(); updateRoleBox(); if (ti >= 0 && ti <
		 * 11) { pitchPanel.selected = ti; atkDefPanel.selected = ti; } else {
		 * pitchPanel.selected = -1; atkDefPanel.selected = -1; }
		 * pitchPanel.repaint(); atkDefPanel.repaint(); posList.clearSelection();
		 */
	}

	public void dragEnter(DragSourceDragEvent event) {
	}

	public void dragExit(DragSourceEvent event) {
	}

	public void dragOver(DragSourceDragEvent event) {
	}

	public void dropActionChanged(DragSourceDragEvent event) {
	}

}
