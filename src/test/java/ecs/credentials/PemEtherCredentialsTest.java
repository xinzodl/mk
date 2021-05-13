package ecs.credentials;

import mk.coco.ecs.credentials.PemEtherCredentials;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PemEtherCredentialsTest {

    String doesNotExistMsg = "does not exist";
    String wrongCertificateFormatMsg = "Wrong certificate format";
    String wrongPrivateKeyFormatMsg = "Wrong private key format";
    String unableToLoadEtherCredentialsMessage = "Unable to load Ether credentials from";
    String certCantBeNullMsg = "PEM cert can't be null or empty";
    String keyCantBeNullMsg = "PEM key can't be null or empty";

    String nonExistingPath = "/this/path/does/not/exist";
    String existingPath = "this/path/does/exist/file.txt";
    String validCertFormatPath = "this/path/does/exist/validCertFormat.txt";
    String validPrivateKeyFormatPath = "this/path/does/exist/validPrivateKeyFormat.txt";

    String expectedExceptionWasNotThrown = "Expected exception was not thrown";

    @Test (expected = Exception.class)
    public void shouldThrowCertificateDoesNotExistException() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.buildKeystoreFromFiles(nonExistingPath, existingPath);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains(doesNotExistMsg) && e.getMessage().contains(nonExistingPath));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = Exception.class)
    public void shouldThrowKeyDoesNotExistException() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.buildKeystoreFromFiles(existingPath, nonExistingPath);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains(doesNotExistMsg) && e.getMessage().contains(nonExistingPath));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowWrongCertificateFormatException() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.buildKeystoreFromFiles(existingPath, validPrivateKeyFormatPath);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains(wrongCertificateFormatMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowWrongPrivateKeyFormatException() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.buildKeystoreFromFiles(validCertFormatPath, existingPath);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains(wrongPrivateKeyFormatMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = Exception.class)
    public void shouldThrowUnableToLoadEtherCredentialsMessage() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.buildKeystoreFromFiles(validCertFormatPath, validPrivateKeyFormatPath);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains(unableToLoadEtherCredentialsMessage));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowPemCertCantBeNullMessage() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.fromString(null, "some key");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(certCantBeNullMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowPemKeyCantBeNullMessage() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.fromString("some cert", null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(keyCantBeNullMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }


    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowPemCertCantBeEmptyMessage() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.fromString("", "some key");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(certCantBeNullMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowPemKeyCantBeEmptyMessage() throws Exception {
        try{
            PemEtherCredentials cred = PemEtherCredentials.fromString("some cert", "");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(keyCantBeNullMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

}
