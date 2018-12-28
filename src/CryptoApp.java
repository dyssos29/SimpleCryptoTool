import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoApp
{
    public static void main(String[] args)
    {
//        try {
//            FileCrypto fileCrypto = new FileCrypto(PASSWORD);
//            CryptoGui gui = new CryptoGui(fileCrypto);
//            gui.setVisible(true);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
        LoginGui loginGui = new LoginGui();
        loginGui.setVisible(true);
    }
}
