package gov.nasa.pds.imaging.generation.generate.elements;

import java.io.File;

public class FileName implements Element {

	private File file;
	
	@Override
	public void setParameters(String filePath) {
		this.file = new File(filePath);
	}

	@Override
	public String getValue() {
		return file.getName();
	}

	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return null;
	}

}
