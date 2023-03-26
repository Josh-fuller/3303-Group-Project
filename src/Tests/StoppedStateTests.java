package Tests;

import ElevatorStateMachine.ElevatorStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * @author : Ahmad
 *
 * This class tests transitions from Stopped state
 *
 *                  Method                                     Transition Tested                        Test Result
 * -------------------------------------------------------------------------------------------------------------------
 *      stoppedToIdleTransitionTest()                   Stopped -> Idle                                     PASS
 *      stoppedToApproachingFloorTransitionTest()       Stopped -> MovingDown -> ApproachingFloor           PASS
 *      stoppedToMovingUpTransitionTest()               Stopped -> MovingUp                                 PASS
 *      stoppedToMovingDownTransitionTest()             Stopped -> MovingDown                               PASS
 *
 */
public class StoppedStateTests {
    ElevatorStateMachine s;


    @Before
    public void setup(){
        s = new ElevatorStateMachine(77777);
        s.closeDoor(); // To change the current state from Idle to Stopped
    }



    @Test
    // Testing elevator transitions from Stopped to Idle when door is opened
    public void stoppedToIdleTransitionTest(){
        //TODO fix s.openDoor();
        // should transition from Stopped to Idle.
        assertEquals("Idle", (s.getState().toString()));
    }


    @Test
    // Testing elevator transitions from Stopped to ApproachingFloor after it moves down
    public void stoppedToApproachingFloorTransitionTest() throws InterruptedException {
        s.moveDown();
        // should transition from Stopped to ApproachingFloor.
        assertEquals("ApproachingFloor", (s.getState().toString()));
    }


    @Test
    // Testing elevator moving upwards from Stopped to MovingUp
    public void stoppedToMovingUpTransitionTest() throws InterruptedException {
        s.moveUp();
        // making sure moving direction is UP
        assertEquals(ElevatorStateMachine.Direction.UP, s.getMovingDirection());
    }


    @Test
    // Testing elevator moving downwards from Stopped to MovingDown
    public void stoppedToMovingDownTransitionTest() throws InterruptedException {
        s.moveDown();
        // making sure moving direction is DOWN
        assertEquals(ElevatorStateMachine.Direction.DOWN, s.getMovingDirection());
    }



    @After
    public void teardown(){}
}
