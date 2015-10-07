package gov.nasa.pds.imaging.generate;

import gov.nasa.pds.imaging.generate.context.ContextMappings;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

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
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.ToolManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.OutputStream;

public class Generator {

	private static final String CLEAN_XSLT = "cleanup.xsl";

	private PDSObject pdsObject = null;
	private Template template;
	private VelocityContext context;
	private ContextMappings ctxtMappings;
	
	private final Map<String, Map> mapped = null;

	private File templateFile;
	private File outputFile;

    // (srl) eventually we could also pass this in as an InputStream (derived using webdav from a URL)
	private String templatePath;

	private OutputStream outputStream;
	// ImageOutputStream, RandomAccessFile, OutputStream, FileOutputStream
	// this allows the OutputStream to be passed in from the caller (jConvertIIO)
	// Then all writers work in a consistent way. Also allows writing to a URL (webdav)

	public Generator() throws Exception {
		this.context = null;
		this.templatePath = "";
		this.templateFile = null;
		this.template = null;
		this.pdsObject = null;
		this.outputFile = null;

        System.getProperties().setProperty(
                "javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

		this.ctxtMappings = new ContextMappings();
	}

	public Generator(final PDSObject pdsObject, final File templateFile,
			final File outputFile) throws Exception {
        this.context = null;
        this.templateFile = templateFile;
        this.pdsObject = pdsObject;
        this.outputFile = outputFile;
        
        if (this.outputFile != null)
			FileUtils.forceMkdir(this.outputFile.getParentFile());
        
        System.getProperties().setProperty(
                "javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

        this.ctxtMappings = new ContextMappings(this.pdsObject);
               
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
			domFactory.setNamespaceAware(true);
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final Document doc = builder.parse(new ByteArrayInputStream(sw.toString()
					.getBytes()));

			// First, lets make a transformer that will output the raw
			// XML before we clean out the empty tags
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			StringWriter out = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(out));
			
			Debugger.debug("outputUnclean ="+out.toString()+"<END>");
			if (Debugger.debugFlag) {
			  PrintWriter cout = new PrintWriter(this.outputFile+"_doc.xml");
			  cout.write(out.toString());
			  cout.close();
			}
			
			// Next, let's create the transformer with the XSLT to clean out
			// extra whitespace and remove empty tags
			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer(new StreamSource(
					Generator.class.getResourceAsStream(CLEAN_XSLT)));

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			doc.normalize();

			String outputUnclean = doc.toString();
			Debugger.debug("this.outputFile = "+this.outputFile.getAbsolutePath());
			
			Debugger.debug("outputUnclean ="+outputUnclean+"<END>");
			if (Debugger.debugFlag) {
			  PrintWriter cout = new PrintWriter(this.outputFile+"_doc.xml");
			  cout.write(outputUnclean);
			  cout.close();
			}

			out = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(out));

			Debugger.debug("out.toString() =" + out.toString());

			return out.toString();
		} catch (Exception e) {	// TODO Better error handling here
			System.err.println("Error applying XSLT to output XML.  Verify label and template are correctly formatted.");
		}
		return null;
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
		String pdsObjFlat = this.pdsObject.toString();
		Debugger.debug("this.pdsObject.toString() " );
		Debugger.debug(pdsObjFlat );       
		// this is the flattened PDS3Label used by the velocity template
		if (Debugger.debugFlag) {
		  PrintWriter cout = new PrintWriter("PDS3_flatten.txt");
		  cout.write(pdsObjFlat);
		  cout.close();
		}
		// End debugging code

		try {
			// Some debugging code
			// how is context set??
			Debugger.debug("this.context before merge");
			Debugger.debug(this.context.toString());

			this.template.merge(this.context, sw);

			Debugger.debug("this.context after merge");
			Debugger.debug(this.context.toString());

			String output = clean(sw);

			PrintWriter cout2 = new PrintWriter("output.xml");
			cout2.write(output);
			cout2.close();
			// End debugging code

			if (output.equals("null")) {	// TODO Need to validate products prior to this step to find WHY output == null
				throw new Exception("Error generating PDS4 Label. No output found. Validate input files.");
			} else {	           
				ios.writeBytes(output);
				ios.flush();
			}
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
		String pdsObjFlat = this.pdsObject.toString();
		Debugger.debug("this.pdsObject.toString() "+pdsObjFlat );

		// this is the flattened PDS3Label
		if (Debugger.debugFlag) {
		  PrintWriter cout = new PrintWriter("PDS3_flatten.txt");
		  cout.write(pdsObjFlat);
		  cout.close();
		}
		// End debugging code

		try {
			this.template.merge(this.context, sw);

			Debugger.debug("this.context");
			Debugger.debug(this.context.toString());

			String output = clean(sw);

			if (output == "null") {	// TODO Need to validate products prior to this step to find WHY output == null
				throw new Exception("Error generating PDS4 Label. No output found. Validate input files.");
			} else {

				System.out.println("New PDS4 Label:");
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
	 *
	 * @throws Exception - when output file does not exist, or error close String writer
	 * @throws TemplateException - when output is null - reason needs to be found
	 */
	public void generate(final boolean toStdOut) throws Exception {
		final StringWriter sw = new StringWriter();
		PrintWriter out = null;

		// Some debugging output
		Debugger.debug("generate toStdout " + toStdOut);
		Debugger.debug("this.pdsObject.getFilePath() "+this.pdsObject.getFilePath() );
		Debugger.debug("this.outputFile "+this.outputFile.getAbsolutePath());
		String pdsObjFlat = this.pdsObject.toString();
		Debugger.debug("this.pdsObject.toString() "+pdsObjFlat );

		// this is the flattened PDS3Label
		if (Debugger.debugFlag) {
		  PrintWriter cout = new PrintWriter("PDS3_flatten.txt");

		  cout.write(pdsObjFlat);
		  cout.close();
		}
		// End debugging output

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

			if (output == "null") {	// TODO Need to validate products prior to this step to find WHY output == null
				throw new Exception("Error generating PDS4 Label. No output found. Validate input files.");
			} else {
				if (toStdOut) {
					System.out.println(output);
				} else if (this.outputStream != null) {
					// FIXME Need to implement this here
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

	/**
	 * Initializes and reads in the Velocity Template into the Velocity
	 * Engine.
	 *
	 * @throws TemplateException
	 * @throws IOException
	 */
	public void initTemplate() throws TemplateException {
		final String filename = this.templateFile.getName();

		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("file.resource.loader.path", // Need to add base path
				// for resource loader
				this.templateFile.getParent());
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
	 * Sets the Velocity Template contexts specified by the PDSObject API and
	 * ContextMappings object.
	 *
	 * @throws TemplateException
	 * @throws Exception
	 */
	public void setContext() throws TemplateException, Exception {
		addToolManager();

		// Set default contexts
		for (final String str : this.ctxtMappings.contextMap.keySet()) {
			this.context.put(str, this.ctxtMappings.contextMap.get(str));
		}

		// Set context for base initial PDSObject
		this.context.put(this.pdsObject.getContext(), this.pdsObject);
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

	public void setContext(final VelocityContext context) {
		this.context = context;
	}

	public void setContextMappings(final ContextMappings ctxMappings) {
		this.ctxtMappings = ctxMappings;
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

	public void setTemplateFile(final File templateFile) throws TemplateException {
		this.templateFile = templateFile;
		initTemplate();
	}

	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

}
