package com.alucintech.saci.helpers;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CifradorHelper {

    private static final String ALGORITMO = "AES";
    private static final String CLAVE_SECRETA = "clavecimarnetSACI789223@"; // La clave debe tener 16, 24 o 32 bytes para AES

    public static String cifrar(String texto) throws Exception {
        SecretKeySpec clave = new SecretKeySpec(CLAVE_SECRETA.getBytes(), ALGORITMO);
        Cipher cifrador = Cipher.getInstance(ALGORITMO);
        cifrador.init(Cipher.ENCRYPT_MODE, clave);
        byte[] textoCifrado = cifrador.doFinal(texto.getBytes());
        return Base64.encodeToString(textoCifrado, Base64.DEFAULT);
    }

    public static String descifrar(String textoCifrado) throws Exception {
        SecretKeySpec clave = new SecretKeySpec(CLAVE_SECRETA.getBytes(), ALGORITMO);
        Cipher descifrador = Cipher.getInstance(ALGORITMO);
        descifrador.init(Cipher.DECRYPT_MODE, clave);
        byte[] textoBytes = descifrador.doFinal(Base64.decode(textoCifrado, Base64.DEFAULT));
        return new String(textoBytes);
    }
}
