package com.morganstanley.azureapimclientsdemo;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;


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
    // https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow
    WebClient webClient = WebClient.create( "https://login.microsoftonline.com/2e3601f0-776b-44dc-bfe5-80c351c26702" );
    MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
    Map<String,List<String>> map = new HashMap<>();
    map.put( "client_id", singletonList( "6731de76-14a6-49ae-97bc-6eba6914391e" ) );
    map.put( "scope", singletonList( "user.read" ) );
    map.put( "client_secret", singletonList( "-c2P90Yav7@]uG@V1qA]1JnnED-]uVQR" ) );
//    map.put( "client_assertion_type", singletonList( "urn:ietf:params:oauth:client-assertion-type:jwt-bearer" ) );
//    map.put( "client_assertion", singletonList( "eyJhbGciOiJSUzI1NiIsIng1dCI6Imd4OHRHeXN5amNScUtqRlBuZDdSRnd2d1pJMCJ9" +
//        ".eyJ{alotofcharactershere}M8U3bSUKKJDEg" ) );
    map.put( "grant_type", singletonList( "client_credentials" ) );

//    map.put( "response_type", singletonList( "code" ) );
//    map.put( "redirect_uri", singletonList( "https://jpgough.azure-api.net" ) );
//    map.put( "response_mode", singletonList( "query" ) );
//    map.put( "state", singletonList( "12345" ) );

    multiValueMap.putAll( map );
    String response =
        webClient.method( HttpMethod.POST )
                 .uri( "/oauth2/v2.0/token" )
                 .body( BodyInserters.fromFormData( multiValueMap ) )
                 .retrieve()
                 .bodyToMono( String.class )
                 .block();
    System.out.println( response );
  }

}
