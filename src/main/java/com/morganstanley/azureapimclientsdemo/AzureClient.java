package com.morganstanley.azureapimclientsdemo;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;


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

  public void requestToken() {
    RestTemplate restTemplate = new RestTemplate();
    // https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow

    MultiValueMap<String,String> data = map( "client_id", "16ead5d7-9392-416a-bde8-1317e8a8d254",
        "client_secret", "-c2P90Yav7@]uG@V1qA]1JnnED-]uVQR",
        "scope", "https://graph.microsoft.com/.default",
        "grant_type", "client_credentials" );
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
    HttpEntity<MultiValueMap<String,String>> httpEntity =
        new HttpEntity<>( new LinkedMultiValueMap<>( data ), headers );

    System.out.println( httpEntity );
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        "https://login.microsoftonline.com/2e3601f0-776b-44dc-bfe5-80c351c26702/oauth2/v2.0/token"
        , httpEntity, String.class );
    System.out.println( responseEntity.toString() );
  }

  private static String base64( String source ) {
    return Base64.getEncoder().encodeToString( source.getBytes() );
  }

  private static MultiValueMap<String,String> map( String... keyValues ) {
    MultiValueMap<String,String> aggregated = new LinkedMultiValueMap<>();
    for ( int i = 0; i < keyValues.length; i += 2 ) {
      aggregated.add( keyValues[i], keyValues[i + 1] );
    }
    return aggregated;
  }

}
