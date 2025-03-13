# Mode d'emploi pour le projet JavaServer

## Introduction

Ce projet consiste à créer un serveur en Java en s'inspirant des serveurs Minecraft. Il contient plusieurs fichiers et répertoires qui sont nécessaires pour le bon fonctionnement du serveur. Ce guide vous expliquera comment configurer et exécuter ce serveur, ainsi que fournir une explication des méthodes principales et du fonctionnement des classes.

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