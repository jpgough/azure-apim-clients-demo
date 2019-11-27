package com.morganstanley.azureapimclientsdemo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class JwtGenerator {
  public String generateJwt() {
    try {
      int KEY_SIZE = 1024;
      byte[] seed = new byte[]{1};
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( "RSA" );
      keyPairGenerator.initialize( KEY_SIZE, new SecureRandom( seed ) );
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
      Algorithm algorithm = Algorithm.RSA256( publicKey, privateKey );
      return JWT.create()
                .sign( algorithm );
    }
    catch ( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  public String constructJwt( String privateKeyFile, String publicKeyFile ) {
    RSAPrivateKey rsaPrivateKey = readPrivateKey( privateKeyFile );
    RSAPublicKey rsaPublicKey = readPublicKey( publicKeyFile );
    Algorithm algorithm = Algorithm.RSA256( rsaPublicKey, rsaPrivateKey );
    return JWT.create()
              .sign( algorithm );
  }

  private RSAPrivateKey readPrivateKey( String filename ) {
    try {
      byte[] keyBytes = Files.readAllBytes( Paths.get( filename ) );
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( keyBytes );
      KeyFactory kf = KeyFactory.getInstance( "RSA" );
      return (RSAPrivateKey) kf.generatePrivate( spec );
    }
    catch ( IOException | NoSuchAlgorithmException | InvalidKeySpecException e ) {
      throw new RuntimeException( e );
    }
  }

  private RSAPublicKey readPublicKey( String filename ) {
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
}
