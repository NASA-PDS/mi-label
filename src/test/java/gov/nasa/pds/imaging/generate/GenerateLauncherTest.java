//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.imaging.generate;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		Debugger.debugFlag = true;
		testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/generatelaunchertest");
		FileUtils.forceMkdir(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_OUT_DIR));
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
            String outFilePath = TestConstants.TEST_OUT_DIR;
            File output = new File(outFilePath + "/pds3_example.xml" );
            File expected = new File(testPath + "/generationDemo_expected.xml");
            
            String[] args = {"-d", 
                    "-p",exPath + "/pds3_example.lbl",
                    "-t",exPath + "/template_example.vm",
                    "-o",outFilePath, "-b", exPath};
            GenerateLauncher.main(args);
        
            // Check expected file exists
            assertTrue(expected.getAbsolutePath() + " does not exist.", 
                    expected.exists());
            
            // Check output was generated
            assertTrue(output.getAbsolutePath() + " does not exist.",
                    output.exists());
            
	        // Check the files match
	        assertTrue(expected.getAbsolutePath() + " not equals " + output.getAbsolutePath(),
	        		FileUtils.contentEquals(expected, output));
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
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/generatelaunchertest/");
    		String testOut = Utility.getAbsolutePath(TestConstants.TEST_OUT_DIR);
    		
	        String filebase = "1p216067135edn76pop2102l2m1";
    		
	        String[] args = {"-p", testPath + "/" + filebase + ".img",
	        		"-t", testPath + "/mer_template.vm",
	        		"-o", testOut , "-b", testPath };
	        GenerateLauncher.main(args);
	        
        	System.out.println("output file: " + testOut + "/" + filebase + ".xml");
			String outFilePath = testOut + "/" + filebase + ".xml";
			File output = new File(outFilePath);
			System.out.println("expected output file: " + testPath + "/" + filebase + "_expected.xml");
			File expected = new File(testPath + "/" + filebase + "_expected.xml");
			
	        // Check the files match
	        assertTrue(expected.getAbsolutePath() + " not equals " + output.getAbsolutePath(),
	        		FileUtils.contentEquals(expected, output));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Failed Due To Exception: " + e.getMessage());
		}
    }
    
    /**
     * Test Generation Tool with MPF Data
     */
    @Test
    public void testGenerationMPFExample() {
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/mpf/");
    		String testOut = Utility.getAbsolutePath(TestConstants.TEST_OUT_DIR);
    		String dataPath = Utility.getAbsolutePath(TestConstants.EXAMPLE_DIR + "/mpf_example/");
    		
	        List<String> filebases = new ArrayList<String>();
	        filebases.add("i646954r");
	        filebases.add("i985135l");
	        filebases.add("i455934l");
    		
	        String[] args = {"-p", dataPath + "/" + filebases.get(0) + ".img", 
	        		dataPath + "/" + filebases.get(1) + ".img",
	        		dataPath + "/" + filebases.get(2) + ".drk",
	        		"-t", testPath + "/mpf_imp_raw_template_1400.xml",
	        		"-o", testOut , "-b", dataPath };
	        GenerateLauncher.main(args);
	        
	        for (String filebase : filebases) {
	        	System.out.println("output file: " + testOut + "/" + filebase + ".xml");
				String outFilePath = testOut + "/" + filebase + ".xml";
				File output = new File(outFilePath);
				System.out.println("expected output file: " + testPath + "/" + filebase + "_expected.xml");
				File expected = new File(testPath + "/" + filebase + "_expected.xml");
				
		        // Check the files match
		        assertTrue(expected.getAbsolutePath() + " not equals " + output.getAbsolutePath(),
		        		FileUtils.contentEquals(expected, output));
	        }
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Failed Due To Exception: " + e.getMessage());
		}
    }
    
    @Test
    public void testCleanup() {
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/generatortest/");
    		String testOut = Utility.getAbsolutePath(TestConstants.TEST_OUT_DIR);
    		String dataPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/generatortest/");
    		
	        String filebase = "mpf";
    		
	        String[] args = {"-p", dataPath + "/" + filebase + ".img",
	        		"-t", testPath + "/mpf_template.xml",
	        		"-o", testOut , "-b", dataPath };
	        GenerateLauncher.main(args);
	        
        	System.out.println("output file: " + testOut + "/" + filebase + ".xml");
			String outFilePath = testOut + "/" + filebase + ".xml";
			File output = new File(outFilePath);
			System.out.println("expected output file: " + testPath + "/" + filebase + "_expected.xml");
			File expected = new File(testPath + "/" + filebase + "_expected.xml");
			
	        // Check the files match
	        assertTrue(expected.getAbsolutePath() + " not equals " + output.getAbsolutePath(),
	        		FileUtils.contentEquals(expected, output));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Failed Due To Exception: " + e.getMessage());
		}
    }
}
