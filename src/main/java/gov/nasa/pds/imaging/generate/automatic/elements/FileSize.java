package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.label.PDSObject;

import java.io.File;

public class FileSize implements Element {

    private File file;

    public FileSize() {
    }

    @Override
    public String getUnits() {
        return null;
    }

    @Override
    public String getValue() {
        return String.valueOf(this.file.length());
    }

    @Override
    public void setParameters(final PDSObject pdsObject) {
        this.file = new File(pdsObject.getFilePath());
    }

}
