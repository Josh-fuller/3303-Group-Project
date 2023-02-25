package ElevatorStateMachine;

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
