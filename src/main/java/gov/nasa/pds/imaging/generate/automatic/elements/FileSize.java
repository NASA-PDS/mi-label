package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

public class FileSize implements Element {

  private File file;
  private ImageInputStream imageInputStream;

  public FileSize() {
  }

  @Override
  public String getUnits() {
    return null;
  }

  @Override
  public String getValue() {
    // -1 indicates an error, unable to get length 
    Debugger.debug("gov.nasa.pds.imaging.generate.automatic.elements.FileSize.getValue()");
    Debugger.debug("file="+this.file+" imageInputStream="+this.imageInputStream);
    long length =  -1;
    if (this.imageInputStream != null) {

      try {
        length = this.imageInputStream.length();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } else if (this.file != null) {
      length = this.file.length();

    } else {
      length = -1;
    }

    Debugger.debug("length = "+length);
    return String.valueOf(length);
  }

  @Override
  public void setParameters(final PDSObject pdsObject) {
    this.file = new File(pdsObject.getFilePath());
    this.imageInputStream = pdsObject.getImageInputStream();
  }

}
