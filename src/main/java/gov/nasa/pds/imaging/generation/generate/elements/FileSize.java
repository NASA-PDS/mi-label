package gov.nasa.pds.imaging.generation.generate.elements;

import java.io.File;

public class FileSize implements Element {

	private File file;
	
	public FileSize() { }
	
	public String getValue() {
		return String.valueOf(this.file.length());
	}
	
	public String getUnits() {
		return null;
	}
	
	public void setParameters(String filePath) {
		System.out.println("FileSize-filePath " + filePath);
		this.file = new File(filePath);
	}
	
}
