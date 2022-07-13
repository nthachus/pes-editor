package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Strings;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public final class CsvMakerTest extends BaseTest {
	private final CsvMaker csvMaker = new CsvMaker(true);

	@Test
	public void testExportCsvForOriginalOF() throws IOException {
		testExportCsv(OF_ORIGINAL);
	}

	@Test
	public void testExportCsvForLicensedOF() throws IOException {
		testExportCsv(OF_LICENSED);
	}

	@Test
	public void testExportCsvForLatestOF() throws IOException {
		testExportCsv(OF_LATEST);
	}

	private void testExportCsv(String fn) throws IOException {
		OptionFile of = loadOptionFile(fn);

		File fs = createTempFile(fn, Files.CSV);
		boolean res = csvMaker.makeFile(of, fs, true/*, true*/, false);

		Assert.assertTrue(res);

		// Verify number of columns
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(fs);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Strings.UTF8));

			String header = reader.readLine();
			String firstLine = reader.readLine();

			Assert.assertEquals(firstLine.split(Strings.TAB).length, header.split(Strings.TAB).length);
		} finally {
			Files.closeStream(stream);
		}
	}

	@Test(expected = NullArgumentException.class)
	public void testExportWithNullOF() {
		csvMaker.makeFile(null, new File(getClass().getSimpleName()), true/*, true*/, true);
	}

	@Test(expected = NullArgumentException.class)
	public void testExportWithNullDest() {
		csvMaker.makeFile(new OptionFile(), null, true/*, true*/, true);
	}

}
