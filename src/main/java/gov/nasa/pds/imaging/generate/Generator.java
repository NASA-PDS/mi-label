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

package gov.nasa.pds.imaging.generate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.ToolManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import gov.nasa.pds.imaging.generate.automatic.elements.ExistTemplate;
import gov.nasa.pds.imaging.generate.context.ContextMappings;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

public class Generator {

    private Map<String, PDSObject> pdsObjects ;
    private Template template;
    private VelocityContext context;
    private ContextMappings ctxtMappings;

    private File templateFile;
    private File outputFile;
    // used to set the include path to the directory of the input file
    private File inputFile;

    // eventually we could also pass this in as an InputStream (derived using webdav from a URL)
    private String templatePath;
    private String inputFilePath;
    private VelocityEngine ve;

    private boolean removedNode = false;

    /** Flag to specify whether or not the output is XML. defaults to true. **/
    private boolean isXML = true;
    
    // boolean noContext = true;
    boolean noContext = false;

    private OutputStream outputStream;
    // ImageOutputStream, RandomAccessFile, OutputStream, FileOutputStream
    // this allows the OutputStream to be passed in from the caller (jConvertIIO)
    // Then all writers work in a consistent way. Also allows writing to a URL (webdav)

    public Generator() throws TemplateException, IOException {
        this.context = null;
        this.templatePath = "";
        this.inputFilePath = "";
        this.templateFile = null;
        this.template = null;
        this.pdsObjects = new TreeMap<>();
        this.outputFile = null;
        this.inputFile = null;

        System.getProperties().setProperty(
                "javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

        this.ctxtMappings = new ContextMappings();
    }

    /**
     * Initialize Generator object with a PDSObject. Defaults as XML output.
     * 
     * @param pdsObject
     * @param templateFile
     * @param outputFile
     * @throws Exception
     */
    public Generator(final PDSObject pdsObject, final File templateFile,
            final File outputFile) throws Exception {
        this(pdsObject, templateFile, outputFile, null, true);
    }
    
    /**
     * Initialize Generator object with a PDSObject. Defaults as XML output.
     * 
     * @param pdsObject
     * @param templateFile
     * @param outputFile
     * @param inputFile
     * @throws Exception
     */
    public Generator(final PDSObject pdsObject, final File templateFile,
            final File outputFile, final File inputFile) throws Exception {
        this(pdsObject, templateFile, outputFile, inputFile, true);
    }
    
    /**
     * Initialize Generator object with a PDSObject. Defaults as XML output.
     * 
     * @param pdsObject
     * @param templateFile
     * @param outputFile
     * @param isXML 		flag to specify whether or not the output is expected to be XML
     * @throws Exception
     */
    public Generator(final PDSObject pdsObject, final File templateFile,
            final File outputFile, final Boolean isXML) throws Exception {
        this(pdsObject, templateFile, outputFile, null, isXML);
    }

    /**
     * Generator constructor class.
     *
     * 
     * @param pdsObject		pds object, i.e. PDS3 label
     * @param templateFile	velocity template file path
     * @param outputFile	path to output file. can be null in cases where we output to streams
     * @param inputFile	    path to input file. can be null in cases where we don't care
     * @param isXML 		flag to specify whether or not the output is expected to be XML
     * @throws Exception
     */	
    @SuppressWarnings("deprecation")
    public Generator(final PDSObject pdsObject, final File templateFile,
    	             final File outputFile, final File inputFile, final Boolean isXML) throws Exception {
        this.context = null;
        this.templateFile = templateFile;
        Map<String, PDSObject> pdsObjects = new TreeMap<>();
        this.pdsObjects = pdsObjects;
        this.pdsObjects.put("label", pdsObject);
        // this.pdsObjects = pdsObjects;
        this.outputFile = outputFile;
        this.inputFile = inputFile;
        this.isXML = isXML;
        
        Debugger.debug("Generator.java : Generator(final PDSObject pdsObject, final File templateFile, final File outputFile, final File inputFile, final Boolean isXML)  ");
        if (this.outputFile != null)
            FileUtils.forceMkdir(this.outputFile.getParentFile());
        
        if (this.inputFile != null)
            this.inputFilePath = this.inputFile.getParent();  //  this.inputFile.getParentFile();


        System.getProperties().setProperty(
                "javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        
        this.ctxtMappings = new ContextMappings(pdsObject);

        initTemplate();
        setContext();
    }

    /**
     * Method to use to remove all empty tags and whitespace from
     * the generated XML
     *
     * @param sw
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private String clean(final StringWriter sw) {
        if (this.isXML) {
            try {
                DocumentBuilderFactory dbFactory;
                DocumentBuilder dBuilder;
                Document document = null;

                dbFactory = DocumentBuilderFactory.newInstance();
                dBuilder = dbFactory.newDocumentBuilder();
                document = dBuilder.parse(new ByteArrayInputStream(sw.toString().getBytes()));

                String outputUnclean = document.toString();
                Debugger.debug("this.outputFile = "+this.outputFile);

                Debugger.debug("outputUnclean ="+outputUnclean+"<END>");
                if (Debugger.debugFlag) {
                    PrintWriter cout = new PrintWriter(this.outputFile+"_doc.xml");
                    cout.write(outputUnclean);
                    cout.close();
                }

                this.formatDocument(document);

                // Set all the transformer stuff to pretty print it
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                StringWriter out = new StringWriter();
                StreamResult streamResult = new StreamResult(out);

                // add the comment node    
                document.insertBefore(document.createComment(" "), document.getDocumentElement());
                transformer.transform(new DOMSource(document), streamResult);

                Debugger.debug(out.toString());

                // cleanup the top of the XML by adding in some newlines
                String outputXmlString = out.toString()
                        .replaceFirst("<!-- -->", "\n\n")
                        .replaceAll("<\\?xml-model", "\n<?xml-model")
                        .replaceAll("xmlns:", "\n    xmlns:")
                        .replaceAll("xsi:schemaLocation", "\n    xsi:schemaLocation");

                return outputXmlString;
            } catch (SAXParseException e) {
                System.err.println("\n\nError applying XSLT to output XML.  Verify label and template are correctly formatted.");
                System.err.println(e.getMessage());
                System.err.println("Outputting without formatting. \n\n");
            } catch (Exception e) {
                System.err.println("Error attempting to format XML. Malformed XML expected.");
            }
            return sw.toString();
        } else {
            return sw.toString();
        }
    }

    /**
     * format the XML by normalizing the spacing and recursively visiting each node a remove if empty.
     * will repeat the method if any nodes (or attributes) were removed in case that now leaves another node (or class)
     * empty .
     * 
     * @param document
     * @throws Exception
     */
    private void formatDocument(Document document) throws Exception{
        this.removedNode = false;

        handleNode(document.getDocumentElement());

        // normalize new lines
        document.getDocumentElement().normalize();

        // need to run an Xpath to clean up spaces. the SAX pretty print allows for spaces if you put them in there
        // so we need to use XPath to clean them up first
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            node.getParentNode().removeChild(node);
        }

        // if we removed something let's run through the cleanup again
        if (this.removedNode) {
            this.formatDocument(document);
        }
    }

    /**
     * method to recursively check a node and it's children for those that have no attributes and are empty
     * @param node
     * @param stop
     */
    private void handleNode(Node node) {  
        // first lets check we have no child nodes (text or other)
        if (node.getChildNodes().getLength() == 0) {
            // next check if the value is null or empty string
            if (node.getNodeValue().isEmpty() || node.getNodeValue() == null) {
                // finally make sure it does not have xsi:nil attribute
                if (node.getAttributes().getLength() == 0 || node.getAttributes().getNamedItem("xsi:nil") == null) {
                    node.getParentNode().removeChild(node);
                    this.removedNode = true;
                    return;
                }
            }
        }

        // recurse the children
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            handleNode(node.getChildNodes().item(i));
        }
    }

