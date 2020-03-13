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

package gov.nasa.pds.imaging.generate.readers;

import gov.nasa.pds.imaging.generate.collections.PDSTreeMap;
import gov.nasa.pds.imaging.generate.label.FlatLabel;
import gov.nasa.pds.imaging.generate.label.ItemNode;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.GroupStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.Numeric;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.Scalar;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Set;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.util.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author mcayanan
 * 
 */
public class ProductToolsLabelReader {

  // TODO Refactor this into a java object
  private final List<String> pdsObjectTypes;
  private final List<String> pdsObjectNames;
  
  private List<URL> includePaths;
  
  private final List<String> pdsSimpleItemNames;

  public ProductToolsLabelReader() {

    // from PDS3 Label
    this.pdsObjectTypes = new ArrayList<String>();
    this.pdsObjectTypes.add(FlatLabel.GROUP_TYPE);
    this.pdsObjectTypes.add(FlatLabel.OBJECT_TYPE);
    
    this.pdsObjectNames = new ArrayList<String>();
    
    // all items not in a GROUP or OBJECT
    this.pdsSimpleItemNames = new ArrayList<String>();
    this.includePaths = new ArrayList<URL>();
    
  }

  /**
   * Handles the items created for each node that contain explicit information
   * about the node
   * 
   * i.e. quoted, units, etc.
   * 
   * @param item
   * @param container
   */
  private void handleItemNode(final AttributeStatement attribute, final Map container) {
    String elementName = attribute.getIdentifier().getId();
    String units = null;
      
    ItemNode itemNode;
    // If elementName is null, try to get it as a name
    if (elementName == null) {
      // try "name"
      elementName = attribute.getElementIdentifier();
    }

    // If elementName is still null, then let's get out of here.
    if (elementName == null) {
      // print something??
      Debugger.debug("1x) Return XXXX PDS3LabelReader.handleItemNode nodeName "+ attribute.getClass().getName() + ", elementName "+elementName);
      return;
    }
      
    // Check is the units is null
    // (jp) This doesn't make sense. We want to grab the units from
    //      the node...
    if (units == null) {
      units = "none"; // ""
    }
    itemNode = new ItemNode(elementName, units);

    // An item element node can either
    // have a #text child or subitem children
    if (attribute.getValue() instanceof Set) {
      Set values = (Set) attribute.getValue();
      for (Scalar value : values) {
        itemNode.addValue(value.toString());
      }
    } else if (attribute.getValue() instanceof Sequence) {
      Sequence values = (Sequence) attribute.getValue();
      for (Value value : values) {
        itemNode.addValue(value.toString());
      }
    } else {
      itemNode.addValue(attribute.getValue().toString());
    }
    
    Debugger.debug("2) PDS3LabelReader.handleItemNode nodeName "+ attribute.getClass().getName() + ", elementName "+elementName+" units "+units);
    Debugger.debug("2) itemNode "+itemNode);

    container.put(elementName, itemNode);
  }
    
