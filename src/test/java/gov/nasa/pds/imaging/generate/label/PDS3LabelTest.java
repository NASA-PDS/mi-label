package gov.nasa.pds.imaging.generate.label;

import java.util.HashMap;

import gov.nasa.pds.imaging.generate.constants.TestConstants;
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.util.Utility;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PDS3LabelTest extends GenerateTest {

	private PDS3Label label;
	
	private final static String TEST_LABEL_NAME = "gen_ELE_MOM.LBL";
	
	@Before public void setUp() throws Exception {
		this.label = new PDS3Label(Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/cli1/" + TEST_LABEL_NAME));
		this.label.setMappings();
	}
	
	@Test
	public void testGetSimple() {
		String expected = "VG2-J-PLS-5-SUMM-ELE-MOM-96.0SEC-V1.0";
		assertTrue(expected.equals(this.label.get("DATA_SET_ID")));
	}
	
	/**
	 * FIXME Broken test from Transcoder bug
	 */
	@Test
	@Ignore
	public void testLabelReader() {
		HashMap<String, String>keyValueMap = new HashMap<String, String>();
		keyValueMap.put("PROCESSING_HISTORY_TEXT", "CODMAC LEVEL 1 TO LEVEL 2 CONVERSION VIA     JPL/MIPL MPFTELEMPROC");
		keyValueMap.put("INST_CMPRS_NAME", "JPEG DISCRETE COSINE TRANSFORM (DCT);        HUFFMAN/RATIO");
		for (String key : keyValueMap.keySet()) {
			//System.out.println(this.label.get(key) + "\n");			
			if (!this.label.get(key).equals(keyValueMap.get(key))) {
				fail("'" + key + "' returned '" + this.label.get(key) + "'\n" +
						"Expected: '" + keyValueMap.get(key) + "'");
			}
		}
	}
	
	/**
	 * Not really a test, just trying out some functionality
	 */
	@Ignore
	public void testLabelOutput() {
		System.out.println(this.label.toString());
	}
	
}
