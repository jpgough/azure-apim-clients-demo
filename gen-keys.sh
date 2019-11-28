#!/bin/sh
# Cleanup
#rm private_key* public_key* public_private_key.pfx

#openssl genrsa -out private_key.pem 2048
# We need DER format for java
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt

# Generate public key
openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der

# Generate self-signed x509 certificate
#openssl req -new -x509 -key private_key.pem -out public_key.cer -days 1825
openssl pkcs12 -export -out public_private_key.pfx -inkey private_key.pem -in public_key.cer

