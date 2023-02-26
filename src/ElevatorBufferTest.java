import static org.junit.Assert.*;

import MainPackage.FloorEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ahmad
 * In this test, a new ElevatorBuffer object is created and a MainPackage.FloorEvent object
 * is added to it using the put method. Then, the take method is called and the
 * contents of the ElevatorBuffer object are retrieved using the getContentsOfBuffer
 * method. Finally, assertTrue is used to check if the contents are empty.
 */

public class ElevatorBufferTest {
    ElevatorBuffer buff;
    FloorEvent floorEvnt;


    @Before
    public void setup(){
        buff = new ElevatorBuffer();
        floorEvnt = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 1, 1);
    }

    @Test
    public void elevatorBufferIsEmptyAfterTake(){
        buff.put(floorEvnt);
        buff.take();
        assertTrue((buff.getContentsOfBuffer()).isEmpty());
    }

    @After
    public void teardown(){}


}
