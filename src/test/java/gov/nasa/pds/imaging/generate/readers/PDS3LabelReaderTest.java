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

package gov.nasa.pds.imaging.generate.readers;

import static org.junit.Assert.fail;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import gov.nasa.pds.imaging.generate.constants.TestConstants;
import gov.nasa.pds.imaging.generate.test.GenerateTest;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.Utility;
import jpl.mipl.io.plugins.PDSLabelToDOM;

@RunWith(JUnit4.class)
public class PDS3LabelReaderTest extends GenerateTest {

  @Rule
  public SingleTestRule test = new SingleTestRule("");

  @Test
  public void testGetSimple() {
    try {
      Debugger.debugFlag = true;
      String filePath =
          Utility.getAbsolutePath(TestConstants.TEST_DATA_DIR + "/PDS-259/gen_ELE_MOM.LBL");
      final BufferedReader input = new BufferedReader(new FileReader(filePath));
      // TODO - what is the purpose of this
      // in PDSLabelToDOM
      final PrintWriter output = new PrintWriter(System.out);

      final PDSLabelToDOM pdsToDOM = new PDSLabelToDOM(input, output);
      pdsToDOM.setDebug(true);
      // pdsToDOM.buildDocument();

      pdsToDOM.getDocument();

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception thrown.");
    }
  }

}
