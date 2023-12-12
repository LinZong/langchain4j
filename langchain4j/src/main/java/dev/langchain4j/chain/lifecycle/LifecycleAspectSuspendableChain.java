package dev.langchain4j.chain.lifecycle;

import dev.langchain4j.chain.ChainSuspendingException;
import dev.langchain4j.chain.SuspendableChain;
import dev.langchain4j.chain.lifecycle.listener.SuspendableChainLifecycleListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

public class LifecycleAspectSuspendableChain<Input, Output, State extends Serializable> extends SuspendableChain<Input, Output, State> {

    private final SuspendableChain<Input, Output, State> peer;

    private final List<SuspendableChainLifecycleListener<Input, Output, State>> listeners = new ArrayList<>();


    public LifecycleAspectSuspendableChain(SuspendableChain<Input, Output, State> peer,
                                           List<SuspendableChainLifecycleListener<Input, Output, State>> listeners) {
        this.peer = ensureNotNull(peer, "peer");
        this.listeners.addAll(ensureNotNull(listeners, "listeners"));
    }

    @Override
    public Output execute(Input input) {
        try {
            safeExecuteListener(listener -> listener.beforeExecute(input));
            Output output = peer.execute(input);
            safeExecuteListener(listener -> listener.afterExecute(input, output));
            return output;
        } catch (ChainSuspendingException suspend) {
            safeExecuteListener(listener -> listener.afterSuspend(suspend));
            throw suspend;
        } catch (Throwable t) {
            safeExecuteListener(listener -> listener.executeError(input, t));
            throw t;
        }
    }


    @Override
    public void resume(State state) {
        try {
            safeExecuteListener(listener -> listener.beforeResume(state));
            peer.resume(state);
            safeExecuteListener(listener -> listener.afterResume(state));
        } catch (Throwable t) {
            safeExecuteListener(listener -> listener.resumeError(state, t));
            throw t;
        }
    }

    @Override
    protected State onSuspend() {
        throw new UnsupportedOperationException("Not supported for LifecycleAspectSuspendableChain.");
    }

    @Override
    protected void onResume(State state) {
        throw new UnsupportedOperationException("Not supported for LifecycleAspectSuspendableChain.");
    }

    protected void safeExecuteListener(Consumer<SuspendableChainLifecycleListener<Input, Output, State>> executor) {
        for (SuspendableChainLifecycleListener<Input, Output, State> listener : listeners) {
            try {
                executor.accept(listener);
            } catch (Throwable t) {
                // TODO log for exception.
            }
        }
    }
}
