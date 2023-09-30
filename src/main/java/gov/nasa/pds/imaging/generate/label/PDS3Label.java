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

package gov.nasa.pds.imaging.generate.label;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.collections.PDSTreeMap;
import gov.nasa.pds.imaging.generate.context.ContextUtil;
import gov.nasa.pds.imaging.generate.readers.PDS3LabelReader;
import gov.nasa.pds.imaging.generate.readers.ParserType;
import gov.nasa.pds.imaging.generate.readers.ProductToolsLabelReader;
import gov.nasa.pds.imaging.generate.readers.VICARReaderException;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.tools.label.Label;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Represents PDS3 Label object to provide the necessary functionality to
 *
 * @author jpadams
 * @author srlevoe
 *
 */
public class PDS3Label implements PDSObject {

  public static final String CONTEXT = "label";
  public ContextUtil ctxtUtil;

  private static final boolean debug = false;
  // private static final boolean debug = true;

  // Contains the DOM representation of
  // the PDS label as generated by thethis.flatLabel.get(key);
  // PDSLabel2DOM parser
  private Document vicarDocument;

  // Contains an object representation of
  // the PDS label as generated by the
  // product-tools library
  private Label labelDocument;

  // Contains a flattened representation of
  // the PDS label. In this case, flattened
  // means everything has been normalized
  // to simple keyword=value pairs.
  private Map<String, Map> flatLabel;

  private List<String> pdsObjectTypes;
  private List<String> pdsObjectNames;

  private List<String> pdsSimpleItemNames;

  private String filePath;

  private ImageInputStream imageInputStream;

  private Long image_start_byte = 0L;

  private ParserType parserType;

  private List<String> includePaths;

  private String readerFormat = "";

  /**
   * Empty Constructor, set everything later on
   */
  public PDS3Label() {
    this.filePath = null;
    this.flatLabel = new PDSTreeMap();
    this.parserType = ParserType.VICAR;
    this.includePaths = new ArrayList<String>();
  }

  /**
   * Constructor
   * 
   * Construct the PDS3label using a DOM object from somewhere else
   * 
   * @param document
   */
  public PDS3Label(Document document) {
    Debugger.debug("PDS3Label constructor document " + document);
    this.filePath = "";
    this.imageInputStream = null;
    this.vicarDocument = document;
    this.parserType = ParserType.VICAR;
    this.flatLabel = new PDSTreeMap();
    this.includePaths = new ArrayList<String>();
  }

  /**
   * Constructor
   *
   * @param filePath
   */
  public PDS3Label(final String filePath) {
    this.filePath = filePath;
    this.flatLabel = new PDSTreeMap();
    this.parserType = ParserType.VICAR;
    this.pdsObjectNames = new ArrayList<String>();
    this.includePaths = new ArrayList<String>();

    this.pdsSimpleItemNames = new ArrayList<String>();
  }

  /**
   * Retrieves the value for the specified key
   *
   * @param key
   * @return value for key
   */
  @Override
  public final Object get(final String key) {
    Debugger.debug("++++ node(0) get(" + key + ") ------>\n");
    // new Exception().printStackTrace();
    final Object node = getNode(key.toUpperCase());
    Debugger.debug("++++ node(0.1) get(" + key + ") after getNode  -->\n");
    Debugger.debug("\n\n++ Get " + key + " ----->");

    if (node == null) {
      Debugger.debug("++++ node(0.2) get(" + key + ") node is null  -->\n");
      return null;
    } else if (node instanceof ItemNode) {
      Debugger.debug("++++ node(2) ------>\n" + ((ItemNode) node));

      return node;
    } else {
      Debugger.debug("++ node(1) ------>\n" + node);
      return node;
    }
  }

  /**
   * Returns the variable to be used in the Velocity Template Engine to map to this object.
   */
  @Override
  public final String getContext() {
    return CONTEXT;
  }

  @Override
  public final String getFilePath() {
    return this.filePath;
  }

