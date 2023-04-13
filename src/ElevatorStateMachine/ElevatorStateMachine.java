package ElevatorStateMachine;
import Threads.FloorEvent;
import java.util.*;
import static java.lang.Thread.sleep;

/**
 * ElevatorStateMachine represents the context of an elevator state machine, based on the State design pattern.
 * The concrete states of the machine are: IdleState, StoppedState, MovingUpState, MovingDownState, ApproachingFloorState
 *
 * @author  Mahtab Ameli
 * @version Iteration 2
 */
public class ElevatorStateMachine {

    private ElevatorState state;    // Elevator's current state
    private boolean cartStationary; // true if elevator is stationary, false if moving
    private boolean doorOpen;       // true if door is open, false if closed
    private Set<Integer> buttonSet; // Set of internal destination requests, to be communicated to the scheduler
    private Set<Integer> lampSet;   // Set of all stop requests (internal and external), communicated by the scheduler
    private Integer currentFloor;   // Elevator's current floor as signalled by the arrival sensor
    private List<Integer> floors;   // list of 5 floors that have access to the elevator
    private boolean stopSignal; // signal set to true when scheduler makes a command to stop at approaching floor
    private Integer arrivalSignal;  // integer indicates the floor number being approached by the elevator
    public enum Direction{UP, DOWN, NONE}   // enum type for direction of moving elevator. NONE if elevator is stationary
    private Direction movingDirection;      // current moving direction of the elevator. NONE if elevator is stationary

    private int port;


    private List<FloorEvent> elevatorEvents; // list of input events from user pressing buttons inside elevator

    /**
     * Constructor for the class.
     */
    public ElevatorStateMachine(int port) {
        this.port = port;
        this.cartStationary = true; // elevator is initially stationary
        this.doorOpen = true;   // the elevator door is initially open
        this.lampSet = new HashSet<Integer>();
        this.buttonSet = new HashSet<Integer>();
        this.currentFloor = 1;  // assuming elevator starts at floor #1
        this.floors = new ArrayList<Integer>();
        this.populateFloors();
        this.arrivalSignal = -10;    // the elevator is not approaching any floor when stationary
        this.movingDirection = Direction.NONE; // the elevator is not moving
        this.stopSignal = false;
        this.elevatorEvents = new ArrayList<>();
        //this.state = new ElevatorState(this);    // the elevator is in the Idle state
        this.state = new IdleState(this);    // the elevator is in the Idle state
        this.sendMoveRequest();
    }

    /**
     * Populates the list of floors that the machine will move between.
     */
    private void populateFloors() {
        floors.clear();
        floors.addAll(Arrays.asList(new Integer[]{1,2,3,4,5}));
    }

    /**
     * Changes elevator's state to given concrete state.
     * @param concreteState
     */
    protected void setState(ElevatorState concreteState) {
        this.state = concreteState;
    }

    /**
     * Attempts to make the elevator move up.
     */
    public void moveUp() throws InterruptedException {
        state.handleMovingUp();
    }


    /**
     * Attempts to make the elevator move down.
     */
    public void moveDown() throws InterruptedException {
        state.handleMovingDown();
    }

    /**
     * Forces the elevator to move up 1 floor.
     */
    public void forceMoveUp(int destination) throws InterruptedException {
        state = new MovingUpState(this);
        if (!(destination > currentFloor)) {
            System.out.println("Elevator cannot move up from " + currentFloor + " to " + destination
                    + ". Move up destination must be larger than current floor.");
            return;
        }
        int floorDifference = destination - currentFloor;
        for (int i = 0; i < floorDifference; i++) {
            forceIncrementFloor();
            //todo communicate this.arrivalSignal to scheduler. wait to hear back about whether to stop at this floor
            //todo update this.stopSignal based on communication received back from scheduler on whether to stop at this floor
            if (stopSignal) { // if there is a stop signal, stop the elevator and stop going up more floors
                state = new IdleState(this);
                System.out.println("\nElevator has stopped at " + currentFloor + ", because of scheduler's stop signal.");
                stopSignal = false; // resetting stop signal to false until scheduler sets it to true again
                break;
            }
        }
        System.out.print("****************************************************************************\n"
                + "ELEVATOR EXECUTING SCHEDULER COMMAND TO MOVE UP...\n"
                + "CURRENT ELEVATOR STATE: " + state + "     CURRENT FLOOR: " + currentFloor + "\n"
                + "****************************************************************************\n");
    }

