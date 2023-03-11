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
        ERROR
    }

    public SchedulerThread(){
        
        this.schedulerTasks = new ArrayList<>();
        state = SchedulerState.IDLE;

        this.schedulerTasks = new ArrayList<>();
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

    /** *
     * Gets the translation number from current floor -> start floor [assuming up is positive]
     * TODO Make current floor variable in elevator (Do we need this function after UDP changes?)
     */
    //private int getStartTranslation(){
        //return eventTransferOne.getFloorNumber() - elevatorThread.getCurrentFloor;
    //}

    /** *
     * Gets the translation number from start floor -> end floor [assuming up is positive]
     */
    //private int getEndTranslation(){
        //return eventTransferOne.getElevatorButton() - eventTransferOne.getElevatorNum();
    //}

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


    public int getDestinationFloor(){
        int destinationFloor = schedulerTasks.get(0).getElevatorButton();
        elevatorStops.add(destinationFloor);
        schedulerTasks.remove(0);
        return destinationFloor;
    }
    public int proccessStopRequest(int currentFloor){
        for(int i = 0;i < elevatorStops.size(); i++){
            if(elevatorStops.contains(currentFloor)){
                return 0;
            }else{
                return 1;
            }

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

    public static messageType parseByteArrayForType(byte[] byteArray) {

        messageType type = messageType.ERROR; // default value

        // check first two bytes
        if (byteArray.length >= 2 && byteArray[0] == 0x0 && byteArray[1] == 0x1) {
            type = messageType.ARRIVAL_SENSOR;
        } else if (byteArray.length >= 2 && byteArray[0] == 0x0 && byteArray[1] == 0x2) {
            type = messageType.FLOOR_EVENT;
        }
        else if (byteArray.length >= 2 && byteArray[0] == 0x0 && byteArray[1] == 0x3) {
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

    public static int parseByteArrayForFloorNum(byte[] byteArray) {

        int floorNum = byteArray[3] & 0xff; // get 4th byte as int
        return floorNum;

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

                    // Create a DatagramPacket to receive data from client
                    byte[] receiveData = new byte[1024];
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    try {
                        receiveSocket.receive(receivePacket); //Receive from anywhere
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    messageType messageType = parseByteArrayForType(receivePacket.getData());

                    //based on message type, go to state
                    if (messageType == SchedulerThread.messageType.FLOOR_EVENT) {
                        processingFloorState();
                    } else if(messageType == SchedulerThread.messageType.ARRIVAL_SENSOR) {
                        processingSensorRequestState();
                    } else if(messageType == SchedulerThread.messageType.MOVE_REQUEST)
                        processingMoveRequestState();
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

                    sortTasks();

                    idleState();
                    break;

                case PROCESSING_MOVE_REQUEST:

                    int currentMovingFloorNum = parseByteArrayForFloorNum(receivePacket.getData());
                    int destinationFloor = getDestinationFloor(currentMovingFloorNum);

                    byte[] destinationFloorMessage = intToByteArray(destinationFloor);

                    DatagramPacket sendElevatorMovePacket = new DatagramPacket(destinationFloorMessage, destinationFloorMessage.length, IPAddress, 69);//SEND BACK TO ELEVATOR THAT MADE THE REQUEST
                    //TODO ACC SEND THE MESSAGE

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

                    //TODO ACC SEND THE MESSAGE

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

                    sendSocket.send(sendFloorPacket);//SEND TO FLOOR

                    byte[] receivedFloorData = new byte[1024];
                    DatagramPacket receivedFloorPacket = new DatagramPacket(receivedFloorData, receivedFloorData.length); //Add error handling in future iterations

                    receiveSocket.receive(receivedFloorPacket);//RECEIVE FROM FLOOR, just an ack rn but will be used for error hanndling in the future
                    System.out.println("RECEIVED ACK FROM FLOOR");

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