  @Override
  public void setFilePath(String filePath) {

    this.filePath = filePath;
    Debugger.debug("PDS3label.setFilePath this.filePath = " + this.filePath + " ");
  }

  @Override
  @SuppressWarnings("rawtypes")
  public final List getList(final String key) throws TemplateException {
    Debugger.debug("PDS3Label.getList(" + key + " \n");
    if (getNode(key) == null) {
      // return new GenerateEmptyList();
      return new ItemNode(key, null);
    } else {
      return ((ItemNode) getNode(key)).getValues();
    }
  }

  private final Object getNode(final String key) {
    // Handles call where . is embedded in key.
    // Mainly for IndexedGroup implementation.
    Debugger.debug("PDS3Label.getNode(" + key + ") \n");
    if (key.contains(".")) {
      Debugger.debug("PDS3Label.getNode(" + key + ")  embedded '.' \n");
      final String[] links = key.split("\\.");
      // object->item
      // object->subobject->item
      if (links[0] == null) {
        return null;
      }
      LabelObject labelObj = (LabelObject) this.flatLabel.get(links[0]);
      if (labelObj == null) {
        return null;
      }
      Object obj = null;
      for (int i = 1; i < links.length; ++i) {
        obj = labelObj.get(links[i]);
        if (obj instanceof LabelObject) {
          labelObj = (LabelObject) obj;
        }
      }

      Debugger.debug("PDS3Label.getNode(" + key + ") return obj\n");
      return obj;
    } else if (key.contains("*") || key.contains("\\") || key.contains("+")) {
      Debugger.debug("## PDS3Label.getNode(" + key + ") regex ***************** \n");
      // return this.pdsObjectNames;
      // build a new List of all names that match
      // loop thru the members of this List
      // private List<String> pdsObjectNames;
      List matches = new ArrayList<String>();
      // this.pdsObjectNames.s

      Pattern keyP = Pattern.compile(key);
      Matcher keyMatcher;

      for (int i = 0; i < this.pdsObjectNames.size(); i++) {
        String name = this.pdsObjectNames.get(i);
        Debugger.debug("this.pdsObjectNames: " + name + " \n");
        keyMatcher = keyP.matcher(name);
        if (keyMatcher.find()) {
          Debugger.debug("this.pdsObjectNames: MATCH " + name + " ****************\n");
          matches.add(name);
        }
      }
      if (matches.size() > 0) {
        return matches;
      }

      for (int i = 0; i < this.pdsSimpleItemNames.size(); i++) {
        String name = this.pdsSimpleItemNames.get(i);
        Debugger.debug("this.pdsSimpleItemNames: " + name + " \n");
        keyMatcher = keyP.matcher(name);
        if (keyMatcher.find()) {
          Debugger.debug("this.pdsSimpleItemNames: MATCH " + name + " ****************\n");
          matches.add(name);
        }
      }
      return matches;
    }


    // new Exception().printStackTrace();
    // gov.nasa.pds.imaging.generate.label.ItemNode
    Object value = this.flatLabel.get(key);
    if (value instanceof ItemNode) {

      ItemNode in = (ItemNode) value;
      // in.getUnits()


      Debugger.debug("## PDS3Label.getNode(" + key + ") value is an ItemNode size = " + in.size()
          + " name " + in.getName() + " units " + in.getUnits() + " \n");
      if (in.size() > 1) {
        // convert the ItemNode to an Arraylist. This will allow velocity templates to use indexes
        List values = new ArrayList<String>();

        for (int i = 0; i < in.size(); i++) {
          String s = in.get(i);
          Debugger.debug(String.format("%d) %s \n", i, s));
          /***
           * if (in.getName().startsWith("PTR_")) { if (i == 1) { if (!in.getUnits().equals("none"))
           * { s += " " + in.getUnits(); } } }
           ***/
          // Debugger.debug("## PDS3Label.getNode("+key+") i="+i+" s="+s+" ");
          values.add(s);
        }
        Debugger.debug(
            String.format("### PDS3Label.getNode(%s) value = %s - returning List\n", key, value));
        return values;
      } else {
        String s = in.get(0);
        Debugger.debug(
            String.format("### PDS3Label.getNode(%s) value = %s - check for array \n", key, s));
        if (s.startsWith("(") && s.endsWith(")")) { // could also check for []
          Debugger.debug(String.format("() treat as an array s=%s \n", s));
          List values = new ArrayList<String>();
          Debugger.debug(String.format("s=%s replace ()\n", s));
          // split by ,
          s = s.replaceAll("[()]", "");

          Debugger.debug(String.format("s=%s \n", s));
          String[] words = s.split(",");
          for (int i = 0; i < words.length; i++) {
            String word = words[i];
            System.out.printf("%d) %s \n", i, word);
            values.add(word);
          }
          Debugger.debug(String.format(
              "#### PDS3Label.getNode(%s) value = %s - %s returning List\n", key, value, values));
          return values;
        } else if (s.startsWith("[") && s.endsWith("]")) { // could also check for []
          Debugger.debug(String.format("[] treat as an array s=%s \n", s));
          List values = new ArrayList<String>();
          Debugger.debug(String.format("s=%s replace []\n", s));
          // split by ,
          s = s.replaceAll("\\[|\\]", "");

          Debugger.debug(String.format("s=%s \n", s));
          String[] words = s.split(",");
          for (int i = 0; i < words.length; i++) {
            String word = words[i];
            System.out.printf("%d) %s \n", i, word);
            values.add(word);
          }
          Debugger.debug(String.format(
              "#### PDS3Label.getNode(%s) value = %s - %s returning List\n", key, value, values));
          return values;
        }

      }

    }

    if (Debugger.debugFlag) {
      if (value != null) {
        System.out.printf("## PDS3Label.getNode(%s) returning %s class = %s\n", key, value,
            value.getClass().toString());
        if (value instanceof ItemNode) {

          ItemNode in = (ItemNode) value;

          System.out.printf("## PDS3Label.getNode(%s) value is an ItemNode size = %d\n", key,
              in.size());
        }
      } else {
        System.out.printf("## PDS3Label.getNode(%s) returning null value \n", key);
      }
      // gov.nasa.pds.imaging.generate.label.ItemNode
    }


    // return this.flatLabel.get(key);
    return value;
  }

