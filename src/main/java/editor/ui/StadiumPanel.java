package editor.ui;

import editor.data.OptionFile;
import editor.data.Stadiums;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StadiumPanel extends JPanel implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 5755796824210992527L;
	private static final Logger log = LoggerFactory.getLogger(StadiumPanel.class);

	private final OptionFile of;
	private final TeamPanel teamPanel;

	private/* final*/ JTextField editor;
	private/* final*/ JList/*<String>*/ list;

	public StadiumPanel(OptionFile of, TeamPanel tp) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == tp) {
			throw new NullArgumentException("tp");
		}
		this.of = of;
		this.teamPanel = tp;

		log.debug("Initialize Stadium panel by Team panel: {}", Strings.valueOf(tp));
		initComponents();

		//refresh();
	}

	private void initComponents() {
		editor = new JTextField(Math.round(0.25f * Stadiums.NAME_LEN));
		editor.setToolTipText(Resources.getMessage("stadium.tooltip"));
		editor.setDocument(new JTextFieldLimit(Stadiums.NAME_LEN * 2 / 3));
		editor.addActionListener(this);

		list = new JList/*<String>*/();
		list.setFixedCellHeight(Math.round(4f * getFont().getSize() / 3));
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
		log.info("Try to reload all Stadiums from OF: {}", of.getFilename());

		list.setListData(Stadiums.get(of));
	}

	public void actionPerformed(ActionEvent evt) {
		int stadiumId = list.getSelectedIndex();
		String text = editor.getText();
		// DEBUG
		log.info("Perform updating for stadium {} to '{}'", stadiumId, text);

		if (stadiumId >= 0 && null != text && text.length() <= Stadiums.NAME_LEN * 2 / 3) {
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
		if (null == evt) {
			throw new NullArgumentException("evt");
		}

		if (!evt.getValueIsAdjusting()) {
			int stadiumId = list.getSelectedIndex();
			// DEBUG
			log.debug("Stadium list selection was changed to: {}", stadiumId);

			if (stadiumId < 0) {
				editor.setText(Strings.EMPTY);
			} else {
				editor.setText(Stadiums.get(of, stadiumId));
				editor.selectAll();
			}
		}
	}

}
