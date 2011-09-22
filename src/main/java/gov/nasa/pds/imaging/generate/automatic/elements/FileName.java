package gov.nasa.pds.imaging.generate.automatic.elements;

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
        return file.getName();
    }

    @Override
    public void setParameters(final String filePath) {
        this.file = new File(filePath);
    }

}
