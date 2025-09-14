# 🎓 StudentHub  

Application **client-serveur sécurisée** en Java pour la gestion des étudiants.  
Le projet est structuré en deux modules distincts :  

studenthub_client → Application lourde Swing (frontend + socket client)
studenthub_server → Serveur Java (serveur socket TLS + base MySQL)


---

## 📌 Contexte & Objectifs  

Le projet **StudentHub** s’inscrit dans un cadre pédagogique autour de la **programmation réseau sécurisée** et du **développement d’applications distribuées**.  

Les objectifs principaux sont :  
- Mettre en place une **communication sécurisée** entre un client Swing et un serveur Java.  
- Utiliser la **cryptographie asymétrique** (RSA) afin de garantir :  
  - **Confidentialité** des échanges (mTLS)  
  - **Authenticité** des entités (client ↔ serveur)  
  - **Intégrité** des données transmises  
- Implémenter un **protocole applicatif personnalisé** au-dessus des sockets TLS (XML).  
- Gérer la **persistance des données** via une base MySQL côté serveur.  
- Offrir une **interface utilisateur (Swing)** côté client, avec une architecture MVC.  

---

## ✨ Fonctionnalités principales  

- **Authentification mutuelle (mTLS)** entre client et serveur via certificats (`keytool`)  
- **Base MySQL** avec table `students` (id, regnum, firstname, lastname, email, id_card, address, level, scholarship)  
- **Opérations CRUD** complètes :  
  - Création d’un étudiant  
  - Consultation (un étudiant / tous les étudiants)  
  - Mise à jour d’un étudiant  
  - Suppression d’un étudiant  
- **Communication réseau sécurisée** :  
  - Sockets TLS avec certificats propres à chaque module  
  - Vérification mutuelle des identités (mTLS)   
- **Gestion des erreurs réseau** :  
  - Si le serveur est indisponible, les requêtes CRUD sont **sérialisées en XML** côté client  
  - Transmission différée dès que la connexion est rétablie  
- **Client Swing (MVC)** avec :  
  - Formulaire complet de saisie étudiant  
  - JTable interactive (recherche, filtrage)  
  - Journalisation (logs) des événements  

---

## 📂 Structure du projet  
```
studenthub_client
├── certs → certificats et clés TLS du client
├── logs → journaux côté client
├── requests → requêtes en attente (XML)
├── responses → réponses sauvegardées (XML)
└── src/main/java/mg/eni/studenthub
├── auth → authentification (credentials fourni par l'admin du BD)
├── client → client TLS (studentClient), retrythread
├── config → chargement du fichier de configuration (config.properties) et configuration logs
├── controller → logique CRUD
├── model → entités
├── shared → classes communes client/serveur
└── utils → utilitaires (DB, Sérialisation/desérialisation XML, TLS)
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

---

## 🏗️ Conception & Modélisation  

### Architecture globale  

[ Client Swing ] ⇄ [ Sockets TLS + Protocole Applicatif ] ⇄ [ Serveur Java ] ⇄ [ MySQL ]


- **Couche Réseau :**
  - Utilisation de **sockets sécurisés (SSLServerSocket / SSLSocket)**.  
  - Authentification mutuelle : le client et le serveur échangent leurs certificats.  
  - Communication chiffrée avec RSA 2048.  

- **Couche Applicative :**
  - Protocole maison basé sur des **requetes XML** échangés entre client et serveur.  
  - Chaque requête contient un **code d’opération** (ADD, GET, UPDATE, DELETE) et une **payload XML** (la requete lui meme).  
  - Le serveur interprète la requête, interagit avec la base, puis renvoie une réponse XML.  

- **Couche Persistance :**
  - Base MySQL `students & users`.  
  - Pattern DAO pour l’accès aux données.  

- **Couche Présentation (Swing) :**
  - Architecture **MVC**.  
  - Interface utilisateur modernisé à l'aide **FlatLaf**.  

---

## ⚙️ Technologies utilisées  

- **Java 21**  
- **Swing (+FlatLaf)**  
- **Sockets TLS (mTLS)**
- **MySQL** 
- **Maven**
- **JAXB**
- **keytool**
---

## 🚀 Lancer l'application  

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

## 🛠️ Améliorations futures

    Ajout de la gestion des utilisateurs & rôles

    Packaging exécutable unique (.jar) avec dépendances

    Évolution vers une API REST pour exposition web/mobile


## Documentations
### ⚙️ Script d’automatisation

Le script generate_certs.sh :

  - Génère une clé RSA 2048 bits pour le CA, serveur, client.
  - Crée des keystore PKCS12 pour le serveur et le client.
  - Exporte les certificats publics (.crt) pour inspection ou distribution.
  - Crée des truststores contenant seulement le certificat du CA (chaîne de confiance).

**imp** : C’est ce truststore qui permet au serveur de faire confiance au client (et inversement).

### 🔒 RSA et mTLS

- **RSA** → algorithme utilisé pour générer les paires de clés (2048 bits).

- **Certificat** → prouve l’identité du client/serveur, signé par le CA.

- **mTLS (mutual TLS)** →

  - Le serveur prouve son identité au client (via son certificat signé par la CA).
  - Le client prouve aussi son identité au serveur (certificat signé par la même CA).
  - Java (JSSE) → utilise SSLContext initialisé avec :
    - Keystore → pour sa clé privée et son certificat.
    - Truststore → pour la liste des CA de confiance (CA).

### 🧪 Tester les certificats

**Vérifier un certificat**
```bash
openssl x509 -in studenthub_server/certs/server-cert.crt -text -noout
```

**Vérifier la chaîne de confiance**
```bash
openssl verify -CAfile ca/ca-cert.crt studenthub_client/certs/client-cert.crt
```

📌 Auteur : Andrainarivo / ENI
📅 Version actuelle : 1.0