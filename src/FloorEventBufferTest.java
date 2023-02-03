import java.io.IOException;

/** *
 * Class FloorEventBufferTest tests communication between the floor thread (producer) and the scheduler thread (consumer).
 *
 * @author Mahtab Ameli
 */
class FloorEventBufferTest {

    public static void main(String[] args) throws IOException {
        FloorEventBuffer buffer;
        Thread floor, scheduler;

        buffer = new FloorEventBuffer();
        scheduler = new TempSchedulerThread(buffer);
        floor = new FloorThread(buffer);
        floor.start();
        scheduler.start();
    }

}