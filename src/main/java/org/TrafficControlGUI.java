package org;

import javax.swing.*;
import java.awt.*;

public class TrafficControlGUI {
    private JFrame frame;
    private JPanel leftPanel, rightPanel, lightPanel, monitoringPanel, vehiclePanel, emergencyPanel, pedestrianPanel;
    private JLabel lightTitle, stateLabel, monitoringTitle, monitoringLabel; // monitoringTitle poate fi chiar eliminat din declarare
    private DefaultListModel<String> vehicleListModel, emergencyListModel, pedestrianListModel;
    private JList<String> vehicleList, emergencyList, pedestrianList;

    private LightCircle redCircle, yellowCircle, greenCircle;

    public TrafficControlGUI() {
        frame = new JFrame("Traffic Control System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setLayout(new GridLayout(1, 2, 10, 10));

        // --- Inițializare și Configurare leftPanel ---
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 1. lightPanel (Semafor)
        lightPanel = new JPanel();
        lightPanel.setLayout(new BoxLayout(lightPanel, BoxLayout.Y_AXIS));
        lightPanel.setBorder(BorderFactory.createTitledBorder("Semafor"));

        lightTitle = new JLabel("Traffic Light: Culoarea activă este evidențiată", SwingConstants.CENTER);
        lightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lightPanel.add(lightTitle);

        JPanel lightsRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        redCircle = new LightCircle(Color.RED);
        yellowCircle = new LightCircle(Color.YELLOW);
        greenCircle = new LightCircle(Color.GREEN);
        lightsRowPanel.add(redCircle);
        lightsRowPanel.add(yellowCircle);
        lightsRowPanel.add(greenCircle);
        lightPanel.add(lightsRowPanel);

        stateLabel = new JLabel("Current state: RED", SwingConstants.CENTER);
        stateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lightPanel.add(stateLabel);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        leftPanel.add(lightPanel, gbc);


        // 2. monitoringPanel - MODIFICARE AICI: Eliminăm JLabel-ul "Monitoring Data"
        monitoringPanel = new JPanel();
        monitoringPanel.setLayout(new BoxLayout(monitoringPanel, BoxLayout.Y_AXIS));
        monitoringPanel.setBorder(BorderFactory.createTitledBorder("Monitoring Data")); // Acesta este titlul dorit

        // monitoringTitle = new JLabel("Monitoring Data"); // Această linie nu mai este necesară
        // monitoringTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Această linie nu mai este necesară
        // monitoringPanel.add(monitoringTitle); // Această linie nu mai este necesară

        monitoringLabel = new JLabel("<html><br>Masini în intersecție = 0" +
                "<br>Pietoni in intersectie = 0" +
                "<br>Vehicule de urgență = 0" +
                "<br>Frecvența schimbării semaforului = 0 schimbări/minut</html>");
        monitoringLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        monitoringPanel.add(monitoringLabel);

        gbc.gridy = 1;
        gbc.weighty = 0.3;
        leftPanel.add(monitoringPanel, gbc);


        // 3. emergencyPanel
        emergencyPanel = new JPanel(new BorderLayout());
        emergencyPanel.setBorder(BorderFactory.createTitledBorder("Emergency Vehicles"));
        emergencyListModel = new DefaultListModel<>();
        emergencyList = new JList<>(emergencyListModel);
        emergencyPanel.add(new JScrollPane(emergencyList), BorderLayout.CENTER);

        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        leftPanel.add(emergencyPanel, gbc);


        // --- Inițializare și Configurare rightPanel ---
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(2, 1, 10, 10));

        // 1. vehiclePanel
        vehiclePanel = new JPanel(new BorderLayout());
        vehiclePanel.setBorder(BorderFactory.createTitledBorder("Normal Vehicles"));
        vehicleListModel = new DefaultListModel<>();
        vehicleList = new JList<>(vehicleListModel);
        vehiclePanel.add(new JScrollPane(vehicleList), BorderLayout.CENTER);
        rightPanel.add(vehiclePanel);

        // 2. pedestrianPanel
        pedestrianPanel = new JPanel(new BorderLayout());
        pedestrianPanel.setBorder(BorderFactory.createTitledBorder("Pedestrians"));
        pedestrianListModel = new DefaultListModel<>();
        pedestrianList = new JList<>(pedestrianListModel);
        pedestrianPanel.add(new JScrollPane(pedestrianList), BorderLayout.CENTER);
        rightPanel.add(pedestrianPanel);


        // --- Adaugă ambele panouri la frame ---
        frame.add(leftPanel);
        frame.add(rightPanel);

        frame.setVisible(true);
    }

    // --- (restul metodelor rămân la fel) ---
    public void setTrafficLightState(String state) {
        redCircle.setOn(state.equals("RED") || state.equals("RED_PEDESTRIAN_WAITING"));
        yellowCircle.setOn(state.equals("YELLOW"));
        greenCircle.setOn(state.equals("GREEN"));

        if (state.equals("GREEN_PEDESTRIAN")) {
            redCircle.setOn(true);
            yellowCircle.setOn(false);
            greenCircle.setOn(true);
            stateLabel.setText("Current state: GREEN (Pedestrians)");
        } else {
            stateLabel.setText("Current state: " + state);
        }

        lightPanel.repaint();
    }

    public void updateMonitoring(int vehicule, int pedestrian, int emergencyVehicles, long averageWaitingTimeVehicles, double lightChangeFrequency) {
        String displayText = String.format("<html><br>Masini în intersecție = %d" +
                        "<br>Pietoni in intersectie = %d" +
                        "<br>Vehicule de urgență = %d" +
                        "<br>Frecvența schimbării semaforului = %.2f schimbări/minut</html>",
                vehicule,
                pedestrian,
                emergencyVehicles,
                lightChangeFrequency
        );
        monitoringLabel.setText(displayText);
    }

    public void updateAgentStatus(String type, String agentName, String status) {
        DefaultListModel<String> targetListModel;
        JList<String> targetList;
        switch (type) {
            case "Emergency" -> {
                targetListModel = emergencyListModel;
                targetList = emergencyList;
            }
            case "Vehicule" -> {
                targetListModel = vehicleListModel;
                targetList = vehicleList;
            }
            case "Pedestrian" -> {
                targetListModel = pedestrianListModel;
                targetList = pedestrianList;
            }
            default -> {
                System.err.println("Unknown agent type for GUI update: " + type);
                return;
            }
        }

        boolean updated = false;
        for (int i = 0; i < targetListModel.getSize(); i++) {
            String element = targetListModel.get(i);
            if (element.startsWith(agentName + ":")) {
                targetListModel.set(i, agentName + ": " + status);
                updated = true;
                break;
            }
        }
        if (!updated) {
            targetListModel.addElement(agentName + ": " + status);
        }
        targetList.ensureIndexIsVisible(targetListModel.getSize() - 1);
    }

    class LightCircle extends JPanel {
        private Color color;
        private boolean isOn = false;

        public LightCircle(Color color) {
            this.color = color;
            this.setPreferredSize(new Dimension(50, 50));
            this.setMaximumSize(new Dimension(50, 50));
            this.setMinimumSize(new Dimension(50, 50));
        }

        public void setOn(boolean on) {
            this.isOn = on;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g.setColor(isOn ? color : color.darker().darker());
            g.fillOval(x, y, size, size);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, size, size);
        }
    }
}