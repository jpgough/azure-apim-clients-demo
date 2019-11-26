package com.morganstanley.azureapimclientsdemo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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
}
