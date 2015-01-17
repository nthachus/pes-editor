package editor.ui;

import editor.data.Leagues;
import editor.data.OptionFile;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaguePanel extends JPanel implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -4268048687680148383L;
	private static final Logger log = LoggerFactory.getLogger(LeaguePanel.class);

	private final OptionFile of;

	private/* final*/ JTextField editor;
	private/* final*/ JList/*<String>*/ list;

	public LeaguePanel(OptionFile of) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		log.debug("League panel is initializing..");
		initComponents();
		//refresh();
	}

	private void initComponents() {
		editor = new JTextField(Math.round(0.25f * Leagues.NAME_LEN));
		editor.setToolTipText(Resources.getMessage("league.tooltip"));
		editor.setDocument(new JTextFieldLimit(Leagues.NAME_LEN * 2 / 3));
		editor.addActionListener(this);

		list = new JList/*<String>*/();
		list.setFixedCellHeight(Math.round(1.25f * getFont().getSize()));
		list.addListSelectionListener(this);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("league.title")));
		contentPane.add(list);
		contentPane.add(editor);

		add(contentPane);
	}

	@SuppressWarnings("unchecked")
	public void refresh() {
		log.debug("Try to reload all leagues to List");

		String[] model = Leagues.get(of);
		list.setListData(model);
	}

	public void actionPerformed(ActionEvent evt) {
		int id = list.getSelectedIndex();
		String text = editor.getText();
		// DEBUG
		log.debug("Perform update league at {} to: {}", id, text);

		if (id >= 0 && null != text && text.length() <= Leagues.NAME_LEN * 2 / 3) {
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
		if (evt.getValueIsAdjusting()) return;

		int id = list.getSelectedIndex();
		// DEBUG
		log.debug("League list selection was changed to: {}", id);
		if (id < 0) {
			editor.setText("");
		} else {
			editor.setText(Leagues.get(of, id));
			editor.selectAll();
		}
	}

}
