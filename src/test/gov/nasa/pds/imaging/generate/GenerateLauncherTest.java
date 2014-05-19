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
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.Utility;

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
	
	
	@Rule
	public SingleTestRule test = new SingleTestRule("testGenerationMPFExample");
	
	@BeforeClass
	public static void oneTimeSetUp() {
		Debugger.debugFlag = true;
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
        String[] args = {"-d", "-p","src/main/resources/examples/example1/pds3_example.lbl",
        		"-t","src/main/resources/examples/example1/template_example.vm"};
        GenerateLauncher.main(args);
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
        		"-t","src/main/resources/examples/mpf_example/MPF_IMP_EDR7.vm"};
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
