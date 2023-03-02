import MainPackage.FloorEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SchedulerStateMachineTests {

    SchedulerThread scheduler;
    ElevatorBuffer ePutBuffer, eTakeBuffer, fPutBuffer, fTakeBuffer;
    FloorEvent floorEvent;
    FloorEvent.FloorButton button;

    @Before
    public void setUp(){
        ePutBuffer = new ElevatorBuffer();
        eTakeBuffer = new ElevatorBuffer();
        fPutBuffer = new ElevatorBuffer();
        fTakeBuffer = new ElevatorBuffer();
        button = FloorEvent.FloorButton.UP;
        floorEvent = new FloorEvent("01:00:00.000",1,button,1,1);


        scheduler = new SchedulerThread(ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer);
    }

    @Test
    public void testInitialState() {
        assertEquals(SchedulerThread.SchedulerState.IDLE, scheduler.getState());
    }

    @Test
    public void testProcessingFloorState() {
        fPutBuffer.put(floorEvent);
        scheduler.processingFloorState();
        assertEquals(SchedulerThread.SchedulerState.PROCESSING_FLOOR_EVENT, scheduler.getState());
    }

    @Test
    public void testDispatchingToElevatorState() {
        scheduler.dispatchingToElevatorState();
        assertEquals(SchedulerThread.SchedulerState.DISPATCHING_TO_ELEVATOR, scheduler.getState());
    }

    @Test
    public void testProcessingElevatorEventState() {
        scheduler.processingElevatorEventState();
        assertEquals(SchedulerThread.SchedulerState.PROCESSING_ELEVATOR_EVENT, scheduler.getState());
    }

    @Test
    public void testDispatchingToFloorState() {
        scheduler.dispatchingToFloorState();
        assertEquals(SchedulerThread.SchedulerState.DISPATCHING_TO_FLOOR, scheduler.getState());
    }

}
