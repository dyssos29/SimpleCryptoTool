import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoApp
{
    private final static String PASSWORD = "I_Love_Crypto";

    public static void main(String[] args)
    {
//        FileCrypto fileCrypto;
//        try {
//            fileCrypto = new FileCrypto();
//            fileCrypto.encryptFile("plainText.txt","cipherText.txt");
//            fileCrypto.decryptFile("cipherText.txt","plainText2.txt");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
        try {
            FileCrypto fileCrypto = new FileCrypto(PASSWORD);
            CryptoGui gui = new CryptoGui(fileCrypto);
            gui.setVisible(true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
