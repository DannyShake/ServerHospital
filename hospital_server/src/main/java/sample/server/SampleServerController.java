package sample.server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SampleServerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button serverOff;

    @FXML
    private Button serverOn;


    public void StartServer(){
        new Thread(Server.getInstance()).start();
        serverOn.setVisible(false);
        serverOff.setVisible(true);
    }

    public void StopServer(){
        Server.getInstance().stop();
        serverOn.setVisible(true);
        serverOff.setVisible(false);
    }
}

