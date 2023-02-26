package ElevatorStateMachineTests;
import ElevatorStateMachine.ElevatorStateMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author : Ahmad
 *
 * This class tests the invalid actions while in Idle state
 *
 *                  Method                     Action Tested           Current State             Test Result
 * -------------------------------------------------------------------------------------------------------------
 *      invalidOpenDoorActionTest ()         INVALID openDoor()            Idle                     PASS
 *      invalidMoveUpActionTest ()           INVALID moveUp()              Idle                     PASS
 *      invalidMoveDownActionTest ()         INVALID moveDown()            Idle                     PASS
 *      invalidStopActionTest ()             INVALID stop()                Idle                     PASS
 *
 */
public class InvalidActionsTests {
    ElevatorStateMachine s;


    @Before
    public void setup(){
        s = new ElevatorStateMachine();
    }


    
    @Test
    // Testing invalid door opening action while Idle; door is already open in Idle state
    public void invalidOpenDoorActionTest (){
        s.openDoor();
        // there should be no change to door status; stay open
        assertTrue(s.isDoorOpen());
    }

    
    @Test
    // Testing invalid moveUp() action while Idle; elevator cannot move up directly from Idle state
    public void invalidMoveUpActionTest () throws InterruptedException {
        s.moveUp();
        // there should be no change to state; stay Idle
        assertEquals("Idle", (s.getState().toString()));
    }
    
    
    @Test
    // Testing invalid moveDown() action while Idle; elevator cannot move down directly from Idle state
    public void invalidMoveDownActionTest () throws InterruptedException {
        s.moveDown();
        // there should be no change to state; stay Idle
        assertEquals("Idle", (s.getState().toString()));
    }


    @Test
    // Testing invalid stop() action while Idle; elevator already stationary
    public void invalidStopActionTest () {
        s.stop();
        // there should be no change to state; stay Idle
        assertEquals("Idle", (s.getState().toString()));
    }



    @After
    public void teardown(){}

}
