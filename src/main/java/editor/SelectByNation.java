package editor;

import editor.data.OptionFile;
import editor.data.Stats;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectByNation extends JPanel {
	private final NationalityList freeList;
	private final JComboBox<String> nationBox;
	private final JButton sortButton;

	private volatile boolean isAlphaOrder = true;

	public SelectByNation(OptionFile of) {
		super(new BorderLayout());
		freeList = new NationalityList(of);

		sortButton = new JButton("Alpha Order");
		sortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				if (isAlphaOrder) {
					sortButton.setText("Index Order");
					isAlphaOrder = false;
				} else {
					sortButton.setText("Alpha Order");
					isAlphaOrder = true;
				}
				int i = nationBox.getSelectedIndex();
				freeList.refresh(i, isAlphaOrder);
			}
		});
		String[] boxChoice = new String[Stats.NATION.length + 5];
		// boxChoice[boxChoice.length - 6] = "Unused";
		boxChoice[boxChoice.length - 5] = "ML Old";
		boxChoice[boxChoice.length - 4] = "ML Young";
		boxChoice[boxChoice.length - 3] = "Duplicates?";
		boxChoice[boxChoice.length - 2] = "Free Agents";
		boxChoice[boxChoice.length - 1] = "All Players";
		System.arraycopy(Stats.NATION, 0, boxChoice, 0, Stats.NATION.length);
		nationBox = new JComboBox<String>(boxChoice);
		nationBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {

				int i = nationBox.getSelectedIndex();
				// System.out.println(i);
				if (i != -1) {
					// if (i == 0) {
					// freeList.refresh(999);
					// } else {
					freeList.refresh(i, isAlphaOrder);
					// }
				}
			}
		});
		nationBox.setMaximumRowCount(32);
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
		// refresh();
		setPreferredSize(new Dimension(164, 601));
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
