package gov.nasa.pds.imaging.generation.generate;

import gov.nasa.pds.imaging.generation.TemplateException;
import gov.nasa.pds.imaging.generation.context.PDSContext;
import gov.nasa.pds.imaging.generation.generate.elements.Element;
import gov.nasa.pds.imaging.generation.util.XMLUtil;

import java.util.HashMap;
import java.util.Map;

public class GeneratedElements implements PDSContext {
	/** Specifies the CONTEXT to be mapped to the Velocity Templates.  **/
	public static final String CONTEXT = "generate";
	
	/** The XML File Path for the file with the generated value mappings **/
    public static final String XML_FILENAME = "generated-mappings.xml";
    
    /** XML element name holding the key value **/
    public static final String XML_KEY = "context";
    
    /** XML element name holding the mapped value **/
    public static final String XML_VALUE = "class";
    
    /** Map that will hole the String -> Class mappings specified in the XML **/
    public Map<String, Class<?>> genValsMap = new HashMap<String, Class<?>>();
    
    private String filePath;
    private String confPath;
	
	public GeneratedElements() { }

	public void setMappings() throws Exception {
		this.genValsMap.putAll(XMLUtil.getGeneratedMappings(this.confPath + "/" + XML_FILENAME, XML_KEY, XML_VALUE));
	}
	
    public void addMapping(String key, Class<?> value) {
    	this.genValsMap.put(key, value);
    }
	
	@Override
	public String get(String value) throws TemplateException {
		Element el;
		try {
			el = (Element) this.genValsMap.get(value).newInstance();
			el.setParameters(this.filePath);
			return el.getValue();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			return "Object Not Found";
			//throw new TemplateException("Generated value: " + value + " Not expected.  Verify class mapping exists.");
		}
		return null;
	}
	
	@Override
	public void setInputPath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String getContext() {
		return CONTEXT;
	}

	@Override
	public String getUnits(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setConfigPath(String path) {
		this.confPath = path;
	}
	
}
