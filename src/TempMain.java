import java.io.IOException;

/**
 * Temporary Main class to test run communication between FloorThread temporary scheduler thread (TempSchedulerThread).
 *
 * @author Mahtb Ameli
 */
public class TempMain {
    public static void main(String[] args) throws IOException {

        FloorEventBuffer floorEventBuffer = new FloorEventBuffer();

        Thread scheduler = new TempSchedulerThread(floorEventBuffer);
        Thread floor = new FloorThread(floorEventBuffer);

        floor.start();
        scheduler.start();
    }
}
