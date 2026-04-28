package service;

import model.Candidate;
import model.User;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Lightweight JSON-based persistence for VoteService state.
 * Uses only the Java standard library — no external dependencies.
 *
 * Save file location: <project root>/data/votingdata.json
 */
public class DataStore {

    // Resolve the data directory relative to the working directory (project root)
    private static final String DATA_DIR  = "data";
    private static final String DATA_FILE = DATA_DIR + File.separator + "votingdata.json";

    // ── SAVE ──────────────────────────────────────────────────────────────────

    public static void save(VoteService vs) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");

            // Election state
            sb.append("  \"electionOpen\": ").append(vs.isElectionOpen()).append(",\n");

            // Voters
            sb.append("  \"voters\": [\n");
            List<User> voters = vs.getVoters();
            for (int i = 0; i < voters.size(); i++) {
                User u = voters.get(i);
                sb.append("    {");
                sb.append("\"voterId\": ").append(jsonStr(u.getVoterId())).append(", ");
                sb.append("\"name\": ").append(jsonStr(u.getName())).append(", ");
                sb.append("\"mobile\": ").append(jsonStr(u.getMobile())).append(", ");
                sb.append("\"email\": ").append(jsonStr(u.getEmail())).append(", ");
                sb.append("\"password\": ").append(jsonStr(u.getPassword())).append(", ");
                sb.append("\"hasVoted\": ").append(u.hasVoted());
                sb.append("}");
                if (i < voters.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ],\n");

            // Candidates
            sb.append("  \"candidates\": [\n");
            List<Candidate> candidates = vs.getCandidates();
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                sb.append("    {");
                sb.append("\"candidateId\": ").append(c.getCandidateId()).append(", ");
                sb.append("\"name\": ").append(jsonStr(c.getName())).append(", ");
                sb.append("\"party\": ").append(jsonStr(c.getParty())).append(", ");
                sb.append("\"mobile\": ").append(jsonStr(c.getMobile())).append(", ");
                sb.append("\"voteCount\": ").append(c.getVoteCount());
                sb.append("}");
                if (i < candidates.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ],\n");

            // Candidate credentials: { loginId: [password, name, mobile] }
            sb.append("  \"candidateCredentials\": [\n");
            Map<String, String[]> creds = vs.getCandidateCredentials();
            List<Map.Entry<String, String[]>> credList = new ArrayList<>(creds.entrySet());
            for (int i = 0; i < credList.size(); i++) {
                Map.Entry<String, String[]> e = credList.get(i);
                String[] val = e.getValue();
                sb.append("    {");
                sb.append("\"loginId\": ").append(jsonStr(e.getKey())).append(", ");
                sb.append("\"password\": ").append(jsonStr(val[0])).append(", ");
                sb.append("\"name\": ").append(jsonStr(val[1])).append(", ");
                // val[2] is mobile (may not exist in old data, default "")
                sb.append("\"mobile\": ").append(jsonStr(val.length > 2 ? val[2] : ""));
                sb.append("}");
                if (i < credList.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n");

            sb.append("}\n");

            Files.writeString(Paths.get(DATA_FILE), sb.toString());
        } catch (IOException ex) {
            System.err.println("[DataStore] Save failed: " + ex.getMessage());
        }
    }

    // ── LOAD ──────────────────────────────────────────────────────────────────

    /**
     * Loads persisted state into VoteService.
     * Returns true if data was loaded from file; false if no save file exists (first run).
     */
    public static boolean load(VoteService vs) {
        File f = new File(DATA_FILE);
        if (!f.exists()) return false;

        try {
            String json = Files.readString(f.toPath());

            // --- election open ---
            Boolean electionOpen = parseBool(json, "electionOpen");
            if (electionOpen != null) vs.setElectionOpenInternal(electionOpen);

            // --- voters ---
            String votersArr = extractArray(json, "voters");
            if (votersArr != null) {
                vs.getVoters().clear();
                List<Map<String, String>> rows = parseObjectArray(votersArr);
                for (Map<String, String> row : rows) {
                    User u = new User(
                        row.getOrDefault("voterId", ""),
                        row.getOrDefault("name", ""),
                        row.getOrDefault("mobile", ""),
                        row.getOrDefault("email", ""),
                        row.getOrDefault("password", "")
                    );
                    if ("true".equals(row.get("hasVoted"))) u.setHasVoted(true);
                    vs.getVoters().add(u);
                }
            }

            // --- candidates ---
            String candsArr = extractArray(json, "candidates");
            if (candsArr != null) {
                vs.getCandidates().clear();
                List<Map<String, String>> rows = parseObjectArray(candsArr);
                for (Map<String, String> row : rows) {
                    int id = parseInt(row.getOrDefault("candidateId", "0"));
                    int vc = parseInt(row.getOrDefault("voteCount", "0"));
                    Candidate c = new Candidate(id,
                        row.getOrDefault("name", ""),
                        row.getOrDefault("party", ""),
                        row.getOrDefault("mobile", "")
                    );
                    c.setVoteCount(vc);
                    vs.getCandidates().add(c);
                }
            }

            // --- candidate credentials ---
            String credsArr = extractArray(json, "candidateCredentials");
            if (credsArr != null) {
                vs.getCandidateCredentialsMap().clear();
                List<Map<String, String>> rows = parseObjectArray(credsArr);
                for (Map<String, String> row : rows) {
                    String loginId  = row.getOrDefault("loginId", "");
                    String password = row.getOrDefault("password", "");
                    String name     = row.getOrDefault("name", "");
                    String mobile   = row.getOrDefault("mobile", "");
                    vs.getCandidateCredentialsMap().put(loginId, new String[]{password, name, mobile});
                }
            }

            System.out.println("[DataStore] Loaded saved state from " + DATA_FILE);
            return true;
        } catch (Exception ex) {
            System.err.println("[DataStore] Load failed: " + ex.getMessage());
            return false;
        }
    }

    // ── MINI JSON HELPERS ──────────────────────────────────────────────────────

    /** Wraps a string value in JSON quotes, escaping backslashes and double-quotes. */
    private static String jsonStr(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /** Extracts a top-level boolean value from a flat JSON object string. */
    private static Boolean parseBool(String json, String key) {
        String pattern = "\"" + key + "\"";
        int ki = json.indexOf(pattern);
        if (ki < 0) return null;
        int colon = json.indexOf(':', ki);
        if (colon < 0) return null;
        String rest = json.substring(colon + 1).trim();
        if (rest.startsWith("true"))  return true;
        if (rest.startsWith("false")) return false;
        return null;
    }

    /**
     * Extracts the raw content of a top-level JSON array by key.
     * e.g. extractArray(json, "voters") returns the string between the outer [ and ].
     */
    private static String extractArray(String json, String key) {
        String marker = "\"" + key + "\"";
        int ki = json.indexOf(marker);
        if (ki < 0) return null;
        int bracket = json.indexOf('[', ki);
        if (bracket < 0) return null;
        int depth = 0; int end = -1;
        for (int i = bracket; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (ch == '[') depth++;
            else if (ch == ']') { depth--; if (depth == 0) { end = i; break; } }
        }
        if (end < 0) return null;
        return json.substring(bracket + 1, end);
    }

    /**
     * Parses a JSON array of flat objects (no nested objects/arrays).
     * Returns a list of maps from field name → string value.
     */
    private static List<Map<String, String>> parseObjectArray(String arrayContent) {
        List<Map<String, String>> result = new ArrayList<>();
        int i = 0;
        while (i < arrayContent.length()) {
            int objStart = arrayContent.indexOf('{', i);
            if (objStart < 0) break;
            int objEnd = arrayContent.indexOf('}', objStart);
            if (objEnd < 0) break;
            String obj = arrayContent.substring(objStart + 1, objEnd);
            result.add(parseObject(obj));
            i = objEnd + 1;
        }
        return result;
    }

    /**
     * Parses a flat JSON object body (the part between { and }).
     * Handles string and non-string values.
     */
    private static Map<String, String> parseObject(String obj) {
        Map<String, String> map = new LinkedHashMap<>();
        int i = 0;
        while (i < obj.length()) {
            // Find key
            int qs = obj.indexOf('"', i);
            if (qs < 0) break;
            int qe = obj.indexOf('"', qs + 1);
            if (qe < 0) break;
            String key = obj.substring(qs + 1, qe);
            int colon = obj.indexOf(':', qe);
            if (colon < 0) break;
            String rest = obj.substring(colon + 1).trim();
            String value;
            if (rest.startsWith("\"")) {
                // String value — find closing quote, respecting escape sequences
                int vs = colon + 1 + obj.substring(colon + 1).indexOf('"');
                int ve = vs + 1;
                while (ve < obj.length()) {
                    if (obj.charAt(ve) == '"' && obj.charAt(ve - 1) != '\\') break;
                    ve++;
                }
                value = obj.substring(vs + 1, ve)
                           .replace("\\\"", "\"")
                           .replace("\\\\", "\\");
                i = ve + 1;
            } else {
                // Non-string (number, boolean)
                int comma = rest.indexOf(',');
                value = comma >= 0 ? rest.substring(0, comma).trim() : rest.trim();
                i = colon + 1 + obj.substring(colon + 1).indexOf(value) + value.length();
            }
            map.put(key, value);
        }
        return map;
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
