package ElevatorStateMachine;

public class ApproachingFloorState extends ElevatorState {
    public ApproachingFloorState(ElevatorStateMachine elevator) {
        context = elevator;
        context.setDoorOpen(false);
        context.setCartStationary(true);
    }

    @Override
    /**
     * 1 of 2 valid requests: (ApproachingFloor -> ApproachingFloor)
     *
     */
    public void handleApproachingFloor() throws InterruptedException {

            System.out.println("***** From " + context.getState() + "State.handleApproachingFloor() *****");
            System.out.println("... Approaching next floor ... ");
            System.out.println("Floor #" + context.getArrivalSignal());
            context.setState(new ApproachingFloorState(context));

            System.out.println("Transitioned back to:\t" + context.getState());
            System.out.println("**************************************************************");

            if ((!context.getStopSignal())) {
                if ((context.getArrivalSignal() < 5) &&
                        (context.getMovingDirection().equals(ElevatorStateMachine.Direction.UP))) {
                    context.incrementFloor();
                }
                else if ( (context.getArrivalSignal() > 1) &&
                        (context.getMovingDirection().equals(ElevatorStateMachine.Direction.DOWN))) {
                    context.decrementFloor();
                }
            }
    }

    @Override
    /**
     * 2 of 2 valid requests: (ApproachingFloor -> Stopped)
     */
    public void handleStopping() {
        if (context.getStopSignal()) {
            System.out.println("***** From " + context.getState() + "State.handleStopping() *****");
            System.out.println("... Elevator stopping ...");
            context.setState(new StoppedState(context));
            System.out.println("Transitioned to:\t" + context.getState());
            System.out.println("**************************************************************");
        }
        context.setStopSignal(false);
    }

    public String toString() {
        return "ApproachingFloor";
    }
}
