/** *
 * Class Scheduler used to translate data between Floors and Elevators.
 *
 * @author Josh Fuller
 */
public class Scheduler {

    ElevatorBuffer eBuffer = new ElevatorBuffer();
    FloorBuffer fBuffer = new FloorBuffer();

    public Scheduler(ElevatorBuffer eBuffer, FloorBuffer fBuffer){

        this.eBuffer = eBuffer;
        this.fBuffer = fBuffer;
    }



}
