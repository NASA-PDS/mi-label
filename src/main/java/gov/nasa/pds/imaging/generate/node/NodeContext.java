package gov.nasa.pds.imaging.generate.node;

import gov.nasa.pds.imaging.generate.context.PDSContext;

/**
 * Extension point for node-specific contexts
 * 
 * @author jpadams
 * 
 */
public interface NodeContext extends PDSContext {
    public String getNode();
}
