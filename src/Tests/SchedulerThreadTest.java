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

    private SchedulerThread scheduler = new SchedulerThread();

    FloorEvent floorTest1 = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 1, 1);
    FloorEvent floorTest2 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.UP, 3, 1);
    FloorEvent floorTest3 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.DOWN, 1, 1);
    


    @Test
    public void testDestinationFloor(){
        
    }

    @Test
    public void testStopRequestWIthNoRequests(){
        
    }
    
     @Test
    public void testStopRequestWithRequests(){
        
    }
    
    @Test
    public void testByteToFloorEvent() throws IOException, ClassNotFoundException {
       // FloorThread.floorEventToByte(floorTest1);
       //  byte[] e = scheduler.floorEventToByte(floorTest1);
       //  System.out.println(scheduler.byteToFloorEvent(e));
    }


}
