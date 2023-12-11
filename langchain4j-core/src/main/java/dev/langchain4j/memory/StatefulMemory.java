package dev.langchain4j.memory;

import java.io.Serializable;

public interface StatefulMemory<T extends Serializable> extends Memory {

    /**
     * Get state of attached chain component.
     *
     * @return state object or null if there is no state persisted inside memory.
     */
    T getState();


    /**
     * Save state of chain component into memory.
     *
     * @param state state object.
     */
    void setState(T state);
}
