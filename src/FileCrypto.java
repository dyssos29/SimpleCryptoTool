import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class FileCrypto
{
    private Cipher crypto;
    private String password;
    private final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final String ALGORITHM = "AES";
    private final byte[] headerIdentifier = "Dyssos29".getBytes();
    private FileInputStream inputStream;

    public FileCrypto(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException
    {
        this.password = password;
        crypto = Cipher.getInstance(TRANSFORMATION);
    }

    private byte[] generateSalt()
    {
        byte[] salt = new byte[8];
        SecureRandom sRandom = new SecureRandom();
        sRandom.nextBytes(salt);
        return salt;
    }

    private byte[] generateIV()
    {
        byte[] iv = new byte[16];
        SecureRandom sRandom = new SecureRandom();
        sRandom.nextBytes(iv);
        return iv;
    }

    private SecretKey generateSecretKey(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        return key;
    }

    public boolean checkIfCipherText(File inputFile) throws IOException
    {
        byte[] header = new byte[8];
        inputStream = new FileInputStream(inputFile);
        inputStream.read(header);
        return Arrays.equals(header,headerIdentifier);
    }

    public void closeInputStream() throws IOException
    {
        inputStream.close();
    }

    public void encryptFile(File inputFile, String outputFileName) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        byte[] salt = generateSalt();
        byte[] iv = generateIV();
        SecretKey symmetricKey = generateSecretKey(salt);
        crypto.init(Cipher.ENCRYPT_MODE,symmetricKey, new IvParameterSpec(iv));

        String plaintext = new String(Files.readAllBytes(inputFile.toPath()));

        String pathAndNameOfOutputFile = inputFile.toPath().toString().replaceFirst(inputFile.getName(),outputFileName);
        FileOutputStream outputStream = new FileOutputStream(pathAndNameOfOutputFile);
        outputStream.write(headerIdentifier);
        outputStream.write(salt);
        outputStream.write(iv);
        CipherOutputStream cipherOutput = new CipherOutputStream(outputStream,crypto);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(cipherOutput));
        pw.println(plaintext);

        pw.close();
        outputStream.close();
        cipherOutput.close();
    }

    public void decryptFile(File inputFile, String outputFileName) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] salt = new byte[8];
        byte[] ivFromFile = new byte[16];
        inputStream.read(salt);
        inputStream.read(ivFromFile);

        SecretKey symmetricKey = generateSecretKey(salt);
        crypto.init(Cipher.DECRYPT_MODE,symmetricKey,new IvParameterSpec(ivFromFile));
        CipherInputStream cipherInput = new CipherInputStream(inputStream,crypto);
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

        String pathAndNameOfOutputFile = inputFile.toPath().toString().replaceFirst(inputFile.getName(),outputFileName);
        FileOutputStream outputStream = new FileOutputStream(pathAndNameOfOutputFile);
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        PrintWriter pw = new PrintWriter(streamWriter);
        pw.println(plainTextStr);
        pw.close();
        streamWriter.close();
        outputStream.close();
    }
}
