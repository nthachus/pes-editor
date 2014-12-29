package editor.ui;

import editor.util.Files;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter {

	/**
	 * Accept all directories and all png Flag files.
	 */
	public boolean accept(File file) {
		if (null == file) throw new NullPointerException("file");

		if (file.isDirectory())
			return true;

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