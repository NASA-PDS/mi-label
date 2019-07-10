package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.stream.ImageInputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Checksum implements Element {

  public File file;
  private PDSObject pdsObj;
  private long offset = 0;

  public Md5Checksum() { }

  public Md5Checksum(PDSObject pdsObject, long offset) { 
    setParameters(pdsObject);
    this.offset = offset;
  }

  /**
   * createChecksum
   * @return
   * if this.file is empty then try ImageInputStream
   * The value must be supplied from the constructor of the PDSObject
   */
  public byte[] createChecksum() {
    InputStream fis = null;
    ImageInputStream iis = null;
    MessageDigest complete = null;
    Debugger.debug("createChecksum() this.file >"+this.file+"< XXX"); 

    if (this.file == null || this.file.toString().equals("")) {
      Debugger.debug("NULL createChecksum() this.file "+this.file);
      // see below for the work to get the md5sum
      // I need to file input stream from the reader
      // this is the input file we are using to create a detached label. it is the data file
      // add it to the PDSObject when it is created
      // IOException NullPointerException
      // iis.seek() instead of fis.skip()
      // iis = pdsObj.getInputStream()
      // check for null ?? or try and catch exception??
      iis = this.pdsObj.getImageInputStream();
      Debugger.debug("createChecksum() iis >"+iis+"< XXX"); 
      if (iis != null) {
        try {
          iis.seek(this.offset);
          final byte[] buffer = new byte[1024];
          complete = MessageDigest.getInstance("MD5");
          int numRead;
          do {
            numRead = iis.read(buffer);
            if (numRead > 0) {
              complete.update(buffer, 0, numRead);
            }
          } while (numRead != -1);

        } catch (IOException e) {
          // TODO Auto-generated catch block
          Debugger.debug("IOException ImageInputStream  createChecksum()" );
          e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
          Debugger.debug("INoSuchAlgorithmException MessageDigest.getInstance(\"MD5\");  createChecksum()" );
          // e.printStackTrace();
          Debugger.debug(e.getMessage());
        } 
      }

      Debugger.debug("createChecksum() return complete.digest() XXX"); 
    } else {
      try {
        fis = new FileInputStream(this.file);
        fis.skip(this.offset);
        final byte[] buffer = new byte[1024];
        complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
          numRead = fis.read(buffer);
          if (numRead > 0) {
            complete.update(buffer, 0, numRead);
          }
        } while (numRead != -1);
      } catch (final FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (final Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        try {
          fis.close();
        } catch (final IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      Debugger.debug("createChecksum() return complete.digest();");
    }
    if (complete != null) {
      return complete.digest();
    } else {
      return "Object Not Found".getBytes();
    }
  }

  @Override
  public String getUnits() {
    return null;
  }

  @Override
  public String getValue() {
    final byte[] b = createChecksum();
    String checksum = "";
    for (int i = 0; i < b.length; i++) {
      checksum += Integer.toString((b[i] & 0xff) + 0x100, 16)
          .substring(1);
    }
    return checksum;
  }

  @Override
  public void setParameters(final PDSObject pdsObject) {
    this.pdsObj = pdsObject;
    this.file = new File(pdsObject.getFilePath());
  }
}
