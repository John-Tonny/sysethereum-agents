package info.vircletrx.agents.service;

import info.vircletrx.agents.core.vircle.Keccak256Hash;

import java.util.List;

public class ChallengeReport {

    public final boolean isAtLeastOneMine;
    public final List<Keccak256Hash> challenged;

    public ChallengeReport(boolean isAtLeastOneMine, List<Keccak256Hash> challenged) {

        this.isAtLeastOneMine = isAtLeastOneMine;
        this.challenged = challenged;
    }
}