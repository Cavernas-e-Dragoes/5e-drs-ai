package com.ced.controller;

import com.ced.model.Spell;
import com.ced.model.utils.APIReference;
import com.ced.service.DataLoader;
import com.ced.service.OpenAIService;
import com.ced.service.EmbeddingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService openAIService;
    private final EmbeddingService embeddingService;
    private final DataLoader dataLoader;

    public ChatController(OpenAIService openAIService, EmbeddingService embeddingService, DataLoader dataLoader) {
        this.openAIService = openAIService;
        this.embeddingService = embeddingService;
        this.dataLoader = dataLoader;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody String userQuestion) {
        try {
            List<Double> questionEmbedding = openAIService.getEmbedding(userQuestion);

            int topN = 5;
            List<Spell> relevantSpells = findRelevantSpells(questionEmbedding, topN);

            String prompt = constructPrompt(relevantSpells, userQuestion);

            String response = openAIService.getChatCompletion(prompt);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar sua solicitação.");
        }
    }

    private List<Spell> findRelevantSpells(List<Double> questionEmbedding, int topN) {
        Map<String, List<Double>> allSpellEmbeddings = embeddingService.getAllSpellEmbeddings();
        List<Spell> allSpells = dataLoader.getAllSpells();

        List<Map.Entry<Spell, Double>> similarities = new ArrayList<>();

        for (Spell spell : allSpells) {
            List<Double> spellEmbedding = allSpellEmbeddings.get(spell.index());
            if (spellEmbedding != null) {
                double similarity = cosineSimilarity(questionEmbedding, spellEmbedding);
                similarities.add(new AbstractMap.SimpleEntry<>(spell, similarity));
            }
        }

        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return similarities.stream()
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String constructPrompt(List<Spell> spells, String userQuestion) {
        StringBuilder contextBuilder = new StringBuilder();
        for (Spell spell : spells) {
            contextBuilder.append("Nome da Magia: ").append(spell.name()).append("\n");
//            contextBuilder.append("Nível: ").append(spell.level()).append("\n");
            contextBuilder.append("Descrição: ").append(String.join(" ", spell.desc())).append("\n\n");
//            contextBuilder.append("Alcance: ").append(String.join(" ", spell.range())).append("\n\n");
//            contextBuilder.append("Componentes: ").append(String.join(" ", spell.components())).append("\n\n");
//            contextBuilder.append("Materiais: ").append(String.join(" ", spell.material())).append("\n\n");
//            contextBuilder.append("Classes: ").append(
//                    spell.classes().stream()
//                            .map(APIReference::name)
//                            .collect(Collectors.joining(", "))
//            ).append("\n\n");
        }

        String prompt = "Contexto:\n" + contextBuilder.toString() +
                "Pergunta do usuário: " + userQuestion + "\n" +
                "Responda de forma clara e concisa em português, usando as informações do contexto. " +
                "Se a pergunta for relacionado a magia, use apenas o contexto.";

        return prompt;
    }

}