package Tests;
import Threads.ElevatorThread;
import org.junit.*;
import java.net.*;


/**
 * Test Class for methods in ElevatorThread.
 *
 * @author Mahtab Ameli
 * @version Iteration 5
 */
public class ElevatorThreadTest {

    private ElevatorThread elevator;

    @Before
    public void setUp() {
        elevator = new ElevatorThread(6000,1);
    }

    /**
     * Tests whether a new instance of elevatorThread has access to the correct number of building floors.
     *
     * METHODS TESTED:
     * populateFloors()
     * getFloorCount()
     *
     */
    @Test
    public void testPopulateFloors(){
        //PopulateFloors() is called from the constructor of elevatorThread, initializing number of building floors to 22
        Assert.assertEquals(22, elevator.getFloorCount()); // Number of floors
    }



    /**
     * Tests whether currentFloor is correctly updated when the elevator moves up and down floors.
     *
     * METHODS TESTED:
     * incrementFloor()
     * decrementFloor()
     * getCurrentFloor()
     *
     */
    @Test
    public void testIncrementDecrementFloor(){
        Assert.assertEquals(1, elevator.getCurrentFloor()); // Elevator must initially be at floor 1

        elevator.incrementFloor();
        Assert.assertEquals(2, elevator.getCurrentFloor()); // currentFloor must be 2, following single increment

        elevator.decrementFloor();
        Assert.assertEquals(1, elevator.getCurrentFloor()); // currentFloor must be 1, following single decrement

        for (int i = 0; i < 2; i++) {
            elevator.incrementFloor();
        }
        Assert.assertEquals(3, elevator.getCurrentFloor()); // currentFloor must be 6, following 5 increments from floor 1

        for (int i = 0; i < 2; i++) {
            elevator.decrementFloor();
        }
        Assert.assertEquals(1, elevator.getCurrentFloor()); // currentFloor must be 1, following 5 decrements from floor 6
    }


    /**
     * Tests whether the elevator door closes and opens correctly.
     *
     * METHODS TESTED:
     * closeDoor()
     * openDoor()
     * isDoorOpen()
     *
     */
    @Test
    public void testCloseOpenDoor() {
        Assert.assertTrue( elevator.isDoorOpen()); // door must be open initially

        elevator.closeDoor();
        Assert.assertFalse(elevator.isDoorOpen()); // door must close

        elevator.openDoor();
        Assert.assertTrue(elevator.isDoorOpen()); // door must open
    }

    /**
     * Tests whether datagram packets for messaging the scheduler are created correctly.
     *
     * METHODS TESTED:
     * createMessagePacket()
     *
     */
    @Test
    public void testCreateMessagePacket() throws UnknownHostException {
        DatagramPacket testPacket = elevator.createMessagePacket((byte)0x01,6);
        byte[] testBytes = testPacket.getData();
        Assert.assertEquals(0, testBytes[0]);
        Assert.assertEquals(1, testBytes[1]);
        Assert.assertEquals(0, testBytes[2]);
        Assert.assertEquals(6, testBytes[3]);
    }


    /**
     * Tests whether the elevator successfully finishes travelling to all destinations left in destinationList.
     *
     * METHODS TESTED:
     * addDestination()
     * finishLeftoverStops()
     * getDestinationList()
     *
     */
    @Test
    public void testFinishLeftoverStops() {
        Assert.assertEquals(0, elevator.getDestinationList().size()); // there must be 0 destinations on the list initially

        elevator.addDestination(1);
        elevator.addDestination(2);
        Assert.assertEquals(2, elevator.getDestinationList().size()); // there must be 2 destinations on the list

        elevator.finishLeftoverStops();
        Assert.assertEquals(0, elevator.getDestinationList().size()); // there must be 0 destinations on the list again
    }


    /**
     * Tests whether stop signals received from the scheduler are correctly interpreted by the elevator.
     *
     * METHODS TESTED:
     * processStopSignalMessage()
     *
     */
    @Test
    public void testProcessStopSignalMessage() {
        byte[] stopFalse = {0x0};
        Assert.assertFalse(elevator.processStopSignalMessage(stopFalse));

        byte[] stopTrue = {0x1};
        Assert.assertTrue(elevator.processStopSignalMessage(stopTrue));

        stopTrue = new byte[]{0x5};
        Assert.assertTrue(elevator.processStopSignalMessage(stopTrue));
    }


    /**
     * Tests whether destination requests from the scheduler are interpreted correctly by the elevator.
     *
     * METHODS TESTED:
     * processDestinationFloorsMessage()
     * getNextDestination()
     * getSecondDestination()
     *
     */
    @Test
    public void testProcessDestinationFloorsMessage() {
    Assert.assertEquals(0, elevator.getNextDestination());
    Assert.assertEquals(0, elevator.getSecondDestination());
    Assert.assertEquals(0, elevator.getDestinationList().size());

    byte[] testMessage_1 = {0x3,0x7};
    elevator.processDestinationFloorsMessage(testMessage_1);
    Assert.assertEquals(3, elevator.getNextDestination());
    Assert.assertEquals(7, elevator.getSecondDestination());
    Assert.assertEquals(1, elevator.getDestinationList().size());

    byte[] testMessage_2 = {0x9,0x4};
    elevator.processDestinationFloorsMessage(testMessage_2);
    Assert.assertEquals(9, elevator.getNextDestination());
    Assert.assertEquals(4, elevator.getSecondDestination());
    Assert.assertEquals(2, elevator.getDestinationList().size());

    byte[] testMessage_3 = {0x4,0x6};
    elevator.processDestinationFloorsMessage(testMessage_3);
    Assert.assertEquals(4, elevator.getNextDestination());
    Assert.assertEquals(6, elevator.getSecondDestination());
    Assert.assertEquals(3, elevator.getDestinationList().size());
    }

    @After
    public void teardown(){
        elevator.closeSocket();
    }

}
