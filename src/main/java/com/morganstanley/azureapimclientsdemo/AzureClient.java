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

import static com.morganstanley.azureapimclientsdemo.GlobalSettings.BASE_TOKEN_URL;
import static com.morganstanley.azureapimclientsdemo.GlobalSettings.DEFAULT_GRAPH_SCOPE;


@Service
public class AzureClient {

  private final KeyPairHelper keyPairHelper = new KeyPairHelper();

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

  public void getSessions() {
    //  GET [?speakername][&dayno][&keyword]
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.getForEntity( "https://jpgough.azure-api.net/conference/sessions",
        String.class );
    System.out.println( response );
  }

  public void requestToken() {
    RestTemplate restTemplate = new RestTemplate();
    // https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow

    MultiValueMap<String,String> data = map(
        "client_id", "16ead5d7-9392-416a-bde8-1317e8a8d254",
        "client_secret", "-c2P90Yav7@]uG@V1qA]1JnnED-]uVQR",
        "scope", DEFAULT_GRAPH_SCOPE,
        "grant_type", "client_credentials" );
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
    HttpEntity<MultiValueMap<String,String>> httpEntity =
        new HttpEntity<>( new LinkedMultiValueMap<>( data ), headers );

    System.out.println( httpEntity );
    ResponseEntity<String> responseEntity = restTemplate.postForEntity( BASE_TOKEN_URL, httpEntity, String.class );
    System.out.println( responseEntity.toString() );
  }

  /**
   * Request based on 'Access token request with shared secret'
   *
   * @return
   */
  public void templateWithJwtRequest() {
    // https://jwt.io/
    //  'https://login.microsoftonline.com/{tenant}/oauth2/v2.0/token'
    String jwt = new JwtGenerator().generateJwt();
    MultiValueMap<String,String> data = map(
        "client_id", "535fb089-9ff3-47b6-9bfb-4f1264799865",
        "scope", DEFAULT_GRAPH_SCOPE,
        "client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
        "client_assertion", jwt,
        "grant_type", "client_credentials" );
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
    HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>( data, headers );

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.postForEntity( BASE_TOKEN_URL, httpEntity, String.class );
    System.out.println( response );
  }

  private static String sampleJwt() {
    return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6IiJ9" +
        ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.C758n2kQPdmSrUn32mTsFm_LdzNKqOhTMRjhz9G2ebI";
  }

  private static String withPubCertJwt() {
    return
        "TVE4d0RRWURWUVFIREFaTWIyNWtiMjR4RnpBVkJnTlZCQW9NRGsxdmNtZGhiaUJUZEdGdWJHVjVNUkF3RGdZRFZRUUxEQWRJWVdOclpHRjVNU1F3SWdZSktvWklodmNOQVFrQkZoVndhR3RoZW01dmQzTnJhVUJuYldGcGJDNWpiMjB3SGhjTk1Ua3hNVEl3TVRBd09UTXlXaGNOTWpReE1URTRNVEF3T1RNeVdqQ0JoREVMTUFrR0ExVUVCaE1DUVZVeEV6QVJCZ05WQkFnTUNsTnZiV1V0VTNSaGRHVXhEekFOQmdOVkJBY01Ca3h2Ym1SdmJqRVhNQlVHQTFVRUNnd09UVzl5WjJGdUlGTjBZVzVzWlhreEVEQU9CZ05WQkFzTUIwaGhZMnRrWVhreEpEQWlCZ2txaGtpRzl3MEJDUUVXRlhCb2EyRjZibTkzYzJ0cFFHZHRZV2xzTG1OdmJUQ0JuekFOQmdrcWhraUc5dzBCQVFFRkFBT0JqUUF3Z1lrQ2dZRUF6MHRFTkNPWnlRWncvY3pMRFpyeHIrSEZYVTdXQ3JTQTNxOFppMVd2Z21zZ1ZZZGNWWEdiZmRRNkg5SHMxbW4veUw1OXEwMWxZSS94V1VJcHZoaHovYUh0djcxTUJXRTVsRGZIK1FxdytLTnYvc2pnZTA1UGpPR3oxamhMdXZvcEp5aE9jWUV5RDlKZU5DcEptY1B1U202aHc2L0RHS0NKNXpmbmNFKysvVjhDQXdFQUFhTlRNRkV3SFFZRFZSME9CQllFRk41YkVReEhwdm9PZk9ZQlpZVlhXaG85bUxuOE1COEdBMVVkSXdRWU1CYUFGTjViRVF4SHB2b09mT1lCWllWWFdobzltTG44TUE4R0ExVWRFd0VCL3dRRk1BTUJBZjh3RFFZSktvWklodmNOQVFFTEJRQURnWUVBRXFpTC9WZ1Y3UlFrMkdPRU1KUWpzTnhycVROSUV5VkNYdHFBTHhpM2xvcHZoKzNlT3JTT0Y1cmpWT0F3QS9mNUNBZjBsajZFNkowN3YxMmlsVDNGTklOS04xaXQ5VVF3VFF6amdFRldHd2tqeHFlTmJoMXdFMkZUaWhjaTkreTRwWkNKUDNMaEtjbVZXWVFLV3ZiOXVzN0ZhZ1JLYytld012NndmZStTS0NnPSJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.LzRnD8T1JToE_RMW_EnKSqtvw6IFa95GZyhUQO7uUus";
  }

