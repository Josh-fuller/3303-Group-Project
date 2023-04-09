package Threads;

import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Class Threads.FloorThread represents the floor subsystem of the elevator scheduling system.
 *
 * @author Mahtab Ameli
 */
public class FloorThread extends Thread {

    private int schedulerPort;
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;
    private DatagramSocket TimedSocket;
    private DatagramPacket receiveTimedPacket;

    private final ArrayList<FloorEvent> floorEventList; // list of Threads.FloorEvent objects read from input file

    private byte[] array;

    public enum MsgType {
        STARTING_STOP,
        COMPLETED_STOP,
        ERROR
    }
//TODO What is the actual starting state?
    FloorStatus status = FloorStatus.PROCESSING_STARTING_STOP;

    public enum FloorStatus {
        IDLE,
        PROCESSING_STARTING_STOP,
        PROCESSING_COMPLETED_STOP,
    }

    boolean timedOut;

    /**
     * Constructor for the class.
     *
     * @throws IOException floor data input is read from an input text file: "src/Floor_Input.txt"
     */
    public FloorThread() throws IOException {
        super("FLOOR");
        array = new byte[1024];
        schedulerPort = 1003;
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
        File floorInputFile = new File("src/Threads/Floor_Input.txt");
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


    public byte[] buildFloorByteMsg() {
        int arrayCounter = 2;
        array[0] = (byte) 0;
        array[1] = (byte) 2;
        for(int i = 0; i < floorEventList.size();i++){
            array[arrayCounter] = (byte) floorEventList.get(i).getFloorNumber();
            array[arrayCounter + 1] = (byte) floorEventList.get(i).getElevatorButton();
            arrayCounter += 2;
        }
        //System.out.println(Arrays.toString(array));
        byte[] bMsg = array;
        return bMsg;
    }

    // Receive a Datagram packet on the sendReceive socket
    // and print out the packets details
    private DatagramPacket receivePacket() {
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

        return receivePacket;


    }

    public void sendPacket(byte[] bMsg) {
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

    public synchronized void waitForPacketWithTimeout(int timeout) throws SocketTimeoutException{
        // Wait for incoming Datagram packet with a timeout
        byte[] data = new byte[1024];

        receiveTimedPacket = new DatagramPacket(data, data.length);
        try {
            // Build a Datagram socket and associate it with
            // an available port so that it can
            // receive UDP Datagrams and have a timeout.
            TimedSocket = new DatagramSocket(2530);
            TimedSocket.setSoTimeout(timeout); // Timeout for socket in milliseconds
        } catch (SocketException se) {   // Incase a socket can't be created.
            se.printStackTrace();
            System.exit(1);
        }

        try {
            // Block until a packet is received via TimedSocket.
            TimedSocket.receive(receiveTimedPacket);

        } catch (IOException e) {
            throw new SocketTimeoutException(); // we throw a new socketTimeout exception to indicate
            // the socket timer ran out

        }

    }

    /**
     * Parses through received messages to get their type, for easy switch statement implementation
     *
     * @param byteArray
     * @return
     */
    public static MsgType parseByteArrayForType(byte[] byteArray) {

        MsgType type = MsgType.ERROR; // default value

        // check first two bytes
        if (byteArray.length >= 1 && byteArray[0] == 0x0 && byteArray[1] == 0x5) {
            type = MsgType.STARTING_STOP; // start timer
        } else if (byteArray.length >= 1 && byteArray[0] == 0x0 && byteArray[1] == 0x6) {
            type = MsgType.COMPLETED_STOP; // stop timer
        }


        // find first 0
        int firstZeroIndex = -1;
        for (int i = 2; i < byteArray.length; i++) {
            if (byteArray[i] == 0x0) {
                firstZeroIndex = i;
                break;
            }
        }

        // if no 0 found, set type to error and return
        if (firstZeroIndex == -1) {
            type = MsgType.ERROR;
            return type;
        }
        return type;
    }

    private void idleStatus() {
        status = FloorStatus.IDLE;
    }

    public void handleStaringStopStatus() {
        status = FloorStatus.PROCESSING_STARTING_STOP;
    }

    public void handleCompletedStopStatus() {
        status = FloorStatus.PROCESSING_COMPLETED_STOP;
    }


    // Put each event from floorEventList in the floorEventBuffer for communication to the scheduler.
    public void run() {

        //TODO Where is this line actually supposed to be?
        this.sendPacket(buildFloorByteMsg());

        while (!timedOut) {

            switch(status) {
                case IDLE:
                    receivePacket = receivePacket();

                    MsgType messageType = parseByteArrayForType(receivePacket.getData());

                    //based on message type, go to state
                    if (messageType == MsgType.STARTING_STOP) {
                        handleStaringStopStatus();
                    } else if (messageType == MsgType.COMPLETED_STOP) {
                        handleCompletedStopStatus();
                    } else if (messageType == MsgType.ERROR) {
                        System.out.println("ERROR DETECTED IN SCHEDULER MESSAGE");
                    }
                    break;

                case PROCESSING_STARTING_STOP:
                    try {
                        waitForPacketWithTimeout(8000);
                        // TODO 1 refactor + comments

                    } catch (SocketTimeoutException e) {
                        System.out.println("Timer ran out while waiting to receive a packet from scheduler!");
                        timedOut= true;
                        break;
                        // TODO 2 refactor
                    }
                    idleStatus();
                    break;


                // Not really needed, TODO 3 refactor
                /*case PROCESSING_COMPLETED_STOP:
                    try {
                        sendSocket.send(sendElevatorMovePacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    idleStatus();
                    break;*/
            }
            //TODO What of this is still needed?(sendPacket is above)
            this.sendPacket(buildFloorByteMsg());

            for (int i = 0; i < floorEventList.size(); i++) {
                
                byte[] data = new byte[1024];
                receivePacket = new DatagramPacket(data, data.length);
                try {
                    // Block until a Datagram is received via sendReceiveSocket.
                    sendReceiveSocket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                System.out.println("STEP 8");
                System.out.println("Finished Processing Use #" + i);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}