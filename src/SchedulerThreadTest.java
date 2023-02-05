import static org.junit.Assert.*;

import org.junit.Test;

public class SchedulerThreadTest {

    private ElevatorBuffer ePutBuffer = new ElevatorBuffer();
    private ElevatorBuffer eTakeBuffer = new ElevatorBuffer();
    private ElevatorBuffer fPutBuffer = new ElevatorBuffer();
    private ElevatorBuffer fTakeBuffer = new ElevatorBuffer();
    private SchedulerThread scheduler = new SchedulerThread(ePutBuffer, eTakeBuffer, fPutBuffer, fTakeBuffer);



    @Test
    public void testFPutAndFTakeHaveTheSameValues() {
        FloorEvent expectedEvent = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 1, 1);
        fPutBuffer.put(expectedEvent);

        FloorEvent actualEvent = fPutBuffer.take();
        assertEquals(expectedEvent, actualEvent);
    }

}
