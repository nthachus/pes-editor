package editor.ui;

import editor.lang.NullArgumentException;
import editor.util.Files;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter {

	/**
	 * Accept all directories and all image files.
	 */
	public boolean accept(File file) {
		if (null == file) {
			throw new NullArgumentException("file");
		}

		if (file.isDirectory()) {
			return true;
		}

		String extension = Files.getExtension(file);
		return (Files.PNG.equalsIgnoreCase(extension) || Files.GIF.equalsIgnoreCase(extension));
	}

	/**
	 * The description of this filter.
	 */
	public String getDescription() {
		return Files.PNG.toUpperCase() + " / " + Files.GIF.toUpperCase();
	}

}
