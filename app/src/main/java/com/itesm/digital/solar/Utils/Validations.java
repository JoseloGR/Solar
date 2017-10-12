package com.itesm.digital.solar.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {

    //VALIDATE EMAIL
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //VALIDATE PASSWORD
    public static boolean isValidPassword(String pass) {
        return pass != null && pass.length() >= 4;
    }

    public static boolean isSamePassword(String pass, String passC){
        return pass.equals(passC);
    }
}
