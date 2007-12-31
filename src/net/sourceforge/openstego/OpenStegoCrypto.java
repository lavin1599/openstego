/*
 * Utility to embed data into images
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) 2007 Samir Vaidya
 */

package net.sourceforge.openstego;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import net.sourceforge.openstego.util.LabelUtil;

/**
 * This is the class for providing cryptography support to OpenStego.
 */
public class OpenStegoCrypto
{
    /**
     * 8-byte Salt for Password-based cryptography
     */
    private final byte[] SALT = {
            (byte) 0x28, (byte) 0x5F, (byte) 0x71, (byte) 0xC9,
            (byte) 0x1E, (byte) 0x35, (byte) 0x0A, (byte) 0x62 };

    /**
     * Iteration count for Password-based cryptography
     */
    private final int ITER_COUNT = 7;

    /**
     * Cipher to use for encryption
     */
    private Cipher encryptCipher = null;

    /**
     * Cipher to use for decryption
     */
    private Cipher decryptCipher = null;

    /**
     * Default constructor
     * @param password Password to use for encryption
     * @throws Exception
     */
    OpenStegoCrypto(String password) throws Exception
    {
        KeySpec keySpec = null;
        SecretKey secretKey = null;
        AlgorithmParameterSpec algoParamSpec = null;
        
        if(password == null)
        {
            password = "";
        }

        // Create the key
        keySpec = new PBEKeySpec(password.toCharArray(), SALT, ITER_COUNT);
        secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        encryptCipher = Cipher.getInstance(secretKey.getAlgorithm());
        decryptCipher = Cipher.getInstance(secretKey.getAlgorithm());

        // Prepare cipher parameters
        algoParamSpec = new PBEParameterSpec(SALT, ITER_COUNT);

        // Initialize the ciphers
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, algoParamSpec);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, algoParamSpec);
    }

    /**
     * Method to encrypt the data
     * @param input Data to be encrypted
     * @return Encrypted data
     * @throws Exception
     */
    public byte[] encrypt(byte[] input) throws Exception
    {
        return encryptCipher.doFinal(input);
    }

    /**
     * Method to decrypt the data
     * @param input Data to be decrypted
     * @return Decrypted data
     * @throws Exception
     */
    public byte[] decrypt(byte[] input) throws Exception
    {
        try
        {
            return decryptCipher.doFinal(input);
        }
        catch(BadPaddingException bpEx)
        {
            throw new Exception(LabelUtil.getString("err.config.password.invalid"));
        }
    }
}