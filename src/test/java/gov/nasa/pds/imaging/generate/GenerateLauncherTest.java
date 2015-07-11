//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.imaging.generate;

import static org.junit.Assert.*;

import java.io.File;

import gov.nasa.pds.imaging.generate.GenerateLauncher;
import gov.nasa.pds.imaging.generate.cli.options.InvalidOptionException;
import gov.nasa.pds.imaging.generate.constants.TestConstants;
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.Utility;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GenerateLauncherTest extends GenerateTest {
    
	/**
	 * FIXME Only one method test works at a time. Getting error with ResourceManager
	 * 		when trying to load a second Velocity template
	 */
	
    private static String testPath;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("testGenerationMPFExample");
	
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		Debugger.debugFlag = true;
		testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/generatelaunchertest");
	}
	
	@Test
	public void testDisplayVersion() {
		GenerateLauncher launcher = new GenerateLauncher();
		
		try {
			launcher.displayVersion();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed due to exception.");
		}
	}
	
    /**
     * Test Generation Tool with Demo data
     */
    @Test
    public void testGenerationDemo() {
        try {
            String exPath = Utility.getAbsolutePath(TestConstants.EXAMPLE_DIR + "/example1");
            String outFilePath = TestConstants.TEST_OUT_DIR + "/generationDemo.xml";
            File output = new File(outFilePath);
            File expected = new File(testPath + "/generationDemo_expected.xml");
            
            String[] args = {"-d", 
                    "-p",exPath + "/pds3_example.lbl",
                    "-t",exPath + "/template_example.vm",
                    "-o",outFilePath};
            GenerateLauncher.main(args);
        
            // Check expected file exists
            assertTrue(expected.getAbsolutePath() + " does not exist.", 
                    expected.exists());
            
            // Check output was generated
            assertTrue(output.getAbsolutePath() + " does not exist.",
                    output.exists());
            
            // Check the files match
            assertTrue(FileUtils.contentEquals(expected, output));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown.");
        }
    }    
	
    /**
     * Test Transformer with MER data 
     */
    @Test
    public void testGenerationMER() {
        String[] args = {"-d", "-p","src/main/resources/examples/example2/1p216067135edn76pop2102l2m1.img",
        		"-t","src/main/resources/examples/example2/mer_template.vm"};
        GenerateLauncher.main(args);
    }
    
    /**
     * Test Generation Tool with MPF Data
     */
    @Test
    public void testGenerationMPFExample() {
        String[] args = {"-p","src/main/resources/examples/mpf_example/i985135l.img",
        		"-t","src/main/resources/examples/mpf_example/mpf_imp_raw_template.vm"};
        GenerateLauncher.main(args);
    }
    
    /**
     * Test Generation Tool with MPF Data
     * @throws InvalidOptionException 
     */
    @Test
    public void testGenerationOutFile() {
    	try {
        String[] args = {"-p","src/main/resources/examples/mpf_example/i985135l.img",
        		"-t","src/main/resources/examples/mpf_example/MPF_IMP_EDR7.vm","-o", "target/out.pds4"};
        GenerateLauncher.main(args);
        assertTrue((new File(Utility.getAbsolutePath("target/out.pds4"))).exists());
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Failed due to exception: " + e.getMessage());
    	}
    }
}
