package dev.langchain4j.data.message;

import java.util.List;
import java.util.Objects;

public abstract class MultiModalChatMessage extends ChatMessage {

    private final List<String> imageUrls;

    public MultiModalChatMessage(String text, List<String> imageUrls) {
        super(text);
        this.imageUrls = imageUrls;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiModalChatMessage)) return false;
        MultiModalChatMessage that = (MultiModalChatMessage) o;
        return Objects.equals(imageUrls, that.imageUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrls);
    }
}
