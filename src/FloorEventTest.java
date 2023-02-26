import static org.junit.Assert.assertEquals;

import MainPackage.FloorEvent;
import org.junit.Test;

public class FloorEventTest {

    String time = "02:00:00.000";
    int floorNumber = 2;
    FloorEvent.FloorButton floorButton = FloorEvent.FloorButton.DOWN;
    int elevatorButton = 2;
    int elevatorNum = 1;
    FloorEvent floorEvent = new FloorEvent(time, floorNumber, floorButton, elevatorButton, elevatorNum);

    @Test
    public void testTimeReturnedIsCorrect() {
        assertEquals(time, floorEvent.getTime());
    }

    @Test
    public void testFloorNumberReturnedIsCorrect() {
        assertEquals(floorNumber, floorEvent.getFloorNumber());
    }

    @Test
    public void testFloorButtonReturnedIsCorrect() {
        assertEquals(floorButton, floorEvent.getFloorButton());
    }

    @Test
    public void testElevatorButtonReturnedIsCorrect() {
        assertEquals(elevatorButton, floorEvent.getElevatorButton());
    }

    @Test
    public void testElevatorNumReturnedIsCorrect() {
        assertEquals(elevatorNum, floorEvent.getElevatorNum());
    }

    @Test
    public void testIsProcessed() {
        assertEquals(false, floorEvent.isProcessed());
    }

}
