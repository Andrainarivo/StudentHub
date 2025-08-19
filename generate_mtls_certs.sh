#!/bin/bash
# ============================================================
# Script de génération des certificats pour mTLS (Java)
# ============================================================
# Objectif :
#   - Générer une CA interne (clé privée + certificat auto-signé)
#   - Générer les certificats serveur et client signés par cette CA
#   - Préparer les keystores et truststores (PKCS12) adaptés pour Java
#
# Concepts :
#   - RSA : algorithme utilisé pour générer les clés publiques/privées
#   - mTLS : Mutual TLS, chaque partie doit présenter un certificat valide
#   - Keystore : contient l'identité (clé privée + certificat signé)
#   - Truststore : contient les autorités de confiance (CA)
#
# Chaque entité (client, serveur) aura :
#   - Un keystore (son identité)
#   - Un truststore (qui fait confiance au CA commun)
#
# Extensions utilisées :
#   - .p12 = PKCS12 (Java Keystore / Truststore moderne)
#   - .crt = certificat public X.509
# ============================================================

# === Paramètres globaux ===
CA_DIR="ca"
CLIENT_DIR="studenthub_client/certs"
SERVER_DIR="studenthub_server/certs"

CA_ALIAS="studenthub-ca"
CLIENT_ALIAS="studenthub-client"
SERVER_ALIAS="studenthub-server"

# Mots de passe différents pour chaque entité
CA_PASS="capass123"
CLIENT_PASS="clientpass123"
SERVER_PASS="serverpass123"

# DN = Distinguished Name (identité dans le certificat)
CA_DN="CN=StudentHub-CA, OU=CA, O=StudentHub, L=Lafra, C=MG"
CLIENT_DN="CN=StudentHub-Client, OU=Client, O=StudentHub, L=Lafra, C=MG"
SERVER_DN="CN=StudentHub-Server, OU=Server, O=StudentHub, L=Lafra, C=MG"

# Nettoyage et création des dossiers
rm -rf "$CA_DIR" "$CLIENT_DIR" "$SERVER_DIR"
mkdir -p "$CA_DIR" "$CLIENT_DIR" "$SERVER_DIR"

# ============================================================
# 1. Génération de la CA (auto-signée)
# ============================================================
echo "🔑 Génération de la CA..."

keytool -genkeypair \
  -alias "$CA_ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -dname "$CA_DN" \
  -validity 3650 \
  -storetype PKCS12 \
  -keystore "$CA_DIR/ca-keystore.p12" \
  -storepass "$CA_PASS" \
  -keypass "$CA_PASS" \
  -ext bc=ca:true

# Export du certificat public de la CA
echo "📜 Export du certificat public de la CA..."
keytool -exportcert \
  -alias "$CA_ALIAS" \
  -storepass "$CA_PASS" \
  -keystore "$CA_DIR/ca-keystore.p12" \
  -rfc -file "$CA_DIR/ca-cert.crt"

# ============================================================
# 2. Génération du certificat serveur signé par la CA
# ============================================================
echo "🖥️ Génération du certificat serveur..."

# Générer paire de clés serveur
keytool -genkeypair \
  -alias "$SERVER_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -dname "$SERVER_DN" \
  -validity 3650 \
  -storetype PKCS12 \
  -keystore "$SERVER_DIR/server-keystore.p12" \
  -storepass "$SERVER_PASS" \
  -keypass "$SERVER_PASS"

# Générer CSR (Certificate Signing Request : demande de signature)
keytool -certreq \
  -alias "$SERVER_ALIAS" \
  -storepass "$SERVER_PASS" \
  -keystore "$SERVER_DIR/server-keystore.p12" \
  -file "$SERVER_DIR/server.csr"

# Signer CSR avec la CA
keytool -gencert \
  -alias "$CA_ALIAS" \
  -storepass "$CA_PASS" \
  -keystore "$CA_DIR/ca-keystore.p12" \
  -infile "$SERVER_DIR/server.csr" \
  -outfile "$SERVER_DIR/server-cert.crt" \
  -validity 3650 \
  -rfc

# Import du certificat CA dans keystore serveur
keytool -importcert -noprompt \
  -alias "$CA_ALIAS" \
  -file "$CA_DIR/ca-cert.crt" \
  -keystore "$SERVER_DIR/server-keystore.p12" \
  -storepass "$SERVER_PASS"

# Import du certificat signé serveur dans son keystore
keytool -importcert -noprompt \
  -alias "$SERVER_ALIAS" \
  -file "$SERVER_DIR/server-cert.crt" \
  -keystore "$SERVER_DIR/server-keystore.p12" \
  -storepass "$SERVER_PASS"

# Création truststore serveur (avec seulement la CA)
keytool -importcert -noprompt \
  -alias "$CA_ALIAS" \
  -file "$CA_DIR/ca-cert.crt" \
  -keystore "$SERVER_DIR/server-truststore.p12" \
  -storetype PKCS12 \
  -storepass "$SERVER_PASS"

# ============================================================
# 3. Génération du certificat client signé par la CA
# ============================================================
echo "👤 Génération du certificat client..."

# Générer paire de clés client
keytool -genkeypair \
  -alias "$CLIENT_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -dname "$CLIENT_DN" \
  -validity 3650 \
  -storetype PKCS12 \
  -keystore "$CLIENT_DIR/client-keystore.p12" \
  -storepass "$CLIENT_PASS" \
  -keypass "$CLIENT_PASS"

# Générer CSR client
keytool -certreq \
  -alias "$CLIENT_ALIAS" \
  -storepass "$CLIENT_PASS" \
  -keystore "$CLIENT_DIR/client-keystore.p12" \
  -file "$CLIENT_DIR/client.csr"

# Signer CSR avec la CA
keytool -gencert \
  -alias "$CA_ALIAS" \
  -storepass "$CA_PASS" \
  -keystore "$CA_DIR/ca-keystore.p12" \
  -infile "$CLIENT_DIR/client.csr" \
  -outfile "$CLIENT_DIR/client-cert.crt" \
  -validity 3650 \
  -rfc

# Import du certificat CA dans keystore client
keytool -importcert -noprompt \
  -alias "$CA_ALIAS" \
  -file "$CA_DIR/ca-cert.crt" \
  -keystore "$CLIENT_DIR/client-keystore.p12" \
  -storepass "$CLIENT_PASS"

# Import du certificat signé client dans son keystore
keytool -importcert -noprompt \
  -alias "$CLIENT_ALIAS" \
  -file "$CLIENT_DIR/client-cert.crt" \
  -keystore "$CLIENT_DIR/client-keystore.p12" \
  -storepass "$CLIENT_PASS"

# Création truststore client (avec seulement la CA)
keytool -importcert -noprompt \
  -alias "$CA_ALIAS" \
  -file "$CA_DIR/ca-cert.crt" \
  -keystore "$CLIENT_DIR/client-truststore.p12" \
  -storetype PKCS12 \
  -storepass "$CLIENT_PASS"

# ============================================================
echo "✅ Certificats mTLS générés avec succès !"
echo "   - CA        : $CA_DIR"
echo "   - Serveur   : $SERVER_DIR"
echo "   - Client    : $CLIENT_DIR"
