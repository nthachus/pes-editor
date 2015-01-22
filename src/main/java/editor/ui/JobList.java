package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;

public class JobList extends JList/*<String>*/ implements ListSelectionListener {
	private static final long serialVersionUID = 436909804060295023L;
	private static final Logger log = LoggerFactory.getLogger(JobList.class);

	private final OptionFile of;
	private final int offset;
	private final String job;

	private volatile int team;
	private volatile boolean isOk = false;

	public JobList(OptionFile of, int offset, String job, Color colour) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (Strings.isEmpty(job)) {
			throw new NullArgumentException("job");
		}
		this.of = of;
		this.offset = offset;
		this.job = job;

		log.debug("Initialize Job list '{}' at {}, color: {}", job, offset, colour);
		initComponents(colour);

		refresh(-1);
	}

	private void initComponents(Color colour) {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(Formations.CLUB_TEAM_SIZE);
		setSelectionBackground(colour);
		setSelectionForeground(Color.BLACK);
		//setBackground(UIUtil.LIGHT_YELLOW);
		//setFont(new Font(UIUtil.DIALOG, Font.BOLD, 12));

		addListSelectionListener(this);
	}

	/*public int getTeam() {
		return team;
	}*/

	@SuppressWarnings("unchecked")
	public void refresh(int team) {
		log.info("Try to refresh Job list '{}' for team: {}", job, team);

		isOk = false;
		this.team = team;

		String[] posJobs = new String[Formations.PLAYER_COUNT];
		Arrays.fill(posJobs, " ");

		int p = 0;
		if (team < 0) {
			posJobs[p] = job;
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
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (evt.getValueIsAdjusting()) {
			return;
		}

		if (!isSelectionEmpty() && isOk) {
			// DEBUG
			log.debug("Change Job list '{}' at {} for team: {}", job, offset, team);

			Formations.setJob(of, team, offset, getSelectedIndex());
			refresh(team);
		}
	}

}
