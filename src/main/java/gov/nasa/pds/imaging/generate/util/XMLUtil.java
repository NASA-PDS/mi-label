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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import gov.nasa.pds.imaging.generate.TemplateException;

/**
 * Utility class for reading XML files
 * 
 * @author jpadams
 * 
 */
public class XMLUtil {
    /**
     * Static method that returns a list of Classes that will be extracted from the XML file for
     * context mappings.
     * 
     * @param file
     * @param tag
     * @return
     * @throws TemplateException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static List<String> getClassList(final InputStream inputStream, final String tag)
        throws TemplateException, ParserConfigurationException, SAXException, IOException {
      try {
        final List<String> classList = new ArrayList<String>();

        final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);

        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        final Document doc = builder.parse(inputStream);
        final NodeList classes = doc.getElementsByTagName(tag);

        for (int i = 0; i < classes.getLength(); i++) {
            classList.add(classes.item(i).getTextContent());
        }

        return classList;
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
    }

    /**
     * A static method that returns the mapping of String to Class for Generated Values found in the
     * Velocity Template.
     * 
     * @param file
     * @param key
     * @param value
     * @return
     * @throws TemplateException
     * @throws IOException
     * @throws SAXException
     * @throws Exception
     */
    public static Map<String, Class<?>> getGeneratedMappings(final InputStream inputStream,
        final String key, final String value) throws TemplateException, SAXException, IOException {
        final Map<String, Class<?>> map = new HashMap<String, Class<?>>();
        try {
          final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                  .newInstance();
          domFactory.setNamespaceAware(true);
  
          final DocumentBuilder builder = domFactory.newDocumentBuilder();
          final Document doc = builder.parse(inputStream);
  
          final NodeList contexts = doc.getElementsByTagName(key);
          final NodeList classes = doc.getElementsByTagName(value);
  
          for (int i = 0; i < contexts.getLength(); i++) {
              map.put(contexts.item(i).getTextContent(),
                      Class.forName(classes.item(i).getTextContent()));
          }
  
          return map;
        } catch (SAXException sax) {
          throw new TemplateException(
              "Internal Error. Could not read config files: " + sax.getMessage());
        } catch (ParserConfigurationException | ClassNotFoundException e) {
          // We should never get this error, so if we do, let's just stacktrace it and crash
          e.printStackTrace();
          System.exit(1);
        } finally {
          IOUtils.closeQuietly(inputStream);
        }
        return null;
    }
}
