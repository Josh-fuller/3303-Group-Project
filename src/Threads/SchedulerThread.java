package Threads;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import Threads.ElevatorBuffer;
import Threads.ElevatorThread;
import Threads.FloorEvent;

/** *
 * Class Scheduler used to translate data between Floors and Elevators.
 *
 * @author Josh Fuller
 */
public class SchedulerThread implements Runnable{

    ElevatorBuffer ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer;
    FloorEvent eventTransferOne;        //TODO Needed?
    FloorEvent eventTransferTwo;

    SchedulerState state;


    int startTranslation; //The number of floors between the cars current location and where the elevator request happens
    int endTranslation; //The number of floors between the start of the elevator request and the end

    //boolean emptyBuffer;
    // TODO Change thread call to have no buffers
    ElevatorThread elevatorThread = new ElevatorThread(ePutBuffer,eTakeBuffer, 1);

    ArrayList<FloorEvent> schedulerTasks, elevatorOneTasks, elevatorTwoTasks, elevatorThreeTasks;

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

        state = SchedulerState.IDLE;
        this.schedulerTasks = new ArrayList<>();
        this.elevatorOneTasks = new ArrayList<>();
        this.elevatorTwoTasks = new ArrayList<>();
        this.elevatorThreeTasks = new ArrayList<>();
        state = SchedulerState.IDLE;

        this.schedulerTasks = new ArrayList<>();
        this.elevatorOneTasks = new ArrayList<>();
        this.elevatorTwoTasks = new ArrayList<>();
        this.elevatorThreeTasks = new ArrayList<>();
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

    public void sortTasks(){

        for(int i = 0; i < schedulerTasks.size(); i++){

        }

        /*
        Two messages -
        Elevator message(Up or down) and floor message(
         */
    }

    public FloorEvent byteToFloorEvent(byte[] event) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(event));
        return (FloorEvent) inputStream.readObject();
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

        // Create a DatagramSocket

        try {
            receiveSocket = new DatagramSocket(69);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        // Get server IP address
        try {
            InetAddress IPAddress = InetAddress.getByName("localhost"); //edit with ip
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        while(true){

            switch(state) {
                case IDLE:

                    // Create a DatagramPacket to receive data from client
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    receivePacket = null;

                    try {
                        receiveSocket.receive(receivePacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    messageType messageType = parseByteArrayForType(receivePacket.getData());

                    if (messageType == SchedulerThread.messageType.FLOOR_EVENT) {
                        processingFloorState();
                    }
                    else if(messageType == SchedulerThread.messageType.ARRIVAL_SENSOR) {
                        processingSensorRequestState();
                    } else if(messageType == SchedulerThread.messageType.MOVE_REQUEST)
                        processingMoveRequestState();
                    else if (messageType == SchedulerThread.messageType.ERROR) {
                        System.out.println("ERROR DETECTED IN SCHEDULER MESSAGE");
                    }


                    break;

                case PROCESSING_FLOOR_EVENT:
                    // Take event from fPutBuffer
                    FloorEvent tempFloorEvent;

                    tempFloorEvent = byteToFloorEvent(receivePacket.getData());

                    schedulerTasks.add(tempFloorEvent);

                    sortTasks();

                    //get the number of floors to translate:

                    // Transition to DISPATCHING_TO_ELEVATOR state
                    idleState();
                    break;

                case PROCESSING_MOVE_REQUEST:

                    int currentFloorNum;

                    currentFloorNum = parseByteArrayForFloorNum(receivePacket.getData());

                    idleState();

                    break;

                case PROCESSING_ARRIVAL_SENSOR:

                    //Dispatch elevator based on processed event

                    //go to the right floor to start
                    //System.out.println("(STEP 3)");

                    //transition to idle state
                    idleState();

                    break;

                case DISPATCHING_TO_FLOOR:
                    // Put event in fTakeBuffer, signifying completion of event
                    System.out.println("SENDING ACKNOWLEDGE TO FLOOR");

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
