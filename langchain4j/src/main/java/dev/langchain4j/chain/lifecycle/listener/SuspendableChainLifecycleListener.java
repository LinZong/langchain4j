package dev.langchain4j.chain.lifecycle.listener;

import dev.langchain4j.chain.ChainSuspendingException;

import java.io.Serializable;

public interface SuspendableChainLifecycleListener<Input, Output, State extends Serializable> extends ChainLifecycleListener<Input, Output> {

    /**
     * After suspending signal raised from listening chain.
     *
     * @param ex suspend signal exception.
     */
    void afterSuspend(ChainSuspendingException ex);

    /**
     * Before resume execution for listening chain.
     *
     * @param state state object generated from previous suspending.
     */
    void beforeResume(State state);

    /**
     * After resume execution for listening chain.
     *
     * @param state state object generated from previous suspending.
     */
    void afterResume(State state);

    /**
     * Exception occurred while resuming listening chain.
     *
     * @param state state object generated from previous suspending.
     * @param t     occurred exception instance.
     */
    void resumeError(State state, Throwable t);
}
