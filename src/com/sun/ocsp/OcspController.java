package com.sun.ocsp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

public class OcspController {

    public static Stage stage = new Stage();
    private Actions actions;
    public OcspController() {
        actions = new Actions();
    }

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

    @FXML
    private void  certificateAction(ActionEvent event) {
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
        OCSP.RevocationStatus revocationStatus = null;
        try {
            certificate = actions.getCertificateFromFile(txtCertificate.getText());
            X509Certificate issuer = actions.getCertificateFromFile(txtIssuer.getText());
            X509Certificate responder = actions.getCertificateFromFile(txtResponder.getText());
            revocationStatus = OCSP.check(certificate, issuer, new URI(txtOcspLink.getText()), responder, new Date());
            clearAllFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Check all the fields and make sure the link is correct ! ");
            clearAllFields();
            return;
        }

        String result = "";
        result += "Certificate SubjectDN : " + certificate.getSubjectDN().getName() + "\n";
        result += "Certificate IssuerDN : " + certificate.getIssuerDN().getName() + "\n";
        result += "Certificate Validity : " + certificate.getNotBefore().toString() + " To " + certificate.getNotAfter().toString() + "\n";
        result += "Revocation Status : " + revocationStatus.getCertStatus().toString() +"\n";
        result += "Revocation Reason : " + revocationStatus.getRevocationReason().toString() + "\n";
        result += "Revocation Time : " + revocationStatus.getRevocationTime().toString() + "\n";

        JOptionPane.showMessageDialog(null, result);
    }

    private void clearAllFields() {
        txtResponder.clear();
        txtIssuer.clear();
        txtCertificate.clear();
    }
}
