import java.io.IOException;

/** *
 * Class Scheduler used to translate data between Floors and Elevators.
 *
 * @author Josh Fuller
 */
public class SchedulerThread implements Runnable{

    ElevatorBuffer ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer;
    FloorEvent eventTransferOne;
    FloorEvent eventTransferTwo;

    public SchedulerThread(ElevatorBuffer ePutBuffer, ElevatorBuffer eTakeBuffer, ElevatorBuffer fPutBuffer, ElevatorBuffer fTakeBuffer){

        this.ePutBuffer = ePutBuffer;
        this.eTakeBuffer = eTakeBuffer;
        this.fPutBuffer = fPutBuffer;
        this.fTakeBuffer = fTakeBuffer;
    }


    /** *
     * The runnable portion of scheduler, responsible for acting as the translator from floor/elevator and back
     *
     * @author Josh Fuller
     */
    @Override
    public void run() {
        while(true){

            //take from floor
            eventTransferOne = fPutBuffer.take();
            System.out.println("Scheduling event from floor: " + eventTransferOne.getFloorNumber() + " to floor: "
                    + eventTransferOne.getCarButton() + "(STEP 2)");

            //put in elevator
            eTakeBuffer.put(eventTransferOne);
            System.out.println("(STEP 3)");

            //Take from elevator when available, different event to prove transfer
            eventTransferTwo = ePutBuffer.take();
            System.out.println("(STEP 6)");

            System.out.println("Scheduler processed event from floor: " + eventTransferTwo.getFloorNumber() + " to floor: "
                    + eventTransferTwo.getCarButton());

            //put back in floor
            System.out.println("STEP 7");
            fTakeBuffer.put(eventTransferTwo);


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

        }
    }

    public static void main(String[] args) throws IOException {

        //declare and initialise everything

        ElevatorBuffer ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer;

        Thread elevator, floor, scheduler;

        ePutBuffer = new ElevatorBuffer();
        eTakeBuffer = new ElevatorBuffer();
        fPutBuffer = new ElevatorBuffer();
        fTakeBuffer = new ElevatorBuffer();


        // Create the floor,scheduler and elevator threads,
        // passing each thread a reference to the
        // shared BoundedBuffer object.

        elevator = new Thread(new
                ElevatorThread(ePutBuffer, eTakeBuffer,1),"Elevator 1");
        System.out.println("Elevator Created");

        floor = new Thread(new
                FloorThread(fPutBuffer, fTakeBuffer), "Floor");
        System.out.println("Floor Created");

        scheduler = new Thread(new
                SchedulerThread(ePutBuffer, eTakeBuffer,fPutBuffer,fTakeBuffer), "Scheduler");
        System.out.println("Scheduler Created");


        elevator.start();
        floor.start();
        scheduler.start();

    }
}
