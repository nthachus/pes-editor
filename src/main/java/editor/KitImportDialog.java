package editor;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.ui.CancelButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class KitImportDialog extends JDialog implements MouseListener {
	private final OptionFile of2;
	private int index;

	private JLabel fileLabel;
	private JList<KitItem> list;

	public KitImportDialog(Frame owner, OptionFile opf2) {// TODO: !!!
		super(owner, "Import Kit", true);
		of2 = opf2;
		JPanel panel = new JPanel(new BorderLayout());
		fileLabel = new JLabel("From:");
		list = new JList<KitItem>();
		// list.addListSelectionListener(this);
		list.addMouseListener(this);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(20);
		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(list);
		CancelButton cancelButton = new CancelButton(this);
		panel.add(fileLabel, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(panel);
		index = 0;

		//setPreferredSize(new Dimension(462, 677));
		//setResizable(false);
	}

	public int show(int i) {
		index = -1;
		refresh(i);
		setVisible(true);
		return index;
	}

	public void refresh(int i) {
		DefaultListModel<KitItem> model = new DefaultListModel<KitItem>();
		model.removeAllElements();

		if (i < Clubs.TOTAL) {
			model.addElement(new KitItem(Clubs.getName(of2, i), i));
			for (int c = 0; c < Clubs.TOTAL; c++) {
				if (c != i && !Kits.isLic(of2, c)) {
					model.addElement(new KitItem(Clubs.getName(of2, c), c));
				}
			}
		} else {
			if (i - Clubs.TOTAL > 59) {
				model.addElement(new KitItem(Squads.EXTRAS[i - Clubs.TOTAL - 60], i));
			} else {
				model.addElement(new KitItem(Stats.NATION[i - Clubs.TOTAL], i));
			}
			for (int n = Clubs.TOTAL; n < Clubs.TOTAL + 67; n++) {
				if (n != i && !Kits.isLic(of2, n)) {
					if (n - Clubs.TOTAL > 59) {
						model.addElement(new KitItem(Squads.EXTRAS[n - Clubs.TOTAL - 60], n));
					} else {
						model.addElement(new KitItem(Stats.NATION[n - Clubs.TOTAL], n));
					}
				}
			}
		}
		model.trimToSize();
		list.setModel(model);
		fileLabel.setText("  From:  " + of2.getFilename());
		pack();
	}

	/*
	 * public void valueChanged(ListSelectionEvent e) { if
	 * (e.getValueIsAdjusting() == false) { if (!list.isSelectionEmpty()) { } }
	 * }
	 */

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		int clicks = e.getClickCount();
		JList list = (JList) (e.getSource());
		KitItem i = (KitItem) list.getSelectedValue();
		if (clicks == 2 && i != null) {
			index = i.num;
			setVisible(false);
		}
	}

	private class KitItem {
		String team;
		int num;

		public KitItem(String s, int n) {
			team = s;
			num = n;
		}

		public String toString() {
			return team;
		}
	}

}
