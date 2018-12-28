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
        previewLabel = new JLabel("File preview ");
        addToMainPanel(0,0,1,1,10,previewLabel);

        previewText = new JTextArea();
        previewText.setPreferredSize(new Dimension(getWidth() - 50,getHeight()/3));
        previewText.setEditable(false);
        previewScrollPane = new JScrollPane(previewText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        addToMainPanel(0,1,2,2,10,previewScrollPane);

        chooseFileButton = new JButton("CHOOSE FILE");
        addToMainPanel(0,3,2,1,10,chooseFileButton);

        encryptButton = new JButton("ENCRYPT");
        encryptButton.setEnabled(false);
        encryptButton.setToolTipText("<html><b>Choose appropriate file to encrypt.<b/><html>");
        addToMainPanel(0,4,1,1,10,encryptButton);

        decryptButton = new JButton("DECRYPT");
        decryptButton.setEnabled(false);
        decryptButton.setToolTipText("<html><b>Choose appropriate file to decrypt.<b/><html>");
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
                if (valueReturned == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = fileChooser.getSelectedFile();
                    previewFile(selectedFile);
                }
            }
        });

        encryptButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String outputFileName;
                    if (selectedFile.getName().startsWith("decrypted-"))
                        outputFileName = selectedFile.getName().replaceFirst("decrypted-","encrypted-");
                    else
                        outputFileName = "encrypted-" + selectedFile.getName();

                    File processedFile = fileCryptoObject.encryptFile(selectedFile,outputFileName);
                    JOptionPane.showMessageDialog(CryptoGui.this,"The encrypted file is located in the same directory with the original and its name is prefixed with \"encrypted\".","ENCRYPTED SUCCESSFULLY",JOptionPane.PLAIN_MESSAGE);
                    previewFile(processedFile);
                } catch (InvalidKeyException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidAlgorithmParameterException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                }
                encryptButton.setEnabled(false);
                encryptButton.setToolTipText("<html><b>Choose appropriate file to encrypt.<b/><html>");
            }
        });

        decryptButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String outputFileName;
                    if (selectedFile.getName().startsWith("encrypted-"))
                        outputFileName = selectedFile.getName().replaceFirst("encrypted-","decrypted-");
                    else
                        outputFileName = "decrypted-" + selectedFile.getName();

                    File processedFile = fileCryptoObject.decryptFile(selectedFile,outputFileName);
                    JOptionPane.showMessageDialog(CryptoGui.this,"The decrypted file is located in the same directory with the original and its name is prefixed with \"decrypted\".","DECRYPTED SUCCESSFULLY",JOptionPane.PLAIN_MESSAGE);
                    previewFile(processedFile);
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
                decryptButton.setEnabled(false);
                decryptButton.setToolTipText("<html><b>Choose appropriate file to decrypt.<b/><html>");
            }
        });

        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void previewFile(File aFile)
    {
        encryptButton.setEnabled(true);
        decryptButton.setEnabled(true);
        encryptButton.setToolTipText("<html><b>Choose appropriate file to encrypt.<b/><html>");
        decryptButton.setToolTipText("<html><b>Choose appropriate file to decrypt.<b/><html>");

        String fileContent;
        try {
            if (fileCryptoObject.checkIfCipherText(aFile))
            {
                encryptButton.setEnabled(false);
                decryptButton.setToolTipText(null);
                int offset = 8;
                int length = Files.readAllBytes(aFile.toPath()).length - 8;
                fileContent = new String(Files.readAllBytes(aFile.toPath()),offset,length);
            }
            else
            {
                decryptButton.setEnabled(false);
                encryptButton.setToolTipText(null);
                fileCryptoObject.closeInputStream();
                fileContent = new String(Files.readAllBytes(aFile.toPath()));
            }

            previewLabel.setText("File preview: " + aFile.getName());
            previewText.setText(null);
            previewText.append(fileContent);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
