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
	protected static Logger log = Logger.getLogger(GenerateTest.class
			.getName());

	/**
	 * JUnit Test Rules to print headers for each test
	 */
	@Rule
	public MethodRule watchman = new TestWatchman() {
		public void starting(FrameworkMethod method) {
			log.info("------------ Testing: "
					+ method.getName() + " ------------");
		}
	};

	/**
	 * Custom rule to allow for performing one unit test at a time. Helpful when
	 * a test is failing.
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
		public Statement apply(final Statement statement,
				final FrameworkMethod method, final Object target) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					if (applyMethod.equals(method.getName())) {
						statement.evaluate();
					} else if (applyMethod.equals("")) {
						statement.evaluate();
					}
				}
			};
		}
	}
}
