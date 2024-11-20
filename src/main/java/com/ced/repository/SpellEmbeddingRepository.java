package com.ced.repository;

import com.ced.model.SpellEmbedding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpellEmbeddingRepository extends MongoRepository<SpellEmbedding, String> {
}