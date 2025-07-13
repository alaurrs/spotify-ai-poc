package com.datainsights.spotify_ai_poc.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;
    private final VectorStore vectorStore;
    private final Map<String, String> tableDefinitions;


    public AIService(ChatClient.Builder chatClientBuilder, JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        this.chatClient = chatClientBuilder.build();
        this.jdbcTemplate = jdbcTemplate;

        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        List<Document> documents = List.of(
                new Document("Informations sur les artistes et chanteurs.", Map.of("tableName", "artists")),
                new Document("Informations sur les chansons et titres musicaux.", Map.of("tableName", "tracks")),
                new Document("Historique d'écoute des utilisateurs.", Map.of("tableName", "listening_history")),
                new Document("La table 'listening_history' stocke chaque écoute d'une chanson par un utilisateur avec une date précise. Utiliser cette table pour toute question relative aux statistiques d'écoute.", Map.of("tableName", "listening_history"))

        );
        simpleVectorStore.add(documents);
        this.vectorStore = simpleVectorStore;

        this.tableDefinitions = Map.of(
                "artists", "CREATE TABLE artists ( artist_id UUID PRIMARY KEY, name VARCHAR(100) NOT NULL );",
                "tracks", "CREATE TABLE tracks ( track_id UUID PRIMARY KEY, title VARCHAR(150) NOT NULL, artist_id UUID REFERENCES artists(artist_id) );",
                "listening_history", "CREATE TABLE listening_history ( listen_id BIGSERIAL PRIMARY KEY, user_id UUID REFERENCES users(user_id), track_id UUID REFERENCES tracks(track_id), listened_at TIMESTAMP WITH TIME ZONE );"
        );
    }

    private static final Map<String, List<String>> TABLE_KEYWORDS = Map.of(
        "artists", List.of("artiste", "chanteur", "groupe", "interprète"),
        "tracks", List.of("chanson", "titre", "morceau", "musique", "track"),
        "listening_history", List.of("écoute", "écouté", "écoutés", "écouter", "historique", "écoutées", "écoutée", "écoutant")
    );

    private String getRelevantSchema(String question) {
        String lowerQuestion = question.toLowerCase();
        // Recherche des tables les plus pertinentes selon les mots-clés
        List<String> relevantTables = TABLE_KEYWORDS.entrySet().stream()
            .filter(e -> e.getValue().stream().anyMatch(lowerQuestion::contains))
            .map(Map.Entry::getKey)
            .toList();
        // Si aucune table trouvée par mots-clés, fallback sur la recherche vectorielle
        if (relevantTables.isEmpty()) {
            SearchRequest request = SearchRequest.builder().query(question).topK(2).build();
            List<Document> similarDocuments = this.vectorStore.similaritySearch(request);
            relevantTables = similarDocuments.stream()
                .map(doc -> doc.getMetadata().get("tableName").toString())
                .distinct()
                .toList();
        }
        return relevantTables.stream()
            .map(tableDefinitions::get)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.joining("\n"));
    }

    public String getInsight(String question, String userId) {
        String relevantSchema = getRelevantSchema(question);

        String userMessageForSql = """
            Ta mission est de traduire la question suivante en UNE SEULE requête SQL PostgreSQL exécutable.
            
            Règles impératives :
            - Le schéma de base de données à utiliser est le suivant : %s
            - La requête doit concerner l'utilisateur avec l'UUID : '%s'
            - Si la question concerne l'historique d'écoute, tu dois OBLIGATOIREMENT utiliser la table 'listening_history' (et faire les jointures nécessaires pour obtenir les titres, artistes, etc.).
            - Ta réponse DOIT contenir uniquement la requête SQL.
            - Ne fournis AUCUNE explication, AUCUN commentaire, et AUCUN texte ou formatage Markdown (comme ```) avant ou après la requête.
            
            Question : "%s"
            """.formatted(relevantSchema, userId, question);

        String rawSqlResponse = chatClient.prompt().user(userMessageForSql).call().content();

        String cleanedSql = rawSqlResponse.trim()
                .replace("```sql", "")
                .replace("```", "")
                .trim();

        List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(cleanedSql);

        String userMessageForAnswer = "Formule une réponse amicale et directe à la question \"%s\" en te basant sur ces données : %s".formatted(question, queryResults);

        return chatClient.prompt().user(userMessageForAnswer).call().content();
    }
}
