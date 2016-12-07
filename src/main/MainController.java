package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.provider.certpath.OCSP;
import utils.Actions;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class MainController {

    private Actions actions;
    public static Stage stage = new Stage();

    public MainController() {
        actions = new Actions();
    }

    @FXML
    private void  itemExitAction(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    private void itemOcspAction(ActionEvent event) throws Exception {
        actions.itemOcspAction(stage);
    }

    @FXML
    public void itemViewCertificate(ActionEvent event) throws Exception {
        actions.itemViewCertificate(stage);
    }
}
