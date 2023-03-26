package Tests;

import ElevatorStateMachine.ElevatorStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * @author : Ahmad
 *
 * This class tests the transition from ApproachingFloor state
 *
 *                  Method                                Transition Tested                 Test Result
 * ------------------------------------------------------------------------------------------------------
 *      approachingFloorTStoppedTransitionTest()       ApproachingFloor -> Stopped             PASS
 *
 */
public class ApproachingFloorTests {
    ElevatorStateMachine s;


    @Before
    public void setup() throws InterruptedException {
        s = new ElevatorStateMachine(7777);
        s.closeDoor(); // To change the current state from Idle to Stopped
        s.moveDown(); // To change the current state from Stopped to ApproachingFloor
    }



    // Testing elevator transitions from ApproachingFloor to Stopped after it stops
    @Test
    public void approachingFloorTStoppedTransitionTest() {

        s.stop();
        // should transition from ApproachingFloor to Stopped.
        assertEquals("Stopped", (s.getState().toString()));
    }



    @After
    public void teardown(){}
}
