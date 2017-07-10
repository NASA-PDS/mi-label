// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.imaging.generate.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PDSTreeMap extends LinkedHashMap{

	private static final long serialVersionUID = 4089687747792708305L;
	
	/**
	 * 
	 */
	@Override
	public Object put(Object key, Object value) {
		if (super.containsKey(key)) {
			List<Object> valueList = new ArrayList<Object>();
			if (super.get(key) instanceof List) {
			  valueList.addAll((List<Object>) super.get(key));
			} else {
			  valueList.add(super.get(key));
			}
			valueList.add(value);
			return super.put(key, valueList);
		} else {
			return super.put(key, value);
		}
	}
}