  private void handlePointerNode(final PointerStatement pointer, final Map container) {
    String elementName = pointer.getIdentifier().getId();
    String units = null;
    
    ItemNode itemNode;
    elementName = "PTR_" + elementName; // could also be "HAT_"
    
    // Check is the units is null
    if (pointer.getValue() instanceof Sequence) {
      Sequence s = (Sequence) pointer.getValue();
      if (s.size() == 2) {
        Value value = s.get(1);
        if (value instanceof Numeric) {
          Numeric n = (Numeric) value;
          if (n.getUnits() != null) {
            units = n.getUnits();
          }
        }
      }
    }

    if (units == null) {
      units = "none"; // ""
    }
    itemNode = new ItemNode(elementName, units);

    // An item element node can either
    // have a #text child or subitem children
    if (pointer.getValue() instanceof Sequence) {
      Sequence s = (Sequence) pointer.getValue();
      for (int i = 0; i < s.size(); i++) {
        itemNode.addValue(s.get(i).toString());
      }
    } else {
      itemNode.addValue(pointer.getValue().toString());
    }
    
    
    Debugger.debug("2) PDS3LabelReader.handlePointerNode nodeName "+ pointer.getClass().getName() + ", elementName "+elementName+" units "+units);
    Debugger.debug("2) itemNode "+itemNode);

    container.put(elementName, itemNode);
  }

  
  /**
   * Used to recursively loop through the PDSObjects until a leaf item is
   * found
   * 
   * @param node
   * @param container
   */
  private void handlePDSObjectNode(final ObjectStatement objectStatement, final Map container) {
    String elementName = objectStatement.getIdentifier().getId();
      
    Debugger.debug("1) PDS3LabelReader.handlePDSObjectNode nodeName "+ objectStatement.getClass().getName() + " elementName "+elementName);
   

    final FlatLabel object = new FlatLabel(elementName, "OBJECT");

    // We now know this element is an object/group
    // So let's add it to object names to keep track
    // of all the groupings in the input object
    this.pdsObjectNames.add(elementName);
    
    final Map labels = new PDSTreeMap();

    for (Statement childStatement : objectStatement.getStatements()) {
      if (childStatement instanceof ObjectStatement ) { // Handles all items nested in groups
        handlePDSObjectNode((ObjectStatement) childStatement, labels);
      } else if (childStatement instanceof GroupStatement) {
        handlePDSGroupNode((GroupStatement) childStatement, labels);
      } else if (childStatement instanceof AttributeStatement) { // Handles all items at base level of label
        handleItemNode((AttributeStatement) childStatement, labels); 
      } else if (childStatement instanceof PointerStatement) {
      	handlePointerNode((PointerStatement) childStatement, labels);
      }
    }
    
    Debugger.debug("2) PDS3LabelReader.handlePDSObjectNode nodeName "+ objectStatement.getClass().getName() + " elementName "+elementName);
    Debugger.debug("2)  labels "+labels);
    
    object.setElements(labels);
    container.put(elementName, object);
  }
  
  private void handlePDSGroupNode(final GroupStatement groupStatement, final Map container) {
    String elementName = groupStatement.getIdentifier().getId();
      
    Debugger.debug("1) PDS3LabelReader.handlePDSGroupNode nodeName "+ groupStatement.getClass().getName() + " elementName "+elementName);
   

    final FlatLabel object = new FlatLabel(elementName, "OBJECT");

    // We now know this element is an object/group
    // So let's add it to object names to keep track
    // of all the groupings in the input object
    this.pdsObjectNames.add(elementName);
    
    final Map labels = new PDSTreeMap();

    for (Statement childStatement : groupStatement.getStatements()) {
      if (childStatement instanceof ObjectStatement ) { // Handles all items nested in groups
        handlePDSObjectNode((ObjectStatement) childStatement, labels);
      } else if (childStatement instanceof GroupStatement) {
        handlePDSGroupNode((GroupStatement) childStatement, labels);
      } else if (childStatement instanceof AttributeStatement) { // Handles all items at base level of label
        handleItemNode((AttributeStatement) childStatement, labels);
      } else if (childStatement instanceof PointerStatement) {
      	handlePointerNode((PointerStatement) childStatement, labels);
      }
    }
    
    Debugger.debug("2) PDS3LabelReader.handlePDSGroupNode nodeName "+ groupStatement.getClass().getName() + " elementName "+elementName);
    Debugger.debug("2)  labels "+labels);
    
    object.setElements(labels);
    container.put(elementName, object);
  }
  

