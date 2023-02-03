/**
 * Class FloorEvent represents the floor input data for requesting an elevator.
 *
 * @author Mahtab Ameli
 */
public class FloorEvent {

    public enum FloorButton {UP, DOWN}; // FloorButton variable represents the direction button pressed elevator from floor

    private String time; // time of elevator request (hh:mm:ss.mmm)
    private int floorNumber; // the floor number elevator request is made from
    private FloorButton floorButton; // the up/down direction button
    private int elevatorButton; // destination floor button pressed inside the elevator



    private int elevatorNum; // which elevator is being used



    /**
     * Constructor for the class.
     * @param time time of elevator request (hh:mm:ss.mmm)
     * @param floorNumber the floor number elevator request is made from
     * @param floorButton the up/down direction button
     * @param elevatorButton destination floor button pressed inside the elevator
     */
    public FloorEvent(String time, int floorNumber, FloorButton floorButton, int elevatorButton) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.floorButton = floorButton;
        this.elevatorButton = elevatorButton;
    }


    /**
     * String representation of floorEvents.
     * @return String
     */
    public String toString() {
        return "[Time: " + time + ", Floor #: " + floorNumber +
                ", Floor Button: " + floorButton + ", Elevator Button: " + elevatorButton + "]";
    }

    /**
     * Getters for data
     * @author Josh Fuller
     * @return time/floorNumber/floorButton/elevatorButton/elevatorNum
     */
    public String getTime() {
        return time;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public FloorButton getFloorButton() {
        return floorButton;
    }

    public int getElevatorButton() {
        return elevatorButton;
    }

    public int getElevatorNum() {
        return elevatorNum;
    }


}
