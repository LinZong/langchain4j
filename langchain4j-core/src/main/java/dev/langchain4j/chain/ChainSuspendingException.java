package dev.langchain4j.chain;

import java.io.Serializable;

/**
 * Exception for propagating suspending signal through chain's hierarchy.
 */
public class ChainSuspendingException extends RuntimeException {

    private final Class<? extends Chain<?, ?>> source;

    private final Serializable state;

    private final Object payload;

    public ChainSuspendingException(Class<? extends SuspendableChain<?, ?, ?>> source, Serializable state) {
        this(source, state, null, null);
    }

    public ChainSuspendingException(Class<? extends SuspendableChain<?, ?, ?>> source, Serializable state, Object payload) {
        this(source, state, payload, null);
    }

    public ChainSuspendingException(Class<? extends SuspendableChain<?, ?, ?>> source,
                                    Serializable state,
                                    Object payload,
                                    ChainSuspendingException cause) {
        super(cause);
        this.source = source;
        this.state = state;
        this.payload = payload;
    }

    public Class<? extends Chain<?, ?>> getSource() {
        return source;
    }

    public Serializable getState() {
        return state;
    }

    public Object getPayload() {
        return payload;
    }

    public <T extends Serializable> T getState(Class<T> typeOfState) {
        return typeOfState.cast(this.state);
    }

    public <T> T getPayload(Class<T> typeOfPayload) {
        return typeOfPayload.cast(this.payload);
    }
}