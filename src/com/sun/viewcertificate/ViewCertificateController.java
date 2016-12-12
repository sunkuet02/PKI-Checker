package com.sun.viewcertificate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import sun.security.provider.certpath.OCSP;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.PKIXExtensions;
import utils.Actions;

import javax.security.cert.CertificateException;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

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
        try {
            X509Certificate certificate = actions.getCertificateFromFile(txtCertificate.getText());
            txtViewCertificateDetails.setText(certificate.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Check if the link is correct or a valid certificate file");
            e.printStackTrace();
        }
    }
}
