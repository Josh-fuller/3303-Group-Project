package Tests;

import Threads.GUIThread;
import org.junit.*;

import java.awt.*;

import static org.junit.Assert.assertEquals;


public class GUITest {


    GUIThread gui = new GUIThread(null);

    @Before
    public void setup(){
        gui.highlightBox(1,2,Color.GREEN);
        gui.killElevator(1);
    }

    @Test
    public void testHighlightCurrentFloor(){
        assertEquals(Color.GREEN, gui.getBoxes()[21][1].getBackground());

        gui.highlightBox(2,2,Color.BLUE);
        assertEquals(Color.WHITE, gui.getBoxes()[21][1].getBackground());
        assertEquals(Color.BLUE, gui.getBoxes()[20][1].getBackground());

    }

    @Test
    public void testKilledElevator(){
        assertEquals(Color.RED, gui.getBoxes()[0][0].getBackground());
        assertEquals(Color.RED, gui.getBoxes()[1][0].getBackground());
        assertEquals(Color.RED, gui.getBoxes()[20][0].getBackground());
        assertEquals(Color.GREEN, gui.getBoxes()[21][1].getBackground());
    }
}
