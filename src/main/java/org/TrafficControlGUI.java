package org;

import javax.swing.*;
import java.awt.*;

public class TrafficControlGUI {
    private JFrame frame;
    private JPanel leftPanel, rightPanel, lightPanel, monitoringPanel, vehiclePanel, emergencyPanel;
    private JLabel lightTitle, stateLabel, monitoringTitle, monitoringLabel;
    private DefaultListModel<String> vehicleListModel, emergencyListModel;
    private JList<String> vehicleList, emergencyList;

    private LightCircle redCircle, yellowCircle, greenCircle;

    public TrafficControlGUI() {
        frame = new JFrame("Traffic Control System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setLayout(new GridLayout(1, 2, 10, 10)); // 1 rând, 2 coloane

        // *** Coloana 1: Semafor + Monitorizare ***
        leftPanel = new JPanel(new BorderLayout());

        // Panel Semafor
        lightPanel = new JPanel();
        lightPanel.setLayout(new BoxLayout(lightPanel, BoxLayout.Y_AXIS));
        lightPanel.setBorder(BorderFactory.createTitledBorder("Semafor"));
        lightTitle = new JLabel("Traffic Light: Culoarea activă este evidențiată", SwingConstants.CENTER);
        lightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel lightsRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        redCircle = new LightCircle(Color.RED);
        yellowCircle = new LightCircle(Color.YELLOW);
        greenCircle = new LightCircle(Color.GREEN);
        lightsRowPanel.add(redCircle);
        lightsRowPanel.add(yellowCircle);
        lightsRowPanel.add(greenCircle);

        stateLabel = new JLabel("Current state: RED", SwingConstants.CENTER);
        stateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lightPanel.add(lightTitle);
        lightPanel.add(lightsRowPanel);
        lightPanel.add(stateLabel);

        leftPanel.add(lightPanel, BorderLayout.NORTH);

        // Panel Monitorizare sub Semafor
        monitoringPanel = new JPanel(new BorderLayout());
        monitoringPanel.setBorder(BorderFactory.createTitledBorder("Monitorizare trafic"));
        monitoringTitle = new JLabel("Date despre trafic și optimizare", SwingConstants.CENTER);
        monitoringLabel = new JLabel("<html><br>Numărul de vehicule în intersecție = 0<br>" +
                "Numărul de vehicule de urgență = 0<br>" +
                "Frecvența schimbării semaforului = 0</html>", SwingConstants.LEFT);
        monitoringPanel.add(monitoringTitle, BorderLayout.NORTH);
        monitoringPanel.add(monitoringLabel, BorderLayout.NORTH);
        leftPanel.add(monitoringPanel, BorderLayout.CENTER);

        frame.add(leftPanel);

        // *** Coloana 2: Vehicule prioritare + restul vehiculelor ***
        rightPanel = new JPanel(new GridLayout(2, 1));

        // Panel Vehicule prioritare
        emergencyPanel = new JPanel(new BorderLayout());
        emergencyPanel.setBorder(BorderFactory.createTitledBorder("Vehicule de urgență"));
        JLabel emergencyTitle = new JLabel("Vehicule prioritare (Ambulanță, Poliție, Pompieri)", SwingConstants.CENTER);
        emergencyListModel = new DefaultListModel<>();
        emergencyList = new JList<>(emergencyListModel);
        emergencyPanel.add(emergencyTitle, BorderLayout.NORTH);
        emergencyPanel.add(new JScrollPane(emergencyList), BorderLayout.CENTER);
        rightPanel.add(emergencyPanel);

        // Panel Vehicule normale
        vehiclePanel = new JPanel(new BorderLayout());
        vehiclePanel.setBorder(BorderFactory.createTitledBorder("Vehicule în intersecție"));
        JLabel vehicleTitle = new JLabel("Lista vehiculelor care așteaptă", SwingConstants.CENTER);
        vehicleListModel = new DefaultListModel<>();
        vehicleList = new JList<>(vehicleListModel);
        vehiclePanel.add(vehicleTitle, BorderLayout.NORTH);
        vehiclePanel.add(new JScrollPane(vehicleList), BorderLayout.CENTER);
        rightPanel.add(vehiclePanel);

        frame.add(rightPanel);

        // Inițial: semaforul este pe roșu
        setTrafficLightState("RED");

        frame.setVisible(true);
    }

    // Actualizează starea semaforului
    public void setTrafficLightState(String state) {
        redCircle.setOn(state.equals("RED"));
        yellowCircle.setOn(state.equals("YELLOW"));
        greenCircle.setOn(state.equals("GREEN"));

        stateLabel.setText("Current state: " + state);
        lightPanel.repaint();
    }

    public void addVehicle(String vehicle) {
        if (!vehicleListModel.contains(vehicle)) {
            vehicleListModel.addElement(vehicle);
        }
    }

    public void addEmergency(String emergency) {
        if (!emergencyListModel.contains(emergency)) {
            emergencyListModel.addElement(emergency);
        }
    }

    public void updateMonitoring(int vehiclesInIntersection, int emergencyVehicles, long averageWaitingTime, double lightChangeFrequency) {
        String displayText = String.format("<html><br>Numărul de vehicule în intersecție = %d" +
                        "<br>Numărul de vehicule de urgență = %d" +
                        "<br>Frecvența schimbării semaforului = %.2f changes/minute</html>",
                vehiclesInIntersection,
                emergencyVehicles,
                lightChangeFrequency
        );
        monitoringLabel.setText(displayText);
    }

    public void updateCarStatus(String type, String car, String status) {
        DefaultListModel<String> targetList = type.equals("Emergency") ? emergencyListModel : vehicleListModel;
        boolean updated = false;
        for (int i = 0; i < targetList.getSize(); i++) {
            String element = targetList.get(i);
            if (element.startsWith(car + ":")) {
                targetList.set(i, car + ": " + status);
                updated = true;
                break;
            }
        }
        if (!updated) {
            targetList.addElement(car + ": " + status);
        }
    }

    // Clasă internă pentru desenarea cercurilor semaforului
    class LightCircle extends JPanel {
        private Color color;
        private boolean isOn = false;

        public LightCircle(Color color) {
            this.color = color;
            this.setPreferredSize(new Dimension(20, 20));
        }

        public void setOn(boolean on) {
            this.isOn = on;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int size = 20;
            g.setColor(isOn ? color : color.darker().darker());
            g.fillOval(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size);
            g.setColor(Color.BLACK);
            g.drawOval(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size);
        }
    }
}
