package gov.nasa.pds.imaging.generation.cli.options;

/**
 * Exception class that is called upon errors found during command-line
 * option processing.
 *
 *
 * @author jpadams
 *
 */
public class InvalidOptionException extends Exception {

    /**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -5662627868701778253L;

	/**
     * Constructor.
     *
     * @param msg An exception message.
     */
    public InvalidOptionException(String msg) {
        super(msg);
    }
	
}
