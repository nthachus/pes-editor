package editor.ui;

import editor.data.EmblemType;
import editor.data.Emblems;
import editor.data.OptionFile;
import editor.util.Resources;
import editor.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmblemChooserDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 2638397173587686242L;

	private final OptionFile of;
	private volatile boolean isTrans = true;
	private volatile int slot = -1;
	private volatile EmblemType type = null;

	private final JButton[] emblemButtons = new JButton[Emblems.TOTAL16];

	public EmblemChooserDialog(Frame owner, OptionFile of) {
		super(owner, true);
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		initComponents();
	}

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(6, 10));
		int refSize = Math.round(0.69f * Emblems.IMG_SIZE);

		UIUtil.javaUI();// fix button background color
		for (int i = 0; i < emblemButtons.length; i++) {
			emblemButtons[i] = new JButton(new ImageIcon(Emblems.BLANK16));
			emblemButtons[i].setMargin(new Insets(0, 0, 0, 0));
			emblemButtons[i].setPreferredSize(new Dimension(refSize, refSize));
			emblemButtons[i].setActionCommand(Integer.toString(i));
			emblemButtons[i].addActionListener(this);

			flagPanel.add(emblemButtons[i]);
		}
		UIUtil.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.setActionCommand("Transparency");
		transButton.addActionListener(this);

		CancelButton cancelButton = new CancelButton(this);
		getContentPane().add(transButton, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		getContentPane().add(flagPanel, BorderLayout.CENTER);

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
			if (slot >= Emblems.count16(of)) {
				slot = Emblems.TOTAL16 - slot - 1;
			} else {
				slot += Emblems.TOTAL128;
			}

			setVisible(false);
		}
	}

	public void refresh() {
		Image icon;
		if (type == null || type == EmblemType.lowRes) {
			for (int i = 0, n = Emblems.count16(of); i < n; i++) {
				icon = Emblems.get16(of, i, !isTrans, true);
				emblemButtons[i].setIcon(new ImageIcon(icon));
				emblemButtons[i].setVisible(true);
			}
		}
		if (type == null || type == EmblemType.highRes) {
			for (int i = 0, n = Emblems.count128(of); i < n; i++) {
				icon = Emblems.get128(of, i, !isTrans, true);
				emblemButtons[Emblems.TOTAL16 - i - 1].setIcon(new ImageIcon(icon));
				emblemButtons[Emblems.TOTAL16 - i - 1].setVisible(true);
			}
		}

		int start = 0, end = 0;
		if (type == EmblemType.lowRes) {
			start = Emblems.count16(of);
			end = Emblems.TOTAL16;
		} else if (type == EmblemType.highRes) {
			start = 0;
			end = Emblems.TOTAL16 - Emblems.count128(of);
		}

		for (int i = start; i < end; i++) {
			emblemButtons[i].setVisible(false);
		}
	}

	public int getEmblem(String title, EmblemType type) {
		this.type = type;
		slot = -1;
		setTitle(title);
		refresh();
		setVisible(true);
		return slot;
	}

}
