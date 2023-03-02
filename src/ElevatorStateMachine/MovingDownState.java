package ElevatorStateMachine;

/**
 * MovingDownState is a concrete state of the elevator state machine.
 * At this state the elevator is moving down the building floors.
 *
 * @author  Mahtab Ameli
 * @version Iteration 2
 */
public class MovingDownState extends ElevatorState{

    /**
     * Constructor for the class.
     * @param elevator
     */
    public MovingDownState(ElevatorStateMachine elevator) {
        context = elevator;
        context.setDoorOpen(false);
        context.setCartStationary(false);
        context.setMovingDirection(ElevatorStateMachine.Direction.DOWN);
        // elevator is not approaching a new floor when set to MovingUp initially
        context.setArrivalSignal(-10);
    }

    @Override
    /**
     * 1 of 1 valid transitions from this state: (MovingUp -> ApproachingFloor)
     *
     */
    public void handleApproachingFloor() throws InterruptedException {
        System.out.println("***** From " + context.getState() + "State.handleApproachingFloor() *****");
        System.out.println("... Approaching next floor ... ");
        System.out.println("Floor #" + context.getArrivalSignal());
        context.setState(new ApproachingFloorState(context));
        System.out.println("Transitioned to:\t" + context.getState());
        System.out.println("**************************************************************");
        if ((!context.getStopSignal()) &&  (context.getArrivalSignal() > 1)) {
            context.decrementFloor();
        }
    }

    public String toString() {
        return "MovingDown";
    }
}