  @Override
  public final String getUnits(final String key) {

    if (key.contains(".")) {
      return ((ItemNode) getNode(key)).getUnits();
    } else {
      Object value = this.flatLabel.get(key);
      if (value instanceof ItemNode) {
        ItemNode in = (ItemNode) value;
        return in.getUnits();
      } else {
        return "";
      }

    }

    // return ((ItemNode) getNode(key)).getUnits();
  }

  @Override
  public final void setParameters(PDSObject pdsObject) {
    this.filePath = pdsObject.getFilePath();
    Debugger.debug("PDS3label.setParameters this.filePath = " + this.filePath + " ");
    this.imageInputStream = pdsObject.getImageInputStream();
    Debugger.debug("PDS3Label.setParameters this.imageInputStream = " + this.imageInputStream
        + " this.filePath = " + this.filePath + " ***");
  }

  public void setImageInputStream(ImageInputStream iis) {
    imageInputStream = iis;
  }

  public ImageInputStream getImageInputStream() {
    return imageInputStream;
  }

  public void setReaderFormat(String format) {
    this.readerFormat = format;
  }

  public String getReaderFormat() {
    return this.readerFormat;
  }

  public void setImageStartByte(Long x) {
    image_start_byte = x;
  }

  @Override
  public Long getImageStartByte() {
    return image_start_byte;
  }

