package gov.nasa.pds.imaging.generate.context;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.util.XMLUtil;

import java.util.HashMap;

public class ContextMappings {

    public static final String XML_FILENAME = "context-classes.xml";

    /** XML element name holding the key value **/
    public static final String XML_TAG = "class";

    public HashMap<String, PDSContext> contextMap = new HashMap<String, PDSContext>();

    public ContextMappings() throws TemplateException, Exception {
        for (final String cl : XMLUtil.getClassList(XML_FILENAME, XML_TAG)) {
            final PDSContext context = (PDSContext) Class.forName(cl).newInstance();
            this.contextMap.put(context.getContext(), context);
        }
    }

    /**
     * Populates the contextMap with those classes specified in the context
     * mappings XML file.
     * 
     * @throws TemplateException
     * @throws Exception
     */
    public ContextMappings(final String filePath, final String confPath)
            throws TemplateException, Exception {
        for (final String cl : XMLUtil.getClassList(confPath + "/" + XML_FILENAME,
                XML_TAG)) {
            final PDSContext context = (PDSContext) Class.forName(cl).newInstance();
            context.setInputPath(filePath);
            context.setConfigPath(confPath);
            context.setMappings();
            this.contextMap.put(context.getContext(), context);
        }
    }

    public void addMapping(final String key, final PDSContext value) {
        this.contextMap.put(key, value);
    }

}
