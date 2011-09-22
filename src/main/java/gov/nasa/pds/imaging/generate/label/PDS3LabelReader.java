package gov.nasa.pds.imaging.generate.label;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jpl.mipl.io.plugins.PDSLabelToDOM;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author astanboli
 * @author atinio
 * @author jpadams
 * 
 */
public class PDS3LabelReader {

    // private Map<String, Map> flatLabel;

    private final List<String> pdsObjectTypes;

    public PDS3LabelReader() {

        // TODO - make this configurable
        // read from config file maybe?
        this.pdsObjectTypes = new ArrayList<String>();
        this.pdsObjectTypes.add(FlatLabel.GROUP_TYPE);
        this.pdsObjectTypes.add(FlatLabel.OBJECT_TYPE);
    }

    private Map<String, String> getAttributes(final Node node) {
        final Map<String, String> attributes = new HashMap<String, String>();

        // Get any possible attributes of this element
        final NamedNodeMap attrs = node.getAttributes();
        Node attr = null;
        for (int i = 0; i < attrs.getLength(); ++i) {
            attr = attrs.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
        return attributes;
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
    private void handleItemNode(final Node item, final Map container) {
        final Map<String, String> attributes = getAttributes(item);
        final String elementName = attributes.get("key");

        final ItemNode itemNode = new ItemNode(attributes.get("key"),
                attributes.get("units"));

        // An item element node can either
        // have a #text child or subitem children
        final Node firstChild = item.getFirstChild();
        if (firstChild.getNodeType() == Node.TEXT_NODE) {
            // elementValues.put("values", firstChild.getNodeValue());
            itemNode.addValue(firstChild.getNodeValue());
            // container.put(elementName, firstChild.getNodeValue());
        } else {
            // item has subitems
            // TODO - can subitems have subitems?
            final NodeList subitems = item.getChildNodes();
            Node subitem = null;
            // List<String> list = new ArrayList<String>();
            for (int i = 0; i < subitems.getLength(); ++i) {
                subitem = subitems.item(i);
                // The subitem's child should be a #text node
                itemNode.addValue(subitem.getFirstChild().getNodeValue());
            }
        }

        container.put(elementName, itemNode);
    }

    /**
     * Used to recursively loop through the PDSObjects until a leaf item is
     * found
     * 
     * @param node
     * @param container
     */
    private void handlePDSObjectNode(final Node node, final Map container) {
        final Map attributes = getAttributes(node);
        final String elementName = (String) attributes.get("name");
        final FlatLabel object = new FlatLabel(elementName, node.getNodeName());

        final Map labels = new TreeMap();

        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node labelItem = children.item(i);
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {

                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
                if (this.pdsObjectTypes.contains(labelItem.getNodeName()
                        .toUpperCase())) {
                    handlePDSObjectNode(labelItem, labels);
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) {
                    handleItemNode(labelItem, labels);
                }

            }
        }
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
     * @throws FileNotFoundException
     */
    public Document parseLabel(final String filePath) throws FileNotFoundException {

        final BufferedReader input = new BufferedReader(new FileReader(filePath));
        // TODO - what is the purpose of this
        // in PDSLabelToDOM
        final PrintWriter output = new PrintWriter(System.out);

        // PDSLabelToDOM does not check if input file
        // contains a valid PDS label.

        // TODO Use VTool to determine if it is a valid PDS Label

        final PDSLabelToDOM pdsToDOM = new PDSLabelToDOM(input, output);
        return pdsToDOM.getDocument();
    }

    /**
     * Traverses the DOM returned by the PDSLabelToDom object.
     * 
     * @param root
     */
    public Map<String, Map> traverseDOM(final Node root) {
        final NodeList labelItems = root.getChildNodes();
        // iterate through each label element and process
        final Map<String, Map> flatLabel = new TreeMap<String, Map>();
        for (int i = 0; i < labelItems.getLength(); ++i) {
            final Node labelItem = labelItems.item(i);
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {

                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
                // System.out.println(labelItem.getNodeName() + " - " +
                // labelItem.getFirstChild().getNodeValue());
                if (this.pdsObjectTypes.contains(labelItem.getNodeName()
                        .toUpperCase())) { // Handles all items nested in groups
                    handlePDSObjectNode(labelItem, flatLabel);
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) { // Handles
                                                                               // all
                                                                               // items
                                                                               // at
                                                                               // base
                                                                               // level
                                                                               // of
                                                                               // label
                    handleItemNode(labelItem, flatLabel);
                } else if (labelItem.getNodeName().equalsIgnoreCase("PDS3")) { // PDS3
                                                                               // -
                                                                               // Version_id
                    final Map<String, String> map = new HashMap<String, String>();
                    map.put("units", "null"); // To ensure all labelItems have
                                              // the proper combination of units
                                              // and values
                    map.put("values", labelItem.getFirstChild().getNodeValue());
                    flatLabel.put(labelItem.getNodeName(), map);
                }

            }
        }
        return flatLabel;
    }

}
