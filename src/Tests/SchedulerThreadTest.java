package Tests;

import static org.junit.Assert.*;

import Threads.ElevatorBuffer;
import Threads.FloorEvent;
import Threads.FloorThread;
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

    FloorEvent floorTest1 = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 1, 1);
    FloorEvent floorTest2 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.UP, 3, 1);
    FloorEvent floorTest3 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.DOWN, 1, 1);




    @Test
    public void testFPutAndFTakeHaveTheSameValues() {
        fPutBuffer.put(floorTest1);

        FloorEvent actualEvent = fPutBuffer.take();
        assertEquals(floorTest1, actualEvent);
    }
    @Test
    public void testFloorEventToByte() throws IOException {
        //System.out.println(Arrays.toString(scheduler.floorEventToByte(floorTest1)));

    }
    @Test
    public void testByteToFloorEvent() throws IOException, ClassNotFoundException {
       // FloorThread.floorEventToByte(floorTest1);
       //  byte[] e = scheduler.floorEventToByte(floorTest1);
       //  System.out.println(scheduler.byteToFloorEvent(e));
    }
    

}
