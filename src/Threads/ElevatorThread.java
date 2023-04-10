package Threads;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ElevatorThread implements the elevator state machine. The states are IDLE, STOPPED, MOVING_UP, MOVING_DOWN.
 *
 * @author  Mahtab Ameli
 * @version Iteration 5
 */
public class ElevatorThread extends Thread {
    public enum ElevatorState {
        IDLE,
        MOVING_UP,
        MOVING_DOWN,
    }

    private int portNumber;             // Elevator's port number for receiving UDP communication
    private ElevatorState state;        // Elevator's current state
    private boolean doorOpen;           // true if door is open, false if closed
    private int currentFloor;           // Elevator's current floor as signalled by the arrival sensor
    private List<Integer> floors;       // list of 5 floors that have access to the elevator
    private boolean stopSignal;         // signal set to true when scheduler makes a command to stop at approaching floor
    private int destination = 0;        // destination floor requested by the scheduler
    private DatagramSocket sendReceiveSocket;         // Datagram socket for sending and receiving UDP communication to/from the scheduler thread
    private DatagramSocket timedSocket;
    private DatagramPacket receiveTimedPacket;
    private final int LOAD_UNLOAD_TIME = 3000;
    private final int TIMEOUT = 5000;
    private final int NUMBER_OF_FLOORS = 22;
    private volatile boolean timedOut, running;


