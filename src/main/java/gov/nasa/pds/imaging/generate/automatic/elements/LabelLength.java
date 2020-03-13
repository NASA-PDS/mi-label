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
