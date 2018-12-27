import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoGui extends JFrame
{
    private JPanel mainPanel;
    private JButton chooseFileButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton closeButton;
    private JLabel previewLabel;
    private JTextArea previewText;
    private JScrollPane previewScrollPane;
    private GridBagConstraints gbConstrains;
    private JFileChooser fileChooser;
    private File selectedFile;
    private FileCrypto fileCryptoObject;

    public CryptoGui(FileCrypto fileCryptoObject)
    {
        this.fileCryptoObject = fileCryptoObject;
        drawFrame();
        constructMainPanel();
        addListeners();
    }

    private void drawFrame()
    {
        setTitle("SimpleCryptoTool");
        setSize(650,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(getWidth() - 50,getHeight()));
        add(mainPanel);
    }

    private void addToMainPanel(int gridX, int gridY,int gridWidth, int gridHeight, int inset, JComponent componentObject)
    {
        gbConstrains = new GridBagConstraints();
        gbConstrains.weightx = 1;
        gbConstrains.fill = GridBagConstraints.HORIZONTAL;
        gbConstrains.insets = new Insets(inset,inset,inset,inset);
        gbConstrains.gridx = gridX;
        gbConstrains.gridy = gridY;
        gbConstrains.gridwidth = gridWidth;
        gbConstrains.gridheight = gridHeight;
        mainPanel.add(componentObject,gbConstrains);
    }

    private void constructMainPanel()
    {
        previewLabel = new JLabel("File preview");
        addToMainPanel(0,0,1,1,10,previewLabel);

        previewText = new JTextArea();
        previewText.setPreferredSize(new Dimension(getWidth() - 50,getHeight()/3));
        previewText.setEditable(false);
        previewScrollPane = new JScrollPane(previewText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        addToMainPanel(0,1,2,2,10,previewScrollPane);

        chooseFileButton = new JButton("CHOOSE FILE");
        addToMainPanel(0,3,2,1,10,chooseFileButton);

        encryptButton = new JButton("ENCRYPT");
        addToMainPanel(0,4,1,1,10,encryptButton);

        decryptButton = new JButton("DECRYPT");
        addToMainPanel(1,4,1,1,10,decryptButton);

        closeButton = new JButton("CLOSE");
        addToMainPanel(1,5,1,1,20,closeButton);

        fileChooser = new JFileChooser();
    }

    private void addListeners()
    {
        chooseFileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int valueReturned = fileChooser.showOpenDialog(CryptoGui.this);
                if (valueReturned == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    try {
                        String fileContent = new String(Files.readAllBytes(selectedFile.toPath()));
                        previewText.setText(null);
                        previewText.append(fileContent);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        encryptButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileCryptoObject.encryptFile(selectedFile,"cipherText.txt");
                } catch (InvalidKeyException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidAlgorithmParameterException e1) {
                    e1.printStackTrace();
                }
            }
        });

        decryptButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileCryptoObject.decryptFile(selectedFile,"plainText.txt");
                } catch (InvalidKeyException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidAlgorithmParameterException e1) {
                    e1.printStackTrace();
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                }
            }
        });

        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
