package gov.nasa.pds.imaging.generate.context;

import gov.nasa.pds.imaging.generate.TemplateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextUtil {

    private final List<Map<String, String>> objectList;
    private final Map<String, List<String>> elMap;
    private int elCnt;

    public ContextUtil() {
        this.objectList = new ArrayList<Map<String, String>>();
        this.elMap = new HashMap<String, List<String>>();
        this.elCnt = -1;
    }

    public void addDictionaryElement(final String key, final List<String> elList)
            throws TemplateException {
        final int currSize = elList.size();

        // Verify element count has been set, and is equal to previous element
        // lists
        if (this.elCnt == -1) {
            this.elCnt = elList.size();
        } else if (this.elCnt != currSize) {
            throw new TemplateException("Length of keyword lists must be equal");
        }

        this.elMap.put(cleanKey(key), elList);
    }

    private String cleanKey(final String str) {
        final String[] keyArr = str.split("\\.");
        return keyArr[keyArr.length - 1];
    }

    public List<Map<String, String>> getDictionary() {
        Map<String, String> map;
        final Set<String> keyList = this.elMap.keySet();
        for (int i = 0; i < this.elCnt; i++) {
            map = new HashMap<String, String>();
            for (final String key : keyList) {
                map.put(key, this.elMap.get(key).get(i).trim());
            }
            this.objectList.add(map);
        }
        return this.objectList;
    }
}
