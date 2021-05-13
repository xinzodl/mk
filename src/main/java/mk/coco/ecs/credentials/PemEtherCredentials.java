package mk.coco.ecs.credentials;

import mk.coco.ecs.utils.FileUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static mk.coco.ecs.utils.FileUtils.getFilePathFromResource;

/**
 * Generates the {@link KeyStore} based
 * on a PEM encoded certificate and key.
 * <p>
 * It supports instantiation by loading PEM certificate files or by providing the PEM
 * encoded certificate and private key in a raw string.
 */
public class PemEtherCredentials {

    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    private static final String CERT_ALIAS = "ether-cert";
    private static final String KEY_ALIAS = "ether-key";
    private static final String KEY_PASS = "defaultPassword";
    private static final String X509_CERTIFICATE_TYPE = "X.509";
    private static final String RSA_KEY_TYPE = "RSA";
    private static final String JKS_KEYSTORE_TYPE = "JKS";

    private final String cert;
    private final String key;

    private KeyStore keyStore;

    public static PemEtherCredentials buildKeystoreFromFiles(String certFilePath, String keyFilePath) throws Exception {
        // Get ful path
        String certFileFullPath = getFilePathFromResource(certFilePath);
        String keyFileFullPath = getFilePathFromResource(keyFilePath);

        // Get files
        String cert = FileUtils.readFile(certFileFullPath, StandardCharsets.UTF_8);
        String key = FileUtils.readFile(keyFileFullPath, StandardCharsets.UTF_8);

        // Get credentials
        return PemEtherCredentials.fromString(cert, key);
    }

    /**
     * Creates a new {@link PemEtherCredentials}.
     *
     * @param cert PEM formatted string with the certificate to use.
     * @param key PEM formatted string with the private key associated to the certificate.
     * @throws Exception if the certificate or the key are not in a valid PEM format.
     */
    private PemEtherCredentials(String cert, String key) throws Exception {
        // Check credentials' format
        if (cert == null || cert.isEmpty()) {
            throw new IllegalArgumentException("PEM cert can't be null or empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("PEM key can't be null or empty");
        }
        if (!cert.contains("-----BEGIN CERTIFICATE-----") || !cert.contains("-----END CERTIFICATE-----")) {
            throw new IllegalArgumentException("Wrong certificate format");
        }
        if (!key.contains("-----BEGIN PRIVATE KEY-----") || !key.contains("-----END PRIVATE KEY-----")) {
            throw new IllegalArgumentException("Wrong private key format");
        }

        this.cert = cert;
        this.key = key;

        this.keyStore = buildKeyStore();
    }

    /**
     * Creates a {@link PemEtherCredentials} from PEM cert and PEM key in a string
     *
     * @param cert cert in PEM format
     * @param key  key in PEM format
     * @return the {@link PemEtherCredentials} loaded from the strings
     */
    public static PemEtherCredentials fromString(String cert, String key) throws Exception {
        return new PemEtherCredentials(cert, key);
    }

    public final KeyStore getKeyStore() {
        return keyStore;
    }

    public final char[] getKeyStorePassword() {
        return KEY_PASS.toCharArray();
    }

    /**
     * Initializes the internal {@link KeyStore} and loads the PEM and KEY.
     *
     * @throws Exception if the {@link KeyStore} couldn't be created.
     */
    private KeyStore buildKeyStore() throws Exception {
        try {
            final byte[] certBytes = parseDerFromPem(cert,
                    BEGIN_CERTIFICATE,
                    END_CERTIFICATE);
            final byte[] keyBytes = parseDerFromPem(key,
                    BEGIN_PRIVATE_KEY,
                    END_PRIVATE_KEY);

            final X509Certificate x509Cert = generateCertificateFromDer(certBytes);
            final RSAPrivateKey privKey = generatePrivateKeyFromDer(keyBytes);

            final KeyStore ks = KeyStore.getInstance(JKS_KEYSTORE_TYPE);
            ks.load(null); // Creates a new keystore
            ks.setCertificateEntry(CERT_ALIAS, x509Cert);
            ks.setKeyEntry(KEY_ALIAS, privKey, getKeyStorePassword(), new Certificate[]{x509Cert});

            return ks;
        } catch (Exception e) {
            throw new Exception("Unable to load Ether credentials from PEM", e);
        }
    }

    /**
     * Generates a DER encoded certificate from a PEM encoded certificate string.
     *
     * @param cert           string with the PEM content.
     * @param beginDelimiter PEM begin header.
     * @param endDelimiter   PEM end header.
     * @return byte array binary content of the certificate.
     */
    private byte[] parseDerFromPem(String cert, final String beginDelimiter, final String endDelimiter) {
        return DatatypeConverter.parseBase64Binary(cert
                .replace(beginDelimiter, "")
                .replaceAll("\n", "")
                .replace(endDelimiter, ""));
    }

    /**
     * Generates a X509 certificate from a DER encoded certificate.
     *
     * @param certificateDer byte array with the DER certificate to transform into a X509 certificate.
     * @return a valid X509 certificate.
     * @throws CertificateException on parsing errors.
     */
    private X509Certificate generateCertificateFromDer(byte[] certificateDer) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certificateDer));
    }

    /**
     * Generates a RSA private key from a DER encoded private key.
     *
     * @param privateKeyDer DER encoded private key to transform into a RSA private key.
     * @return a valid RSA private key.
     * @throws InvalidKeySpecException  on invalid key content.
     * @throws NoSuchAlgorithmException on invalid key type.
     */
    private RSAPrivateKey generatePrivateKeyFromDer(byte[] privateKeyDer)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final KeyFactory factory = KeyFactory.getInstance(RSA_KEY_TYPE);
        return (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDer));
    }
}
