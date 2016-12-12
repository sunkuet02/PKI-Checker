package com.sun.ocsp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.provider.certpath.OCSP;
import sun.security.provider.certpath.OCSPResponse;
import utils.Actions;

import javax.security.cert.CertificateException;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;

import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.Date;

public class OcspController {

    public static Stage stage = new Stage();
    private Actions actions;

    public OcspController() {
        actions = new Actions();
    }

    private String ocspLink;

    private String caIssuerLink;

    @FXML
    private Button btnCertificate;

    @FXML
    private TextField txtCertificate;

    @FXML
    private Button btnIssuer;

    @FXML
    private TextField txtIssuer;

    @FXML
    private Button btnResponder;

    @FXML
    private TextField txtResponder;

    @FXML
    private Button btnSubmit;

    @FXML
    private TextField txtOcspLink;

    @FXML
    private CheckBox radioOcspUrl;

    @FXML
    private CheckBox radioIssuerUrl;

    @FXML
    private void itemExitAction(ActionEvent event) {
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

    @FXML
    private void certificateAction(ActionEvent event) {
        File file = actions.fileChooserAction();
        txtCertificate.setText(file.getPath());
    }

    @FXML
    private void issuerAction(ActionEvent event) {
        File file = actions.fileChooserAction();
        txtIssuer.setText(file.getPath());
    }

    @FXML
    private void responderAction(ActionEvent event) {
        File file = actions.fileChooserAction();
        txtResponder.setText(file.getPath());
    }

    @FXML
    private void submitAction(ActionEvent event) {
        X509Certificate certificate = null;
        X509Certificate issuer = null;
        OCSP.RevocationStatus revocationStatus = null;

        if (!txtOcspLink.getText().equals("") && txtOcspLink.getText() != null) {
            ocspLink = txtOcspLink.getText();
        }

        try {
            if (!txtIssuer.getText().equals("") && txtIssuer.getText() != null) {
                issuer = actions.getCertificateFromFile(txtIssuer.getText());
            } else if(radioIssuerUrl.isSelected()) {
                issuer = actions.getCertificateFromUrl(caIssuerLink);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Check all the fields and make sure the link is correct ! ");
            e.printStackTrace();
            return ;
        }
        try {
            certificate = actions.getCertificateFromFile(txtCertificate.getText());
            revocationStatus = OCSP.check(certificate, issuer);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Check all the fields and make sure the link is correct ! ");
            e.printStackTrace();
            return;
        }

        String result = "";
        result += "Certificate SubjectDN : " + certificate.getSubjectDN().getName() + "\n";
        result += "Certificate IssuerDN : " + certificate.getIssuerDN().getName() + "\n";
        result += "Certificate Validity : " + certificate.getNotBefore().toString() + " To " + certificate.getNotAfter().toString() + "\n";
        result += "Revocation Status : " + revocationStatus.getCertStatus().toString() + "\n";
        result += "Revocation Reason : " + revocationStatus.getRevocationReason().toString() + "\n";
        if (!revocationStatus.getCertStatus().toString().equals("GOOD")) {
            result += "Revocation Time : " + revocationStatus.getRevocationTime().toString() + "\n";
        }

        JOptionPane.showMessageDialog(null, result);
    }

    @FXML
    private void radioOcspUrlAction(ActionEvent event) {
        if (radioOcspUrl.isSelected()) {
            txtOcspLink.setEditable(false);
            try {
                ocspLink = actions.getOCSPUrlsFromCertificate(actions.getCertificateFromFile(txtCertificate.getText())).get(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Upload a certificate with the Authority Information Access");
            }
        }
    }

    @FXML
    private void radioIssuerUrlAction(ActionEvent event) {
        if (radioIssuerUrl.isSelected()) {
            txtIssuer.setText("");
            txtIssuer.setEditable(false);
            try {
                caIssuerLink = actions.getIssuerCAUrlsFromCertificate(actions.getCertificateFromFile(txtCertificate.getText())).get(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Upload a certificate with the Authority Information Access");
            }
        }
    }

    private void clearAllFields() {
        txtResponder.clear();
        txtIssuer.clear();
        txtCertificate.clear();
    }
}
