import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class FileCrypto
{
    private SecretKey symmetricKey;
    private Cipher crypto;
    private final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final String ALGORITHM = "AES";
    private byte[] salt;
    private String password;

    public FileCrypto(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException
    {
        salt = generateSalt();
        this.password = password;
        symmetricKey = generateSecretKey(password);
        crypto = Cipher.getInstance(TRANSFORMATION);
    }

    private byte[] generateSalt()
    {
        byte[] salt = new byte[8];
        SecureRandom sRandom = new SecureRandom();
        sRandom.nextBytes(salt);
        return salt;
    }

    private SecretKey generateSecretKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        KeyGenerator keyGenerator = null;
//        keyGenerator = KeyGenerator.getInstance(algorithm);
//        SecureRandom secureRandom = new SecureRandom();
//        int keyBitSize = 128;
//        keyGenerator.init(keyBitSize, secureRandom);
//
//        return keyGenerator.generateKey();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        return skey;
    }

    public void encryptFile(File inputFile, String outputFileName) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        byte[] iv = new byte[16];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(iv);
        crypto.init(Cipher.ENCRYPT_MODE,symmetricKey, new IvParameterSpec(iv));
        String saltStr = new String(salt);
        String ivStr = new String(iv);

        String plaintext = new String(Files.readAllBytes(inputFile.toPath()));
        System.out.println("This is the plaintext: " + plaintext);
        System.out.println("This is in encryp --> salt: " + saltStr + " iv: " + ivStr);

        FileOutputStream outputStream = new FileOutputStream(outputFileName);
        outputStream.write(salt);
        outputStream.write(iv);
        CipherOutputStream cipherOutput = new CipherOutputStream(outputStream,crypto);
        //cipherOutput.write(Files.readAllBytes(inputFile.toPath()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(cipherOutput));
        pw.println(plaintext);

        pw.close();
        outputStream.close();
        cipherOutput.close();
        FileInputStream in = new FileInputStream(outputFileName);
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader inb = new BufferedReader(inr);
        System.out.println("This should be the plaintext encrypted: " + inb.readLine());
        in.close();
        inr.close();
        inb.close();
    }

    public void decryptFile(File inputFile, String outputFileName) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] ivFromFile = new byte[16];
        byte[] salt = new byte[8];
        FileInputStream inputStream = new FileInputStream(inputFile);
        inputStream.read(salt);
        inputStream.read(ivFromFile);
        String saltStr = new String(salt);
        String ivStr = new String(ivFromFile);
        System.out.println("This is the salt: " + saltStr);
        System.out.println("This is the iv: " + ivStr);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

        crypto.init(Cipher.DECRYPT_MODE,skey,new IvParameterSpec(ivFromFile));
        CipherInputStream cipherInput = new CipherInputStream(inputStream,crypto);
//        byte[] plainText = new byte[(int) inputFile.length()];
//        cipherInput.read(plainText);
//        String plainTextStr = new String(plainText);
        InputStreamReader inputReader = new InputStreamReader(cipherInput);
        BufferedReader reader = new BufferedReader(inputReader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String plainTextStr = sb.toString();

        reader.close();
        inputReader.close();
        cipherInput.close();
        inputStream.close();

        System.out.println("This is the decrypted data: " + plainTextStr);
        FileOutputStream outputStream = new FileOutputStream(outputFileName);
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        PrintWriter pw = new PrintWriter(streamWriter);
        pw.println(plainTextStr);
        pw.close();
        streamWriter.close();
        outputStream.close();
    }
}
