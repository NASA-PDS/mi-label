package gov.nasa.pds.imaging.generate.readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

import jpl.mipl.io.plugins.PDSLabelToDOM;
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
public class PDS3LabelReaderTest extends GenerateTest {
    
    @Rule
    public SingleTestRule test = new SingleTestRule("");
    
	@Test
	public void testGetSimple() {
	    try {
	        Debugger.debugFlag = true;
	        String filePath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-259/gen_ELE_MOM.LBL");
	        final BufferedReader input = new BufferedReader(new FileReader(filePath));
	        // TODO - what is the purpose of this
	        // in PDSLabelToDOM
	        final PrintWriter output = new PrintWriter(System.out);
	        
	        final PDSLabelToDOM pdsToDOM = new PDSLabelToDOM(input, output);
	        pdsToDOM.setDebug(true);
	        //pdsToDOM.buildDocument();
	        
	        pdsToDOM.getDocument();

	    } catch (Exception e) {
	        e.printStackTrace();
	        fail("Exception thrown.");
	    }
	}
	
}
