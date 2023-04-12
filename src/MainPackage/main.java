package MainPackage;

import Threads.ElevatorThread;
import Threads.FloorThread;
import Threads.GUIThread;
import Threads.SchedulerThread;

import java.io.IOException;
import java.util.ArrayList;

public class main {

    public static void main(String[] args) throws IOException {

        //declare and initialise everything

        Thread elevator1,elevator2, elevator3, elevator4, floor, scheduler;
        ArrayList<ElevatorThread> elevators = new ArrayList<>();
        ElevatorThread e1,e2,e3,e4;

        e1 = new ElevatorThread(1011,1);
        e2 = new ElevatorThread(1012,2);
        e3 = new ElevatorThread(1013,3);
        e4 = new ElevatorThread(1014,4);

        // Create the floor,scheduler and elevator threads,
        // passing each thread a reference to the

        elevator1 = new Thread(e1,"Elevator 1");
        elevator2 = new Thread(e2,"Elevator 2");
        elevator3 = new Thread(e3,"Elevator 3");
        elevator4 = new Thread(e4,"Elevator 4");
        System.out.println("Elevator Created");


        elevators.add(e1);
        elevators.add(e2);
        elevators.add(e3);
        elevators.add(e4);

        floor = new Thread(new
                FloorThread(), "Floor");
        System.out.println("Floor Created");

        scheduler = new Thread(new
                SchedulerThread(), "Scheduler");
        System.out.println("Scheduler Created");


        GUIThread gui = new GUIThread(elevators);

        gui.start();
        elevator1.start();
        elevator2.start();
        elevator3.start();
        elevator4.start();
        floor.start();
        scheduler.start();

    }
}
