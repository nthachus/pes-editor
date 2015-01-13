package editor.data;

import editor.util.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public final class CsvMakerTest extends BaseTest {
	private final CsvMaker csvMaker = new CsvMaker(true);

	@Test
	public void testExportCsvForOriginalOF() throws Exception {
		testExportCsv(OF_ORIGINAL);
	}

	@Test
	public void testExportCsvForLicensedOF() throws Exception {
		testExportCsv(OF_LICENSED);
	}

	@Test
	public void testExportCsvForLatestOF() throws Exception {
		testExportCsv(OF_LATEST);
	}

	private void testExportCsv(String fn) throws Exception {
		OptionFile of = loadOptionFile(fn);

		File fs = createTempFile(fn, Files.CSV);
		boolean res = csvMaker.makeFile(of, fs, true/*, true*/, false);

		Assert.assertTrue(res);
	}

	@Test(expected = NullPointerException.class)
	public void testExportWithNullOF() throws Exception {
		csvMaker.makeFile(null, new File(getClass().getSimpleName()), true/*, true*/, true);
	}

	@Test(expected = NullPointerException.class)
	public void testExportWithNullDest() throws Exception {
		csvMaker.makeFile(new OptionFile(), null, true/*, true*/, true);
	}

}
