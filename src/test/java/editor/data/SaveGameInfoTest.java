package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public final class SaveGameInfoTest extends BaseTest {
	private final SaveGameInfo saveInfo = new SaveGameInfo();

	@Test
	public void testGetInfoForOriginalOF() {
		testGetInfo(OF_ORIGINAL);
	}

	@Test
	public void testGetInfoForLicensedOF() {
		testGetInfo(OF_LICENSED);
	}

	@Test
	public void testGetInfoForLatestOF() {
		testGetInfo(OF_LATEST);
	}

	private void testGetInfo(String fn) {
		File fs = getResourceFile(fn);
		boolean res = saveInfo.getInfo(fs);
		Assert.assertTrue(res);
		// DEBUG
		log.debug("SaveGame info: {}", saveInfo);

		res = OptionFile.isValidGameId(saveInfo.getGame());
		Assert.assertTrue(res);

		if (!fn.endsWith(Files.PSU)) {
			Assert.assertTrue(saveInfo.getGameName().length() > 0);
		}
	}

	@Test(expected = NullArgumentException.class)
	public void testGetInfoWithNullFile() {
		saveInfo.getInfo(null);
	}

}
