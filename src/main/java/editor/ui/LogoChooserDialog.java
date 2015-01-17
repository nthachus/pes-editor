package editor.ui;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.util.Resources;
import editor.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoChooserDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 8328193997153067543L;

	private final OptionFile of;
	private volatile boolean isTrans = true;
	private volatile int slot = -1;

	public LogoChooserDialog(Frame owner, OptionFile of) {
		super(owner, true);
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		initComponents();
	}

	private final JButton[] flagButtons = new JButton[Logos.TOTAL];
	private/* final*/ JLabel repLabel;

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(8, 10));
		Insets margin = new Insets(0, 0, 0, 0);
		Icon blankIcon = new ImageIcon(Logos.BLANK);

		UIUtil.javaUI();// fix button background color
		for (int l = 0; l < Logos.TOTAL; l++) {
			flagButtons[l] = new JButton(blankIcon);
			flagButtons[l].setMargin(margin);
			flagButtons[l].setActionCommand(Integer.toString(l));
			flagButtons[l].addActionListener(this);

			flagPanel.add(flagButtons[l]);
		}
		UIUtil.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		JButton cancelButton = new CancelButton(this);

		repLabel = new JLabel(new ImageIcon(Logos.BLANK));

		JPanel centrePanel = new JPanel(new BorderLayout());
		centrePanel.add(repLabel, BorderLayout.NORTH);
		centrePanel.add(flagPanel, BorderLayout.CENTER);

		getContentPane().add(transButton, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(centrePanel, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if ("Transparency".equalsIgnoreCase(evt.getActionCommand())) {
			isTrans = !isTrans;
			refresh();
		} else {
			slot = Integer.parseInt(evt.getActionCommand());
			setVisible(false);
		}
	}

	public void refresh() {
		Image logo;
		for (int f = 0; f < Logos.TOTAL; f++) {
			logo = Logos.get(of, f, !isTrans);
			flagButtons[f].setIcon(new ImageIcon(logo));
		}
	}

	public int getFlag(String title, Image image) {
		slot = -1;
		setTitle(title);
		repLabel.setIcon(new ImageIcon(image));
		refresh();
		setVisible(true);

		return slot;
	}

}
