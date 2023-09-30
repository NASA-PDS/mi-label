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

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class ExistTemplate {
  private VelocityEngine engine;

  public ExistTemplate(VelocityEngine e) {
    Debugger.debug("Constructor ExistTemplate.\n");
    engine = new VelocityEngine();
    engine = e;
  }

  public boolean exists(String name) {
    if (Debugger.debugFlag)
      System.out.printf("ExistTemplate.exists(%s) \n", name);
    try {
      // checks for both templates and static content
      boolean r = engine.resourceExists(name);
      if (Debugger.debugFlag)
        System.out.printf("ExistTemplate.exists(%s) %b \n", name, r);

      // now the template should be loaded if TRUE
      return r;
    }
    // make sure about this...
    catch (ResourceNotFoundException rnfe) {
      if (Debugger.debugFlag)
        System.out.printf("ExistTemplate.exists ResourceNotFoundException \n");
      return false;
    } catch (NullPointerException rnfe) {
      if (Debugger.debugFlag)
        System.out.printf("ExistTemplate.exists NullPointerException \n");
      return false;
    }
  }

  // could add parsing of the file if it found
  // check if we can specify a subdir and be able to find it
  // parseIfExists(String name)
  public String existstr(String name) {

    boolean r = false;
    try {
      // checks for both templates and static content
      r = engine.resourceExists(name);
      // System.out.printf("ExistTemplate.existstr(%s) %b \n", name, r);

    }
    // make sure about this...
    catch (ResourceNotFoundException rnfe) {
      if (Debugger.debugFlag)
        System.out.printf("ExistTemplate.existstr ResourceNotFoundException \n");
      r = false;
    } catch (NullPointerException rnfe) {
      if (Debugger.debugFlag)
        System.out.printf("ExistTemplate.existstr NullPointerException \n");
      r = false;
    }

    // decide what to put in this message
    // String rs = "ExistTemplate.existstr("+name+") "+r+" ";
    String rs = String.format("ExistTemplate.existstr(%s) %b ", name, r);
    // later return "TRUE" or "FALSE"
    // String rs = ""+r;
    if (Debugger.debugFlag)
      System.out.printf("%s \n", rs);

    return rs;
  }

}
