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

package gov.nasa.pds.imaging.generate.util;

import org.apache.commons.lang.WordUtils;

import gov.nasa.pds.imaging.generate.context.PDSContext;

public class TextUtil extends WordUtils implements PDSContext {
  public static final String CONTEXT = "text";

  public String getContext() {
    return CONTEXT;
  }

  /**
   * Utility method used to transform the str into the applicable PDS title-case format for
   * enumerated values.
   * 
   * @param str
   * @return
   */
  public static String capitalize(String str) {
    if (str != null) {
      str = str.replace('_', ' ');
      return WordUtils.capitalizeFully(str);
    } else {
      return str;
    }
  }

  /**
   * Utility method used to transform the object into the applicable PDS title-case format for
   * enumerated values.
   * 
   * @param obj
   * @return
   */
  public static String capitalize(Object obj) {
    if (obj != null) {
      String str = obj.toString().replace('_', ' ');
      return WordUtils.capitalizeFully(str);
    } else {
      return null;
    }
  }
}
