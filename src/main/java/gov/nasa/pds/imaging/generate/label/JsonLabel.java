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
import java.util.List;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.context.ContextUtil;
import gov.nasa.pds.imaging.generate.readers.ParserType;
import gov.nasa.pds.imaging.generate.util.Debugger;

/** Parse a JSON object and make it available to VTL.
 * Originally this was a complicated derivative of PDSObject.  However,
 * it turns out that if we just use the tree returned by the JSON parser,
 * everything Just Works, except for spurious quotes around string values
 * (which is handled both here and in NoQuiteUberspector.java).
 * It probably does not need to be derived from PDSObject at this point,
 * but I did not want to mess with the calling API.
 */

public class JsonLabel implements PDSObject {
	
	public static String CONTEXT = "json";
	// allow this to be set by the user?? Not final
	public ContextUtil ctxtUtil;

	private static final boolean debug = false;

	private JsonNode jsonNode;	// The actual tree
	  
	private String jsonFilename = "";
	private String jsonString = "";

	private String filePath;

	// The below values aren't really used for anything other than
	// superclass compatibility.  May not be needed at all.

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
	    this.parserType = ParserType.JSON;
	    this.includePaths = new ArrayList<String>();
	}

	/**
	 * Retrieves the value for the specified key
	 *
	 * Dequotes text values.
	 *
	 * @param key
	 * @return value for key
	 */
	@Override

	public final Object get(final String key) {
	    JsonNode n = jsonNode.get(key);
	    if (n == null)
		return null;
	    if (n.isTextual())
		return n.asText();
	    return n;
	}


	/**
	 * Returns the variable to be used in the Velocity Template Engine to
	 * map to this object.
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

	// findValues returns a List of items that match the key.  However,
	// we want a list of children of that key.  Weirdly, there does not
	// seem to be a JsonNode function that returns a List of all children,
	// so we just build one from the Iterator.
	// This function shouldn't be needed but some macros might expect it.
	// Note that you lose tags (makes it an array implicitly) so it is
	// really not that useful.
	@Override
	@SuppressWarnings("rawtypes")
	public final List getList(final String key) throws TemplateException {
	    if (debug)  System.out.printf("JsonLabel.getList(%s) \n", key);
	    JsonNode n = jsonNode.findValue(key);
	    Iterator<JsonNode> iter = n.elements();
	    List<JsonNode> list = new ArrayList<JsonNode>();
	    while (iter.hasNext()) {
		list.add(iter.next());
	    }
	    return list;
	}


	// Units not supported on JSON.  TBD: should we support something
	// like x__UNIT does on VICAR in order to support units?  The
	// Labelocity templates do this but we could support it more
	// generally here...
	@Override
	public final String getUnits(final String key) {
	      return null;
	}

	// No-op
	@Override
	public final void setParameters(PDSObject pdsObject) {
	    Debugger.debug("JsonLabel.setParameters this.filePath = "+this.filePath+" ");
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

                if (!this.jsonFilename.isEmpty()) {

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
		}

		// Fall through from above for file, or directly for preset str

        	if (!this.jsonString.isEmpty()) {
		    if (debug) System.out.println("jsonString = "+this.jsonString);
		    parseJson(this.jsonString);
        	}
	    }
	    
	}


	@Override
	public final String toString() {
	    return jsonNode.toString();
	}

	public void setJsonFilename(String s) {
	    this.jsonFilename = s;
	}

	public void setJsonString(String s) {
	    this.jsonString = s;
	}

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
	// I doubt this works or is even needed
	public void setIncludePaths(List<String> i) {
	    this.includePaths = new ArrayList<String> (i);
	    while(this.includePaths.remove(""));
	}
	  
	/**
	 * parseJson
	 * Parse a json String and save the parsed tree for use.
	 * Originally, the tree was partially transferred to a list
	 * within this object, to better mimic what VICAR/ODL labels do.
	 * However, this led to a lot of problems and ended up negating
	 * much of the functionality of JSON.  So now, we just save the
	 * tree and let the VTL automagically traverse it to find nodes.
	 * Much simpler this way, and everything Just Works.  Except that
	 * the final result strings are quoted, but that's fixed in
	 * get() here as well as the NoQuoteUberspector class.
	 *
	 * @param jsonString
	 */

	public void parseJson(String jsonString) {

	    if (debug) System.out.printf("######################### parseJson ######################################\n");
	    if (jsonString.isEmpty()) {
	 	if (debug) System.out.printf("parseJson jsonString is empty");
		return;
	    }

	    // create an ObjectMapper instance.
	    ObjectMapper mapper = new ObjectMapper();

	    // use the ObjectMapper to read the json string and create a tree
	    jsonNode = null;
	    try {
		jsonNode = mapper.readTree(jsonString);
		if (debug) {
		    System.out.println("Parsed tree: "+ jsonNode.toString());
		}

	    } catch (IOException e ) {
		// This initially was in an if (debug) but it seems that the
		// user should be informed about this kind of error regardless.
		System.out.println("IOException reading jsonString "+e);
		e.printStackTrace();
	    }
	}
}
