package com.ced.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "spell_embeddings")
public class SpellEmbedding {

    @Id
    private String index;

    private List<Double> embedding;

    public SpellEmbedding() {
    }

    public SpellEmbedding(String index, List<Double> embedding) {
        this.index = index;
        this.embedding = embedding;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }
}
