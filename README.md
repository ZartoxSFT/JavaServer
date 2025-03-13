# Mode d'emploi pour le projet JavaServer

## Introduction

Ce projet consiste à créer un serveur en Java. Il contient plusieurs fichiers et répertoires qui sont nécessaires pour le bon fonctionnement du serveur. Ce guide vous expliquera comment configurer et exécuter ce serveur, ainsi que fournir une explication des méthodes principales et du fonctionnement des classes.

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

## Description des classes et méthodes principales

### Client.java

#### Fonctionnement

La classe `Client` gère la logique du client qui se connecte au serveur. Elle permet à l'utilisateur de se connecter à un serveur en spécifiant son nom, l'adresse IP du serveur et le port. Une fois connecté, le client peut envoyer et recevoir des messages du serveur.

#### Méthodes principales :

- `Client(String nom, String serverIP, int serverPort)`: Constructeur qui initialise le client avec un nom, une adresse IP de serveur et un port.
- `void run(Scanner scanner)`: Méthode qui lance le client et communique avec le serveur. Elle envoie le nom du client au serveur et écoute les messages entrants.

### Server.java

#### Fonctionnement

La classe `Server` gère la logique du serveur. Elle initialise un socket UDP, écoute les connexions entrantes des clients, et relaie les messages entre les clients connectés. Le serveur peut également envoyer des messages spécifiques à un client.

#### Méthodes principales :

- `Server()`: Constructeur de la classe Server.
- `void run()`: Méthode qui exécute le serveur Java. Elle initialise le socket UDP, écoute les connexions entrantes, et relaie les messages aux clients connectés.
- `void relayMessageToClients(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort)`: Méthode qui relaie un message à tous les clients connectés.
- `ClientInfo getUser(InetAddress address, int port)`: Méthode qui retourne un client en fonction de son adresse et de son port.

### UDPIO.java

#### Fonctionnement

La classe `UDPIO` gère les entrées/sorties UDP. Elle fournit des méthodes pour envoyer et recevoir des paquets de données UDP, et facilite la lecture et l'écriture des données via des flux de données.

#### Méthodes principales :

- `UDPIO()`: Constructeur qui initialise le socket UDP.
- `void sendData(InetAddress address, int port)`: Méthode qui envoie des données à une adresse et un port spécifiés.
- `void receiveData()`: Méthode qui reçoit des données d'un client.
- `DataInputStream getInput()`: Méthode qui retourne le flux de sortie de données.
- `DataOutputStream getOutput()`: Méthode qui retourne le flux d'entrée de données.
- `DatagramSocket getSocket()`: Méthode qui retourne le socket de la classe.
- `DatagramPacket getReceivePacket()`: Méthode qui retourne le paquet reçu par le socket.

## Conclusion

Ce guide vous a expliqué comment configurer et exécuter le serveur Java, ainsi que le fonctionnement des différentes classes et leurs méthodes principales. Si vous avez des questions ou des problèmes, n'hésitez pas à consulter la documentation ou à ouvrir une issue sur le dépôt GitHub.
