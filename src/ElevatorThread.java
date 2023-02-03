/**
 * The Elevator class simulates an elevator which has buttons and lamps inside of the elevator used to select floors and
 * indicate the floors selected, and to indicate the location of the elevator itself.
 * The elevator also  communicates with floors through a scheduler to allow the elevator to go to a requested destination.
 *
 * @author  Justin Winford
 * @version Iteration 1
 * @since   2023-02-03
 */
public class ElevatorThread extends Thread{

    private ElevatorBuffer elevatorBuffer;  // buffer for the scheduler and elevator data

    private boolean isRightElevator;   // holds if the buffer has work for the current elevator

    private int ElevatorNum;    // holds which elevator this currently is

    /**
     * Constructor for the Elevator class
     *
     * @param elevatorBuffer buffer for the scheduler and elevator to communicate the floor events
     * @param ElevatorNum the current elevators number
     */
    public ElevatorThread(ElevatorBuffer elevatorBuffer, int ElevatorNum){
        super("Elevator");
        this.elevatorBuffer = elevatorBuffer;
        this.ElevatorNum = ElevatorNum;
    }

    /**
     * The elevator will make calls to the Scheduler. Once there is work to be done the elevator accepts or declines
     * the call. The Elevator will then send the data back to the Scheduler.
     */
    @Override
    public void run(){

        while(true){

            System.out.println("Elevator" + ElevatorNum + " is checking if any floor button has been pressed.");

            // Check if there is anything in the buffer
            if(elevatorBuffer.getContentsOfBuffer().size() > 0 ){
                System.out.println("Elevator" + ElevatorNum + " has found work.");
            }else{
                System.out.println("Elevator" + ElevatorNum + "has found no work.");
            }

            // Check if the work is meant for this specific elevator.
            for(int i = 0; i > elevatorBuffer.getContentsOfBuffer().size(); i++){
                if(elevatorBuffer.getContentsOfBuffer().size() > 0 &&
                        elevatorBuffer.getContentsOfBuffer().get(i).getFloorNumber() == ElevatorNum){
                    System.out.println("Elevator" + ElevatorNum + "has found work on floor" + elevatorBuffer.getContentsOfBuffer().get(i).getElevatorNum());
                    isRightElevator = true;
                }else{
                    System.out.println("Scheduler is not looking for Elevator" + ElevatorNum);
                    isRightElevator = false;
                }

                // Retrieves the floor the elevator must go to and sends the info back to the buffer.
                if(isRightElevator){
                    FloorEvent destination =  elevatorBuffer.take(i);
                    System.out.println("Elevator" + ElevatorNum + "is going to floor" + destination.getCarButton());
                    elevatorBuffer.put(destination);
                }

                try{
                    Thread.sleep(500);
                }catch(InterruptedException e){}
            }


        }

    }
}

