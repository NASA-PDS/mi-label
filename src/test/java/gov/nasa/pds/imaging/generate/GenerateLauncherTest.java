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
import org.junit.Ignore;
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
		testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/int_launchertest");
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
            
            String[] args = {//"-d", 
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
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/int_launchertest/");
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
    @Ignore
    public void testGenerationMPFExample() {
    	try {
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/int_mpf/");
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
    		String testPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/int_generatortest/");
    		String testOut = Utility.getAbsolutePath(TestConstants.TEST_OUT_DIR);
    		String dataPath = Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/int_generatortest/");
    		
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
