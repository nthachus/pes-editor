package editor.ui;

import editor.data.*;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class KitImportDialog extends JDialog implements MouseListener {
	private static final long serialVersionUID = 6176947558253913789L;
	private static final Logger log = LoggerFactory.getLogger(KitImportDialog.class);

	private final OptionFile of2;
	private volatile int index = 0;

	private JLabel fileLabel;
	private JList/*<KitItem>*/ list;

	public KitImportDialog(Frame owner, OptionFile of2) {
		super(owner, Resources.getMessage("kitImport.title"), true);
		if (null == of2) throw new NullPointerException("of2");
		this.of2 = of2;

		log.debug("Kit Import dialog is initializing..");
		initComponents();
	}

	private void initComponents() {
		fileLabel = new JLabel(Resources.getMessage("import.label", ""));

		list = new JList/*<KitItem>*/();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(20);
		//list.addListSelectionListener(this);
		list.addMouseListener(this);

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(list);

		JButton cancelButton = new CancelButton(this);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(fileLabel, BorderLayout.NORTH);
		contentPane.add(scroll, BorderLayout.CENTER);
		contentPane.add(cancelButton, BorderLayout.SOUTH);

		getContentPane().add(contentPane);
		//setPreferredSize(new Dimension(462, 677));
		//setResizable(false);
	}

	public int show(int teamId) {
		log.debug("Show Kit Import dialog for team: {}", teamId);
		index = -1;

		refresh(teamId);
		setVisible(true);

		log.debug("Team {} was selected to import Kit", index);
		return index;
	}

	@SuppressWarnings("unchecked")
	public void refresh(int teamId) {
		log.debug("Try to refresh importable Kit teams for team: {}", teamId);

		Vector<KitItem> model = new Vector<KitItem>(Clubs.TOTAL);
		if (teamId < Clubs.TOTAL) {
			// Clubs
			addClubKitItem(model, teamId);

			for (int c = 0; c < Clubs.TOTAL; c++) {
				if (c != teamId && !Kits.isLicensed(of2, c))
					addClubKitItem(model, c);
			}
		} else {
			// National teams
			addNationKitItem(model, teamId);

			for (int n = Clubs.TOTAL, e = Clubs.TOTAL + Squads.NATION_COUNT + Squads.CLASSIC_COUNT; n < e; n++) {
				if (n != teamId && !Kits.isLicensed(of2, n))
					addNationKitItem(model, n);
			}
		}

		model.trimToSize();
		list.setListData(model);

		fileLabel.setText(Resources.getMessage("import.label", of2.getFilename()));

		pack();
		log.debug("Refresh completed on {} Kit teams", model.size());
	}

	private void addClubKitItem(List<KitItem> model, int clubId) {
		model.add(new KitItem(Clubs.getName(of2, clubId), clubId));
	}

	private void addNationKitItem(List<KitItem> model, int teamId) {
		if (teamId < Clubs.TOTAL + Squads.NATION_COUNT)
			model.add(new KitItem(Stats.NATION[teamId - Clubs.TOTAL], teamId));
		else
			model.add(new KitItem(Squads.EXTRAS[teamId - Clubs.TOTAL - Squads.NATION_COUNT], teamId));
	}

	public void mousePressed(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mouseClicked(MouseEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		int clicks = evt.getClickCount();
		if (clicks > 1) {
			KitItem item = (KitItem) list.getSelectedValue();
			// DEBUG
			log.debug("Perform double-click on Kit team: {}", item);
			if (null != item) {
				index = item.teamId;
				setVisible(false);
			}
		}
	}

	private static class KitItem implements Serializable {
		private static final long serialVersionUID = 3682840498301996863L;

		private final String team;
		private final int teamId;

		public KitItem(String teamName, int teamId) {
			this.team = teamName;
			this.teamId = teamId;
		}

		@Override
		public String toString() {
			return Strings.isEmpty(team) ? " " : team;
		}
	}

}
