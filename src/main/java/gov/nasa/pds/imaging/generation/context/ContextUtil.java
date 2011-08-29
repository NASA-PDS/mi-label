package gov.nasa.pds.imaging.generation.context;

import gov.nasa.pds.imaging.generation.TemplateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextUtil {

	private List<Map<String, String>> objectList;
	private Map<String, List<String>> elMap;
	private int elCnt;
	
	public ContextUtil() {
		this.objectList = new ArrayList<Map<String, String>>();
		this.elMap = new HashMap<String, List<String>>();
		this.elCnt = -1;
	}
	
	public void addDictionaryElement(String key, List<String> elList) throws TemplateException {
		int currSize = elList.size();
		
		// Verify element count has been set, and is equal to previous element lists
		if (this.elCnt == -1)	// Set element count if it has not been set
			this.elCnt = elList.size();
		else if (this.elCnt != currSize)
			throw new TemplateException("Length of keyword lists must be equal");
		
		this.elMap.put(cleanKey(key), elList);
	}
	
	public List<Map<String, String>> getDictionary() {
		Map<String, String> map;
		Set<String> keyList = this.elMap.keySet();
		for (int i=0; i<this.elCnt; i++) {
			map = new HashMap<String, String>();
			for (String key : keyList) {
				map.put(key, this.elMap.get(key).get(i).trim());
			}
			this.objectList.add(map);
		}
		return this.objectList;
	}
	
	private String cleanKey(String str) {
		String[] keyArr = str.split("\\.");
		return keyArr[keyArr.length-1];
	}
}
