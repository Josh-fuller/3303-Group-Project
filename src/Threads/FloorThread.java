package Threads;

import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.net.DatagramPacket;

/**
 * Class Threads.FloorThread represents the floor subsystem of the elevator scheduling system.
 *
 * @author Mahtab Ameli
 */
public class FloorThread extends Thread {

    //private FloorBuffer floorEventBuffer; // buffer for holding floorEvent data

    private final ElevatorBuffer elevatorPutBuffer;
    private final ElevatorBuffer elevatorTakeBuffer;

    private int schedulerPort;
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;

    private final ArrayList<FloorEvent> floorEventList; // list of Threads.FloorEvent objects read from input file

    private byte[] array;

    /**
     * Constructor for the class.
     *
     * @param elevatorPutBuffer,elevatorTakeBuffer buffer for communicating floor events between the floor and the scheduler.
     * @throws IOException floor data input is read from an input text file: "src/Floor_Input.txt"
     */
    public FloorThread(ElevatorBuffer elevatorPutBuffer, ElevatorBuffer elevatorTakeBuffer) throws IOException {
        super("FLOOR");
        this.elevatorPutBuffer = elevatorPutBuffer;
        this.elevatorTakeBuffer = elevatorTakeBuffer;
        this.floorEventList = new ArrayList<>();
        this.populateFloorEventList(); // populate list of floor events from input text file

        try {
            // Build a Datagram socket and associate it with
            // an available socket so that it can both transmit
            // and receive UDP Datagrams.
            sendReceiveSocket = new DatagramSocket(2529);
        } catch (SocketException se) {   // Incase a socket can't be created.
            se.printStackTrace();
            System.exit(1);
        }
    }


    // Populate floorEventList with input from Floor_Input text file.
    private void populateFloorEventList() throws IOException {
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
        timeInput = words[0];
        floorNumberInput = words[1];
        floorButtonInput = words[2];
        carButtonInput = words[3];
        elevatorNumInput = words[4];

        // Cast each string input to the corresponding parameter type of Threads.FloorEvent. Create an object of Threads.FloorEvent.
        FloorEvent event = new FloorEvent(timeInput,
                Integer.parseInt(floorNumberInput),
                FloorEvent.FloorButton.valueOf(floorButtonInput),
                Integer.parseInt(carButtonInput), Integer.parseInt(elevatorNumInput));
        //System.out.println(event);


        return event;
    }


    private byte[] buildFloorByteMsg(ArrayList<FloorEvent> floorEventList) {

        array[0] = (byte) 0;
        array[1] = (byte) 1;
        array[2] = (byte) 3;
        array[3] = (byte) floorEventList.get(3).getFloorNumber();
        array[4] = (byte) 3;
        array[5] = (byte) floorEventList.get(5).getElevatorButton();
        array[6] = (byte) floorEventList.get(6).getElevatorNum();

        byte[] bMsg = array;
        return bMsg;
    }

    // Receive a Datagram packet on the sendReceive socket
    // and print out the packets details
    private void receivePacket() {
        // Wait for incoming Datagram packet
        byte[] data = new byte[1024];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a Datagram is received via sendReceiveSocket.
            sendReceiveSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        int schedulerPort = recievePacket.getPort();
    }

    private void sendPacket(byte[] bMsg) {
        // Create the Datagram packet
        try {
            sendPacket = new DatagramPacket(bMsg, bMsg.length,
                    InetAddress.getLocalHost(), schedulerPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Send the Datagram packet to the server
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }







   /* public byte[] floorEventToByte(FloorEvent event) throws IOException {
        // Serialize to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput object = new ObjectOutputStream(stream);
        object.writeObject(event);
        object.close();

        byte[] serializedMessage = stream.toByteArray();

        return serializedMessage;
    }*/


    // Put each event from floorEventList in the floorEventBuffer for communication to the scheduler.
    public void run() {
        for (int i = 0; i < floorEventList.size(); i++) {
            FloorEvent currentFloorEvent = floorEventList.get(i);

            this.sendPacket(buildFloorByteMsg(floorEventList));
            elevatorTakeBuffer.take();


            byte[] data = new byte[1024];
            receivePacket = new DatagramPacket(data, data.length);
            try {
                // Block until a Datagram is received via sendReceiveSocket.
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            elevatorPutBuffer.put(currentFloorEvent);


            FloorEvent finishedFloorEvent = elevatorTakeBuffer.take();
            System.out.println("STEP 8");
            System.out.println("Finished Processing Use #" + i);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }


    }

    /**
     * Getter for floor event list.
     *
     * @return floorEventList
     */
    public ArrayList<FloorEvent> getFloorEventList() {
        return floorEventList;
    }

}
