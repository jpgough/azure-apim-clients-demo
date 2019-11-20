package com.morganstanley.azureapimclientsdemo;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AzureClient {
  private final WebClient webClient;

  AzureClient() {
    webClient = WebClient.builder()
                         .baseUrl( "https://jpgough.azure-api.net/" )
                         .build();
  }

  public void accessPositions() {
    String response =
        webClient.method( HttpMethod.GET )
                 .uri( "/positions" )
//             .body( BodyInserters.fromPublisher( Mono.just( "data" ) ) )
                 .retrieve()
                 .bodyToMono( String.class )
                 .block();

    System.out.println( response );
  }

  public void accessPositionsOauth() {
    String response =
        webClient.method( HttpMethod.GET )
                 .uri( "/positions-oauth" )
                 .retrieve()
                 .bodyToMono( String.class )
                 .block();
    System.out.println( response );
  }
}
