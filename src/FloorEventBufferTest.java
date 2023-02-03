import java.io.IOException;

/** *
 * Class FloorEventBufferTest tests communication between the floor thread (producer)
 * and the scheduler thread (consumer) by creating instances of each.
 *
 * @author Mahtab Ameli
 */
class FloorEventBufferTest {

    public static void main(String[] args) throws IOException {
        // floor thread (producer) and scheduler thread (consumer) represent corresponding subsystems in the elevator system
        Thread floor, scheduler;
        // the buffer used to communicate data between floor and scheduler threads
        FloorEventBuffer buffer;

        buffer = new FloorEventBuffer();
        scheduler = new SchedulerThread(buffer);
        floor = new FloorThread(buffer);
        floor.start();
        scheduler.start();
    }

}