  /**
   * Parse the label and create a XML DOM representation.
   * 
   * PDSLabelToDom: Within the DOM returned the Elements are:
   * 
   * PDS3 - At top of document to describe it is a PDS3 label COMMENT - All
   * commented text in label is contained within these elements item - A data
   * item at base level of label GROUP - A group of related elements
   * containing a collection of items OBJECT - A group of related elements
   * containing a collection of items
   * 
   * @param filePath
   * @throws Exception 
   */   
  public Label parseLabel(final String filePath) throws Exception {
    ManualPathResolver resolver = new ManualPathResolver();
    if (!includePaths.isEmpty()) {
      resolver.setIncludePaths(includePaths);
    } else {
      resolver.setBaseURI(ManualPathResolver.getBaseURI(new File(filePath).toURI()));
    }
    DefaultLabelParser parser = new DefaultLabelParser(true, true, true, resolver);
    try {
      Label label = parser.parseLabel(new File(filePath).toURI().toURL(), true);
      return label;
    } catch (LabelParserException lpe) {
      String message = "";
      try {
        message = MessageUtils.getProblemMessage(lpe);
      } catch (RuntimeException re) {
        // For now the MessageUtils class seems to be throwing this exception
        // when it can't find a key. In these cases the actual message seems to
        // be the key itself.
        message = lpe.getKey();
      }
      throw new Exception ("Error occurred while trying to parse label '"
          + filePath + "': " + message);
    } catch (IOException io) {
      throw new Exception ("Error occurred while trying to parse label '"
          + filePath + "': " + io.getMessage());
    }      
  }
  
  /**
   * Traverses the DOM returned by the PDSLabelToDom object.
   * 
   * @param root
   */    
  public Map<String, Map> traverseDOM(final Label label) {
    Debugger.debug("PDS3LabelReader.traverseDOM ***************************** ");
  
    // iterate through each label element and process
    final Map flatLabel = new PDSTreeMap();
    for (Statement statement : label.getStatements()) {
      // Check if this element node is one of:
      // GROUP, OBJECT, item, sub-item
      Debugger.debug("PDS3LabelReader.traverseDOM nodeName "+ statement.getIdentifier().getId() + " ");
      if (statement instanceof ObjectStatement) { // Handles all items nested in groups
        handlePDSObjectNode((ObjectStatement) statement, flatLabel);
      } else if (statement instanceof GroupStatement) {
        handlePDSGroupNode((GroupStatement) statement, flatLabel);
      } else if (statement instanceof AttributeStatement) { // Handles all items at base level of label
        AttributeStatement attribute = (AttributeStatement) statement;
        if (attribute.getIdentifier().getId().equals("PDS_VERSION_ID") 
            && attribute.getValue().toString().equals("PDS3")) {
          final Map<String, String> map = new LinkedHashMap<String, String>();
          map.put("units", "null"); // To ensure all labelItems have
                                    // the proper combination of units
                                   // and values
          map.put("values", attribute.getIdentifier().getId());
          flatLabel.put("PDS3", map);
        } else {
          handleItemNode(attribute, flatLabel);
          this.pdsSimpleItemNames.add(attribute.getElementIdentifier());
        }
      } else if (statement instanceof PointerStatement) {
    	PointerStatement ps = (PointerStatement) statement;
        handlePointerNode((PointerStatement) statement, flatLabel);
        this.pdsSimpleItemNames.add("PTR_" + ps.getIdentifier().getId());
      }
    }
  //  FlatLabel table = (FlatLabel) flatLabel.get("AFM_F_ERROR_TABLE");
  //  List<Object> subObjects = table.getSubObjects();
  //  for (Object subObject : subObjects) {
  //    FlatLabel child = (FlatLabel) subObject;
  //    System.out.println("Get child");
  //  }
 //   Object colObject = table.get("COLUMN");
 //   Object contObject = table.get("CONTAINER");
    
    
    return flatLabel;
  }
    
  public List<String> getPDSObjectNames() {
    return this.pdsObjectNames;
  }
  
  public List<String> getPDSSimpleItemNames() {
	return this.pdsSimpleItemNames;
  }
  
  /**
   * Set the paths to search for files referenced by pointers.
   * <p>
   * Default is to always look first in the same directory
   * as the label, then search specified directories.
   * @param i List of paths
   * @throws MalformedURLException 
   */
  public void setIncludePaths(List<String> paths)
      throws MalformedURLException {
    while(paths.remove(""));
    for (String path : paths) {
      URL url = null;
      try {
        url = new URL(path);
      } catch (MalformedURLException ex) {
        url = new File(path).toURI().toURL();
      }
      this.includePaths.add(url);
    }
  }
}

