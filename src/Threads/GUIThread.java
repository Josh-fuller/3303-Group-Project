package Threads;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUIThread {
    private JFrame frame;
    private JPanel[][] boxes;

    // This is where we construct everything needed for the class
    public GUIThread() {
        // Create the frame and set its properties
        frame = new JFrame("ElevatorGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        // Create the panel for the boxes and set its layout
        JPanel boxPanel = new JPanel(new GridLayout(23, 6));

        // Create the labels for the columns and add them to the panel
        boxPanel.add(new JLabel());
        boxPanel.add(new JLabel("Elevator 1"));
        boxPanel.add(new JLabel("Elevator 2"));
        boxPanel.add(new JLabel("Elevator 3"));
        boxPanel.add(new JLabel("Elevator 4"));
        boxPanel.add(new JLabel());

        // Create the boxes and add them to the panel
        boxes = new JPanel[22][4];
        for (int i = 0; i < 22; i++) {
            // Add the row number label
            JLabel rowLabel = new JLabel("Floor " + (22 - i));
            rowLabel.setHorizontalAlignment(JLabel.CENTER);
            boxPanel.add(rowLabel);

            for (int j = 0; j < 4; j++) {
                // Create the box
                JPanel box = new JPanel();
                box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                // Add the box to the panel and the boxes array
                boxes[i][j] = box;
                boxPanel.add(box);
            }

            boxPanel.add(new JLabel());
        }

        // Create the legend panel and set its layout
        JPanel legendPanel = new JPanel(new GridLayout(4, 1));
        //legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // Add the legend labels to the panel
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel openLabel = new JLabel("Door Opening");
        openLabel.setBackground(Color.ORANGE);
        openLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel closeLabel = new JLabel("Door Closing");
        closeLabel.setBackground(Color.RED);
        closeLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel closedLabel = new JLabel("Door Closed");
        closedLabel.setBackground(Color.GREEN);
        closedLabel.setHorizontalAlignment(JLabel.CENTER);

        // Set the font color for the labels
        statusLabel.setForeground(Color.BLACK);
        openLabel.setForeground(Color.BLACK);
        closeLabel.setForeground(Color.BLACK);
        closedLabel.setForeground(Color.BLACK);

        // Set the background color for the labels
        statusLabel.setBackground(Color.BLACK);
        closedLabel.setOpaque(true);
        closeLabel.setOpaque(true);
        openLabel.setOpaque(true);

        // Add the labels to the panel
        legendPanel.add(statusLabel);
        legendPanel.add(openLabel);
        legendPanel.add(closeLabel);
        legendPanel.add(closedLabel);

        // Add the box panel and legend panel to the frame
        frame.add(boxPanel);
        frame.add(legendPanel, "East");

        // Make the frame visible
        frame.setVisible(true);
    }


    /**
     * This is the method that sets each box to the desired colour
     *
     * @param FloorNum is the floor number
     * @param ElevatorNum is the elevator number
     * @param color is the colour you would like to set that box to
     */
    public void highlightBox(int FloorNum, int ElevatorNum, Color color) {
        // Check if the FloorNum and ElevatorNum are valid
        if (FloorNum < 1 || FloorNum > 22 || ElevatorNum < 1 || ElevatorNum > 4) {
            throw new IllegalArgumentException("Invalid Floor number or elevator number");
        }
        // Get the box and set its background color
        JPanel box = boxes[22 - FloorNum][ElevatorNum - 1];
        box.setBackground(color);
        // Clear the previous highlight in the same column
        for (int i = 0; i < 22; i++) {
            if (i != (22 - FloorNum)) {
                JPanel prevBox = boxes[i][ElevatorNum - 1];
                if (prevBox.getBackground() == color) {
                    prevBox.setBackground(null);
                    break;
                }
            }
        }
    }

    /**
     * This method/runnable will allow us to make sure that only one box is highlighted per column and will
     * keep the gui window active while changing the colours of the boxes
     *
     * @param boxCoords is the Floor-Elevator pair (or simply the specific box in the gui)
     * @param color is the color you'd like to set that box to
     * @param delay is the delay before switching the highlighted box, so it doesn't switch instantly
     */
    public void highlightBoxes(int[][] boxCoords, Color color, int delay) {
        // Create a new thread to handle the color changes
        Thread colorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int[] coord : boxCoords) {
                    highlightBox(coord[0], coord[1], color);
                    try {
                        // Delay for the specified amount of time
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // Start the thread
        colorThread.start();
    }

    // This is just a main to show an example
    public static void main(String[] args) {
        GUIThread gui = new GUIThread();
        // Example usage: simulating elevators 2 and 4 going from floor 5 to 8
        int[][] boxCoords = {{5, 2}, {8, 2}, {5, 4}, {8, 4}};
        gui.highlightBoxes(boxCoords, Color.RED, 1000); // Delay for 1 second between color changes
    }
}
