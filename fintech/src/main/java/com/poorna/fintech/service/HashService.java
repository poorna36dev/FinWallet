package com.poorna.fintech.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashService {

    private final ObjectMapper objectMapper;

    public String generateHash(Object request) {

        try {

            String json = objectMapper.writeValueAsString(request);

            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    messageDigest.digest(json.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (JsonProcessingException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unable to generate request hash", ex);
        }
    }
}
