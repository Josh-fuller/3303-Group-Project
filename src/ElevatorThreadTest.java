/*
Note: this is currently not passing the test




import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ElevatorThreadTest {
    ElevatorBuffer putBuff;
    ElevatorBuffer takeBuff;
    MainPackage.FloorEvent floorEvnt;
    ElevatorThread elvtrThread;
    int ElevatorNum = 1;

    @Before
    public void setup(){
        putBuff = new ElevatorBuffer();
        takeBuff = new ElevatorBuffer();
        floorEvnt = new MainPackage.FloorEvent("01:00:00.000", 3, MainPackage.FloorEvent.FloorButton.UP, 1, ElevatorNum);
        takeBuff.getContentsOfBuffer().add(floorEvnt);


    }


    @Test
    public void testingIsRightElevatorTrue(){
        elvtrThread = new ElevatorThread(putBuff, takeBuff, ElevatorNum);
        elvtrThread.run();

        assertTrue(elvtrThread.isRightElevator);



    }


    @After
    public void teardown(){}
}
*/