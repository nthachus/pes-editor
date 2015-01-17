package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FormationDialog extends JDialog implements ActionListener, WindowListener {
	private static final long serialVersionUID = 8519475558888665768L;
	private static final Logger log = LoggerFactory.getLogger(FormationDialog.class);

	private final OptionFile of;
	private final byte[] original = new byte[Formations.SIZE];
	private volatile int squadIndex;

	private/* final*/ FormationPanel formationPan;

	public FormationDialog(Frame owner, OptionFile of) {
		super(owner, Resources.getMessage("formation.title"), true);
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		log.debug("Formation dialog is initializing..");
		initComponents();
	}

	private void initComponents() {
		formationPan = new FormationPanel(of);

		JButton acceptButton = new JButton(Resources.getMessage("Accept"));
		acceptButton.setActionCommand("Accept");
		acceptButton.addActionListener(this);

		JButton cancelButton = new JButton(Resources.getMessage("Cancel"));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(acceptButton);
		buttonPanel.add(cancelButton);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(formationPan, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPane);
		addWindowListener(this);

		setResizable(false);
		pack();
	}

	public void show(int team, String title) {
		log.debug("Show Formation dialog for team: {}", team);

		int adr = Formations.getOffset(team);
		squadIndex = team;
		System.arraycopy(of.getData(), adr, original, 0, original.length);

		setTitle(Resources.getMessage("title.format", Resources.getMessage("formation.title"), title));
		formationPan.refresh(team);

		setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		log.debug("Perform Formation dialog action: {}", evt.getActionCommand());

		if ("Cancel".equalsIgnoreCase(evt.getActionCommand())) {
			restoreData();
		}
		//else if ("Accept".equalsIgnoreCase(evt.getActionCommand()))
		setVisible(false);
	}

	private void restoreData() {
		int adr = Formations.getOffset(squadIndex);
		System.arraycopy(original, 0, of.getData(), adr, original.length);
	}

	public void windowClosing(WindowEvent e) {
		log.debug("Formation dialog is closing..");
		restoreData();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}
