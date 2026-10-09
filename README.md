# LLM Comparison — Spring AI

A Spring Boot application for experimenting with and comparing responses from two LLM providers:

- **Google Gemini** through the Gemini API
- **Ollama** running a model locally on your machine

The project uses **Spring AI's `ChatClient` API** so each provider can be called through a separate, named client.

## Tech Stack

- Java (use the JDK version configured for this project)
- Spring Boot
- Spring AI 2.0.1
- Spring Web
- Google GenAI / Gemini API
- Ollama
- Lombok
- Maven

## Features

- Generate responses using Google Gemini.
- Generate responses using a local Ollama model.
- Keep the two providers separate using named `ChatClient` beans.
- Configure the Gemini API key through an environment variable rather than committing it to source control.
- Call the model endpoints from a browser, Postman, or another HTTP client.

## How It Works

```text
Client
  ├── GET /api/google/{message}
  │       └── googleGenAiChatClient → Google Gemini API
  │
  └── GET /api/ollama/{message}
          └── ollamaChatClient → Ollama → local model
```

## Prerequisites

1. A JDK compatible with the Java version configured in `pom.xml`.
2. Maven, or the Maven Wrapper if it is included in the project.
3. [Ollama](https://ollama.com/) installed and running.
4. A Gemini API key from [Google AI Studio](https://aistudio.google.com/apikey) if you want to use the Gemini endpoint.

## 1. Download the Ollama Model

This project is configured to use `qwen3:1.7b` locally. Download it with:

```bash
ollama pull qwen3:1.7b
```

You can test the model in the terminal:

```bash
ollama run qwen3:1.7b
```

Make sure Ollama is running and reachable at its default local address, `http://localhost:11434`.

## 2. Configure the Gemini API Key

The application reads the key from the `GEMINI_API_KEY` environment variable. **Do not hard-code your real API key in `application.yml` or commit it to GitHub.**

### Windows PowerShell

For the current PowerShell session:

```powershell
$env:GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

Start the application from that same terminal, or configure the variable in IntelliJ as described below.

### IntelliJ IDEA

1. Open **Run → Edit Configurations…**.
2. Select your Spring Boot application run configuration.
3. In **Environment variables**, click the edit icon.
4. Add a variable with name `GEMINI_API_KEY` and value your actual API key.
5. Save the configuration and restart the application.

Never share screenshots or logs that expose the full API key. If a key is accidentally exposed, revoke it and create a replacement.

## 3. Application Configuration

In `src/main/resources/application.yml`, use the following configuration. The Gemini model ID must be available to the Google project associated with your key; change it if Google's model catalog or your account access changes.

```yaml
spring:
  application:
    name: llm.comparison

  ai:
    google:
      genai:
        api-key: ${GEMINI_API_KEY}
        chat:
          options:
            model: gemini-3.8-flash

    ollama:
      chat:
        options:
          model: qwen3:1.7b
```

The key property is `spring.ai.google.genai.api-key`. Keeping `${GEMINI_API_KEY}` in the YAML allows Spring Boot to resolve the secret from the environment at startup.

## 4. Configure Separate ChatClient Beans

Create a configuration class such as `ChatClientConfig.java` to bind each model to its own client:

```java
package com.av.llm.comparison.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient googleGenAiChatClient(GoogleGenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
```

Because the application defines two `ChatClient` beans, inject the required client with `@Qualifier` in each controller. The qualifier must match the bean method name.

Example for Ollama:

```java
public OllamaController(
        @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
    this.ollamaChatClient = ollamaChatClient;
}
```

Use `@Qualifier("googleGenAiChatClient")` in the Gemini controller.

## 5. Run the Application

From the project root, run one of the following:

```bash
./mvnw spring-boot:run
```

On Windows, if the Maven Wrapper is present:

```powershell
.\mvnw.cmd spring-boot:run
```

Or, if Maven is installed and the project does not include a wrapper:

```bash
mvn spring-boot:run
```

The examples below assume the application runs on port `8080`.

## 6. API Endpoints

### Google Gemini

```http
GET http://localhost:8080/api/google/Explain%20Spring%20AI
```

This endpoint sends the supplied message to the Gemini-backed `ChatClient` and returns its response.

### Ollama

```http
GET http://localhost:8080/api/ollama/Explain%20Spring%20AI
```

This endpoint sends the supplied message to the locally running Ollama model and returns its response.

**Note:** The current controller style places the message in the URL path, so encode spaces and special characters. For longer prompts or production APIs, consider changing these endpoints to `POST` and accepting the prompt in a request body.

## Troubleshooting

| Error | What to check |
|---|---|
| `API_KEY_INVALID` | Check that `GEMINI_API_KEY` contains a valid Gemini API key, that IntelliJ has the environment variable, and that the key has not been revoked or restricted. |
| `404` model unavailable | Select a model supported by the Gemini API and accessible to the project associated with your key. Model availability can change. |
| `429` quota exceeded | Check the project's current rate limits and usage in the Google AI Studio / Gemini API dashboard. Wait for the specified retry window or review the applicable quota and billing options. |
| `503` high demand | This is generally a temporary server-capacity issue. Wait and retry later; avoid repeatedly refreshing the endpoint. |
| `No qualifying bean` / two `ChatClient` beans | Add `@Qualifier("googleGenAiChatClient")` or `@Qualifier("ollamaChatClient")` where the client is injected. |
| Ollama connection refused | Ensure Ollama is running and that the configured model has been pulled with `ollama pull qwen3:1.7b`. |

## Security Notes

- Do not commit API keys, `.env` files containing secrets, or IntelliJ run configurations that expose secrets.
- Keep `GEMINI_API_KEY` in an environment variable or a secret manager.
- Remember that Gemini API requests use the remote Google service, while Ollama runs the selected model locally.

## Useful Links

- [Spring AI documentation](https://docs.spring.io/spring-ai/reference/)
- [Spring AI Google GenAI chat documentation](https://docs.spring.io/spring-ai/reference/api/chat/google-genai-chat.html)
- [Spring AI Ollama chat documentation](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Google AI Studio API keys](https://aistudio.google.com/apikey)
- [Gemini API models](https://ai.google.dev/gemini-api/docs/models)
- [Ollama](https://ollama.com/)

---

Built as a learning project to explore multiple LLM providers with Spring AI.
