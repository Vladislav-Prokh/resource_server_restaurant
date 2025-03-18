package delivery.app.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final String conversationId;
    private final OpenAiChatOptions options;

    public AiService(ChatClient.Builder builder) {
        OpenAiApi.ChatCompletionRequest.ResponseFormat format =
                new OpenAiApi.ChatCompletionRequest.ResponseFormat(OpenAiApi.ChatCompletionRequest.ResponseFormat.Type.JSON_OBJECT);

        options = OpenAiChatOptions.builder()
                .withTemperature(0.7f)
                .withResponseFormat(format)
                .build();

        this.chatMemory = new InMemoryChatMemory();
        this.conversationId = UUID.randomUUID().toString();

        this.chatClient = builder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory, conversationId, 10)
                )
                .build();
    }

    public String getResponse() {
        List<Message> history = new ArrayList<>(chatMemory.get(conversationId, 10));

        Message systemMessage = new SystemMessage("must respond in JSON form, key - `fact`, don't repeat");
        if (history.isEmpty()) {
            chatMemory.add(conversationId, systemMessage);
            history.add(systemMessage);
        }

        Message userMessage = new UserMessage("give me interesting fact about beverage, cocktails or alcohol");
        chatMemory.add(conversationId, userMessage);
        history.add(userMessage);

        Prompt prompt = new Prompt(history, options);
        String response = chatClient.prompt(prompt).call().content();

        chatMemory.add(conversationId, new AssistantMessage(response));

        return response;
    }
}
