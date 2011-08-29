package gov.nasa.pds.imaging.generation.label;

import java.util.ArrayList;
import java.util.List;

public class ItemNode {

	private String name;
	private List<String> values;
	public String units;
	
	public ItemNode(String name, String units) {
		this.name = name;
		this.values = new ArrayList<String>();
		this.units = units;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.values.get(0);
	}
	
	public List getValues() {
		return this.values;
	}
	
	public void addValue(String value) {
		this.values.add(value);
	}

	public void setValues(List<String> values) {
		this.values.addAll(values);
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
	
	public String toString() {
		return this.values.get(0);
	}
	
	
}
