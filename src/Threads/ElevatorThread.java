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

    public boolean isRightElevator;   // holds if the buffer has work for the current elevator

    private int ElevatorNum;    // holds which elevator this currently is

    /**
     * Constructor for the Elevator class (TODO Change elevator to not use buffers)
     *
     * @param ElevatorNum the current elevators number
     */
    public ElevatorThread(int ElevatorNum){
        super(1033);
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

        }
    }

}

