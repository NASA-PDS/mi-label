package gov.nasa.pds.imaging.generation;

import gov.nasa.pds.imaging.generation.context.ContextMappings;
import gov.nasa.pds.imaging.generation.label.PDSObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
    
    private Map<String,Map> mapped = null;
    
    private VelocityContext context;
    private String templatePath;
    private File templateFile;
    private Template template;
    private File outputFile;
    private ContextMappings ctxtMappings;
    
    public Generator(PDSObject pdsObject, File templateFile, String filePath, String confPath, File outputFile) throws Exception {
		this.context = null;
		this.templateFile = templateFile;
		this.pdsObject = pdsObject;
		this.filePath = filePath;
		this.outputFile = outputFile;
		this.ctxtMappings = new ContextMappings(pdsObject.getFilePath(), confPath);
    	
    	initTemplate();
    	setContext();
    }
    
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

    private void initTemplate() throws TemplateException, IOException {
    	String filename = this.templateFile.getName();
    	
    	Properties props = new Properties();
    	props.setProperty("file.resource.loader.path", 								// Need to add base path for resource loader
    			this.templateFile.getAbsolutePath().replace("/" + filename, ""));	// Remove filename from basepath
    	Velocity.init(props);	// Add the properties to the velocity initialization
    	
    	this.context = new VelocityContext();

    	try {
    	   this.template = Velocity.getTemplate(filename);
    	} catch( ResourceNotFoundException e ) {
    	   throw new TemplateException("Template not found - " + this.templatePath);
    	} catch( ParseErrorException e ) {
    		throw new TemplateException("Error parsing the template at "+ e.getLineNumber() + ":" + e.getColumnNumber() + ". " + e.getMessage());
    	} catch( MethodInvocationException e ) {
    		throw new TemplateException("Error in template syntax "+ e.getLineNumber() + ":" + e.getColumnNumber() + ". " + e.getMessage());
    	} /*catch( Exception e ) {
    		e.printStackTrace();
    	}   */ 	
    }
    
    public void generate(boolean toFile) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException, TransformerException, SAXException, ParserConfigurationException {
    	StringWriter sw = new StringWriter();
    	PrintWriter out = null;
    	try {
	    	//this.template.merge( this.context, clean(sw) );
    		this.template.merge(this.context, sw);
    		
	    	if (toFile) {
	    		System.out.println(clean(sw));
	    	} else {
	    		out = new PrintWriter(this.outputFile);
	    		out.write(clean(sw));
	    		
	    	}
    	} finally {
    		sw.close();
    		try {
    			out.close();
    		} catch (NullPointerException e) { }
    	}
    }
    
    private void setContext() throws TemplateException, Exception {
    	this.context.put(this.pdsObject.getContext(), this.pdsObject);	// Set context for base initial PDSObject
    	//this.context.put(GeneratedObjects.CONTEXT, new GeneratedObjects());
    	for (String str : this.ctxtMappings.contextMap.keySet()) {	// Set default contexts
    		this.context.put(str, this.ctxtMappings.contextMap.get(str));
    	}
    }
    
    private String clean (StringWriter sw) throws TransformerException, ParserConfigurationException, SAXException, IOException {
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    Document doc = builder.parse(new ByteArrayInputStream(sw.toString().getBytes()));
	    
        TransformerFactory tFactory = TransformerFactory.newInstance();

        //URL url = Generator.class.getResource("/Users/jpadams/dev/workspace/transform-workspace/generation-tool/src/main/resources/cleanup.xsl");
        //InputStream in = url.openStream();
        Transformer transformer = tFactory.newTransformer (new StreamSource(Generator.class.getResourceAsStream(CLEAN_XSLT)));
        //in.close();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        doc.normalize();
        StringWriter out = new StringWriter();
        transformer.transform (new DOMSource(doc), 
        		new StreamResult (out));
        
        return out.toString();
    }
    
    public ContextMappings getContextMappings() {
    	return ctxtMappings;
    }
    
    public void setContextMappings(ContextMappings ctxMappings) {
    	this.ctxtMappings = ctxMappings;
    }
	
    public PDSObject getPdsObject() {
		return pdsObject;
	}

	public void setPDSObject(PDSObject pdsObject) {
		this.pdsObject = pdsObject;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public File getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
}
