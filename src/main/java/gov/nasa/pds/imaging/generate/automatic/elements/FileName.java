package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.label.PDSObject;

import java.io.File;

public class FileName implements Element {

    private File file;

    @Override
    public String getUnits() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getValue() {
        return this.file.getName();
    }

    @Override
    public void setParameters(final PDSObject pdsObject) {
        this.file = new File(pdsObject.getFilePath());
    }

}
