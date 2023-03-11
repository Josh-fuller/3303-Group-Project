package MainPackage;

import Threads.ElevatorBuffer;
import Threads.ElevatorThread;
import Threads.FloorThread;
import Threads.SchedulerThread;

import java.io.IOException;

public class main {

    public static void main(String[] args) throws IOException {

        //declare and initialise everything

        ElevatorBuffer ePutBuffer,eTakeBuffer,fPutBuffer,fTakeBuffer;

        Thread elevator, floor, scheduler;

        ePutBuffer = new ElevatorBuffer();
        eTakeBuffer = new ElevatorBuffer();
        fPutBuffer = new ElevatorBuffer();
        fTakeBuffer = new ElevatorBuffer();


        // Create the floor,scheduler and elevator threads,
        // passing each thread a reference to the

        elevator = new Thread(new
                ElevatorThread(ePutBuffer, eTakeBuffer,1),"Elevator 1");
        System.out.println("Elevator Created");

        floor = new Thread(new
                FloorThread(fPutBuffer, fTakeBuffer), "Floor");
        System.out.println("Floor Created");

        scheduler = new Thread(new
                SchedulerThread(), "Scheduler");
        System.out.println("Scheduler Created");


        elevator.start();
        floor.start();
        scheduler.start();

    }
}
