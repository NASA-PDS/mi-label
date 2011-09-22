//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.imaging.generate.label;

import java.util.Map;

public interface LabelObject {

    /**
     * Retrieves the value of this object's element as identified by key.
     * 
     * @param key
     * @return
     */
    public Object get(String key);

    /**
     * Set the elements of this object
     * 
     * @param elements
     */
    public void setElements(Map elements);
}
