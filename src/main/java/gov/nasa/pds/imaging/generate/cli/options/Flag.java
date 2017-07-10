// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.imaging.generate.cli.options;

import org.apache.commons.cli.Options;

public enum Flag {
    /** Flag to display the help. */
    HELP("h", "help", "Display usage."),

    /** Flag to output the logging to a file. */
    BASEPATH("b", "base-path", "base path mask", String.class,
            "Specify the base file path mask to be stripped from the input file path " +
            "to allow output in a relative directory structure. Requires -o flag to " +
            "also be specified."),
    
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
    TEXTOUT("x", "text-output", "With this flag set, the software will output the file as plain text. By default, the output is XML."),
    
    INCLUDES("I", "include", "paths", String.class, "Specify the paths to look"
        + " for files referenced by pointers in a label. Default is to"
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
     * @param shortName
     *            The short name.
     * @param longName
     *            The long name.
     * @param description
     *            A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String description) {
        this(shortName, longName, null, null, description);
    }

    /**
     * Constructor for flags that can take arguments.
     * 
     * @param shortName
     *            The short name.
     * @param longName
     *            The long name.
     * @param argName
     *            The argument name.
     * @param argType
     *            The argument type.
     * @param allowsMultipleArgs
     *            Allow multiple arguments?
     * @param description
     *            A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String argName, final Object argType,
            final boolean allowsMultipleArgs, final String description) {
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
     * @param shortName
     *            The short name.
     * @param longName
     *            The long name.
     * @param argName
     *            The argument name.
     * @param argType
     *            The argument type.
     * @param description
     *            A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String argName, final Object argType, final String description) {
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
