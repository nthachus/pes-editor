package editor.ui;

import editor.data.SaveGameInfo;
import editor.util.Files;
import editor.util.Resources;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class OptionPreviewPanel extends JPanel implements PropertyChangeListener {
	private final FileFilter filter;
	private final JTextArea previewText;

	private final SaveGameInfo saveInfo = new SaveGameInfo();
	private volatile File file = null;

	public OptionPreviewPanel(JFileChooser fc) {
		super();
		if (null == fc) throw new NullPointerException("fc");

		filter = fc.getFileFilter();

		previewText = new JTextArea(20, 19);//30
		previewText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		previewText.setEditable(false);
		previewText.setLineWrap(true);
		previewText.setWrapStyleWord(true);

		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("preview.title")));

		fc.addPropertyChangeListener(this);
		add(previewText);
	}

	public void loadImage() {
		String text = "";
		if (null != file && !file.isDirectory() && filter.accept(file)) {
			if (saveInfo.getInfo(file)) {
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
		}

		previewText.setText(text);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		boolean isUpdated = false;
		String prop = evt.getPropertyName();
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equalsIgnoreCase(prop)) {
			file = null;
			isUpdated = true;
		} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equalsIgnoreCase(prop)) {
			file = (evt.getNewValue() instanceof File) ? (File) evt.getNewValue() : null;
			isUpdated = true;
		}

		if (isUpdated) {
			previewText.setText("");
			if (isShowing()) {
				loadImage();
				repaint();
			}
		}
	}

}
