import java.io.*;
import java.util.ArrayList;

/**
 * Class FloorThread represents the floor subsystem of the elevator scheduling system.
 *
 * @author Mahtab Ameli
 */
public class FloorThread extends Thread {

    private FloorEventBuffer floorEventBuffer; // buffer for holding floorEvent data
    private ArrayList<FloorEvent> floorEventList; // list of FloorEvent objects read from input file

    /**
     * Constructor for the class.
     * @param floorEventBuffer buffer for communicating floor events between the floor and the scheduler.
     * @throws IOException floor data input is read from an input text file: "src/Floor_Input.txt"
     */
    public FloorThread(FloorEventBuffer floorEventBuffer) throws IOException {
        super("FLOOR");
        this.floorEventBuffer = floorEventBuffer;
        this.floorEventList = new ArrayList<>();
        this.populateFloorEventList(); // populate list of floor events from input text file
    }


    // Populate floorEventList with input from Floor_Input text file.
    private void populateFloorEventList() throws FileNotFoundException, IOException {
        // Read the text file containing floor input data.
        File floorInputFile = new File("src/Floor_Input.txt");
        BufferedReader reader = new BufferedReader(new FileReader(floorInputFile));
        String inputLine;
        // Read the text file line by line. Each line contains the information for a separate floorEvent.
        while ((inputLine = reader.readLine()) != null) {
            // Create a FloorEvent object from the information in the inputLine.
            FloorEvent event = createFloorEvent(inputLine);
            floorEventList.add(event);
        }
    }


    // Create a FloorEvent from a line of information retrieved from "Floor_Input.txt".
    private FloorEvent createFloorEvent(String inputLine) {
        // Split the line into words separated by spaces.
        String[] words = inputLine.split("\\s");
        String timeInput, floorNumberInput, floorButtonInput, carButtonInput, elevatorNumInput;

        // Set each input string to its corresponding value based on the order specified in project specs:
        // Time Floor_Number Floor_Button Elevator_Button (hh:mm:ss.mmm n Up/Down n)
        timeInput = words[0]; floorNumberInput = words[1]; floorButtonInput = words[2]; carButtonInput = words[3]; elevatorNumInput = words[4];

        // Cast each string input to the corresponding parameter type of FloorEvent. Create an object of FloorEvent.
        FloorEvent event = new FloorEvent(timeInput,
                Integer.parseInt(floorNumberInput),
                FloorEvent.FloorButton.valueOf(floorButtonInput),
                Integer.parseInt(carButtonInput), Integer.parseInt(elevatorNumInput));
        //System.out.println(event);

        return event;
    }


    // Put each event from floorEventList in the floorEventBuffer for communication to the scheduler.
    public void run () {
        for (int i = 0; i < floorEventList.size(); i++) {
            FloorEvent currentFloorEvent = floorEventList.get(i);
            floorEventBuffer.putFloorEvent(currentFloorEvent);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }
    }

}
