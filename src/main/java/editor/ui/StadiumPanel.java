package editor.ui;

import editor.data.OptionFile;
import editor.data.Stadiums;
import editor.util.Resources;
import editor.util.swing.JList;
import editor.util.swing.JTextFieldLimit;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StadiumPanel extends JPanel implements ActionListener, ListSelectionListener {
	private final OptionFile of;
	private final TeamPanel teamPanel;

	private/* final*/ JTextField editor;
	private/* final*/ JList<String> list;

	public StadiumPanel(OptionFile of, TeamPanel tp) {
		super();
		if (null == of) throw new NullPointerException("of");
		if (null == tp) throw new NullPointerException("tp");
		this.of = of;
		this.teamPanel = tp;

		initComponents();
		//refresh();
	}

	private void initComponents() {
		editor = new JTextField(15);
		editor.setToolTipText(Resources.getMessage("stadium.tooltip"));
		editor.setDocument(new JTextFieldLimit(Stadiums.NAME_LEN));
		editor.addActionListener(this);

		list = new JList<String>();
		list.setFixedCellHeight(Math.round(Editor.LINE_HEIGHT * getFont().getSize()));
		list.setVisibleRowCount(Stadiums.TOTAL);
		list.addListSelectionListener(this);

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(list);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("stadium.title")));
		contentPane.add(scroll);
		contentPane.add(editor);

		add(contentPane);
	}

	public void refresh() {
		list.setListData(Stadiums.get(of));
	}

	public void actionPerformed(ActionEvent evt) {
		int stadiumId = list.getSelectedIndex();
		String text = editor.getText();

		if (stadiumId >= 0 && null != text && text.length() <= Stadiums.NAME_LEN) {
			if (!text.equals(Stadiums.get(of, stadiumId))) {
				Stadiums.set(of, stadiumId, text);
				teamPanel.refresh();
				refresh();
			}

			if (stadiumId < Stadiums.TOTAL - 1) {
				list.setSelectedIndex(stadiumId + 1);
				list.ensureIndexIsVisible(list.getSelectedIndex());
			}
		}
	}

	public void valueChanged(ListSelectionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if (!evt.getValueIsAdjusting()) {
			int stadiumId = list.getSelectedIndex();
			if (stadiumId < 0) {
				editor.setText("");
			} else {
				editor.setText(Stadiums.get(of, stadiumId));
				editor.selectAll();
			}
		}
	}

}
