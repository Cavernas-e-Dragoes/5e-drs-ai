package com.ced.service;

import com.ced.cache.GenericCache;
import com.ced.model.Spell;
import com.ced.repository.SpellsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    private final GenericCache<Spell> spellCache;

    public DataLoader(SpellsRepository spellsRepository) {
        this.spellCache = new GenericCache<>(spellsRepository, LOGGER, "magia");
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        initializeData();
    }

    private void initializeData() {
        spellCache.loadData();
    }

    public List<Spell> getAllSpells() {
        return spellCache.getAll();
    }

    public Optional<Spell> getSpellByIndex(String index) {
        return spellCache.getByIndex(index);
    }

}