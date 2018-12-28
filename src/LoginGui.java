import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginGui extends JFrame
{
    private JPanel mainPanel;
    private JLabel passwordLabel;
    private JPasswordField password;
    private JButton loginButton;
    private JButton closeButton;
    private GridBagConstraints gbConstrains;
    private CryptoGui cryptoGuiWindow;
    private FileCrypto fileCrypto;
    private final String CORRECT_PASSWORD = "I_love_crypto";

    public LoginGui()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                drawFrame();
                constructMainPanel();
                addListeners();
            }
        });
    }

    private void drawFrame()
    {
        setTitle("LOGIN");
        setSize(300,250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        mainPanel = new JPanel(new GridBagLayout());
        add(mainPanel);
    }

    private void addToMainPanel(int weightX, int gridX, int gridY, int inset, JComponent componentObject)
    {
        gbConstrains = new GridBagConstraints();
        gbConstrains.weightx = weightX;
        gbConstrains.fill = GridBagConstraints.HORIZONTAL;
        gbConstrains.insets = new Insets(inset,inset,inset,inset);
        gbConstrains.gridx = gridX;
        gbConstrains.gridy = gridY;
        mainPanel.add(componentObject,gbConstrains);
    }

    private void constructMainPanel()
    {
        passwordLabel = new JLabel("Password");
        addToMainPanel(1,0,0,10,passwordLabel);

        password = new JPasswordField();
        addToMainPanel(1,1,0,10,password);

        loginButton = new JButton("Login");
        addToMainPanel(0,0,1,20,loginButton);

        closeButton = new JButton("Close");
        addToMainPanel(0,1,1,20,closeButton);
    }

    private void addListeners()
    {
        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(checkIfInputsCorrect())
                {
                    try {
                        fileCrypto = new FileCrypto(password.getPassword().toString());
                    } catch (NoSuchAlgorithmException e1) {
                        e1.printStackTrace();
                    } catch (NoSuchPaddingException e1) {
                        e1.printStackTrace();
                    } catch (InvalidKeySpecException e1) {
                        e1.printStackTrace();
                    }
                    cryptoGuiWindow = new CryptoGui(fileCrypto);
                    cryptoGuiWindow.setVisible(true);
                    setVisible(false);
                }
                else
                    JOptionPane.showMessageDialog(LoginGui.this, "Incorrect password.", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        });

        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private boolean checkIfInputsCorrect()
    {
        String passwordInput = new String(password.getPassword());

        if (passwordInput.length() == 0 || !passwordInput.equals(CORRECT_PASSWORD))
            return false;

        return true;
    }
}
