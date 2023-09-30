// Copyright 2020, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.imaging.generate.automatic.elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.imageio.stream.ImageInputStream;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class Md5Checksum implements Element {

  public File file;
  private PDSObject pdsObj;
  private long offset = 0;

  public Md5Checksum() {}

  public Md5Checksum(PDSObject pdsObject, long offset) {
    setParameters(pdsObject);
    this.offset = offset;
  }

  /**
   * createChecksum
   * 
   * @return if this.file is empty then try ImageInputStream The value must be supplied from the
   *         constructor of the PDSObject
   */
  public byte[] createChecksum() {
    InputStream fis = null;
    ImageInputStream iis = null;
    MessageDigest complete = null;
    Debugger.debug("createChecksum() this.file >" + this.file + "< XXX");

    if (this.file == null || this.file.toString().isEmpty()) {
      Debugger.debug("NULL createChecksum() this.file " + this.file);
      // see below for the work to get the md5sum
      // I need to file input stream from the reader
      // this is the input file we are using to create a detached label. it is the data file
      // add it to the PDSObject when it is created
      // IOException NullPointerException
      // iis.seek() instead of fis.skip()
      // iis = pdsObj.getInputStream()
      // check for null ?? or try and catch exception??
      iis = this.pdsObj.getImageInputStream();
      Debugger.debug("createChecksum() iis >" + iis + "< XXX");
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
          Debugger.debug("IOException ImageInputStream  createChecksum()");
          e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
          Debugger.debug(
              "INoSuchAlgorithmException MessageDigest.getInstance(\"MD5\");  createChecksum()");
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
      checksum += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    }
    return checksum;
  }

  @Override
  public void setParameters(final PDSObject pdsObject) {
    this.pdsObj = pdsObject;
    this.file = new File(pdsObject.getFilePath());
  }
}
