package gov.nasa.pds.imaging.generate.automatic.elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class LabelLength implements Element {

	private PDSObject pdsObject;
	private File file;
	
	public LabelLength() {
		this.pdsObject = null;
	}
	
	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue() throws TemplateException {
		if (Debugger.debugFlag) System.out.printf("LabelLength.getValue() ############################################################# \n");
		
		long value = 0;
		try {
			if (Debugger.debugFlag) System.out.printf("LabelLength.getValue(1) ############################################################# \n");
			value = Long.parseLong(this.pdsObject.get("LABEL_RECORDS").toString())
			* Long.parseLong(this.pdsObject.get("RECORD_BYTES").toString());
			if (Debugger.debugFlag) System.out.printf("LabelLength.getValue(2) ############################################################# \n");
		} catch (NumberFormatException nfe) {
			// throw new TemplateException("Error generating LabelLength: NumberFormatException");
			if (Debugger.debugFlag) System.out.println("Error generating LabelLength: NumberFormatException "+nfe.getMessage());
		} catch (TemplateException te) {
			// throw new TemplateException("Error generating LabelLength: TemplateException");
			if (Debugger.debugFlag) System.out.println("Error generating LabelLength: TemplateException "+te.getMessage());
		} catch (Exception e) {
			// throw new TemplateException("Error generating LabelLength: TemplateException");
			if (Debugger.debugFlag) System.out.println("Error generating LabelLength: Exception "+e);
		}
		
		if (Debugger.debugFlag) System.out.printf("getValue() value = %d \n", value);
		if (value == 0) {
			if (Debugger.debugFlag) System.out.printf("getValue try vicar LBLSIZE \n");
			if (this.file != null) {
	    		
	    		FileInputStream fis;
				try {
					fis = new FileInputStream(this.file);
					// get the first 18 bytes of the stream into byte array
					// split on space first, then trim
		            byte[] header = new byte[18];
		            // fis.skip()
		            // fis.reset();
		            fis.read(header);
		            fis.close();
		            
		            String s = new String(header).trim();
		            
		            String[] words = s.split("\\s+");
		            
		            if (Debugger.debugFlag) System.out.printf("getValue s=%s words[0] =%s<\n", s, words[0]);
		            // .trim() will strip off any spaces
		            String[] words2 = words[0].split("=");
		            if (words2.length == 2 && words2[0].equalsIgnoreCase("LBLSIZE")) {
		            	value = Long.parseLong(words2[1]);
		            	if (Debugger.debugFlag) System.out.printf("getValue %s %s value = %d \n", words2[0], words2[1], value);
		            }
		            	            
				} catch (FileNotFoundException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	           
			}
		}
		if (value == 0) {
			throw new TemplateException("Error generating LabelLength: TemplateException");
		}
		
		if (Debugger.debugFlag) System.out.printf("getValue() return value = %d \n", value);
		return String.valueOf(value);
	}

	@Override
	public void setParameters(final PDSObject pdsObject) {
		this.pdsObject = pdsObject;
		this.file = new File(pdsObject.getFilePath());
	}

}
