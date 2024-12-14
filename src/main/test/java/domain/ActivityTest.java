package domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    @Test
    void testConstructorAndGetters() {
        Activity activity = new Activity("A1", "Description", 5, "days", 100, Arrays.asList("A0"));
        assertEquals("A1", activity.getActId());
        assertEquals("Description", activity.getDescription());
        assertEquals(5, activity.getDuration());
        assertEquals("5 days", activity.getDurationWithUnit());
        assertEquals(100, activity.getTotalCost());
        assertEquals(Arrays.asList("A0"), activity.getPrevActIds());
    }

    @Test
    void testSetters() {
        Activity activity = new Activity("A1", "Description", 5, "days", 100, Arrays.asList("A0"));
        activity.setActId("A2");
        activity.setDescription("New Description");
        activity.setDuration(10);
        activity.setDurationUnit("hours");
        activity.setTotalCost(200);
        activity.setPrevActIds(Collections.singletonList("A1"));

        assertEquals("A2", activity.getActId());
        assertEquals("New Description", activity.getDescription());
        assertEquals(10, activity.getDuration());
        assertEquals("10 hours", activity.getDurationWithUnit());
        assertEquals(200, activity.getTotalCost());
        assertEquals(Collections.singletonList("A1"), activity.getPrevActIds());
    }

    @Test
    void testGetEarliestStart() {
        Activity activity = new Activity("A1", "Description", 5, "days", 100, Arrays.asList("A0"));
        assertEquals(0.0, activity.getEarliestStart());
    }

    @Test
    void testGetLatestFinish() {
        Activity activity = new Activity("A1", "Description", 5, "days", 100, Arrays.asList("A0"));
        assertEquals(0.0, activity.getLatestFinish());
    }

    @Test
    void testGetSlack() {
        Activity activity = new Activity("A1", "Description", 5, "days", 100, Arrays.asList("A0"));
        assertEquals(0.0, activity.getSlack());
    }
}
