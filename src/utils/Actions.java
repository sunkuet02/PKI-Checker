package utils;

import com.sun.ocsp.OcspController;
import com.sun.viewcertificate.ViewCertificateController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by sun on 12/7/16.
 */
public class Actions {
    public void itemOcspAction(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/sun/ocsp/ocsp.fxml"));
        OcspController.stage.setTitle("OCSP Checker");
        OcspController.stage.setScene(new Scene(root, Main.windowWidth, Main.windowHeight));
        OcspController.stage.show();
        stage.hide();
    }

    public void itemViewCertificate(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/sun/viewcertificate/viewcertificate.fxml"));
        ViewCertificateController.stage.setTitle("View Certificate");
        ViewCertificateController.stage.setScene(new Scene(root, Main.windowWidth, Main.windowHeight));
        ViewCertificateController.stage.show();
        stage.hide();
    }

    public X509Certificate getCertificateFromFile(String link) throws Exception{
        FileInputStream certificateInputStream = new FileInputStream(link);
        Security.addProvider(new BouncyCastleProvider());
        return (X509Certificate) CertificateFactory.getInstance("X.509", "BC").generateCertificate(certificateInputStream);
    }

    public File fileChooserAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Certificate Files files (*.pem)", "*.pem", ".crt");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(null);
    }
}
