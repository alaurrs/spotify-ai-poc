# Spotify AI POC

Ce projet est une preuve de concept (POC) d'une application utilisant l'API de Spotify et des fonctionnalités d'intelligence artificielle (Spring AI + OpenAI).

## Prérequis
- Java 17 ou supérieur
- Maven
- Docker et Docker Compose
- Une clé API OpenAI valide (obligatoire pour l'IA)

## Démarrage rapide

### 1. Préparer la clé OpenAI
Avant de lancer l'application (notamment via Docker Compose), vous devez définir la variable d'environnement `OPENAI_API_KEY` avec votre clé OpenAI :

```bash
export OPENAI_API_KEY=sk-...votre_cle...
```

> ⚠️ L'application ne fonctionnera pas sans cette clé.

### 2. Lancer la base de données PostgreSQL

```bash
docker-compose up -d
```

Cela démarre un conteneur PostgreSQL accessible sur le port 5432.

### 3. Initialiser la base de données

Vous pouvez utiliser le script SQL fourni dans `sql/schema.sql` pour créer les tables nécessaires :

```bash
psql -h localhost -U spotifyuser -d spotifydb -f sql/schema.sql
```

### 4. Configurer l'application

Modifiez le fichier `src/main/resources/application.yml` si besoin (identifiants de la base, modèles OpenAI, etc). Par défaut, la clé OpenAI est lue depuis la variable d'environnement `OPENAI_API_KEY`.

### 5. Lancer l'application Spring Boot

```bash
./mvnw spring-boot:run
```

L'application sera accessible sur le port configuré (par défaut 8080).

## Structure du projet
- `src/main/java` : code source principal (Spring Boot)
- `src/main/resources` : ressources (config, templates, statiques)
- `sql/` : scripts SQL pour la base de données
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

## Notes
- Le schéma SQL crée les tables : users, artists, albums, tracks, listening_history.
- La configuration OpenAI se fait via la variable d'environnement `OPENAI_API_KEY`.

## Licence
Ce projet est fourni à titre d'exemple et sans garantie.
