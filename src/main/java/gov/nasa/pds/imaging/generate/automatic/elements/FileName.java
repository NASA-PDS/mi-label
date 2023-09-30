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
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class FileName implements Element {

  private File file;
  private String filePath = "";

  @Override
  public String getUnits() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getValue() {
    String name = "NA";
    if (Debugger.debugFlag)
      System.out.printf("FileName.getValue(1) name=%s filePath=%s\n", name, filePath);

    if (this.file != null) {
      name = this.file.getName();
    }
    if (Debugger.debugFlag)
      System.out.printf("FileName.getValue(2) name=%s\n", name);
    return name;
  }

  @Override
  public void setParameters(final PDSObject pdsObject) {
    this.filePath = pdsObject.getFilePath();
    if (Debugger.debugFlag)
      System.out.printf("FileName.setParameters >%s<\n", filePath);
    try {
      this.file = new File(pdsObject.getFilePath());
    } catch (final Exception e) {
      // TODO Auto-generated catch block
      System.out.println("Exception: " + e);
      // e.printStackTrace();
      this.file = null;
    }
  }
}
