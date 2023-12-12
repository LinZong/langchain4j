package dev.langchain4j.chain.lifecycle;

import dev.langchain4j.chain.Chain;
import dev.langchain4j.chain.lifecycle.listener.ChainLifecycleListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

public class LifecycleAspectChain<Input, Output> implements Chain<Input, Output> {

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
                // TODO log for exception.
            }
        }
    }
}
