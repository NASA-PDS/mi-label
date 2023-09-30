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

package gov.nasa.pds.imaging.generate.test;

import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 *
 * @author jpadams
 *
 */
@RunWith(JUnit4.class)
@Ignore
public class GenerateTest {

  /** Logger for test classes **/
  protected static Logger log = Logger.getLogger(GenerateTest.class.getName());

  /**
   * JUnit Test Rules to print headers for each test
   */
  @Rule
  public MethodRule watchman = new TestWatchman() {
    @Override
    public void starting(FrameworkMethod method) {
      log.info("------------ Testing: " + method.getName() + " ------------");
    }
  };

  /**
   * Custom rule to allow for performing one unit test at a time. Helpful when a test is failing.
   *
   * @author jpadams
   *
   */
  public class SingleTestRule implements MethodRule {
    private String applyMethod;

    public SingleTestRule(String applyMethod) {
      this.applyMethod = applyMethod;
    }

    @Override
    public Statement apply(final Statement statement, final FrameworkMethod method,
        final Object target) {
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          if (applyMethod.equals(method.getName())) {
            statement.evaluate();
          } else if (applyMethod.isEmpty()) {
            statement.evaluate();
          }
        }
      };
    }
  }
}
