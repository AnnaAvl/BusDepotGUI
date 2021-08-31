package com.auto;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class BaseEditor extends JDialog {
    private JPanel baseForm;
    private JTable tbBase;
    private JButton btDel;
    private JButton btAdd;
    private JButton btOK;
    private JButton btCancel;
    public String dataName;

    public BaseEditor(String dataName) {
        this.dataName = dataName;
        setContentPane(baseForm);
        pack();
        setModal(true);
        setTitle(dataName);
        setLocationRelativeTo(null);

        btAdd.addActionListener(e -> {
            if (tbBase.isEditing()) {
                JOptionPane.showMessageDialog(null, "Ошибка.\nТаблица в режиме редактирования.");
                return;
            }
            DefaultTableModel model = (DefaultTableModel) tbBase.getModel();
            model.addRow(new String[]{});
        });


        btDel.addActionListener(e -> {
            if (tbBase.isEditing()) {
                JOptionPane.showMessageDialog(null, "Ошибка.\nТаблица в режиме редактирования.");
                return;
            }
            DefaultTableModel model = (DefaultTableModel) tbBase.getModel();
            int r = tbBase.getSelectedRow();
            if (r >= 0) {
                boolean is = false;
                var trip = readTrip();
                if (dataName.equals("Buses")) {
                    for (int j = 0; j < trip.size(); j++) {
                        if (trip.get(j).get(1).equals(tbBase.getValueAt(r, 2))) {
                            is = true;
                            JOptionPane.showMessageDialog(null, "Нельзя удалить.\nАвтобус назначен на рейс.");
                        }
                    }
                } else if (dataName.equals("Routes")) {
                    for (int j = 0; j < trip.size(); j++) {
                        if (trip.get(j).get(2).equals(tbBase.getValueAt(r, 2))) {
                            is = true;
                            JOptionPane.showMessageDialog(null, "Нельзя удалить.\nМаршрут назначен на рейс.");
                        }
                    }
                }
                if (!is) {
                    model.removeRow(r);
                }
            }
        });

        btOK.addActionListener(e -> {
            if (tbBase.isEditing()) {
                JOptionPane.showMessageDialog(null, "Ошибка.\nТаблица в режиме редактирования.");
                return;
            }
            onOK();
        });

        btCancel.addActionListener(e -> dispose());

    }

    private ArrayList<ArrayList> readTrip() {
        ArrayList<ArrayList> arr = new ArrayList<>();
        try (var br = new BufferedReader(new InputStreamReader(
                new FileInputStream("Trips.txt"), ENCODING))) {
            for (String line; (line = br.readLine()) != null; ) {
                var str = new ArrayList<>(asList(line.split(",")));
                arr.add(str);
            }
        } catch (IOException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        return arr;
    }

    private void createUIComponents() throws IOException {
        // TODO: place custom component creation code here
        DefaultTableModel tableModel;
        switch (dataName) {
            case "Buses" -> {
                tableModel = new DefaultTableModel(new String[]{"Марка", "Водитель", "№ автобуса", "Кол-во мест"}, 0) {
                    final Class[] types = {String.class, String.class, String.class, Integer.class};

                    @Override
                    public Class getColumnClass(int columnIndex) {
                        return types[columnIndex];
                    }

                    public boolean isCellEditable(int r, int c) {
                        return (c == 1 || this.getValueAt(r, c) == null);
                    }

                };
                tbBase = new JTable(tableModel);
                tbBase.getColumnModel().getColumn(1).setCellEditor(new isCellUnique(new JTextField(), tbBase));
                tbBase.getColumnModel().getColumn(2).setCellEditor(new isCellUnique(new JTextField(), tbBase));
            }
            case "Routes" -> {
                tableModel = new DefaultTableModel(new String[]{"Место отправления", "Место прибытия", "№ маршрута", "Время отправления", "Время прибытия"}, 0) {
                    final Class[] types = {String.class, String.class, String.class, String.class, String.class};

                    @Override
                    public Class getColumnClass(int columnIndex) {
                        return types[columnIndex];
                    }

                    public boolean isCellEditable(int r, int c) {
                        return (c == 3 || c == 4 || this.getValueAt(r, c) == null);
                    }
                };
                tbBase = new JTable(tableModel);
                tbBase.getColumnModel().getColumn(2).setCellEditor(new isCellUnique(new JTextField(), tbBase));
                tbBase.getColumnModel().getColumn(3).setCellEditor(new isCellCheck(new JTextField(), "time"));
                tbBase.getColumnModel().getColumn(4).setCellEditor(new isCellCheck(new JTextField(), "time"));
            }
            case "Trips" -> {
                tableModel = new DefaultTableModel(new String[]{"ID рейса", "№ автобуса", "№ маршрута", "Дата отправления"}, 0) {
                    final Class[] types = {Integer.class, String.class, String.class, String.class};

                    @Override
                    public Class getColumnClass(int columnIndex) {
                        return types[columnIndex];
                    }

                    public boolean isCellEditable(int r, int c) {
                        return (this.getValueAt(r, c) == null);
                    }
                };
                tbBase = new JTable(tableModel);
                tbBase.getColumnModel().getColumn(0).setCellEditor(new isCellUnique(new JTextField(), tbBase));
                tbBase.getColumnModel().getColumn(3).setCellEditor(new isCellCheck(new JTextField(), "date"));
            }
        }
        readFile();
    }

    private static class isCellUnique extends DefaultCellEditor {

        private final JTextField textField;
        private final JTable jt;

        public isCellUnique(JTextField textField, JTable jt) {
            super(textField);
            this.jt = jt;
            this.textField = textField;
        }

        @Override
        public boolean stopCellEditing() {
            try {
                for (int i = 0; i < jt.getRowCount() - 1; i++) {
                    if (jt.getValueAt(i, jt.getSelectedColumn()).toString().toLowerCase().replaceAll("[. ]", "").equals(textField.getText().toLowerCase().replaceAll("[. ]", "")) && i != jt.getSelectedRow()) {
                        throw new NumberFormatException();
                    }
                }
            } catch (NumberFormatException e) {
                textField.setBorder(new LineBorder(Color.red));
                return false;
            }
            return super.stopCellEditing();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            textField.setBorder(new LineBorder(Color.black));
            return super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);
        }
    }

    private static class isCellCheck extends DefaultCellEditor {

        private final JTextField textField;
        private final String type;

        public isCellCheck(JTextField textField, String type) {
            super(textField);
            this.type = type;
            this.textField = textField;
            this.textField.setHorizontalAlignment(JTextField.LEFT);
        }

        @Override
        public boolean stopCellEditing() {
            try {
                switch (type) {
                    case "date" -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                        sdf.setLenient(false);
                        sdf.parse(textField.getText());
                    }
                    case "time" -> {
                        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
                        stf.setLenient(false);
                        stf.parse(textField.getText());
                    }
                }
            } catch (NumberFormatException | ParseException e) {
                textField.setBorder(new LineBorder(Color.red));
                return false;
            }
            return super.stopCellEditing();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            textField.setBorder(new LineBorder(Color.black));
            return super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);
        }
    }

    private void onOK() {
        int colC = tbBase.getColumnCount();
        StringBuilder sb;
        FileWriter fw;
        try {
            fw = new FileWriter(dataName + ".txt");
            for (int i = 0; i < tbBase.getRowCount(); i++) {
                sb = new StringBuilder();
                for (int j = 0; j < colC; j++) {
                    sb.append(tbBase.getValueAt(i, j));
                    if (j < colC - 1) sb.append(',');
                    if (j == colC - 1) sb.append("\r\n");
                }
                try {
                    fw.write(sb.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        dispose();
    }

    public static final String ENCODING = "UTF-8";

    public void readFile() throws IOException {
        DefaultTableModel model = (DefaultTableModel) tbBase.getModel();
        try (var br = new BufferedReader(new InputStreamReader(
                new FileInputStream(dataName + ".txt"), ENCODING))) {
            for (String line; (line = br.readLine()) != null; ) {
                var str = (line.split(","));
                model.addRow(str);
            }
        }
    }
}