    /**
     * Forces the elevator to move down.
     */
    public void forceMoveDown(int destination) throws InterruptedException {
        state = new MovingDownState(this);
        if (destination >= currentFloor) {
            System.out.println("Elevator cannot move down from " + currentFloor + " to " + destination
                    + ". Move down destination must be lower than current floor.");
            return;
        }
        int floorDifference = currentFloor - destination;
        for (int i = 0; i < floorDifference; i++) {
            forceDecrementFloor();
            //todo communicate this.arrivalSignal to scheduler. wait to hear back about whether to stop at this floor
            //todo update this.stopSignal based on communication received back from scheduler
            if (stopSignal) { // if there is a stop signal, stop the elevator at current floor
                state = new IdleState(this); // transition to idle state with open door to load/unload users
                System.out.println("\nElevator has stopped at " + currentFloor + ", following scheduler's stop signal.");
                stopSignal = false; // reset stop signal
                break;
            }
        }
        this.currentFloor = destination;
        this.arrivalSignal = destination;
        //state.handleApproachingFloor();
        System.out.print("****************************************************************************\n"
                + "ELEVATOR EXECUTING SCHEDULER COMMAND TO MOVE DOWN...\n"
                + "CURRENT ELEVATOR STATE: " + state + "     CURRENT FLOOR: " + currentFloor + "\n"
                + "****************************************************************************\n");
    }

    /**
     * Sends move request to the scheduler via UDP communication.
     */
    public void sendMoveRequest() {
        // todo send move request message to scheduler
    }

    /**
     * Force opens the elevator door.
     */
    public void forceOpenDoor() throws InterruptedException {
        state = new IdleState(this);
        System.out.print("****************************************************************************\n"
                + "ELEVATOR EXECUTING SCHEDULER COMMAND TO OPEN DOOR...\n"
                + "CURRENT ELEVATOR STATE: " + state + "     CURRENT FLOOR: " + currentFloor
                + "     DOOR OPEN: " + doorOpen + "\n"
                + "****************************************************************************\n");
    }

    /**
     * Force closes the elevator door.
     */
    public void forceCloseDoor() {
        state = new StoppedState(this);
        System.out.print("****************************************************************************\n"
                + "ELEVATOR EXECUTING SCHEDULER COMMAND TO CLOSE DOOR...\n"
                + "CURRENT ELEVATOR STATE: " + state + "     CURRENT FLOOR: " + currentFloor
                + "     DOOR OPEN: " + doorOpen + "\n"
                + "****************************************************************************\n");
    }

    /**
     * Attempts to make the elevator stop at approaching floor.
     */
    public void stop() {
        this.stopSignal = true;
        state.handleStopping();
    }

    /**
     * Attempts to make the elevator open its door.
     */
    public void openDoor() throws InterruptedException {
        state.handleOpeningDoor();
    }

    /**
     * Attempts to make the elevator close its door.
     */
    public void closeDoor() {
        state.handleClosingDoor();
    }

    /**
     * Turns on the elevator lamp for given floor number
     * by adding floorNumber to set of lamps.
     */
    public void switchLampOn(int floorNumber) {
        lampSet.add(floorNumber);
    }

    /**
     * Turns off the elevator lamp for given floor number
     * by removing floorNumber from set of lamps.
     */
    public void switchLampOff(int floorNumber) {
        lampSet.remove(floorNumber);
    }

    /**
     * Getter for doorOpen. Returns true when elevator door is open.
     * @return doorOpen
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Getter for cartStationary. Returns true when elevator is stationary.
     * @return cartStationary
     */
    public boolean isStationary() {
        return cartStationary;
    }

    /**
     * getter for the current floor the elevator is on.
     * @return currentFloor
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Getter for elevator's current state.
     * @return state
     */
    public ElevatorState getState() {
        return this.state;
    }

    /**
     * Adds floorNumber to set of buttons pressed inside elevator,
     * and to the set of switched-on destination lamps.
     * The scheduler will switchLampOff() once the elevator stops at that floor.
     */
    public void pressDestinationButton(int destinationFloor) {
        buttonSet.add(destinationFloor);
        lampSet.add(destinationFloor);
        elevatorEvents.add(createElevatorEvent(destinationFloor));
    }

