/**
 * @author Josh Fuller
 * @author Ahmad Alkawasmeh
 * Modified from consumer BoundedBuffer program used in lecture slides
 */
import java.util.*;

public class ElevatorBuffer {

    //contents of the plate
    private ArrayList<FloorEvent> contents = new ArrayList<>();

    // If true, there is room for at least one object
    // in the buffer.
    private boolean writeable = true;

    // If true, there is at least one object stored
    // in the buffer.
    private boolean readable = false;

    /**
     * Method used to serve ingredients on the plate. Immediately adds 2 items to not separate placement, which is why
     * it is readable right away and also instantly not writeable.
     * @param event A FloorEvent
     */
    public synchronized void put(FloorEvent event)
    {
        while (!writeable) {
            try {
                wait();
            } catch (InterruptedException e)
            { System.err.println(e);
            }
        }
        //Add to plate
        contents.add(event);

        if(contents.size() > 0){
            readable = true;
        }
        //System.out.println("");
        //System.out.println("");
        if(contents.size() > 19){
            writeable = false;
        }

        //System.out.println(""); //for debug
        notifyAll();
    }

    /**
     * Method used to take ingredients from the plate. Immediately takes 2 items to not separate removal, which is why
     * it is writeable right away and also instantly not readable.
     *
     * @return items A list of the two ingredients taken
     */

    public synchronized FloorEvent take(int i)
    {
        while (!readable) {
            try {
                wait();
            } catch (InterruptedException e){
                System.err.println(e);
            }
        }

        FloorEvent specificEvent = contents.get(i);
        contents.remove(i);

        writeable = true;

        if (contents.size() < 1){
            readable = false;
        }

        notifyAll();
        return specificEvent;
    }

    /**
     * Getter method for plate contents
     *
     * @return contents The plate contents
     */
    public ArrayList<FloorEvent> getContentsOfBuffer() {
        return contents;
    }

    public boolean isWriteable() {
        return writeable;
    }

    public boolean isReadable() {
        return readable;
    }


}