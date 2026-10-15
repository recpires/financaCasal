package br.com.fiap.fintech.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Gera o hash SHA-256 da senha, para nao guardar a senha em texto puro no banco. */
public final class SenhaUtil {

    private SenhaUtil() {
    }

    public static String hash(String senha) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel", e);
        }
    }
}