    /**
     * Functionality to generate the PDS4 Label from the Velocity Template
     * 
     * @param ImageOutputStream - ImageOutputStream write to
     * ImageOutputStream and OutputStream are NOT related so we must use one or the other
     * no casting allowed
     * 
     * @throws Exception - when output file does not exist, or error close String writer
     * @throws TemplateException - when output is null - reason needs to be found 
     * * may be able to merge the 2 generate()s together since we actually write to a PrintWriter
     * which can wrap a File or OutputStream
     */
    public void generate(ImageOutputStream ios) throws Exception {
        final StringWriter sw = new StringWriter();

        
        // Some debugging code
        Debugger.debug("generate ImageOutputStream");
        Debugger.debug("ios "+ios);

        // loop thru all the pdsObjects in the map
        for (Map.Entry<String, PDSObject> entry : pdsObjects.entrySet()) {
            PDSObject pdsObject = entry.getValue();
            String key = entry.getKey();
            String context_label = pdsObject.getContext();
            Debugger.debug("this.pdsObject.toString() "+key+" "+context_label+" " );
            
                 
            // this is the flattened PDS3Label used by the velocity template
            if (Debugger.debugFlag) {
            	String pdsObjFlat = pdsObject.toString();
            	Debugger.debug(pdsObjFlat);  
            	String fname = context_label+"_flatten.txt" ;
                PrintWriter cout = new PrintWriter(fname);
                cout.write(pdsObjFlat);
                cout.close();
                System.out.println("pdsObject "+key+" "+context_label+" "+fname);
            }
            // End debugging code   		
       }
       

       

        try {
            Debugger.debug("this.context before merge");
            Debugger.debug(this.context.toString());

            // Check if context has been set
            if (this.context == null) {
                setContext();
            }

            // Do some velocity engine stuff
            this.template.merge(this.context, sw);

            Debugger.debug("this.context after merge");
            Debugger.debug(this.context.toString());

            String output = clean(sw);

            PrintWriter cout3 = new PrintWriter("output.xml");
            cout3.write(output);
            cout3.close();

            if (Debugger.debugFlag) {
                PrintWriter cout2 = new PrintWriter("output.xml");
                cout2.write(output);
                cout2.close();
            }
            // End debugging code

            if (output.equals("null")) {	// TODO Need to validate products prior to this step to find WHY output == null
                throw new Exception("Error generating PDS4 Label. No output found. Validate input files.");
            } else {
                ios.writeBytes(output);
                ios.flush();
            }
        } catch (ResourceNotFoundException e) {
        	System.out.println("Generator ***** ResourceNotFoundException "+ e);
        	e.printStackTrace(); 
        } finally {
            sw.close();
            try {
                // out.close();
                // ios.close();
                // it seems writers do not close the stream. It will be done later
            } catch (final NullPointerException e) {
                System.out.println("NullPointerException "+ e);
            }
        }
    }


