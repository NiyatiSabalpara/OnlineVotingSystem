package service;

import model.User;
import model.Candidate;

import java.util.ArrayList;
import java.util.List;

public class VoteService {

    private static VoteService instance = new VoteService();

    public static VoteService getInstance() {
        return instance;
    }

    private List<User> voters = new ArrayList<>();
    private List<Candidate> candidates = new ArrayList<>();
    private User currentUser = null;
    private boolean electionOpen = true;

    private VoteService() {
        voters.add(new User("V101", "Niyati Sabalpara", "9999999999", "niyati@example.com", "pass"));
        voters.add(new User("V102", "Rahul Sharma", "8888888888", "rahul@example.com", "pass"));
        voters.add(new User("V103", "Priya Mehta", "7777777777", "priya@example.com", "pass"));

        candidates.add(new Candidate(1, "Alice Johnson", "Progressive Alliance"));
        candidates.add(new Candidate(2, "Bob Williams", "National Unity Party"));
        candidates.add(new Candidate(3, "Charlie Davis", "Democratic Front"));
    }

    public String registerVoter(String name, String mobile, String email, String password) {
        String newId = "V" + (101 + voters.size());
        User newUser = new User(newId, name, mobile, email, password);
        voters.add(newUser);
        return newId;
    }

    /**
     * Authenticates and returns the role based on ID/Pass.
     */
    public String authenticate(String id, String password) {
        if ("admin".equals(id) && "admin123".equals(password)) {
            return "ADMIN";
        }

        if (("cand_alice".equals(id) && "pass1".equals(password)) ||
            ("cand_bob".equals(id) && "pass2".equals(password)) ||
            ("cand_charlie".equals(id) && "pass3".equals(password))) {

            // Set current candidate user for portal personalization
            if ("cand_alice".equals(id)) currentUser = new User("cand_alice", "Alice Johnson", "", "", "pass1");
            else if ("cand_bob".equals(id)) currentUser = new User("cand_bob", "Bob Williams", "", "", "pass2");
            else if ("cand_charlie".equals(id)) currentUser = new User("cand_charlie", "Charlie Davis", "", "", "pass3");
            return "CANDIDATE";
        }

        for (User u : voters) {
            if (u.getVoterId().equals(id) && u.getPassword().equals(password)) {
                this.currentUser = u;
                return "VOTER";
            }
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean castVote(int candidateId) {
        if (!electionOpen) return false;
        if (currentUser == null || currentUser.hasVoted()) {
            return false;
        }

        for (Candidate c : candidates) {
            if (c.getCandidateId() == candidateId) {
                c.addVote();
                currentUser.setHasVoted(true);

                SmsService.send(currentUser.getMobile(), "Your vote has been successfully cast. Thank you for voting!");
                EmailService.send(currentUser.getEmail(), "Vote Confirmed",
                        "Hello " + currentUser.getName() + ",\nYour vote has been successfully cast.\nThank you!");

                return true;
            }
        }
        return false;
    }

    // --- Election Control ---

    public boolean isElectionOpen() {
        return electionOpen;
    }

    public void setElectionOpen(boolean open) {
        this.electionOpen = open;
    }

    // --- Statistics Helpers ---

    public int getTotalVotesCast() {
        int total = 0;
        for (Candidate c : candidates) {
            total += c.getVoteCount();
        }
        return total;
    }

    public double getTurnoutPercent() {
        if (voters.isEmpty()) return 0;
        long voted = voters.stream().filter(User::hasVoted).count();
        return (voted * 100.0) / voters.size();
    }

    public Candidate getWinner() {
        Candidate winner = null;
        for (Candidate c : candidates) {
            if (winner == null || c.getVoteCount() > winner.getVoteCount()) {
                winner = c;
            }
        }
        return winner;
    }

    public int getVoterCount() {
        return voters.size();
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public List<User> getVoters() {
        return voters;
    }

    public List<String> getCandidateNames() {
        List<String> names = new ArrayList<>();
        for (Candidate c : candidates) {
            names.add(c.getName());
        }
        return names;
    }

    public String getResults() {
        StringBuilder result = new StringBuilder();
        for (Candidate c : candidates) {
            result.append(c.getName()).append(" : ").append(c.getVoteCount()).append(" votes\n");
        }
        return result.toString();
    }
}