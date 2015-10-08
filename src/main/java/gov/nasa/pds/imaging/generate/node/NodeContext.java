package gov.nasa.pds.imaging.generate.node;

import gov.nasa.pds.imaging.generate.context.PDSObjectContext;

/**
 * Extension point for node-specific contexts
 * 
 * @author jpadams
 * 
 */
public interface NodeContext extends PDSObjectContext {
    public String getNode();
}
