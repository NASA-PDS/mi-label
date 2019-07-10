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
