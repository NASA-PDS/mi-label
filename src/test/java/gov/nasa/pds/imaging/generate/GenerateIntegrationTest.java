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

package gov.nasa.pds.imaging.generate;

import static org.junit.Assert.*;
import gov.nasa.pds.imaging.generate.constants.TestConstants;
import gov.nasa.pds.imaging.generate.label.PDS3Label;
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.Utility;

import java.io.File;
import java.security.Permission;

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
public class GenerateIntegrationTest extends GenerateTest {

	private Generator generator;
	
  protected static class ExitException extends SecurityException 
  {
      public final int status;
      public ExitException(int status) 
      {
          super("There is no escape!");
          this.status = status;
      }
  }

  private static class NoExitSecurityManager extends SecurityManager 
  {
      @Override
      public void checkPermission(Permission perm) 
      {
          // allow anything.
      }
      @Override
      public void checkPermission(Permission perm, Object context) 
      {
          // allow anything.
      }
      @Override
      public void checkExit(int status) 
      {
          super.checkExit(status);
          throw new ExitException(status);
      }
  }
	
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
//    super.setUp();
    System.setSecurityManager(new NoExitSecurityManager());

	  // Generator(final PDSObject pdsObject, final File templateFile, final String filePath, final String confPath, final File outputFile)
		this.generator = new Generator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	  System.setSecurityManager(null);
	}
	
	/**
	 * Test CLI end-to-end with rchen test data per PDS-259 bug
	 */
	@Test
	public void testPDS259() {        
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-259");
    		String outFilePath = TestConstants.TEST_OUT_DIR;
    		File output = new File(outFilePath + "/gen_ELE_MOM.xml");
    		File expected = new File(testPath + "/gen_ELE_MOM_expected.xml");

	        String[] args = {//"-d", 
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
	        assertTrue(expected + " and " + output + " do not match.",
	            FileUtils.contentEquals(expected, output));
    	} catch (ExitException e) {
        assertEquals("Exit status", 0, e.status);
    	} catch (Exception e) {
//    		e.printStackTrace();
    		fail("Test Failed Due To Exception: " + e.getMessage());
    	}
	}

    /**
     * 
     */
    @Test
    public void testTransformCLI() {
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/int_transform-0.2.2");
    		System.out.println(testPath);
    		String outFilePath = TestConstants.TEST_OUT_DIR;
    		File output = new File(outFilePath + "/ELE_MOM.xml");
    		File expected = new File(testPath + "/ELE_MOM_expected.xml");

	        String[] args = {//"-d", 
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
    	} catch (ExitException e) {
        assertEquals("Exit status", 0, e.status);
    	} catch (Exception e) {
//    		e.printStackTrace();
    		fail("Test Failed Due To Exception: " + e.getMessage());
    	}
    }
	
  /**
   * 
   */
  @Test
  public void testPDS493_1() {
    try {
      String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-493");
      System.out.println(testPath);
      String outFilePath = TestConstants.TEST_OUT_DIR;
      File output = new File(outFilePath + "/ahalebp-1.xml");
      File expected = new File(testPath + "/ahalebp-1_expected.xml");

        String[] args = {//"-d", 
            "-p", testPath + "/ahalebp-1.LBL",
            "-t", testPath + "/generic-pds3_to_pds4.vm",
            "-o", outFilePath,
            "-b", testPath
            };
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
    }  catch (ExitException e) {
      assertEquals("Exit status", 0, e.status);
    } catch (Exception e) {
      fail("Test Failed Due To Exception: " + e.getMessage());
    }
  }
  
  @Test
  public void testPDS493_2() {
    try {
      String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-493");
      System.out.println(testPath);
      String outFilePath = TestConstants.TEST_OUT_DIR;
      File output = new File(outFilePath + "/ahalebp-3.xml");
      File expected = new File(testPath + "/ahalebp-3_expected.xml");

        String[] args = {//"-d", 
            "-p", testPath + "/ahalebp-3.LBL",
            "-t", testPath + "/generic-pds3_to_pds4.vm",
            "-o", outFilePath,
            "-b", testPath
            };
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
    }  catch (ExitException e) {
      assertEquals("Exit status", 0, e.status);
    } catch (Exception e) {
      fail("Test Failed Due To Exception: " + e.getMessage());
    }
  }
  
  /**
   * 
   */
  @Test
  public void testPDS500() {
    try {
      String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-500");
      System.out.println(testPath);
      String outFilePath = TestConstants.TEST_OUT_DIR;
      File output = new File(outFilePath + "/OPTE_12.xml");
      File expected = new File(testPath + "/OPTE_12_expected.xml");

        String[] args = {//"-d", 
            "-p", testPath + "/OPTE_12.LBL",
            "-t", testPath + "/generic-pds3_to_pds4.vm",
            "-o", outFilePath,
            "-b", testPath
            };

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
    } catch (ExitException e) {
      assertEquals("Exit status", 0, e.status);
    } catch (Exception e) {
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
    //    e.printStackTrace();
      fail("Test Failed Due To Exception: " + e.getMessage());
    }
  }

}
