package MainPackage;

import Threads.ElevatorThread;
import Threads.FloorThread;
import Threads.SchedulerThread;

import java.io.IOException;

public class main {

    public static void main(String[] args) throws IOException {

        //declare and initialise everything

        Thread elevator1, floor, scheduler, elevator2;


        // Create the floor,scheduler and elevator threads,
        // passing each thread a reference to the

        elevator1 = new Thread(new
                ElevatorThread(1011),"Elevator 1");
        System.out.println("Elevator Created");



        floor = new Thread(new
                FloorThread(), "Floor");
        System.out.println("Floor Created");

        scheduler = new Thread(new
                SchedulerThread(), "Scheduler");
        System.out.println("Scheduler Created");


        elevator1.start();
        //elevator2.start();
        floor.start();
        scheduler.start();

    }
}
