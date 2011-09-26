//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.pds4.generate;

import gov.nasa.pds.imaging.generate.GenerateLauncher;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;


import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GenerateLauncherTest {
    
    /**
     * Test Generation Tool with Demo data
     */
    //@Ignore
    @Test
    public void testGenerationDemo() {
        String[] args = {"-d", "-p","src/main/resources/examples/pds3_example.lbl",
        		"-t","src/main/resources/examples/template_example.vm","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
    }    
	
    /**
     * Test Transformer with MER data
     */
    @Ignore
    @Test
    public void testGenerationMER() {
        String[] args = {"-d", "-p","src/main/resources/examples/1p216067135edn76pop2102l2m1.img",
        		"-t","src/main/resources/examples/mer_template.vm","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
    }
    
    /**
     * Test Generation Tool with MPF Data
     */
    @Ignore
    @Test
    public void testGenerationMPF() {
        String[] args = {"-d", "-p","src/main/resources/examples/i985135l.img",
        		"-t","src/main/resources/examples/MPF_IMP_EDR7.vm","-c","src/main/resources/conf"};
        GenerateLauncher.main(args);
    }    
}
