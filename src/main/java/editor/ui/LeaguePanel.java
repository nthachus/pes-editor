package editor.ui;

import editor.data.Leagues;
import editor.data.OptionFile;
import editor.util.Strings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaguePanel extends JPanel implements ActionListener, ListSelectionListener {
	private final OptionFile of;

	private final JTextField editor;
	private final JList<String> list;

	public LeaguePanel(OptionFile of) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		editor = new JTextField(15);
		editor.setToolTipText(Strings.getMessage("league.tooltip"));
		editor.addActionListener(this);

		list = new JList<String>();
		list.setFixedCellHeight(Math.round(Editor.LINE_HEIGHT * getFont().getSize()));
		list.addListSelectionListener(this);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createTitledBorder(Strings.getMessage("league.title")));
		contentPane.add(list);
		contentPane.add(editor);

		add(contentPane);
		//refresh();
	}

	public void refresh() {
		list.setListData(Leagues.get(of));
	}

	public void actionPerformed(ActionEvent evt) {
		int id = list.getSelectedIndex();
		String text = editor.getText();

		if (id >= 0 && null != text && text.length() <= Leagues.NAME_LEN) {
			if (!text.equals(Leagues.get(of, id))) {
				Leagues.set(of, id, text);
				refresh();
			}

			if (id < Leagues.TOTAL - 1) {
				list.setSelectedIndex(id + 1);
			}
		}
	}

	public void valueChanged(ListSelectionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if (!evt.getValueIsAdjusting()) {
			int id = list.getSelectedIndex();
			if (id < 0) {
				editor.setText("");
			} else {
				editor.setText(Leagues.get(of, id));
				editor.selectAll();
			}
		}
	}

}
