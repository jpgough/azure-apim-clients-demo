package com.morganstanley.azureapimclientsdemo;

import org.junit.Test;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.Assert.assertNotNull;

public class KeyPairHelperTest {
  @Test
  public void canReadPrivate() {
    KeyPairHelper keyPairHelper = new KeyPairHelper();

    File privateKeyFile = new File( "privatekey.pem" );
    RSAPrivateKey rsaPrivateKey = keyPairHelper.readPrivateKey( privateKeyFile );
    assertNotNull( rsaPrivateKey );
  }

  @Test
  public void canReadPublic() {
    KeyPairHelper keyPairHelper = new KeyPairHelper();

    File publicKeyFile = new File( "publickey.cer" );
    RSAPublicKey rsaPublicKey = keyPairHelper.readPublicKey( publicKeyFile );
    assertNotNull( rsaPublicKey );
  }
}
