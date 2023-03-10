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


    int startTranslation; //The number of floors between the cars current location and where the elevator request happens
    int endTranslation; //The number of floors between the start of the elevator request and the end

    //boolean emptyBuffer;

    ElevatorThread elevatorThread = new ElevatorThread(ePutBuffer,eTakeBuffer, 1);

    public enum SchedulerState {
        IDLE,
        PROCESSING_FLOOR_EVENT,
        DISPATCHING_TO_ELEVATOR,
        PROCESSING_ELEVATOR_EVENT,
        DISPATCHING_TO_FLOOR
    }




    public SchedulerThread(){

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
     * Gets the translation number from current floor -> start floor [assuming up is positive]
     */
    private int getStartTranslation(){
        return eventTransferOne.getFloorNumber() - elevatorThread.getCurrentFloor;
    }

    /** *
     * Gets the translation number from start floor -> end floor [assuming up is positive]
     */
    private int getEndTranslation(){
        return eventTransferOne.getElevatorButton() - eventTransferOne.getElevatorNum();
    }

    private void translateCar(int distance){

        boolean direction = true;

        if(distance < 0){
            direction = false;
        }

        for(int i = 0;i < distance; i++){
            if(direction){
                elevatorThread.moveUp();
            }
            else{
                elevatorThread.moveDown();
            }
            elevatorThread.openDoor();
            elevatorThread.closeDoor();
        }
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
                    if(!fPutBuffer.isEmpty()) {
                        processingFloorState();

                    } else {
                        idleState();
                    }
                    break;

                case PROCESSING_FLOOR_EVENT:
                    // Take event from fPutBuffer
                    eventTransferOne = fPutBuffer.take();
                    System.out.println("Scheduling event from floor: " + eventTransferOne.getFloorNumber() + " to floor: "
                            + eventTransferOne.getElevatorButton() + "(STEP 2)");

                    //get the number of floors to translate:
                    startTranslation = getStartTranslation();
                    endTranslation = getEndTranslation();

                    // Transition to DISPATCHING_TO_ELEVATOR state
                    dispatchingToElevatorState();
                    break;

                case DISPATCHING_TO_ELEVATOR:

                    //Dispatch elevator based on processed event
                    translateCar(startTranslation);//go to start floor
                    translateCar(endTranslation);//go to end floor

                    //go to the right floor to start
                    //System.out.println("(STEP 3)");

                    //transition to idle state
                    idleState();

                    break;

                case DISPATCHING_TO_FLOOR:
                    // Put event in fTakeBuffer, signifying completion of event
                    System.out.println("STEP 7");
                    fTakeBuffer.put(eventTransferOne);
                    //go to idle state
                    idleState();
                    break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

        }
    }
}
