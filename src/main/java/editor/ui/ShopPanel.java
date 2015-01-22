package editor.ui;

import editor.data.OptionFile;
import editor.data.Player;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ShopPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6735593924290315504L;
	private static final Logger log = LoggerFactory.getLogger(ShopPanel.class);

	private static final int PLAYER_COUNT = Player.TOTAL_SHOP;
	private static final int START_ADR = OptionFile.blockAddress(1);

	private final OptionFile of;
	private/* final*/ JLabel status;

	public ShopPanel(OptionFile of) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Shop panel is initializing..");
		initComponents();
	}

	private void initComponents() {
		JButton lock = new JButton(Resources.getMessage("Lock"));
		lock.setActionCommand("Lock");
		lock.addActionListener(this);

		JButton unlock = new JButton(Resources.getMessage("Unlock"));
		unlock.setActionCommand("Unlock");
		unlock.addActionListener(this);

		JPanel buttonsPane = new JPanel();
		buttonsPane.add(lock);
		buttonsPane.add(unlock);

		status = new JLabel();
		status.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("shop.title")));
		contentPane.add(buttonsPane);
		contentPane.add(status);

		add(contentPane);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Try to perform shop action: {}", evt.getActionCommand());

		if ("Lock".equalsIgnoreCase(evt.getActionCommand())) {
			lockAll();
		} else {
			unlockAll();
		}
	}

	public void refresh() {
		log.info("Try to refresh Shop panel for OF: {}", of.getFilename());

		if (!of.isLoaded()) {
			status.setText(Strings.EMPTY);
		} else {

			boolean unlocked = false;
			int endAdr = START_ADR + PLAYER_COUNT / 8 + 6;
			for (int adr = START_ADR; adr < endAdr; adr++) {
				if (of.getData()[adr] != 0) {
					unlocked = true;
					break;
				}
			}
			setStatusText(unlocked);
		}
	}

	private void setStatusText(boolean unlocked) {
		status.setText(Resources.getMessage(unlocked ? "Unlocked" : "Locked"));

		log.debug("Shop status was refreshed with unlock: {}", unlocked);
	}

	private void lockAll() {
		int ofs = START_ADR + PLAYER_COUNT / 8;
		Arrays.fill(of.getData(), START_ADR, ofs, (byte) 0);
		Arrays.fill(of.getData(), ofs, ofs + 6, (byte) 0);

		setStatusText(false);
	}

	private void unlockAll() {
		int ofs = START_ADR + PLAYER_COUNT / 8;
		Arrays.fill(of.getData(), START_ADR, ofs, (byte) 0xFF);

		of.getData()[ofs] = 0x38;
		of.getData()[++ofs] = 0x09;
		of.getData()[++ofs] = (byte) 0xFE;
		of.getData()[++ofs] = (byte) 0xFF;
		of.getData()[++ofs] = (byte) 0xCF;
		of.getData()[++ofs] = 0x7F;

		setStatusText(true);
	}

}
