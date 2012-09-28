//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.imaging.generate;

import gov.nasa.pds.imaging.generate.GenerateLauncher;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;


import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GenerateLauncherTest {
    
    /**r
     * Test Generation Tool with Demo data
     */
    @Ignore
    @Test
    public void testGenerationDemo() {
        String[] args = {"-d", "-p","src/main/resources/examples/example1/pds3_example.lbl",
        		"-t","src/main/resources/examples/example1/template_example.vm","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
    }    
	
    /**
     * Test Transformer with MER data
     */
    @Ignore
    @Test
    public void testGenerationMER() {
        String[] args = {"-d", "-p","src/main/resources/examples/example2/1p216067135edn76pop2102l2m1.img",
        		"-t","src/main/resources/examples/example2/mer_template.vm","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
    }
    
    /**
     * Test Generation Tool with MPF Data
     */
    @Ignore
    @Test
    public void testGenerationStdOut() {
    	System.out.println("--------- Test: Generate PDS4 Label - Output to Std Out ---------");
        String[] args = {"-p","src/main/resources/examples/mpf_example/i985135l.img",
        		"-t","src/main/resources/examples/mpf_example/MPF_IMP_EDR7.vm_math","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
        System.out.println("-----------------------------------------------------------------");
    }
    
    /**
     * Test Generation Tool with Elizabeth's Example Data
     */
    //@Ignore
    @Test
    public void testGenerationElizabeth() {
    	System.out.println("--------- Test: Generate PDS4 Label - Output to Std Out ---------");
        String[] args = {"-p","src/main/resources/examples/rye-example/i646954r.img",
        		"-t","src/main/resources/examples/rye-example/mpf_imp_raw_template.xml","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
        System.out.println("-----------------------------------------------------------------");
    }
    
    /**
     * Test Generation Tool with MPF Data
     */
    @Ignore
    @Test
    public void testGenerationOutFile() {
    	System.out.println("--------- Test: Generate PDS4 Label - Output to File ---------");
        String[] args = {"-p","src/main/resources/examples/mpf_example/i985135l.img",
        		"-t","src/main/resources/examples/mpf_example/MPF_IMP_EDR7.vm","-c","src/main/resources/conf", "-o", "target/out.pds4"};
        GenerateLauncher.main(args);
        System.out.println("--------------------------------------------------------------");
    }
}
