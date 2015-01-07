package editor.ui;

import editor.data.OptionFile;
import editor.data.Player;
import editor.data.Stat;
import editor.data.Stats;
import editor.util.Resources;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlayerImportDialog extends JDialog implements ListSelectionListener, MouseListener {
	private final OptionFile of;
	private final OptionFile of2;

	private volatile int index = 0;
	private volatile int replacement = 0;

	private JLabel fileLabel;
	private SelectByTeam playerList;
	private InfoPanel infoPanel;
	private JRadioButton allButton;
	private JRadioButton statsButton;

	public PlayerImportDialog(Frame owner, OptionFile of, OptionFile of2) {
		super(owner, Resources.getMessage("imPlayer.title"), true);
		if (null == of) throw new NullPointerException("of");
		if (null == of2) throw new NullPointerException("of2");
		this.of = of;
		this.of2 = of2;

		initComponents();
	}

	private void initComponents() {
		fileLabel = new JLabel(Resources.getMessage("import.label", ""));

		playerList = new SelectByTeam(of2, false);
		playerList.getSquadList().addListSelectionListener(this);
		playerList.getSquadList().addMouseListener(this);

		infoPanel = new InfoPanel(of2);
		CancelButton cancelButton = new CancelButton(this);

		allButton = new JRadioButton(Resources.getMessage("imPlayer.all"));
		statsButton = new JRadioButton(Resources.getMessage("imPlayer.stats"));
		JRadioButton exceptStatsBtn = new JRadioButton(Resources.getMessage("imPlayer.exceptS"));

		ButtonGroup importRadio = new ButtonGroup();
		importRadio.add(allButton);
		importRadio.add(statsButton);
		importRadio.add(exceptStatsBtn);

		allButton.setSelected(true);

		JPanel topPanel = new JPanel(new GridLayout(4, 1));
		topPanel.add(fileLabel);
		topPanel.add(allButton);
		topPanel.add(statsButton);
		topPanel.add(exceptStatsBtn);

		getContentPane().add(playerList, BorderLayout.WEST);
		getContentPane().add(infoPanel, BorderLayout.CENTER);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(topPanel, BorderLayout.NORTH);

		setResizable(false);
		pack();
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	public void show(int playerId) {
		this.index = playerId;
		setVisible(true);
	}

	public void refresh() {
		playerList.refresh();
		fileLabel.setText(Resources.getMessage("import.label", of2.getFilename()));
		index = 0;
		replacement = 0;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (null == e) throw new NullPointerException("e");
		if (e.getValueIsAdjusting())
			return;

		if (!playerList.getSquadList().isSelectionEmpty()) {
			Player p = playerList.getSquadList().getSelectedValue();
			infoPanel.refresh(p.getIndex(), 0);
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (null == e) throw new NullPointerException("e");
		if (!(e.getSource() instanceof JList)) throw new IllegalArgumentException("e");

		if (e.getClickCount() < 2)
			return;

		JList list = (JList) e.getSource();
		if (!(list.getSelectedValue() instanceof Player))
			return;

		int pid = ((Player) list.getSelectedValue()).getIndex();
		if (pid > 0) {
			replacement = pid;
			importPlayer();

			setVisible(false);
		}
	}

	private void importPlayer() {
		if (allButton.isSelected()) {
			importPlayerAll();
		} else if (statsButton.isSelected()) {
			importPlayerStats();
		} else {
			importPlayerExceptStats();
		}
	}

	private void importPlayerExceptStats() {
		int adr = Player.getOffset(index);
		int repAdr = Player.getOffset(replacement);

		byte[] temp = new byte[Player.SIZE];
		System.arraycopy(of2.getData(), repAdr, temp, 0, temp.length);

		importPlayerStats(of, index, of2, replacement);
		System.arraycopy(of2.getData(), repAdr, of.getData(), adr, Player.SIZE);

		Stats.setValue(of, index, Stats.NAME_EDITED, 1);
		Stats.setValue(of, index, Stats.CALL_EDITED, 1);
		Stats.setValue(of, index, Stats.SHIRT_EDITED, 1);

		System.arraycopy(temp, 0, of2.getData(), repAdr, temp.length);
	}

	private static void importPlayerStats(OptionFile of2, int replacement, OptionFile of, int index) {
		Stats.setValue(of, index, Stats.NATIONALITY, Stats.getValue(of2, replacement, Stats.NATIONALITY));
		Stats.setValue(of, index, Stats.AGE, Stats.getValue(of2, replacement, Stats.AGE));
		Stats.setValue(of, index, Stats.HEIGHT, Stats.getValue(of2, replacement, Stats.HEIGHT));
		Stats.setValue(of, index, Stats.WEIGHT, Stats.getValue(of2, replacement, Stats.WEIGHT));
		Stats.setValue(of, index, Stats.FOOT, Stats.getValue(of2, replacement, Stats.FOOT));
		Stats.setValue(of, index, Stats.FAVORITE_SIDE, Stats.getValue(of2, replacement, Stats.FAVORITE_SIDE));
		Stats.setValue(of, index, Stats.WEAK_FOOT_ACC, Stats.getValue(of2, replacement, Stats.WEAK_FOOT_ACC));
		Stats.setValue(of, index, Stats.WEAK_FOOT_FREQ, Stats.getValue(of2, replacement, Stats.WEAK_FOOT_FREQ));
		Stats.setValue(of, index, Stats.CONDITION, Stats.getValue(of2, replacement, Stats.CONDITION));
		Stats.setValue(of, index, Stats.CONSISTENCY, Stats.getValue(of2, replacement, Stats.CONSISTENCY));
		Stats.setValue(of, index, Stats.INJURY, Stats.getValue(of2, replacement, Stats.INJURY));
		Stats.setValue(of, index, Stats.DRIBBLE_STYLE, Stats.getValue(of2, replacement, Stats.DRIBBLE_STYLE));
		Stats.setValue(of, index, Stats.PK_STYLE, Stats.getValue(of2, replacement, Stats.PK_STYLE));
		Stats.setValue(of, index, Stats.FREE_KICK, Stats.getValue(of2, replacement, Stats.FREE_KICK));
		Stats.setValue(of, index, Stats.DK_STYLE, Stats.getValue(of2, replacement, Stats.DK_STYLE));
		Stats.setValue(of, index, Stats.REG_POS, Stats.getValue(of2, replacement, Stats.REG_POS));

		for (Stat s : Stats.ROLES) {
			Stats.setValue(of, index, s, Stats.getValue(of2, replacement, s));
		}
		for (Stat s : Stats.ABILITY99) {
			Stats.setValue(of, index, s, Stats.getValue(of2, replacement, s));
		}
		for (Stat s : Stats.ABILITY_SPECIAL) {
			Stats.setValue(of, index, s, Stats.getValue(of2, replacement, s));
		}
	}

	private void importPlayerStats() {
		importPlayerStats(of2, replacement, of, index);

		Stats.setValue(of, index, Stats.ABILITY_EDITED, 1);
	}

	private void importPlayerAll() {
		int adr = Player.getOffset(index);
		int repAdr = Player.getOffset(replacement);
		System.arraycopy(of2.getData(), repAdr, of.getData(), adr, Player.SIZE);

		Stats.setValue(of, index, Stats.NAME_EDITED, 1);
		Stats.setValue(of, index, Stats.CALL_EDITED, 1);
		Stats.setValue(of, index, Stats.SHIRT_EDITED, 1);
		Stats.setValue(of, index, Stats.ABILITY_EDITED, 1);
	}

}
