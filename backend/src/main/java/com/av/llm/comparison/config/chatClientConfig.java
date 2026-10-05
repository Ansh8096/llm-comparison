package com.av.llm.comparison.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class chatClientConfig {

    @Bean
    public ChatClient googleGenAiChatClient(GoogleGenAiChatModel googleGenAiChatModel){
        return ChatClient.builder(googleGenAiChatModel).build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel){
        return ChatClient.builder(ollamaChatModel).build();
    }
}
