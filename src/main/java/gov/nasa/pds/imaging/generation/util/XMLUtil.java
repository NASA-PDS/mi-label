package gov.nasa.pds.imaging.generation.util;

import gov.nasa.pds.imaging.generation.TemplateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Utility class for reading XML files
 * 
 * @author jpadams
 *
 */
public class XMLUtil {
	/**
	 * A static method that returns the mapping of String to Class for
	 * Generated Values found in the Velocity Template.
	 * @param file
	 * @param key
	 * @param value
	 * @return
	 * @throws TemplateException
	 * @throws Exception
	 */
	public static Map<String, Class<?>> getGeneratedMappings(String file, String key, String value) throws TemplateException, Exception {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);

	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    Document doc = builder.parse(file);

    	NodeList contexts = doc.getElementsByTagName(key);
    	NodeList classes = doc.getElementsByTagName(value);
	
    	for (int i = 0; i < contexts.getLength(); i++) {
    		map.put(contexts.item(i).getTextContent(), Class.forName(classes.item(i).getTextContent()));
    	}
    	
    	return map;
	}
	
	/**
	 * Static method that returns a list of Classes that will be extracted
	 * from the XML file for context mappings.
	 * 
	 * @param file
	 * @param tag
	 * @return
	 * @throws TemplateException
	 * @throws Exception
	 */
	public static List<String> getClassList(String file, String tag) throws TemplateException, Exception {
		List<String> classList = new ArrayList<String>();
		
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);

	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    Document doc = builder.parse(file);

    	NodeList classes = doc.getElementsByTagName(tag);
	
    	for (int i = 0; i < classes.getLength(); i++) {
    		classList.add(classes.item(i).getTextContent());
    	}
    	
    	return classList;
	}
	
    public static void main(String[] args) {
    	try {
    		XMLUtil.getGeneratedMappings("src/main/resources/conf/generated-mappings.xml", "context", "class");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }
}
