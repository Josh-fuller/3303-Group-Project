package Tests;

import static org.junit.Assert.*;
import Threads.FloorEvent;
import Threads.FloorThread;
import Threads.SchedulerThread;
import org.junit.*;
import java.io.IOException;
import java.util.ArrayList;

public class SchedulerThreadTest {

    private SchedulerThread scheduler = new SchedulerThread();
    FloorThread floor = new FloorThread();


    FloorEvent floorTest1 = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 2, 2);
    FloorEvent floorTest2 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.UP, 3, 1);
    FloorEvent floorTest3 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.DOWN, 4, 1);
    ArrayList<FloorEvent> eventList;





    public SchedulerThreadTest() throws IOException {
        ArrayList<FloorEvent> eventList = new ArrayList();
    }

    @Before
    public void setup(){
        eventList.add(floorTest1);
    }

    @Test
    public void testMessageSendFromFloor(){
        //scheduler.run();
        floor.sendPacket(floor.buildFloorByteMsg(eventList));
        //System.out.println(scheduler.getState());
    }
    
    @Test
    public void testByteToFloorEvent() throws IOException, ClassNotFoundException {
       // FloorThread.floorEventToByte(floorTest1);
       //  byte[] e = scheduler.floorEventToByte(floorTest1);
       //  System.out.println(scheduler.byteToFloorEvent(e));
    }


}
