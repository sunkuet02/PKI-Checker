package com.sun.viewcertificate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.provider.certpath.OCSP;
import sun.security.util.DerValue;
import utils.Actions;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;

public class ViewCertificateController {
    public static Stage stage = new Stage();
    private Actions actions;
    public ViewCertificateController() {
        actions = new Actions();
    }

    @FXML
    private TextField txtCertificate;

    @FXML
    private TextArea txtViewCertificateDetails;

    @FXML
    private void  itemExitAction(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    private void itemOcspAction(ActionEvent event) throws Exception {
        actions.itemOcspAction(stage);
    }

    @FXML
    private void itemViewCertificate(ActionEvent event) throws Exception {
        actions.itemViewCertificate(stage);
    }

    @FXML
    private void certificateAction(ActionEvent event) {
        File file = actions.fileChooserAction();
        txtCertificate.setText(file.getPath());
    }

    @FXML
    private void viewDetailsAction(ActionEvent event) {
        X509Certificate certificate = null;
        try {
            certificate = actions.getCertificateFromFile(txtCertificate.getText());
            txtViewCertificateDetails.setText(certificate.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Check if the link is correct or a valid certificate file");
            e.printStackTrace();
        }
    }
}
