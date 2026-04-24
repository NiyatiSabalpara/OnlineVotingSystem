package model;

public class Candidate {

    private int candidateId;
    private String name;
    private String party;
    private int voteCount;

    public Candidate(int candidateId, String name, String party) {
        this.candidateId = candidateId;
        this.name = name;
        this.party = party;
        this.voteCount = 0;
    }

    // Backward-compat constructor
    public Candidate(int candidateId, String name) {
        this(candidateId, name, "Independent");
    }

    public int getCandidateId() {
        return candidateId;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void addVote() {
        voteCount++;
    }
}