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

package gov.nasa.pds.imaging.generate.automatic;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.automatic.elements.Element;
import gov.nasa.pds.imaging.generate.context.PDSObjectContext;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.XMLUtil;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

public class AutoGeneratedElements implements PDSObjectContext {
  /** Specifies the CONTEXT to be mapped to the Velocity Templates. **/
  public static final String CONTEXT = "generate";

  /** The XML File Path for the file with the generated value mappings **/
  public static final String XML_FILENAME = "generated-mappings.xml";

  /** XML element name holding the key value **/
  public static final String XML_KEY = "context";

  /** XML element name holding the mapped value **/
  public static final String XML_VALUE = "class";

  /** Map that will hole the String -> Class mappings specified in the XML **/
  public Map<String, Class<?>> genValsMap = new HashMap<String, Class<?>>();

  // private String filePath;
  private PDSObject pdsObject;

  public AutoGeneratedElements() {
    Debugger.debug("AutoGeneratedElements constructor");
  }

  public void addMapping(final String key, final Class<?> value) {
    if (Debugger.debugFlag)
      System.out.printf("AutoGeneratedElements.addMappings key=%s", key);
    this.genValsMap.put(key, value);
  }

  @Override
  public String get(final String value) throws TemplateException {
    Element el;
    if (Debugger.debugFlag)
      System.out.printf("AutoGeneratedElements.get(%s) \n", value);
    String[] items = value.split("--");

    List<String> itemList = Arrays.asList(items);
    if (Debugger.debugFlag) {
      System.out.printf("items.length = %d \n", items.length);
      System.out.println(itemList);
    }

    try {
      if (items.length == 1) {
        el = (Element) this.genValsMap.get(value).newInstance();
        el.setParameters(this.pdsObject);
        String v = el.getValue();
        if (Debugger.debugFlag)
          System.out.printf("AutoGeneratedElements.get(%s) return %s\n", value, v);
        return v;
      } else {
        el = (Element) this.genValsMap.get(items[0]).newInstance();
        el.setParameters(this.pdsObject);
        // el.setValue(items[1]);
        return el.getValue();
      }
    } catch (final InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final NullPointerException e) {
      return "Object Not Found";
      // throw new TemplateException("Generated value: " + value +
      // " Not expected. Verify class mapping exists.");
    }

    return null;
  }

  @Override
  public String getContext() {
    return CONTEXT;
  }

  @Override
  public String getUnits(final String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setParameters(PDSObject pdsObject) {
    this.pdsObject = pdsObject;
  }

  @Override
  public void setMappings() throws Exception {
    Debugger.debug("AutoGeneratedElements.setMappings ");
    this.genValsMap.putAll(XMLUtil.getGeneratedMappings(
        AutoGeneratedElements.class.getResourceAsStream(XML_FILENAME), XML_KEY, XML_VALUE));
  }

}
