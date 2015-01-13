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

		UIUtil.javaUI();// fix button background color
		for (int l = 0; l < Logos.TOTAL; l++) {
			flagButtons[l] = new JButton(new ImageIcon(Logos.BLANK));
			flagButtons[l].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[l].setActionCommand(Integer.toString(l));
			flagButtons[l].addActionListener(this);

			flagPanel.add(flagButtons[l]);
		}
		UIUtil.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onTransparency(evt);
			}
		});

		CancelButton cancelButton = new CancelButton(this);

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

	private void onTransparency(ActionEvent evt) {
		isTrans = !isTrans;
		refresh();
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof AbstractButton)) throw new IllegalArgumentException("evt");

		AbstractButton btn = (AbstractButton) evt.getSource();
		slot = Integer.parseInt(btn.getActionCommand());
		setVisible(false);
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
