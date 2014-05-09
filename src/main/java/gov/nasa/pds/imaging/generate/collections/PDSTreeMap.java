package gov.nasa.pds.imaging.generate.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PDSTreeMap extends TreeMap{

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
