package Tests;

import Threads.FloorEvent;
import Threads.FloorThread;
import Threads.SchedulerThread;
import org.junit.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
        testSendSocket = new DatagramSocket();
        testReceiveSocket = new DatagramSocket(69);
        byte[] message = floor.buildFloorByteMsg();
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
    public void testCreateByteMultiMap() {
        byte[] testArray = {0x0 , 0x1,  0x2,  0x4, 0x5};
        Multimap<Byte, Byte> testMap = scheduler.createByteMultiMap(testArray);
        Multimap<Byte, Byte> realMap = ArrayListMultimap.create();
        realMap.put((byte) 2,(byte) 4);
        realMap.put((byte) 2,(byte) 5);
        Assert.assertEquals(realMap,testMap);
    }


}
