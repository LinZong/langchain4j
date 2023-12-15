package dev.langchain4j.chain.lifecycle;

import dev.langchain4j.chain.Chain;
import dev.langchain4j.chain.lifecycle.listener.ChainLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

/**
 * Chain for taking aspect on execution lifecycle of peer chain.
 *
 * @param <Input>  Generic type of chain's input.
 * @param <Output> Generic type of chain's output.
 * @see ChainLifecycleListener for implementation of lifecycle listener.
 */
public class LifecycleAspectChain<Input, Output> implements Chain<Input, Output> {
    private static final Logger log = LoggerFactory.getLogger(LifecycleAspectChain.class);

    private final Chain<Input, Output> peer;

    private final List<ChainLifecycleListener<Input, Output>> listeners = new ArrayList<>();

    public LifecycleAspectChain(Chain<Input, Output> peer, List<? extends ChainLifecycleListener<Input, Output>> listeners) {
        this.peer = ensureNotNull(peer, "peer");
        this.listeners.addAll(ensureNotNull(listeners, "listeners"));
    }

    @Override
    public Output execute(Input input) {
        try {
            safeExecuteListeners(listener -> listener.beforeExecute(input));
            Output output = peer.execute(input);
            safeExecuteListeners(listener -> listener.afterExecute(input, output));
            return output;
        } catch (Throwable t) {
            safeExecuteListeners(listener -> listener.executeError(input, t));
            throw t;
        }
    }

    protected void safeExecuteListeners(Consumer<ChainLifecycleListener<Input, Output>> executor) {
        for (ChainLifecycleListener<Input, Output> listener : listeners) {
            try {
                executor.accept(listener);
            } catch (Throwable t) {
                log.error("Failed to execute listener: {}", listener, t);
            }
        }
    }
}