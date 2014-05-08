package gov.nasa.pds.imaging.generate.cli.options;

import org.apache.commons.cli.Options;

public enum Flag {
    /** Flag to display the help. */
    HELP("h", "help", "Display usage."),

    /** Flag to output the logging to a file. */
    PDS3("p", "pds3-label", "pds3 label", String.class,
            "Specify the file path for the PDS3 Label to be converted to PDS4"),

    /** Flag to specify the path of a velocity template. */
    TEMPLATE("t", "template", "velocity template", String.class,
            "Specify the file path for the Velocity template for the PDS4 label"),

    /** Flag to specify a file containing multiple PDS3 Label paths. */
    /*FILE("f", "file-list", "file list", String.class,
            "Specify the path for a file containing a list of file paths for PDS3 Labels"),*/

    /** Flag to specify path for the context-classes.xml */
    CONFIG("c", "config-home", "config home", String.class,
            "Specify the path for the configuration files"),

    /** Flag to specify the output file name */
    OUTPUT("o", "output-file", "output file", String.class,
            "Specify an output filename."),

    /** Flag to display the version. */
    VERSION("V", "version", "Display application version."),

    /** Flag to display the version. */
    DEBUG("d", "debug", "Directs output to screen.  <default>");

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
        //options.addOption(new ToolsOption(FILE));
        options.addOption(new ToolsOption(CONFIG));
        options.addOption(new ToolsOption(OUTPUT));
        options.addOption(new ToolsOption(DEBUG));
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
