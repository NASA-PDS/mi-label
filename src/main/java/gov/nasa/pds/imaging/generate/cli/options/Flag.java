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

package gov.nasa.pds.imaging.generate.cli.options;

import org.apache.commons.cli.Options;

public enum Flag {
  /** Flag to display the help. */
  HELP("h", "help", "Display usage."),

  /** Flag to output the logging to a file. */
  BASEPATH("b", "base-path", "base path mask", String.class,
      "Specify the base file path mask to be stripped from the input file path "
          + "to allow output in a relative directory structure. Requires -o flag to "
          + "also be specified."),

  /** Flag to display all debug output. */
  DEBUG("d", "debug", "Verbose debugging output. Shows each step of parsing and label generation."),

  /** Flag to specify the input PDS3 label */
  PDS3("p", "pds3-label", "pds3 label", String.class, true,
      "Parse the file or list of files as PDS3 Standard labels. This also applies to file formatted similarly to PDS3 labels (i.e. list of key-value pairs, VICAR labels)."),

  /** Flag to specify the path of a velocity template. */
  TEMPLATE("t", "template", "velocity template", String.class,
      "Specify the file path for the Velocity template used to translate the data into a PDS4 label."),

  /** Flag to specify the output file name */
  OUTPUT("o", "output-path", "output path", String.class,
      "Specify an output path to output the new PDS4 labels. By default, the file will"
          + " output in same location as the input file."),

  /** Flag to specify text file output, versus the default XML output */
  TEXTOUT("x", "text-output",
      "With this flag set, the software will output the file as plain text. By default, the output is XML."),

  INCLUDES("I", "include", "paths", String.class,
      "Specify the paths to look" + " for files referenced by pointers in a label. Default is to"
          + " always look at the same directory as the label."),

  /** Flag to display the version. */
  VERSION("V", "version", "Display application version.");

  /** The short name of the flag. */
  private final String shortName;

  /** The long name of the flag. */
  private final String longName;

  /** An argument name for the flag, if it accepts argument values. */
  private final String argName;

  /** The type of argument values the flag accepts. */
  private final Object argType;

  /**
   * A boolean value indicating if the flag accepts more than one argument.
   */
  private final boolean allowsMultipleArgs;

  /** The flag description. */
  private final String description;

  /** A list of Option objects for command-line processing. */
  private static Options options;

  static {
    options = new Options();

    options.addOption(new ToolsOption(HELP));
    options.addOption(new ToolsOption(VERSION));
    options.addOption(new ToolsOption(PDS3));
    options.addOption(new ToolsOption(TEMPLATE));
    options.addOption(new ToolsOption(BASEPATH));
    options.addOption(new ToolsOption(OUTPUT));
    options.addOption(new ToolsOption(DEBUG));
    options.addOption(new ToolsOption(TEXTOUT));
    options.addOption(new ToolsOption(INCLUDES));
  }

  /**
   * Get the command-line options.
   *
   * @return A class representation of the command-line options.
   */
  public static Options getOptions() {
    return options;
  }

  /**
   * Constructor.
   *
   * @param shortName The short name.
   * @param longName The long name.
   * @param description A description of the flag.
   */
  private Flag(final String shortName, final String longName, final String description) {
    this(shortName, longName, null, null, description);
  }

  /**
   * Constructor for flags that can take arguments.
   *
   * @param shortName The short name.
   * @param longName The long name.
   * @param argName The argument name.
   * @param argType The argument type.
   * @param allowsMultipleArgs Allow multiple arguments?
   * @param description A description of the flag.
   */
  private Flag(final String shortName, final String longName, final String argName,
      final Object argType, final boolean allowsMultipleArgs, final String description) {
    this.shortName = shortName;
    this.longName = longName;
    this.argName = argName;
    this.argType = argType;
    this.allowsMultipleArgs = allowsMultipleArgs;
    this.description = description;
  }

  /**
   * Constructor for flags that can take arguments.
   *
   * @param shortName The short name.
   * @param longName The long name.
   * @param argName The argument name.
   * @param argType The argument type.
   * @param description A description of the flag.
   */
  private Flag(final String shortName, final String longName, final String argName,
      final Object argType, final String description) {
    this(shortName, longName, argName, argType, false, description);
  }

  /**
   * Find out if the flag can handle multiple arguments.
   *
   * @return 'true' if yes.
   */
  public boolean allowsMultipleArgs() {
    return allowsMultipleArgs;
  }

  /**
   * Get the argument name of the flag.
   *
   * @return The argument name.
   */
  public String getArgName() {
    return argName;
  }

  /**
   * Get the argument type of the flag.
   *
   * @return The argument type.
   */
  public Object getArgType() {
    return argType;
  }

  /**
   * Get the flag description.
   *
   * @return The description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the long name of the flag.
   *
   * @return The long name.
   */
  public String getLongName() {
    return longName;
  }

  /**
   * Get the short name of the flag.
   *
   * @return The short name.
   */
  public String getShortName() {
    return shortName;
  }
}
