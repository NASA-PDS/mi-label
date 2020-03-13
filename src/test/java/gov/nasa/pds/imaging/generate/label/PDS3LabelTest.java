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

package gov.nasa.pds.imaging.generate.label;

import java.util.HashMap;

import gov.nasa.pds.imaging.generate.constants.TestConstants;
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.test.GenerateTest.SingleTestRule;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.Utility;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PDS3LabelTest extends GenerateTest {
    
    @Rule
    public SingleTestRule test = new SingleTestRule("");
    
	@Test
	public void testGetSimple() {
	    try {
	        Debugger.debugFlag = true;
            PDS3Label label = new PDS3Label(Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-259/gen_ELE_MOM.LBL"));
            label.setMappings();
    		String expected = "VG2-J-PLS-5-SUMM-ELE-MOM-96.0SEC-V1.0";
    		System.out.println("DATA_SET_ID:" + label.get("DATA_SET_ID").toString());
    		//assertTrue(expected.equals(label.get("DATA_SET_ID")));
	    } catch (Exception e) {
	        e.printStackTrace();
	        fail("Exception thrown.");
	    }
	}
	
	/**
	 * FIXME Broken test from Transcoder bug
	 */
	@Test
	@Ignore
	public void testLabelReader() {
	    try {
	        PDS3Label label = new PDS3Label(Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-259/gen_ELE_MOM.LBL"));
	        label.setMappings();
        
    		HashMap<String, String>keyValueMap = new HashMap<String, String>();
    		keyValueMap.put("PROCESSING_HISTORY_TEXT", "CODMAC LEVEL 1 TO LEVEL 2 CONVERSION VIA     JPL/MIPL MPFTELEMPROC");
    		keyValueMap.put("INST_CMPRS_NAME", "JPEG DISCRETE COSINE TRANSFORM (DCT);        HUFFMAN/RATIO");
    		for (String key : keyValueMap.keySet()) {
    			//System.out.println(this.label.get(key) + "\n");			
    			if (!label.get(key).equals(keyValueMap.get(key))) {
    				fail("'" + key + "' returned '" + label.get(key) + "'\n" +
    						"Expected: '" + keyValueMap.get(key) + "'");
    			}
    		}
	    } catch (Exception e) {
	        e.printStackTrace();
	        fail("Exception thrown.");
	    }
	}
	
}
