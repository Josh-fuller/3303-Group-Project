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

    private final int elevatorNum;
    private int portNumber;             // Elevator's port number for receiving UDP communication
    private ElevatorState state;        // Elevator's current state
    private boolean doorOpen;           // true if door is open, false if closed
    private int currentFloor;           // Elevator's current floor as signalled by the arrival sensor
    private List<Integer> floorList;       // list of 5 floors that have access to the elevator
    private boolean stopSignal;         // signal set to true when scheduler makes a command to stop at approaching floor
    private DatagramSocket sendReceiveSocket;         // Datagram socket for sending and receiving UDP communication to/from the scheduler thread
    private DatagramSocket timedSocket;
    private DatagramPacket receiveTimedPacket;
    private final int LOAD_UNLOAD_TIME = 2000;
    private final int FLOOR_TRANSITION_TIME = 1200;
    private final int OPEN_CLOSE_TIME = 300;
    private final int TIMEOUT = 12000;
    private final int NUMBER_OF_FLOORS = 22;
    private volatile boolean timedOut, running;
    private int nextDestination = 0;        // destination floor requested by the scheduler
    private int secondDestination = 0;
    private int thirdDestination = 0;
    private LinkedList<Integer> destinationList;



    /**
     * Constructor for the class.
     */
    public ElevatorThread(int portNumber,int elevatorNum) {
        this.elevatorNum = elevatorNum;
        this.portNumber = portNumber;
        this.doorOpen = true;   // the elevator door is initially open
        this.currentFloor = 1;  // elevator starts at floor #1
        this.floorList = new ArrayList<>();
        this.destinationList = new LinkedList<>();
        this.stopSignal = false;
        this.state = ElevatorState.IDLE;
        this.timedOut = false;
        this.running  = true;
        this.populateFloors();
        // Create a Datagram socket for both sending and receiving messages via UDP communication
        try {
            sendReceiveSocket = new DatagramSocket(portNumber);
            timedSocket = new DatagramSocket();
        } catch (SocketException se) {   // if socket creation fails
            se.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Populates the list of floors that the elevator will move between.
     */
    public void populateFloors() {
        floorList.clear();
        for (int i = 0; i < NUMBER_OF_FLOORS; i++) {
            floorList.add(i+1);
        }
    }


    public int getFloorCount() {
        return floorList.size();
    }

    /**
     * Increments floors one by one updates arrivalSignal after reaching new floor.
     */
    public void incrementFloor() {
        int topFloor = floorList.size();
        int i = currentFloor;
        if (i < topFloor) {
            floorTransitionTime();
            i++;
            currentFloor = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }


    /**
     * Decrements floors one by one updates arrivalSignal after reaching new floor.
     */
    public void decrementFloor(){
        int bottomFloor = 1;
        int i = currentFloor;
        if (i > bottomFloor) {
            floorTransitionTime();
            i--;
            currentFloor = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }

    /**
     * Creates and returns a Datagram Packet using given input parameters.
     */
    public DatagramPacket createMessagePacket(byte typeByte, int floorNumber) throws UnknownHostException {
        byte[] messageTypeBytes = new byte[] {0x0, typeByte, 0x0};
        byte[] floorNumberBytes = new byte[] {(byte) (floorNumber & 0xFF)};
        ByteBuffer bb = ByteBuffer.allocate(messageTypeBytes.length + floorNumberBytes.length);
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
            System.out.println("ELEVATOR RECEIVED DATA WITHIN TIME LIMIT");
        //} catch (SocketTimeoutException s){
            //System.out.println("NO MOVE REQUEST PROVIDED IN TIME IN ELEVATOR: " + portNumber);
        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
        return receivePacket;
    }


    /**
     * author: Ahmad
     * @param timeout
     * @throws SocketTimeoutException
     */
    private synchronized DatagramPacket receivePacketWithTimeout (int timeout) throws SocketTimeoutException {
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

        } catch (IOException e) {
            timedOut = true;
            throw new SocketTimeoutException();
        }

        return receiveTimedPacket;
    }


    /**
     * Process byte array message from scheduler containing stop signal.
     * @param stopSignalBytes
     */
    public boolean processStopSignalMessage (byte[] stopSignalBytes){
        int signal = byteArrayToInt(stopSignalBytes);
        if (signal == 0) { // if signal is 0, return true.
            return false;
        }
        return true;
    }


    /**
     * Process string message from scheduler containing destination floor number for elevator to move to.
     * @param destFloorBytes
     */
    public void processDestinationFloorMessage (byte[] destFloorBytes) {
        int startFloor = destFloorBytes[0];
        int endFloor = destFloorBytes[1];


        if ((startFloor > NUMBER_OF_FLOORS) || (endFloor > NUMBER_OF_FLOORS)) { // invalid destination input from scheduler: floor too high
            System.out.println("INVALID DESTINATION REQUEST: The highest possible destination floor is #22.");
            state = ElevatorThread.ElevatorState.IDLE;
            return;
        }
        int bottomFloor = 0;
        if ((startFloor < bottomFloor) || (endFloor < bottomFloor)) { // invalid destination input from scheduler: floor too low
            System.out.println("INVALID DESTINATION REQUEST: The lowest possible destination floor is #1.");
            state = ElevatorThread.ElevatorState.IDLE;
            return;
        }
        this.nextDestination = startFloor;
        this.secondDestination = endFloor;
        addDestination(nextDestination);
        addDestination(secondDestination); // todo new

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Move Request Response: ");
        System.out.println("1st destination = " + nextDestination + "       2nd destination = " + secondDestination);
        System.out.println("------------------------------------------------------------------------------------");

        if (nextDestination == currentFloor) {
            System.out.println("The elevator is already at destination floor " + nextDestination + ".");
            openDoor();
            loadUnload(); // todo new
            closeDoor();
            state = ElevatorThread.ElevatorState.IDLE;
        } else if (nextDestination > currentFloor) {
            System.out.println("Initiating move up from floor " + currentFloor + " to " + nextDestination + "...");
            state = ElevatorThread.ElevatorState.MOVING_UP;
        } else if (nextDestination < currentFloor) {
            System.out.println("Initiating move down from floor " + currentFloor + " to " + nextDestination + "...");
            state = ElevatorThread.ElevatorState.MOVING_DOWN;
        } else {
            nextDestination = -1; // invalid destination
            state = ElevatorThread.ElevatorState.IDLE;
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


    private void floorTransitionTime() {
        try {
            Thread.sleep(FLOOR_TRANSITION_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openDoor() {
        try {
            Thread.sleep(OPEN_CLOSE_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.doorOpen = true;
    }

    private void closeDoor() {
        try {
            Thread.sleep(OPEN_CLOSE_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.doorOpen = false;
    }

    /**
     * Handles stops requested by the scheduler. Communicates to the scheduler when the stop is completed successfully.
     */
    public void handleStopping() {
        openDoor();
        System.out.println("\nElevator " + portNumber + " has stopped at floor " + currentFloor + ".");
        System.out.println("Initiating load/unload at floor: " + currentFloor + "...");
        loadUnload();
        closeDoor();
        System.out.println("End of load/unload. Closing elevator " + portNumber + "  door.");
        // send a message to scheduler communicating that the door was open and closed
        try {
            DatagramPacket doorClosedSendPacket = createMessagePacket((byte) 0x04, currentFloor); //create STOP_FINISHED message
            sendReceiveSocket.send(doorClosedSendPacket); // send STOP_FINISHED message to scheduler
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopSignal = false; // reset stop signal to false
    }

    public void addDestination(int destination) {
        if (!destinationList.contains(destination)) { // add destination to list if absent
            destinationList.add(destination);
        }
    }


    public void finishLeftoverStops() {
        while(!destinationList.isEmpty()) {

            if (destinationList.getFirst() == currentFloor) {
                destinationList.removeFirst();
                if (destinationList.isEmpty()) {return;}
            }
            int thisDestination = destinationList.getFirst();
            System.out.println("-----------------------------------------");
            System.out.println("leftover stop: " + thisDestination);
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("Elevator " + portNumber + "  : Finishing leftover stop at floor " + thisDestination);
            System.out.println("------------------------------------------------------------------------------------");
            if ((thisDestination > 0) && (thisDestination < 22)) { // if the destination is valid
                // directly go to thisDestination without stopping on the way
                if(currentFloor > thisDestination){
                    while(currentFloor != thisDestination){
                        closeDoor();
                        decrementFloor();

                    }
                }else{
                    while(currentFloor != thisDestination){
                        closeDoor();
                        incrementFloor();
                    }
                }
                handleStopping();
                destinationList.removeFirst();
            }
        }
    }

    public int getCurrentFloor(){
        return currentFloor;
    }

    public int getPortNumber(){
        return portNumber;
    }

    public boolean isDoorOpen(){
        return doorOpen;
    }

    public int getElevatorNum() {
        return elevatorNum;
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
                    System.out.println("Elevator " + portNumber + " State: IDLE");
                    // if there are leftover destinations on the list, go to those first
                    // then send new move request
                    finishLeftoverStops();
                    // Send move request to the scheduler and wait to hear back on destination floor
                    try {
                        // Create move request datagram packet
                        DatagramPacket moveRequestPacket = createMessagePacket((byte) 0x03, currentFloor);
                        // Send message to scheduler
                        sendReceiveSocket.send(moveRequestPacket);
                        // Wait for a response from the scheduler for the destination floor to move to
                        DatagramPacket moveRequestReceivePacket = receivePacket();
                        System.out.println("ELEVATOR " + portNumber + " THINKS IT RECEIVED: " + Arrays.toString(moveRequestReceivePacket.getData()));
                        //System.out.println("Scheduler's response back to elevator's move request: " + (moveRequestReceivePacket.getData()[0] + "," + moveRequestReceivePacket.getData()[1])); //TODO fix this to re-state the message floors
                        this.processDestinationFloorMessage(moveRequestReceivePacket.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                        state = ElevatorState.IDLE;
                    }
                    break;


                /**
                 * Elevator state: MOVING_UP
                 */

                case MOVING_UP:

                    System.out.println("Elevator " + portNumber + " State: MOVING UP");
                    int floorDifference = nextDestination - currentFloor; // floorDifference = the number of times the elevator must increment to reach destination
                    // move up to the destination floor one floor at a time
                    for (int i = 0; i < floorDifference; i++) {
                        if (i == NUMBER_OF_FLOORS) { // if elevator reaches topmost floor
                            break;
                        }
                        closeDoor();
                        incrementFloor(); // go up 1 floor
                        // communicate arriving at new floor to the scheduler and ask if should stop at this floor
                        try {
                            DatagramPacket arriveUpSignalPacket = this.createMessagePacket((byte) 0x01, currentFloor);
                            timedSocket.send(arriveUpSignalPacket); // Send ARRIVAL_SENSOR message to scheduler
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        try {
                            DatagramPacket arriveUpReceivePacket = receivePacketWithTimeout(TIMEOUT); // Wait for a response from the scheduler on whether to stop at this floor
                            stopSignal = processStopSignalMessage(arriveUpReceivePacket.getData());
                            if (stopSignal) { // if scheduler requested stop at this floor, stop the elevator
                                thirdDestination = arriveUpReceivePacket.getData()[0];
                                addDestination(thirdDestination);
                                handleStopping();
                            }
                        } catch (SocketTimeoutException e) {
                            running = false;
                            System.out.println("Elevator " + portNumber + "'s receive socket timed out while waiting for scheduler's command. Stopping elevatorThread.");
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    // Elevator has reached destination.
                    System.out.println("------------------------------------------------------------------------------------");
                    System.out.println("Elevator" + portNumber + " has reached destination floor #" + nextDestination + " .");
                    addDestination(secondDestination);
                    System.out.println("Added 2nd destination floor to the list: #" + secondDestination);
                    System.out.println("------------------------------------------------------------------------------------");
                    handleStopping();
                    state = ElevatorState.IDLE;
                    break;

                /**
                 * Elevator state: MOVING_DOWN
                 */

                case MOVING_DOWN:

                    System.out.println("Elevator " + portNumber + " State: MOVING DOWN");
                    floorDifference = currentFloor - nextDestination; // floorDifference = the number of times the elevator must decrement to reach destination
                    // move down to the destination floor one floor at a time
                    for (int i = 0; i < floorDifference; i++) {
                        int bottomFloor = 1;
                        if (i == bottomFloor) { // if elevator reaches topmost floor
                            break;
                        }
                        closeDoor();
                        decrementFloor(); // go down 1 floor
                        // communicate arriving at new floor to the scheduler and ask if should stop at this floor
                        try {
                            DatagramPacket arriveDownSignalPacket = this.createMessagePacket((byte) 0x01, currentFloor);
                            timedSocket.send(arriveDownSignalPacket); // Send ARRIVAL_SENSOR message to scheduler
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        try {
                            DatagramPacket arriveDownReceivePacket = receivePacketWithTimeout(TIMEOUT); // Wait for a response from the scheduler on whether to stop at this floor
                            stopSignal = processStopSignalMessage(arriveDownReceivePacket.getData());
                            if (stopSignal) { // if scheduler requested stop at this floor, stop the elevator
                                thirdDestination = arriveDownReceivePacket.getData()[0];
                                addDestination(thirdDestination);
                                handleStopping();

                            }
                        } catch (SocketTimeoutException e) {
                            running = false;
                            System.out.println("Elevator " + portNumber + "'s receive socket timed out while waiting for scheduler's command. Stopping elevatorThread. (" + portNumber + ")");
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    // Elevator has reached destination.
                    System.out.println("------------------------------------------------------------------------------------");
                    System.out.println("Elevator " + portNumber + " has reached destination floor #" + nextDestination + " .");
                    addDestination(secondDestination);
                    System.out.println("Added 2nd destination floor to the list: #" + secondDestination);
                    System.out.println("------------------------------------------------------------------------------------");
                    handleStopping();
                    state = ElevatorState.IDLE;
                    break;
            }
        }
    }
}