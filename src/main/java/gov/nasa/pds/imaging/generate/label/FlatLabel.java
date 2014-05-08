//	Copyright 2013, by the California Institute of Technology.
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

import java.util.Iterator;
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
        return this._flatLabel.get(key);
    }

    @Override
    public void setElements(final Map elements) {
        this._flatLabel = elements;
    }

    @Override
    public String toString() {
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n[" + this._type + ":" + this._name + "]\n");
        String key = null;
        for (final Iterator iterator = this._flatLabel.keySet().iterator(); iterator
                .hasNext();) {
            key = (String) iterator.next();
            strBuff.append("  " + key + "=" + this._flatLabel.get(key) + "\n");
        }
        return strBuff.toString();
    }

}
