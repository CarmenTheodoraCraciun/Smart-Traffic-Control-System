package org;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TrafficLightAgent extends Agent {
    private String state = "RED";
    private boolean emergencyActive = false;
    private TrafficControlGUI gui;

    protected void setup() {
        // Primim GUI-ul ca parametru
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            gui = (TrafficControlGUI) args[0];
        } else {
            System.out.println("TrafficLightAgent: No GUI received!");
        }

        System.out.println("Traffic Light Agent started.");
        gui.setTrafficLightState(state);

        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                if (!emergencyActive) {
                    if (state.equals("RED")) {
                        state = "GREEN";
                    } else if (state.equals("GREEN")) {
                        state = "YELLOW";
                    } else if (state.equals("YELLOW")) {
                        state = "RED";
                    }

                    gui.setTrafficLightState(state);
                    System.out.println("Traffic Light changed to: " + state);
                }
            }
        });
    }
}