    /**
     * Functionality to generate the PDS4 Label from the Velocity Template
     * 
     * @param OutputStream - OutputStream write to
     * 
     * @throws Exception - when output file does not exist, or error close String writer
     * @throws TemplateException - when output is null - reason needs to be found 
     * * may be able to merge the 2 generate()s together since we actually write to a PrintWriter
     * which can wrap a File or OutputStream
     */
    public void generate(OutputStream os) throws Exception {
        final StringWriter sw = new StringWriter();
        PrintWriter out = null;

        // Some debugging code
        Debugger.debug("generate OutputStream");
        Debugger.debug("os "+os);

        for (Map.Entry<String, PDSObject> entry : pdsObjects.entrySet()) {
            PDSObject pdsObject = entry.getValue();
            String key = entry.getKey();
            String context_label = pdsObject.getContext();
            Debugger.debug("this.pdsObject.toString() "+key+" "+context_label+" " );
            
                 
            // this is the flattened PDS3Label used by the velocity template
            if (Debugger.debugFlag) {
            	String pdsObjFlat = pdsObject.toString();
            	Debugger.debug(pdsObjFlat);  
            	String fname = context_label+"_flatten.txt" ;
                PrintWriter cout = new PrintWriter(fname);
                cout.write(pdsObjFlat);
                cout.close();
                System.out.println("pdsObject "+key+" "+context_label+" "+fname);
            }   		
       }
       // End debugging code

        try {
        	// Check if context has been set
            if (this.context == null) {
                setContext();
            }
            
            this.template.merge(this.context, sw);

            Debugger.debug("this.context");
            Debugger.debug(this.context.toString());

            String output = clean(sw);

            if (output.equals("null")) {	// TODO Need to validate products prior to this step to find WHY output == null
                throw new Exception("Error generating PDS4 Label. No output found. Validate input files.");
            } else {

            	Debugger.debug("New PDS4 Label:");
                out = new PrintWriter(os);
                out.write(output);
            }
        } finally {
            sw.close();
            try {
                out.close();
            } catch (final NullPointerException e) {
            }
        }
    }