    /**
     * Creates a FloorEvent object for communication to the scheduler, following each button press from inside the elevator.
     */
    public FloorEvent createElevatorEvent(int destinationFloor) {
        FloorEvent.FloorButton directionButton;
        if (destinationFloor >= arrivalSignal) {
            directionButton = FloorEvent.FloorButton.UP;
        }
        else {
            directionButton = FloorEvent.FloorButton.DOWN;
        }
        FloorEvent event = new FloorEvent("10:00:00.000", arrivalSignal, directionButton, destinationFloor);
        return event;
    }

    /**
     * Setter for doorOpen.
     * @param doorStatus
     */
    protected void setDoorOpen(boolean doorStatus) {
        this.doorOpen = doorStatus;
    }

    /**
     * Setter for cartStationary.
     * @param cartStationary
     */
    protected void setCartStationary(boolean cartStationary) {
        this.cartStationary = cartStationary;
    }

    /**
     * Setter for movingDirection. Possible directions are: up, down, none.
     * @param
     */
    protected void setMovingDirection(Direction movingDirection) {
        this.movingDirection = movingDirection;
    }

    /**
     * Setter arrivalSignal. arrivalSignal indicates the floor number that the elevator is approaching.
     * @param
     */
    public void setArrivalSignal(int arrivalSignal) {
        this.arrivalSignal = arrivalSignal;
    }

    /**
     * Getter for arrivalSignal. Returns the floor number that the elevator is approaching.
     * @return arrivalSignal
     */
    public Integer getArrivalSignal() {
        return arrivalSignal;
    }

    /**
     * Increments floors one by one and signals arrival to each floor.
     */
    public void incrementFloor() throws InterruptedException {
        int topFloor= floors.size();
        int i = currentFloor;
        sleep(2000);
        if (i < topFloor) {
            i++;
            arrivalSignal = i;
            currentFloor = i;
        }
        state.handleApproachingFloor();
    }

    /**
     * Increments floors one by one and signals arrival to each floor.
     */
    public void forceIncrementFloor() {
        int topFloor= floors.size();
        int i = currentFloor;
        if (i < topFloor) {
            i++;
            currentFloor = i;
            arrivalSignal = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }


    /**
     * decrements floors one by one and signals arrival to each floor.
     */
    public void decrementFloor() throws InterruptedException {
        int bottomFloor = 1;
        int i = currentFloor;
        sleep(2000);
        if (i > bottomFloor) {
            i--;
            arrivalSignal = i;
            currentFloor = i;
        }
        state.handleApproachingFloor();
    }

    /**
     * Decrements floors by one level only and signals arrival to each floor.
     */
    public void forceDecrementFloor() throws InterruptedException {
        int bottomFloor = 1;
        int i = currentFloor;
        if (i > bottomFloor) {
            i--;
            arrivalSignal = i;
            currentFloor = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }

    /**
     * Getter for the stopSignal.
     */
    public boolean getStopSignal() {
        return stopSignal;
    }

    /**
     * Setter for stopSignal. This signal is set to true internally following scheduler's command to stop() the elevator.
     * @param signal
     */
    public void setStopSignal(boolean signal) {
        this.stopSignal = signal;
    }

    /**
     * Getter for elevator's movingDirection (up, down, or none).
     * @return
     */
    public Direction getMovingDirection() {
        return movingDirection;
    }

    /**
     * Getter for elevatorEvents (list of events corresponding to button presses from inside the elevator).
     * @return
     */
    public List<FloorEvent> getElevatorEvents() {
        return elevatorEvents;
    }

    public static void main(String[] args) throws InterruptedException {
/*        ElevatorStateMachine e = new ElevatorStateMachine();
        e.pressDestinationButton(2);
        e.pressDestinationButton(5);
        e.pressDestinationButton(1);*/

//*****************************************************************************************************
        /**
         * Testing longest transition path
         */
/*        e.closeDoor();  // Idle -> Stopped
        e.moveUp();     // Stopped -> MovingUp -> ApproachingFloor -> ApproachingFloor
        e.stop();       // ApproachingFloor -> Stopped
        e.moveDown();   // Stopped -> MovingDown -> ApproachingFloor -> ApproachingFloor
        e.stop();       // MovingDown -> Stopped
        e.openDoor();   // Stopped -> Idle*/
    }
}
