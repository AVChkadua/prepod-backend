package ru.mephi.prepod.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.Charset;
import java.util.Base64;

public class Base64PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return Base64.getEncoder().encodeToString(rawPassword.toString().getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
