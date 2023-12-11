package dev.langchain4j.chain;

import java.io.Serializable;

/**
 * A suspendable chain which can be suspended during chain's execution.
 * It's helpful when requires additional external input or interacts with user during chain's execution.
 * <pre>
 * {@code
 *      Usage:
 *      public class FooChain extends SuspendableChain<Long, Long, Long> {
 *          @Override
 *          public Long execute(Long value) {
 *              if (some stuff) {
 *                  // do something before being suspended.
 *                  throw suspend();
 *              }
 *              // gathered some additional information.
 *              Long value = calculated from other stuff...;
 *              return value;
 *          }
 *      }
 * }
 * </pre>
 *
 * @param <Input>  Generic type of chain's input.
 * @param <Output> Generic type of chain's output.
 * @param <State>  Generic type of chain's intermediate state object.
 */
@SuppressWarnings("unchecked")
public abstract class SuspendableChain<Input, Output, State extends Serializable> implements Chain<Input, Output> {


    /**
     * Called from implementation of {@link SuspendableChain} when it is eligible to be suspended.
     *
     * @return a signal for suspending event. Should be thrown immediately, a.k.a: {@code throw suspend();}.
     */
    protected ChainSuspendingException suspend() {
        return new ChainSuspendingException((Class<? extends SuspendableChain<?, ?, ?>>) getClass(), onSuspend());
    }

    /**
     * Called from implementation of {@link SuspendableChain} when it is handling suspending event propagated from chain's hierarchy.
     *
     * @return a signal for suspending event. Should be thrown immediately, a.k.a: {@code throw suspend(cause);}.
     */
    protected ChainSuspendingException suspend(ChainSuspendingException cause) {
        return new ChainSuspendingException((Class<? extends SuspendableChain<?, ?, ?>>) getClass(), onSuspend(), null, cause);
    }

    /**
     * Called from implementation of {@link SuspendableChain} when it is eligible to be suspended, with generated payload.
     *
     * @param payload payload generated from chain component.
     * @return a signal for suspending event. Should be thrown immediately, a.k.a: {@code throw suspend(payload);}.
     */
    protected ChainSuspendingException suspendWithPayload(Object payload) {
        return new ChainSuspendingException((Class<? extends SuspendableChain<?, ?, ?>>) getClass(), onSuspend(payload), payload);
    }

    /**
     * Called from implementation of {@link SuspendableChain} when it is handling suspending event propagated from chain's hierarchy, with generated payload.
     *
     * @param payload payload generated from chain component.
     * @return a signal for suspending event. Should be thrown immediately, a.k.a: {@code throw suspend(cause);}.
     */
    protected ChainSuspendingException suspendWithPayload(Object payload, ChainSuspendingException cause) {
        return new ChainSuspendingException((Class<? extends SuspendableChain<?, ?, ?>>) getClass(), onSuspend(payload), payload, cause);
    }

    /**
     * Called from framework code for saving state while being suspended.
     *
     * @return suspended state object.
     */
    protected abstract State onSuspend();


    /**
     * Resume chain's execution from suspended state.
     *
     * @param state suspended state generated from {@link SuspendableChain#onSuspend()}.
     */
    public void resume(State state) {
        onResume(state);
    }

    /**
     * Called from framework code for restoring state generated from {@link SuspendableChain#onSuspend()}.
     *
     * @param state suspended state generated from {@link SuspendableChain#onSuspend()}.
     */
    protected abstract void onResume(State state);

}
