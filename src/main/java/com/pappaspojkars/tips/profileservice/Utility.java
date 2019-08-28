package com.pappaspojkars.tips.profileservice;

import org.springframework.beans.factory.annotation.Autowired;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    @Autowired
   static ProfileRepo profileRepo;

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final Integer SECONDS_UNTIL_AUTOMATIC_LOGOUT = 15*60;
    public static final Integer SECONDS_OF_LOGIN_DENIAL = 60*60;
    public static final ZoneOffset SERVER_OFFSET = ZoneOffset.UTC;


    /** Generates a token, used to authenticate a login attempt.
     *
     * @return The generated token. Should be sent to DB and to the client.
     */
    public static String generateToken() {
        int count = 20;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    /** Encodes a String using MD5 and some salt.
     *  Should be used on every password... EVER!
     *
     * @param password Input which should be encoded.
     * @return  Encoded String
     */
    public static String MD5Encode(String password) {
        try {
            byte[] bytes = MessageDigest.getInstance("MD5")
                    .digest(password.concat("salt").getBytes()); //TODO fix salt Enviroment variable

            StringBuilder sb = new StringBuilder();
            for (byte b: bytes) {
                sb.append(Integer.toString((b & 0xff)  + 0x100, 16));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isFullname(String str) {
        return str.matches("^[a-zA-z ]*$");
    }


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static final Pattern VALID_PHONE_REGEX =
            Pattern.compile("^[0-9]{10,13}$", Pattern.CASE_INSENSITIVE);

    public static boolean validatePhone(String phoneStr) {
        Matcher matcher = VALID_PHONE_REGEX .matcher(phoneStr);
        return matcher.find();
    }


}
