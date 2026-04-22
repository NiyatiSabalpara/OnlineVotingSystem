package service;

public class EmailService {

    public static void send(String email, String subject, String message) {
        System.out.println("EMAIL to " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }
}
