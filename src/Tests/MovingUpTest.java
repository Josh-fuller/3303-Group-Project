package Tests;

import ElevatorStateMachine.ElevatorStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * @author : Ahmad
 *
 * This class tests transitions from ApproachingFloor state
 *
 *                  Method                                    Transition Tested                         Test Result
 * -------------------------------------------------------------------------------------------------------------------
 *      movingUpToApproachingFloorTransitionTest()        Stopped -> MovingUp -> ApproachingFloor           PASS
 *
 */
public class MovingUpTest {
    ElevatorStateMachine s;



    @Before
    public void setup() {
        s = new ElevatorStateMachine(7777);
        s.closeDoor(); // To change the current state from Idle to Stopped
    }



    @Test
    // Testing elevator transitions from MovingUp to ApproachingFloor after it moves up
    public void movingUpToApproachingFloorTransitionTest() throws InterruptedException {
        s.moveUp();
        // making sure state transitions to ApproachingFloor after moving up
        assertEquals("ApproachingFloor", (s.getState().toString()));
    }



    @After
    public void teardown(){}
}
