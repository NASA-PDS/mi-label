package gov.nasa.pds.imaging.generate.label;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ItemNodeTest {

    ItemNode node;
    
    @Before
    public void setUp() throws Exception {
        this.node = new ItemNode("test", "test-units");
        this.node.addValue("val1");
    }

//    @After
//    public void tearDown() throws Exception {
//    }
    
    @Test
    public final void testGetValue() {
        assertEquals("val1", this.node.toString());
    }
    
    @Test
    public final void testGetValueList() {
        this.node.addValue("val2");
        
        List<String> expected = new ArrayList<String>();
        expected.add("val1");
        expected.add("val2");
        assertTrue(this.node.getValue() instanceof List);
        assertEquals(expected, this.node.getValue());
    }    

    @Test
    public final void testToString() {
        assertEquals("val1", this.node.toString());
    }
    
    @Test
    public final void testToStringList() {
        this.node.addValue("val2");
        assertEquals("(val1,val2)", this.node.toString());
    }    

}
