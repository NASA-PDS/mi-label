package gov.nasa.pds.imaging.generate.label;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.imaging.generate.constants.TestConstants;

import org.junit.Before;

import junit.framework.TestCase;

public class PDS3LabelTest extends TestCase {

	private PDS3Label label;
	
	private Map<String, String> keyValueMap;
	
	@Before public void setUp() {
		this.label = new PDS3Label(TestConstants.MPF_TEST_LABEL);
		this.label.setMappings();
		
		this.keyValueMap = new HashMap<String, String>();
		this.keyValueMap.put("PROCESSING_HISTORY_TEXT", "CODMAC LEVEL 1 TO LEVEL 2 CONVERSION VIA     JPL/MIPL MPFTELEMPROC");
		this.keyValueMap.put("INST_CMPRS_NAME", "JPEG DISCRETE COSINE TRANSFORM (DCT);        HUFFMAN/RATIO");
	}
	
	public void testLabelReader() {
		String value;
		for (String key : this.keyValueMap.keySet()) {
			//System.out.println(this.label.get(key) + "\n");			
			if (!this.label.get(key).equals(this.keyValueMap.get(key))) {
				fail("'" + key + "' returned '" + this.label.get(key) + "'\n" +
						"Expected: '" + this.keyValueMap.get(key) + "'");
			}
		}
	}
	
	public void testLabelOutput() {
		System.out.println(this.label.toString());
	}
	
}
