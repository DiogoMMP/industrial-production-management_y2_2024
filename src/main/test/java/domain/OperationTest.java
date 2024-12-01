package domain;

import org.junit.Test;
import static org.junit.Assert.*;
import domain.Operation;

public class OperationTest {

    @Test
    public void testConstructorWithParameters() {
        Operation operation = new Operation("1", "Test Operation", 10.0);
        assertEquals("1", operation.getId());
        assertEquals("Test Operation", operation.getDescription());
        assertEquals(10.0, operation.getQuantity(), 0.0);
    }

    @Test
    public void testDefaultConstructor() {
        Operation operation = new Operation();
        assertEquals("", operation.getId());
        assertEquals("", operation.getDescription());
        assertEquals(0.0, operation.getQuantity(), 0.0);
    }

    @Test
    public void testGetId() {
        Operation operation = new Operation("1", "Test Operation", 10.0);
        assertEquals("1", operation.getId());
    }

    @Test
    public void testGetDescription() {
        Operation operation = new Operation("1", "Test Operation", 10.0);
        assertEquals("Test Operation", operation.getDescription());
    }

    @Test
    public void testGetQuantity() {
        Operation operation = new Operation("1", "Test Operation", 10.0);
        assertEquals(10.0, operation.getQuantity(), 0.0);
    }

    @Test
    public void testSetId() {
        Operation operation = new Operation();
        operation.setId("2");
        assertEquals("2", operation.getId());
    }

    @Test
    public void testSetDescription() {
        Operation operation = new Operation();
        operation.setDescription("New Description");
        assertEquals("New Description", operation.getDescription());
    }

    @Test
    public void testSetQuantity() {
        Operation operation = new Operation();
        operation.setQuantity(20.0);
        assertEquals(20.0, operation.getQuantity(), 0.0);
    }

    @Test
    public void testToString() {
        Operation operation = new Operation("1", "Test Operation", 10.0);
        assertEquals("Test Operation", operation.toString());
    }
}