  private static String staticSha1SumBase64() {
    return
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6IlpqaGlaR0ZsTlRRMU0yVmpNak0yTWpWa056RXpNMk0xTmpZek1HSTVOMlZrWmpKaU5EZzFNZ289In0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.6IN6BSHtN3NL_8_IeV8qj73DXWshXTblN0M6IVGjDYg";
  }

  private static String staticDecodeSha1SumBase64Encode() {
    return
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6Ik0yTTJaVEV5TURKbFlURmxNRGszWXpGak9EQTBNelpoWlRRek1ESTJNemd5TXpOaE0yVmxNU0FnTFFvPSJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.XdKTL9pRkbaMjLfpyAPlo1eGcAjvy4okJW_P1l6dDPU";
  }

  private static String staticSha1Sum() {
    return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6ImY4YmRhZTU0NTNlYzIzNjI1ZDcxMzNjNTY2MzBiOTdlZGYyYjQ4NTIifQ" +
        ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.c_XLUOtAuL5LObMKr8FGsCkoAj0PQ0Pf4C1LRj2gLZg";
  }

  private static String staticJwtBase64PubCert() {
    return
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6IkxTMHRMUzFDUlVkSlRpQkRSVkpVU1VaSlEwRlVSUzB0TFMwdENrMUpTVU0xYWtORFFXc3JaMEYzU1VKQlowbFZZbXRQUkZwVlIybDVTWEJQTXpGNWJ6aHBSVE0xTUdNM1lqSTBkMFJSV1VwTGIxcEphSFpqVGtGUlJVd0tRbEZCZDJkWlVYaERla0ZLUW1kT1ZrSkJXVlJCYTBaV1RWSk5kMFZSV1VSV1VWRkpSRUZ3VkdJeU1XeE1WazR3V1ZoU2JFMVJPSGRFVVZsRVZsRlJTQXBFUVZwTllqSTFhMkl5TkhoR2VrRldRbWRPVmtKQmIwMUVhekYyWTIxa2FHSnBRbFJrUjBaMVlrZFdOVTFTUVhkRVoxbEVWbEZSVEVSQlpFbFpWMDV5Q2xwSFJqVk5VMUYzU1dkWlNrdHZXa2xvZG1OT1FWRnJRa1pvVm5kaFIzUm9aVzAxZG1RelRuSmhWVUp1WWxkR2NHSkROV3BpTWpCM1NHaGpUazFVYTNnS1RWUkpkMDFVUVhkUFZFMTVWMmhqVGsxcVVYaE5WRVUwVFZSQmQwOVVUWGxYYWtOQ2FFUkZURTFCYTBkQk1WVkZRbWhOUTFGV1ZYaEZla0ZTUW1kT1ZncENRV2ROUTJ4T2RtSlhWWFJWTTFKb1pFZFZlRVI2UVU1Q1owNVdRa0ZqVFVKcmVIWmliVkoyWW1wRldFMUNWVWRCTVZWRlEyZDNUMVJYT1hsYU1rWjFDa2xHVGpCWlZ6VnpXbGhyZUVWRVFVOUNaMDVXUWtGelRVSXdhR2haTW5ScldWaHJlRXBFUVdsQ1oydHhhR3RwUnpsM01FSkRVVVZYUmxoQ2IyRXlSallLWW0wNU0yTXlkSEJSUjJSMFdWZHNjMHh0VG5aaVZFTkNibnBCVGtKbmEzRm9hMmxIT1hjd1FrRlJSVVpCUVU5Q2FsRkJkMmRaYTBObldVVkJlakIwUlFwT1EwOWFlVkZhZHk5amVreEVXbko0Y2l0SVJsaFZOMWREY2xOQk0zRTRXbWt4VjNabmJYTm5WbGxrWTFaWVIySm1aRkUyU0RsSWN6RnRiaTk1VERVNUNuRXdNV3haU1M5NFYxVkpjSFpvYUhvdllVaDBkamN4VFVKWFJUVnNSR1pJSzFGeGR5dExUbll2YzJwblpUQTFVR3BQUjNveGFtaE1kWFp2Y0VwNWFFOEtZMWxGZVVRNVNtVk9RM0JLYldOUWRWTnRObWgzTmk5RVIwdERTalY2Wm01alJTc3JMMVk0UTBGM1JVRkJZVTVVVFVaRmQwaFJXVVJXVWpCUFFrSlpSUXBHVGpWaVJWRjRTSEIyYjA5bVQxbENXbGxXV0Zkb2J6bHRURzQ0VFVJNFIwRXhWV1JKZDFGWlRVSmhRVVpPTldKRlVYaEljSFp2VDJaUFdVSmFXVlpZQ2xkb2J6bHRURzQ0VFVFNFIwRXhWV1JGZDBWQ0wzZFJSazFCVFVKQlpqaDNSRkZaU2t0dldrbG9kbU5PUVZGRlRFSlJRVVJuV1VWQlJYRnBUQzlXWjFZS04xSlJhekpIVDBWTlNsRnFjMDU0Y25GVVRrbEZlVlpEV0hSeFFVeDRhVE5zYjNCMmFDc3paVTl5VTA5R05YSnFWazlCZDBFdlpqVkRRV1l3YkdvMlJRbzJTakEzZGpFeWFXeFVNMFpPU1U1TFRqRnBkRGxWVVhkVVVYcHFaMFZHVjBkM2EycDRjV1ZPWW1neGQwVXlSbFJwYUdOcE9TdDVOSEJhUTBwUU0weG9Da3RqYlZaWFdWRkxWM1ppT1hWek4wWmhaMUpMWXl0bGQwMTJObmRtWlN0VFMwTm5QUW90TFMwdExVVk9SQ0JEUlZKVVNVWkpRMEZVUlMwdExTMHRDZz09In0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.QXcDPnC3YYgmdM0oqLex0mEq1lN3PoLXOsqpw4QndB0";
  }

  private static MultiValueMap<String,String> map( String... keyValues ) {
    MultiValueMap<String,String> aggregated = new LinkedMultiValueMap<>();
    for ( int i = 0; i < keyValues.length; i += 2 ) {
      aggregated.add( keyValues[i], keyValues[i + 1] );
    }
    return aggregated;
  }

}
