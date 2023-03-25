package Threads;

import java.io.*;
import java.net.*;
import java.util.*;

/** *
 * Class Scheduler used to translate data between Floors and Elevators.
 *
 * @author Josh Fuller
 */
public class SchedulerThread implements Runnable{

    ElevatorBuffer ePutBuffer,eTakeBuffer;

    SchedulerState state;

    private DatagramSocket receiveSocket,sendSocket;

    //boolean emptyBuffer;
    // TODO Change thread call to have no buffers
    ElevatorThread elevatorThread = new ElevatorThread(ePutBuffer,eTakeBuffer, 1);

    public ArrayList<FloorEvent> getSchedulerTasks() {
        return schedulerTasks;
    }

    ArrayList<FloorEvent> schedulerTasks, destinationList;
    Set<Integer> elevatorStops = new TreeSet<>();


    public enum SchedulerState {
        IDLE,
        PROCESSING_FLOOR_EVENT,
        PROCESSING_ARRIVAL_SENSOR,
        PROCESSING_MOVE_REQUEST,
        PROCESSING_ELEVATOR_EVENT,
        DISPATCHING_TO_FLOOR
    }

    public enum messageType {
        ARRIVAL_SENSOR,
        FLOOR_EVENT,
        MOVE_REQUEST,
        STOP_FINISHED,
        ERROR
    }

