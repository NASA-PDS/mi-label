package gov.nasa.pds.imaging.generate.util;

import gov.nasa.pds.imaging.generate.cli.options.InvalidOptionException;

import java.io.File;

public class Utility {
	public static String getAbsolutePath(String path) throws InvalidOptionException {
		String finalPath = "";
		File testFile = new File(path);
		if (!testFile.isAbsolute()) {
			finalPath = System.getProperty("user.dir") + "/" + path;
		} else {
			finalPath = path;
		}
		
		if (!(new File(finalPath)).exists()) {
			throw new InvalidOptionException("Path does not exist: " + finalPath);
		}
		
		return finalPath;
	}
}
