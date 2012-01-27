package ndsr.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ndsr.Main;

public final class VersionRetriever {
	private static final Logger LOG = LoggerFactory.getLogger(VersionRetriever.class);
	
			
	public static String getVersion() {
		String version = null;
		String mainClassName = Main.class.getCanonicalName();
		if (mainClassName != null) {
			try {
				Enumeration<URL> resources = Main.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
				while (resources.hasMoreElements()) {
					URL nextElement = resources.nextElement();
					Manifest manifest = new Manifest(nextElement.openStream());
					Attributes mainAttributes = manifest.getMainAttributes();
					if (mainClassName.equals(mainAttributes.getValue("Main-Class"))) {
						version = mainAttributes.getValue("Implementation-Version");
						LOG.debug("Found version = {}", version);
					}	
				}
			} catch (IOException e) {
				LOG.info("Could not find version", e);
			}
		}
		return version;
	}
}
