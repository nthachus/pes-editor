package editor.ui;

import editor.util.Files;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.Serializable;

public class CsvFilter extends FileFilter implements Serializable {
	private static final long serialVersionUID = 3471877034296204757L;

	/**
	 * Accept all directories and all csv files.
	 */
	public boolean accept(File file) {
		if (null == file) throw new NullPointerException("file");

		if (file.isDirectory())
			return true;

		String extension = Files.getExtension(file);
		return Files.CSV.equalsIgnoreCase(extension);
	}

	/**
	 * The description of this filter.
	 */
	public String getDescription() {
		return Files.EXT_SEPARATOR + Files.CSV;
	}

}
