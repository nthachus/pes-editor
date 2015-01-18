package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Stats;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class SelectByNation extends JPanel implements ActionListener {
	private static final long serialVersionUID = 228073822479688825L;
	private static final Logger log = LoggerFactory.getLogger(SelectByNation.class);

	private final NationalityList freeList;
	private/* final*/ JComboBox nationBox;
	private/* final*/ JButton sortButton;

	private volatile boolean isAlphaOrder = true;

	public SelectByNation(OptionFile of) {
		super(new BorderLayout());
		freeList = new NationalityList(of);

		log.debug("Initialize By Nation dropdown #{}", hashCode());
		initComponents();
		//refresh();
	}

	private void initComponents() {
		sortButton = new JButton(Resources.getMessage("nation.sortAlpha"));
		sortButton.setActionCommand("Sort");
		sortButton.addActionListener(this);

		String[] boxChoice = getAllNations();
		nationBox = new JComboBox<String>(boxChoice);
		nationBox.setMaximumRowCount(Formations.CLUB_TEAM_SIZE);
		nationBox.addActionListener(this);

		freeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		freeList.setLayoutOrientation(JList.VERTICAL);
		freeList.setVisibleRowCount(Formations.PLAYER_COUNT);

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(freeList);

		add(nationBox, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(sortButton, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(164, 601));
	}

	public static String[] getExtraNations() {
		String s = Resources.getMessage("nation.extras");
		return s.split("\\s*,\\s*");
	}

	private static String[] getAllNations() {
		String[] arr = getExtraNations();

		ArrayList<String> list = new ArrayList<String>(Arrays.asList(Stats.NATION));
		list.addAll(Arrays.asList(arr));

		return list.toArray(new String[list.size()]);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.debug("Try to perform action: {}", evt.getActionCommand());

		if ("Sort".equalsIgnoreCase(evt.getActionCommand())) {
			sortList();
		} else if ("y".equalsIgnoreCase(evt.getActionCommand())) {
			refreshForNation();
		}
	}

	private void sortList() {
		if (isAlphaOrder) {
			sortButton.setText(Resources.getMessage("nation.sortIndex"));
			isAlphaOrder = false;
		} else {
			sortButton.setText(Resources.getMessage("nation.sortAlpha"));
			isAlphaOrder = true;
		}

		refreshForNation();
	}

	public void refreshForNation() {
		int i = nationBox.getSelectedIndex();
		if (i >= 0) {
			freeList.refresh(i, isAlphaOrder);
		}

		log.debug("Select completed By Nation {}", i);
	}

	public NationalityList getFreeList() {
		return freeList;
	}

	public boolean isAlphaOrder() {
		return isAlphaOrder;
	}

	public void refresh() {
		log.debug("Reload By Nation dropdown #{}", hashCode());

		nationBox.setActionCommand("n");
		nationBox.setSelectedIndex(nationBox.getItemCount() - 1);

		refreshForNation();

		nationBox.setActionCommand("y");
		// DEBUG
		log.debug("Refresh completed on By Nation dropdown #{}", hashCode());
	}

}
