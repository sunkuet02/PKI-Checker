package utils;

import com.sun.ocsp.OcspController;
import com.sun.viewcertificate.ViewCertificateController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.PEMUtil;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import sun.security.pkcs.PKCS7;
import sun.security.util.DerValue;

import javax.security.cert.CertificateException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public X509Certificate getCertificateFromUrl(String link) throws Exception{
        URL url = new URL(link);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream response = httpURLConnection.getInputStream();
        CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
        Iterator i = cf.generateCertificates( response ).iterator();
        X509Certificate cert = null;
        while ( i.hasNext() ) {
            cert = (X509Certificate)i.next();
        }
        return cert;
    }

    public File fileChooserAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Certificate Files files (*.pem)", "*.pem", ".crt");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(null);
    }

    public List<String> getOCSPUrlsFromCertificate(X509Certificate cert) throws CertificateException {

        byte[] aiaExtensionValue = cert.getExtensionValue(Extension.authorityInfoAccess.getId());
        if (aiaExtensionValue == null)
            throw new CertificateException("Certificate Doesn't have Authority Information Access points");

        AuthorityInformationAccess authorityInformationAccess;
        try {
            DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(aiaExtensionValue)).readObject());
            authorityInformationAccess = AuthorityInformationAccess.getInstance(new ASN1InputStream(oct.getOctets()).readObject());
        } catch (IOException e) {
            throw new CertificateException("Cannot read certificate to get OSCP urls");
        }

        List<String> ocspUrlList = new ArrayList<String>();
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
        for (AccessDescription accessDescription : accessDescriptions) {

            GeneralName gn = accessDescription.getAccessLocation();
            if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                DERIA5String str = DERIA5String.getInstance(gn.getName());
                String accessLocation = str.getString();
                if(OCSPObjectIdentifiers.id_pkix_ocsp.toString()
                        .equals(accessDescription.getAccessMethod().toString() )) {
                    ocspUrlList.add(accessLocation);
                }
            }
        }
        if(ocspUrlList.isEmpty())
            throw new CertificateException("Cant get OCSP urls from certificate");

        return ocspUrlList;
    }

    public List<String> getIssuerCAUrlsFromCertificate(X509Certificate cert) throws CertificateException {

        byte[] aiaExtensionValue = cert.getExtensionValue(Extension.authorityInfoAccess.getId());
        if (aiaExtensionValue == null)
            throw new CertificateException("Certificate Doesn't have Authority Information Access points");

        AuthorityInformationAccess authorityInformationAccess;
        try {
            DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(aiaExtensionValue)).readObject());
            authorityInformationAccess = AuthorityInformationAccess.getInstance(new ASN1InputStream(oct.getOctets()).readObject());
        } catch (IOException e) {
            throw new CertificateException("Cannot read certificate to get OSCP urls");
        }

        List<String> issuerCAUrlsList = new ArrayList<String>();
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
        for (AccessDescription accessDescription : accessDescriptions) {

            GeneralName gn = accessDescription.getAccessLocation();
            if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                DERIA5String str = DERIA5String.getInstance(gn.getName());
                String accessLocation = str.getString();
                if( AccessDescription.id_ad_caIssuers.toString()
                        .equals(accessDescription.getAccessMethod().toString() )) {
                    issuerCAUrlsList.add(accessLocation);
                }
            }
        }
        if(issuerCAUrlsList.isEmpty())
            throw new CertificateException("Cant get OCSP urls from certificate");

        return issuerCAUrlsList;
    }
}
