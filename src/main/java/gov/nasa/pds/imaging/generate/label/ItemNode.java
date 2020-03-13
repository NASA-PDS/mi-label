// Copyright 2020, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.imaging.generate.label;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemNode extends ArrayList<String>{

    private String name;
    public String units;

    public ItemNode(final String name, final String units) {
        super();
        this.name = name;
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
            return super.get(0);
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
    public String get(int index) {
    	if (this.size() == 0) {
    		return "";
    	}
    	
    	return super.get(index);
    }
    
    @Override
    public String toString() {
        String out = "";
        if (name.startsWith("PTR_")) {
          if (this.size() == 1) {
            out = this.get(0);
          } else {
            out = "(";
            for (String value : this) {
                out += value + ",";
            }
            out = out.substring(0, out.length()-1);
            if (!units.equals("none")) {
              out += " " + units;
            }
            out = out + ")";
          }
        } else {
          if (this.size() == 1) {
              out = this.get(0);
          } else {
              out = "(";
              for (String value : this) {
                  out += value + ",";
              }
              out = out.substring(0, out.length()-1) + ")";
          }
        }
        return out;
    }

}