    /**
     * Functionality to generate the PDS4 Label from the Velocity Template
     *
     * @param toStdOut - Determines whether the output should be to a file or System.out
     * @throws FileNotFoundException
     *
     * @throws Exception - when output file does not exist, or error close String writer
     * @throws TemplateException - when output is null - reason needs to be found
     */
    public void generate(final boolean toStdOut) throws FileNotFoundException, TemplateException {
        final StringWriter sw = new StringWriter();
        PrintWriter out = null;

        // Some debugging output
        Debugger.debug("generate toStdout " + toStdOut);

        // loop thru all the pdsObjects in the map
        for (Map.Entry<String, PDSObject> entry : pdsObjects.entrySet()) {
            PDSObject pdsObject = entry.getValue();
            String key = entry.getKey();
            String context_label = pdsObject.getContext();
            Debugger.debug("this.pdsObject.toString() "+key+" "+context_label+" " );
            
                 
            // this is the flattened PDS3Label used by the velocity template
            if (Debugger.debugFlag) {
            	String pdsObjFlat = pdsObject.toString();
            	Debugger.debug(pdsObjFlat);  
            	String fname = context_label+"_flatten.txt" ;
                PrintWriter cout = new PrintWriter(fname);
                cout.write(pdsObjFlat);
                cout.close();
                System.out.println("pdsObject "+key+" "+context_label+" "+fname);
            }
            // End debugging code   		
       }
        
        try {
            // Check if context has been set
            if (this.context == null) {
                setContext();
            }

            // Do some velocity engine stuff
            this.template.merge(this.context, sw);

            Debugger.debug("this.context");
            Debugger.debug(this.context.toString());

            String output = clean(sw);

            // TODO Need to validate products prior to this step to find WHY output == null
            if (output == "null") {
              throw new TemplateException(
                  "Unknown error generating PDS4 Label. No output found. Verify input files are valid.");
            } else {
                if (toStdOut) {
                    System.out.println(output);
                } else if (this.outputStream != null) {
                    // FIXME Need to implement this here
                } else {
                	Debugger.debug("New PDS4 Label: " + this.outputFile.getAbsolutePath());

                    out = new PrintWriter(this.outputFile);
                    out.write(output);

                }
            }
        } finally {
            try {
              sw.close();
            } catch (IOException e) {
              e.printStackTrace();
            }

            if (out != null) {
                out.close();
              }
        }
    }

    /**
     * Initializes and reads in the Velocity Template into the Velocity
     * Engine.
     *
     * @throws TemplateException
     * @throws IOException
     */
    public void initTemplate() throws TemplateException {
        final String filename = this.templateFile.getName();

        Debugger.debug("Generator.initTemplate()");
        // VelocityEngine ve = new VelocityEngine();
        Debugger.debug(String.format("Generator.initTemplate() %s, this.inputFilePath = %s \n", this.templateFile.getParent(), this.inputFilePath ));
        this.ve = new VelocityEngine();
        if (this.inputFilePath != null && !this.inputFilePath.isEmpty()) {
        	// add the directory of the input file to the path, check if a resource can be loaded from a full path
        	/// that would be supplied by the extra file
        	String paths = String.format("%s, %s", this.templateFile.getParent(), this.inputFilePath);
        	Debugger.debug(String.format("paths = %s \n", paths));
        	ve.setProperty("file.resource.loader.path", // Need to add base path
                    // for resource loader
                    paths);       	
        } else {
        	ve.setProperty("file.resource.loader.path", // Need to add base path
                // for resource loader
                this.templateFile.getParent());
        }
        ve.setProperty("file.resource.loader.cache",
                "false");
        
        ve.init();

        this.context = new VelocityContext();
        
        

        try {
            this.template = ve.getTemplate(filename);
        } catch (final ResourceNotFoundException e) {
            throw new TemplateException("Template not found - "
                    + this.templatePath);
        } catch (final ParseErrorException e) {
            throw new TemplateException("Error parsing the template at "
                    + e.getLineNumber() + ":" + e.getColumnNumber() + ". "
                    + e.getMessage());
        } catch (final MethodInvocationException e) {
            throw new TemplateException("Error in template syntax "
                    + e.getLineNumber() + ":" + e.getColumnNumber() + ". "
                    + e.getMessage());
        }
        
       
        
    }
    
    
    /**
     * 
     * addPdsObject
     * 
     * add an additional PDSObject
     * The contextStr is the variable prefix used in a velocity template
     * * $extra.SOMETHING if contextStr is "extra"
     * The pdsObject.getContext() value should be this same String
     * * it is used by getPdsObject
     * 
     * @param extraLabel
     * @param contextStr
     * @throws TemplateException
     * @throws Exception
     */
    public void addPdsObject(PDSObject pdsObject, String contextStr) throws TemplateException, Exception {
    	
    	Debugger.debug("Generator.addPdsObject() contextStr="+contextStr+" ");
    	
    	
    	if (pdsObject != null) {
    		String context_label = pdsObject.getContext();
    		Debugger.debug("addPdsObject "+contextStr+"  "+context_label+" ");
    		this.pdsObjects.put(contextStr, pdsObject);
    		if (noContext == true) {
    			Debugger.debug("addPdsObject  noContext is true. Not adding "+contextStr+" ");
    		} else {
    			this.context.put(contextStr, pdsObject);
    		}
        	
        }
    	
    	if (Debugger.debugFlag) {
	    	// show what has been set
	        Object[] keys = this.context.getKeys();
	        System.out.println("Generator.setContext() keys"+keys);
	        // print each key 
	        for (int i = 0; i < keys.length; i++)
	        	{
	        	System.out.printf("%d) key %s %s \n", i, keys[i].getClass().toString(), keys[i].toString());
	        	}	
    	}
    }
    
