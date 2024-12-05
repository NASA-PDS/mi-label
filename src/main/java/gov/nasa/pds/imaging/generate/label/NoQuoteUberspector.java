package gov.nasa.pds.imaging.generate.label;

import java.lang.reflect.Method;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.JsonNode;

/** The purpose of this class is to dequote values retrieved from 
  * a JSON tree.  For reasons unknown (that make no sense), TextNode
  * includes the quotes for toString() (folks: quotes are for representing
  * it IN text, NOT part of the value!!!).  But asText() does not return
  * the quotes.  So all this is to simply intercept calls to TextNode's
  * to return the unquoted string.  Note that we don't actually directly
  * see the TextNode but rather the enclosing node which is indexed at
  * the very end.
  */

public class NoQuoteUberspector extends UberspectImpl {

    /** Install a special getter for the class we care about */

    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i)
        				throws Exception  {

        if (obj == null) return null;

	//DEBUG:  System.out.println("UBERSPECTOR getPropertyGet");
	//DEBUG:  System.out.println("obj class = " + obj.getClass().getName().toString());
	//DEBUG:  System.out.println("info = " + i.toString());
	//DEBUG:  System.out.println("identifier = " + identifier);
	//DEBUG:  System.out.println("obj = " +obj.toString());

	// If it's not a ContainerNode class, let the default handle it

	if (! (obj instanceof ContainerNode))
	    return null;

        return new NoQuoteVelPropertyGetImpl(obj, identifier);
    }

    /** Intercept get(n) calls to array */

    public VelMethod getMethod(Object obj, String method, Object[] args, Info i)
					throws Exception {
	if (obj == null)
	    return null;
	if (! (obj instanceof ArrayNode))
	    return null;
	if (args.length != 1)
	    return null;
	if (! method.equals("get"))
	    return null;
	if (! (args[0] instanceof Integer))
	    return null;

	return new NoQuoteVelMethodGetImpl(obj);
    }

    /** The infrastructure wants a class implementing an interface, I guess
      * to make it easier to cache?
      */

    protected class NoQuoteVelPropertyGetImpl implements VelPropertyGet {
        Object _obj;
        String _identifier;

        public NoQuoteVelPropertyGetImpl(Object obj, String identifier) {
            _obj = obj;
	    _identifier = identifier;
        }

	/** Does the actual work.  I am honestly not sure of the difference
	 * between the passed-in o and saved _obj - they seem to be the same.
	 */

        public Object invoke(Object o) {

	    if (o == null) return null;
	    if (_obj == null) return null;

	    // This shouldn't happen because of the type check in the main
	    // class above... but just in case.

	    if (! (_obj instanceof ContainerNode))
		return null;

	    ContainerNode cn = (ContainerNode) _obj;

	    // Get the sub-node based on the identifier

	    JsonNode jn = cn.get(_identifier);

	    if (jn == null) {
		return null;
	    }

	    // If it's a text mode, use asText - the whole point of this

	    if (jn.isTextual()) {
		return jn.asText();
	    }
	    return jn;
        }

        public String getMethodName()
        {
	    return "dummyname";		// just to satisfy the interface
					// we don't actually use Method calls
        }

        public boolean isCacheable() { return true; }

    }

    /** The infrastructure wants a class implementing an interface, I guess
      * to make it easier to cache?
      */

    protected class NoQuoteVelMethodGetImpl implements VelMethod {
        Object _obj;

        public NoQuoteVelMethodGetImpl(Object obj) {
            _obj = obj;
        }

	/** Does the actual work.  I am honestly not sure of the difference
	 * between the passed-in o and saved _obj - but in this case o worked
	 * but obj did not.
	 */

        public Object invoke(Object o, Object[] params) {

	    if (o == null) return null;
	    if (_obj == null) return null;
	    if (params == null) return null;
	    if (params.length != 1) return null;


	    // This shouldn't happen because of the type check in the main
	    // class above... but just in case.

	    if (! (_obj instanceof ContainerNode))
		return null;
	    if (! (params[0] instanceof Integer))
		return null;

	    ContainerNode cn = (ContainerNode) o;

	    // Get the sub-node based on the identifier

	    JsonNode jn = cn.get((int)params[0]);

	    if (jn == null) {
		return null;
	    }

	    // If it's a text mode, use asText - the whole point of this

	    if (jn.isTextual()) {
		return jn.asText();
	    }
	    return jn;
        }

        public String getMethodName()
        {
	    return "get";		// just to satisfy the interface
					// we don't actually use Method calls
	}

        public boolean isCacheable() { return true; }

	public Method getMethod()
	{
	    return null;		// Hoping this is not used...
	}

	public Class getReturnType()
	{
	    Object o = new Object();
	    return o.getClass();
	}
    }
}

