package dev.langchain4j.data.message;

import java.util.List;
import java.util.Objects;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;

public class MultiModalUserChatMessage extends MultiModalChatMessage {
    private final String name;

    public MultiModalUserChatMessage(String text, List<String> imageUrls) {
        this(null, text, imageUrls);
    }

    public MultiModalUserChatMessage(String name, String text, List<String> imageUrls) {
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
        if (!(o instanceof MultiModalUserChatMessage)) return false;
        if (!super.equals(o)) return false;
        MultiModalUserChatMessage that = (MultiModalUserChatMessage) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }


    public static MultiModalUserChatMessage from(String text, List<String> imageUrls) {
        return new MultiModalUserChatMessage(ensureNotBlank(text, "text"), ensureNotEmpty(imageUrls, "imageUrls"));
    }

    public static MultiModalUserChatMessage from(String name, String text, List<String> imageUrls) {
        return new MultiModalUserChatMessage(name, ensureNotBlank(text, "text"), ensureNotEmpty(imageUrls, "imageUrls"));
    }
}
