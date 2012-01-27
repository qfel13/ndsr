package ndsr.utils;

public final class PathExtractor {
	public static String getNdsrJarPath() {
		return PathExtractor.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");
	}
}
