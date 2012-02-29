package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;

public interface Element {
    public String getUnits();

    public String getValue() throws TemplateException;

    public void setParameters(PDSObject pdsObject);
}
