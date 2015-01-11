package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FormationDialog extends JDialog implements WindowListener {
	private final OptionFile of;
	private final byte[] original = new byte[Formations.SIZE];
	private volatile int squadIndex;

	private/* final*/ FormationPanel formationPan;

	public FormationDialog(Frame owner, OptionFile of) {
		super(owner, Resources.getMessage("formation.title"), true);
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		initComponents();
	}

	private void initComponents() {
		formationPan = new FormationPanel(of);

		JButton acceptButton = new JButton(Resources.getMessage("Accept"));
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

		JButton cancelButton = new JButton(Resources.getMessage("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onCancel(evt);
			}
		});

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
		int adr = Formations.getOffset(team);
		setTitle(Resources.getMessage("title.format", Resources.getMessage("formation.title"), title));

		squadIndex = team;
		System.arraycopy(of.getData(), adr, original, 0, original.length);

		formationPan.refresh(team);
		setVisible(true);
	}

	private void onCancel(ActionEvent evt) {
		windowClosing(null);
		setVisible(false);
	}

	public void windowClosing(WindowEvent e) {
		int adr = Formations.getOffset(squadIndex);
		System.arraycopy(original, 0, of.getData(), adr, original.length);
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
