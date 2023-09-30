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

import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

public class FileSize implements Element {

  private File file;
  private ImageInputStream imageInputStream;

  public FileSize() {}

  @Override
  public String getUnits() {
    return null;
  }

  @Override
  public String getValue() {
    // -1 indicates an error, unable to get length
    Debugger.debug("gov.nasa.pds.imaging.generate.automatic.elements.FileSize.getValue()");
    Debugger.debug("file=" + this.file + " imageInputStream=" + this.imageInputStream);
    long length = -1;
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

    Debugger.debug("length = " + length);
    return String.valueOf(length);
  }

  @Override
  public void setParameters(final PDSObject pdsObject) {
    this.file = new File(pdsObject.getFilePath());
    this.imageInputStream = pdsObject.getImageInputStream();
  }

}
