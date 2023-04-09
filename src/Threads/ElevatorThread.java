package Threads;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * ElevatorThread implements the elevator state machine. The states are IDLE, STOPPED, MOVING_UP, MOVING_DOWN.
 *
 * @author  Mahtab Ameli
 * @version Iteration 4
 */
public class ElevatorThread implements Runnable {
    public enum ElevatorState {
        IDLE,
        STOPPED,
        MOVING_UP,
        MOVING_DOWN,
    }

    private int elevatorNumber;     // Elevator identifier number
    private int portNumber;         // Elevator's port number for receiving UDP communication
    private ElevatorState state;    // Elevator's current state
    private boolean doorOpen;       // true if door is open, false if closed
    private int currentFloor;       // Elevator's current floor as signalled by the arrival sensor
    private List<Integer> floors;   // list of 5 floors that have access to the elevator
    private boolean stopSignal;     // signal set to true when scheduler makes a command to stop at approaching floor
    private int arrivalSignal;      // integer indicates the floor number being approached by the elevator
    private int destination = 0;    // destination floor requested by the scheduler
    DatagramPacket sendPacket, receivePacket; // Datagram Packets for UDP communication with the scheduler thread
    DatagramSocket sendReceiveSocket;         // Datagram socket for sending and receiving UDP communication to/from the scheduler thread
    private boolean timerInterrupted = false;
    private final int LOAD_UNLOAD_TIME = 5000;




