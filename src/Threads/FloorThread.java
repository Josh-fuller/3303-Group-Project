package Threads;

import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Class Threads.FloorThread represents the floor subsystem of the elevator scheduling system which communicates with
 * the scheduler via UDP.
 *
 * @author Mahtab
 * @author Ahmad
 * @author Josh
 * @author Justin
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

    // IDLE is the default floor status
    FloorStatus status = FloorStatus.IDLE;

    public enum FloorStatus {
        IDLE,
        PROCESSING_STARTING_STOP,
        PROCESSING_COMPLETED_STOP,
    }

    // Flag used for fault handling in run()
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



    /**
     * Populate floorEventList with input from Floor_Input text file.
     *
     */
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



    /**
     * Create a Threads.FloorEvent from a line of information retrieved from "Floor_Input.txt".
     *
     * @param inputLine is a line from "Floor_Input.txt" containing data of a FloorEvent
     * @return event
     */
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

        // Cast each string input to the corresponding parameter type of Threads.FloorEvent.
        // Create an object of Threads.FloorEvent.
        FloorEvent event = new FloorEvent(timeInput,
                Integer.parseInt(floorNumberInput),
                FloorEvent.FloorButton.valueOf(floorButtonInput),
                Integer.parseInt(carButtonInput), Integer.parseInt(elevatorNumInput));

        return event;
    }

    /**
     * Generate a byte array message from a floor event to prepare it for transfer to scheduler via a
     * datagram packet.
     *
     * @return bMsg
     */
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


    /**
     * Method for receiving datagram packets from scheduler via sendReceiveSocket.
     *
     * @return receivedPacket
     */
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


    /**
     * Method for sending datagram packets to scheduler via sendReceiveSocket.
     *
     * @param bMsg is the byte array message being sent
     * @return receivedPacket
     */
    public void sendPacket(byte[] bMsg) {
        // Create the Datagram packet
        try {
            sendPacket = new DatagramPacket(bMsg, bMsg.length,
                    InetAddress.getLocalHost(), schedulerPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Send the Datagram packet to the scheduler
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * This method is used to detect a fault in the elevator's door. It implements a socket with timeout that's used
     * to detect if the door is open for too long, thus indicating a fault. If a packet is received before the
     * specified timeout period, then we know everything is functioning as expected. However, if no packet is
     * received within the set timeout period, it indicates there is a fault, then the method throws a
     * SocketTimeoutException, which gets caught and handled accordingly in the runnable part below.
     *
     * @param timeout is the period we wish to set as the maximum time the method shall wait for
     * @throws SocketTimeoutException
     */
    public synchronized void waitForPacketWithTimeout(int timeout) throws SocketTimeoutException{

        byte[] data = new byte[1024];
        receiveTimedPacket = new DatagramPacket(data, data.length);

        try {
            // Build a Datagram socket and associate it with
            // an available port so that it can
            // receive UDP Datagrams and have a timeout.
            TimedSocket = new DatagramSocket(2530);

            TimedSocket.setSoTimeout(timeout); // Timeout for socket in milliseconds

        } catch (SocketException se) {   // In case a socket can't be created.
            se.printStackTrace();
            System.exit(1);
        }

        try {
            // Block until a packet is received within the specified timeout or until the timer runs out.
            TimedSocket.receive(receiveTimedPacket);

        } catch (IOException e) {
            // we throw a new socketTimeout exception to indicate the socket timer ran out.
            throw new SocketTimeoutException();
        }
    }

    /**
     * Parses the received messages for its type; which is then used for the switch statements in the runnable
     * part below.
     *
     * @param byteArray the received packet passed to the method as an argument
     * @return type
     */
    public static MsgType parseByteArrayForType(byte[] byteArray) {

        MsgType type = MsgType.ERROR; // default value

        // check first two bytes
        if (byteArray.length >= 1 && byteArray[0] == 0x0 && byteArray[1] == 0x5) {
            type = MsgType.STARTING_STOP; // start timer

        } else if (byteArray.length >= 1 && byteArray[0] == 0x0 && byteArray[1] == 0x6) {
            type = MsgType.COMPLETED_STOP; // stop timer
        }

        return type;
    }


    /**
     * The following three methods are used to set the "status" based on the message received from scheduler,
     * so that we can proceed accordingly in the runnable part below.
     *
     */
    private void idleStatus() {status = FloorStatus.IDLE;}

    public void handleStaringStopStatus() {status = FloorStatus.PROCESSING_STARTING_STOP;}

    public void handleCompletedStopStatus() {status = FloorStatus.PROCESSING_COMPLETED_STOP;}


    /**
     * This is the runnable portion that handles all the delegation to methods instantiated above.
     */
    public void run() {

        while (!timedOut) {

            switch(status) {

                case IDLE:
                    // Send FloorEvents to scheduler.
                    this.sendPacket(buildFloorByteMsg());

                    // Receive a message from scheduler and parse it for message type.
                    receivePacket = receivePacket();
                    MsgType messageType = parseByteArrayForType(receivePacket.getData());

                    // TODO change to "Elevator Stopped" because it handles both.
                    // Based on message type, go to status.
                    if (messageType == MsgType.STARTING_STOP) {
                        handleStaringStopStatus();

                        // TODO I might delete this cuz it's not used; the [06] is now sent to a specific socket with timeout.
                    } else if (messageType == MsgType.COMPLETED_STOP) {
                        handleCompletedStopStatus();

                    } else if (messageType == MsgType.ERROR) {
                        System.out.println("ERROR DETECTED IN SCHEDULER MESSAGE");

                    } break;


                // TODO Change to "PROCESSING_STOP" because it handles both "starting" and "completed" stop.
                case PROCESSING_STARTING_STOP: //Scheduler tells floor elevator has stopped, floor starts timer
                    try {
                        // The called method will wait for 8 seconds to receive a packet from scheduler, otherwise
                        // an exception will be thrown, implying there is a fault and the door didn't close.
                        System.out.println("Elevator is coming to a stop, starting timer.");
                        waitForPacketWithTimeout(8*1000);
                        System.out.println("Door closed on time, timer stopped :)");

                    } catch (SocketTimeoutException e) {
                        // Socket timeout exception is caught, an error statement is printed,
                        // timedOut flag is set to true and the floorThread is halted.
                        System.out.println("ERROR: Timer ran out while waiting to receive a packet from scheduler!");
                        timedOut = true;
                        break;
                    }
                    // go back to idle status
                    idleStatus();
                    break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}


        }
    }
}
