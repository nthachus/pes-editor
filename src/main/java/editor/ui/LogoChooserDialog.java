package editor.ui;

import editor.data.Logos;
import editor.data.OptionFile;
import editor.util.Resources;
import editor.util.Systems;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoChooserDialog extends JDialog {
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

		Systems.javaUI();// fix button background color
		Image icon;
		for (int l = 0; l < Logos.TOTAL; l++) {

			icon = Logos.get(of, -1, false);
			flagButtons[l] = new JButton(new ImageIcon(icon));
			flagButtons[l].setMargin(new Insets(0, 0, 0, 0));
			flagButtons[l].setActionCommand(Integer.toString(l));
			flagButtons[l].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					onSelectLogo(evt);
				}
			});
			flagPanel.add(flagButtons[l]);
		}
		Systems.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onTransparency(evt);
			}
		});

		CancelButton cancelButton = new CancelButton(this);

		icon = Logos.get(of, -1, false);
		repLabel = new JLabel(new ImageIcon(icon));

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

	private void onSelectLogo(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		JButton btn = (JButton) evt.getSource();
		if (null == btn) throw new IllegalArgumentException("evt");

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
