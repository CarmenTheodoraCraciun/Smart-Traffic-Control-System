package org;

public enum ParticipantType {
    VEHICLE("Green"),
    PEDESTRIAN("Red");

    private final String crossSignal;

    ParticipantType(String crossSignal) {
        this.crossSignal = crossSignal;
    }

    public String getCrossSignal() {
        return crossSignal;
    }
}
