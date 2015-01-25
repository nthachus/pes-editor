package editor.ui;

import editor.data.SaveGameInfo;
import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class OptionPreviewPanel extends JPanel implements PropertyChangeListener {
	private static final long serialVersionUID = 2370607914947646769L;
	private static final Logger log = LoggerFactory.getLogger(OptionPreviewPanel.class);

	private final transient FileFilter filter;
	private final JTextArea previewText;

	private final SaveGameInfo saveInfo;

	public OptionPreviewPanel(JFileChooser fc) {
		super();
		if (null == fc) {
			throw new NullArgumentException("fc");
		}
		if (null == fc.getFileFilter()) {
			throw new NullArgumentException("fc.fileFilter");
		}
		// DEBUG
		log.debug("Initialize OF preview for file chooser: {}", Strings.valueOf(fc));

		filter = fc.getFileFilter();
		saveInfo = new SaveGameInfo();

		previewText = new JTextArea(20, 19);//30
		previewText.setFont(new Font(UIUtil.SANS_SERIF, Font.PLAIN, 12));
		previewText.setEditable(false);
		previewText.setLineWrap(true);
		previewText.setWrapStyleWord(true);

		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("preview.title")));

		fc.addPropertyChangeListener(this);
		add(previewText);
	}

	private void previewSaveFile(File file) {
		log.debug("Try to preview info of OF: {}", file);

		String text = Strings.EMPTY;
		if (null != file && !file.isDirectory() && filter.accept(file)
				&& saveInfo.getInfo(file)) {

			String extension = Files.getExtension(file);
			if (Files.isXPortFile(extension)) {
				text = Resources.getMessage("preview.xPort",
						saveInfo.getGame(), saveInfo.getGameName(), saveInfo.getSaveName(), saveInfo.getNotes());
			} else if (Files.isARMaxFile(extension)) {
				text = Resources.getMessage("preview.arMax", saveInfo.getGame(), saveInfo.getGameName());
			} else if (Files.isEmsFile(extension)) {
				text = Resources.getMessage("preview.ems", saveInfo.getGame());
			}
		}

		previewText.setText(text);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (!isShowing()) {
			//previewText.setText(Strings.EMPTY);
			return;
		}
		String prop = evt.getPropertyName();
		log.info("On File chooser property '{}' changed", prop);

		boolean updated = false;
		File file = null;

		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equalsIgnoreCase(prop)) {
			updated = true;
		} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equalsIgnoreCase(prop)) {
			if (evt.getNewValue() instanceof File) {
				file = (File) evt.getNewValue();
			}
			updated = true;
		}

		if (updated) {
			previewSaveFile(file);
			repaint();
		}
	}

}
