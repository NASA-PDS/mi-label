package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;

/**
 * Generated value class that will generate an MD5 checksum for an object
 * within a file. This is specifically for attached labels or files with headers.
 * This class will truncate the header from the object and take the checksum of the
 * object itself.
 * 
 * @author jpadams
 *
 */
public class ObjectMd5Checksum implements Element {

	private PDSObject pdsObj;
	public long offset = 0;
	
    public ObjectMd5Checksum() { }

    @Override
    public String getUnits() {
        return null;
    }

    @Override
    public String getValue() {
    	Md5Checksum checksum = null;
    	try {
	    	String recbytes = (String)this.pdsObj.get("RECORD_BYTES");
	    	String recs = (String)this.pdsObj.get("LABEL_RECORDS");
	    	this.offset = Long.valueOf(recbytes) * Long.valueOf(recs);
	    	//System.out.println(this.offset);
	    	
	    	checksum = new Md5Checksum(this.pdsObj, this.offset);
	        return checksum.getValue();
    	} catch (TemplateException e) {
    		System.err.println("ERROR: " + checksum.file.getAbsolutePath() +
    				" does not contain RECORD_BYTES and/or LABEL_RECORDS keywords." +
    				" These are required to generate a partial checksum.");
    	}
    	return "Object Not Found";
    }

    @Override
    public void setParameters(final PDSObject pdsObject) {
    	this.pdsObj = pdsObject;
    }
}
