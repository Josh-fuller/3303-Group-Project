package ElevatorStateMachine;

import static java.lang.Thread.sleep;

public class MovingUpState extends ElevatorState{

    public MovingUpState(ElevatorStateMachine elevator) throws InterruptedException {
        context = elevator;
        context.setDoorOpen(false);
        context.setCartStationary(false);
        context.setMovingDirection(ElevatorStateMachine.Direction.UP);
        // elevator is not approaching a new floor when set to MovingUp initially
        context.setArrivalSignal(-10);
    }

    @Override
    /**
     * 1 of 1 valid requests: (MovingUp -> ApproachingFloor)
     *
     */
    public void handleApproachingFloor() throws InterruptedException {
        System.out.println("***** From " + context.getState() + "State.handleApproachingFloor() *****");
        System.out.println("... Approaching next floor ... ");
        System.out.println("Floor #" + context.getArrivalSignal());
        context.setState(new ApproachingFloorState(context));
        System.out.println("Transitioned to:\t" + context.getState());
        System.out.println("**************************************************************");
        if ((!context.getStopSignal()) && (context.getArrivalSignal() < 5)) {
                context.incrementFloor();
        }
    }

    public String toString() {
        return "MovingUp";
    }
}
