package editor.ui;

import editor.data.Emblems;
import editor.data.OptionFile;
import editor.util.Resources;
import editor.util.Systems;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmblemImportDialog extends JDialog {
	private final OptionFile of2;

	private volatile boolean isTrans = true;
	private volatile int slot = -1;
	private volatile int type;

	public EmblemImportDialog(Frame owner, OptionFile of2) {
		super(owner, true);
		if (null == of2) throw new NullPointerException("of2");
		this.of2 = of2;

		initComponents();
	}

	private final JButton[] emblemButtons = new JButton[Emblems.TOTAL16];
	private/* final*/ JLabel fileLabel;

	private void initComponents() {
		JPanel flagPanel = new JPanel(new GridLayout(6, 10));
		Systems.javaUI();// fix button background color
		for (int i = 0; i < emblemButtons.length; i++) {
			Image icon = Emblems.get16(of2, -1, false, true);
			emblemButtons[i] = new JButton(new ImageIcon(icon));
			emblemButtons[i].setMargin(new Insets(0, 0, 0, 0));
			emblemButtons[i].setActionCommand(Integer.toString(i));
			emblemButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					onSelectEmblem(evt);
				}
			});

			flagPanel.add(emblemButtons[i]);
		}
		Systems.systemUI();

		JButton transButton = new JButton(Resources.getMessage("Transparency"));
		transButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onTransparency(evt);
			}
		});

		CancelButton cancelButton = new CancelButton(this);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(transButton, BorderLayout.NORTH);
		contentPane.add(cancelButton, BorderLayout.SOUTH);
		contentPane.add(flagPanel, BorderLayout.CENTER);

		fileLabel = new JLabel(Resources.getMessage("import.label", ""));

		getContentPane().add(fileLabel, BorderLayout.NORTH);
		getContentPane().add(contentPane, BorderLayout.CENTER);

		setResizable(false);
		pack();
	}

	private void onTransparency(ActionEvent evt) {
		isTrans = !isTrans;
		refresh();
	}

	private void onSelectEmblem(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof AbstractButton)) throw new IllegalArgumentException("evt");

		AbstractButton btn = (AbstractButton) evt.getSource();
		slot = Integer.parseInt(btn.getActionCommand());
		if (slot >= Emblems.count16(of2)) {
			slot = Emblems.TOTAL16 - slot - 1;
		} else {
			slot = slot + Emblems.TOTAL128;
		}

		setVisible(false);
	}

	public boolean isOf2Loaded() {
		return of2.isLoaded();
	}

	/**
	 * @see EmblemChooserDialog#refresh()
	 */
	public void refresh() {
		Image icon;
		if (type == Emblems.TYPE_INHERIT || type == Emblems.TYPE_16) {
			for (int i = 0, n = Emblems.count16(of2); i < n; i++) {
				icon = Emblems.get16(of2, i, !isTrans, true);
				emblemButtons[i].setIcon(new ImageIcon(icon));
				emblemButtons[i].setVisible(true);
			}
		} else if (type == Emblems.TYPE_INHERIT || type == Emblems.TYPE_128) {
			for (int i = 0, n = Emblems.count128(of2); i < n; i++) {
				icon = Emblems.get128(of2, i, !isTrans, true);
				emblemButtons[Emblems.TOTAL16 - i - 1].setIcon(new ImageIcon(icon));
				emblemButtons[Emblems.TOTAL16 - i - 1].setVisible(true);
			}
		}

		int start = 0, end = 0;
		if (type == Emblems.TYPE_16) {
			start = Emblems.count16(of2);
			end = Emblems.TOTAL16;
		} else if (type == Emblems.TYPE_128) {
			start = 0;
			end = Emblems.TOTAL16 - Emblems.count128(of2);
		}

		for (int i = start; i < end; i++) {
			emblemButtons[i].setVisible(false);
		}
	}

	/**
	 * @see Emblems#TYPE_INHERIT
	 */
	public int getEmblem(String title, int type) {
		this.type = type;
		slot = -1;

		setTitle(title);
		fileLabel.setText(Resources.getMessage("import.label", of2.getFilename()));
		refresh();
		setVisible(true);

		return slot;
	}

	public void import128(OptionFile of, int slot, int replacement) {
		Emblems.importData128(of2, replacement, of, slot);
	}

	public void import16(OptionFile of, int slot, int replacement) {
		Emblems.importData16(of2, replacement, of, slot);
	}

}
