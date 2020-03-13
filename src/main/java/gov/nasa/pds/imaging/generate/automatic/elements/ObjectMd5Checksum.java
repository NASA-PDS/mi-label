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

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

/**
 * Generated value class that will generate an MD5 checksum for an object
 * within a file. This is specifically for attached labels or files with headers.
 * This class will truncate the header from the object and take the checksum of the
 * object itself.
 * 
 * @author jpadams
 * @author srlevoe
 *
 */
public class ObjectMd5Checksum implements Element {

  private PDSObject pdsObj;
  public long offset = 0;

  public ObjectMd5Checksum() { }

  @Override
  public String getUnits() {
    return null;
  }

  @Override
  public String getValue() {
    Md5Checksum checksum = null;
    Debugger.debug("ObjectMd5Checksum.getValue() XXX");
    try {
      // value to skip to data. We need a flag passed in via the PDSImageWriteParam
      // to decide if we checksum the entire image or only the data portion (excluding labels)
      // from a PDS3 input file
      // vicar files could have an End of file label, requires extra work to calculate the start of the EOL.
      String recbytes = (String)this.pdsObj.get("RECORD_BYTES");
      String recs = (String)this.pdsObj.get("LABEL_RECORDS");
      // from a vicar input file
      String lblsize = (String)this.pdsObj.get("SYSTEM.LBLSIZE");
      Debugger.debug("ObjectMd5Checksum.getValue() recbytes "+recbytes+", recs "+recs+" lblsize "+lblsize+" XXX");
      Long recbytes_Long = 0L;
      Long recs_Long = 0L;
      Long lblsize_Long = 0L;
      Long image_start_byte = this.pdsObj.getImageStartByte();
      // for a VICAR image this may not be enough to isolate the image data
      // a VICAR file can have an EOL label which starts after the data. If EOL =1
      // then we need to calculate the actual data size and use image_start_byte to skip there.
      // then we would only checksum the actual data bytes.

      if (recbytes != null) {
        recbytes_Long = Long.valueOf(recbytes);
      }
      if (recs != null) {
        recs_Long = Long.valueOf(recs);
      } 
      if (lblsize != null) {
        lblsize_Long = Long.valueOf(lblsize);
      } 
      // just use lblsize_long ??
      this.offset = recbytes_Long * recs_Long ;
      Debugger.debug("offset "+this.offset+"   lblsize_Long "+lblsize_Long+"  image_start_byte "+image_start_byte);

      checksum = new Md5Checksum(this.pdsObj, this.offset);
      Debugger.debug("ObjectMd5Checksum.getValue() checksum "+checksum.getValue()+"  XXX");
      return checksum.getValue();
    } catch (TemplateException e) {
      System.err.println("ERROR: RECORD_BYTES and/or LABEL_RECORDS keywords not found" +
          " These are required to generate a partial checksum.");
    } catch (java.lang.NumberFormatException nfe) {
      System.err.println("ERROR: java.lang.NumberFormatException " +
          " RECORD_BYTES and/or LABEL_RECORDS keywords not found" +
          " These are required to generate a partial checksum.");
    }
    return "Object Not Found";
  }



  @Override
  public void setParameters(final PDSObject pdsObject) {
    this.pdsObj = pdsObject;
  }
}
