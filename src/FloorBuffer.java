/**
 * Buffer for communicating floorEvents between FloorThread and SchedulerThread.
 *
 * @author Mahtab Ameli
 */
public class FloorBuffer {

    private static FloorEvent floorEvent = null; // elevator request data input from floor
    private boolean empty = true; // true if buffer is empty


    /**
     * Puts objects of FloorEvent in the buffer for communication between the floor and the scheduler.
     */
    public synchronized void putFloorEvent(FloorEvent floorEvent) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        if (Thread.currentThread().getName() == "FLOOR") {
            System.out.println(Thread.currentThread().getName() + " putting floorEvent data in buffer:");
            this.floorEvent = floorEvent;
        } else {
            System.out.println("FLOOR is not the current thread.");
        }
        empty = false;
        System.out.println(floorEvent);
        notifyAll();
    }


    /**
     * Gets floorEvent data placed by the floor, from the buffer.
     */
    public synchronized FloorEvent getFloorEvent() {
        while (empty || (floorEvent == null)) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        System.out.println("\n" + Thread.currentThread().getName() + " getting data from floor buffer...\n");
        empty = true;
        System.out.println(Thread.currentThread().getName() + " received floor event data:\n" + floorEvent);
        System.out.println("-----------------------------------------------------------------------------");
        notifyAll();
        return floorEvent;
    }

    /**
     * Return true if buffer is empty.
     * @return
     */
    public boolean isEmpty() {
        return empty;
    }

}
