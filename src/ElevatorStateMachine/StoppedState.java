package ElevatorStateMachine;

/**
 * StoppedState is a concrete state of the elevator state machine.
 * At this state the elevator is stationary and the door is closed.
 *
 * @author  Mahtab Ameli
 * @version Iteration 2
 */
public class StoppedState extends ElevatorState{

    /**
     * Constructor for the class.
     * @param elevator
     */
    public StoppedState(ElevatorStateMachine elevator) {
        context = elevator;
        context.setDoorOpen(false);
        context.setCartStationary(true);
        context.setMovingDirection(ElevatorStateMachine.Direction.NONE);
        // invalid floor number for arrival signal since elevator cannot approach a floor when stationary
        context.setArrivalSignal(-10);
    }

    @Override
    /**
     * 1 of 3 valid transitions from this state: (Stopped -> Idle)
     */
    public void handleOpeningDoor() {
        System.out.println("***** From " + context.getState() + "State.handleOpeningDoor() *****");
        System.out.println("...Opening elevator door...");
        context.setState(new IdleState(context));
        System.out.println("Transitioned to:\t" + context.getState());
        System.out.println("**************************************************************");
    }

    @Override
    /**
     * 2 of 3 valid transitions from this state: (Stopped -> MovingUp)
     */
    public void handleMovingUp() throws InterruptedException {
        System.out.println("***** From " + context.getState() + "State.handleMovingUp() *****");
        System.out.println("... Elevator Moving Up ...");
        context.setState(new MovingUpState(context));
        System.out.println("Transitioned to:\t" + context.getState());
        System.out.println("**************************************************************");
        context.incrementFloor();
    }

    @Override
    /**
     * 3 of 3 valid transitions from this state: (Stopped -> MovingUp)
     */
    public void handleMovingDown() throws InterruptedException {
        System.out.println("***** From " + context.getState() + "State.handleMovingUp() *****");
        System.out.println("... Elevator Moving Down ...");
        context.setState(new MovingDownState(context));
        System.out.println("Transitioned to:\t" + context.getState());
        System.out.println("**************************************************************");
        context.decrementFloor();
    }

    public String toString() {
        return "Stopped";
    }
}
