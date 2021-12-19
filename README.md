# Les Boulangers - Authentication Server

Dans le cadre du projet du cours AMT (Application multi-tiers) à la HEIG-VD, nous sommes chargés de créer un site web de e-commerce décomposé en microservices. Ce repository gère le service d'authentification.

Le repo central, ainsi que le wiki sont disponibles via ce lien : https://github.com/LeonardBesseau/AMT-Project 

## Description

Ce microservice d'authentification est également basé sur Quarkus.

## Pré-requis

- [Postgresql](https://www.postgresql.org/download/) (v13 ou supérieure)
- [Java 11](https://adoptopenjdk.net/installation.html)
- [Docker](https://docs.docker.com/get-docker/) et [docker-compose](https://docs.docker.com/compose/install/) (optionnels)

## Déploiement

- Télécharger la release et l'extraire. Ajouter les données de connexion à la base de données dans le fichier `config/application.properties` (il peut être nécessaire de le créer).

- Générer la paire de clés publique/privée avec les commandes `openssl` ci-dessous :

  ```
  openssl ecparam -genkey -name prime256v1 -noout -out ec256-key-pair.pem
  openssl ec -in ec256-key-pair.pem -pubout -out publicKey.pem
  openssl pkcs8 -topk8 -nocrypt -inform pem -in ec256-key-pair.pem -outform pem -out privateKey.pem
  ```

  La clé nommée `ec256-key-pair.pem` peut être supprimée ou cachée.

- Lancer le serveur avec `java -jar target/quarkus-auth/quarkus-run.jar` 


## Installation

Les étapes ci-dessous permettent de mettre en place l'environnement de développement en local afin de travailler sur le projet :

1. Cloner le repository. 

2. Mettre en place la base de données

   - Standalone

   Si vous disposez déjà de postgres, vous pouvez créer une nouvelle base de données ou en utiliser une existante. La base de données est configurée automatiquement au lancement du projet grâce à Liquibase.

   Une fois la configuration terminée, vous pouvez mettre les informations de connexions dans le fichier `config/application.properties`. (L'utilisateur à fournir doit avoir des droits de lecture et d'écriture)

   - Docker
     1. `docker-compose up` dans le dossier `docker`
     2. Se connecter à la base de données dans l'IDE

3. Générer la paire de clés publique/privée avec les commandes `openssl` ci-dessous :

   ```
   openssl ecparam -genkey -name prime256v1 -noout -out ec256-key-pair.pem
   openssl ec -in ec256-key-pair.pem -pubout -out publicKey.pem
   openssl pkcs8 -topk8 -nocrypt -inform pem -in ec256-key-pair.pem -outform pem -out privateKey.pem
   ```

   Cela va permettre signer le JWT avec l'algorithme ES256.

   La clé nommée `ec256-key-pair.pem` peut être supprimée ou cachée.

4. Lancer l'application en mode *dev* avec `mvn compile quarkus:dev`.


## Contribution

Les Pull Requests sont les bienvenues. Pour des changements majeurs, ouvrez s'il vous plaît une issue pour discuter de ce que vous souhaitez changer.
Soyez sûrs de mettre à jour les tests si nécessaire.

## Licence

[MIT](https://choosealicense.com/licenses/mit/)

## Auteurs

- Léonard Besseau
- Alexandra Cerottini
- Miguel Do Vale Lopes
- Fiona Gamboni
- Nicolas Ogi
