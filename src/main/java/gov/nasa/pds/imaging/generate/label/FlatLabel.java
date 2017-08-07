//	Copyright 2013-2017, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.imaging.generate.label;

import gov.nasa.pds.imaging.generate.util.Debugger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlatLabel implements LabelObject {
	// For PDS3 Label
    public static final String OBJECT_TYPE = "OBJECT";
    public static final String GROUP_TYPE = "GROUP";

	// For VICAR Labels
    public static final String SYSTEM_TYPE = "SYSTEM";
    public static final String PROPERTY_TYPE = "PROPERTY";
    public static final String TASK_TYPE = "TASK";

    // object name
    private final String _name;

    // object type
    private final String _type;

    // Contains a flattened representation of
    // label elements in this group.
    private Map _flatLabel;

    public FlatLabel(final String name, final String type) {
        this._name = name;
        this._type = type;
    }

    @Override
    public Object get(final String key) {
        if (this._flatLabel.containsKey(key)) {
            return this._flatLabel.get(key);
        } else {
            return "GENERATE_VARIABLE_NOT_FOUND";
        }
    }

    public String getName() {
      return this._name;
    }
    
    public List<Object> getChildObjects() {
      List<Object> objects = new ArrayList<Object>();
      for (final Iterator iterator = this._flatLabel.keySet().iterator(); iterator
          .hasNext();) {
        Object object = this._flatLabel.get(iterator.next());
        if (object instanceof ItemNode) {
          continue;
        } else if (object instanceof FlatLabel) {
          objects.add(object);
        } else if (object instanceof ArrayList) {
          ArrayList list = (ArrayList) object;
          for (Object item : list) {
            if (item instanceof FlatLabel) {
              objects.add(item);
            }
          }
        }
      }
      return objects;
    }
    
    @Override
    public void setElements(final Map elements) {
        this._flatLabel = elements;
    }

    @Override
    public String toString() {
    	
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n[" + this._type + ":" + this._name + "]\n");
        Debugger.debug("++ FlatLabel.toString [" + this._type + ":" + this._name + "] ");
        String key = null;
        Object obj = null;
        String name = null;
        Object value = null;
        ArrayList args = null;
        for (final Iterator iterator = this._flatLabel.keySet().iterator(); iterator
                .hasNext();) {
        	obj = iterator.next();
        	key = (String) obj;
        	if (this._type.equals("TASK")) {
        		Debugger.debug("++ TASK FlatLabel.toString "+key+" obj is: "+obj.getClass().getName()+" ");
        	}
        	if (obj instanceof ArrayList) {
        		args = (ArrayList) obj;
        		Object arg_obj = null;
        		String arg_key = null;
        		// ItemNode itemNode = (ItemNode)
        		Debugger.debug("++ FlatLabel.toString  ArrayList ");
        		for (final Iterator iterator2 = args.iterator(); iterator2
                        .hasNext();) {
                	arg_obj = iterator2.next();
                	// arg_key = (String) arg_obj;
                	Debugger.debug("++ FlatLabel.toString  arg_obj is: "+arg_obj.getClass().getName()+" ");
        		}
        		
        	} else {
        		strBuff.append("  " + key + "=" + this._flatLabel.get(key) + "\n");
        		if (this._type.equals("TASK")) {
            		Debugger.debug("++ TASK FlatLabel.toString "+key+"=" + this._flatLabel.get(key) +" ");
            	}
        	}
        	
        	// key = (String) iterator.next();
        	       	
            // strBuff.append("  " + key + "=" + this._flatLabel.get(key) + "\n");
        }
        return strBuff.toString();
    }

}
