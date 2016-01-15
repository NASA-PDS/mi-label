package gov.nasa.pds.imaging.generate.label;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.context.PDSObjectContext;

import java.util.List;
import java.util.Map;

import javax.imageio.stream.ImageInputStream;

/**
 * Specific PDSContext applying to those contexts that contain PDS Data in DOM
 * object form.
 * 
 * @author jpadams
 * 
 */
public interface PDSObject extends PDSObjectContext {
    public String getFilePath();

    public List getList(String key) throws TemplateException;
    
    public ImageInputStream getImageInputStream();
    
    public Long getImageStartByte();
}
