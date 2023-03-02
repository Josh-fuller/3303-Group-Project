package ElevatorStateMachine;

/**
 * IdleState is the starting concrete state of the elevator state machine.
 * At this state the elevator is stationary and the door is open.
 *
 * @author  Mahtab Ameli
 * @version Iteration 2
 */
public class IdleState extends ElevatorState{

    /**
     * Constructor for the class
     * @param elevator the context of the elevator state machine
     */
    public IdleState(ElevatorStateMachine elevator) {
        context = elevator;
        context.setDoorOpen(true);
        context.setCartStationary(true);
        context.setMovingDirection(ElevatorStateMachine.Direction.NONE);
        // invalid floor number for arrival signal since elevator cannot approach a floor when stationary
        context.setArrivalSignal(-10);
    }

    @Override
    /**
     * 1 of 1 valid transitions from this state: (Idle -> Stopped)
     */
    public void handleClosingDoor() {
        System.out.println("***** From " + context.getState() + "State.handleClosingDoor() *****");
        System.out.println("... Closing elevator door ...");
        context.setState(new StoppedState(context));
        System.out.println("Transitioned to:\t" + context.getState());
        System.out.println("**************************************************************");
    }

    @Override
    public void handleOpeningDoor() {
        System.out.println("From " + context.getState() + "State.handleOpeningDoor():");
        System.out.println("Current State: " + context.getState());
        System.out.println("INVALID REQUEST. The door is already open at the Idle state.");
    }

    @Override
    public void handleMovingUp() {
        System.out.println("From " + context.getState() + "State.handleMovingUp():");
        System.out.println("Current State: " + context.getState());
        System.out.println("INVALID REQUEST. Elevator cannot move directly from the Idle state.");
    }

    @Override
    public void handleMovingDown() {
        System.out.println("From " + context.getState() + "State.handleMovingDown():");
        System.out.println("Current State: " + context.getState());
        System.out.println("INVALID REQUEST. Elevator cannot move directly from the Idle state.");
    }

    @Override
    public void handleStopping() {
        System.out.println("From " + context.getState() + " State : handleStopping():");
        System.out.println("Current State: " + context.getState());
        System.out.println("INVALID REQUEST. Elevator is already stationary.");
    }

    public String toString() {
        return "Idle";
    }
}
