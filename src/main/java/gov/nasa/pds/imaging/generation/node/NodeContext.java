package gov.nasa.pds.imaging.generation.node;

import gov.nasa.pds.imaging.generation.context.PDSContext;

/**
 * Extension point for node-specific contexts
 * 
 * @author jpadams
 *
 */
public interface NodeContext extends PDSContext {
	public String getNode();
}
