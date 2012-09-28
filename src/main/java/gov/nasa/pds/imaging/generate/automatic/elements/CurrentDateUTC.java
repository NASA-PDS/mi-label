package gov.nasa.pds.imaging.generate.automatic.elements;

import java.text.SimpleDateFormat;
import java.util.Date;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;

/**
 * Generates the Current Date in UTC format.
 * 
 * Deprecated by using Velocity DateTool
 * Example: $date.get("yyyy-MM-dd");
 * 
 * @author jpadams
 *
 */
@Deprecated
public class CurrentDateUTC implements Element {

	private static final String UTC_FORMAT="yyyy-MM-dd";
	
	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue() throws TemplateException {
		// TODO Auto-generated method stub
		SimpleDateFormat format = new SimpleDateFormat(UTC_FORMAT);
		Date date = new Date();
		return format.format(date);
	}

	@Override
	public void setParameters(PDSObject pdsObject) {
		// TODO Auto-generated method stub

	}

}
