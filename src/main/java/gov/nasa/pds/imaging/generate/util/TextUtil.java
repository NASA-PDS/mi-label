package gov.nasa.pds.imaging.generate.util;

import org.apache.commons.lang.WordUtils;

import gov.nasa.pds.imaging.generate.context.PDSContext;

public class TextUtil extends WordUtils implements PDSContext {
	public static final String CONTEXT = "text";
	
	public String getContext() {
		return CONTEXT;
	}
	
	/**
	 * Utility method used to transform the str into the applicable
	 * PDS title-case format for enumerated values.
	 * 
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
		str = str.replace('_', ' ');
		return WordUtils.capitalizeFully(str);
	}
	
    /**
     * Utility method used to transform the object into the applicable
     * PDS title-case format for enumerated values.
     * 
     * @param obj
     * @return
     */
    public static String capitalize(Object obj) {
        String str = obj.toString().replace('_', ' ');
        return WordUtils.capitalizeFully(str);
    }
}
