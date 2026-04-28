package service;

import model.User;
import model.Candidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteService {

    private static VoteService instance = new VoteService();

    public static VoteService getInstance() {
        return instance;
    }

    private List<User> voters = new ArrayList<>();
    private List<Candidate> candidates = new ArrayList<>();
    // Maps candidateLoginId -> [password, candidateName]
    private Map<String, String[]> candidateCredentials = new HashMap<>();
    private User currentUser = null;
    private boolean electionOpen = true;

    private VoteService() {
        voters.add(new User("V101", "Niyati Sabalpara", "9999999999", "niyati@example.com", "pass"));
        voters.add(new User("V102", "Rahul Sharma",     "8888888888", "rahul@example.com",  "pass"));
        voters.add(new User("V103", "Priya Mehta",      "7777777777", "priya@example.com",  "pass"));

        // Seed demo candidates (no SMS sent for demo data)
        addCandidateInternal("Alice Johnson",  "Progressive Alliance", "0000000000", false);
        addCandidateInternal("Bob Williams",   "National Unity Party",  "0000000000", false);
        addCandidateInternal("Charlie Davis",  "Democratic Front",      "0000000000", false);

        // Load persisted state — overrides seed data if a save file exists
        DataStore.load(this);
    }

    /**
     * Adds a new candidate via admin UI — auto-generates credentials and sends SMS.
     * @return the generated login ID
     */
    public String addCandidate(String name, String party, String mobile) {
        String loginId = addCandidateInternal(name, party, mobile, true);
        DataStore.save(this);
        return loginId;
    }

    /**
     * Internal helper used for seeding and loading from DataStore (no SMS).
     */
    private String addCandidateInternal(String name, String party, String mobile, boolean sendSms) {
        int newId = candidates.size() + 1;
        Candidate c = new Candidate(newId, name, party, mobile);
        candidates.add(c);
        String firstName = name.trim().split("\\s+")[0].toLowerCase();
        String loginId   = "cand_" + firstName;
        String password  = "pass" + newId;
        candidateCredentials.put(loginId, new String[]{password, name, mobile});
        if (sendSms && mobile != null && !mobile.isBlank()) {
            SmsService.send(mobile,
                "Your Candidate Portal credentials — Login ID: " + loginId
                + " | Password: " + password
                + " | Portal: Candidate Login");
        }
        return loginId;
    }

    /**
     * Removes a candidate by their ID.
     */
    public boolean removeCandidate(int candidateId) {
        return candidates.removeIf(c -> c.getCandidateId() == candidateId);
    }

    /**
     * Returns a copy of candidate credentials for display.
     * Key = loginId, Value = [password, fullName, mobile]
     */
    public Map<String, String[]> getCandidateCredentials() {
        return new HashMap<>(candidateCredentials);
    }

    /** Direct access for DataStore loading — do not call from UI. */
    public Map<String, String[]> getCandidateCredentialsMap() {
        return candidateCredentials;
    }

    public String registerVoter(String name, String mobile, String email, String password) {
        String newId = "V" + (101 + voters.size());
        User newUser = new User(newId, name, mobile, email, password);
        voters.add(newUser);
        DataStore.save(this);
        return newId;
    }

    /**
     * Authenticates and returns the role based on ID/Pass.
     */
    public String authenticate(String id, String password) {
        if ("admin".equals(id) && "admin123".equals(password)) {
            return "ADMIN";
        }

        // Dynamic candidate authentication
        if (candidateCredentials.containsKey(id)) {
            String[] cred = candidateCredentials.get(id);
            if (cred[0].equals(password)) {
                currentUser = new User(id, cred[1], "", "", cred[0]);
                return "CANDIDATE";
            }
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

                DataStore.save(this);
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
        DataStore.save(this);
    }

    /** Used by DataStore only — does not trigger a save. */
    public void setElectionOpenInternal(boolean open) {
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