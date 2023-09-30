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

// added to parse a json file srl-4-20-18
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.collections.PDSTreeMap;
import gov.nasa.pds.imaging.generate.context.ContextUtil;
import gov.nasa.pds.imaging.generate.readers.ParserType;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class JsonLabel implements PDSObject {
	
	public static String CONTEXT = "json";
	// allow this to be set by the user?? Not final
	public ContextUtil ctxtUtil;

	private static final boolean debug = false;
	// private static final boolean debug = true;


	
	
	// read in a json file  and store and access in the same way a PDS label is used
	  private JsonNode jsonNode;
	  
	  private String jsonFilename = "";
	  private String jsonString = "";
	  
	  // Contains a flattened representation of
	  // the PDS label. In this case, flattened
	  // means everything has been normalized
	  // to simple keyword=value pairs.
	  private Map<String, Map> flatLabel;
	  
	  private List<String> pdsObjectTypes;
	  private List<String> pdsObjectNames;
	  
	  private String filePath;
	  
	  
	  private Long image_start_byte = 0L;
	  
	  private ParserType parserType;
	  
	  private List<String> includePaths;
	  
	  private String readerFormat = "";

	  /**
	   * Empty Constructor, set everything later on
	   */
	  public JsonLabel() {
	      this.filePath = null;
	      this.jsonNode = null;
	      this.flatLabel = new PDSTreeMap();
	      this.parserType = ParserType.JSON;
	      this.includePaths = new ArrayList<String>();
	  }
	  
	  /**
	   * Constructor
	   * 
	   * Construct the Jsonlabel using a DOM object from somewhere else
	   * the JsonNode was created smewhere else
	   * 
	   * @param JsonNode
	   */
	  public JsonLabel(JsonNode jsonNode) {
	  	Debugger.debug("JsonLabel constructor JsonNode " + jsonNode);
	    this.filePath = "";
	    this.jsonNode = jsonNode;
	    this.parserType = ParserType.JSON;
	    this.flatLabel = new PDSTreeMap();
	    this.includePaths = new ArrayList<String>();
	  }

	  /**
	   * Constructor
	   *
	   ** the filePath will read and parsed as json
	   * @param filePath
	   */
	  public JsonLabel(final String filePath) {
	      this.filePath = filePath;
	      this.jsonNode = null;
	      this.flatLabel = new PDSTreeMap();
	      this.parserType = ParserType.JSON;
	      this.pdsObjectNames = new ArrayList<String>();
	      this.includePaths = new ArrayList<String>();
	  }

	  /**
	   * Retrieves the value for the specified key
	   *
	   * @param key
	   * @return value for key
	   */
	  @Override
	  public final Object get(final String key) {
		  if (debug) System.out.printf("++json++ node(0) get(%s) ------>\n", key);
		  // new Exception().printStackTrace();
	      final Object node = getNode(key.toUpperCase());
	      if (debug) System.out.printf("++json++ node(0.1) get(%s) after getNode  -->\n", key);
	     
	      Debugger.debug("\n\n++ Get " + key + " ----->");
	      
	      if (node == null) {
	    	 if (debug) System.out.printf("++json++ node(0.2) get(%s) node is null  -->\n", key);
	        return null;
	      } else if (node instanceof ItemNode) {
	      	Debugger.debug("++json++ node(2) ------>\n" + ((ItemNode) node).toString());
	      	if (debug)  {
	      		System.out.println("++json++ node(2) ------>\n" + ((ItemNode) node).toString());
	      		System.out.println("++json++ node(2) ------>\n" + StringEscapeUtils.escapeXml(((ItemNode) node).toString()));
	            }
	      	
	          return StringEscapeUtils.escapeXml(((ItemNode) node).toString());
	      } else {
	      	Debugger.debug("++ node(1) ------>\n" + node);
	      	if (debug) System.out.println("++ node(1) ------>\n" + node);
	          return node;
	      }
	  }

	  /**
	   * Returns the variable to be used in the Velocity Template Engine to map to
	   * this object.
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
	      Debugger.debug("JsonLabel.setFilePath this.filePath = "+this.filePath+" ");
	  }

	  @Override
	  @SuppressWarnings("rawtypes")
	  public final List getList(final String key) throws TemplateException {
		  if (debug)  System.out.printf("JsonLabel.getList(%s) \n", key);
	  	if (getNode(key) == null) {
	  		//return new GenerateEmptyList();
	  		return new ItemNode(key,null);
	  	} else {
	  		return ((ItemNode) getNode(key)).getValues();
	  	}
	  }

	  private final Object getNode(final String key) {
	      // Handles call where . is embedded in key.
	      // Mainly for IndexedGroup implementation.
		  if (debug) System.out.printf("JsonLabel.getNode(%s) \n", key);
	      if (key.contains(".")) {
	    	  if (debug) System.out.printf("JsonLabel.getNode(%s)  embedded '.' \n", key);
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
	       
	          if (debug) System.out.printf("JsonLabel.getNode(%s) return obj\n", key);
	          return obj;
	      } else if (key.contains("*") || key.contains("\\") || key.contains("+") ) {
	    	  if (debug) System.out.printf("## JsonLabel.getNode(%s) regex ***************** \n", key);
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
	    		if (debug) System.out.printf("this.pdsObjectNames: %s \n", name);
	  			keyMatcher = keyP.matcher(name);
	  			if (keyMatcher.find()) {
	  				if (debug) System.out.printf("this.pdsObjectNames: MATCH %s ****************\n", name);
	  				matches.add(name);	
	  				}			
	  			}
	    	 return matches;
	      }
	      
	      // System.out.printf("JsonLabel.getNode(%s) return this.flatLabel.get(key)\n", key);
	      // new Exception().printStackTrace();
	      // gov.nasa.pds.imaging.generate.label.ItemNode
	      Object value = this.flatLabel.get(key);
	      if (value instanceof ItemNode) {
			  
			  ItemNode in = (ItemNode) value;
			  
			  if (debug) { System.out.printf("## JsonLabel.getNode(%s) value is an ItemNode size = %d\n", key, in.size());}
			  if (in.size() > 1) {
				  // convert the ItemNode to an Arraylist. This will allow velocity templates to use indexes 
				  List values = new ArrayList<String>();
				  
				  for (int i = 0; i < in.size(); i++) {
					  String s = in.get(i);
					  if (debug) System.out.printf("%d) %s \n", i, s);
					  values.add(s);
				  }
				  if (debug) {System.out.printf("### JsonLabel.getNode(%s) value = %s - returning List\n", key, value);}
				  return values;
			  } else {
				  String s = in.get(0);
				  if (debug) {System.out.printf("### JsonLabel.getNode(%s) value = %s - check for array \n", key, s);}
				  if (s.startsWith("(") && s.endsWith(")") ) { // could also check for []
					  if (debug) System.out.printf("treat as an array s=%s \n", s);
					  List values = new ArrayList<String>();
					  if (debug) System.out.printf("s=%s replace ()\n", s);
					  // split by ,
					  s = s.replaceAll("[()]",""); 
					 
					  if (debug) System.out.printf("s=%s \n", s);
					  String[] words = s.split(",");
					  for (int i = 0; i < words.length; i++) {
						  String word = words[i];
						  if (debug) System.out.printf("%d) %s \n", i, word);
						  values.add(word);
					  }
					  if (debug) {System.out.printf("#### JsonLabel.getNode(%s) value = %s - %s returning List\n", key, value, values);}
					  return values;
				  } else if (s.startsWith("[") && s.endsWith("]") ) { // could also check for []
					  if (debug) System.out.printf("treat as an array s=%s \n", s);
					  List values = new ArrayList<String>();
					  if (debug) System.out.printf("s=%s replace []\n", s);
					  // split by ,
					  s = s.replaceAll("\\[|\\]", "");
					 
					  if (debug) System.out.printf("s=%s \n", s);
					  String[] words = s.split(",");
					  for (int i = 0; i < words.length; i++) {
						  String word = words[i];
						  if (debug) System.out.printf("%d) %s \n", i, word);
						  values.add(word);
					  }
					  if (debug) {System.out.printf("#### JsonLabel.getNode(%s) value = %s - %s returning List\n", key, value, values);}
					  return values;
				  }
				  
			  }
		  }
	      
	      
	      if (debug) {
	    	  if (value != null) {
		    	  if (debug) System.out.printf("## JsonLabel.getNode(%s) returning %s class = %s\n", key, value, value.getClass().toString() );
		    	  if (value instanceof ItemNode) {
		    		  
		    		  ItemNode in = (ItemNode) value;
		    		  
		    		  if (debug) System.out.printf("## JsonLabel.getNode(%s) value is an ItemNode size = %d\n", key, in.size());
		    	  }
	    	  } else{
	    		  if (debug) System.out.printf("## JsonLabel.getNode(%s) returning null value \n", key);
	    	  }
	    	  //  gov.nasa.pds.imaging.generate.label.ItemNode
	      }
	   	  
	   	  
	      // return this.flatLabel.get(key);
	      return value;
	  }

	  @Override
	  public final String getUnits(final String key) {
	      return ((ItemNode) getNode(key)).getUnits();
	  }

	  @Override
	  public final void setParameters(PDSObject pdsObject) {
	  	this.filePath = pdsObject.getFilePath();
	  	Debugger.debug("JsonLabel.setParameters this.filePath = "+this.filePath+" ");
	  	// this.imageInputStream = pdsObject.getImageInputStream();
	  	// Debugger.debug("JsonLabel.setParameters this.imageInputStream = "+ this.imageInputStream + " this.filePath = "+this.filePath+" ***");
	  }
	  
	  public void setImageInputStream(ImageInputStream iis) {
	  	// imageInputStream = iis;
	  }
	  
	  public ImageInputStream getImageInputStream() {
	  	// return imageInputStream ;
		  return null;
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
      public void setMappings() throws IOException {
		if (debug)  System.out.println("JsonLabel.setMapping parserType = "+this.parserType);
	   
		// assume we will always use the JSON parser ???
	    if (this.parserType.equals(ParserType.JSON)) {
		    if (!this.jsonString.equals("")) {
		    	// code from TreeModelParser1, convert TreeModal to Map
		    	// parse the string create the flatLabel
		    	if (debug) System.out.println("jsonString = "+this.jsonString);
		    	parseJson(this.jsonString);
		    }
		    
		    if (!this.jsonFilename.equals("")) {
		    	
		    	if (debug) System.out.println("JsonLabel.setMappings() jsonFilename = "+this.jsonFilename);
                FileInputStream fis = null;
				try {
					File f = new File(this.jsonFilename);
					fis = new FileInputStream(f);
					String js = IOUtils.toString(fis);
					this.jsonString = js;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.printf("FileNotFoundException "+e); 
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.printf("IOException "+e); 
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					System.out.printf("NullPointerException "+e); 
					e.printStackTrace();
                  } finally {
                    if (fis != null) {
                      fis.close();
                    }
				}
				
		    	// parse the string create the flatLabel
				// set an empty flatLabel if something fails
				parseJson(this.jsonString);
				String flatLabelStr = this.flatLabel.toString();
				if (debug) System.out.printf("JsonLabel flatLabel \n %s \n", flatLabelStr);
		    }
	    }
	    
	  }
	  
	  /**
	   * Added per request from mcayanan in order to be able to loop through the
	   * PDS Objects that can be found in the label
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
	  
	  public void setJsonFilename(String s) {
		  this.jsonFilename = s;
	  }
	  
	  public void setJsonString(String s) {
		  this.jsonString = s;
	  }
		// this.pds3Label.setJsonString(json_string);
	    
	  public void setParserType(ParserType type) {
	    this.parserType = type;
	  }
	  
	  /**
	   * Set the paths to search for files referenced by pointers.
	   * <p>
	   * Default is to always look first in the same directory
	   * as the label, then search specified directories.
	   * @param i List of paths
	   */
	  public void setIncludePaths(List<String> i) {
	    this.includePaths = new ArrayList<String> (i);
	    while(this.includePaths.remove(""));
	  }
	  
	  /**
	   * parseJson
	   * Parse a json String and load it into the flatLabel.
	   * The contents of the json will be used in exactly the same way as a PDS or vicar label
	   * When this JsonLabel is set into the context the velocity template prefix associated with it is set
	   * @param jsonString
	   */
	  public void parseJson(String jsonString) {
		  
		  // this.flatLabel = reader.traverseDOM(root);
	      // this.pdsObjectNames = reader.getPDSObjectNames();
		  if (debug) System.out.printf("######################### parseJson ######################################");
		  if (jsonString.isEmpty()) {
			  if (debug) System.out.printf("parseJson jsonString is empty");
			  return;
		  }
		  
		  this.pdsObjectNames = new ArrayList<String>(); // list of all parents
		  // Map<String, String> jsonLabel = new HashMap<String, String>();
		  // private Map<String, Map> flatLabel;
		  Map aFlatLabel = new PDSTreeMap();
		  
		  // this.flatLabel = new HashMap<String, String>();
		  this.flatLabel = aFlatLabel;
		  
	 // create an ObjectMapper instance.
		ObjectMapper mapper = new ObjectMapper();
		// use the ObjectMapper to read the json string and create a tree
		JsonNode node = null;
		String parentStr = ""; // add a '.' at the end
		String nodeName = "";
		String nodeValue = "";
		String units = "none";
		try {
			node = mapper.readTree(jsonString);
			// lets see what type the node is
			if (debug) {
				System.out.printf("nodeType = %s \n",node.getNodeType()); // prints OBJECT
				// is it a container
				System.out.printf("isContainerNode = %s \n", node.isContainerNode()); // prints true
			}
			// lets find out what fields it has
			Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				String fieldName = fieldNames.next();
				parentStr = "";
				nodeName = fieldName;
				// System.out.printf("fieldName = %s \n", fieldName);// prints title, message, errors,
												// total,
												// total_pages, page, limit, dataset
				JsonNode aNode = node.get(fieldName);
				if (debug) System.out.printf("%s = %s,   %s\n", fieldName, aNode.asText(), aNode.getNodeType().toString()); // prints
				if (aNode.getNodeType().toString().equals("ARRAY")) {
					handleJsonArray(aNode, nodeName, parentStr, aFlatLabel);
				} else if (aNode.getNodeType().toString().equals("OBJECT")) {
					parentStr = nodeName+"." ;
					handleJsonObject(aNode, nodeName, parentStr, aFlatLabel);
				} else {
					nodeValue = aNode.asText();
					if (debug) System.out.printf("#### %s%s = %s \n", parentStr, nodeName, nodeValue  );
					final Map<String, String> map = new HashMap<String, String>();
			    	map.put("units", "null"); // To ensure all labelItems have the proper combination of units and values
			    	map.put("values", nodeValue);
			    	// this.flatLabel.put(nodeName, map);
			    	
			    	ItemNode itemNode = new ItemNode(fieldName, units);
			    	itemNode.addValue(nodeValue);
			    	aFlatLabel.put(parentStr+nodeName, itemNode);
				}
			}

		} catch (IOException e ) {
			// TODO Auto-generated catch block
			if (debug) System.out.println("IOException reading jsonString "+e);
			e.printStackTrace();
		}
	  // end of parseJson 
	  }
	  
	  
	  /**
	   * handleJsonArray
	   * 
	   * @param aNode
	   * @param nodeName
	   * @param parentStr
	   * @param flatLabel
	   */
	  // private void handleJsonArray(JsonNode aNode, String nodeName, String parentStr, Map<String, Map> flatLabel) {
	  private void handleJsonArray(JsonNode aNode, String nodeName, String parentStr, Map flatLabel) {
	  
		  
		  if (debug) System.out.printf("handleJsonArray nodeName = %s parent=%s  aNode.size %d type=%s\n", nodeName, parentStr, aNode.size(), aNode.getNodeType().toString());
		  String nodeValue = "";
		  String NodeName = "";
		  String units = "none";
			Iterator<JsonNode> elements =  aNode.elements();
			if (debug) System.out.printf(" elements.size = %d [ ", aNode.size());
			String nodeValueStr = "[";
			
			ItemNode itemNode = new ItemNode(nodeName, units);
			
			// can be an array of values or an array of objects
			int i=0;
			while (elements.hasNext()) {
				JsonNode e = elements.next();
				itemNode.addValue(e.toString());
				if (i == aNode.size()-1) {
					// System.out.printf("%d %s ", i, e.toString());
					if (debug) System.out.printf(" %s", e.toString());
					nodeValueStr = nodeValueStr+e.toString();
				} else {
					// System.out.printf("%d %s, ", i, e.toString());
					if (debug) System.out.printf(" %s,", e.toString());
					nodeValueStr = nodeValueStr+e.toString()+",";
				}
				
				i++;  
			}
			if (debug) System.out.printf("]\n");
			nodeValueStr = nodeValueStr+"]";
			if (debug) System.out.printf("#### %s%s = %s \n", parentStr, nodeName, nodeValueStr  );
			
			final Map<String, String> map = new HashMap<String, String>();
	    	map.put("units", "null"); // To ensure all labelItems have the proper combination of units and values
	    	map.put("values", nodeValueStr);
	    	// flatLabel.put(parentStr+nodeName, map);
	    	
	    	flatLabel.put(parentStr+nodeName, itemNode);
	  }
	  
	  
	  /**
	   * handleJasonObject
	   * @param aNode
	   * @param nodeName
	   * @param parentStr
	   * @param flatLabel
	   */
	  // private void handleJsonObject(JsonNode aNode, String nodeName, String parentStr, Map<String, Map> flatLabel) {
	  private void handleJsonObject(JsonNode aNode, String nodeName, String parentStr, Map flatLabel) {
		  if (debug) {
			  System.out.printf("handleJsonObject #######################################################################\n");
		  	  System.out.printf("handleJsonObject loop thru this object aNode.size %d nodeName = %s parentStr = %s\n", aNode.size(), nodeName, parentStr);
	  		}
		  String nodeValue = "";
		  String nodeName2 = "";
		  String units = "none";
			Iterator<String> fieldNames = aNode.fieldNames();
			while (fieldNames.hasNext()) {
				String fieldName = fieldNames.next();
				JsonNode aNode2 = aNode.get(fieldName);
				
				nodeName2 = fieldName;
				if (aNode2.getNodeType().toString().equals("OBJECT")) { 
					if (debug) System.out.printf("handleJsonOBJECT %d parentStr = %s nodeName = %s nodeName2 = %s \n", aNode2.size(), parentStr, nodeName, nodeName2);
					parentStr = parentStr+"."+nodeName2;
					handleJsonObject(aNode2, nodeName2, parentStr, flatLabel) ;
				
				} else if (aNode2.getNodeType().toString().equals("ARRAY")) {
					if (debug) System.out.printf("handleJsonARRAY %d parentStr = %s nodeName = %s nodeName2 = %s \n", aNode2.size(), parentStr, nodeName, nodeName2);
					handleJsonArray(aNode2, nodeName2, parentStr, flatLabel) ;
					
				} else {
					String nodeValue2 = aNode2.toString();
					nodeValue = aNode.get(fieldName).asText();
					if (debug)  {
						System.out.printf("handleJsonObject %s.%s = %s %s  %s\n", nodeName, fieldName, nodeValue, nodeValue2, aNode2.getNodeType().toString());
						System.out.printf("#### %s%s = %s \n", parentStr, nodeName2, nodeValue  );
					}
					
					final Map<String, String> map = new HashMap<String, String>();
			    	map.put("units", "null"); // To ensure all labelItems have the proper combination of units and values
			    	map.put("values", nodeValue);
			    	// flatLabel.put(parentStr+nodeName, map);
			    	
			    	ItemNode itemNode = new ItemNode(fieldName, units);
			    	itemNode.addValue(nodeValue);
			    	flatLabel.put(parentStr+nodeName2, itemNode);
				}
			}
		  
	  }



}
