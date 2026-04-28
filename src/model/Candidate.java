package model;

public class Candidate {

    private int candidateId;
    private String name;
    private String party;
    private String mobile;
    private int voteCount;

    public Candidate(int candidateId, String name, String party) {
        this(candidateId, name, party, "");
    }

    public Candidate(int candidateId, String name, String party, String mobile) {
        this.candidateId = candidateId;
        this.name = name;
        this.party = party;
        this.mobile = mobile;
        this.voteCount = 0;
    }

    // Backward-compat constructor
    public Candidate(int candidateId, String name) {
        this(candidateId, name, "Independent", "");
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void addVote() {
        voteCount++;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}