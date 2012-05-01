package gov.nasa.pds.imaging.generate;

import gov.nasa.pds.imaging.generate.context.ContextMappings;
import gov.nasa.pds.imaging.generate.label.PDSObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Generator {

    private static final String CLEAN_XSLT = "cleanup.xsl";

    private PDSObject pdsObject = null;

    private String filePath = null;

    private final Map<String, Map> mapped = null;

    private VelocityContext context;
    private String templatePath;
    private File templateFile;
    private Template template;
    private File outputFile;
    private ContextMappings ctxtMappings;

    public Generator() throws Exception {
        this.context = null;
        this.templatePath = "";
        this.templateFile = null;
        this.template = null;
        this.pdsObject = null;
        this.filePath = null;
        this.outputFile = null;

        this.ctxtMappings = new ContextMappings();
    }

    public Generator(final PDSObject pdsObject, final File templateFile, final String filePath,
            final String confPath, final File outputFile) throws Exception {
        this.context = null;
        this.templateFile = templateFile;
        this.pdsObject = pdsObject;
        this.filePath = filePath;
        this.outputFile = outputFile;
        this.ctxtMappings = new ContextMappings(this.pdsObject,
                confPath);
        
        initTemplate();
        setContext();
    }

    /**
     * Method to use XSLT in order to remove all empty tags and whitespace from
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
    	try {
	        final DocumentBuilderFactory domFactory = DocumentBuilderFactory
	                .newInstance();
	        final DocumentBuilder builder = domFactory.newDocumentBuilder();
	        final Document doc = builder.parse(new ByteArrayInputStream(sw.toString()
	                .getBytes()));
	
	        final TransformerFactory tFactory = TransformerFactory.newInstance();
	        final Transformer transformer = tFactory.newTransformer(new StreamSource(
	                Generator.class.getResourceAsStream(CLEAN_XSLT)));
	        
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        doc.normalize();
	        final StringWriter out = new StringWriter();
	        transformer.transform(new DOMSource(doc), new StreamResult(out));

	        return out.toString();
    	} catch (Exception e) {
    		System.err.println("Error applying XSLT to output XML.  Verify label and template are correctly formatted.");
    	}
    	return null;
    }

    /**
     * Functionality to generate the PDS4 Label from the Velocity Template
     * 
     * @param toStdOut - Determines whether the output should be to a file or System.out
     * 
     * @throws Exception - when output file does not exist, or error close String writer
     * @throws TemplateException - when output is null - reason needs to be found 
     */
    public void generate(final boolean toStdOut) throws Exception {
        final StringWriter sw = new StringWriter();
        PrintWriter out = null;
        try {
            this.template.merge(this.context, sw);
            
            String output = clean(sw);
            
            if (output == null) {	// TODO Need to validate products prior to this step to find WHY output == null
            	throw new Exception("Error generating PDS4 Label. No output found. Validate input files.");
            } else {
	            if (toStdOut) {
	                System.out.println(output);
	            } else {
	            	System.out.println("New PDS4 Label: " + this.outputFile.getAbsolutePath());
	                out = new PrintWriter(this.outputFile);
	                out.write(output);
	
	            }
            }
        } finally {
            sw.close();
            try {
                out.close();
            } catch (final NullPointerException e) {
            }
        }
    }

    public VelocityContext getContext() {
        return context;
    }

    public ContextMappings getContextMappings() {
        return ctxtMappings;
    }

    // **************************************************************//
    // Getter/Setter methods for local variables. Included for API //
    // to allow external software to plug directly into Generator //
    // object. //
    // **************************************************************//

    public String getFilePath() {
        return filePath;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public PDSObject getPdsObject() {
        return pdsObject;
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

    /**
     * Initializes and reads in the Velocity Template into the Velocity
     * Engine.
     * 
     * @throws TemplateException
     * @throws IOException
     */
    public void initTemplate() throws TemplateException, IOException {
        final String filename = this.templateFile.getName();

        final Properties props = new Properties();
        props.setProperty("file.resource.loader.path", // Need to add base path
                                                       // for resource loader
                this.templateFile.getParent());
        Velocity.init(props); // Add the properties to the velocity
                              // initialization

        this.context = new VelocityContext();

        try {
            this.template = Velocity.getTemplate(filename);
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
     * Sets the Velocity Template contexts specified by the PDSObject API and
     * ContextMappings object.
     * 
     * @throws TemplateException
     * @throws Exception
     */
    public void setContext() throws TemplateException, Exception {
    	// Set context for base initial PDSObject
        this.context.put(this.pdsObject.getContext(), this.pdsObject);
        
        // Set default contexts
        for (final String str : this.ctxtMappings.contextMap.keySet()) { 
            this.context.put(str, this.ctxtMappings.contextMap.get(str));
        }
    }

    public void setContext(final VelocityContext context) {
        this.context = context;
    }

    public void setContextMappings(final ContextMappings ctxMappings) {
        this.ctxtMappings = ctxMappings;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }

    public void setPDSObject(final PDSObject pdsObject) {
        this.pdsObject = pdsObject;
    }

    public void setTemplate(final Template template) {
        this.template = template;
    }

    public void setTemplateFile(final File templateFile) {
        this.templateFile = templateFile;
    }

    public void setTemplatePath(final String templatePath) {
        this.templatePath = templatePath;
    }
}
