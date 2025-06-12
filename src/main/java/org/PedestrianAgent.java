package org;

public class PedestrianAgent extends TrafficParticipantAgent {
    @Override
    protected ParticipantType getParticipantType() {
        return ParticipantType.PEDESTRIAN;
    }
}