    /**
     * Constructor for the class.
     */
    public ElevatorThread(int portNumber) {
        this.portNumber = portNumber;
        this.doorOpen = true;   // the elevator door is initially open
        this.currentFloor = 1;  // elevator starts at floor #1
        this.floors = new ArrayList<>();
        this.stopSignal = false;
        this.state = ElevatorState.IDLE;
        this.timedOut = false;
        this.running  = true;
        this.populateFloors();
        // Create a Datagram socket for both sending and receiving messages via UDP communication
        try {
            //sendReceiveSocket = new DatagramSocket(portNumber); //todo uncomment
            sendReceiveSocket = new DatagramSocket();
            timedSocket = new DatagramSocket();
        } catch (SocketException se) {   // if socket creation fails
            se.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Populates the list of floors that the elevator will move between.
     */
    private void populateFloors() {
        floors.clear();
        for (int i = 0; i < NUMBER_OF_FLOORS; i++) {
            floors.add(i);
        }
    }

    /**
     * Increments floors one by one updates arrivalSignal after reaching new floor.
     */
    private void incrementFloor() {
        int topFloor = floors.size();
        int i = currentFloor;
        if (i < topFloor) {
            i++;
            currentFloor = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }

    /**
     * Decrements floors one by one updates arrivalSignal after reaching new floor.
     */
    private void decrementFloor(){
        int bottomFloor = 1;
        int i = currentFloor;
        if (i > bottomFloor) {
            i--;
            currentFloor = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }

    /**
     * Creates and returns a Datagram Packet using given input parameters.
     */
    private DatagramPacket createMessagePacket(byte typeByte, int floorNumber) throws UnknownHostException {
        byte[] messageTypeBytes = new byte[] {0x0, typeByte, 0x0};
        byte[] floorNumberBytes = new byte[] {(byte) (floorNumber & 0xFF)};
        //ByteBuffer bb = ByteBuffer.allocate(messageTypeBytes.length + floorNumberBytes.length + sentenceBytes.length);
        ByteBuffer bb = ByteBuffer.allocate(messageTypeBytes.length + floorNumberBytes.length);
        //make read request
        bb.put(messageTypeBytes);
        bb.put(floorNumberBytes);
        byte[] message = bb.array();
        InetAddress schedulerAddress = InetAddress.getByName("localhost");
        int schedulerPort = 1003;
        DatagramPacket sendPacket = new DatagramPacket(message, message.length, schedulerAddress, schedulerPort);
        return sendPacket;
    }


    /**
     * Waits to receive a DatagramPacket on sendReceiveSocket.
     *
     * @return receivePacket A packet sent from the elevator or floor
     */
    private DatagramPacket receivePacket(){
        DatagramPacket receivePacket;
        // Create a DatagramPacket to receive data from client
        byte[] receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            sendReceiveSocket.receive(receivePacket); //Receive from anywhere
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return receivePacket;
    }


    /**
     * author: Ahmad
     * @param timeout
     * @throws SocketTimeoutException
     */
    private synchronized DatagramPacket receivePacketWithTimeout(int timeout) {
        // Wait for incoming Datagram packet with a timeout
        byte[] data = new byte[1024];

        receiveTimedPacket = new DatagramPacket(data, data.length);
        try {
            timedSocket.setSoTimeout(TIMEOUT); // Timeout for socket in milliseconds
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

        try {
            // Block until a packet is received via TimedSocket.
            timedSocket.receive(receiveTimedPacket);

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            timedOut = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiveTimedPacket;
    }


    /**
     * Process byte array message from scheduler containing stop signal.
     * @param stopSignalBytes
     */
    private boolean processStopSignalMessage (byte[] stopSignalBytes){
        //int signal = Integer.valueOf(stopSignalMessage);
        int signal = byteArrayToInt(stopSignalBytes);
        if (signal == 0) { // if signal is 0, return true.
            return true;
        }
        return false;
    }


    /**
     * Process string message from scheduler containing destination floor number for elevator to move to.
     * @param destFloorBytes
     */
    private void processDestinationFloorMessage (byte[] destFloorBytes) {
        //this.destination = Integer.valueOf(String.valueOf(destFloorBytes));
        this.destination = byteArrayToInt(destFloorBytes);
        if (destination > NUMBER_OF_FLOORS) {destination = NUMBER_OF_FLOORS;}
        else if (destination < 0) {destination = 0;}
        if (destination == currentFloor) {
            System.out.println("The elevator is already at destination floor " + destination + ".");
            state = ElevatorState.IDLE;
        } else if (destination > currentFloor) {
            System.out.println("Initiating move up from floor " + currentFloor + " to " + destination + "...");
            state = ElevatorState.MOVING_UP;
        } else if (destination < currentFloor) {
            System.out.println("Initiating move down from floor " + currentFloor + " to " + destination + "...");
            state = ElevatorState.MOVING_DOWN;
        } else {
            destination = -1; // invalid destination
            state = ElevatorState.IDLE;
        }
    }

    /**
     * Converts array of bytes into int.
     * @param byteArray
     * @return
     */
    public static int byteArrayToInt(byte[] byteArray) {
        int result = 0;
        for (int i = 0; i < byteArray.length; i++) {
            result |= (byteArray[i] & 0xFF) << (8 * i);
        }
        return result;
    }

    /**
     * Makes elevator wait a constant amount of time for loading/unloading passengers.
     */
    private void loadUnload() {
        try {
            Thread.sleep(LOAD_UNLOAD_TIME); // elevator door stays open for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles stops requested by the scheduler. Communicates to the scheduler when the stop is completed successfully.
     */
    private void handleStopping() {
        this.doorOpen = true; // open elevator door for load/unload
        System.out.println("\nElevator has stopped at floor " + currentFloor + ".");
        System.out.println("Initiating load/unload...");
        loadUnload();
        this.doorOpen = false; // close door after load/unload is finished
        System.out.println("End of load/unload. Closing elevator door.");
        // send a message to scheduler communicating that the door was open and closed
        try {
            DatagramPacket doorClosedSendPacket = createMessagePacket((byte) 0x04, currentFloor); //create STOP_FINISHED message
            sendReceiveSocket.send(doorClosedSendPacket); // send STOP_FINISHED message to scheduler
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopSignal = false; // reset stop signal to false
    }


    /**
     * ElevatorThread's run() method.
     */

    @Override
    public void run() {

        while (running) {

            switch (state) {

                /**
                 * Elevator state: IDLE
                 */
                case IDLE:

                    // Send move request to the scheduler and wait to hear back on destination floor
                    System.out.println("Elevator State: IDLE");
                    try {
                        // Create move request datagram packet
                        DatagramPacket moveRequestPacket = createMessagePacket((byte) 0x03, currentFloor);
                        // Send message to scheduler
                        sendReceiveSocket.send(moveRequestPacket);
                        // Wait for a response from the scheduler for the destination floor to move to
                        DatagramPacket moveRequestReceivePacket = receivePacket();
                        System.out.println("Scheduler's response back to elevator's move request: " + byteArrayToInt(moveRequestReceivePacket.getData()));
                        this.processDestinationFloorMessage(moveRequestReceivePacket.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


                /**
                 * Elevator state: MOVING_UP
                 */

                case MOVING_UP:

                    System.out.println("Elevator State: MOVING UP");
                    int floorDifference = destination - currentFloor;
                    // move up to the destination floor one floor at a time
                    for (int i = 0; i < floorDifference; i++) {
                        if (i == NUMBER_OF_FLOORS) {
                            break;
                        }
                        incrementFloor(); // go up 1 floor
                        try {
                            DatagramPacket arriveUpSignalPacket = this.createMessagePacket((byte) 0x01, currentFloor);
                            timedSocket.send(arriveUpSignalPacket); // Send ARRIVAL_SENSOR message to scheduler
                            DatagramPacket arriveUpReceivePacket = receivePacketWithTimeout(TIMEOUT); // Wait for a response from the scheduler on whether to stop at this floor
                            if (timedOut) { // socket timed out while waiting for stop signal message from scheduler
                                running = false;
                                System.out.println("Elevator's receive socket timed out while waiting for scheduler's command. Stopping elevatorThread.");
                                Thread.currentThread().interrupt();
                                break;
                            }
                            System.out.println("Scheduler response to arrival sensor (0 = stop, 1 = continue): " + arriveUpReceivePacket.getData()[0]);
                            stopSignal = processStopSignalMessage(arriveUpSignalPacket.getData());
                            if (stopSignal) { // if scheduler requested stop at this floor
                                handleStopping();
                            }
                        }
                        catch (IOException e) {e.printStackTrace();}
                    }
                    handleStopping();
                    state = ElevatorState.IDLE;
                    break;


                /**
                 * Elevator state: MOVING_DOWN
                 */

                case MOVING_DOWN:

                    System.out.println("Elevator State: MOVING DOWN");
                    floorDifference = currentFloor - destination;
                    // move down to the destination floor one floor at a time
                    for (int i = 0; i < floorDifference; i++) {
                        int bottomFloor = 0;
                        if (i == bottomFloor) {
                            break;
                        }
                        decrementFloor(); // go down 1 floor
                        try {
                            DatagramPacket arriveDownSignalPacket = this.createMessagePacket((byte) 0x01, currentFloor);
                            timedSocket.send(arriveDownSignalPacket); // Send ARRIVAL_SENSOR message to scheduler
                            DatagramPacket arriveDownReceivePacket = receivePacketWithTimeout(TIMEOUT); // Wait for a response from the scheduler on whether to stop at this floor
                            if (timedOut) { // socket timed out while waiting for stop signal message from scheduler
                                running = false;
                                System.out.println("Elevator's receive socket timed out while waiting for scheduler's command. Stopping elevatorThread.");
                                Thread.currentThread().interrupt(); // stop this thread in a thread-safe way
                                break;
                            }
                            System.out.println("Scheduler response to arrival sensor (0 = stop, 1 = continue): " + arriveDownReceivePacket.getData()[0]);
                            stopSignal = processStopSignalMessage(arriveDownSignalPacket.getData());
                            if (stopSignal) { // if scheduler requested stop at this floor
                                handleStopping();
                            }
                        }
                        catch (IOException e) {e.printStackTrace();}
                    }
                    handleStopping();
                    state = ElevatorState.IDLE;
                    break;
            }
        }
    }
}