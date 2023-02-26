import MainPackage.FloorEvent;

import java.io.IOException;

/** *
 * Class Scheduler used to translate data between Floors and Elevators.
 *
 * @author Josh Fuller
 */
public class SchedulerThread implements Runnable{

    ElevatorBuffer ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer;
    FloorEvent eventTransferOne;
    FloorEvent eventTransferTwo;

    SchedulerState state;

    boolean emptyBuffer;

    public enum SchedulerState {
        IDLE,
        PROCESSING_FLOOR_EVENT,
        DISPATCHING_TO_ELEVATOR,
        PROCESSING_ELEVATOR_EVENT,
        DISPATCHING_TO_FLOOR
    }




    public SchedulerThread(ElevatorBuffer ePutBuffer, ElevatorBuffer eTakeBuffer, ElevatorBuffer fPutBuffer, ElevatorBuffer fTakeBuffer){

        this.ePutBuffer = ePutBuffer;
        this.eTakeBuffer = eTakeBuffer;
        this.fPutBuffer = fPutBuffer;
        this.fTakeBuffer = fTakeBuffer;
        state = SchedulerState.IDLE;
    }

    public void idleState(){
        state = SchedulerState.IDLE;
    }

    public void processingFloorState(){
        state = SchedulerState.PROCESSING_FLOOR_EVENT;
    }

    public void dispatchingToElevatorState(){
        state = SchedulerState.DISPATCHING_TO_ELEVATOR;
    }

    public void processingElevatorEventState(){
        state = SchedulerState.PROCESSING_ELEVATOR_EVENT;
    }

    public void dispatchingToFloorState(){
        state = SchedulerState.DISPATCHING_TO_FLOOR;
    }

    public SchedulerState getState(){
        return state;
    }


    /** *
     * The runnable portion of scheduler, responsible for acting as the translator from floor/elevator and back
     *
     * @author Josh Fuller
     */
    @Override
    public void run() {
        while(true){

            switch(state) {
                case IDLE:

                    // Check if any buffer is not empty
                    if(!ePutBuffer.isEmpty()) {
                        processingElevatorEventState();
                    }
                    else if(!fPutBuffer.isEmpty()) {
                        processingFloorState();
                    }
                    else {
                        idleState();
                    }

                    break;

                case PROCESSING_FLOOR_EVENT:
                    // Take event from fPutBuffer
                    eventTransferOne = fPutBuffer.take();
                    System.out.println("Scheduling event from floor: " + eventTransferOne.getFloorNumber() + " to floor: "
                            + eventTransferOne.getElevatorButton() + "(STEP 2)");

                    // Transition to DISPATCHING_TO_ELEVATOR state
                    dispatchingToElevatorState();
                    break;

                case DISPATCHING_TO_ELEVATOR:
                    // Put event in eTakeBuffer
                    eTakeBuffer.put(eventTransferOne);
                    System.out.println("(STEP 3)");

                    //transition to idle state
                    idleState();

                    break;

                case PROCESSING_ELEVATOR_EVENT:

                    // Take event from ePutBuffer if available
                    if(!ePutBuffer.isEmpty()) {
                        eventTransferTwo = ePutBuffer.take();
                        System.out.println("(STEP 6)");
                    }

                    // Print event information
                    System.out.println("Scheduler processed event from floor: " + eventTransferTwo.getFloorNumber() + " to floor: "
                            + eventTransferTwo.getElevatorButton());


                    // Transition to DISPATCHING_TO_FLOOR state
                    dispatchingToFloorState();
                    break;

                case DISPATCHING_TO_FLOOR:
                    // Put event in fTakeBuffer
                    System.out.println("STEP 7");
                    fTakeBuffer.put(eventTransferTwo);
                    //go to idle state
                    idleState();
                    break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

        }
    }

    public static void main(String[] args) throws IOException {

        //declare and initialise everything

        ElevatorBuffer ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer;

        Thread elevator, floor, scheduler;

        ePutBuffer = new ElevatorBuffer();
        eTakeBuffer = new ElevatorBuffer();
        fPutBuffer = new ElevatorBuffer();
        fTakeBuffer = new ElevatorBuffer();


        // Create the floor,scheduler and elevator threads,
        // passing each thread a reference to the
        // shared BoundedBuffer object.

        elevator = new Thread(new
                ElevatorThread(ePutBuffer, eTakeBuffer,1),"Elevator 1");
        System.out.println("Elevator Created");

        floor = new Thread(new
                FloorThread(fPutBuffer, fTakeBuffer), "Floor");
        System.out.println("Floor Created");

        scheduler = new Thread(new
                SchedulerThread(ePutBuffer, eTakeBuffer,fPutBuffer,fTakeBuffer), "Scheduler");
        System.out.println("Scheduler Created");


        elevator.start();
        floor.start();
        scheduler.start();

    }
}
