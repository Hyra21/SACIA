package com.alucintech.saci.helpers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CifradorHelper {

    private static final String ALGORITMO = "AES";
        private static final String CLAVE_SECRETA = "clavecimarnetSACI789223@"; // La clave debe tener 16, 24 o 32 bytes para AES

    public static String descifrar(String textoCifrado) throws Exception {
        try {
            SecretKeySpec clave = new SecretKeySpec(CLAVE_SECRETA.getBytes(), ALGORITMO);
            Cipher descifrador = Cipher.getInstance(ALGORITMO);
            descifrador.init(Cipher.DECRYPT_MODE, clave);
            byte[] textoBytes = Base64.getDecoder().decode(textoCifrado);
            return new String(descifrador.doFinal(textoBytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            // Manejar la excepción apropiadamente
            return null;
        }
    }
}
