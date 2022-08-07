package editor.ui;

import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;

public class HelpDialog extends JDialog implements ActionListener, HyperlinkListener {
	private static final long serialVersionUID = -2853334995318291197L;
	private static final Logger log = LoggerFactory.getLogger(HelpDialog.class);

	private static final String HOME_PAGE = "/META-INF/help/index%s.html";
	private final URL homePage;

	public HelpDialog(Frame owner) {
		super(owner, Resources.getMessage("help.title"), false);

		log.debug("Help dialog is initializing..");
		initComponents();

		homePage = getIndexPage();
	}

	private/* final*/ JEditorPane editPanel;

	private void initComponents() {
		editPanel = new JEditorPane();
		editPanel.setEditable(false);
		editPanel.addHyperlinkListener(this);

		JScrollPane scroll = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(editPanel);
		scroll.setPreferredSize(new Dimension(645, 550));

		JButton exitButton = new JButton(Resources.getMessage("help.close"));
		exitButton.addActionListener(this);

		getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(exitButton, BorderLayout.SOUTH);

		//setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent evt) {
		super.setVisible(false);
	}

	private URL getIndexPage() {
		Locale loc = Locale.getDefault();
		String lang = loc.getLanguage() + '-' + loc.getCountry();

		URL helpUrl = getClass().getResource(String.format(HOME_PAGE, Files.EXT_SEPARATOR + lang));
		if (null == helpUrl) {
			lang = loc.getLanguage();
			helpUrl = getClass().getResource(String.format(HOME_PAGE, Files.EXT_SEPARATOR + lang));

			if (null == helpUrl) {
				helpUrl = getClass().getResource(String.format(HOME_PAGE, Strings.EMPTY));
			}
		}

		log.debug("Loaded help index URL: {}", helpUrl);
		return helpUrl;
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			showPage(homePage);
		}
		super.setVisible(b);
	}

	private void showPage(URL url) {
		if (null != url && url != editPanel.getPage()) {
			try {
				editPanel.setPage(url);
			} catch (Exception e) {
				log.error("Error while loading help page: " + url, e);
			}
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}

		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			log.debug("Try to process hyperlink event: {}", evt.getURL());
			// Show the new page in the editor pane
			showPage(evt.getURL());
		}
	}

}
