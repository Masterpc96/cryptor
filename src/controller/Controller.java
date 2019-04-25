package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Controller {
    private File file;

    private static Charset charset = Charset.forName("ISO-8859-1");

    private int block = 6;

    private ArrayList<String> crypted = new ArrayList<>();

    private StringBuilder decrypted = new StringBuilder();

    @FXML
    private TextField path;

    @FXML
    private PasswordField password;

    @FXML
    private Label informLabel;

    @FXML
    private Button crypt;

    @FXML
    private Button decrypt;


    @FXML
    private void help(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cryptor");
        alert.setHeaderText(null);
        alert.setContentText("Program szyfrujący, działa na kodowaniu SO-8859-1. \nWersja 2.0 stabilna");

        alert.showAndWait();
    }


    @FXML
    private void crypt() {
        String line;
        String readed = "";
        String passwd = password.getText();
        int block = 6;
        try {

            readed = readFromFile();

            cryptText(readed, passwd, block);

            saveData();

            informLabel.setText("Zaszyfrowano");
            informLabel.setTextFill(Color.web("#00C300"));
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
            informLabel.setText("Coś poszło nie tak");
            informLabel.setTextFill(Color.rgb(255,0,0));
        }

    }

    private void saveData() throws IOException {

        System.out.println(file.getParent());
        Writer writer = new OutputStreamWriter(new FileOutputStream(file.getParent() + "/crypted.txt"), charset);

        String veryTemp = "";
        for (String s : crypted) {
            veryTemp += s;
        }

        writer.write(veryTemp);
        writer.close();
    }

    private String readFromFile() throws IOException {
        String line;
        String readed;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), charset));
        StringBuilder builder = new StringBuilder();

        line = in.readLine();

        while (line != null) {
            builder.append(line);
            line = in.readLine();
            System.out.println("test");
            if (line != null) builder.append("\n");
        }

        in.close();

        readed = builder.toString();

        if (file.length() % 2 == 0) {
            while (file.length() % block != 0) {
                block += 2;
            }
        } else {
            long val = file.length() % block;

            for (int y = 0; y < val; y++) {
                readed += ((char) 4);
            }
        }

        return readed;
    }

    private void cryptText(String readed, String passwd, int block) {
        int j = 0;
        int previouseSave = 0;

        String temp = "";
        for (int i = 0; i < readed.length(); i++) {
            int code = readed.charAt(i);
            int passCode = passwd.charAt(j);
            int newValue = code + passCode;
            if (newValue > 255) newValue -= 255;

            temp += (char) newValue;

            j++;
            if (j == passwd.length()) j = 0;

            if (i == (previouseSave + block - 1)) {
                char[] arr = temp.toCharArray();
                for (int x = 1; x < arr.length; x += 2) {
                    char swap = arr[x];
                    arr[x] = arr[x - 1];
                    arr[x - 1] = swap;

                }
                previouseSave = i + 1;
                crypted.add(String.valueOf(arr));
                temp = "";

            }
        }
    }

    private void decryptFile(String readed, String passwd, int block) {
        int j = 0;
        int previouseSave = 0;

        String temp = "";
        char[] arr = readed.toCharArray();
        for (int x = 1; x < arr.length; x += 2) {
            char swap = arr[x];
            arr[x] = arr[x - 1];
            arr[x - 1] = swap;

        }



        for (int i = 0; i < arr.length; i++) {
            int code = arr[i];
            int passCode = passwd.charAt(j);
            int newValue = code - passCode;
            if (newValue < 0) newValue += 255;

            if(newValue != 4){
                decrypted.append((char) newValue);
            }else {
                return;
            }

            j++;
            if (j == passwd.length()) j = 0;


        }
    }

    @FXML
    private void decrypt() {
        String line;
        String readed = "";
        String passwd = password.getText();
        int block = 6;
        try {

            readed = readFromFile();


            decryptFile(readed, passwd, block);


            System.out.println(decrypted);

            FileWriter fileWriter = new FileWriter(file.getParent() + "/decrypted.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(decrypted.toString());
            printWriter.close();

            informLabel.setText("Odszyfrowano");
            informLabel.setTextFill(Color.web("#00C300"));
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
            informLabel.setText("Coś poszło nie tak");
            informLabel.setTextFill(Color.rgb(255,0,0));
        }

    }

    @FXML
    private void selectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        file = chooser.showOpenDialog(new Stage());
        path.setText(file.getName());
    }

    public void initialize() {
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 8) {
                crypt.setDisable(true);
                decrypt.setDisable(true);
                informLabel.setVisible(true);
                informLabel.setText("Hasło musi mieć co najmniej 8 znaków");
                informLabel.setTextFill(Color.color(1,0,0));
            } else {
                crypt.setDisable(false);
                decrypt.setDisable(false);
                informLabel.setVisible(false);
            }
        });
    }
}
