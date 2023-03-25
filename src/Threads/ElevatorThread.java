package Threads;

import ElevatorStateMachine.ElevatorStateMachine;

/**
 * The Elevator class simulates an elevator which has buttons and lamps inside of the elevator used to select floors and
 * indicate the floors selected, and to indicate the location of the elevator itself.
 * The elevator also  communicates with floors through a scheduler to allow the elevator to go to a requested destination.
 *
 * @author  Justin Winford
 * @version Iteration 1
 * @since   2023-02-03
 */
public class ElevatorThread extends ElevatorStateMachine implements Runnable {

    private ElevatorBuffer elevatorPutBuffer;  // buffer for the scheduler and elevator data
    private ElevatorBuffer elevatorTakeBuffer;

    public boolean isRightElevator;   // holds if the buffer has work for the current elevator

    private int ElevatorNum;    // holds which elevator this currently is

    /**
     * Constructor for the Elevator class (TODO Change elevator to not use buffers)
     *
     * @param elevatorPutBuffer,elevatorTakeBuffer buffer for the scheduler and elevator to communicate the floor events
     * @param ElevatorNum the current elevators number
     */
    public ElevatorThread(ElevatorBuffer elevatorPutBuffer, ElevatorBuffer elevatorTakeBuffer, int ElevatorNum){
        //super("Elevator");
        this.elevatorPutBuffer = elevatorPutBuffer;
        this.elevatorTakeBuffer = elevatorTakeBuffer;
        this.ElevatorNum = ElevatorNum;
    }


    public void moveUp(int destination) throws InterruptedException {
        super.forceMoveUp(destination);
    }

    public void moveDown(int destination) throws InterruptedException {
        super.forceMoveDown(destination);
    }

    @Override
    public void openDoor() throws InterruptedException {
        super.forceOpenDoor();
    }

    @Override
    public void closeDoor() {
        super.forceCloseDoor();
    }

    /**
     * The elevator will make calls to the Scheduler. Once there is work to be done the elevator accepts or declines
     * the call. The Elevator will then send the data back to the Scheduler.
     */
    @Override
    public void run(){

        while(true){

            //System.out.println("Elevator " + ElevatorNum + " is checking if any floor button has been pressed.");

            // Check if there is anything in the buffer
            if(elevatorTakeBuffer.getContentsOfBuffer().size() > 0 ){
                //System.out.println("Elevator " + ElevatorNum + " has found work.");
            }else{
                //System.out.println("Elevator " + ElevatorNum + " has found no work.");
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){}
            }

            // Check if the work is meant for this specific elevator.
            for(int i = 0; i < elevatorTakeBuffer.getContentsOfBuffer().size(); i++){
                if(elevatorTakeBuffer.getContentsOfBuffer().size() > 0 &&
                        elevatorTakeBuffer.getContentsOfBuffer().get(i).getElevatorNum() == ElevatorNum){

                    System.out.println("Elevator " + ElevatorNum + " has found work on floor " + elevatorTakeBuffer.getContentsOfBuffer().get(i).getFloorNumber());
                    isRightElevator = true;


                }else{
                    System.out.println("Scheduler is not looking for Elevator " + ElevatorNum);
                    isRightElevator = false;
                }

                // Retrieves the floor the elevator must go to and sends the info back to the buffer.
                if(isRightElevator){
                    FloorEvent destination =  elevatorTakeBuffer.take();
                    System.out.println("STEP 4");
                    System.out.println("Elevator " + ElevatorNum + " is going to floor " + destination.getElevatorButton());
                    destination.setProcessed();
                    System.out.println("Elevator " + ElevatorNum + " has reached floor " + destination.getElevatorButton());
                    System.out.println("STEP 5");
                    elevatorPutBuffer.put(destination);
                }
                try{
                    Thread.sleep(500);
                }catch(InterruptedException e){}
            }

        }
    }

}

