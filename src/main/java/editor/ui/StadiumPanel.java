package editor.ui;

import editor.TeamPanel;
import editor.data.OptionFile;
import editor.data.Stadiums;
import editor.util.Strings;

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
		editor.setToolTipText(Strings.getMessage("stadium.tooltip"));
		editor.addActionListener(this);

		list = new JList<String>();
		list.addListSelectionListener(this);
		list.setVisibleRowCount(21);

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(list);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createTitledBorder(Strings.getMessage("stadium.title")));
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

		if (stadiumId >= 0 && !Strings.isEmpty(text) && text.length() <= Stadiums.NAME_LEN) {
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
