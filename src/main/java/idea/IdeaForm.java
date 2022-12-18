package idea;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IdeaForm {
    private JPanel panelParent;
    private JButton cipherButton;
    private JTextField textFieldKey;
    private JTextArea textAreaLeft;
    private JButton buttonLoadFromFile;
    private JLabel LabelStartText;
    private JTextField textFieldIV;
    private JTextArea textAreaRight;
    private JButton buttonSaveOnFile;
    private JCheckBox checkBoxEncrypt;

    public IdeaForm() {
        textAreaLeft.setLineWrap(true);
        textAreaRight.setLineWrap(true);

        checkBoxEncrypt.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                LabelStartText.setText("Исходный текст");
                cipherButton.setText("Зашифровать");
            } else {
                LabelStartText.setText("Зашифрованный текст");
                cipherButton.setText("Расшифровать");
            }
        });

        buttonLoadFromFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        return f.getName().toLowerCase().endsWith(".txt")
                                || f.getName().toLowerCase().endsWith(".dat");
                    }
                }

                @Override
                public String getDescription() {
                    return "Text files (*.txt)";
                }
            });
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                try {
                    if (selectedFile.getAbsolutePath().contains(".txt")) {
                        List<String> fileStrings = Files.readAllLines(Path.of(selectedFile.getAbsolutePath()));
                        textAreaLeft.setText(String.join("\n", fileStrings));
                    }
                    else if (selectedFile.getAbsolutePath().contains(".dat")) {
                        byte[] fileBytes = Files.readAllBytes(Path.of(selectedFile.getAbsolutePath()));
                        int[] r = new int[fileBytes.length];

                        for (int i = 0; i < fileBytes.length; i++) {
                            r[i] = fileBytes[i];
                        }
                        String res = Arrays.stream(r).boxed().map(String::valueOf).collect(Collectors.joining(" "));
                        textAreaLeft.setText(res);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        buttonSaveOnFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        return f.getName().toLowerCase().endsWith(".txt")
                                || f.getName().toLowerCase().endsWith(".dat");
                    }
                }

                @Override
                public String getDescription() {
                    return "Text files (*.txt)";
                }
            });
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                if (selectedFile.getAbsolutePath().contains(".txt")) {
                    try {
                        Files.writeString(Path.of(selectedFile.getAbsolutePath()), textAreaRight.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if (selectedFile.getAbsolutePath().contains(".dat")) {
                    List<Byte> bytes = Arrays.stream(textAreaRight.getText().split(" ")).map(Integer::parseInt).map(Integer::byteValue).toList();

                    byte[] bytes1 = new byte[bytes.size()];
                    for (int i = 0; i < bytes.size(); i++) {
                        bytes1[i] = bytes.get(i);
                    }
                    try {
                        Files.write(Path.of(selectedFile.getAbsolutePath()), bytes1);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        cipherButton.addActionListener(e -> {
            String key = textFieldKey.getText();
            String iv = textFieldIV.getText();

            if (key.length() != 16)
                JOptionPane.showMessageDialog(null, "Длина ключа должна составлять 16 байт", "Ошибка", JOptionPane.ERROR_MESSAGE);

            if (iv.length() != 8)
                JOptionPane.showMessageDialog(null, "Длина вектора инициализации должна составлять 8 байт", "Ошибка", JOptionPane.ERROR_MESSAGE);

            if (key.length() != 16 || iv.length() != 8)
                return;

            IdeaCBCImpl ideaCBC = new IdeaCBCImpl(key, iv);
            if (checkBoxEncrypt.isSelected()) {
                String inputText = textAreaLeft.getText();
                byte[] encrypt = ideaCBC.encrypt(inputText);
                List<Byte> bytes = new ArrayList<>();
                for (int i = 0; i < encrypt.length; i++) {
                    bytes.add(encrypt[i]);
                }
                System.out.println(bytes.stream().map(Integer::new).toList());
                System.out.println(bytes.stream().map(Integer::new).map(Integer::toHexString).collect(Collectors.joining(" ")));

                String result = Arrays.toString(encrypt)
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", "");
                textAreaRight.setText(result);
            } else {
                List<Byte> bytes = Arrays.stream(textAreaLeft.getText().split(" ")).map(Integer::parseInt).map(Integer::byteValue).toList();
                byte[] bytes1 = new byte[bytes.size()];

                for (int i = 0; i < bytes.size(); i++) {
                    bytes1[i] = bytes.get(i);
                }
                String decrypt = ideaCBC.decrypt(bytes1);

                textAreaRight.setText(decrypt);
            }
        });
    }

    public static void main(String[] args) {
        JFrame ideaFrame = new JFrame("IDEA CBC");
        ideaFrame.setContentPane(new IdeaForm().panelParent);
        ideaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ideaFrame.pack();
        ideaFrame.setVisible(true);
    }
}
