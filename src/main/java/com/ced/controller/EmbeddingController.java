package com.ced.controller;


import com.ced.service.EmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/generate/spells")
    public ResponseEntity<String> generateSpellEmbeddings() {
        embeddingService.generateAndSaveSpellEmbeddings();
        return ResponseEntity.ok("Embeddings das magias gerados com sucesso.");
    }

    @PostMapping("/generate/spell/{spellIndex}")
    public ResponseEntity<String> generateSpellEmbedding(@PathVariable String spellIndex) {
        try {
            embeddingService.generateAndSaveSpellEmbedding(spellIndex);
            return ResponseEntity.ok("Embedding da magia '" + spellIndex + "' gerado com sucesso.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
