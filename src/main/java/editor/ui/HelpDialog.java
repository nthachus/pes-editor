package editor.ui;

import editor.util.Files;
import editor.util.Resources;
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

public class HelpDialog extends JDialog implements HyperlinkListener {
	private static final Logger log = LoggerFactory.getLogger(HelpDialog.class);
	private static final String INDEX_PAGE = "/META-INF/help/index%s.html";

	private final JEditorPane editPanel;

	public HelpDialog(Frame owner) {
		super(owner, Resources.getMessage("help.title"), false);

		editPanel = new JEditorPane();
		editPanel.setEditable(false);
		editPanel.addHyperlinkListener(this);

		JScrollPane scroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(editPanel);
		scroll.setPreferredSize(new Dimension(645, 550));

		URL helpUrl = getIndexPage();
		showPage(helpUrl);

		JButton exitButton = new JButton(Resources.getMessage("help.close"));
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

		getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(exitButton, BorderLayout.SOUTH);

		//setResizable(false);
		pack();
	}

	private URL getIndexPage() {
		Locale loc = Locale.getDefault();
		String lang = loc.getLanguage() + '-' + loc.getCountry();

		URL helpUrl = getClass().getResource(String.format(INDEX_PAGE, Files.EXT_SEPARATOR + lang));
		if (null == helpUrl) {
			lang = loc.getLanguage();
			helpUrl = getClass().getResource(String.format(INDEX_PAGE, Files.EXT_SEPARATOR + lang));
		}

		if (null == helpUrl)
			helpUrl = getClass().getResource(String.format(INDEX_PAGE, ""));
		return helpUrl;
	}

	private void showPage(URL url) {
		if (null != url) {
			try {
				editPanel.setPage(url);
			} catch (Exception e) {
				log.error("Error while loading help page: " + url, e);
			}
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			// Show the new page in the editor pane
			showPage(evt.getURL());
		}
	}

}
