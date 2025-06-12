package org;

public class VehicleAgent extends TrafficParticipantAgent {
    @Override
    protected ParticipantType getParticipantType() {
        return ParticipantType.VEHICLE;
    }
}
