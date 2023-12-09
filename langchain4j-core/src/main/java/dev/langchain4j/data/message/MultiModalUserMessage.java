package dev.langchain4j.data.message;

import java.util.List;
import java.util.Objects;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;

public class MultiModalUserMessage extends MultiModalChatMessage {
    private final String name;

    public MultiModalUserMessage(String text, List<String> imageUrls) {
        this(null, text, imageUrls);
    }

    public MultiModalUserMessage(String name, String text, List<String> imageUrls) {
        super(text, imageUrls);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public ChatMessageType type() {
        return ChatMessageType.USER_MULTIMODAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiModalUserMessage)) return false;
        if (!super.equals(o)) return false;
        MultiModalUserMessage that = (MultiModalUserMessage) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }


    public static MultiModalUserMessage from(String text, List<String> imageUrls) {
        return new MultiModalUserMessage(ensureNotBlank(text, "text"), ensureNotEmpty(imageUrls, "imageUrls"));
    }

    public static MultiModalUserMessage from(String name, String text, List<String> imageUrls) {
        return new MultiModalUserMessage(name, ensureNotBlank(text, "text"), ensureNotEmpty(imageUrls, "imageUrls"));
    }
}
