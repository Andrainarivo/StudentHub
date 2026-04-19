# 🎓 StudentHub  

Application sécurisée **client-serveur** en Java pour la gestion des étudiants.  
Le projet est structuré en deux modules distincts :  

+ studenthub_client → Application Cliente
+ studenthub_server → Serveur Java

---

## Conception & Modélisation  

### Architecture  

[ Client Swing + Protocole Applicatif ] ⇄ [ Socket client ] ⇄ [ Serveur Socket ] ⇄ [ MySQL ]

- **Couche Application :**
  - Protocole maison basé sur des **requetes XML** échangés entre client et serveur.  
  - Chaque requête contient un **code d’opération** (ADD, GET, UPDATE, DELETE) et une **payload XML** (la requete lui meme).  
  - Si le serveur est indisponible, les requêtes CRUD sont **sérialisées en XML** côté client  
  - Transmission différée dès que la connexion est rétablie
  - Le serveur interprète la requête, interagit avec la base, puis renvoie une réponse XML.
 
- **Couche Session & Présentation :**
  - Chiffrement asymétrique de la communication avec RSA 2048.
  - Authentification mutuelle des deux parties avec TLSv3.  
  
---

## Technologies utilisées  

- **Runtime**: Java 21  
- **Écosystème Java**: Swing, JSSE, JAXB
- **Base de donnée**: MySQL
- **Build management**: Maven, keytool

---

## Lancer l'application  

### 1. Génération des certificats TLS (CA-client-serveur)
Executer le script `generate_mtls_certs.sh` 

```bash
chmod 764 generate_mtls_certs.sh
bash generate_mtls_certs.sh
```
### 2. Lancer le serveur
```bash
cd studenthub_server
mvn clean install
mvn exec:java -Dexec.mainClass="mg.eni.studenthub.server.StudentServer"
```

### 3. Lancer le client
```bash
cd studenthub_client
mvn clean install
mvn exec:java
```

## Améliorations futures
```text
Ajout de la gestion des utilisateurs & rôles
Évolution vers une API REST pour exposition web/mobile
```


## Documentations

### Contexte

Le projet **StudentHub** s’inscrit dans un cadre pédagogique autour de la **programmation réseau sécurisée** et du **développement d’applications distribuées**.  

Les objectifs principaux sont :  
- Mettre en place une **communication sécurisée** entre un client Swing et un serveur Java.  
- Utiliser le **protocol cryptographique SSL/TLS** afin de garantir :  
  - **Confidentialité** des échanges 
  - **Authenticité** des entités (client ↔ serveur)  
  - **Intégrité** des données transmises  
- Implémenter un **protocole applicatif en XML** au-dessus des couches SSL/TLS.  
- Gérer la **persistance des données** via une base MySQL côté serveur.

### Structure  
```text
studenthub_client
├── certs → certificats et clés TLS du client
├── logs → journaux côté client
├── requests → requêtes en attente (XML)
├── responses → réponses sauvegardées (XML)
└── src/main/java/mg/eni/studenthub
├── auth → authentification (credentials gérés par l'admin)
├── client → client TLS (studentClient), retrythread
├── config → chargement du fichier de configuration (config.properties) et configuration logs
├── controller → logique CRUD
├── model → entités
├── shared → classes communes client/serveur
└── utils → utilitaires (DB, Sérialisation/desérialisation XML)
├── view → Vue Swing

studenthub_server
├── certs → certificats et clés TLS du serveur
├── logs → journaux côté serveur
└── src/main/java/mg/eni/studenthub
├── config → config.properties et logging
├── dao → accès MySQL (requete CRUD)
├── model → entité (Student)
├── server → serveur TLS
├── shared → classes communes client/serveur
└── utils → utilitaire (DB)
```  

### Script d’automatisation

Le script generate_certs.sh :

  - Génère une clé RSA 2048 bits pour le CA, serveur, client.
  - Crée des keystore PKCS12 pour le serveur et le client.
  - Exporte les certificats publics (.crt) pour inspection ou distribution.
  - Crée des truststores contenant seulement le certificat du CA (chaîne de confiance).

**i** C’est ce truststore qui permet au serveur de faire confiance au client (et inversement).

### SSL/TLS

  - Le serveur prouve son identité au client (via son certificat signé par la CA).
  - Le client prouve aussi son identité au serveur (certificat signé par la même CA).
  - Java Secure Socket Extension (JSSE) → utilise SSLContext initialisé avec :
    - Keystore → sa clé privée et son certificat.
    - Truststore → CA de confiance (CA).

### Tester les certificats

**Vérifier un certificat**
```bash
openssl x509 -in studenthub_server/certs/server-cert.crt -text -noout
```

**Vérifier la chaîne de confiance**
```bash
openssl verify -CAfile ca/ca-cert.crt studenthub_client/certs/client-cert.crt
```
