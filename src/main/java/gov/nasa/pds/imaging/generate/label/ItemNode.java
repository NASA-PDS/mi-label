package gov.nasa.pds.imaging.generate.label;

import java.util.ArrayList;
import java.util.List;

public class ItemNode {

    private String name;
    private final List<String> values;
    public String units;

    public ItemNode(final String name, final String units) {
        this.name = name;
        this.values = new ArrayList<String>();
        this.units = units;
    }

    public void addValue(final String value) {
        this.values.add(value);
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public String getValue() {
        return this.values.get(0);
    }

    public List getValues() {
        return this.values;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setUnits(final String units) {
        this.units = units;
    }

    public void setValues(final List<String> values) {
        this.values.addAll(values);
    }

    @Override
    public String toString() {
        return this.values.get(0);
    }

}
