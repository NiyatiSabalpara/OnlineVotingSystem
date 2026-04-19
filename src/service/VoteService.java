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
    private User currentUser = null; // Track logged-in user

    private VoteService() {
        voters.add(new User("V101", "Niyati", "9999999999", "pass"));
        voters.add(new User("V102", "Rahul", "8888888888", "pass"));

        candidates.add(new Candidate(1, "Alice"));
        candidates.add(new Candidate(2, "Bob"));
        candidates.add(new Candidate(3, "Charlie"));
    }

    public String registerVoter(String name, String mobile, String password) {
        String newId = "V" + (101 + voters.size()); 
        User newUser = new User(newId, name, mobile, password);
        voters.add(newUser);
        return newId;
    }

    /**
     * Authenticates and returns the role based on ID/Pass.
     */
    public String authenticate(String id, String password) {
        // Admin separate credentials
        if ("admin".equals(id) && "admin123".equals(password)) {
            return "ADMIN";
        }
        
        // Distinct candidate credentials
        if (("cand_alice".equals(id) && "pass1".equals(password)) ||
            ("cand_bob".equals(id) && "pass2".equals(password)) ||
            ("cand_charlie".equals(id) && "pass3".equals(password))) {
            return "CANDIDATE";
        }

        for (User u : voters) {
            if (u.getVoterId().equals(id) && u.getPassword().equals(password)) {
                this.currentUser = u;
                return "VOTER";
            }
        }
        return null; // authentication failed
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean castVote(int candidateId) {
        if (currentUser == null || currentUser.hasVoted()) {
            return false;
        }

        for (Candidate c : candidates) {
            if (c.getCandidateId() == candidateId) {
                c.addVote();
                currentUser.setHasVoted(true);
                return true;
            }
        }
        return false;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public List<String> getCandidateNames() {
        List<String> names = new ArrayList<>();
        for (Candidate c : candidates) {
            names.add(c.getName());
        }
        return names;
    }

    public String getResults() {
        String result = "";
        for (Candidate c : candidates) {
            result += c.getName() + " : " + c.getVoteCount() + " votes\n";
        }
        return result;
    }
}