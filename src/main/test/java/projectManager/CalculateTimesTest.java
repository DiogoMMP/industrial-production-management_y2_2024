package projectManager;

import domain.Activity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ActivitiesMapRepository;
import repository.Instances;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class CalculateTimesTest {
    private PERT_CPM pertCPM;

    @BeforeEach
    void setUp() {
        // Initialize the PERT_CPM instance before each test
        ActivitiesMapRepository activitiesMapRepository = Instances.getInstance().getActivitiesMapRepository();
        activitiesMapRepository.addActivities("small_project.csv");
        pertCPM = Instances.getInstance().getPERT_CPM();
        pertCPM.buildPERT_CPM();
        CalculateTimes calculateTimes = new CalculateTimes();
        calculateTimes.calculateTimes();
    }

    @Test
    void calculateTimes() {

        LinkedHashMap<String, Activity> activities = pertCPM.getActivitiesPERT_CPM();

        // Verify the calculated times for each activity
        Activity startActivity = activities.get("START");
        assertNotNull(startActivity);
        assertEquals(0.0, startActivity.getEarliestStart());
        assertEquals(0.0, startActivity.getEarliestFinish());
        assertEquals(0.0, startActivity.getLatestStart());
        assertEquals(0.0, startActivity.getLatestFinish());
        assertEquals(0.0, startActivity.getSlack());

        Activity endActivity = activities.get("END");
        assertNotNull(endActivity);
        assertTrue(endActivity.getEarliestStart() >= 0.0);
        assertTrue(endActivity.getEarliestFinish() >= 0.0);
        assertTrue(endActivity.getLatestStart() >= 0.0);
        assertTrue(endActivity.getLatestFinish() >= 0.0);
        assertEquals(0.0, endActivity.getSlack());

        // Add more assertions for other activities based on the expected values
        Activity activityA = activities.get("A");
        assertNotNull(activityA);
        assertEquals(0.0, activityA.getEarliestStart());
        assertEquals(1.0, activityA.getEarliestFinish());
        assertEquals(4.0, activityA.getLatestStart());
        assertEquals(5.0, activityA.getLatestFinish());
        assertEquals(4.0, activityA.getSlack());
        assertEquals(activityA.getLatestStart() - activityA.getEarliestStart(), activityA.getSlack());
        assertEquals(activityA.getLatestFinish() - activityA.getEarliestFinish(), activityA.getSlack());

        Activity activityB = activities.get("B");
        assertNotNull(activityB);
        assertEquals(0.0, activityB.getEarliestStart());
        assertEquals(4.0, activityB.getEarliestFinish());
        assertEquals(0.0, activityB.getLatestStart());
        assertEquals(4.0, activityB.getLatestFinish());
        assertEquals(0.0, activityB.getSlack());
        assertEquals(activityB.getLatestStart() - activityB.getEarliestStart(), activityB.getSlack());
        assertEquals(activityB.getLatestFinish() - activityB.getEarliestFinish(), activityB.getSlack());

        // Test for an activity with non-zero slack
        Activity activityN = activities.get("A");
        assertNotNull(activityN);
        assertTrue(activityN.getSlack() > 0.0);
        assertEquals(activityN.getLatestStart() - activityN.getEarliestStart(), activityN.getSlack());
        assertEquals(activityN.getLatestFinish() - activityN.getEarliestFinish(), activityN.getSlack());
    }
    @AfterAll
    static void tearDownAll() {
        Instances.getInstance().clear();
    }
}