package com.morganstanley.azureapimclientsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AzureApimClientsDemoApplication {

  public static void main( String[] args ) {
    ConfigurableApplicationContext context =
        SpringApplication.run( AzureApimClientsDemoApplication.class, args );
    AzureClient azureClient = context.getBean( AzureClient.class );
    azureClient.accessPositions();
  }

}
