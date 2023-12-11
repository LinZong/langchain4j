package dev.langchain4j.memory;

public interface Memory {

    /**
     * @return The ID of the {@link Memory}.
     */
    Object id();

    /**
     * Clears current memory.
     */
    void clear();
}