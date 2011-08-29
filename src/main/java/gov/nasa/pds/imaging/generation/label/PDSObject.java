package gov.nasa.pds.imaging.generation.label;

import gov.nasa.pds.imaging.generation.TemplateException;
import gov.nasa.pds.imaging.generation.context.PDSContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Specific PDSContext applying to those contexts that contain PDS Data in DOM object form.
 * 
 * @author jpadams
 *
 */
public interface PDSObject extends PDSContext {
	public String getFilePath();
	public List<Map<String, String>> getRecordsWithIndices(List<String> keys, String... keyword)  throws TemplateException;
	public List<Map<String, String>> getRecords(String... keyword)  throws TemplateException;
	public List getList(String key);
}
