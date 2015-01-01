package editor.data;

import editor.util.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public final class SaveGameInfoTest extends BaseTest {
	private final SaveGameInfo saveInfo = new SaveGameInfo();

	@Test
	public void testGetInfo() throws Exception {
		for (String fn : OF_ALL) {
			File fs = getResourceFile(fn);
			boolean res = saveInfo.getInfo(fs);
			Assert.assertTrue(res);
			// DEBUG
			log.debug("SaveGame info: {}", saveInfo);

			res = OptionFile.isValidGameId(saveInfo.getGame());
			Assert.assertTrue(res);

			if (!fn.endsWith(Files.PSU))
				Assert.assertTrue(saveInfo.getGameName().length() > 0);
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetInfoWithNullFile() throws Exception {
		saveInfo.getInfo(null);
	}

}
