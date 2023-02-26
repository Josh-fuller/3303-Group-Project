package ElevatorStateMachineTests;
import ElevatorStateMachine.ElevatorStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author : Ahmad
 *
 * This class tests the transition from Idle state
 *
 *                  Method                    Transition Tested             Test Result
 * ---------------------------------------------------------------------------------------
 *      idleToStoppedTransitionTest()         Idle -> Stopped                   PASS
 *
 */
public class IdleStateTests {
    ElevatorStateMachine s;


    @Before
    public void setup(){
        // Initial state is Idle, so there is no need to change it for this test
        s = new ElevatorStateMachine();

    }



    // Testing elevator transitions from Idle to Stopped when door is closed
    @Test
    public void idleToStoppedTransitionTest (){
        s.closeDoor();
        // should transition from idle to stopped.
        assertEquals("Stopped", (s.getState().toString()));
    }



    @After
    public void teardown(){}

}
