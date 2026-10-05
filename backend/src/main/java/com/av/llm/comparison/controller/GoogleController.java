package com.av.llm.comparison.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/google")
@CrossOrigin(origins = "http://localhost:5173")
public class GoogleController {

    private final ChatClient googleChatClient;

    public GoogleController(
            @Qualifier("googleGenAiChatClient")
            ChatClient googleChatClient
    ){
        this.googleChatClient = googleChatClient;
    }

    @GetMapping("/{message}")
    public ResponseEntity<?> getAnswer(@PathVariable String message){

        String response = googleChatClient.prompt(message).call().content();
        return ResponseEntity.ok(response);
    }
}
