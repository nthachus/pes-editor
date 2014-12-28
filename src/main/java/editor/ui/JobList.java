package editor.ui;

import editor.Formations;
import editor.data.OptionFile;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;

public class JobList extends JList<String> implements ListSelectionListener {
	private final OptionFile of;
	private final int offset;
	private final String job;

	private volatile int team;
	private volatile boolean isOk = false;

	public JobList(OptionFile of, int offset, String job, Color colour) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;
		this.offset = offset;
		this.job = job;

		refresh(-1);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(32);
		setSelectionBackground(colour);
		setSelectionForeground(Color.BLACK);
		//setBackground(new Color(255, 255, 224));
		//setFont(new Font("Dialog", Font.BOLD, 12));

		addListSelectionListener(this);
	}

	public int getTeam() {
		return team;
	}

	public void refresh(int team) {
		isOk = false;
		this.team = team;

		String[] posJobs = new String[11];
		Arrays.fill(posJobs, " ");

		int p = 0;
		if (team < 0) {
			posJobs[0] = job;
		} else {
			p = Formations.getJob(of, team, offset);
			if (p >= 0 && p < posJobs.length) {
				posJobs[p] = job;
			}
		}

		setListData(posJobs);
		setSelectedIndex(p);

		isOk = true;
	}

	public void valueChanged(ListSelectionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if (!evt.getValueIsAdjusting()) {
			if (!isSelectionEmpty() && isOk) {
				Formations.setJob(of, team, offset, getSelectedIndex());
				refresh(team);
			}
		}
	}

}
