# Mode d'emploi pour le projet JavaServer

## Introduction

Ce projet consiste à créer un serveur en Java. Il contient plusieurs fichiers et répertoires qui sont nécessaires pour le bon fonctionnement du serveur. Ce guide vous expliquera comment configurer et exécuter ce serveur.

## Structure du projet

Le projet est structuré comme suit :

- `Client.jar` : Archive Java pour le client.
- `Serveur.jar` : Archive Java pour le serveur.
- `Server/` : Répertoire contenant les fichiers sources et configurations.
  - `Server/.idea` : Répertoire de configuration pour l'IDE IntelliJ IDEA.
  - `Server/.vscode` : Répertoire de configuration pour l'IDE Visual Studio Code.
  - `Server/out` : Répertoire de sortie pour les fichiers compilés.
  - `Server/src` : Répertoire contenant les fichiers sources Java.
    - `Client.java` : Classe Java pour le client.
    - `Server.java` : Classe Java pour le serveur.
    - `UDPIO.java` : Classe Java pour la gestion des entrées/sorties UDP.
    - `UDPSocketScanner.java` : Classe Java pour le scanner de socket UDP.
  - `Server/untitled.iml` : Fichier de configuration du projet IntelliJ IDEA.

## Prérequis

Avant de commencer, assurez-vous d'avoir les éléments suivants installés sur votre machine :

- Java Development Kit (JDK) 8 ou supérieur
- Un IDE comme IntelliJ IDEA ou Visual Studio Code (facultatif mais recommandé)
- Maven pour la gestion des dépendances (facultatif)

## Étapes pour configurer et exécuter le serveur

### 1. Cloner le dépôt

Clonez le dépôt GitHub sur votre machine locale en utilisant la commande suivante :

```sh
git clone https://github.com/ZartoxSFT/JavaServer.git
```

### 2. Importer le projet dans l'IDE

Si vous utilisez un IDE comme IntelliJ IDEA ou Visual Studio Code, importez le projet en tant que projet Java existant.

### 3. Compiler les fichiers sources

Naviguez vers le répertoire `Server/src` et compilez les fichiers sources Java. Vous pouvez utiliser l'IDE pour compiler les fichiers ou utiliser la ligne de commande :

```sh
javac Server/src/*.java
```

### 4. Exécuter le serveur

Pour exécuter le serveur, utilisez la classe `Server.java`. Vous pouvez le faire à partir de l'IDE ou de la ligne de commande :

```sh
java Server/src/Server
```

### 5. Exécuter le client

Pour exécuter le client, utilisez la classe `Client.java`. Vous pouvez le faire à partir de l'IDE ou de la ligne de commande :

```sh
java Server/src/Client
```

## Description des classes Java

### Client.java

Cette classe gère la logique du client qui se connecte au serveur. Voici comment cela fonctionne concrètement :

1. Le client demande à l'utilisateur d'entrer son nom, l'adresse IP du serveur et le port du serveur.
2. Il initialise une connexion avec le serveur en utilisant les informations fournies.
3. Le client envoie un message de connexion au serveur et attend une réponse.
4. Une fois connecté, le client peut envoyer des messages au serveur, qui seront relayés aux autres clients connectés.
5. Le client écoute également les messages entrants du serveur et les affiche.

### Server.java

Cette classe gère la logique du serveur. Voici comment cela fonctionne concrètement :

1. Le serveur initialise un socket UDP et écoute les connexions entrantes sur un port spécifié.
2. Lorsqu'un client se connecte, le serveur enregistre les informations du client (adresse, port, nom).
3. Le serveur écoute les messages des clients et les relaye aux autres clients connectés.
4. Le serveur peut également envoyer des messages spécifiques à un client.
5. Lors de la fermeture du serveur, il envoie un message de déconnexion à tous les clients connectés.

### UDPIO.java

Cette classe gère les entrées/sorties UDP. Voici comment cela fonctionne concrètement :

1. La classe initialise un socket UDP pour envoyer et recevoir des paquets de données.
2. Elle fournit des méthodes pour envoyer des données à une adresse et un port spécifiés.
3. Elle fournit des méthodes pour recevoir des données d'un client.
4. Elle retourne les flux de données d'entrée et de sortie pour faciliter la lecture et l'écriture des données.

## Conclusion

Ce guide vous a expliqué comment configurer et exécuter le serveur Java. Si vous avez des questions ou des problèmes, n'hésitez pas à consulter la documentation ou à ouvrir une issue sur le dépôt GitHub.