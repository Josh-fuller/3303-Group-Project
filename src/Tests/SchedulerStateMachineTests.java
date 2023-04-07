package Tests;

import Threads.FloorEvent;
import Threads.SchedulerThread;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SchedulerStateMachineTests {

    SchedulerThread scheduler;
    FloorEvent floorEvent;
    FloorEvent.FloorButton button;

    @Before
    public void setUp(){
        button = FloorEvent.FloorButton.UP;
        floorEvent = new FloorEvent("01:00:00.000",1,button,1,1);


        scheduler = new SchedulerThread();
    }

    @Test
    public void testInitialState() {
        Assert.assertEquals(SchedulerThread.SchedulerState.IDLE, scheduler.getState());
    }

    @Test
    public void testProcessingFloorState() {
        scheduler.processingFloorState();
        Assert.assertEquals(SchedulerThread.SchedulerState.PROCESSING_FLOOR_EVENT, scheduler.getState());
    }

    @Test
    public void testDispatchingToElevatorState() {
        //scheduler.dispatchingToElevatorState();
        //Assert.assertEquals(SchedulerThread.SchedulerState.DISPATCHING_TO_ELEVATOR, scheduler.getState());
    }

    @Test
    public void testProcessingElevatorEventState() {
        scheduler.processingElevatorEventState();
        Assert.assertEquals(SchedulerThread.SchedulerState.PROCESSING_ELEVATOR_EVENT, scheduler.getState());
    }

    @Test
    public void testDispatchingToFloorState() {
        scheduler.dispatchingToFloorState();
        Assert.assertEquals(SchedulerThread.SchedulerState.DISPATCHING_TO_FLOOR, scheduler.getState());
    }

}
