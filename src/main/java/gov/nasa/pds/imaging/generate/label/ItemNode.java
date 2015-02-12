package gov.nasa.pds.imaging.generate.label;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemNode extends ArrayList<String>{

    private String name;
    //private final List<String> values;
    public String units;

    public ItemNode(final String name, final String units) {
        super();
        this.name = name;
        //this.values = new ArrayList<String>();
        this.units = units;
    }

    public void addValue(final String value) {
        this.add(value.trim());
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public Object getValue() {
        if (this.size() == 1) {
            return this.get(0);
        } else {
            return this;
        }
    }

    @SuppressWarnings("rawtypes")
	public List getValues() {
        return this;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setUnits(final String units) {
        this.units = units;
    }

    public void setValues(final List<String> values) {
        this.addAll(values);
    }
    
    @Override
    public String toString() {
        String out = "";
        if (this.size() == 1) {
            out = this.get(0);
        } else {
            out = "(";
            for (String value : this) {
                out += value + ",";
            }
            out = out.substring(0, out.length()-1) + ")";
        }
        return out;
    }

}
