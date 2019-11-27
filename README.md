Generate certificates with https://www.digitalocean.com/community/tutorials/openssl-essentials-working-with-ssl-certificates-private-keys-and-csrs#generating-csrs

# Scenario

Imagine that you have an account with the Firm's APIs.
The APIs are managed by Azure API Manager, and that is in fact the entrypoint for dealing with inbound requests.

You need to authenticate with our services, but you don't necessarily want ot have to deal with a login page while doing so.

This is where Auth0 comes in.
With oAuth, users authenticate on Windows Active Directory (?) and receive a token.
The token they have received is signed and cerifiable by third parties.
This token is then forwarded to the Azure API Manager with requests.
Azure API Manager cannot see any of the login credentials, but it can see which groups of permissions the user has been granted (Active Directory does permission granting?!?)

Tokens can be retrieved before hand, but if the client isn't aware that oAuth is required, they may make an API request and then be redirected to the Active Directory login page if a token isn't contained.


# OSS spring cloud
https://github.com/spring-projects/spring-security/issues/6881

# How OAuth works
https://www.youtube.com/watch?time_continue=17&v=996OiexHze0&feature=emb_logo

# MSAL4J

The [MSAL4J library](https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki) is the preferred way of connecting to Azure services.

It is also possible to follow the [redirect scenario](https://docs.microsoft.com/en-us/azure/active-directory/develop/scenario-desktop-app-registration) for your application, if you prefer not to use MSAL4J

