// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.imaging.generate.automatic.elements;

import gov.nasa.pds.imaging.generate.TemplateException;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.util.ToolInfo;
import gov.nasa.pds.imaging.generate.util.Debugger;

/**
 * Class that can be used by a Velocity template to get the
 * data model version.
 * 
 * @author mcayanan
 *
 */
public class ModelVersion implements Element {

  @Override
  public String getUnits() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getValue() throws TemplateException {
	String v =  ToolInfo.getModelVersion().toString();
	if (Debugger.debugFlag) System.out.printf("modelVersion.getValue %s\n", v);
    return ToolInfo.getModelVersion().toString();
  }
  
  @Override
  public void setParameters(PDSObject pdsObject) {
    // TODO Auto-generated method stub
	  if (Debugger.debugFlag) System.out.printf("modelVersion.setParameters \n");
  }

}
