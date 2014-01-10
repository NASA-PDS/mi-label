package gov.nasa.pds.imaging.generate.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PDSTreeMap extends TreeMap{

	private static final long serialVersionUID = 4089687747792708305L;

	@Override
	public Object put(Object key, Object value) {
		if (super.containsKey(key)) {
			List<Object> valueList = new ArrayList<Object>();
			valueList.add(super.get(key));
			valueList.add(value);
			System.out.println("ah ah ah you didnt say the magic words");
			return super.put(key, valueList);
		} else {
			return super.put(key, value);
		}
	}
}
