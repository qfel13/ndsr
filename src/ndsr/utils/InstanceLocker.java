package ndsr.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InstanceLocker {
	
	private static final String ndsrInstanceLockFileName = System.getProperty("java.io.tmpdir") + "ndsr.lck";
	private static final Logger log = LoggerFactory.getLogger(InstanceLocker.class);
	
	public static boolean lockInstance() {
		final String lockFile = ndsrInstanceLockFileName;
		try {
			final File file = new File(lockFile);
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (Exception e) {
							log.error("Unable to remove lock file: " + lockFile, e);
						}
					}
				});
				return true;
			}
		} catch (Exception e) {
			log.error("Unable to create and/or lock file: " + lockFile, e);
		}
		return false;
	}
}
