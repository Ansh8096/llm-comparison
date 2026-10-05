package com.av.llm.comparison.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ollama")
@CrossOrigin(origins = "http://localhost:5173")
public class OllamaController {

    private final ChatClient ollamaChatClient;

    public OllamaController(
            @Qualifier("ollamaChatClient")
            ChatClient ollamaChatClient
    ){
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/{message}")
    public ResponseEntity<?> getAnswer(@PathVariable String message){
        String response = ollamaChatClient
                .prompt(message)
                .call()
                .content();
        return ResponseEntity.ok(response);
    }
}
