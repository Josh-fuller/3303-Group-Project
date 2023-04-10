package Threads;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class V1ElevatorGUI {
    private JFrame frame;
    private JPanel[][] boxes;

    public V1ElevatorGUI() {
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
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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

    public void highlightBox(int floorNum, int elevatorNum, Color color) {
        // Check if the floorNum and elevatorNum are valid
        if (floorNum < 1 || floorNum > 22 || elevatorNum < 1 || elevatorNum > 4) {
            throw new IllegalArgumentException("Invalid Floor or Elevator number");
        }
        // Get the box and set its background color
        JPanel box = boxes[22 - floorNum][elevatorNum - 1];
        box.setBackground(color);
    }

    public static void main(String[] args) {
        V1ElevatorGUI gui = new V1ElevatorGUI();
        gui.highlightBox(5, 2, Color.RED); // Example usage: highlight box in row 5, column 2 with red color
        gui.highlightBox(6, 1, Color.RED);
        gui.highlightBox(5, 2, Color.GREEN);
        gui.highlightBox(6, 1, Color.ORANGE);
    }

}