  @Override
  public void setMappings() {
    Debugger.debug("PDS3Label.setMapping parserType = " + this.parserType);
    if (this.parserType.equals(ParserType.VICAR)) {
      Debugger.debug("+++++++++++++++++++++++++++\n" + "PDS3Label.setMapping()\n" + "DOM document "
          + this.vicarDocument + "\n" + "+++++++++++++++++++++++++++");
      try {

        final PDS3LabelReader reader = new PDS3LabelReader();
        // Look to see if a document was already set
        if (this.vicarDocument == null) {
          Debugger.debug("+++++++++++++++++++++++++++\n"
              + "PDS3Label.setMapping() DOM document is null parseLabel");

          this.vicarDocument = reader.parseLabel(this.filePath);
        } else { // we already have the document supplied in the constructor
          Debugger.debug("+++++++++++++++++++++++++++\n"
              + "PDS3Label.setMapping() DOM document is GOOD using");
        }

        // start of traversal of DOM
        if (this.vicarDocument == null) {
          throw new VICARReaderException(
              "VICAR library unable to read document. Consider trying Product Tools library as an alternative reader. See Advanced Usage documentation for more details.");
        }
        final Node root = this.vicarDocument.getDocumentElement();

        this.flatLabel = reader.traverseDOM(root);
        this.pdsObjectNames = reader.getPDSObjectNames();
        this.pdsSimpleItemNames = reader.getPDSSimpleItemNames();
      } catch (final FileNotFoundException fnfe) {
        // TODO - create a logger
        Debugger.debug("PDS3Label.setMapping FileNotFoundException " + fnfe);
        fnfe.printStackTrace();
      } catch (Exception e) {
        Debugger.debug("PDS3Label.setMapping Exception " + e);
        e.printStackTrace();
      }
    } else if (this.parserType.equals(ParserType.PRODUCT_TOOLS)) {
      // Using the Product-Tools parser
      Debugger.debug("+++++++++++++++++++++++++++\n" + "PDS3Label.setMapping()\n"
          + "product-tools document " + this.labelDocument + "\n" + "+++++++++++++++++++++++++++");
      try {
        final ProductToolsLabelReader reader = new ProductToolsLabelReader();
        reader.setIncludePaths(includePaths);
        // Look to see if we a document was already set
        if (this.labelDocument == null) {
          Debugger.debug("+++++++++++++++++++++++++++\n"
              + "PDS3Label.setMapping() product-tools document is null parseLabel");
          this.labelDocument = reader.parseLabel(this.filePath);
        } else { // we already have the document supplied in the constructor
          Debugger.debug("+++++++++++++++++++++++++++\n"
              + "PDS3Label.setMapping() product-tools document is GOOD using");
        }

        // start of traversal of label object
        this.flatLabel = reader.traverseDOM(this.labelDocument);
        this.pdsObjectNames = reader.getPDSObjectNames();
        this.pdsSimpleItemNames = reader.getPDSSimpleItemNames();
      } catch (final FileNotFoundException fnfe) {
        // TODO - create a logger
        fnfe.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


  }

  /**
   * Added per request from mcayanan in order to be able to loop through the PDS Objects that can be
   * found in the label
   * 
   * @return
   */
  public final List<String> getPDSObjectNames() {
    return this.pdsObjectNames;
  }

  @Override
  public final String toString() {
    final StringBuffer strBuff = new StringBuffer();
    final Set<String> keys = this.flatLabel.keySet();
    for (final String key : keys) {
      // String key = (String) iter.next();
      strBuff.append(key + " = " + this.flatLabel.get(key) + "\n");
    }
    return strBuff.toString();
  }

  public void setParserType(ParserType type) {
    this.parserType = type;
  }

  /**
   * Set the paths to search for files referenced by pointers.
   * <p>
   * Default is to always look first in the same directory as the label, then search specified
   * directories.
   * 
   * @param i List of paths
   */
  public void setIncludePaths(List<String> i) {
    this.includePaths = new ArrayList<String>(i);
    while (this.includePaths.remove(""));
  }



}
