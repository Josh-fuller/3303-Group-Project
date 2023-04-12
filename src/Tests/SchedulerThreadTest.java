package Tests;

import Threads.FloorEvent;
import Threads.FloorThread;
import Threads.SchedulerThread;
import org.junit.*;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class SchedulerThreadTest {

    private SchedulerThread scheduler = new SchedulerThread();

    FloorEvent floorTest1 = new FloorEvent("01:00:00.000", 1, FloorEvent.FloorButton.UP, 2, 2);
    FloorEvent floorTest2 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.UP, 3, 1);
    FloorEvent floorTest3 = new FloorEvent("01:00:00.000", 2, FloorEvent.FloorButton.DOWN, 4, 1);

    DatagramSocket testSendSocket, testReceiveSocket;
    DatagramPacket sendPacket;

    public SchedulerThreadTest() throws IOException {}

    @Before
    public void setup() throws SocketException, UnknownHostException {
        //testSendSocket = new DatagramSocket();
        //testReceiveSocket = new DatagramSocket(69);
       // sendPacket = new DatagramPacket(message, message.length,InetAddress.getLocalHost(), 1003);
    }

    /*
    @Test
    public void testMessageSendFromFloor() throws IOException {
        scheduler.receivePacket();
        testSendSocket.send(sendPacket);
        System.out.println(scheduler.getState());
    }
    
    @Test
    public void testByteToFloorEvent() throws IOException, ClassNotFoundException {
       // FloorThread.floorEventToByte(floorTest1);
       //  byte[] e = scheduler.floorEventToByte(floorTest1);
       //  System.out.println(scheduler.byteToFloorEvent(e));
    }
    */

    @Test
    public void testFindBothIntArray(){

        scheduler.closeSocket(); //closes sockets for testing purposes, to avoid bind error
        ArrayList<int[]> list = new ArrayList<>();
        list.add(new int[] {2, 5});
        list.add(new int[] {8, 11});
        list.add(new int[] {15, 18});

        byte[] testb = scheduler.findBothIntArray(2, list);

        byte[] realb = {(byte)2,(byte)5};

        Assert.assertEquals(testb[0],realb[0]);
        Assert.assertEquals(testb[1],realb[1]);


    }

    @Test
    public void testFindSingleIntArray(){

        scheduler.closeSocket(); //closes sockets for testing purposes, to avoid bind error
        ArrayList<int[]> list = new ArrayList<>();
        list.add(new int[] {2, 5});
        list.add(new int[] {8, 11});
        list.add(new int[] {15, 18});

        byte[] testb = scheduler.findSingleIntArray(2, list);

        byte[] realb = {(byte)5};

        Assert.assertEquals(testb[0],realb[0]);
    }

    @Test
    public void testParseByteArrayForType(){

        scheduler.closeSocket();

        byte[] testA = {0x0,0x1};
        byte[] testB = {0x0,0x2};
        byte[] testC = {0x0,0x3};
        byte[] testD = {0x0,0x6};

        SchedulerThread.messageType msgTypeA = SchedulerThread.messageType.ARRIVAL_SENSOR;
        SchedulerThread.messageType msgTypeB = SchedulerThread.messageType.FLOOR_EVENT;
        SchedulerThread.messageType msgTypeC = SchedulerThread.messageType.MOVE_REQUEST;
        SchedulerThread.messageType msgTypeD = SchedulerThread.messageType.STOP_FINISHED;

        Assert.assertEquals(scheduler.parseByteArrayForType(testA) , msgTypeA);
        Assert.assertEquals(scheduler.parseByteArrayForType(testB) , msgTypeB);
        Assert.assertEquals(scheduler.parseByteArrayForType(testC) , msgTypeC);
        Assert.assertEquals(scheduler.parseByteArrayForType(testD) , msgTypeD);
    }



}
