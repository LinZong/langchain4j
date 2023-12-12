package dev.langchain4j.chain.lifecycle.listener;

/**
 * Listener for lifecycle of chain's execution.
 *
 * @param <Input>
 * @param <Output>
 */
public interface ChainLifecycleListener<Input, Output> {

    /**
     * Before call into {@link dev.langchain4j.chain.Chain#execute(Input)}.
     *
     * @param input execution param of listening chain.
     */
    void beforeExecute(Input input);

    /**
     * After call into {@link dev.langchain4j.chain.Chain#execute(Input)}.
     *
     * @param input  execution param of listening chain.
     * @param output execution result of listening chain.
     */
    void afterExecute(Input input, Output output);

    /**
     * Exception occurred while executing chain.
     *
     * @param input execution param of listening chain.
     * @param t     occurred exception instance.
     */
    void executeError(Input input, Throwable t);
}
