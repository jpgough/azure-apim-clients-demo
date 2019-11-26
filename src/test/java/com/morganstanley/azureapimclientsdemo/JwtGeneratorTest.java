package com.morganstanley.azureapimclientsdemo;

import org.junit.Test;

public class JwtGeneratorTest {
  @Test
  public void display() {
    JwtGenerator jwtGenerator = new JwtGenerator();
    System.out.println( jwtGenerator.generateJwt() );
  }
}
