package deronzier.remi.safetynetalerts.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.springframework.stereotype.Component;

@Component
public class FileTestManagement {

	static final private File SOURCE = new File("src/main/resources/static/test/data-not-modified.json");
	static final private File DEST = new File("src/main/resources/static/test/data-test.json");

	@SuppressWarnings("resource")
	public static void resetDataFile() throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;

		try {
			sourceChannel = new FileInputStream(SOURCE).getChannel();
			destChannel = new FileOutputStream(DEST).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceChannel.close();
			destChannel.close();
		}

	}

}
