package com.auto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.util.Arrays.asList;

public class Search extends JDialog {
    private JTextField nameField;
    private JTextField departField;
    private JTextField destField;
    private JButton btSearch;
    private JTable tbResult;
    private JPanel searchForm;

    public Search() {
        setContentPane(searchForm);
        pack();
        setModal(true);
        setTitle("Поиск");

        btSearch.addActionListener(e -> {
            try {
                var buses = readFile("Buses");
                var routes = readFile("Routes");
                var trips = readFile("Trips");
                DefaultTableModel model = (DefaultTableModel) tbResult.getModel();
                model.getDataVector().removeAllElements();
                for (int i = 0; i < trips.size(); i++) {
                    for (int j = 0; j < buses.size(); j++) {
                        if (buses.get(j).get(2).equals(trips.get(i).get(1))
                                && (buses.get(j).get(1).toString().toLowerCase().contains(nameField.getText().toLowerCase())
                                || (nameField.getText() == null))) {
                            for (int k = 0; k < routes.size(); k++) {
                                if (routes.get(k).get(2).equals(trips.get(i).get(2))
                                        && (routes.get(k).get(0).toString().toLowerCase().contains(departField.getText().toLowerCase())
                                        || (destField.getText() == null))
                                        && (routes.get(k).get(1).toString().toLowerCase().contains(destField.getText().toLowerCase())
                                        || (departField.getText() == null))) {
                                    var row = new ArrayList<>();
                                    row.add(buses.get(j).get(1).toString());
                                    row.add(routes.get(k).get(0).toString());
                                    row.add(routes.get(k).get(1).toString());
                                    for (int t = 0; t < model.getColumnCount() - 3; t++) {
                                        row.add(trips.get(i).get(t));
                                    }
                                    model.addRow(row.toArray());
                                }
                            }

                        }
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public static final String ENCODING = "UTF-8";

    public static ArrayList<ArrayList> readFile(String fName) throws IOException {
        ArrayList<ArrayList> arr = new ArrayList<>();
        try (var br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fName + ".txt"), ENCODING))) {
            for (String line; (line = br.readLine()) != null; ) {
                var str = new ArrayList<>(asList(line.split(",")));
                arr.add(str);
            }
        }
        return arr;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Водитель", "Место отправления", "Место прибытия", "ID рейса", "№ автобуса", "№ маршрута", "Дата отправления"}, 0) {
            Class[] types = {String.class, String.class, String.class, Integer.class, String.class, String.class, String.class};

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        tbResult = new JTable(tableModel);
    }
}