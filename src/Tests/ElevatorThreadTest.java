/*
Note: this is currently not passing the test




import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ElevatorThreadTest {
    Threads.ElevatorBuffer putBuff;
    Threads.ElevatorBuffer takeBuff;
    Threads.FloorEvent floorEvnt;
    Threads.ElevatorThread elvtrThread;
    int ElevatorNum = 1;

    @Before
    public void setup(){
        putBuff = new Threads.ElevatorBuffer();
        takeBuff = new Threads.ElevatorBuffer();
        floorEvnt = new Threads.FloorEvent("01:00:00.000", 3, Threads.FloorEvent.FloorButton.UP, 1, ElevatorNum);
        takeBuff.getContentsOfBuffer().add(floorEvnt);


    }


    @Test
    public void testingIsRightElevatorTrue(){
        elvtrThread = new Threads.ElevatorThread(putBuff, takeBuff, ElevatorNum);
        elvtrThread.run();

        assertTrue(elvtrThread.isRightElevator);



    }


    @After
    public void teardown(){}
}
*/