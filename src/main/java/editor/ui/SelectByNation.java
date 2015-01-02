package editor.ui;

import editor.NationalityList;
import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class SelectByNation extends JPanel {
	private final NationalityList freeList;
	private/* final*/ JComboBox<String> nationBox;
	private/* final*/ JButton sortButton;

	private volatile boolean isAlphaOrder = true;

	public SelectByNation(OptionFile of) {
		super(new BorderLayout());
		freeList = new NationalityList(of);

		initComponents();
		//refresh();
	}

	private void initComponents() {
		sortButton = new JButton(Strings.getMessage("nation.sortAlpha"));
		sortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSort(evt);
			}
		});

		String[] boxChoice = getAllNations();
		nationBox = new JComboBox<String>(boxChoice);
		nationBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onSelectNation(evt);
			}
		});
		nationBox.setMaximumRowCount(Formations.CLUB_TEAM_SIZE);

		freeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		freeList.setLayoutOrientation(JList.VERTICAL);
		freeList.setVisibleRowCount(11);

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(freeList);

		add(nationBox, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(sortButton, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(164, 601));
	}

	private static String[] getAllNations() {
		String s = Strings.getMessage("nation.extras");
		String[] arr = s.split("\\s*,\\s*");

		ArrayList<String> list = new ArrayList<String>(Arrays.asList(Stats.NATION));
		list.addAll(Arrays.asList(arr));

		return list.toArray(new String[list.size()]);
	}

	private void onSort(ActionEvent evt) {
		if (isAlphaOrder) {
			sortButton.setText(Strings.getMessage("nation.sortIndex"));
			isAlphaOrder = false;
		} else {
			sortButton.setText(Strings.getMessage("nation.sortAlpha"));
			isAlphaOrder = true;
		}

		int i = nationBox.getSelectedIndex();
		freeList.refresh(i, isAlphaOrder);
	}

	private void onSelectNation(ActionEvent evt) {
		int i = nationBox.getSelectedIndex();
		if (i >= 0) {
			freeList.refresh(i, isAlphaOrder);
		}
	}

	public NationalityList getFreeList() {
		return freeList;
	}

	public JComboBox<String> getNationBox() {
		return nationBox;
	}

	public boolean isAlphaOrder() {
		return isAlphaOrder;
	}

	public void refresh() {
		nationBox.setSelectedIndex(nationBox.getItemCount() - 1);
		freeList.refresh(nationBox.getSelectedIndex(), isAlphaOrder);
	}

}