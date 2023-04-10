package Tests;

import Threads.FloorEvent;
import Threads.FloorThread;
import Threads.SchedulerThread;
import org.junit.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class SchedulerThreadTest {

    private SchedulerThread scheduler = new SchedulerThread();
    FloorThread floor = new FloorThread();


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
        ArrayList<int[]> list = new ArrayList<>();
        list.add(new int[] {2, 5});
        list.add(new int[] {8, 11});
        list.add(new int[] {15, 18});

        byte[] testb = scheduler.findSingleIntArray(2, list);

        byte[] realb = {(byte)5};

        Assert.assertEquals(testb[0],realb[0]);
    }



}
