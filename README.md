# Spotify AI POC

Ce projet est une preuve de concept (POC) d'une application utilisant l'API de Spotify et des fonctionnalités d'intelligence artificielle.

## Prérequis
- Java 17 ou supérieur
- Maven
- Docker et Docker Compose

## Démarrage rapide

### 1. Lancer la base de données PostgreSQL

```bash
docker-compose up -d
```

Cela démarre un conteneur PostgreSQL accessible sur le port 5432.

### 2. Configurer l'application

Modifiez le fichier `src/main/resources/application.yml` pour adapter la configuration à vos besoins (identifiants de la base, clés API, etc).

### 3. Lancer l'application Spring Boot

```bash
./mvnw spring-boot:run
```

L'application sera accessible sur le port configuré (par défaut 8080).

## Structure du projet
- `src/main/java` : code source principal (Spring Boot)
- `src/main/resources` : ressources (config, templates, statiques)
- `src/test/java` : tests unitaires
- `docker-compose.yml` : configuration Docker pour la base de données

## Commandes utiles
- Lancer les tests :
  ```bash
  ./mvnw test
  ```
- Arrêter la base de données :
  ```bash
  docker-compose down
  ```

## Licence
Ce projet est fourni à titre d'exemple et sans garantie.

