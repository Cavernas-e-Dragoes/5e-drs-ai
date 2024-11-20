package com.ced.service;

import com.ced.model.Spell;
import com.ced.model.SpellEmbedding;
import com.ced.model.utils.APIReference;
import com.ced.repository.SpellEmbeddingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private final SpellEmbeddingRepository spellEmbeddingRepository;
    private final OpenAIService openAIService;
    private final DataLoader dataLoader;
    private Map<String, List<Double>> spellEmbeddings;

    public EmbeddingService(SpellEmbeddingRepository spellEmbeddingRepository,
                            OpenAIService openAIService,
                            DataLoader dataLoader) {
        this.spellEmbeddingRepository = spellEmbeddingRepository;
        this.openAIService = openAIService;
        this.dataLoader = dataLoader;
    }

    public void generateAndSaveSpellEmbeddings() {
        List<Spell> spells = dataLoader.getAllSpells();

        for (Spell spell : spells) {
            if (spellEmbeddingRepository.existsById(spell.index())) {
                continue;
            }

            String textForEmbedding = createTextForEmbedding(spell);
            List<Double> embedding = openAIService.getEmbedding(textForEmbedding);

            SpellEmbedding spellEmbedding = new SpellEmbedding(spell.index(), embedding);

            spellEmbeddingRepository.save(spellEmbedding);
        }
    }

    public void generateAndSaveSpellEmbedding(String spellIndex) {
        if (spellEmbeddingRepository.existsById(spellIndex)) {
            return;
        }

        Optional<Spell> optionalSpell = dataLoader.getSpellByIndex(spellIndex);

        if (optionalSpell.isPresent()) {
            Spell spell = optionalSpell.get();

            String textForEmbedding = createTextForEmbedding(spell);
            List<Double> embedding = openAIService.getEmbedding(textForEmbedding);

            SpellEmbedding spellEmbedding = new SpellEmbedding(spell.index(), embedding);

            spellEmbeddingRepository.save(spellEmbedding);
        } else {
            throw new NoSuchElementException("Magia não encontrada: " + spellIndex);
        }
    }

    private String createTextForEmbedding(Spell spell) {
        StringBuilder sb = new StringBuilder();

        sb.append("Nome: ").append(spell.name()).append("\n");
        sb.append("Nível: ").append(spell.level()).append("\n");
        sb.append("Escola: ").append(spell.school() != null ? spell.school().name() : "Desconhecida").append("\n");
        sb.append("Componentes: ").append(String.join(", ", spell.components())).append("\n");
        if (spell.material() != null && !spell.material().isEmpty()) {
            sb.append("Materiais: ").append(spell.material()).append("\n");
        }
        sb.append("Tempo de Conjuração: ").append(spell.castingTime()).append("\n");
        sb.append("Alcance: ").append(spell.range()).append("\n");
        sb.append("Duração: ").append(spell.duration()).append("\n");
        sb.append("Concentração: ").append(spell.concentration() ? "Sim" : "Não").append("\n");
        sb.append("Ritual: ").append(spell.ritual() ? "Sim" : "Não").append("\n");
        if (spell.damage() != null && spell.damage().getDamageTypeList() != null && !spell.damage().getDamageTypeList().isEmpty()) {
            sb.append("Tipo de Dano: ").append(
                    spell.damage().getDamageTypeList().stream()
                            .map(APIReference::name)
                            .collect(Collectors.joining(", "))
            ).append("\n");
        }

        if (spell.areaOfEffect() != null) {
            sb.append("Área de Efeito: ").append(spell.areaOfEffect().type())
                    .append(" de ").append(spell.areaOfEffect().size()).append(" pés\n");
        }
        sb.append("Classes: ").append(spell.classes().stream()
                .map(APIReference::name)
                .collect(Collectors.joining(", "))).append("\n");
        sb.append("Descrição: ").append(String.join(" ", spell.desc())).append("\n");

        return sb.toString();
    }

    @PostConstruct
    public void loadEmbeddings() {
        List<SpellEmbedding> embeddings = spellEmbeddingRepository.findAll();
        spellEmbeddings = embeddings.stream()
                .collect(Collectors.toMap(
                        SpellEmbedding::getIndex,
                        SpellEmbedding::getEmbedding
                ));
    }

    public List<Double> getSpellEmbedding(String index) {
        return spellEmbeddings.get(index);
    }

    public Map<String, List<Double>> getAllSpellEmbeddings() {
        return spellEmbeddings;
    }

}
