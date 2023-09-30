// Copyright 2020, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
    for (int i = 0; i < buffer.length(); i++) {
      char value = buffer.charAt(i);
      if (Character.isLetter(value)) {
        model += Integer.parseInt(Character.toString(value), 16);
      } else {
        model += value;
      }
      if (i != (buffer.length() - 1)) {
        model += ".";
      }
    }
    return model;
  }
}
