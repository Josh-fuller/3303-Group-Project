package Tests;

import static org.junit.Assert.*;

import Threads.ElevatorBuffer;
import Threads.FloorEvent;
import Threads.SchedulerThread;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class SchedulerThreadTest {

    private ElevatorBuffer ePutBuffer = new ElevatorBuffer();
    private ElevatorBuffer eTakeBuffer = new ElevatorBuffer();
    private ElevatorBuffer fPutBuffer = new ElevatorBuffer();
    private ElevatorBuffer fTakeBuffer = new ElevatorBuffer();
    private SchedulerThread scheduler = new SchedulerThread();

    FloorEvent expectedEvent = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 1, 1);



    @Test
    public void testFPutAndFTakeHaveTheSameValues() {
        fPutBuffer.put(expectedEvent);

        FloorEvent actualEvent = fPutBuffer.take();
        assertEquals(expectedEvent, actualEvent);
    }
    @Test
    public void testFloorEventToByte() throws IOException {
        System.out.println(Arrays.toString(scheduler.floorEventToByte(expectedEvent)));

    }
    public void testByteToFloorEvent() throws IOException, ClassNotFoundException {
        scheduler.floorEventToByte(expectedEvent);
        byte[] e = scheduler.floorEventToByte(expectedEvent);
        System.out.println(scheduler.byteToFloorEvent(e));
    }

}
