package gov.nasa.pds.imaging.generate.automatic.elements;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class ExistTemplate {
    private VelocityEngine engine;
    
    public ExistTemplate(VelocityEngine e) {
    	Debugger.debug("Constructor ExistTemplate.\n");
        engine = new VelocityEngine();
        engine = e;
    }

    public boolean exists(String name)
    {
    	if (Debugger.debugFlag) System.out.printf("ExistTemplate.exists(%s) \n", name);
        try
        {
          // checks for both templates and static content
          boolean r = engine.resourceExists(name);
          if (Debugger.debugFlag) System.out.printf("ExistTemplate.exists(%s) %b \n", name, r);
          
          // now the template should be loaded if TRUE
          return r;
        }
        // make sure about this...
        catch (ResourceNotFoundException rnfe)
        {
        	if (Debugger.debugFlag) System.out.printf("ExistTemplate.exists ResourceNotFoundException \n");
            return false;
        }
        catch (NullPointerException rnfe)
        {
        	if (Debugger.debugFlag) System.out.printf("ExistTemplate.exists NullPointerException \n");
            return false;
        }
    }
    
    // could add parsing of the file if it found
    // check if we can specify a subdir and be able to find it
    // parseIfExists(String name)
    public String existstr(String name)
    {
    	
    	boolean r = false;
    	try
        {
          // checks for both templates and static content
          r = engine.resourceExists(name);
          // System.out.printf("ExistTemplate.existstr(%s) %b \n", name, r);
            
        }
        // make sure about this...
        catch (ResourceNotFoundException rnfe)
        {
        	if (Debugger.debugFlag) System.out.printf("ExistTemplate.existstr ResourceNotFoundException \n");
            r = false;
        }
        catch (NullPointerException rnfe)
        {
        	if (Debugger.debugFlag) System.out.printf("ExistTemplate.existstr NullPointerException \n");
            r = false;
        }
    	
    	// decide what to put in this message
    	// String rs = "ExistTemplate.existstr("+name+") "+r+" ";
    	String rs = String.format("ExistTemplate.existstr(%s) %b ", name, r);
    	// later return "TRUE" or "FALSE"
    	// String rs = ""+r;
    	if (Debugger.debugFlag)  System.out.printf("%s \n", rs);
    	
        return rs;
    }

}