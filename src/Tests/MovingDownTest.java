package Tests;

import ElevatorStateMachine.ElevatorStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * @author : Ahmad
 *
 * This class tests transition from MovingDown state
 *
 *                  Method                                      Transition Tested                         Test Result
 * --------------------------------------------------------------------------------------------------------------------
 *      movingUpToApproachingFloorTransitionTest()        Stopped -> MovingDown -> ApproachingFloor          PASS
 *
 */
public class MovingDownTest {
    ElevatorStateMachine s;



    @Before
    public void setup() {
        s = new ElevatorStateMachine();
        s.closeDoor(); // To change the current state from Idle to Stopped
    }



    @Test
    // Testing elevator transitions from MovingDown to ApproachingFloor after it moves down
    public void movingDownToApproachingFloorTransitionTest() throws InterruptedException {
        s.moveDown();
        // making sure state transitions to ApproachingFloor after moving up
        assertEquals("ApproachingFloor", (s.getState().toString()));
    }



    @After
    public void teardown(){}
}
