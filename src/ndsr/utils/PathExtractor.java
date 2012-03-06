package ndsr.utils;

public final class PathExtractor {
	public static String getNdsrJarPath() {
		return PathExtractor.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");
	}
	
	public static String getNdsrHomeDir() {
		String jarPath = getNdsrJarPath();
		
		String homeDir = jarPath.substring(0, jarPath.lastIndexOf("/"));
		if (homeDir.startsWith("/")) {
			homeDir = homeDir.replaceFirst("/", "");
		}
		
		return homeDir;
	}
}
