package dev.langchain4j.model.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.MultiModalChatMessage;
import dev.langchain4j.model.output.Response;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Multi modal chat language model, a.k.a model can accept both image and text content.
 */
public interface MultiModalChatLanguageModel extends ChatLanguageModel {


    /**
     * Generates a response from the multi-modal model based on a sequence of multi-modal messages.
     * Typically, the sequence contains messages in the following order:
     * System (optional) - User - AI - User - AI - User ...
     *
     * @param messages An array of messages.
     * @return The response generated by the model.
     */
    Response<AiMessage> generateMultiModal(List<MultiModalChatMessage> messages);

    /**
     * Generates a response from the multi-modal model based on a sequence of multi-modal messages.
     * Typically, the sequence contains messages in the following order:
     * System (optional) - User - AI - User - AI - User ...
     *
     * @param messages An array of multi-modal messages.
     * @return The response generated by the model.
     */
    default Response<AiMessage> generateMultiModal(MultiModalChatMessage... messages) {
        return generateMultiModal(asList(messages));
    }
}
