package com.morganstanley.azureapimclientsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootApplication
public class AzureApimClientsDemoApplication {

  public static void main( String[] args ) {
    try (
        ConfigurableApplicationContext context =
            SpringApplication.run( AzureApimClientsDemoApplication.class, args ) ) {

      AzureClient azureClient = context.getBean( AzureClient.class );
      azureClient.accessPositions();
      azureClient.requestToken();
      azureClient.getSessions();
      azureClient.templateWithJwtRequest();
    }
    catch ( HttpClientErrorException e ) {
      System.err.println( e.getResponseHeaders() );
      System.err.println( e.getResponseBodyAsString() );
      throw e;
    }
  }

}
