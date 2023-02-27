package ElevatorStateMachine;

/**
 * ElevatorState represents parent state class for the elevator state machine, based on the State design pattern.
 * The five concrete states extending this class are: IdleState, StoppedState, MovingUpState, MovingDownState, ApproachingFloorState
 *
 * @author  Mahtab Ameli
 * @version Iteration 2
 */
public class ElevatorState {
    protected ElevatorStateMachine context;
    public ElevatorState(ElevatorStateMachine elevator) {
        this.context = elevator;
    }
    public ElevatorState() {}
    public void handleMovingDown() throws InterruptedException {}
    public void handleMovingUp() throws InterruptedException {}
    public void handleStopping(){}
    public void handleOpeningDoor() {}
    public void handleClosingDoor() {}
    public void handleApproachingFloor() throws InterruptedException {}

}
