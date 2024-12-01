package prodPlanSimulator.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import domain.Material;

public class MaterialTest {

    @Test
    public void testConstructor() {
        Material material = new Material("1", "Steel", "100");
        assertEquals("1", material.getID());
        assertEquals("Steel", material.getName());
        assertEquals("100", material.getQuantity());
    }

    @Test
    public void testGetID() {
        Material material = new Material("1", "Steel", "100");
        assertEquals("1", material.getID());
    }

    @Test
    public void testSetID() {
        Material material = new Material("1", "Steel", "100");
        material.setID("2");
        assertEquals("2", material.getID());
    }

    @Test
    public void testGetName() {
        Material material = new Material("1", "Steel", "100");
        assertEquals("Steel", material.getName());
    }

    @Test
    public void testSetName() {
        Material material = new Material("1", "Steel", "100");
        material.setName("Iron");
        assertEquals("Iron", material.getName());
    }

    @Test
    public void testGetQuantity() {
        Material material = new Material("1", "Steel", "100");
        assertEquals("100", material.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        Material material = new Material("1", "Steel", "100");
        material.setQuantity("200");
        assertEquals("200", material.getQuantity());
    }

    @Test
    public void testToString() {
        Material material = new Material("1", "Steel", "100");
        assertEquals("100x Steel", material.toString());
    }
}