    public SchedulerThread(){
        
        this.schedulerTasks = new ArrayList<>();
        state = SchedulerState.IDLE;

        this.schedulerTasks = new ArrayList<>();

        // Create a DatagramSocket on port 23
        try {
            receiveSocket = new DatagramSocket(1003);
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    public void idleState(){
        state = SchedulerState.IDLE;
    }

    public void processingFloorState(){
        state = SchedulerState.PROCESSING_FLOOR_EVENT;
    }

    public void processingMoveRequestState(){
        state = SchedulerState.PROCESSING_MOVE_REQUEST;
    }

    public void sendingStopCompleteState(){ state = SchedulerState.SENDING_STOP_COMPLETE;}

    public void processingSensorRequestState(){
        state = SchedulerState.PROCESSING_ARRIVAL_SENSOR;
    }


    public void processingElevatorEventState(){
        state = SchedulerState.PROCESSING_ELEVATOR_EVENT;
    }

    public void dispatchingToFloorState(){
        state = SchedulerState.DISPATCHING_TO_FLOOR;
    }

    public SchedulerState getState(){
        return state;
    }


    //TODO Make javadoc + fix up once elevatorThread is fixed

    public Set<Integer> getElevatorStops() {
        return elevatorStops;
    }

    /**
    private void translateCar(int distance){

        boolean direction = true;

        if(distance < 0){
            direction = false;
        }

        for(int i = 0;i < distance; i++){
            if(direction){
                elevatorThread.moveUp();
            }
            else{
                elevatorThread.moveDown();
            }
            elevatorThread.openDoor();
            elevatorThread.closeDoor();
        }
    }
     */

    /**
     * Adds the destination floor to a list based on the schedulerTasks list. Also removes the added
     * task from the schedulerTask list.
     *
     * @return the destination floor
     */
    public int getDestinationFloor(){
        if(schedulerTasks.isEmpty()){
            return -1;
        }
        int destinationFloor = schedulerTasks.get(0).getElevatorButton();
        elevatorStops.add(destinationFloor);
        schedulerTasks.remove(0);
        return destinationFloor;
    }
    public boolean processStopRequest(int currentFloor){
        for(int i = 0;i < elevatorStops.size(); i++) {
            if (elevatorStops.contains(currentFloor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the floor event that was sent from the floor thread from a serialized object that
     * was converted into a byteArray back into a floor event.
     *
     * @param event The floor event that was sent from the floor thread
     * @return The floor event that was sent from the floor thread
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public FloorEvent byteToFloorEvent(byte[] event) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(event));
        return (FloorEvent) inputStream.readObject();
    }

    public static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (value >>> (i * 8));
        }
        return bytes;
    }

    /**
     *
     * @param byteArray
     * @return
     */
    public static messageType parseByteArrayForType(byte[] byteArray) {

        messageType type = messageType.ERROR; // default value

        // check first two bytes
        if (byteArray.length >= 2 && byteArray[0] == 0x0 && byteArray[1] == 0x1) {
            type = messageType.ARRIVAL_SENSOR;
        } else if (byteArray.length >= 2 && byteArray[0] == 0x0 && byteArray[1] == 0x2) {
            type = messageType.FLOOR_EVENT;
        } else if (byteArray.length >= 2 && byteArray[0] == 0x0 && byteArray[1] == 0x3) {
            type = messageType.MOVE_REQUEST;
        }


        // find first 0
        int firstZeroIndex = -1;
        for (int i = 2; i < byteArray.length; i++) {
            if (byteArray[i] == 0x0) {
                firstZeroIndex = i;
                break;
            }
        }

        // if no 0 found, set type to 2 and return
        if (firstZeroIndex == -1) {
            type = messageType.ERROR;
            return type;
        }

        // find second 0
        int secondZeroIndex = -1;
        for (int i = firstZeroIndex + 1; i < byteArray.length; i++) {
            if (byteArray[i] == 0x0) {
                secondZeroIndex = i;
                break;
            }
        }

        // if no second 0 found, set type to 2
        if (secondZeroIndex == -1) {
            type = messageType.ERROR;;
        }

        return type;
    }

    /**
     * Converts the packet data's floor number into an integer.
     *
     * @param byteArray the recieved packet data containing request information
     * @return floorNum the floor number an elevator requests to go to
     */
    public static int parseByteArrayForFloorNum(byte[] byteArray) {

        int floorNum = byteArray[3] & 0xff; // get 4th byte as int
        return floorNum;
    }

    /**
     * Waits for the elevator or floor to send a packet to the scheduler and receives that packet.
     *
     * @return recievePacket A packet sent from the elevator or floor
     */
    public DatagramPacket receivePacket(){
        DatagramPacket receivePacket = null;

        // Create a DatagramPacket to receive data from client
        byte[] receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            receiveSocket.receive(receivePacket); //Receive from anywhere
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received packet");
        return receivePacket;
    }

    /** *
     * The runnable portion of scheduler, responsible for acting as the translator from floor/elevator and back
     *
     * @author Josh Fuller
     */
    @Override
    public void run() {

        //initialise everything as null to start, so it is inside scope in case IDLE is skipped, though that is not possible practically
        DatagramPacket receivePacket = null;

        // Get server IP address
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName("localhost"); //edit with ip
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        while(true){

            switch(state) {
                case IDLE:
                    receivePacket = receivePacket();

                    messageType messageType = parseByteArrayForType(receivePacket.getData());

                    //based on message type, go to state
                    if (messageType == SchedulerThread.messageType.FLOOR_EVENT) {
                        processingFloorState();
                    } else if(messageType == SchedulerThread.messageType.ARRIVAL_SENSOR) {
                        processingSensorRequestState();
                    } else if(messageType == SchedulerThread.messageType.MOVE_REQUEST) {
                        processingMoveRequestState();
                    } else if(messageType == SchedulerThread.messageType.STOP_FINISHED) {
                        sendingStopCompleteState();
                    }
                    else if (messageType == SchedulerThread.messageType.ERROR) {
                        System.out.println("ERROR DETECTED IN SCHEDULER MESSAGE");
                    }


                    break;

                case PROCESSING_FLOOR_EVENT:

                    FloorEvent tempFloorEvent;

                    try {
                        tempFloorEvent = byteToFloorEvent(receivePacket.getData());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    schedulerTasks.add(tempFloorEvent);

                    idleState();
                    break;

                case PROCESSING_MOVE_REQUEST:

                    byte b = (byte) 0xFF;
                    byte[] destinationFloorMessage = null;
                    int currentMovingFloorNum = parseByteArrayForFloorNum(receivePacket.getData());
                    int destinationFloor = getDestinationFloor();

                    if(destinationFloor == -1){
                        destinationFloorMessage[0] = b;
                    }else{
                        destinationFloorMessage = intToByteArray(destinationFloor);
                    }

                    DatagramPacket sendElevatorMovePacket = new DatagramPacket(destinationFloorMessage, destinationFloorMessage.length, IPAddress, 69);//SEND BACK TO ELEVATOR THAT MADE THE REQUEST

                    try {
                        sendSocket.send(sendElevatorMovePacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    idleState();
                    break;

                case PROCESSING_ARRIVAL_SENSOR:

                    int stopRequest; //0 is affirmative, 1 is negative

                    int currentArrivingFloorNum = parseByteArrayForFloorNum(receivePacket.getData()); //gets the 4th byte as the floor num, message = 2 bytes mode + 0 byte + floor num byte

                    if(processStopRequest(currentArrivingFloorNum)){
                        stopRequest = 0;
                    } else {
                        stopRequest = 1;
                    }

                    byte[] stopFloorMessage = intToByteArray(stopRequest);

                    DatagramPacket sendElevatorStopPacket = new DatagramPacket(stopFloorMessage, stopFloorMessage.length, IPAddress, 69);//SEND TO ELEVATOR TAT ASKED TO MOVE
                    try {
                        sendSocket.send(sendElevatorStopPacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    if(stopRequest == 0){
                        dispatchingToFloorState();
                    } else{
                        idleState();
                    }


                    break;

                case DISPATCHING_TO_FLOOR:
                    // A stop was made at a floor, make sure the floor acknowledges

                    int floorNumber = parseByteArrayForFloorNum(receivePacket.getData()); //the floor it stopped at

                    byte[] sendFloorData = intToByteArray(floorNumber); //the data to send to the floor

                    DatagramPacket sendFloorPacket = new DatagramPacket(sendFloorData, sendFloorData.length, IPAddress, 2529);//SEND TO FLOOR

                    try {
                        sendSocket.send(sendFloorPacket);//SEND TO FLOOR
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    byte[] receivedFloorData = new byte[1024];
                    DatagramPacket receivedFloorPacket = new DatagramPacket(receivedFloorData, receivedFloorData.length); //Add error handling in future iterations

                    try {
                        receiveSocket.receive(receivedFloorPacket);//RECEIVE FROM FLOOR, just an ack rn but will be used for error hanndling in the future
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("RECEIVED ACK FROM FLOOR");

                    //TODO SEND MESSAGE BACK TO ELEVATOR CONFIRMING STOP AT ELEVATOR SUCCESS

                    //go to idle state
                    idleState();
                    break;

                case SENDING_STOP_COMPLETE:
                    // A stop was completed at a floor, make sure the floor acknowledges to stop timer

                    byte[] stopCompleteMessage = new byte[] {0x1};

                    DatagramPacket sendFloorSecondPacket = new DatagramPacket(stopCompleteMessage, stopCompleteMessage.length, IPAddress, 2529);//TODO MAKE RIGHT PORT FOR FLOOR

                    sendSocket.send(sendFloorSecondPacket);//SEND TO FLOOR

                    //go to idle state
                    idleState();
                    break;
            }



            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

        }
    }
}
