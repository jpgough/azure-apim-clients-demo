package com.morganstanley.azureapimclientsdemo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.util.Arrays.asList;

public class KeyPairHelper {

  public RSAPrivateKey readPrivateKey( File privateKey ) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream( privateKey );
      DataInputStream dis = new DataInputStream( fis );
      byte[] keyBytes = new byte[(int) privateKey.length()];
      dis.readFully( keyBytes );
      dis.close();

      String temp = new String( keyBytes );
      String privKeyPEM = temp.replace( "-----BEGIN PRIVATE KEY-----\n", "" );
      privKeyPEM = privKeyPEM.replace( "-----END PRIVATE KEY-----", "" );
      //System.out.println("Private key\n"+privKeyPEM);

      Base64.Decoder b64 = Base64.getDecoder();
      byte[] decoded = b64.decode( privKeyPEM );

      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( decoded );
      KeyFactory kf = KeyFactory.getInstance( "RSA" );
      return (RSAPrivateKey) kf.generatePrivate( spec );
    }
    catch ( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  public RSAPublicKey readPublicKey( File publicKey ) {
    try {
      byte[] fileContents = Files.readAllBytes( publicKey.toPath() );
      if ( endsWith( publicKey.getName(), ".pem", ".crt", ".cer" ) ) {
        // We need to convert ASCII to DER
        // https://knowledge.digicert.com/solution/SO26449.html
        // https://support.ssl.com/Knowledgebase/Article/View/19/0/der-vs-crt-vs-cer-vs-pem-certificates-and-how-to-convert-them
        fileContents = readableToBinary( fileContents );
      }
      X509EncodedKeySpec spec = new X509EncodedKeySpec( fileContents );
      KeyFactory keyFactory = KeyFactory.getInstance( "RSA" );
      return (RSAPublicKey) keyFactory.generatePublic( spec );
    }
    catch ( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  private static byte[] readableToBinary( byte[] fileContents ) {
    String raw = new String( fileContents );
    String[] lines = raw.split( "\n" );
    if ( !lines[0].contains( "BEGIN CERTIFICATE" ) ) {
      throw new RuntimeException( "Not a valid cert" );
    }
    if ( !lines[lines.length - 1].contains( "END CERTIFICATE" ) ) {
      throw new RuntimeException( "Not a valid cert" );
    }
    List<String> keyContents = new ArrayList<>( asList( lines ).subList( 1, lines.length - 1 ) );
    String baseEncodedKey = String.join( "", keyContents );
    return Base64.getDecoder().decode( baseEncodedKey.getBytes() );
//    return baseEncodedKey.getBytes();
  }

  private static boolean endsWith( String raw, String... patterns ) {
    for ( String pattern : patterns ) {
      if ( raw.toLowerCase().endsWith( pattern ) ) {
        return true;
      }
    }
    return false;
  }

}
