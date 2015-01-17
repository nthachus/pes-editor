package editor.ui;

import editor.util.Files;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.Serializable;

public class PngFilter extends FileFilter implements Serializable {
	private static final long serialVersionUID = 3041517162286861322L;

	/**
	 * Accept all directories and PNG flag files.
	 */
	public boolean accept(File file) {
		if (null == file) throw new NullPointerException("file");

		if (file.isDirectory())
			return true;

		String extension = Files.getExtension(file);
		return Files.PNG.equalsIgnoreCase(extension);
	}

	/**
	 * The description of this filter.
	 */
	public String getDescription() {
		return Files.PNG.toUpperCase();
	}

}
