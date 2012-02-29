package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;

public class LabelLength implements Element {

	private PDSObject pdsObject;
	
	public LabelLength() {
		this.pdsObject = null;
	}
	
	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue() throws TemplateException {
		long value = 0;
		try {
			value = Long.parseLong(this.pdsObject.get("LABEL_RECORDS").toString())
			* Long.parseLong(this.pdsObject.get("RECORD_BYTES").toString());
		} catch (NumberFormatException e) {
			throw new TemplateException("Error generating LabelLength: NumberFormatException");
		} catch (TemplateException e) {
			throw new TemplateException("Error generating LabelLength: TemplateException");
		}
		return String.valueOf(value);
	}

	@Override
	public void setParameters(final PDSObject pdsObject) {
		this.pdsObject = pdsObject;
	}

}
