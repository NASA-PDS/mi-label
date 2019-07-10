package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReaderFormat implements Element {

    private File file;
    private String readerFormat = "";

    @Override
    public String getUnits() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getValue() {
  		System.out.printf("ReaderFormat.getValue(1) readerFormat=%s\n", readerFormat);
  
      return readerFormat;
    }
    
    
    @Override
    public void setParameters(final PDSObject pdsObject) {
    	this.readerFormat = pdsObject.getReaderFormat();
    	if (Debugger.debugFlag)  
    	  System.out.printf("ReaderFormat.setParameters >%s<\n", readerFormat);
    }
}
