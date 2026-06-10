package com.sromip.auth.util;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class PasswordValidator {

    public static void validate(String password) {

        if (password.length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Min 8 chars");

        if (!password.matches(".*[A-Z].*"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Need uppercase");

        if (!password.matches(".*[0-9].*"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Need number");

        if (!password.matches(".*[@#$%^&+=].*"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Need special char");
    }
}