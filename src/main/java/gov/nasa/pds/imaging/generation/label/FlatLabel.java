//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.imaging.generation.label;


import java.util.Iterator;
import java.util.Map;

public class FlatLabel implements LabelObject {
    public static final String OBJECT_TYPE = "OBJECT";
    public static final String GROUP_TYPE = "GROUP";

    // object name
    private String _name;
    
    // object type
    private String _type;
    
    // Contains a flattened representation of
    // label elements in this group. 
    private Map _flatLabel;
    
    public FlatLabel(String name,String type) {
        this._name = name;
        this._type = type;
    }
    
    @Override
	public void setElements(Map elements) {
        this._flatLabel = elements;
    }
    
    @Override
	public Object get(String key) {
        return this._flatLabel.get(key);
    }
    
    @Override
	public String toString() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n["+this._type+":"+this._name+"]\n");
        String key = null;
        for (Iterator iterator = this._flatLabel.keySet().iterator(); iterator.hasNext();) {
            key = (String)iterator.next();
            strBuff.append("  "+key+"="+this._flatLabel.get(key)+"\n");
        }
        return strBuff.toString();
    }
    
    

}
