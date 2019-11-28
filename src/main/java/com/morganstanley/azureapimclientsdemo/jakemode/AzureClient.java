package com.morganstanley.azureapimclientsdemo.jakemode;


import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.morganstanley.azureapimclientsdemo.GlobalSettings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki/Acquiring-Tokens
public class AzureClient {
  public static void main( String... args ) throws MalformedURLException {
    // generate with openssl genrsa -out private_key.pem 2048
    PrivateKey privateKey = readPrivateKey( "private_key.der" );
    X509Certificate certificate = readPublicCert( "public_key.cer" );

// https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki/Client-Applications
    String TENANT_SPECIFIC_AUTHORITY = GlobalSettings.BASE_TOKEN_URL;
    ConfidentialClientApplication app = ConfidentialClientApplication.builder(
        GlobalSettings.CLIENT_ID,
        ClientCredentialFactory
            .createFromCertificate( privateKey, certificate ) )
//            .createFromSecret( CLIENT_SECRET ) )
                                                                     .authority( TENANT_SPECIFIC_AUTHORITY )
                                                                     .build();
//    String jwt = new JwtGenerator().constructJwt( "private_key.der", "public_key.der" );

    Set<String> scopes = set( GlobalSettings.DEFAULT_GRAPH_SCOPE );
    ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder( scopes ).build();
    CompletableFuture<IAuthenticationResult> future = app.acquireToken( clientCredentialParameters );
    future.handle( ( res, ex ) -> {
      if ( ex != null ) {
        throw new RuntimeException( ex );
      }
      else {
        System.out.println( "res.accessToken() = " + res.accessToken() );
        System.out.println( "res.idToken() = " + res.idToken() );
        System.out.println( "res.scopes() = " + res.scopes() );
        System.out.println( "res.expiresOnDate() = " + res.expiresOnDate() );
      }
      return "unknown";
    } );
    future.join();
  }

  private static <E> Set<E> set( E... e ) {
    return new HashSet<>( Arrays.asList( e ) );
  }

  private static PrivateKey readPrivateKey( String filename ) {
    try {
      byte[] keyBytes = Files.readAllBytes( Paths.get( filename ) );
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( keyBytes );
      KeyFactory kf = KeyFactory.getInstance( "RSA" );
      return kf.generatePrivate( spec );
    }
    catch ( IOException | NoSuchAlgorithmException | InvalidKeySpecException e ) {
      throw new RuntimeException( e );
    }
  }

  public static X509Certificate readPublicCert( String filename ) {
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance( "X.509" );
      FileInputStream fileInputStream = new FileInputStream( filename );
      X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate( fileInputStream );
      return certificate;
    }
    catch ( CertificateException | FileNotFoundException e ) {
      throw new RuntimeException( e );
    }

  }
}
