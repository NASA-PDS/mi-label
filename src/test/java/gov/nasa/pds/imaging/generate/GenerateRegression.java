/**
 * 
 */
package gov.nasa.pds.imaging.generate;

import static org.junit.Assert.*;
import gov.nasa.pds.imaging.generate.constants.TestConstants;
import gov.nasa.pds.imaging.generate.label.PDS3Label;
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.Utility;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author jpadams
 *
 */
@RunWith(JUnit4.class)
public class GenerateRegression extends GenerateTest {

	private Generator generator;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Debugger.debugFlag = true;
		FileUtils.forceMkdir(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_OUT_DIR));
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    // Generator(final PDSObject pdsObject, final File templateFile, final String filePath, final String confPath, final File outputFile)
		this.generator = new Generator();
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@After
//	public void tearDown() throws Exception {
//	}
	
	/**
	 * Test CLI end-to-end with rchen test data per PDS-259 bug
	 */
	@Test
	public void testCLI1() {        
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/cli1");
    		String outFilePath = TestConstants.TEST_OUT_DIR;
    		File output = new File(outFilePath + "/gen_ELE_MOM.xml");
    		File expected = new File(testPath + "/gen_ELE_MOM_expected.xml");

	        String[] args = {"-d", 
	        		"-p", testPath + "/gen_ELE_MOM.LBL",
	        		"-t", testPath + "/gen_data.vm",
	        		"-o", outFilePath,
	        		"-b", testPath};
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
    		fail("Test Failed Due To Exception: " + e.getMessage());
    	}
	}

    /**
     * 
     */
    @Test
    public void testTransformCLI() {
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/transform-0.2.2");
    		System.out.println(testPath);
    		String outFilePath = TestConstants.TEST_OUT_DIR;
    		File output = new File(outFilePath + "/ELE_MOM.xml");
    		File expected = new File(testPath + "/ELE_MOM_expected.xml");

	        String[] args = {"-d", 
	        		"-p", testPath + "/ELE_MOM.LBL",
	        		"-t", testPath + "/generic-pds3_to_pds4.vm",
	        		"-o", outFilePath,
	        		"-b", testPath};
	        GenerateLauncher.main(args);
	        
	        // Check expected file exists
	        assertTrue(expected.getAbsolutePath() + " does not exist.", 
	        		expected.exists());
	        
	        // Check output was generated
	        assertTrue(output.getAbsolutePath() + " does not exist.",
	        		output.exists());
	        
	        // Check the files match
	        assertTrue(expected + " and " + output + " do not match.",
	        		FileUtils.contentEquals(expected, output));
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Test Failed Due To Exception: " + e.getMessage());
    	}
    }
	
    // FIXME Under construction, this doesn't work
	@Test
	@Ignore
	public void testTransformAPI() {
		try {
			PDS3Label label = new PDS3Label(Utility.getAbsolutePath("src/main/resources/examples/mpf_example/i985135l.img"));
			this.generator.setPDSObject(label);
			File template = new File(Utility.getAbsolutePath("src/main/resources/examples/mpf_example/MPF_IMP_EDR7.vm"));
			this.generator.setTemplateFile(template);
			this.generator.generate(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Failed Due To Exception: " + e.getMessage());
		}
	}

}
