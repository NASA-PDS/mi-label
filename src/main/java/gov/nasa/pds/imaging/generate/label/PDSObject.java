package gov.nasa.pds.imaging.generate.label;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.context.PDSContext;

import java.util.List;
import java.util.Map;

/**
 * Specific PDSContext applying to those contexts that contain PDS Data in DOM
 * object form.
 * 
 * @author jpadams
 * 
 */
public interface PDSObject extends PDSContext {
    public String getFilePath();

    public List getList(String key) throws TemplateException;
}
