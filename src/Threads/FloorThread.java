package Threads;

import java.io.*;
import java.util.ArrayList;

/**
 * Class Threads.FloorThread represents the floor subsystem of the elevator scheduling system.
 *
 * @author Mahtab Ameli
 */
public class FloorThread extends Thread {

    //private FloorBuffer floorEventBuffer; // buffer for holding floorEvent data

    private ElevatorBuffer elevatorPutBuffer;
    private ElevatorBuffer elevatorTakeBuffer;

    private ArrayList<FloorEvent> floorEventList; // list of Threads.FloorEvent objects read from input file

    /**
     * Constructor for the class.
     * @param elevatorPutBuffer,elevatorTakeBuffer buffer for communicating floor events between the floor and the scheduler.
     * @throws IOException floor data input is read from an input text file: "src/Floor_Input.txt"
     */
    public FloorThread(ElevatorBuffer elevatorPutBuffer, ElevatorBuffer elevatorTakeBuffer) throws IOException {
        super("FLOOR");
        this.elevatorPutBuffer = elevatorPutBuffer;
        this.elevatorTakeBuffer = elevatorTakeBuffer;
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
            // Create a Threads.FloorEvent object from the information in the inputLine.
            FloorEvent event = createFloorEvent(inputLine);
            floorEventList.add(event);
        }
    }


    // Create a Threads.FloorEvent from a line of information retrieved from "Floor_Input.txt".
    private FloorEvent createFloorEvent(String inputLine) {
        // Split the line into words separated by spaces.
        String[] words = inputLine.split("\\s");
        String timeInput, floorNumberInput, floorButtonInput, carButtonInput, elevatorNumInput;

        // Set each input string to its corresponding value based on the order specified in project specs:
        // Time Floor_Number Floor_Button Elevator_Button (hh:mm:ss.mmm n Up/Down n)
        timeInput = words[0]; floorNumberInput = words[1]; floorButtonInput = words[2]; carButtonInput = words[3]; elevatorNumInput = words[4];

        // Cast each string input to the corresponding parameter type of Threads.FloorEvent. Create an object of Threads.FloorEvent.
        FloorEvent event = new FloorEvent(timeInput,
                Integer.parseInt(floorNumberInput),
                FloorEvent.FloorButton.valueOf(floorButtonInput),
                Integer.parseInt(carButtonInput), Integer.parseInt(elevatorNumInput));
        //System.out.println(event);

        return event;
    }

    public byte[] floorEventToByte(FloorEvent event) throws IOException {
        // Serialize to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput object = new ObjectOutputStream(stream);
        object.writeObject(event);
        object.close();

        byte[] serializedMessage = stream.toByteArray();

        return serializedMessage;
    }


    // Put each event from floorEventList in the floorEventBuffer for communication to the scheduler.
    public void run () {
        for (int i = 0; i < floorEventList.size(); i++) {

            FloorEvent currentFloorEvent = floorEventList.get(i);

            System.out.println("STEP 1 #" + i);
            elevatorPutBuffer.put(currentFloorEvent);


            FloorEvent finishedFloorEvent = elevatorTakeBuffer.take();
            System.out.println("STEP 8");
            System.out.println("Finished Processing Use #" + i);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }


    }

    /**
     * Getter for floor event list.
     * @return floorEventList
     */
    public ArrayList<FloorEvent> getFloorEventList() {
        return floorEventList;
    }

}