    /**
     * Constructor for the class.
     */
    public ElevatorThread(int portNumber) {
        //this.elevatorNumber = elevatorNumber;
        this.portNumber = portNumber;
        this.doorOpen = true;   // the elevator door is initially open
        this.currentFloor = 1;  // elevator starts at floor #1
        this.arrivalSignal = 1;
        this.floors = new ArrayList<Integer>();
        this.stopSignal = false;
        this.state = ElevatorState.IDLE;
        this.populateFloors();
        // Create a Datagram socket for both sending and receiving messages via UDP communication
        try {
            //sendReceiveSocket = new DatagramSocket(portNumber); //todo uncomment
            sendReceiveSocket = new DatagramSocket();
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
/*        for (int i = 0; i < 23; i++) {
            floors.add(i);
        }*/
        floors.addAll(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}));
    }



    /**
     * Increments floors one by one updates arrivalSignal after reaching new floor.
     */
    public void incrementFloor() {
        int topFloor = floors.size();
        int i = currentFloor;
        if (i < topFloor) {
            i++;
            currentFloor = i;
            arrivalSignal = i;
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
            i--;
            arrivalSignal = i;
            currentFloor = i;
            System.out.println("\nCURRENT FLOOR: " + currentFloor);
        }
    }


    /**
     * Waits to receive a DatagramPacket on sendReceiveSocket.
     *
     * @return receivePacket A packet sent from the elevator or floor
     */
    public DatagramPacket receivePacket(){
        DatagramPacket receivePacket;

        // Create a DatagramPacket to receive data from client
        byte[] receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            sendReceiveSocket.receive(receivePacket); //Receive from anywhere
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("Received packet"); // todo ask group why receiveData[3] originally
        //System.out.println("Scheduler received packet: " + receiveData[2]);
        return receivePacket;
    }


    /**
     * Creates and returns a Datagram Packet using given input parameters.
     */
    public DatagramPacket createMessagePacket(byte typeByte, int floorNumber, String sentence) throws UnknownHostException {
        byte[] messageTypeBytes = new byte[] {0x0, typeByte};
        byte[] floorNumberBytes = new byte[] {(byte) (floorNumber & 0xFF)};
        //byte[] sentenceBytes = sentence.getBytes();

        //ByteBuffer bb = ByteBuffer.allocate(messageTypeBytes.length + floorNumberBytes.length + sentenceBytes.length);
        ByteBuffer bb = ByteBuffer.allocate(messageTypeBytes.length + floorNumberBytes.length);

        //make read request
        bb.put(messageTypeBytes);
        bb.put(floorNumberBytes);
        //bb.put(sentenceBytes);

        byte[] message = bb.array();
        InetAddress schedulerAddress = InetAddress.getByName("localhost");
        int schedulerPort = 1003;

        DatagramPacket sendPacket = new DatagramPacket(message, message.length, schedulerAddress, schedulerPort);
        return sendPacket;
    }



    /**
     * Process string message from scheduler containing stop signal.
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
/*    *//**
     * Process string message from scheduler containing stop signal.
     * @param stopSignalMessage
     *//*
    private boolean processStopSignalMessage (String stopSignalMessage){
        int signal = Integer.valueOf(stopSignalMessage);
        if (signal == 0) { // if signal is 0, return true.
            return true;
        }
        return false;
    }*/



    /**
     * Process string message from scheduler containing destination floor number for elevator to move to.
     * @param destFloorBytes
     */
    private void processDestinationFloorMessage (byte[] destFloorBytes) {
        //this.destination = Integer.valueOf(String.valueOf(destFloorBytes));
        this.destination = byteArrayToInt(destFloorBytes);
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
            destination = -20; // invalid destination
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


/*    *//**
     * Handles floor transition timeout by killing the ElevatorThread if there is a fault.
     * @throws InterruptedException
     *//*
    public synchronized void handleTimeout(long timeoutMillis) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (!timerInterrupted) {
            long remainingTime = timeoutMillis - elapsedTime;
            if (remainingTime <= 0) {
                break;
            }
            wait(remainingTime);
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        // if after waiting, message to scheduler is still not acknowledged, kill ElevatorThread
        if (!timerInterrupted) {
            // handle timeout case
            System.out.println("Floor Transition Timeout!");
            //todo kill the thread
            System.exit(1);
        }
    }*/


    /**
     * ElevatorThread's run() method.
     */

    @Override
    public void run() {

        while (true) {

            switch (state) {

            //Elevator state: IDLE

                case IDLE:

                    // Send move request to the scheduler and wait to hear back on destination floor
                    System.out.println("Elevator State: IDLE");

                    try {
                        // Create move request datagram packet
                        DatagramPacket moveRequestPacket = createMessagePacket((byte) 0x03, currentFloor, "");
                        // Send message to scheduler
                        sendReceiveSocket.send(moveRequestPacket);
                        // Wait for a response from the scheduler for the destination floor to move to
                        byte[] responseBytes = new byte[1024];
                        //DatagramPacket moveRequestReceivePacket = new DatagramPacket(responseBytes, responseBytes.length); // todo remove
                        //sendReceiveSocket.receive(moveRequestReceivePacket); // todo remove
                        DatagramPacket moveRequestReceivePacket = receivePacket();
                        //responseBytes = moveRequestReceivePacket.getData();
                        //String destinationFloorMessage = new String(responseBytes, 0, moveRequestReceivePacket.getLength());
                        //System.out.println("Scheduler response to move request: " + destinationFloorMessage);
                        System.out.println("Scheduler's response back to elevator's move request: " + byteArrayToInt(moveRequestPacket.getData()));
                        //this.processDestinationFloorMessage(destinationFloorMessage);
                        this.processDestinationFloorMessage(moveRequestReceivePacket.getData());
                        // close port
                        //sendReceiveSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


                    //Elevator state: MOVING_UP

                case MOVING_UP:

                    System.out.println("Elevator State: MOVING UP");

                    int floorDifference = destination - currentFloor;

                    // move up to the destination floor one floor at a time
                    for (int i = 0; i < floorDifference; i++) {
                        incrementFloor(); // go up 1 floor
                        try {
                            // Create arrival sensor message as elevator approaches next floor up
                            //DatagramPacket arriveUpSignalPacket = this.createMessagePacket((byte) 0x01, arrivalSignal, "");
                            DatagramPacket arriveUpSignalPacket = this.createMessagePacket((byte) 0x01, currentFloor, "");
                            // Send arrival sensor message to scheduler
                            // start timer
                            //handleTimeout(10000);
                            sendReceiveSocket.send(arriveUpSignalPacket);
                            // Wait for a response from the scheduler on whether to stop at this floor
                            //byte[] responseBytes = new byte[1024];
                            //DatagramPacket arriveUpReceivePacket = new DatagramPacket(responseBytes, responseBytes.length);
                            DatagramPacket arriveUpReceivePacket = receivePacket();
                            //sendReceiveSocket.receive(arriveUpReceivePacket);


                            //floor transition timer was interrupted
                            //timerInterrupted = true;
                            //String stopSignalMessage = new String(responseBytes, 0, receivePacket.getLength());
                            //System.out.println("Scheduler response to arrival sensor: " + stopSignalMessage);
                            System.out.println("Scheduler response to arrival sensor: " + arriveUpReceivePacket.getData()[0]);

                            //this.stopSignal = processStopSignalMessage(stopSignalMessage);
                            this.stopSignal = processStopSignalMessage(arriveUpSignalPacket.getData());

                            // if stop is not requested, keep moving up. else, stop the elevator
                            if (!stopSignal) {continue;}


                            // if stop is requested
                            this.doorOpen = true;
                            System.out.println("\nElevator has stopped at " + currentFloor + ".");
                            //wait some time for load/unload, then close door and continue moving up
                            try {
                                Thread.sleep(LOAD_UNLOAD_TIME); // elevator door stays open for 5 seconds
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            this.doorOpen = false;

                            // send a message to scheduler communicating that the door was open and closed
                            try {
                                //create door opening and closing (after stopping at a new floor) to scheduler
                                String stringMessage = "The door has opened and closed.";
                                DatagramPacket doorClosedSendPacket = createMessagePacket((byte) 0x04, currentFloor, stringMessage);
                                // Send door closed message to the scheduler
                                sendReceiveSocket.send(doorClosedSendPacket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // reset stop signal to false until scheduler sets it to true again
                            stopSignal = false;

                        }
                        catch (IOException e) {e.printStackTrace();}
                    }
                    break;

                    //Elevator state: MOVING_DOWN

                case MOVING_DOWN:
                    System.out.println("Elevator State: MOVING DOWN");
                    floorDifference = currentFloor - destination;
                    for (int i = 0; i < floorDifference; i++) {
                        decrementFloor();
                        try {
                            // Create arrival sensor message
                            DatagramPacket arriveDownSendPacket = createMessagePacket((byte) 0x01, arrivalSignal, "");
                            // Send arrival sensor message to scheduler
                            // start timer
                            //handleTimeout(5000);
                            sendReceiveSocket.send(arriveDownSendPacket);
                            // Wait for a response from the scheduler on whether to stop at this floor
                            byte[] responseBytes = new byte[1024];
                            DatagramPacket arriveDownReceivePacket = new DatagramPacket(responseBytes, responseBytes.length);
                            sendReceiveSocket.receive(arriveDownReceivePacket);
                            // floor transition timer was interrupted
                            timerInterrupted = true;

                            String stopSignalMessage = new String(responseBytes, 0, arriveDownReceivePacket.getLength());
                            System.out.println("Scheduler response to arrival sensor: " + stopSignalMessage);

                            //this.stopSignal = processStopSignalMessage(stopSignalMessage);
                            this.stopSignal = processStopSignalMessage(responseBytes);

                            // if stop is not requested, move on to next iteration of loop
                            if (!stopSignal) {
                                continue;
                            }

                            System.out.println("\nElevator has stopped at " + currentFloor + ".");
                            this.doorOpen = true;
                            //wait some time for load/unload, then close door and continue moving down
                            try {
                                Thread.sleep(2000); // elevator door stays open for 5 seconds
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            this.doorOpen = false; // close door
                            // send a message to scheduler communicating that the door was open and closed
                            try {
                                //create door opening and closing (after stopping at a new floor) to scheduler
                                String stringMessage = "The door has opened and closed.";
                                DatagramPacket doorClosedSendPacket = createMessagePacket((byte) 0x06, currentFloor, stringMessage);
                                // Send door closed message to the scheduler
                                sendReceiveSocket.send(doorClosedSendPacket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // reset stop signal to false until scheduler sets it to true again
                            stopSignal = false;
                        }
                        catch (IOException e) {e.printStackTrace();}
                    }
                    break;

            }
        }
    }
}