package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileName implements Element {

    private File file;
    private String filePath = "";

    @Override
    public String getUnits() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getValue() {
    	String name = "NA";
    	if (Debugger.debugFlag) System.out.printf("FileName.getValue(1) name=%s filePath=%s\n", name, filePath);
    
    	if (this.file != null) {
           name = this.file.getName();
    	}
    	if (Debugger.debugFlag) System.out.printf("FileName.getValue(2) name=%s\n", name);
        return name;
    }
    
    @Override
    public void setParameters(final PDSObject pdsObject) {
    	this.filePath = pdsObject.getFilePath();
    	if (Debugger.debugFlag)  System.out.printf("FileName.setParameters >%s<\n", filePath);
    	try {
    		this.file = new File(pdsObject.getFilePath());
	    } catch (final Exception e) {
	        // TODO Auto-generated catch block
	    	System.out.println("Exception: "+e);
	        // e.printStackTrace();
	    	this.file = null;
	    }
    }
}
