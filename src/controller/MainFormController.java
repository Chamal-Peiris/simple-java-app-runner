package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainFormController {
    public TextArea txtInputCode;
    public TextArea txtOutputCode;

    public void btnExecute_OnAction(ActionEvent actionEvent) throws IOException {

        try {
            /* Step 1 */
            String data = "public class DEP8IDEDemo{\n" +
                    "public static void main(String args[]){\n"+
                    txtInputCode.getText() +
                    "\n}\n" +
                    "}";

            /* Step 2 */
            String tempDir = System.getProperty("java.io.tmpdir");
            Path tempFilePath = Paths.get(tempDir ,  "DEP8IDEDemo.java");
            Files.write(tempFilePath, data.getBytes());

            /* Step 3 */
            Process javac = Runtime.getRuntime().exec("javac " + tempFilePath);
            int exitCode = javac.waitFor();

            if (exitCode == 0){
                /* Step 4 */
                Process java = Runtime.getRuntime().exec("java -cp " + tempDir + " DEP8IDEDemo");
                exitCode = java.waitFor();

                if (exitCode == 0){
                    readStream(java.getInputStream());
                }else{
                    readStream(java.getErrorStream());
                }
            }else{
                readStream(javac.getErrorStream());
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            Path classFilePath = Paths.get(System.getProperty("java.io.tmpdir"), "DEP8IDEDemo.class");
            Path javaFilePath = Paths.get(System.getProperty("java.io.tmpdir"), "DEP8IDEDemo.java");
            Files.deleteIfExists(classFilePath);
            Files.deleteIfExists(javaFilePath);
        }
    }

    private void readStream(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        txtOutputCode.setText(new String(buffer));
        is.close();
    }

}
