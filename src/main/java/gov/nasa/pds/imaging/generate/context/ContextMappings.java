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

package gov.nasa.pds.imaging.generate.context;

import java.util.HashMap;
import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.XMLUtil;

public class ContextMappings {

  public static final String XML_FILENAME = "context-classes.xml";

  /** XML element name holding the key value **/
  public static final String XML_TAG = "class";

  public HashMap<String, PDSContext> contextMap = new HashMap<>();

  public ContextMappings() throws TemplateException, Exception {
    for (final String cl : XMLUtil
        .getClassList(ContextMappings.class.getResourceAsStream(XML_FILENAME), XML_TAG)) {
      final PDSContext context = (PDSContext) Class.forName(cl).newInstance();
      this.contextMap.put(context.getContext(), context);
    }
  }

  /**
   * Populates the contextMap with those classes specified in the context mappings XML file.
   *
   * @throws TemplateException
   * @throws Exception
   */
  public ContextMappings(final PDSObject pdsObject) throws TemplateException, Exception {
    Debugger.debug("Generator.ContextMappings()");
    for (final String cl : XMLUtil
        .getClassList(ContextMappings.class.getResourceAsStream(XML_FILENAME), XML_TAG)) {
      Object instance = Class.forName(cl).newInstance();
      if (instance instanceof PDSObjectContext) {
        final PDSObjectContext context = (PDSObjectContext) instance;
        context.setParameters(pdsObject);
        context.setMappings();
        this.contextMap.put(context.getContext(), context);
      } else if (instance instanceof PDSContext) {
        final PDSContext context = (PDSContext) instance;
        this.contextMap.put(context.getContext(), context);
      }
    }
  }

  public void addMapping(final String key, final PDSContext value) {
    this.contextMap.put(key, value);
  }

}