    public void setPDSObject(PDSObject pdsObject) throws TemplateException {
    	// could also call addPdsObject. Then loop thru them all?
    	if (pdsObject != null) {
    		String contextLabel = pdsObject.getContext();
            Debugger.debug("Generator.setContext()  " + contextLabel + " ");
    		
    		this.pdsObjects.put(contextLabel, pdsObject);
    		if (noContext == true) {
    			Debugger.debug("addPdsObject  noContext is true. Not adding ");
    		} else {
              // Check if context has been set
              if (this.context == null) {
                setContext();
              }
              this.context.put(contextLabel, pdsObject);
    		}
        }
    }
    
    
    public PDSObject getPdsObject() {
    	return getPdsObject(null);
    }
    
    /**
     * getPdsObject
     * 
     * @return
     */
    public PDSObject getPdsObject(String context_label) {
    	PDSObject pdsObject = null;
    	if (context_label == null) {
    		pdsObject = pdsObjects.get("label"); // default label
    	} else {
    		pdsObject = pdsObjects.get(context_label);
    	}
        return pdsObject;
    }

    /**
     * Sets the Velocity Template contexts specified by the PDSObject API and
     * ContextMappings object.
     *
     * @throws TemplateException
     * @throws Exception
     */
    public void setContext() throws TemplateException {
        addToolManager();
        
        if (noContext == true) {
        	Debugger.debug("setContext() not setting context ");
        	return;
        }
        

        Debugger.debug("Generator.setContext()");
        // Set default contexts
        for (final String str : this.ctxtMappings.contextMap.keySet()) {
            this.context.put(str, this.ctxtMappings.contextMap.get(str));
        }

        for (Map.Entry<String, PDSObject> entry : pdsObjects.entrySet()) {
            PDSObject pdsObject = entry.getValue();
            String key = entry.getKey();
            String context_label = pdsObject.getContext();
            Debugger.debug("this.pdsObject.toString() "+key+" "+context_label+" " );
            // System.out.println("setContext pdsObject "+key+" "+context_label+" " );
            this.context.put(context_label, pdsObject);
            // check if a key already exists? Will a duplicate make 2 of the same things 
            // or will it replace one with the same label??
        }
        
        // add a function to check if a parse file exists
        // this function can be passed an argument. The methods set by generate-mappings cannot
        ExistTemplate incl = new ExistTemplate(this.ve);
        this.context.put("incl",  incl);
        Debugger.debug(this.context.toString());
        
        if (Debugger.debugFlag) {
	        // show what has been set
	        Object[] keys = this.context.getKeys();
	        System.out.println("Generator.setContext() keys"+keys);
	        // print each key 
	        for (int i = 0; i < keys.length; i++)
	        {
	        	System.out.printf("%d) key %s %s \n", i, keys[i].getClass().toString(), keys[i].toString());
	        }
        }
        EventCartridge ec = this.context.getEventCartridge();
        // use this to add the event handler
    }

    private void addToolManager() {
        ToolManager velocityToolManager = new ToolManager();
        velocityToolManager.configure("gov/nasa/pds/imaging/generate/velocity-tools.xml");
        this.context = new VelocityContext(velocityToolManager.createContext());
    }

    // **************************************************************//
    // Getter/Setter methods for local variables. Included for API //
    // to allow external software to plug directly into Generator //
    // object. //
    // **************************************************************//

    public VelocityContext getContext() {
        return context;
    }

    public ContextMappings getContextMappings() {
        return ctxtMappings;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public Template getTemplate() {
        return template;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public boolean getIsXML() {
        return isXML;
    }


    public void setContext(final VelocityContext context) {
        this.context = context;
    }

    public void setContextMappings(final ContextMappings ctxMappings) {
        this.ctxtMappings = ctxMappings;
    }

    public void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }

    public void setTemplate(final Template template) {
        this.template = template;
    }

    public void setTemplateFile(final File templateFile) throws TemplateException {
        this.templateFile = templateFile;
        initTemplate();
    }

    public void setTemplatePath(final String templatePath) {
        this.templatePath = templatePath;
    }
    
    public void setInputFilePath(final String inputFilePath) {
    	
        this.inputFilePath = inputFilePath;
        Debugger.debug("Generator.setInputFilePath this.inputFilePath = "+this.inputFilePath);
    }

    public void setIsXML(final boolean isXML) {
        this.isXML = isXML;
    }

}
