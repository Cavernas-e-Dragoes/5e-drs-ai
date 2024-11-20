package com.ced.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OpenAIService {

    private final OpenAiService openAiService;

    public OpenAIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String getChatCompletion(String userMessage) {
        ChatMessage message = new ChatMessage("user", userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(Arrays.asList(message))
                .maxTokens(300)
                .temperature(0.5)
                .build();

        ChatCompletionResult chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

        return chatCompletionResult.getChoices().get(0).getMessage().getContent();
    }

    public List<Double> getEmbedding(String text) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(List.of(text))
                .build();

        EmbeddingResult embeddingResult = openAiService.createEmbeddings(embeddingRequest);

        return embeddingResult.getData().get(0).getEmbedding();
    }
}
