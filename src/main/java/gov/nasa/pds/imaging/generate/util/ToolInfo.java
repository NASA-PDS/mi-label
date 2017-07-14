package gov.nasa.pds.imaging.generate.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Class to get tool release information.
 * 
 * @author jpadams
 * 
 */
public class ToolInfo {
    public static final String FILE = "generate.properties";

    public static final String NAME = "generate.name";

    public static final String VERSION = "generate.version";

    public static final String RELEASE_DATE = "generate.date";

    public static final String COPYRIGHT = "generate.copyright";
    
    public static final String MODEL_VERSION = "model.version";

    private static final Properties props = new Properties();

    static {
        try {
            final URL propertyFile = ToolInfo.class.getResource(FILE);
            final InputStream in = propertyFile.openStream();
            props.load(in);
        } catch (final IOException io) {
            throw new RuntimeException(io.getMessage());
        }
    }

    /**
     * Get copyright information.
     * 
     * @return The copyright info.
     */
    public static String getCopyright() {
        return props.getProperty(COPYRIGHT);
    }

    /**
     * Get the name of the tool.
     * 
     * @return The tool name.
     */
    public static String getName() {
        return props.getProperty(NAME);
    }

    /**
     * Get the release date.
     * 
     * @return The tool release date.
     */
    public static String getReleaseDate() {
        return props.getProperty(RELEASE_DATE);
    }

    /**
     * Get the version.
     * 
     * @return The tool version.
     */
    public static String getVersion() {
        return props.getProperty(VERSION);
    }
    
    public static String getModelVersion() {
      StringBuffer buffer = new StringBuffer(props.getProperty(MODEL_VERSION));
      String model = "";
      for(int i = 0; i < buffer.length(); i++) {
        model += buffer.charAt(i);
        if (i != (buffer.length() - 1)) {
          model += ".";
        }
      }
      return model;
    }
}
