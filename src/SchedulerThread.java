import java.io.IOException;

/** *
 * Class Scheduler used to translate data between Floors and Elevators.
 *
 * @author Josh Fuller
 */
public class SchedulerThread implements Runnable{

    ElevatorBuffer eBuffer = new ElevatorBuffer();
    ElevatorBuffer fBuffer = new ElevatorBuffer();
    FloorEvent eventTransferOne;
    FloorEvent eventTransferTwo;

    public SchedulerThread(ElevatorBuffer elevator, ElevatorBuffer floorBuffer){

        this.eBuffer = eBuffer;
        this.fBuffer = fBuffer;
    }


    @Override
    public void run() {
        while(true){

            //take from floor
            eventTransferOne = fBuffer.take();
            System.out.println("Scheduling event from floor: " + eventTransferOne.getFloorNumber() + " to floor: "
                    + eventTransferOne.getCarButton());

            //put in elevator
            eBuffer.put(eventTransferOne);

            //Take from elevator when available, different event to prove transfer
            eventTransferTwo = eBuffer.take();

            System.out.println("Scheduler processed event from floor: " + eventTransferTwo.getFloorNumber() + " to floor: "
                    + eventTransferTwo.getCarButton());

            //put back in floor
            fBuffer.put(eventTransferTwo);

        }
    }

    public static void main(String[] args) throws IOException {

        Thread elevator, floor, scheduler;
        ElevatorBuffer ebuffer, fbuffer;
        ebuffer = new ElevatorBuffer();
        fbuffer = new ElevatorBuffer();

        // Create the producer and consumer threads,
        // passing each thread a reference to the
        // shared BoundedBuffer object.

        elevator = new Thread(new
                ElevatorThread(ebuffer, 1),"Elevator 1");
        System.out.println("Elevator Created");

        floor = new Thread(new
                FloorThread(fbuffer), "Floor");
        System.out.println("Floor Created");

        scheduler = new Thread(new
                SchedulerThread(ebuffer, fbuffer), "Scheduler");
        System.out.println("Scheduler Created");


        elevator.start();
        floor.start();
        scheduler.start();

    }
}
