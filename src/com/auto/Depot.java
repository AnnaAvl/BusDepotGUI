package com.auto;

import javax.swing.*;

public class Depot extends JFrame {
    private JButton btBuses;
    private JPanel rootPan;
    private JButton btRouts;
    private JButton btTrips;
    private JButton btSearch;

    public Depot() {

        setContentPane(rootPan);
        pack();
        setTitle("Депо");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btBuses.addActionListener(e -> {
            JDialog dialog = new BaseEditor("Buses");
            dialog.setVisible(true);
        });
        btRouts.addActionListener(e -> {
            JDialog dialog = new BaseEditor("Routes");
            dialog.setVisible(true);
        });
        btTrips.addActionListener(e -> {
            JDialog dialog = new BaseEditor("Trips");
            dialog.setVisible(true);
        });
        btSearch.addActionListener(e -> {
            JDialog dialog = new Search();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }
}