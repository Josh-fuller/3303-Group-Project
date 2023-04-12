package Tests;

import Threads.FloorEvent;
import Threads.FloorThread;
import Threads.SchedulerThread;
import org.junit.*;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test Class for all relevant methods in the scheduler
 */
public class SchedulerThreadTest {

    private SchedulerThread scheduler = new SchedulerThread();

    public SchedulerThreadTest() {}

    @Before
    public void setup() throws SocketException, UnknownHostException {

    }

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
        byte[] testD = {0x0,0x4};

        SchedulerThread.messageType msgTypeA = SchedulerThread.messageType.ARRIVAL_SENSOR;
        SchedulerThread.messageType msgTypeB = SchedulerThread.messageType.FLOOR_EVENT;
        SchedulerThread.messageType msgTypeC = SchedulerThread.messageType.MOVE_REQUEST;
        SchedulerThread.messageType msgTypeD = SchedulerThread.messageType.STOP_FINISHED;

        Assert.assertEquals(scheduler.parseByteArrayForType(testA) , msgTypeA);
        Assert.assertEquals(scheduler.parseByteArrayForType(testB) , msgTypeB);
        Assert.assertEquals(scheduler.parseByteArrayForType(testC) , msgTypeC);
        Assert.assertEquals(scheduler.parseByteArrayForType(testD) , msgTypeD);
    }

    @Test
    public void testSortElevatorTasks(){

        scheduler.closeSocket();

        byte[] taskByteArray = {0x0,0x2, 0x1, 0x3, 0x4, 0x7, 0x9, 0x22, 0x13, 0x20, 0x0, 0x0};

        ArrayList<int[]> expectedEventList = new ArrayList<>();

        int[] tempIntArray = {1,3};
        expectedEventList.add(tempIntArray);

        tempIntArray = new int[]{4, 7};
        expectedEventList.add(tempIntArray);

        tempIntArray = new int[]{9, 34};
        expectedEventList.add(tempIntArray);

        tempIntArray = new int[]{19, 32};
        expectedEventList.add(tempIntArray);

        scheduler.sortElevatorTasks(taskByteArray);

        Assert.assertTrue(Arrays.equals(scheduler.getEventList().get(0), expectedEventList.get(0)));
        Assert.assertTrue(Arrays.equals(scheduler.getEventList().get(1), expectedEventList.get(1)));
        Assert.assertTrue(Arrays.equals(scheduler.getEventList().get(2), expectedEventList.get(2)));
        Assert.assertTrue(Arrays.equals(scheduler.getEventList().get(3), expectedEventList.get(3)));

    }

    @Test
    public void testByteArrayForFloorNum(){

        scheduler.closeSocket();

        byte[] numFloorByteArrayA = {0x0,0x1, 0x0, 0x3};
        byte[] numFloorByteArrayB = {0x0,0x1, 0x0, 0x4};
        byte[] numFloorByteArrayC = {0x0,0x1, 0x0, 0x7};
        byte[] numFloorByteArrayD = {0x0,0x1, 0x0, 0x9};


        Assert.assertEquals(scheduler.parseByteArrayForFloorNum(numFloorByteArrayA), 3);
        Assert.assertEquals(scheduler.parseByteArrayForFloorNum(numFloorByteArrayB), 4);
        Assert.assertEquals(scheduler.parseByteArrayForFloorNum(numFloorByteArrayC), 7);
        Assert.assertEquals(scheduler.parseByteArrayForFloorNum(numFloorByteArrayD), 9);

    }



}
