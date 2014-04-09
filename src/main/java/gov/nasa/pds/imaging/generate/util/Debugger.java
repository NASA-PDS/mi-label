//	Copyright 2014, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id: Debugger.java 11478 2013-03-25 22:17:42Z jpadams $
//
package gov.nasa.pds.imaging.generate.util;

/**
 * Debug class that shows a ton of debug messages. Messages
 * are way too much for even logger DEBUG level.
 * 
 * @author jpadams
 *
 */
public class Debugger {

	public static boolean debugFlag = false;
	
	/** Simple output method
	 * 
	 *  @param msg
	 */
	public static void debug(String msg) {
		if (debugFlag) {
			System.out.println(msg);
		}
	}
}
