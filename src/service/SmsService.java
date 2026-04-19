package service;
public class SmsService {

    public static void send(String mobile, String message) {
        System.out.println("SMS to " + mobile + ": " + message);
    }
}